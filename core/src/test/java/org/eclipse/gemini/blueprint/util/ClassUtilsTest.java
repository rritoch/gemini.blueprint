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

package org.eclipse.gemini.blueprint.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.gemini.blueprint.context.ConfigurableOsgiBundleApplicationContext;
import org.eclipse.gemini.blueprint.context.DelegatedExecutionOsgiBundleApplicationContext;
import org.eclipse.gemini.blueprint.context.support.AbstractDelegatedExecutionApplicationContext;
import org.eclipse.gemini.blueprint.context.support.AbstractOsgiBundleApplicationContext;
import org.eclipse.gemini.blueprint.context.support.OsgiBundleXmlApplicationContext;
import org.eclipse.gemini.blueprint.util.internal.ClassUtils;
import org.junit.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.HierarchicalBeanFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.Lifecycle;
import org.springframework.context.MessageSource;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.AbstractRefreshableApplicationContext;
import org.springframework.core.env.EnvironmentCapable;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.ObjectUtils;

import java.io.Closeable;
import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Costin Leau
 */
public class ClassUtilsTest {

    private static final Log log = LogFactory.getLog(ClassUtilsTest.class);

    @Test
    public void testAutoDetectClassesForPublishingDisabled() throws Exception {
        Class<?>[] clazz = ClassUtils.getClassHierarchy(Integer.class, ClassUtils.ClassSet.INTERFACES);
        assertFalse(ObjectUtils.isEmpty(clazz));
        assertEquals(2, clazz.length);
    }

    @Test
    public void testAutoDetectClassesForPublishingInterfaces() throws Exception {
        Class<?>[] clazz = ClassUtils.getClassHierarchy(HashMap.class, ClassUtils.ClassSet.INTERFACES);
        Class<?>[] expected = new Class<?>[]{Cloneable.class, Serializable.class, Map.class};

        assertTrue(compareArrays(expected, clazz));
    }


    @Test
    public void testAutoDetectClassesForPublishingClassHierarchy() throws Exception {
        Class<?>[] clazz = ClassUtils.getClassHierarchy(HashMap.class, ClassUtils.ClassSet.CLASS_HIERARCHY);
        Class<?>[] expected = new Class<?>[]{HashMap.class, AbstractMap.class};
        assertTrue(compareArrays(expected, clazz));
    }

    @Test
    public void testAutoDetectClassesForPublishingAll() throws Exception {
        Class<?>[] clazz = ClassUtils.getClassHierarchy(HashMap.class, ClassUtils.ClassSet.ALL_CLASSES);
        Class<?>[] expected =
                new Class<?>[]{Map.class, Cloneable.class, Serializable.class, HashMap.class, AbstractMap.class};

        assertTrue(compareArrays(expected, clazz));
    }

    @Test
    public void testInterfacesHierarchy() {

        Class<?>[] clazz = ClassUtils.getAllInterfaces(DelegatedExecutionOsgiBundleApplicationContext.class);
        Class<?>[] expected =
                {ConfigurableOsgiBundleApplicationContext.class, ConfigurableApplicationContext.class,
                        ApplicationContext.class, Lifecycle.class, Closeable.class, EnvironmentCapable.class, ListableBeanFactory.class,
                        HierarchicalBeanFactory.class, MessageSource.class, ApplicationEventPublisher.class,
                        ResourcePatternResolver.class, BeanFactory.class, ResourceLoader.class, AutoCloseable.class};

        assertTrue(compareArrays(expected, clazz));
    }

    @Test
    public void testAppContextClassHierarchy() {
        Class<?>[] clazz =
                ClassUtils.getClassHierarchy(OsgiBundleXmlApplicationContext.class, ClassUtils.ClassSet.ALL_CLASSES);

        Class<?>[] expected = {OsgiBundleXmlApplicationContext.class,
                AbstractDelegatedExecutionApplicationContext.class,
                DelegatedExecutionOsgiBundleApplicationContext.class,
                ConfigurableOsgiBundleApplicationContext.class,
                ConfigurableApplicationContext.class,
                ApplicationContext.class,
                Lifecycle.class,
                Closeable.class,
                EnvironmentCapable.class,
                ListableBeanFactory.class,
                HierarchicalBeanFactory.class,
                MessageSource.class,
                ApplicationEventPublisher.class,
                ResourcePatternResolver.class,
                BeanFactory.class,
                ResourceLoader.class,
                AutoCloseable.class,
                AbstractOsgiBundleApplicationContext.class,
                AbstractRefreshableApplicationContext.class,
                AbstractApplicationContext.class,
                DisposableBean.class,
                DefaultResourceLoader.class};

        assertTrue(compareArrays(expected, clazz));
    }

    private boolean compareArrays(Object[] a, Object[] b) {
        if ((a == null && b != null) || (b == null && a != null))
            return false;

        if (a == null && b == null)
            return true;

        if (a == b)
            return true;

        if (a.length != b.length)
            return false;

        for (int i = 0; i < a.length; i++) {
            boolean found = false;
            for (int j = 0; j < b.length; j++) {
                if (a[i].equals(b[j])) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                log.debug("did not find " + a[i]);
                return false;
            }

        }
        return true;
    }

}
