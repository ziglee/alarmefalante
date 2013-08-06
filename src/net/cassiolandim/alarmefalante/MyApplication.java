package net.cassiolandim.alarmefalante;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class MyApplication extends Application {

	public static SharedPreferences sp;
	 
    @Override
    public void onCreate() {
        super.onCreate();
        sp = PreferenceManager.getDefaultSharedPreferences(this);
    }
    
    public boolean getEnabled() {
    	return sp.getBoolean("enabled", false);
    }
    
    public void setEnabled(boolean enabled) {
    	sp.edit().putBoolean("enabled", enabled).commit();
    }
    
    public int getVolume() {
    	return sp.getInt("volume", 100);
    }
    
    public void setVolume(int volume) {
    	sp.edit().putInt("volume", volume).commit();
    }
    
    public int getHour() {
    	return sp.getInt("hour", 7);
    }
    
    public void setHour(int hour) {
    	sp.edit().putInt("hour", hour).commit();
    }
    
    public int getMinute() {
    	return sp.getInt("minute", 0);
    }
    
    public void setMinute(int minute) {
    	sp.edit().putInt("minute", minute).commit();
    }

	public boolean getVibrationEnabled() {
		return sp.getBoolean("vibration", false);
	}
	
	public void setVibrationEnabled(boolean enabled) {
    	sp.edit().putBoolean("vibration", enabled).commit();
	}
}
