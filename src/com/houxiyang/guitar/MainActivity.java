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
		int minSize = AudioRecord.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT);
		aRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate,
				AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT, minSize);
		byte[] buffer = new byte[minSize];
		aRecord.startRecording();
		int bufferReadResult = aRecord.read(buffer, 0, minSize);
		byte[] tmpBuff = new byte[bufferReadResult];
		Log.e("x", tmpBuff.toString());
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
					setText("Í£Ö¹Â¼Òô");
				} else {
					setText("¿ªÊ¼Â¼Òô");
				}
				mStartRecording = !mStartRecording;
			}

		};

		public RecordButton(Context ctx) {
			super(ctx);
			setText("¿ªÊ¼Â¼Òô");
			setOnClickListener(clicker);
		}

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		LinearLayout ll = new LinearLayout(this);
        mRecordButton = new RecordButton(this);
        ll.addView(mRecordButton,
            new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                0));
        setContentView(ll);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
}
