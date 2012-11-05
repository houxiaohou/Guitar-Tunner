package com.houxiyang.guitar;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.houxiyang.guitar.Utils.Complex;
import com.houxiyang.guitar.Utils.FFT;

public class Tuner extends Activity implements OnClickListener {
	Button btnTune;
	TextView fft;
	TextView results;
	AudioRecord tuner;
	boolean startTuning = true;
	int audioSource = MediaRecorder.AudioSource.MIC;
	int sampleRateInHz = AudioTrack
			.getNativeOutputSampleRate(AudioManager.STREAM_SYSTEM);
	@SuppressWarnings("deprecation")
	int channelConfig = AudioFormat.CHANNEL_CONFIGURATION_MONO;
	int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
	int bufferSizeInBytes;
	int samples;
	short[] audioBuffer;
	short[] audioData;
	double[] temp;
	String fileName;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		LinearLayout ll = new LinearLayout(this);
		ll.setOrientation(1);
		btnTune = new Button(this);
		btnTune.setText("Start Tuning");
		fft = new TextView(this);
		results = new TextView(this);
		ll.addView(btnTune, new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT, 0));
		ll.addView(fft, new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT, 0));
		ll.addView(results, new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT, 0));
		setContentView(ll);
		btnTune.setOnClickListener(this);
		bufferSizeInBytes = 4096;
	}

	public void onClick(View v) {
		if (v == btnTune) {
			onTune(startTuning);
			if (startTuning) {
				((Button) v).setText("Stop Tuning");
			} else {
				((Button) v).setText("Start Tuninig");
			}
			startTuning = !startTuning;
		}
	}

	private void onTune(boolean start) {
		if (start) {
			startTuning();
		} else {
			Toast.makeText(getApplicationContext(), "Tuning Stopped",
					Toast.LENGTH_SHORT).show();
			tuner.stop();
		}
	}

	private void startTuning() {
		tuner = new AudioRecord(audioSource, sampleRateInHz, channelConfig,
				audioFormat, bufferSizeInBytes);

		audioData = new short[bufferSizeInBytes];
		trigger();
	}

	public void trigger() {
		acquire();
		computeFFT();
	}

	public void acquire() {
		try {
			tuner.startRecording();
			samples = tuner.read(audioData, 0, bufferSizeInBytes);
		} catch (Throwable t) {

		}
	}

	public void computeFFT() {
		// Conversion from short to double
		double[] micBufferData = new double[bufferSizeInBytes];// size may need
																// to change
		final int bytesPerSample = 2; // As it is 16bit PCM
		final double amplification = 100.0; // choose a number as you like
		for (int index = 0, floatIndex = 0; index < bufferSizeInBytes
				- bytesPerSample + 1; index += bytesPerSample, floatIndex++) {
			double sample = 0;
			for (int b = 0; b < bytesPerSample; b++) {
				int v = audioData[index + b];
				if (b < bytesPerSample - 1 || bytesPerSample == 1) {
					v &= 0xFF;
				}
				sample += v << (b * 8);
			}
			double sample32 = amplification * (sample / 32768.0);
			micBufferData[floatIndex] = sample32;
		}

		// Create Complex array for use in FFT
		Complex[] fftTempArray = new Complex[bufferSizeInBytes];
		for (int i = 0; i < bufferSizeInBytes; i++) {
			fftTempArray[i] = new Complex(micBufferData[i], 0);
		}

		// Obtain array of FFT data
		final Complex[] fftArray = FFT.fft(fftTempArray);

		// Create an array of magnitude of fftArray
		double[] magnitude = new double[fftArray.length];
		for (int i = 0; i < fftArray.length; i++) {
			magnitude[i] = fftArray[i].abs();
		}
		for (int i = 2; i < samples; i++) {
			fft.append(" " + magnitude[i] + " Hz");
			Log.i("频率", magnitude[i] + " Hz");
		}
	}
}
