package net.cassiolandim.alarmefalante;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.PowerManager;

@SuppressLint("SimpleDateFormat")
public class TimeTalkerReceiver extends BroadcastReceiver {
 
	private MediaPlayer hoursMediaPlayer;
	private MediaPlayer minutesMediaPlayer;
	private OnCompletionListener hoursOnCompletionListener;
	private OnCompletionListener minutesOnCompletionListener;
	private String hours;
	private String minutes;
	private static final DateFormat HOURS_DATE_FORMATTER = new SimpleDateFormat("HH");
	private static final DateFormat MINUTES_DATE_FORMATTER = new SimpleDateFormat("mm");
	
    @Override
    public void onReceive(final Context context, Intent intent) {
    	hoursOnCompletionListener = new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				hoursMediaPlayer.release();
				if (!minutes.equalsIgnoreCase("00"))
					playMinutes(context);
			}
		};

		minutesOnCompletionListener = new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				minutesMediaPlayer.release();
			}
		};
		
    	Date now = new Date();
		hours = HOURS_DATE_FORMATTER.format(now);
		minutes = MINUTES_DATE_FORMATTER.format(now);
		playHours(context);
    }
    
    private void playMinutes(Context context) {
		final int minutesId = context.getResources().getIdentifier("minutos_" + minutes, "raw", context.getPackageName());
		minutesMediaPlayer = MediaPlayer.create(context, minutesId);
		minutesMediaPlayer.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK);
		minutesMediaPlayer.setOnCompletionListener(minutesOnCompletionListener);
		minutesMediaPlayer.start();
	}

	private void playHours(Context context) {
		String raw = "horas_" + hours;
		if (hours.equalsIgnoreCase("00"))
			raw = "meia_noite";
		if (hours.equalsIgnoreCase("12"))
			raw = "meio_dia";
		
		final int hoursId = context.getResources().getIdentifier(raw, "raw", context.getPackageName());
		hoursMediaPlayer = MediaPlayer.create(context, hoursId);
		hoursMediaPlayer.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK);
		hoursMediaPlayer.setOnCompletionListener(hoursOnCompletionListener);
		hoursMediaPlayer.start();
	}
}
