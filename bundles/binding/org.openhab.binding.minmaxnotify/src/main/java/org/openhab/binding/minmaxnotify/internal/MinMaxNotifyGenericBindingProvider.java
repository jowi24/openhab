/**
 * Copyright (c) 2010-2014, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.minmaxnotify.internal;

import org.openhab.binding.minmaxnotify.MinMaxNotifyBindingConfig;
import org.openhab.binding.minmaxnotify.MinMaxNotifyBindingProvider;
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
public class MinMaxNotifyGenericBindingProvider extends
		AbstractGenericBindingProvider implements MinMaxNotifyBindingProvider {

	/**
	 * {@inheritDoc}
	 */
	public String getBindingType() {
		return "minmaxnotify";
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
	 * {@inheritDoc}
	 */
	@Override
	public void processBindingConfiguration(String context, Item item,
			String bindingConfig) throws BindingConfigParseException {
		super.processBindingConfiguration(context, item, bindingConfig);
		String[] token = bindingConfig.split(":");
		double min = Double.parseDouble(token[0]);
		double max = Double.parseDouble(token[1]);
		double step = Double.parseDouble(token[2]);
		MinMaxNotifyBindingConfig config = new MinMaxNotifyBindingConfig(min,
				max, step);
		addBindingConfig(item, config);
	}

	@Override
	public MinMaxNotifyBindingConfig getConfigForItemName(String itemName) {
		return (MinMaxNotifyBindingConfig) bindingConfigs.get(itemName);
	}

}
