package org.eclipse.gemini.blueprint.test;

import org.junit.runners.model.InitializationError;
import org.ops4j.pax.exam.junit.PaxExam;

/**
 * Created by dsklyut on 11/10/14.
 */
public class BlueprintRunner extends PaxExam {
    public BlueprintRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }


}
