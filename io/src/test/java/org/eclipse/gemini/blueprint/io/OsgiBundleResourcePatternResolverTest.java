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

package org.eclipse.gemini.blueprint.io;

import org.eclipse.gemini.blueprint.mock.MockBundle;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.UrlResource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Costin Leau
 */
public class OsgiBundleResourcePatternResolverTest {

    OsgiBundleResourcePatternResolver resolver;

    Bundle bundle;

    /*
     * (non-Javadoc)
     *
     * @see junit.framework.TestCase#setUp()
     */
    @Before
    public void setUp() throws Exception {
        bundle = new MockBundle();
        resolver = new OsgiBundleResourcePatternResolver(bundle);

    }

    /**
     * Test method for
     * {@link org.eclipse.gemini.blueprint.io.OsgiBundleResourcePatternResolver#OsgiBundleResourcePatternResolver(org.osgi.framework.Bundle)}.
     */
    @Test
    public void testOsgiBundleResourcePatternResolverBundle() {
        ResourceLoader res = resolver.getResourceLoader();
        assertTrue(res instanceof OsgiBundleResourceLoader);
        Resource resource = res.getResource("foo");
        assertSame(bundle, ((OsgiBundleResource) resource).getBundle());
        assertEquals(res.getResource("foo"), resolver.getResource("foo"));
    }

    /**
     * Test method for
     * {@link org.eclipse.gemini.blueprint.io.OsgiBundleResourcePatternResolver#OsgiBundleResourcePatternResolver(org.springframework.core.io.ResourceLoader)}.
     */
    @Test
    public void testOsgiBundleResourcePatternResolverResourceLoader() {
        ResourceLoader resLoader = new DefaultResourceLoader();
        resolver = new OsgiBundleResourcePatternResolver(resLoader);
        ResourceLoader res = resolver.getResourceLoader();

        assertSame(resLoader, res);
        assertEquals(resLoader.getResource("foo"), resolver.getResource("foo"));
    }

    /**
     * Test method for
     * {@link org.eclipse.gemini.blueprint.io.OsgiBundleResourcePatternResolver#getResources(java.lang.String)}.
     */
    @Test
    public void testGetResourcesString() throws Exception {
        Resource[] res;

        try {
            res = resolver.getResources("classpath*:**/*");
            fail("should have thrown exception");
        } catch (Exception ex) {
            // expected
        }

        String thisClass = "org/eclipse/gemini/blueprint/io/OsgiBundleResourcePatternResolverTest.class";

        res = resolver.getResources("osgibundle:" + thisClass);
        assertNotNull(res);
        assertEquals(1, res.length);
        assertTrue(res[0] instanceof UrlResource);
    }
}
