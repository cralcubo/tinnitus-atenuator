package com.bo.tinnitus;

public class TinnitusFrequencies {
	
	private Number leftFrequency;
	private Number rightFrequency;
	
	public TinnitusFrequencies() {
		this(null, null);
	}
	
	public TinnitusFrequencies(Number leftFrequency, Number rightFrequency) {
		this.leftFrequency = leftFrequency;
		this.rightFrequency = rightFrequency;
	}
	public Number getLeftFrequency() {
		return leftFrequency;
	}
	public void setLeftFrequency(Number leftFrequency) {
		this.leftFrequency = leftFrequency;
	}
	public Number getRightFrequency() {
		return rightFrequency;
	}
	public void setRightFrequency(Number rightFrequency) {
		this.rightFrequency = rightFrequency;
	}
}
