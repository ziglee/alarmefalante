package net.cassiolandim.alarmefalante;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class MyApplication extends Application {

	private SharedPreferences prefs;
	private MyDatabase db;
	 
    @Override
    public void onCreate() {
        super.onCreate();
        this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
        this.db = new MyDatabase(this);
        
        AlarmSet alarmSet = getLegacyAlarmSet();
		if (alarmSet != null)
			db.insert(alarmSet);
    }
    
    public AlarmSet getLegacyAlarmSet() {
    	if (!prefs.contains("hour"))
    		return null;
    	
    	AlarmSet alarmSet = new AlarmSet();
    	alarmSet.enabled = prefs.getBoolean("enabled", false);
    	alarmSet.volume = prefs.getInt("volume", 100);
    	alarmSet.hour = prefs.getInt("hour", 7);
    	alarmSet.minute = prefs.getInt("minute", 0);
    	alarmSet.vibration = prefs.getBoolean("vibration", false);
    	
    	Editor edit = prefs.edit();
    	edit.remove("hour");
    	edit.remove("minute");
    	edit.remove("enabled");
    	edit.remove("volume");
    	edit.remove("vibration");
    	edit.commit();
    	
    	return alarmSet;
    }
    
	public MyDatabase getDb() {
		return db;
	}
}
