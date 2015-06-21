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

package org.eclipse.gemini.blueprint.iandt.tccl.internal;

import org.eclipse.gemini.blueprint.iandt.tccl.TCCLService;

/**
 * Private class that should not be exposed.
 * 
 * @author Costin Leau
 */
public class PrivateTCCLServiceImplementation implements TCCLService {

	public ClassLoader getTCCL() {
		return Thread.currentThread().getContextClassLoader();
	}

	public String toString() {
		return PrivateTCCLServiceImplementation.class.getName() + "#" + System.identityHashCode(this);
	}
}
