package net.cassiolandim.alarmefalante;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.flurry.android.FlurryAgent;

import net.cassiolandim.alarmefalante.AlarmRingingService.LocalBinder;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class AlarmRingingActivity extends Activity {

	public static final String EXTRA_ID = "id";
	
	@SuppressLint("SimpleDateFormat")
	private static final SimpleDateFormat SDF = new SimpleDateFormat("HH:mm");
	
	private AlarmRingingService alarmRingingService;
	private boolean bound = false;
	private TextView clock;
	private Button offButton;
	private Button snoozeButton;
	private Timer timer;
	private long startedAt = 0;
	private String flurryKey;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);  
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON); 
		setContentView(R.layout.activity_alarm_ringing);

		this.flurryKey = getString(R.string.flurry_key);
		
		setVolumeControlStream(AudioManager.STREAM_ALARM);
		
		this.startedAt = SystemClock.elapsedRealtime();
		this.clock = (TextView) findViewById(R.id.clock);
		this.offButton = (Button) findViewById(R.id.off_button);
		this.snoozeButton = (Button) findViewById(R.id.snooze_button);

		offButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (bound)
					alarmRingingService.stopSelf();
				finish();
			}
		});
		
		snoozeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (bound) {
					alarmRingingService.scheduleSnooze();
				}
				finish();
			}
		});
		
		scheduleTimer();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		FlurryAgent.onStartSession(this, flurryKey);
		Intent intent = new Intent(this, AlarmRingingService.class);
		bindService(intent, connection, Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (bound) {
			unbindService(connection);
			bound = false;
		}
		FlurryAgent.onEndSession(this);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (timer != null)
			timer.cancel();
	}
	
	private void scheduleTimer() {
		timer = new Timer();
		TimerTask timerTask = new TimerTask() {
			@Override
			public void run() {
				clock.post(new Runnable() {
	                public void run() {
	                	clock.setText(SDF.format(new Date()));
	                };
				});
				long beenPlayingForMilliseconds = SystemClock.elapsedRealtime() - startedAt;
				if (beenPlayingForMilliseconds > AlarmRingingService.RING_DURATION) {
					finish();
				}
			}
		};
		timer.schedule(timerTask, 1000, 1000);
	}

	private ServiceConnection connection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			LocalBinder binder = (LocalBinder) service;
			alarmRingingService = binder.getService();
			bound = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			bound = false;
			finish();
		}
	};
}
