package net.cassiolandim.alarmefalante;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.PowerManager;

@SuppressLint("SimpleDateFormat")
public class TimeTalkerReceiver extends BroadcastReceiver {
 
	private Context context;
	private MediaPlayer mp;
	private Queue<Integer> queue;
	private OnCompletionListener mpOnCompletionListener;
	private static final DateFormat HOURS_DATE_FORMATTER = new SimpleDateFormat("HH");
	private static final DateFormat MINUTES_DATE_FORMATTER = new SimpleDateFormat("mm");
	
    @Override
    public void onReceive(final Context context, Intent intent) {
    	this.context = context;
    	this.queue = new LinkedList<Integer>();
    	this.mpOnCompletionListener = new OnCompletionListener() {
    		@Override
    		public void onCompletion(MediaPlayer mp) {
    			if (!queue.isEmpty())
    				playNextAudio();
    		}
    	};
    	
    	Date now = new Date();
		String hours = HOURS_DATE_FORMATTER.format(now);
		String minutes = MINUTES_DATE_FORMATTER.format(now);
		
    	String raw = "horas_" + hours;
		if (hours.equalsIgnoreCase("00"))
			raw = "meia_noite";
		if (hours.equalsIgnoreCase("12"))
			raw = "meio_dia";
		queue.add(context.getResources().getIdentifier(raw, "raw", context.getPackageName()));
		
		if (!minutes.equalsIgnoreCase("00"))
			queue.add(context.getResources().getIdentifier("minutos_" + minutes, "raw", context.getPackageName()));
		
		playNextAudio();
    }
    
	private void playNextAudio() {
		if (mp != null)
			mp.release();
		mp = MediaPlayer.create(context, queue.remove());
		//mp.setAudioStreamType(AudioManager.STREAM_ALARM);
		mp.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK);
		mp.setOnCompletionListener(mpOnCompletionListener);
		mp.start();
	}
}
