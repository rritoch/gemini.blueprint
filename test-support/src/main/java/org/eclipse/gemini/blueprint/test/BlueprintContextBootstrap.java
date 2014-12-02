package org.eclipse.gemini.blueprint.test;

import org.springframework.test.context.ContextLoader;
import org.springframework.test.context.support.AbstractTestContextBootstrapper;

/**
 * Created by dsklyut on 11/12/14.
 */
public class BlueprintContextBootstrap extends AbstractTestContextBootstrapper {

    @Override
    protected Class<? extends ContextLoader> getDefaultContextLoaderClass(Class<?> aClass) {
        return OsgiContextLoader.class;
    }
}
