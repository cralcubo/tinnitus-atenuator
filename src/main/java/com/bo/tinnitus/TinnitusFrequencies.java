package com.bo.tinnitus;

public class TinnitusFrequencies {
	
	private final double leftFrequency;
	private final double rightFrequency;
	
	public TinnitusFrequencies(double leftFrequency, double rightFrequency) {
		this.leftFrequency = leftFrequency;
		this.rightFrequency = rightFrequency;
	}
	
	public double getLeftFrequency() {
		return leftFrequency;
	}
	
	public double getRightFrequency() {
		return rightFrequency;
	}

}
