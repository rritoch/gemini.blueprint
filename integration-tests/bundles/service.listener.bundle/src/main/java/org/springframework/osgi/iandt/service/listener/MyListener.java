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

package org.eclipse.gemini.blueprint.iandt.service.listener;

import org.eclipse.gemini.blueprint.iandt.simpleservice.MyService;
import java.util.Dictionary;


/**
 * @author Hal Hildebrand
 *         Date: Nov 14, 2006
 *         Time: 8:07:27 AM
 */
public class MyListener {
    public static int BOUND_COUNT = 0;
    public static int UNBOUND_COUNT = 0;


    public void serviceAvailable(MyService simpleService, Dictionary props) {
        BOUND_COUNT++;
    }


    public void serviceUnavailable(MyService simpleService, Dictionary props) {
        UNBOUND_COUNT++;
    }


    public void setSimpleService(MyService simpleService) {
        // Dummy used to force initialization of osgi service reference to simple service
    }
}
