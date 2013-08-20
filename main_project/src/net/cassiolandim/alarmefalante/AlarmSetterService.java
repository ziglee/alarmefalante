package net.cassiolandim.alarmefalante;

import java.util.Date;

import org.joda.time.DateTime;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;

public class AlarmSetterService extends IntentService {

	public static final String CREATE = "CREATE";
	public static final String CANCEL = "CANCEL";
	public static final String RECONFIGURE = "RECONFIGURE";

	private IntentFilter matcher;

	public AlarmSetterService() {
		super("AlarmService");
		matcher = new IntentFilter();
		matcher.addAction(CREATE);
		matcher.addAction(CANCEL);
		matcher.addAction(RECONFIGURE);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		MyApplication app = (MyApplication) getApplication();
		AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		String action = intent.getAction();
		if (matcher.matchAction(action)) {
			if (RECONFIGURE.equals(action)) {
				AlarmSet alarmSet = app.getLegacyAlarmSet();
				MyDatabase db = app.getDb();
				if (alarmSet != null)
					db.insert(alarmSet);
				
				Cursor cursor = db.query();
				if (cursor != null) {
					do {
						createAlarm(am, MyDatabase.populateModel(cursor));
					} while (cursor.moveToNext());
				}
			} else {
				long id = intent.getLongExtra("id", -1);
				MyDatabase db = app.getDb();
				if (CREATE.equals(action)) {
					createAlarm(am, db.find(id));
				}  else if (CANCEL.equals(action)) {
					Intent i = new Intent(this, AlarmRingingService.class);
					PendingIntent pi = PendingIntent.getService(this, (int) id, i, PendingIntent.FLAG_UPDATE_CURRENT);
					am.cancel(pi);
				}
			}
			
		}
	}

	private void createAlarm(AlarmManager am, AlarmSet alarmSet) {
		DateTime dt = new DateTime();
		dt = dt.withHourOfDay(alarmSet.hour);
		dt = dt.withMinuteOfHour(alarmSet.minute);
		dt = dt.withSecondOfMinute(0);
		if (dt.toDate().before(new Date()))
			dt = dt.plusDays(1);
		
		Intent i = new Intent(this, AlarmRingingService.class);
		i.putExtra(AlarmRingingService.EXTRA_ID, alarmSet.id);
		PendingIntent pi = PendingIntent.getService(this, (int) alarmSet.id.longValue(), i, PendingIntent.FLAG_UPDATE_CURRENT);
		am.setRepeating(AlarmManager.RTC_WAKEUP, dt.getMillis(), AlarmManager.INTERVAL_DAY, pi);
	}
}
