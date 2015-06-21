/******************************************************************************
 * Copyright (c) 2006, 2010 VMware Inc., Oracle Inc.
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
 *   Oracle Inc.
 *****************************************************************************/

package org.eclipse.gemini.blueprint.iandt.dependencies;

import org.eclipse.gemini.blueprint.iandt.simpleservice2.MyService2;
import org.eclipse.gemini.blueprint.iandt.simpleservice.MyService;

/**
 * @author Hal Hildebrand
 *         Date: Dec 1, 2006
 *         Time: 3:39:40 PM
 */
public class DependentImpl implements Dependent {
    private MyService service1;
    private MyService2 service2;
    private MyService2 service3;


    public void setService1(MyService service1) {
        this.service1 = service1;
    }

    public void setService2(MyService2 service2) {
        this.service2 = service2;
    }


    public void setService3(MyService2 service3) {
        this.service3 = service3;
    }


    public boolean isResolved() {
        return service2 != null && service3 != null && service1 != null;
    }
}
