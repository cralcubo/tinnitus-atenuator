package com.bo.tinnitus;

public class Main {
	
	public static void main(String[] args) {
		TinnitusFrequencies tinnitusFrequencies = new TinnitusFrequencies(7600, 7600); //3100
		SystemSoundParameters systemSoundParameters = new SystemSoundParameters("Soundflower (2ch)", "AudioQuest DragonFly");
		TinnitusAtenuator filter = new TinnitusAtenuator(tinnitusFrequencies, systemSoundParameters);
		filter.runFilter();
	}
}
