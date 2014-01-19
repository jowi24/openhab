/**
 * Copyright (c) 2010-2014, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.txsensor;

import org.openhab.core.binding.BindingProvider;

/**
 * @author MaJo
 * @since 1.4.0
 */
public interface TXSensorBindingProvider extends BindingProvider {
		public TXSensorBindingConfig getConfigForSensor(int type, int address);
}
