package net.cassiolandim.alarmefalante;

import java.util.Date;

import org.joda.time.DateTime;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class AlarmSetterService extends IntentService {

	public static final String CREATE = "CREATE";
	public static final String CANCEL = "CANCEL";

	private IntentFilter matcher;

	public AlarmSetterService() {
		super("AlarmService");
		matcher = new IntentFilter();
		matcher.addAction(CREATE);
		matcher.addAction(CANCEL);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		String action = intent.getAction();
		if (matcher.matchAction(action)) {
			MyApplication app = (MyApplication) getApplication();
			execute(action, app.getHour(), app.getMinute());
		}
	}

	private void execute(String action, int hour, int minute) {
		AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		Intent i = new Intent(this, AlarmRingingService.class);
		PendingIntent pi = PendingIntent.getService(this, 999, i, PendingIntent.FLAG_UPDATE_CURRENT);
		if (CREATE.equals(action)) {
			DateTime dt = new DateTime();
			dt = dt.withHourOfDay(hour);
			dt = dt.withMinuteOfHour(minute);
			dt = dt.withSecondOfMinute(0);
			if (dt.toDate().before(new Date()))
				dt = dt.plusDays(1);
			am.setRepeating(AlarmManager.RTC_WAKEUP, dt.getMillis(), AlarmManager.INTERVAL_DAY, pi);
		} else if (CANCEL.equals(action)) {
			am.cancel(pi);
		}
	}
}
