package org.openhab.binding.minmaxnotify;

import org.openhab.core.binding.BindingConfig;

public class MinMaxNotifyBindingConfig implements BindingConfig {
	public MinMaxNotifyBindingConfig(double min, double max, double step) {
		this.setMinValue(min);
		this.setMaxValue(max);
		this.setStep(step);
	}

	public double getMinValue() {
		return minValue;
	}

	public void setMinValue(double minValue) {
		this.minValue = minValue;
	}

	public double getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(double maxValue) {
		this.maxValue = maxValue;
	}

	public double getStep() {
		return step;
	}

	public void setStep(double step) {
		this.step = step;
	}

	private double minValue;
	private double maxValue;
	private double step;

}
