package net.cassiolandim.alarmefalante;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TimePicker;
import android.widget.ToggleButton;

public class MainActivity extends Activity {

	private TimePicker timePicker;
	private ToggleButton toggleButton;

	private Button ringnowButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final MyApplication app = (MyApplication) getApplication();
		
		timePicker = (TimePicker) findViewById(R.id.time_picker);
		toggleButton = (ToggleButton) findViewById(R.id.toggle_button);
		ringnowButton = (Button) findViewById(R.id.ringnow_button);

		timePicker.setIs24HourView(true);
		timePicker.setCurrentHour(app.getHour());
		timePicker.setCurrentMinute(app.getMinute());

		toggleButton.setChecked(app.getEnabled());
		toggleButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				app.setHour(timePicker.getCurrentHour());
				app.setMinute(timePicker.getCurrentMinute());
				app.setEnabled(isChecked);
				
				Intent service = new Intent(MainActivity.this, AlarmSetterService.class);
				service.setAction(isChecked ? AlarmSetterService.CREATE : AlarmSetterService.CANCEL);
				startService(service);
			}
		});
		
		ringnowButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startService(new Intent(MainActivity.this, AlarmRingingService.class));
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
