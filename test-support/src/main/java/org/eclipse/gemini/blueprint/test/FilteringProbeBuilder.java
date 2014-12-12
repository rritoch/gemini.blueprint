/*
 * Copyright 2009 Toni Menzel.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.eclipse.gemini.blueprint.test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ops4j.pax.exam.TestAddress;
import org.ops4j.pax.exam.TestContainerException;
import org.ops4j.pax.exam.TestInstantiationInstruction;
import org.ops4j.pax.exam.TestProbeBuilder;
import org.ops4j.pax.exam.TestProbeProvider;
import org.ops4j.pax.exam.spi.ContentCollector;
import org.ops4j.pax.exam.spi.intern.CollectFromItems;
import org.ops4j.pax.exam.spi.intern.CompositeCollector;
import org.ops4j.pax.exam.spi.intern.DefaultTestAddress;
import org.ops4j.pax.exam.spi.intern.DefaultTestProbeProvider;
import org.ops4j.pax.tinybundles.core.TinyBundle;
import org.ops4j.store.Store;
import org.ops4j.store.intern.TemporaryStore;
import org.osgi.framework.Constants;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;

import static org.ops4j.pax.exam.Constants.PROBE_EXECUTABLE;
import static org.ops4j.pax.tinybundles.core.TinyBundles.bundle;
import static org.ops4j.pax.tinybundles.core.TinyBundles.withClassicBuilder;

/**
 * Default implementation allows you to dynamically create a probe from current classpath.
 *
 * @author Toni Menzel
 * @since Dec 2, 2009
 * <p/>
 * NOTE: this is a modified copy of the pax exam default test probe builder.
 * Modified to add support for filtering out directories/
 */
public class FilteringProbeBuilder implements TestProbeBuilder {

    private final static Log log = LogFactory.getLog(FilteringProbeBuilder.class);

    private static final String EVERYTHING_PATTERN = "/**/*";
    private static final String DEFAULT_PROBE_METHOD_NAME = "probe";
    private static final List<String> EVERYTHING_CONTENT = Collections.singletonList(EVERYTHING_PATTERN);

    private final Map<TestAddress, TestInstantiationInstruction> probeCalls = new LinkedHashMap<>();
    private final List<Class<?>> anchors = new ArrayList<>();
    private final List<String> contentPatterns = new ArrayList<>(8);
    private final Properties extraProperties = new Properties();
    private final Set<String> ignorePackages = new HashSet<>();

    private File tempDir;
    private final Store<InputStream> store;

    public FilteringProbeBuilder(File tempDir) {
        if (tempDir == null) throw new IllegalArgumentException("temp dir must not be null");
        this.tempDir = tempDir;
        this.store = new TemporaryStore(this.tempDir, false);
    }

    @Override
    public TestAddress addTest(Class<?> clazz, String methodName, Object... args) {
        TestAddress address = new DefaultTestAddress(clazz.getName() + "." + methodName, args);
        probeCalls.put(address, new TestInstantiationInstruction(clazz.getName() + ";" + methodName));
        addAnchor(clazz);
        return address;
    }

    @Override
    public TestAddress addTest(Class<?> clazz, Object... args) {
        return addTest(clazz, DEFAULT_PROBE_METHOD_NAME, args);
    }

    @Override
    public List<TestAddress> addTests(Class<?> clazz, Method... methods) {
        List<TestAddress> list = new ArrayList<>();
        for (Method method : methods) {
            list.add(addTest(clazz, method.getName()));
        }
        return list;
    }

    @Override
    public TestProbeBuilder setHeader(String key, String value) {
        extraProperties.put(key, value);
        return this;
    }

    // when your test class contains clutter in non-test methods,
    // bnd generates too many imports.
    // This makes packages optional.
    @Override
    public TestProbeBuilder ignorePackageOf(Class<?>... classes) {
        for (Class<?> c : classes) {
            ignorePackages.add(c.getPackage().getName());
        }
        return this;
    }

    @Override
    public TestProbeProvider build() {
        if (anchors.size() == 0) {
            throw new TestContainerException("No tests added to setup!");
        }

        constructProbeTag(extraProperties);


        try {
            TinyBundle bundle = prepareProbeBundle(createExtraIgnores());
            InputStream is = bundle.build(withClassicBuilder());
            TestProbeProvider pp = new DefaultTestProbeProvider(getTests(), store, store.store(is));
            return pp;

        } catch (IOException e) {
            throw new TestContainerException(e);
        }
    }

    @Override
    public Set<TestAddress> getTests() {
        return probeCalls.keySet();
    }

    @Override
    public File getTempDir() {
        return tempDir;
    }

    @Override
    public void setTempDir(File tempDir) {
        this.tempDir = tempDir;
    }

    ////---- extensions
    private String manifestLocation = null;

    /**
     * add location of the manifest file in spring resource style
     *
     * @param location
     * @return
     */
    public void setManifestLocation(String location) {
        this.manifestLocation = location;
    }

    /**
     * add locations of the resources to include in a probe bundle.
     *
     * @param pattern spring resource style path patterns
     * @return
     */
    public void addContentPattern(String... pattern) {
        this.contentPatterns.addAll(Arrays.asList(pattern));
    }


    ////----------  internal

    /**
     * prepare TinyBundle shell
     *
     * @param p
     * @return
     * @throws IOException
     */
    protected TinyBundle prepareProbeBundle(Properties p) throws IOException {

        // by default use dynamic-import...???  not kool
        TinyBundle bundle = bundle(store);

        // handle manifest first
        if (StringUtils.hasText(this.manifestLocation)) {
            appendManifest(bundle);
        } else {
            appendDefaultManifest(bundle);
        }

        bundle.set(Constants.BUNDLE_MANIFESTVERSION, "2");

        // override/add extra headers
        for (String key : extraProperties.stringPropertyNames()) {
            bundle.set(key, extraProperties.getProperty(key));
        }

        Map<String, URL> resources = collectResources();
        for (String item : resources.keySet()) {
            URL url = resources.get(item);
            if (log.isDebugEnabled()) {
                log.debug(String.format("Adding resource: %s -> %s", item, url));
            }
            bundle.add(item, url);
        }

        return bundle;
    }

    private void appendDefaultManifest(TinyBundle bundle) {
        bundle.set(Constants.DYNAMICIMPORT_PACKAGE, "*");
        bundle.set(Constants.BUNDLE_SYMBOLICNAME, "");
        bundle.set(Constants.BUNDLE_MANIFESTVERSION, "2");
    }

    private void appendManifest(TinyBundle bundle) throws IOException {

        DefaultResourceLoader loader = new DefaultResourceLoader();
        Manifest manifest = new Manifest(loader.getResource(manifestLocation).getInputStream());
        Attributes attr = manifest.getMainAttributes();
        for (Object key : attr.keySet()) {
            String k = key.toString();
            String v = attr.getValue(k);

            // append optional import for org.ops4j.pax.exam
            if (k.equalsIgnoreCase(Constants.IMPORT_PACKAGE)) {
                if (StringUtils.hasText(v)) {
                    v = v + ",";
                }
            }
            v = v + "org.ops4j.pax.exam;resolution:=optional";
            bundle.set(k, v);
        }
    }

    private TestProbeBuilder addAnchor(Class<?> clazz) {
        anchors.add(clazz);
        return this;
    }

    private Properties createExtraIgnores() {
        Properties properties = new Properties();
        StringBuilder sb = new StringBuilder();
        for (String p : ignorePackages) {
            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append(p);
        }
        properties.put("Ignore-Package", sb.toString());
        return properties;
    }


    private Map<String, URL> collectResources() throws IOException {
        ContentCollector collector = selectCollector();
        Map<String, URL> map = new HashMap<>();
        collector.collect(map);
        return map;
    }

    static String convertClassToPath(Class<?> c) {
        return c.getName().replace(".", File.separator) + ".class";
    }

    /**
     * @param clazz to find the root classes folder for.
     * @return A File instance being the exact folder on disk or null, if it hasn't been found.
     * @throws java.io.IOException if a problem occurs (method crawls folders on disk..)
     */
    private static File findClassesFolder(Class<?> clazz) throws IOException {
        ClassLoader classLoader = clazz.getClassLoader();
        String clazzPath = convertClassToPath(clazz);
        URL url = classLoader.getResource(clazzPath);
        if (url == null || !"file".equals(url.getProtocol())) {
            return null;
        } else {
            try {
                File file = new File(url.toURI());
                String fullPath = file.getCanonicalPath();
                String parentDirPath = fullPath.substring(0, fullPath.length() - clazzPath.length());
                return new File(parentDirPath);
            } catch (URISyntaxException e) {
                // this should not happen as the uri was obtained from getResource
                throw new TestContainerException(e);
            }
        }
    }

    private ContentCollector selectCollector() throws IOException {


        File root = findClassesFolder(anchors.get(0));

        if (root != null) {
            List<String> lp = contentPatterns.isEmpty() ? EVERYTHING_CONTENT : appendRoot(root, contentPatterns);
            return new CompositeCollector(new CollectFromBase(root, lp), new CollectFromItems(anchors));
        } else {
            return new CollectFromItems(anchors);
        }
    }

    private List<String> appendRoot(File root, List<String> patterns) throws IOException {
        List<String> result = new ArrayList<>();
        // append base (i.e. root dir) to all patterns to match so we can use spring path matcher properly
        String path = root.getCanonicalPath();
        for (String pattern : patterns) {
            result.add(new File(root, "/" + pattern).getAbsolutePath());
        }
        return result;
    }

    private void constructProbeTag(Properties p) {
        // construct out of added Tests
        StringBuilder sbKeyChain = new StringBuilder();

        for (TestAddress address : probeCalls.keySet()) {
            sbKeyChain.append(address.identifier());
            sbKeyChain.append(",");
            p.put(address.identifier(), probeCalls.get(address).toString());
        }
        p.setProperty(PROBE_EXECUTABLE, sbKeyChain.toString());
    }

    private class CollectFromBase implements ContentCollector {

        private File base;
        private PathMatcher pm = new AntPathMatcher();
        private final List<String> matches;

        public CollectFromBase(File base, List<String> toMatch) {
            this.base = base;
            this.matches = toMatch;
        }

        public void collect(Map<String, URL> map) throws IOException {
            collectFromBase(map, base);
        }

        private void collectFromBase(Map<String, URL> map, File dir) throws IOException {
            if (dir != null && dir.canRead() && dir.isDirectory()) {
                //noinspection ConstantConditions
                for (File f : dir.listFiles()) {
                    if (f.isDirectory()) {
                        collectFromBase(map, f);
                    } else if (!f.isHidden() && doesItMatch(f)) {
                        map.put(normalize(base, f), f.toURI().toURL());
                    }
                }
            }
        }

        private boolean doesItMatch(File file) throws IOException {
            for (String pattern : matches) {
                if (pm.match(pattern, file.getCanonicalPath())) {
                    return true;
                }
            }
            return false;
        }

        private String normalize(File _base, File f) throws IOException {
            return f.getCanonicalPath().substring(_base.getCanonicalPath().length() + 1)
                    .replace(File.separatorChar, '/');
        }

    }
}
