/**
 * Copyright (c) 2010-2014, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.txsensor.internal;

import java.util.HashMap;
import java.util.Map;

import org.openhab.binding.txsensor.TXSensorBindingConfig;
import org.openhab.binding.txsensor.TXSensorBindingProvider;
import org.openhab.core.items.Item;
import org.openhab.core.library.items.NumberItem;
import org.openhab.model.item.binding.AbstractGenericBindingProvider;
import org.openhab.model.item.binding.BindingConfigParseException;

/**
 * This class is responsible for parsing the binding configuration.
 * 
 * @author MaJo
 * @since 1.4.0
 */
public class TXSensorGenericBindingProvider extends
		AbstractGenericBindingProvider implements TXSensorBindingProvider {

	private Map<Integer, TXSensorBindingConfig> addressMap = new HashMap<Integer, TXSensorBindingConfig>();
	
	/**
	 * {@inheritDoc}
	 */
	public String getBindingType() {
		return "txsensor";
	}

	/**
	 * @{inheritDoc
	 */
	@Override
	public void validateItemType(Item item, String bindingConfig)
			throws BindingConfigParseException {
		if (!(item instanceof NumberItem)) {
			throw new BindingConfigParseException(
					"item '"
							+ item.getName()
							+ "' is of type '"
							+ item.getClass().getSimpleName()
							+ "', only NumberItems are allowed - please check your *.items configuration");
		}

		if (bindingConfig.length() != 3) {
			throw new BindingConfigParseException(
					"The configured address must consist of 1 bytes type and 2 byte device address");
		}
	}

	/**
	 * Binding config is in the style of {txsensor="TAA"} {@inheritDoc}
	 */
	@Override
	public void processBindingConfiguration(String context, Item item,
			String bindingConfig) throws BindingConfigParseException {
		super.processBindingConfiguration(context, item, bindingConfig);

		int type = Integer.parseInt(bindingConfig.substring(0, 1), 16);
		int address = Integer.parseInt(bindingConfig.substring(1), 16);
		
		TXSensorBindingConfig config = new TXSensorBindingConfig(item, type,
				address);

		// parse bindingconfig here ...
		addressMap.put(generateKey(type, address), config);
		addBindingConfig(item, config);
	}

	@Override
	public TXSensorBindingConfig getConfigForSensor(int type, int address) {
		
		return addressMap.get(generateKey(type, address));
	}

	private int generateKey(int type, int address) {
		return type << 16 | address;
	}
}
