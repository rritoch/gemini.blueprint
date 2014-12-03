/******************************************************************************
 * Copyright (c) 2006, 2010 VMware Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution. 
 * The Eclipse Public License is available at 
 * http://www.eclipse.org/legal/epl-v10.html and the Apache License v2.0
 * is available at http://www.opensource.org/licenses/apache2.0.php.
 * You may elect to redistribute this code under either of these licenses. 
 *
 * Contributors:
 *   VMware Inc.
 *****************************************************************************/
package org.eclipse.gemini.blueprint.iandt.context;

import org.eclipse.gemini.blueprint.context.ConfigurableOsgiBundleApplicationContext;
import org.eclipse.gemini.blueprint.context.support.OsgiBundleXmlApplicationContext;
import org.eclipse.gemini.blueprint.test.AbstractBlueprintTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.OptionUtils;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.util.PathUtils;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;
import org.springframework.test.context.ContextConfiguration;

import java.io.File;
import java.util.Arrays;

import static org.eclipse.gemini.blueprint.test.BlueprintOptions.blueprintDefaults;
import static org.eclipse.gemini.blueprint.test.BlueprintOptions.withConsole;
import static org.eclipse.gemini.blueprint.test.BlueprintOptions.withLogging;
import static org.junit.Assert.assertEquals;
import static org.ops4j.pax.exam.CoreOptions.options;

//TODO: Revisit this test: OsgiBXAC has more interfaces vs. the annon version.

/**
 * Test checking the context published interfaces.
 *
 * @author Costin Leau
 */
@RunWith(PaxExam.class)
@ContextConfiguration
public class PublishedInterfacesTest extends AbstractBlueprintTest {


    @Configuration
    public Option[] config() {
        return options(blueprintDefaults(),
                withLogging(new File(PathUtils.getBaseDir() + "/target/test-classes/logback.xml").toURI()));
    }

    @Test
    public void testEmptyApplicationContext() throws Exception {
        checkedPublishedOSGiService(1);
    }


    @Test
    public void testXmlOsgiContext() throws Exception {
        OsgiBundleXmlApplicationContext context = new OsgiBundleXmlApplicationContext(
                new String[]{"/org/eclipse/gemini/blueprint/iandt/context/no-op-context.xml"});
        context.setBundleContext(bundleContext);
        context.refresh();

        checkedPublishedOSGiService(2);
        context.close();
    }

    private void checkedPublishedOSGiService(int expectedContexts) throws Exception {
        ServiceReference[] refs = bundleContext.getServiceReferences(
                ConfigurableOsgiBundleApplicationContext.class.getName(), null);
        assertEquals("different number of published contexts encountered", expectedContexts, refs.length);

        for (ServiceReference serviceReference : refs) {
            String[] interfaces = (String[]) serviceReference.getProperty(Constants.OBJECTCLASS);

            Arrays.sort(interfaces);
//            logger.error(serviceReference.toString());
//            logger.error(serviceReference.getBundle().getSymbolicName() + " " + interfaces.length + " \n\t" + Arrays.toString(interfaces) );
//            assertEquals("not enough interfaces published", 13, interfaces.length);
            assertEquals(Version.emptyVersion, serviceReference.getProperty(Constants.BUNDLE_VERSION));
            assertEquals(bundleContext.getBundle().getSymbolicName(),
                    serviceReference.getProperty(Constants.BUNDLE_SYMBOLICNAME));
        }
    }
}
