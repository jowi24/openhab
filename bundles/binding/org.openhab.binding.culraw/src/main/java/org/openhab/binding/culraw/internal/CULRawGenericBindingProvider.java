/**
 * Copyright (c) 2010-2014, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.culraw.internal;

import org.openhab.binding.culraw.CULRawBindingProvider;
import org.openhab.core.binding.BindingConfig;
import org.openhab.core.items.Item;
import org.openhab.core.library.items.DimmerItem;
import org.openhab.core.library.items.RollershutterItem;
import org.openhab.core.library.items.SwitchItem;
import org.openhab.model.item.binding.AbstractGenericBindingProvider;
import org.openhab.model.item.binding.BindingConfigParseException;


/**
 * This class is responsible for parsing the binding configuration.
 * 
 * @author MaJo
 * @since 1.4.0
 */
public class CULRawGenericBindingProvider extends AbstractGenericBindingProvider implements CULRawBindingProvider {

	/**
	 * {@inheritDoc}
	 */
	public String getBindingType() {
		return "culraw";
	}

	/**
	 * @{inheritDoc}
	 */
	@Override
	public void validateItemType(Item item, String bindingConfig) throws BindingConfigParseException {
		if (!(item instanceof SwitchItem || item instanceof RollershutterItem)) {
			throw new BindingConfigParseException("item '" + item.getName()
					+ "' is of type '" + item.getClass().getSimpleName()
					+ "', only Switch- and RollershutterItems are allowed - please check your *.items configuration");
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void processBindingConfiguration(String context, Item item, String bindingConfig) throws BindingConfigParseException {
		super.processBindingConfiguration(context, item, bindingConfig);
		String[] tokens = bindingConfig.split(";");
		CULRawBindingConfig config = new CULRawBindingConfig(tokens[0], tokens[1], tokens[2]);
		addBindingConfig(item, config);		
	}
	
	public class CULRawBindingConfig implements BindingConfig {
		private String OnUpCommand;
		private String OffDownCommand;
		private String device;
		public CULRawBindingConfig(String device, String onUpCommand, String offDownCommand) {
			setDevice(device);
			setOnUpCommand(onUpCommand);
			setOffDownCommand(offDownCommand);
		}
		public String getOnUpCommand() {
			return OnUpCommand;
		}
		public void setOnUpCommand(String onUpCommand) {
			OnUpCommand = onUpCommand;
		}
		public String getOffDownCommand() {
			return OffDownCommand;
		}
		public void setOffDownCommand(String offDownCommand) {
			OffDownCommand = offDownCommand;
		}
		public String getDevice() {
			return device;
		}
		public void setDevice(String device) {
			this.device = device;
		}
	}

	@Override
	public CULRawBindingConfig getItemConfig(String item) {
		return (CULRawBindingConfig) bindingConfigs.get(item);
	}
	
	
}
