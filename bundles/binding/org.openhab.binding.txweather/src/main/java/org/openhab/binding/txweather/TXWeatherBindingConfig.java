package org.openhab.binding.txweather;

import org.openhab.core.binding.BindingConfig;
import org.openhab.core.items.Item;

public class TXWeatherBindingConfig implements BindingConfig {
	private int type;
	private int address;
	private Item item;

	public TXWeatherBindingConfig(Item item, int type, int address) {
		this.item = item;
		this.type = type;
		this.address = address;
	}

	public int getType() {
		return type;
	}

	public int getAddress() {
		return address;
	}

	public Item getItem() {
		return item;
	}

}
