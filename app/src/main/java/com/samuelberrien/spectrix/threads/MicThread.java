package com.samuelberrien.spectrix.threads;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import com.samuelberrien.spectrix.utils.audio.FFT;
import com.samuelberrien.spectrix.utils.core.Visualization;

/**
 * Created by samuel on 30/08/17.
 */

public class MicThread extends VisualizationThread {

	private AudioRecord audioRecord;
	private int bufferSize;
	private FFT fft;

	public MicThread(Visualization visualization) {
		super("MicThread", visualization);

		int audioSource = MediaRecorder.AudioSource.MIC;    // Audio source is the device MIC
		int channelConfig = AudioFormat.CHANNEL_IN_MONO;    // Recording in mono
		int audioEncoding = AudioFormat.ENCODING_PCM_16BIT; // Records in 16bit
		int sampleRateInHz = getValidSampleRates();
		bufferSize = 1024;
		fft = new FFT(bufferSize);

		audioRecord = new AudioRecord(audioSource, sampleRateInHz, channelConfig, audioEncoding, bufferSize);
		audioRecord.startRecording();
	}

	@Override
	protected void work(Visualization visualization) {
		visualization.update(getFrequencyMagns());
	}

	@Override
	protected void onEnd() {
		audioRecord.stop();
		audioRecord.release();
	}

	@Override
	protected float[] getFrequencyMagns() {
		short[] buffer = new short[bufferSize];
		audioRecord.read(buffer, 0, bufferSize);

		double[] real = new double[buffer.length];
		double[] img = new double[real.length];
		for (int i = 0; i < buffer.length; i++) {
			real[i] = (double) buffer[i] / 32768.0; // signed 16 bit
		}

		fft.fft(real, img);

		float[] fft = new float[real.length];
		for (int i = 0; i < fft.length; i++) {
			float x = (float) real[i];
			float y = (float) img[i];
			fft[i] = ((x * x) + (y * y));
			fft[i] = fft[i] <= 1f ? fft[i] : 1f;
		}

		return fft;
	}

	private int getValidSampleRates() {
		int res = 0;
		for (int rate : new int[]{8000, 11025, 16000, 22050, 44100}) {  // add the rates you wish to check against
			int bufferSize = AudioRecord.getMinBufferSize(rate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
			if (bufferSize > 0) {
				// buffer size is valid, Sample rate supported
				res = rate;
			}
		}
		return res;
	}
}
