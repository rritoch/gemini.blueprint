package org.eclipse.gemini.blueprint.test;

import org.eclipse.gemini.blueprint.context.support.AbstractDelegatedExecutionApplicationContext;
import org.eclipse.gemini.blueprint.context.support.OsgiBundleXmlApplicationContext;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.test.context.MergedContextConfiguration;
import org.springframework.test.context.support.AbstractContextLoader;

/**
 * Created by dsklyut on 11/12/14.
 */
public class OsgiContextLoader extends AbstractContextLoader {


    // todo: should we support it or not?
    /**
     * Returns {@code "-context.xml"} in order to support detection of a
     * default XML config file.
     */
    @Override
    protected String getResourceSuffix() {
        return "-context.xml";
    }

    @Override
    public ApplicationContext loadContext(MergedContextConfiguration mergedContextConfiguration) throws Exception {

        Class<?> testClass = mergedContextConfiguration.getTestClass();

        // todo: assertions
        Bundle testBundle = FrameworkUtil.getBundle(testClass);
        BundleContext testBundleContext = testBundle.getBundleContext();

        AbstractDelegatedExecutionApplicationContext context = null;
        if (mergedContextConfiguration.getLocations().length > 0) {
            context = new OsgiBundleXmlApplicationContext(mergedContextConfiguration.getLocations());
        } else {
            context = new EmptyOsgiApplicationContext();
        }
        context.setBundleContext(testBundleContext);

        context.refresh();
        AnnotationConfigUtils.registerAnnotationConfigProcessors((org.springframework.beans.factory.support.BeanDefinitionRegistry) context.getBeanFactory());
//        context.registerShutdownHook();
        return context;
    }

    @Override
    public ApplicationContext loadContext(String... strings) throws Exception {
        throw new UnsupportedOperationException("This operation is not supported.  Use annotation @ContextConfiguration provide locations");
    }
}
