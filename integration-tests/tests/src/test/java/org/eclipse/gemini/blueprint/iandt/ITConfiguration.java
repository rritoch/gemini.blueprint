package org.eclipse.gemini.blueprint.iandt;

import java.io.File;

import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.util.PathUtils;

import static org.eclipse.gemini.blueprint.test.BlueprintOptions.blueprintDefaults;
import static org.eclipse.gemini.blueprint.test.BlueprintOptions.withLogging;
import static org.ops4j.pax.exam.CoreOptions.options;

/**
 * Created by dsklyut on 12/1/14.
 */
public class ITConfiguration implements org.ops4j.pax.exam.ConfigurationFactory {
    @Override
    public Option[] createConfiguration() {
        return options(blueprintDefaults(), withLogging(new File(PathUtils.getBaseDir() + "/target/test-classes/logback.xml").toURI()));
    }
}
