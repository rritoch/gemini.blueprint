package org.eclipse.gemini.blueprint.test;

import org.eclipse.gemini.blueprint.context.BundleContextAware;
import org.junit.Before;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.BootstrapWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContextManager;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

/**
 * Created by dsklyut on 11/12/14.
 */
@BootstrapWith(BlueprintContextBootstrap.class)
@ContextConfiguration
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class})
public abstract class AbstractBlueprintTest implements ApplicationContextAware, BundleContextAware {

    protected final Logger logger;

    protected ApplicationContext applicationContext;
    protected BundleContext bundleContext;

    protected TestContextManager testContextManager;

    public AbstractBlueprintTest() {
        logger = LoggerFactory.getLogger(getClass());
    }

    @Before
    public void initSpringTestHarness() throws Exception {
        testContextManager = new TestContextManager(getClass());
        testContextManager.prepareTestInstance(this);
    }

    public final void setApplicationContext(org.springframework.context.ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }
}