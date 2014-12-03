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

import java.io.File;
import java.io.Serializable;

import org.eclipse.gemini.blueprint.context.BundleContextAware;
import org.eclipse.gemini.blueprint.context.ConfigurableOsgiBundleApplicationContext;
import org.eclipse.gemini.blueprint.test.AbstractBlueprintTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import org.ops4j.pax.exam.util.PathUtils;
import org.osgi.framework.BundleContext;
import org.springframework.test.context.ContextConfiguration;

import static org.eclipse.gemini.blueprint.test.BlueprintOptions.blueprintDefaults;
import static org.eclipse.gemini.blueprint.test.BlueprintOptions.withLogging;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.ops4j.pax.exam.CoreOptions.options;

/**
 * Test injection of BundleContextAware.
 *
 * @author Costin Leau
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
@ContextConfiguration(locations = {"classpath:org/eclipse/gemini/blueprint/iandt/context/bundleContextAwareTest.xml"})
public class BundleContextAwareTest extends AbstractBlueprintTest {

    public static class BundleContextAwareHolder implements BundleContextAware {

        private BundleContext bundleContext;

        public BundleContext getBundleContext() {
            return bundleContext;
        }

        public void setBundleContext(BundleContext bundleContext) {
            this.bundleContext = bundleContext;
        }

        private static class AnotherInnerClass implements Serializable {

        }
    }

    @Configuration
    public Option[] config() {
        return options(blueprintDefaults(),
                withLogging(new File(PathUtils.getBaseDir() + "/target/test-classes/logback.xml").toURI()));
    }

    @Test
    public void testBundleContextAware() throws Exception {
        BundleContextAwareHolder holder = (BundleContextAwareHolder) applicationContext.getBean("bean");
        assertNotNull(holder.getBundleContext());
        assertSame(bundleContext, holder.getBundleContext());
        assertSame(applicationContext.getBean(ConfigurableOsgiBundleApplicationContext.BUNDLE_CONTEXT_BEAN_NAME),
                holder.getBundleContext());
    }
}
