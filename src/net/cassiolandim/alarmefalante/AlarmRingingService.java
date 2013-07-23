package net.cassiolandim.alarmefalante;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

public class AlarmRingingService extends Service {

	private static final int SNOOZE_INTERVAL_MINUTES = 5;

	private final IBinder binder = new LocalBinder();
	private boolean threadRunning = true;

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
	public void onCreate() {
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this).setSmallIcon(R.drawable.ic_launcher).setContentTitle("Alarme tocando!")
				.setContentText("Acorda Renataaa!");

		Intent notificationIntent = new Intent(this, AlarmRingingActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		mBuilder.setContentIntent(pendingIntent);
		startForeground(1000, mBuilder.build());

		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				while (threadRunning) {
					sendBroadcast(new Intent(AlarmRingingService.this, TimeTalkerReceiver.class));
					try {
						Thread.sleep(SNOOZE_INTERVAL_MINUTES * 1000 * 60);
					} catch (InterruptedException e) {
					}
				}
			}
		});
		t.start();

		Intent intent = new Intent(this, AlarmRingingActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}

	@Override
	public void onDestroy() {
		threadRunning = false;
		super.onDestroy();
	}
}
