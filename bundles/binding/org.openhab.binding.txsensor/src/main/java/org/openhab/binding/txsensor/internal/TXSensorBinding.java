/**
 * Copyright (c) 2010-2014, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.txsensor.internal;

import java.util.Dictionary;

import org.apache.commons.lang.StringUtils;
import org.openhab.binding.txsensor.TXSensorBindingConfig;
import org.openhab.binding.txsensor.TXSensorBindingProvider;
import org.openhab.core.binding.AbstractActiveBinding;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.types.Command;
import org.openhab.core.types.State;
import org.openhab.io.transport.cul.CULDeviceException;
import org.openhab.io.transport.cul.CULHandler;
import org.openhab.io.transport.cul.CULListener;
import org.openhab.io.transport.cul.CULManager;
import org.openhab.io.transport.cul.CULMode;
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
public class TXSensorBinding extends AbstractActiveBinding<TXSensorBindingProvider> implements ManagedService, CULListener {

	private static final Logger logger = 
		LoggerFactory.getLogger(TXSensorBinding.class);
	
	private CULHandler cul;
	private final static String KEY_DEVICE_NAME = "device";
	private String deviceName;
	
	/** 
	 * the refresh interval which is used to poll values from the TXSensor
	 * server (optional, defaults to 60000ms)
	 */
	private long refreshInterval = 60000;
	
	
	public TXSensorBinding() {
	}
		
	
	public void activate() {
		logger.debug("Activating TXSensor binding");
	}

	private void getCULHandler() {
		try {
			logger.debug("Opening CUL device on " + deviceName);
			cul = CULManager.getOpenCULHandler(deviceName, CULMode.SLOW_RF);
			cul.registerListener(this);
		} catch (CULDeviceException e) {
			logger.error("Can't open cul device", e);
			cul = null;
		}
	}

	
	public void deactivate() {
		// deallocate resources here that are no longer needed and 
		// should be reset when activating this binding again
		logger.debug("Deactivating TXSensor binding");
		cul.unregisterListener(this);
		CULManager.close(cul);
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
		return "TXSensor Refresh Service";
	}
	
	/**
	 * @{inheritDoc}
	 */
	@Override
	protected void execute() {
		// the frequently executed code (polling) goes here ...
//		logger.debug("execute() method is called!");
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
	}
		
	/**
	 * @{inheritDoc}
	 */
	@Override
	public void updated(Dictionary<String, ?> config) throws ConfigurationException {
		logger.debug("Received new config");
		if (config != null) {

			// to override the default refresh interval one has to add a
			// parameter to openhab.cfg like
			// <bindingName>:refresh=<intervalInMs>
			String refreshIntervalString = (String) config.get("refresh");
			if (StringUtils.isNotBlank(refreshIntervalString)) {
				refreshInterval = Long.parseLong(refreshIntervalString);
			}
			String deviceName = (String) config.get(KEY_DEVICE_NAME);
			if (StringUtils.isEmpty(deviceName)) {
				logger.error("No device name configured");
				setProperlyConfigured(false);
				throw new ConfigurationException(KEY_DEVICE_NAME,
						"The device name can't be empty");
			} else {
				setNewDeviceName(deviceName);
			}

			setProperlyConfigured(true);
			// read further config parameters here ...

		}
	}

	private void setNewDeviceName(String deviceName) {
		if (cul != null) {
			CULManager.close(cul);
		}
		this.deviceName = deviceName;
		getCULHandler();
	}


	@Override
	public void dataReceived(String data) {
		// It is possible that we see here messages of other protocols
		if (data.startsWith("tA")) {
			logger.debug("Received TXSensor message: " + data);
			handleReceivedMessage(data);
		}
	}

	private void handleReceivedMessage(String message) {
		// TX3 temperature sensor, example tA0396316381A
		int type  = Integer.parseInt(message.substring(2, 3),16);
		String readableType = null;
		String rawAddress = message.substring(3, 5);
		int address = Integer.parseInt(rawAddress,16) & 0xFE;
		double value = 0.0;
		switch (type){
			case 0x00:{ //temperature
		 		value = (Integer.parseInt(message.substring(5, 8)) - 500) / 10.0;
		 		readableType = "temperature";
		 		break;
			}
			case 0x01:{ //pressure
				value = (Integer.parseInt(message.substring(5, 10)) + 50000) / 100.0;
				readableType = "pressure";
				break;
			}
			case 0x0E:{ //humidity
				value = (Integer.parseInt(message.substring(5, 8))) / 10.0;
				readableType = "humidity";
				break;
			}
		}

		TXSensorBindingConfig config = null;
		for (TXSensorBindingProvider provider : providers) {
			config = provider.getConfigForSensor(type, address);
			if (config != null) {
				break;
			}
		}
		if (config != null) {
			eventPublisher.postUpdate(config.getItem().getName(),
					new DecimalType(value));
		} else {
			logger.warn("No item bound to handle message: " + message);
			logger.info("A correct binding configuration would be: {txsensor=\"type="+readableType+";address=0x"+rawAddress+"\"}");
		}
	}

	@Override
	public void error(Exception e) {
		logger.error("Error while communicating with CUL", e);
	}
	

}
