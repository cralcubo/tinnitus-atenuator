package com.bo.tinnitus;

public class SystemSoundParameters {
	
	private final String audioRouterDeviceName;
	private final String outputSoundDevice;
	
	public SystemSoundParameters(String audioRouterDeviceName, String outputSoundDevice) {
		this.audioRouterDeviceName = audioRouterDeviceName;
		this.outputSoundDevice = outputSoundDevice;
	}
	
	public String getBridgeDeviceName() {
		return audioRouterDeviceName;
	}
	
	public String getOutputSoundDevice() {
		return outputSoundDevice;
	}
}
