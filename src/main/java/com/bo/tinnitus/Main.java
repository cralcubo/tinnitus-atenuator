package com.bo.tinnitus;

public class Main {
	
	public static void main(String[] args) {
		TinnitusFrequencies tinnitusFrequencies = new TinnitusFrequencies(); //3100
		tinnitusFrequencies.setLeftFrequency(Integer.valueOf(7600));
		tinnitusFrequencies.setRightFrequency(Integer.valueOf(7600));
		SystemSoundParameters systemSoundParameters = new SystemSoundParameters();
		systemSoundParameters.setSource("Soundflower (2ch)");
		systemSoundParameters.setTarget( "AudioQuest DragonFly");
		TinnitusAtenuator filter = new TinnitusAtenuator(tinnitusFrequencies, systemSoundParameters);
		filter.runFilter();
	}
}
