package net.cassiolandim.alarmefalante;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

public final class MyDatabase {

	private static final String LOGTAG = "MyDatabase";

	private static final String ALARMSET_TABLE = "alarmset";
	private static final String ALARMSET_ID = "_id";
	private static final String ALARMSET_NAME = "name";
	private static final String ALARMSET_VOLUME = "volume";
	private static final String ALARMSET_VIBRATION = "vibration";
	private static final String ALARMSET_ENABLED = "enabled";
	private static final String ALARMSET_HOUR = "hour";
	private static final String ALARMSET_MINUTE = "minute";
	private static final String ALARMSET_WEEKDAYS = "weekdays";
	private static final String ALARMSET_SNOOZETIME = "snoozetime";

	private static final String[] ALARMSET_COLUMNS_FOR_LISTING = new String[] { ALARMSET_ID, ALARMSET_NAME, ALARMSET_ENABLED, ALARMSET_HOUR, ALARMSET_MINUTE,
			ALARMSET_WEEKDAYS };
	private static final String[] ALARMSET_ALL_COLUMNS = new String[] { ALARMSET_ID, ALARMSET_NAME, ALARMSET_ENABLED, ALARMSET_HOUR, ALARMSET_MINUTE,
		ALARMSET_WEEKDAYS, ALARMSET_VOLUME, ALARMSET_VIBRATION, ALARMSET_SNOOZETIME };

	private SQLiteDatabase mDatabase;

	public MyDatabase(Context context) {
		this.mDatabase = DbOpenHelper.getInstance(context).getWritableDatabase();
	}

	public final void insert(final AlarmSet alarmSet) {
		ContentValues values = new ContentValues();
		values.put(ALARMSET_NAME, alarmSet.name);
		values.put(ALARMSET_VOLUME, alarmSet.volume);
		values.put(ALARMSET_VIBRATION, alarmSet.vibration);
		values.put(ALARMSET_ENABLED, alarmSet.enabled);
		values.put(ALARMSET_HOUR, alarmSet.hour);
		values.put(ALARMSET_MINUTE, alarmSet.minute);
		values.put(ALARMSET_WEEKDAYS, alarmSet.weekdays);
		values.put(ALARMSET_SNOOZETIME, alarmSet.snoozetime);
		Long id = mDatabase.insert(ALARMSET_TABLE, null, values);
		alarmSet.id = id;
	}
	
	public final void update(AlarmSet alarmSet) {
		String whereClause = ALARMSET_ID + " = ?";
		String[] whereClauseArgs = new String[] { String.valueOf(alarmSet.id) };

		ContentValues values = new ContentValues();
		values.put(ALARMSET_NAME, alarmSet.name);
		values.put(ALARMSET_VOLUME, alarmSet.volume);
		values.put(ALARMSET_VIBRATION, alarmSet.vibration);
		values.put(ALARMSET_ENABLED, alarmSet.enabled);
		values.put(ALARMSET_HOUR, alarmSet.hour);
		values.put(ALARMSET_MINUTE, alarmSet.minute);
		values.put(ALARMSET_WEEKDAYS, alarmSet.weekdays);
		values.put(ALARMSET_SNOOZETIME, alarmSet.snoozetime);

		mDatabase.update(ALARMSET_TABLE, values, whereClause, whereClauseArgs);
	}

	public final void updateEnabled(Long id, boolean enabled) {
		String whereClause = ALARMSET_ID + " = ?";
		String[] whereClauseArgs = new String[] { String.valueOf(id) };

		ContentValues initialValues = new ContentValues();
		initialValues.put(ALARMSET_ENABLED, enabled);

		mDatabase.update(ALARMSET_TABLE, initialValues, whereClause, whereClauseArgs);
	}
	
	public final void remove(Long id) {
		String whereClause = ALARMSET_ID + " = ?";
		String[] whereClauseArgs = new String[] { String.valueOf(id) };
		mDatabase.delete(ALARMSET_TABLE, whereClause, whereClauseArgs);
	}

	public final static AlarmSet populateModel(Cursor cursor) {
		AlarmSet tt = new AlarmSet();

		int index = -1;

		index = cursor.getColumnIndex(ALARMSET_ID);
		if (index >= 0)
			tt.id = cursor.getLong(index);

		index = cursor.getColumnIndex(ALARMSET_NAME);
		if (index >= 0)
			tt.name = cursor.getString(index);

		index = cursor.getColumnIndex(ALARMSET_WEEKDAYS);
		if (index >= 0)
			tt.weekdays = cursor.getString(index);

		index = cursor.getColumnIndex(ALARMSET_SNOOZETIME);
		if (index >= 0)
			tt.snoozetime = cursor.getInt(index);

		index = cursor.getColumnIndex(ALARMSET_VOLUME);
		if (index >= 0)
			tt.volume = cursor.getInt(index);

		index = cursor.getColumnIndex(ALARMSET_HOUR);
		if (index >= 0)
			tt.hour = cursor.getInt(index);

		index = cursor.getColumnIndex(ALARMSET_MINUTE);
		if (index >= 0)
			tt.minute = cursor.getInt(index);

		index = cursor.getColumnIndex(ALARMSET_VIBRATION);
		if (index >= 0)
			tt.vibration = cursor.getInt(index) == 1 ? true : false;

		index = cursor.getColumnIndex(ALARMSET_ENABLED);
		if (index >= 0)
			tt.enabled = cursor.getInt(index) == 1 ? true : false;

		return tt;
	}

	public final AlarmSet find(Long id) {
		if (id == -1)
			return null;
		
		String selection = ALARMSET_ID + " = " + id;
		Cursor mCursor = mDatabase.query(ALARMSET_TABLE, ALARMSET_ALL_COLUMNS, selection, null, null, null, null);

		AlarmSet alarmSet = null;
		if (mCursor != null) {
			mCursor.moveToFirst();
			alarmSet = populateModel(mCursor);
		}
		mCursor.close();

		return alarmSet;
	}

	public final Cursor query() {
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		builder.setTables(ALARMSET_TABLE);

		Cursor cursor = builder.query(mDatabase, ALARMSET_COLUMNS_FOR_LISTING, null, null, null, null, null);
		if (cursor == null) {
			return null;
		} else if (!cursor.moveToFirst()) {
			cursor.close();
			return null;
		}

		return cursor;
	}

	private final static class DbOpenHelper extends SQLiteOpenHelper {

		private static final String DATABASE_NAME = "AlarmeFalanteDatabase";
		private static final int DATABASE_VERSION = 1;

		private static DbOpenHelper instance;

		private DbOpenHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		private static synchronized DbOpenHelper getInstance(Context context) {
			if (instance == null)
				instance = new DbOpenHelper(context.getApplicationContext());
			return instance;
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			StringBuilder sb = new StringBuilder();
			sb.append("CREATE TABLE " + ALARMSET_TABLE + " (");
			sb.append(ALARMSET_ID + " INTEGER PRIMARY KEY ASC, ");
			sb.append(ALARMSET_NAME + " TEXT, ");
			sb.append(ALARMSET_VOLUME + " INTEGER, ");
			sb.append(ALARMSET_VIBRATION + " INTEGER, ");
			sb.append(ALARMSET_ENABLED + " INTEGER, ");
			sb.append(ALARMSET_HOUR + " INTEGER, ");
			sb.append(ALARMSET_MINUTE + " INTEGER, ");
			sb.append(ALARMSET_WEEKDAYS + " TEXT, ");
			sb.append(ALARMSET_SNOOZETIME + " INTEGER);");
			db.execSQL(sb.toString());

		}

		@Override
		public void onOpen(SQLiteDatabase db) {
			super.onOpen(db);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.d(LOGTAG, String.format("Updating database from version %d to version %d", oldVersion, newVersion));
			if (newVersion > oldVersion) {
				for (int i = oldVersion; i < newVersion; ++i) {
					int nextVersion = i + 1;
					switch (nextVersion) {
					case 3:
						// upgradeToVersion3(db);
						break;
					case 4:
						// upgradeToVersion4(db);
						break;
					// etc...for further versions
					}
				}
			}
		}
	}
}
