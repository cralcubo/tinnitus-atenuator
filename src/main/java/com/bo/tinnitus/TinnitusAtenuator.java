package com.bo.tinnitus;

import java.io.IOException;
import java.util.Arrays;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bo.tinnitus.utils.DataLineFinder;
import com.bo.tinnitus.utils.LoggerUtils;

import biz.source_code.dsp.filter.FilterPassType;
import biz.source_code.dsp.filter.IirFilter;
import biz.source_code.dsp.filter.IirFilterDesignExstrom;
import biz.source_code.dsp.filter.SignalFilter;
import biz.source_code.dsp.sound.SignalFilterAudioInputStream;

public class TinnitusAtenuator {
	private final static Logger log = LoggerFactory.getLogger(TinnitusAtenuator.class);
	
	private TinnitusFrequencies tinnitusFrequencies;
	private SystemSoundParameters systemSoundParameters;
	
	private TargetDataLine originLine;
	private SourceDataLine targetLine;
	

	public TinnitusAtenuator(TinnitusFrequencies tinnitusFrequencies, SystemSoundParameters systemSoundParameters) {
		this.tinnitusFrequencies = tinnitusFrequencies;
		this.systemSoundParameters = systemSoundParameters;
	}

	public void runFilter() {
		LoggerUtils.logDebug(log, () -> "Running notched filter.");
		processAudio(true);
	}
	
	public void bypassAudio() {
		LoggerUtils.logDebug(log, () -> "Bypassing audio signal.");
		processAudio(false);
	}

	public void stop() {
		LoggerUtils.logDebug(log, () -> "Stopping filter.");
		Arrays.asList(originLine, targetLine).forEach(DataLine::close);
	}
	
	public void setSystemSoundParameters(SystemSoundParameters systemSoundParameters) {
		this.systemSoundParameters = systemSoundParameters;
	}
	
	public SystemSoundParameters getSystemSoundParameters() {
		return systemSoundParameters;
	}
	
	public void setTinnitusFrequencies(TinnitusFrequencies tinnitusFrequencies) {
		this.tinnitusFrequencies = tinnitusFrequencies;
	}
	
	public TinnitusFrequencies getTinnitusFrequencies() {
		return tinnitusFrequencies;
	}
	
	private TargetDataLine createTargetDataLine() {
		try {
			LoggerUtils.logDebug(log, () -> "Creating Data Line for the audio source: " + systemSoundParameters.getSourceDeviceName());
			return DataLineFinder.findDataLine(systemSoundParameters.getSourceDeviceName(), TargetDataLine.class);
		} catch (LineUnavailableException e) {
			throw new RuntimeException("There was an error accessing the Audio device ["
					+ systemSoundParameters.getSourceDeviceName() + "].", e);
		}
	}
	
	private SourceDataLine createSourceDataLine() {
		try {
			LoggerUtils.logDebug(log, () -> "Creating Data Line for the audio destination: " + systemSoundParameters.getTargetSoundDevice());
			return DataLineFinder.findDataLine(systemSoundParameters.getTargetSoundDevice(), SourceDataLine.class);
		} catch (LineUnavailableException e) {
			throw new RuntimeException("There was an error accessing the Audio device ["
					+ systemSoundParameters.getTargetSoundDevice() + "].", e);
		}
	}

	private IirFilter createNotchFilter(double freq, double sampleRate) {
		double SQRT = Math.sqrt(2);
		// calc low cut freq
		double lowFreq = freq / SQRT;
		// calc high cut freq
		double highFreq = freq * SQRT;

		if (log.isDebugEnabled()) {
			log.debug("Channel  frequency[{}] notched: {} - {}", freq, lowFreq, highFreq);
		}

		return new IirFilter(
				IirFilterDesignExstrom.design(FilterPassType.bandstop, 5, lowFreq / sampleRate, highFreq / sampleRate));
	}
	
	private void processAudio(boolean isFiltered) {
		// Initialize Audio Lines
		originLine = createTargetDataLine();
		targetLine = createSourceDataLine();
		if(originLine == null || targetLine == null)
		{
			log.error("There is a missing line: OriginLine[{}] | TargetLine[{}]", originLine, targetLine);
			throw new RuntimeException("Cannot process audio without audio lines.");
		}
		
		Thread t = new Thread(() -> {
			AudioFormat af = originLine.getFormat();
			int frameSize = af.getFrameSize();
			byte[] buffer = new byte[frameSize * 128];
			try {
				originLine.open();
				originLine.start();

				targetLine.open(af);
				targetLine.start();
			} catch (LineUnavailableException e) {
				throw new RuntimeException("There was an error opening the Audio devices from the computer.", e);
			}
			
			try(AudioInputStream ais = getAudioStream(isFiltered, originLine)) {
				int read;
				while ((read = ais.read(buffer)) > 0) {
					targetLine.write(buffer, 0, read);
				}
			} catch (IOException e) {
				throw new RuntimeException("There was an error reading the audio data from the computer.", e);
			}
		});
		t.setName("Tinnitus Filter Thread");
		t.start();
	}
	
	private AudioInputStream getAudioStream(boolean isFiltered, TargetDataLine targetLine) {
		if (targetLine == null) {
			throw new RuntimeException("No audio lines were set up.");
		}
		
		AudioInputStream is = new AudioInputStream(targetLine);
		if (!isFiltered) {
			return is;
		}
		double sampleRate = targetLine.getFormat().getSampleRate();
		IirFilter leftChannelFilter = createNotchFilter(tinnitusFrequencies.getLeftFrequency().doubleValue(), sampleRate);
		IirFilter rightChannelFilter = createNotchFilter(tinnitusFrequencies.getRightFrequency().doubleValue(), sampleRate);
		return SignalFilterAudioInputStream.getAudioInputStream(is, new SignalFilter[] { leftChannelFilter, rightChannelFilter });
	}

}
