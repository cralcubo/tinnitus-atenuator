package com.bo.tinnitus.utils;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;

public class DataLineFinder {
	
	/**
	 * Find a DataLine by name.
	 * <p />
	 * Because a TargetDataLine and a SourceDataLine can have the same name,
	 * they are distinguished by the type of DataLine specified in the argument
	 * clazz.
	 * 
	 * @param name
	 *            of the DataLine requested.
	 * @param clazz
	 *            is the type DataLine needed, it can be either TargetDataLine
	 *            or SourceDataLine.
	 * @return a DataLine
	 * @throws LineUnavailableException
	 */
	@SuppressWarnings("unchecked")
	public static <T> T findDataLine(String name, Class<T> clazz) throws LineUnavailableException {
		for (Mixer.Info i : AudioSystem.getMixerInfo()) {
			if (i.getName().equals(name)) {
				Mixer mixer = AudioSystem.getMixer(i);
				Line.Info[] lis;
				if (clazz.isAssignableFrom(TargetDataLine.class)) {
					lis = mixer.getTargetLineInfo();
				} else {
					lis = mixer.getSourceLineInfo();
				}
					  
				for (Line.Info li : lis) {
					Line line = mixer.getLine(li);
					if (clazz.isAssignableFrom(line.getClass())) {
						return (T) line;
					}
				}
			}
		}
		return null;
	}

}
