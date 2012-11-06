package com.houxiyang.guitar;

import java.math.BigDecimal;
import java.math.RoundingMode;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.houxiyang.guitar.Utils.KeepScreenon;
import com.houxiyang.guitar.Utils.TunnerThread;

public class MainActivity extends Activity {

	private TunnerButton button = null;
	private TextView frequencyShow = null;
	private TunnerThread tunner;

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
		LinearLayout ll = new LinearLayout(this);
		frequencyShow = new TextView(this);
		button = new TunnerButton(this);
		button.setText("开始调音");
		ll.addView(button, new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT, 0));
		ll.addView(frequencyShow, new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT, 0));
		setContentView(ll);
	}

	class TunnerButton extends Button {

		boolean startRecording = true;

		OnClickListener listener = new OnClickListener() {

			public void onClick(View v) {
				startTunning(startRecording);
				startRecording = false;
			}

		};

		public TunnerButton(Context context) {
			super(context);
			setText("开始调音");
			setOnClickListener(listener);
		}

	}

	private void startTunning(boolean startRecording) {
		if (startRecording) {
			tunner = new TunnerThread(handler, callback);
			tunner.start();
			button.setText("停止调音");
		} else {
			tunner.close();
			button.setText("开始调音");
		}

	}

	private void updateText(double currentFrequency) {
		BigDecimal a = new BigDecimal(currentFrequency);
		BigDecimal result = a.setScale(2, RoundingMode.DOWN);
		frequencyShow.setText(String.valueOf(result));
	}
}
