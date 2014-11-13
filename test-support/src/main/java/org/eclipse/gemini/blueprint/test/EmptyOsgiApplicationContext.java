package org.eclipse.gemini.blueprint.test;

import org.eclipse.gemini.blueprint.context.support.AbstractDelegatedExecutionApplicationContext;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import java.io.IOException;

/**
 * Empty OSGi application context that doesn't require any files to be
 * specified.
 * <p/>
 * Useful to still get injection of bundleContext and OSGi specific resource
 * loading.
 *
 * @author Costin Leau
 */
// the disposable interface is added just so that byte code detect the org.springframework.beans.factory package
class EmptyOsgiApplicationContext extends AbstractDelegatedExecutionApplicationContext {
    protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) throws IOException, BeansException {
    }
}
