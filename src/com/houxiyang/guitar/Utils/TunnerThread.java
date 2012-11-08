package com.houxiyang.guitar.Utils;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;

/**
 * 通过调用FFT方法来实时计算输入音频的频率
 * 
 * @author Young
 * 
 */
public class TunnerThread extends Thread {

	static {
		System.loadLibrary("FFT");
	}

	public native double processSampleData(byte[] sample, int sampleRate);

	private static final int[] OPT_SAMPLE_RATES = { 11025, 8000, 22050, 44100 };
	private static final int[] BUFFERSIZE_PER_SAMPLE_RATE = { 8 * 1024,
			4 * 1024, 16 * 1024, 32 * 1024 };

	private int SAMPLE_RATE = 8000;
	private int READ_BUFFERSIZE = 4 * 1024;
	private double currentFrequency;

	private Handler handler;
	private Runnable callback;
	private AudioRecord audioRecord;

	public TunnerThread(Handler handler, Runnable callback) {
		this.handler = handler;
		this.callback = callback;
		initAudioRecord();
	}

	// 每个device的初始化参数可能不同
	private void initAudioRecord() {
		int counter = 0;
		for (int sampleRate : OPT_SAMPLE_RATES) {
			initAudioRecord(sampleRate);
			if (audioRecord.getState() == AudioRecord.STATE_INITIALIZED) {
				SAMPLE_RATE = sampleRate;
				READ_BUFFERSIZE = BUFFERSIZE_PER_SAMPLE_RATE[counter];
				break;
			}
			counter++;
		}
	}

	@SuppressWarnings("deprecation")
	private void initAudioRecord(int sampleRate) {
		audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
				sampleRate, AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT, sampleRate * 6);
	}

	@Override
	public void run() {
		audioRecord.startRecording();
		byte[] bufferRead = new byte[READ_BUFFERSIZE];
		while (audioRecord.read(bufferRead, 0, READ_BUFFERSIZE) > 0) {
			currentFrequency = processSampleData(bufferRead, SAMPLE_RATE);
			if (currentFrequency > 0) {
				handler.post(callback);
				try {
					if (audioRecord.getState() ==  AudioRecord.STATE_INITIALIZED)
						audioRecord.stop();
					Thread.sleep(20);
					audioRecord.startRecording();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void close() {
		if (audioRecord != null
				&& audioRecord.getState() == AudioRecord.STATE_INITIALIZED) {
			audioRecord.stop();
			audioRecord.release();
		}
	}

	public double getCurrentFrequency() {
		return currentFrequency;
	}

}
