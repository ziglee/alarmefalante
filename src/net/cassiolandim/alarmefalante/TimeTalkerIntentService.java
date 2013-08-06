package net.cassiolandim.alarmefalante;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.Vibrator;

@SuppressLint("SimpleDateFormat")
public class TimeTalkerIntentService extends Service {

	private static final DateFormat HOURS_DATE_FORMATTER = new SimpleDateFormat("HH");
	private static final DateFormat MINUTES_DATE_FORMATTER = new SimpleDateFormat("mm");
	private static final long[] VIBRATION = { 0, 400, 400, 400, 400, 400, 400, 400 };

	private MyApplication app;
	private AudioManager audioManager;
	private MediaPlayer mp;
	private Vibrator vibrator;
	private Queue<Integer> queue;
	private OnCompletionListener mpOnCompletionListener;
	private int oldVolume = 0;
	private int maxVolume = 0;

	@Override
	public void onCreate() {
		super.onCreate();
		this.app = (MyApplication) getApplication();
		this.audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
		this.vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
		this.maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		this.mpOnCompletionListener = new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				if (mp != null) {
					mp.release();
					mp = null;
				}

				audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, oldVolume, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
				if (!queue.isEmpty())
					playNextAudio();
				else
					stopSelf();
			}
		};
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (mp != null && mp.isPlaying())
			mp.stop();

		this.queue = new LinkedList<Integer>();

		Date now = new Date();
		String hours = HOURS_DATE_FORMATTER.format(now);
		String minutes = MINUTES_DATE_FORMATTER.format(now);

		String raw = "horas_" + hours;
		if (hours.equalsIgnoreCase("00"))
			raw = "meia_noite";
		if (hours.equalsIgnoreCase("12"))
			raw = "meio_dia";
		queue.add(getResources().getIdentifier(raw, "raw", getPackageName()));

		if (!minutes.equalsIgnoreCase("00"))
			queue.add(getResources().getIdentifier("minutos_" + minutes, "raw", getPackageName()));

		if (app.getVibrationEnabled())
			vibrator.vibrate(VIBRATION, -1);

		playNextAudio();

		return START_STICKY;
	}

	private void playNextAudio() {
		float volume = (float) app.getVolume() / 100;

		oldVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int) (volume * maxVolume), AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);

		mp = MediaPlayer.create(this, queue.remove());
		mp.setVolume(volume, volume);
		mp.setWakeMode(this, PowerManager.PARTIAL_WAKE_LOCK);
		mp.setOnCompletionListener(mpOnCompletionListener);
		mp.start();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
