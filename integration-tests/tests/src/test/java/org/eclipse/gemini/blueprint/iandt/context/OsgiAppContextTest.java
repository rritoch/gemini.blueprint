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
import org.eclipse.gemini.blueprint.test.AbstractBlueprintTest;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.OptionUtils;
import org.ops4j.pax.exam.junit.PaxExam;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;

import static org.eclipse.gemini.blueprint.test.BlueprintOptions.blueprintDefaults;
import static org.eclipse.gemini.blueprint.test.BlueprintOptions.withConsole;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.ops4j.pax.exam.CoreOptions.options;

/**
 * Integration test on the functionality offered by OSGi app context.
 *
 * @author Costin Leau
 */
@RunWith(PaxExam.class)
@ContextConfiguration
public class OsgiAppContextTest extends AbstractBlueprintTest {

    @Configuration
    public Option[] config() {
        return options(blueprintDefaults());
    }

    @Test
    public void testBundleContextAvailableAsBean() {
        ApplicationContext ctx = applicationContext;
        assertNotNull(ctx);
        assertTrue("bundleContext not available as a bean",
                applicationContext.containsBean(ConfigurableOsgiBundleApplicationContext.BUNDLE_CONTEXT_BEAN_NAME));
    }

    @Test
    public void testBundleContextInjected() {
        assertNotNull("bundleContext hasn't been injected into the test", bundleContext);
    }

    @Test
    public void testBundleContextIsTheSame() {
        assertSame(bundleContext,
                applicationContext.getBean(ConfigurableOsgiBundleApplicationContext.BUNDLE_CONTEXT_BEAN_NAME));
    }
}
