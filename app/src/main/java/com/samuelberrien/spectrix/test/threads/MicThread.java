package com.samuelberrien.spectrix.test.threads;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import com.samuelberrien.spectrix.test.utils.Complex;
import com.samuelberrien.spectrix.test.utils.Visualization;
import com.samuelberrien.spectrix.test.utils.audio.FFT;

import org.jtransforms.fft.FloatFFT_1D;

import java.nio.ByteBuffer;

/**
 * Created by samuel on 30/08/17.
 */

public class MicThread extends CancelableThread {

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
		short[] buffer = new short[bufferSize];
		audioRecord.read(buffer, 0, bufferSize);

		double[] toTransform = new double[buffer.length];
		double[] img = new double[toTransform.length];
		for (int i = 0; i < buffer.length; i++) {
			toTransform[i] = (double) buffer[i] / 32768.0; // signed 16 bit
		}

		fft.fft(toTransform, img);

		float[] fft = new float[toTransform.length];
		for (int i = 0; i < fft.length; i++) {
			float real = (float) toTransform[i];
			float imag = (float) img[i];
			fft[i] = ((real * real) + (imag * imag));
		}

		visualization.update(fft);
	}

	@Override
	protected void onEnd() {
		audioRecord.startRecording();
		audioRecord.release();
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
