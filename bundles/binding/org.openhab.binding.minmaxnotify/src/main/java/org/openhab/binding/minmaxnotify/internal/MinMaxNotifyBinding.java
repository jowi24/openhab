/**
 * Copyright (c) 2010-2014, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.minmaxnotify.internal;

import java.util.Dictionary;

import org.apache.commons.lang.StringUtils;
import org.openhab.binding.minmaxnotify.MinMaxNotifyBindingConfig;
import org.openhab.binding.minmaxnotify.MinMaxNotifyBindingProvider;
import org.openhab.core.binding.AbstractActiveBinding;
import org.openhab.core.types.Command;
import org.openhab.core.types.State;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implement this class if you are going create an actively polling service
 * like querying a Website/Device.
 * 
 * @author MaJo
 * @since 1.4.0
 */
public class MinMaxNotifyBinding extends AbstractActiveBinding<MinMaxNotifyBindingProvider> implements ManagedService {

	private static final Logger logger = 
		LoggerFactory.getLogger(MinMaxNotifyBinding.class);

	
	/** 
	 * the refresh interval which is used to poll values from the MinMaxNotify
	 * server (optional, defaults to 60000ms)
	 */
	private long refreshInterval = 60000;


	private String xmppContact = "";
	private double lastValueNotified = 0;
	
	
	public MinMaxNotifyBinding() {
	}
		
	
	public void activate() {
	}
	
	public void deactivate() {
		// deallocate resources here that are no longer needed and 
		// should be reset when activating this binding again
	}

	
	/**
	 * @{inheritDoc}
	 */
	@Override
	protected long getRefreshInterval() {
		return refreshInterval;
	}

	/**
	 * @{inheritDoc}
	 */
	@Override
	protected String getName() {
		return "MinMaxNotify Refresh Service";
	}
	
	/**
	 * @{inheritDoc}
	 */
	@Override
	protected void execute() {
		// the frequently executed code (polling) goes here ...
	}

	/**
	 * @{inheritDoc}
	 */
	@Override
	protected void internalReceiveCommand(String itemName, Command command) {
		// the code being executed when a command was sent on the openHAB
		// event bus goes here. This method is only called if one of the 
		// BindingProviders provide a binding for the given 'itemName'.
		logger.debug("internalReceiveCommand() is called!");
	}
	
	/**
	 * @{inheritDoc}
	 */
	@Override
	protected void internalReceiveUpdate(String itemName, State newState) {
		// the code being executed when a state was sent on the openHAB
		// event bus goes here. This method is only called if one of the 
		// BindingProviders provide a binding for the given 'itemName'.
		logger.debug("internalReceiveCommand() is called!");
		double value = Double.parseDouble(newState.toString()) ;
		for (MinMaxNotifyBindingProvider provider : providers) {
			MinMaxNotifyBindingConfig bc = provider.getConfigForItemName(itemName);
			if (value > bc.getMaxValue() || value < bc.getMinValue()) {
				if (Math.abs(value - lastValueNotified) > bc.getStep()) {
					// TODO xmpp message
//					XMPP.sendMessage("to", "message");
					lastValueNotified = value;
				}
			}
		}
	}
		
	/**
	 * @{inheritDoc}
	 */
	@Override
	public void updated(Dictionary<String, ?> config) throws ConfigurationException {
		if (config != null) {
			
			// to override the default refresh interval one has to add a 
			// parameter to openhab.cfg like <bindingName>:refresh=<intervalInMs>
			String refreshIntervalString = (String) config.get("refresh");
			if (StringUtils.isNotBlank(refreshIntervalString)) {
				refreshInterval = Long.parseLong(refreshIntervalString);
			}
			xmppContact  = (String) config.get("xmppcontact");

			setProperlyConfigured(true);
		}
	}
	

}
