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
        
        if (prefs.getBoolean("first_time", true)) {
        	AlarmSet as = new AlarmSet();
        	as.enabled = false;
        	as.vibration = true;
        	as.hour = 7;
        	as.minute = 30;
        	as.snoozetime = 2;
        	as.name = "Teste";
        	as.volume = 5;
        	db.insert(as);
        	
        	as = new AlarmSet();
        	as.enabled = false;
        	as.vibration = false;
        	as.hour = 18;
        	as.minute = 50;
        	as.snoozetime = 10;
        	as.name = "Estacionamento";
        	as.volume = 10;
        	db.insert(as);
        	
        	prefs.edit().putBoolean("first_time", false).commit();
        }
        
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
