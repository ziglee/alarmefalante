package net.cassiolandim.alarmefalante;

import java.util.Calendar;
import java.util.Date;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;

public class AlarmRingingService extends Service {

	public static final String EXTRA_ID = "id";
	public static final long RING_DURATION = 120 * 1000; // in milliseconds

	private final IBinder binder = new LocalBinder();
	private boolean threadRunning = false;
	private PowerManager powerManager;
	private PowerManager.WakeLock wakeLock;
	public long startedAt = 0;
	
	private AlarmSet alarmSet;

	public class LocalBinder extends Binder {
		public AlarmRingingService getService() {
			return AlarmRingingService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		handleCommand(intent);
		return START_STICKY;
	}

	private void handleCommand(Intent intent) {
		long id = intent.getLongExtra(EXTRA_ID, -1);
		if (id < 0 || threadRunning)
			return;
		
		MyApplication app = (MyApplication) getApplication();
		this.alarmSet = app.getDb().find(id);

		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
		boolean dayOfWeekFound = false;
		for (String dayOfWeekString : alarmSet.weekdays.split(",")) {
			if (dayOfWeek == Integer.parseInt(dayOfWeekString)) {
				dayOfWeekFound = true;
				break;
			}
		}
		
		if (!dayOfWeekFound) {
			stopSelf();
			return;
		}
		
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this).setSmallIcon(R.drawable.ic_launcher).setContentTitle("Alarme tocando!")
				.setContentText("Acordaaaa!");

		Intent notificationIntent = new Intent(this, AlarmRingingActivity.class);
		notificationIntent.putExtra(AlarmRingingActivity.EXTRA_ID, id);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		mBuilder.setContentIntent(pendingIntent);
		Notification notification = mBuilder.build();
		startForeground(1000, notification);
		
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				threadRunning = true;
				startedAt = SystemClock.elapsedRealtime();
				while (threadRunning) {
					Intent intent = new Intent(AlarmRingingService.this, TimeTalkerIntentService.class);
					intent.putExtra(TimeTalkerIntentService.EXTRA_VOLUME, alarmSet.volume);
					intent.putExtra(TimeTalkerIntentService.EXTRA_VIBRATION, alarmSet.vibration);
					startService(intent);
					
					long beenPlayingForMilliseconds = SystemClock.elapsedRealtime() - startedAt;
					if (beenPlayingForMilliseconds > RING_DURATION) {
						scheduleSnooze();
					} else {
						try {
							Thread.sleep(6500);
						} catch (InterruptedException e) {
						}
					}
				}
			}
		});
		t.start();

		Intent intent2 = new Intent(this, AlarmRingingActivity.class);
		intent2.putExtra(AlarmRingingActivity.EXTRA_ID, id);
		intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent2);
	}
	
	public void scheduleSnooze() {
		long elapsedRealtime = SystemClock.elapsedRealtime();
		long beenPlayingForMilliseconds = elapsedRealtime - startedAt;
		AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		Intent i = new Intent(this, AlarmRingingService.class);
		i.putExtra(EXTRA_ID, alarmSet.id);
		PendingIntent pi = PendingIntent.getService(AlarmRingingService.this, -1, i, PendingIntent.FLAG_UPDATE_CURRENT);
		alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, (elapsedRealtime - beenPlayingForMilliseconds) + (alarmSet.snoozetime * 1000 * 60), pi);
		
		stopForeground(true);
		stopSelf();
		threadRunning = false;
	}

	@Override
	public void onCreate() {
		powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "AlarmRingingService");
		wakeLock.acquire();
	}

	@Override
	public void onDestroy() {
		threadRunning = false;
		if (wakeLock != null)
			wakeLock.release();
		super.onDestroy();
	}
}
