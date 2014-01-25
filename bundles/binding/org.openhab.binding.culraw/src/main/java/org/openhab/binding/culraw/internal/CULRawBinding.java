/**
 * Copyright (c) 2010-2014, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.culraw.internal;

import java.util.Dictionary;

import org.openhab.binding.culraw.CULRawBindingProvider;
import org.apache.commons.lang.StringUtils;
import org.openhab.core.binding.AbstractActiveBinding;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.library.types.UpDownType;
import org.openhab.core.types.Command;
import org.openhab.core.types.State;
import org.openhab.io.transport.cul.CULCommunicationException;
import org.openhab.io.transport.cul.CULDeviceException;
import org.openhab.io.transport.cul.CULHandler;
import org.openhab.io.transport.cul.CULManager;
import org.openhab.io.transport.cul.CULMode;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implement this class if you are going create an actively polling service like
 * querying a Website/Device.
 * 
 * @author MaJo
 * @since 1.4.0
 */
public class CULRawBinding extends AbstractActiveBinding<CULRawBindingProvider>
		implements ManagedService {

	private static final Logger logger = LoggerFactory
			.getLogger(CULRawBinding.class);

	/**
	 * the refresh interval which is used to poll values from the CULRaw server
	 * (optional, defaults to 60000ms)
	 */
	private long refreshInterval = 60000;

	public CULRawBinding() {
	}

	public void activate() {
	}

	public void deactivate() {
		// deallocate resources here that are no longer needed and
		// should be reset when activating this binding again
	}

	/**
	 * @{inheritDoc
	 */
	@Override
	protected long getRefreshInterval() {
		return refreshInterval;
	}

	/**
	 * @{inheritDoc
	 */
	@Override
	protected String getName() {
		return "CULRaw Refresh Service";
	}

	/**
	 * @{inheritDoc
	 */
	@Override
	protected void execute() {
		// the frequently executed code (polling) goes here ...
	}

	/**
	 * @{inheritDoc
	 */
	@Override
	protected void internalReceiveCommand(String itemName, Command command) {
		// the code being executed when a command was sent on the openHAB
		// event bus goes here. This method is only called if one of the
		// BindingProviders provide a binding for the given 'itemName'.
		CULRawGenericBindingProvider.CULRawBindingConfig config = null;
		for (CULRawBindingProvider provider : providers) {
			config = provider.getItemConfig(itemName);
			if (config != null) {
				break;
			}

		}
		if (!StringUtils.isEmpty(config.getDevice())) {
			try {
				CULHandler cul = CULManager.getOpenCULHandler(
						config.getDevice(), CULMode.SLOW_RF);
				if (cul != null) {
					if (command instanceof OnOffType) {
						switch ((OnOffType) command) {
						case ON:
							cul.send(config.getOnUpCommand());
							break;
						case OFF:
							cul.send(config.getOffDownCommand());
							break;
						}
					} else if (command instanceof UpDownType) {
						switch ((UpDownType) command) {
						case UP:
							cul.send(config.getOnUpCommand());
							break;
						case DOWN:
							cul.send(config.getOffDownCommand());
							break;
						}
					}
					CULManager.close(cul);
				} else {
					logger.error("Can't open CUL at port " + config.getDevice());
				}
			} catch (CULDeviceException e) {
				logger.error("Can't open CUL", e);
			} catch (CULCommunicationException e) {
				logger.error("Can't set intertechno parameters", e);
			}
		}

	}

	/**
	 * @{inheritDoc
	 */
	@Override
	protected void internalReceiveUpdate(String itemName, State newState) {
		// the code being executed when a state was sent on the openHAB
		// event bus goes here. This method is only called if one of the
		// BindingProviders provide a binding for the given 'itemName'.
	}

	/**
	 * @{inheritDoc
	 */
	@Override
	public void updated(Dictionary<String, ?> config)
			throws ConfigurationException {
		if (config != null) {

			// to override the default refresh interval one has to add a
			// parameter to openhab.cfg like
			// <bindingName>:refresh=<intervalInMs>
			String refreshIntervalString = (String) config.get("refresh");
			if (StringUtils.isNotBlank(refreshIntervalString)) {
				refreshInterval = Long.parseLong(refreshIntervalString);
			}

			// read further config parameters here ...

			setProperlyConfigured(true);
		}
	}

}
