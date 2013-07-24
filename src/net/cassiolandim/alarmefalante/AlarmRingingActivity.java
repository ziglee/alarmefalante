package net.cassiolandim.alarmefalante;

import net.cassiolandim.alarmefalante.AlarmRingingService.LocalBinder;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;

public class AlarmRingingActivity extends Activity {

	private static final int SNOOZE_INTERVAL = 5 * 1000 * 60;
	
	private AlarmRingingService alarmRingingService;
	private boolean bound = false;
	private AlarmManager alarmManager;
	
	private Button offButton;
	private Button snoozeButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_alarm_ringing);

		alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		
		offButton = (Button) findViewById(R.id.off_button);
		snoozeButton = (Button) findViewById(R.id.snooze_button);

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
				if (bound)
					alarmRingingService.stopSelf();
				
				Intent i = new Intent(AlarmRingingActivity.this, AlarmRingingService.class);
				PendingIntent pi = PendingIntent.getService(AlarmRingingActivity.this, 555, i, PendingIntent.FLAG_UPDATE_CURRENT);
				alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + SNOOZE_INTERVAL, pi);
				finish();
			}
		});
	}

	@Override
	protected void onStart() {
		super.onStart();
		// Bind to LocalService
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
		}
	};
}
