package com.houxiyang.guitar;

import java.math.BigDecimal;
import java.math.RoundingMode;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.houxiyang.guitar.Utils.KeepScreenon;
import com.houxiyang.guitar.Utils.TunnerThread;

public class MainActivity extends Activity {

	private boolean startRecording = true;

	private TunnerThread tunner;

	private Button tunning_button = null;
	private TextView frequencyView = null;

	private Handler handler = new Handler();
	private Runnable callback = new Runnable() {

		public void run() {
			updateText(tunner.getCurrentFrequency());
		}

	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		KeepScreenon.keepScreenOn(this, true);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);

		tunning_button = (Button) findViewById(R.id.tunning_button);
		frequencyView = (TextView) findViewById(R.id.frequency);
		tunning_button.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				onRecord(startRecording);
				if (startRecording) {
					tunning_button.setText(R.string.stop_tunning);
				} else {
					tunning_button.setText(R.string.start_tunning);
				}
				startRecording = !startRecording;
			}

		});
	}

	private void onRecord(boolean startRecording) {
		if (startRecording) {
			startTunning();
		} else {
			stopTunning();
		}
	}

	private void startTunning() {
		tunner = new TunnerThread(handler, callback);
		tunner.start();
	}
	
	private void stopTunning() {
		tunner.close();
	}
	
	private void updateText(double currentFrequency) {
		while (currentFrequency < 82.41) {
			currentFrequency = currentFrequency * 2;
		}
		while (currentFrequency > 164.81) {
			currentFrequency = currentFrequency * 0.5;
		}
		BigDecimal a = new BigDecimal(currentFrequency);
		BigDecimal result = a.setScale(2, RoundingMode.DOWN);
		frequencyView.setText(String.valueOf(result));
	}
}
