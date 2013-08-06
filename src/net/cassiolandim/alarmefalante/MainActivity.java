package net.cassiolandim.alarmefalante;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TimePicker;
import android.widget.ToggleButton;

public class MainActivity extends Activity {

	private MyApplication app;
	private TimePicker timePicker;
	private ToggleButton toggleButton;
	private CheckBox vibrationCheck;
	private SeekBar volumeSeeker;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		this.app = (MyApplication) getApplication();
		this.timePicker = (TimePicker) findViewById(R.id.time_picker);
		this.toggleButton = (ToggleButton) findViewById(R.id.toggle_button);
		this.volumeSeeker = (SeekBar) findViewById(R.id.volume_seek);
		this.vibrationCheck = (CheckBox) findViewById(R.id.vibration_check);

		volumeSeeker.setMax(100);
		volumeSeeker.setProgress(app.getVolume());
		timePicker.setIs24HourView(true);
		timePicker.setCurrentHour(app.getHour());
		timePicker.setCurrentMinute(app.getMinute());
		toggleButton.setChecked(app.getEnabled());
		vibrationCheck.setChecked(app.getVibrationEnabled());

		vibrationCheck.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				app.setVibrationEnabled(isChecked);
			}
		});
		
		toggleButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				saveSettings();
			}
		});

		volumeSeeker.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				app.setVolume(seekBar.getProgress());
				startService(new Intent(MainActivity.this, TimeTalkerIntentService.class));
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// getMenuInflater().inflate(R.menu.main, menu);
		return false;
	}

	@Override
	protected void onStop() {
		super.onStop();
		saveSettings();
	}

	private void saveSettings() {
		app.setHour(timePicker.getCurrentHour());
		app.setMinute(timePicker.getCurrentMinute());
		app.setEnabled(toggleButton.isChecked());
		app.setVibrationEnabled(vibrationCheck.isChecked());

		Intent service = new Intent(MainActivity.this, AlarmSetterService.class);
		service.setAction(toggleButton.isChecked() ? AlarmSetterService.CREATE : AlarmSetterService.CANCEL);
		startService(service);
	}
}
