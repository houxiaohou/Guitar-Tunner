package com.houxiyang.guitar;

import android.app.Activity;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;

import com.houxiyang.guitar.Utils.FFTbase;

public class MainActivity extends Activity {

	private RecordButton mRecordButton = null;
	private AudioRecord aRecord;
	private final int sampleRate = 44100;

	private void onRecord(boolean start) {
		if (start) {
			startRecording();
		} else {
			stopRecording();
		}
	}

	@SuppressWarnings("deprecation")
	private void startRecording() {
		
		int minSize = 256;
		
		int bufferSize = AudioRecord.getMinBufferSize(sampleRate,
	             AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
		aRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate,
				AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT, bufferSize);
		short[] buffer = new short[minSize];
		aRecord.startRecording();
		int bufferReadResult = aRecord.read(buffer, 0, minSize);

		double[] re = new double[minSize];
		double[] im = new double[minSize];

		double[] newArray = new double[minSize * 2];
		double[] magns = new double[minSize];

		double MaxMagn = 0;
		double pitch = 0;

		for (int i = 0; i < minSize && i < bufferReadResult; i++) {
			re[i] = (double) buffer[i] / 32768.0;
			im[i] = 0;
		}

		newArray = FFTbase.fft(re, im, true);

		for (int i = 0; i < newArray.length; i += 2) {
			re[i / 2] = newArray[i];
			im[i / 2] = newArray[i + 1];
			magns[i / 2] = Math.sqrt(re[i / 2] * re[i / 2] + im[i / 2]
					* im[i / 2]);
		}

		for (int i = 0; i < (magns.length) / 2; i++) {
			if (magns[i] > MaxMagn) {
				MaxMagn = magns[i];
				pitch = i;
			}
			Log.i("pitch and magnitude", "" + MaxMagn + "   " + pitch*15.625f);
		}

	}

	private void stopRecording() {
		aRecord.stop();
		aRecord.release();
		aRecord = null;
	}

	class RecordButton extends Button {

		boolean mStartRecording = true;

		OnClickListener clicker = new OnClickListener() {

			public void onClick(View v) {
				onRecord(mStartRecording);
				if (mStartRecording) {
					setText("停止录音");
				} else {
					setText("开始录音");
				}
				mStartRecording = !mStartRecording;
			}

		};

		public RecordButton(Context ctx) {
			super(ctx);
			setText("开始录音");
			setOnClickListener(clicker);
		}

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		LinearLayout ll = new LinearLayout(this);
		mRecordButton = new RecordButton(this);
		ll.addView(mRecordButton, new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT, 0));
		setContentView(ll);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
