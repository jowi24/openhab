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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is responsible for parsing the binding configuration.
 * 
 * @author MaJo
 * @since 1.4.0
 */
public class TXSensorGenericBindingProvider extends
		AbstractGenericBindingProvider implements TXSensorBindingProvider {

	private Map<Integer, TXSensorBindingConfig> addressMap = new HashMap<Integer, TXSensorBindingConfig>();
	private static final Logger logger = LoggerFactory
			.getLogger(TXSensorGenericBindingProvider.class);

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
	}

	/**
	 * Binding config is in the style of <br>
	 * <code>{{@literal txsensor="type=<temperature|humidity|pressure>;address=<address>"}}</code> <br>
	 * where address can be given decimal or hexadecimal (0x-prefixed).
	 * {@inheritDoc}
	 */
	@Override
	public void processBindingConfiguration(String context, Item item,
			String bindingConfig) throws BindingConfigParseException {
		super.processBindingConfiguration(context, item, bindingConfig);

		String[] configParts = bindingConfig.split(";");
		int type = 0;
		int address = 0;
		for (String configPart : configParts) {
			String[] nameValue = configPart.split("=");
			if (nameValue[0].equals("type")) {
				switch (nameValue[1]) {
				case "temperature":
					type = 0x0;
					break;
				case "humidity":
					type = 0xE;
					break;
				case "pressure":
					type = 0x1;
					break;
				case "brightness":
					type = 0x2;
					break;
				default:
					if (nameValue[1].startsWith("0x")) {
						type = Integer.parseInt(nameValue[1].substring(2), 16);
					} else {
						logger.error("Unknown value " + nameValue[1]
								+ " in binding for item " + item.getName()
								+ ".");
					}
				}
			} else if (nameValue[0].equals("address")) {
				if (nameValue[1].startsWith("0x")) {
					address = Integer.parseInt(nameValue[1].substring(2), 16) & 0xFE;
				} else {
					address = Integer.parseInt(nameValue[1]) & 0xFE;
				}
			} else {
				logger.error("Unknown parameter " + configPart
						+ " in binding for item " + item.getName() + ".");
			}
		}

		TXSensorBindingConfig config = new TXSensorBindingConfig(item, type,
				address);

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
