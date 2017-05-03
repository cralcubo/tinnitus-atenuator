package com.bo.tinnitus;

import java.io.IOException;

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

	private final SourceDataLine outputAudioLine;
	private final TargetDataLine bridgeAudioLine;

	private final IirFilter leftChannelFilter;
	private final IirFilter rightChannelFilter;

	public TinnitusAtenuator(TinnitusFrequencies tinnitusFrequencies, SystemSoundParameters systemSoundParameters) {
		try {
			bridgeAudioLine = DataLineFinder.findDataLine(systemSoundParameters.getBridgeDeviceName(),
					TargetDataLine.class);
		} catch (LineUnavailableException e) {
			throw new RuntimeException("There was an error accessing the Audio device ["
					+ systemSoundParameters.getBridgeDeviceName() + "].", e);
		}

		try {
			outputAudioLine = DataLineFinder.findDataLine(systemSoundParameters.getOutputSoundDevice(),
					SourceDataLine.class);
		} catch (LineUnavailableException e) {
			throw new RuntimeException("There was an error accessing the Audio device ["
					+ systemSoundParameters.getOutputSoundDevice() + "].", e);
		}

		double sampleRate = bridgeAudioLine.getFormat().getSampleRate();
		leftChannelFilter = createNotchFilter(tinnitusFrequencies.getLeftFrequency(), sampleRate);
		rightChannelFilter = createNotchFilter(tinnitusFrequencies.getRightFrequency(), sampleRate);
	}

	public void runFilter() {
		LoggerUtils.logDebug(log, () -> "Running notched filter.");

		Thread t = new Thread(() -> {
			AudioFormat af = bridgeAudioLine.getFormat();
			int frameSize = af.getFrameSize();
			byte[] buffer = new byte[frameSize * 128];
			try {
				bridgeAudioLine.open();
				bridgeAudioLine.start();

				outputAudioLine.open(af);
				outputAudioLine.start();
			} catch (LineUnavailableException e) {
				throw new RuntimeException("There was an error opening the Audio devices from the computer.", e);
			}

			try (AudioInputStream is = new AudioInputStream(bridgeAudioLine);
			     AudioInputStream filteredStream = SignalFilterAudioInputStream.getAudioInputStream(is,
							new SignalFilter[] { leftChannelFilter, rightChannelFilter });) {
				int read;
				while ((read = filteredStream.read(buffer)) > 0) {
					outputAudioLine.write(buffer, 0, read);
				}
			} catch (IOException e) {
				throw new RuntimeException("There was an error reading the audio data from the computer.", e);
			}
		});
		t.start();
	}
	
	public void stopFilter() {
		LoggerUtils.logDebug(log, () -> "Stopping filter.");
		DataLine[] lines = { outputAudioLine, bridgeAudioLine };
		for (DataLine dl : lines) {
			dl.drain();
			dl.close();
			dl.stop();
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

}
