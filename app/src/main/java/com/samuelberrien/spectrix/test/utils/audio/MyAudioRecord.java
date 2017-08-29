package com.samuelberrien.spectrix.test.utils.audio;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.media.audiofx.Visualizer;

import java.nio.ByteBuffer;

/**
 * Created by Jean-François on 28/08/2017.
 */

public class MyAudioRecord {

	private AudioRecord audioInput;
	private FFT fft;

	public MyAudioRecord() {
		int channel_config = AudioFormat.CHANNEL_IN_MONO;
		int format = AudioFormat.ENCODING_PCM_16BIT;
		int sampleSize = 8000;
		int bufferSize = Visualizer.getCaptureSizeRange()[1];
		audioInput = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleSize, channel_config, format, bufferSize);
		fft = new FFT(Visualizer.getCaptureSizeRange()[1]);
	}

	public final float[] getFreqMagn() {
		float[] magns = new float[Visualizer.getCaptureSizeRange()[1] / 2];
		byte[] tmp = new byte[Visualizer.getCaptureSizeRange()[1]];
		int bufferReadResult = audioInput.read(tmp, 0, Visualizer.getCaptureSizeRange()[1]);

		double[] toTransform = new double[Visualizer.getCaptureSizeRange()[1]];
		for(int i = 0; i < bufferReadResult; i+=8) {
			toTransform[i] = (double) tmp[i] / 32768.0; // signed 16 bit
		}
		fft.fft(toTransform, toTransform.clone());

		for (int i = 0; i < magns.length; i++) {
			float real = (float) toTransform[(i * 2) + 0];
			float imag = (float) toTransform[(i * 2) + 1];
			magns[i] = ((real * real) + (imag * imag));
		}
		return magns;
	}

	public static byte[] toByteArray(double value) {
		byte[] bytes = new byte[8];
		ByteBuffer.wrap(bytes).putDouble(value);
		return bytes;
	}

	public static double toDouble(byte[] bytes) {
		return ByteBuffer.wrap(bytes).getDouble();
	}
}
