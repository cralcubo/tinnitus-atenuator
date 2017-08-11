package com.bo.tinnitus;

public class SystemSoundParameters {
	
	private String sourceDeviceName;
	private String targetSoundDevice;
		
	public SystemSoundParameters() {
		this(null, null);
	}
	
	public SystemSoundParameters(String sourceDeviceName, String targetSoundDevice) {
		this.sourceDeviceName = sourceDeviceName;
		this.targetSoundDevice = targetSoundDevice;
	}

	public String getSourceDeviceName() {
		return sourceDeviceName;
	}
	
	public String getTargetSoundDevice() {
		return targetSoundDevice;
	}

	public void setSource(String source) {
		this.sourceDeviceName = source;
	}

	public void setTarget(String target) {
		this.targetSoundDevice = target;
	}
}
