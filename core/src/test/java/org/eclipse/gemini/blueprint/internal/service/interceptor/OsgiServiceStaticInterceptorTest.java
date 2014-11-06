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

package org.eclipse.gemini.blueprint.internal.service.interceptor;

import org.aopalliance.intercept.MethodInvocation;
import org.eclipse.gemini.blueprint.mock.MockBundleContext;
import org.eclipse.gemini.blueprint.mock.MockServiceReference;
import org.eclipse.gemini.blueprint.service.ServiceUnavailableException;
import org.eclipse.gemini.blueprint.service.importer.support.internal.aop.ServiceStaticInterceptor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author Costin Leau
 */
public class OsgiServiceStaticInterceptorTest {

    private ServiceStaticInterceptor interceptor;

    private Object service;

    @Before
    public void setUp() throws Exception {
        service = new Object();

        ServiceReference reference = new MockServiceReference();

        BundleContext ctx = new MockBundleContext() {
            public Object getService(ServiceReference reference) {
                return service;
            }
        };

        interceptor = new ServiceStaticInterceptor(ctx, reference);
    }

    @After
    public void tearDown() throws Exception {
        service = null;
        interceptor = null;
    }

    @Test
    public void testNullWrapper() throws Exception {
        try {
            interceptor = new ServiceStaticInterceptor(null, null);
            fail("expected exception");
        } catch (RuntimeException ex) {
            // expected
        }
    }

    @Test
    public void testInvocationOnService() throws Throwable {
        Object target = new Object();
        Method m = target.getClass().getDeclaredMethod("hashCode", null);

        MethodInvocation invocation = new MockMethodInvocation(m);
        assertEquals(service.hashCode(), interceptor.invoke(invocation));
    }

    @Test
    public void testInvocationWhenServiceNA() throws Throwable {
        // service n/a
        ServiceReference reference = new MockServiceReference() {
            public Bundle getBundle() {
                return null;
            }
        };

        interceptor = new ServiceStaticInterceptor(new MockBundleContext(), reference);

        Object target = new Object();
        Method m = target.getClass().getDeclaredMethod("hashCode", null);

        MethodInvocation invocation = new MockMethodInvocation(m);
        try {
            interceptor.invoke(invocation);
            fail("should have thrown exception");
        } catch (ServiceUnavailableException ex) {
            // expected
        }
    }
}
