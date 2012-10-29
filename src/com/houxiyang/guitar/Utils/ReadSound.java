package com.houxiyang.guitar.Utils;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

public class ReadSound {

	private AudioRecord aRecord;
	private int sampleRate;
	private int readSize;
	private int index;

	public ReadSound(int sampleRate, int readSize, int index) {
		this.sampleRate = sampleRate;
		this.readSize = readSize;
		this.index = index;
	}

	@SuppressWarnings("deprecation")
	public AudioRecord readSoundToBuffer() {
		int minSize = AudioRecord.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT);
		aRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate,
				AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT, minSize);
		short[] buffer = new short[readSize];
		aRecord.startRecording();
		aRecord.read(buffer, index, readSize);
		return aRecord;
	}

}
