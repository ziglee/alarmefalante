package net.cassiolandim.alarmefalante;

import java.util.HashMap;
import java.util.Map;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

public class DetailsActivity extends FragmentActivity {

	private MyDatabase db;
	private Button saveButton;
	private Button cancelButton;
	private TimePicker timePicker;
	private CheckBox vibrationCheck;
	private SeekBar volumeSeeker;
	private TextView snoozetime;
	private EditText name;
	private ToggleButton domingo;
	private ToggleButton segunda;
	private ToggleButton terca;
	private ToggleButton quarta;
	private ToggleButton quinta;
	private ToggleButton sexta;
	private ToggleButton sabado;
	private AlarmSet alarmSet;
	private SnoozeTimePickerDialogFragment snoozeTimePickerDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.details_activity_main);
		setResult(RESULT_CANCELED);

		MyApplication app = (MyApplication) getApplication();
		this.db = app.getDb();
		this.snoozeTimePickerDialog = new SnoozeTimePickerDialogFragment();
		this.saveButton = (Button) findViewById(R.id.save);
		this.cancelButton = (Button) findViewById(R.id.cancel);
		this.timePicker = (TimePicker) findViewById(R.id.time_picker);
		this.volumeSeeker = (SeekBar) findViewById(R.id.volume_seek);
		this.vibrationCheck = (CheckBox) findViewById(R.id.vibration_check);
		this.snoozetime = (TextView) findViewById(R.id.snoozetime);
		this.name = (EditText) findViewById(R.id.nameEdit);
		this.domingo = (ToggleButton) findViewById(R.id.domingo);
		this.segunda = (ToggleButton) findViewById(R.id.segunda);
		this.terca = (ToggleButton) findViewById(R.id.terca);
		this.quarta = (ToggleButton) findViewById(R.id.quarta);
		this.quinta = (ToggleButton) findViewById(R.id.quinta);
		this.sexta = (ToggleButton) findViewById(R.id.sexta);
		this.sabado = (ToggleButton) findViewById(R.id.sabado);

		findViewById(R.id.snoozetime_picker).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				snoozeTimePickerDialog.show(getSupportFragmentManager(), "Soneca");
			}
		});

		volumeSeeker.setMax(100);
		timePicker.setIs24HourView(true);

		this.alarmSet = db.find(getIntent().getLongExtra("id", -1));
		if (alarmSet == null)
			alarmSet = new AlarmSet();

		volumeSeeker.setProgress(alarmSet.volume);
		timePicker.setCurrentHour(alarmSet.hour);
		timePicker.setCurrentMinute(alarmSet.minute);
		vibrationCheck.setChecked(alarmSet.vibration);
		name.setText(alarmSet.name);
		domingo.setChecked(alarmSet.weekdays.contains("1"));
		segunda.setChecked(alarmSet.weekdays.contains("2"));
		terca.setChecked(alarmSet.weekdays.contains("3"));
		quarta.setChecked(alarmSet.weekdays.contains("4"));
		quinta.setChecked(alarmSet.weekdays.contains("5"));
		sexta.setChecked(alarmSet.weekdays.contains("6"));
		sabado.setChecked(alarmSet.weekdays.contains("7"));
		snoozetime.setText(alarmSet.snoozetime + " min");

		saveButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				saveSettings();
				setResult(RESULT_OK);
				finish();
			}
		});

		cancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setResult(RESULT_CANCELED);
				finish();
			}
		});

		vibrationCheck.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				alarmSet.vibration = isChecked;
			}
		});

		volumeSeeker.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				alarmSet.volume = seekBar.getProgress();
				Intent intent = new Intent(DetailsActivity.this, TimeTalkerIntentService.class);
				intent.putExtra(TimeTalkerIntentService.EXTRA_VOLUME, alarmSet.volume);
				intent.putExtra(TimeTalkerIntentService.EXTRA_VIBRATION, false);
				startService(intent);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			}
		});
	}

	private void saveSettings() {
		StringBuilder weekDays = new StringBuilder();
		if (domingo.isChecked())
			weekDays.append(",1");
		if (segunda.isChecked())
			weekDays.append(",2");
		if (terca.isChecked())
			weekDays.append(",3");
		if (quarta.isChecked())
			weekDays.append(",4");
		if (quinta.isChecked())
			weekDays.append(",5");
		if (sexta.isChecked())
			weekDays.append(",6");
		if (sabado.isChecked())
			weekDays.append(",7");
		weekDays.deleteCharAt(0);

		alarmSet.enabled = true;
		alarmSet.name = name.getText().toString();
		alarmSet.hour = timePicker.getCurrentHour();
		alarmSet.minute = timePicker.getCurrentMinute();
		alarmSet.vibration = vibrationCheck.isChecked();
		alarmSet.weekdays = weekDays.toString();

		if (alarmSet.id == null)
			db.insert(alarmSet);
		else
			db.update(alarmSet);

		Intent service = new Intent(DetailsActivity.this, AlarmSetterService.class);
		service.putExtra("id", alarmSet.id);
		service.setAction(AlarmSetterService.CREATE);
		startService(service);

		Toast.makeText(this, "O alarme est‡ programado.", Toast.LENGTH_SHORT).show();
	}

	private class SnoozeTimePickerDialogFragment extends DialogFragment {
		@SuppressLint("UseSparseArrays")
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			final CharSequence[] choiceList = { "5", "10", "15", "20" };
			final Map<Integer, Integer> mapPositions = new HashMap<Integer, Integer>();
			mapPositions.put(5, 0);
			mapPositions.put(10, 1);
			mapPositions.put(15, 2);
			mapPositions.put(20, 3);

			int selected = mapPositions.get(alarmSet.snoozetime);

			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle("Soneca");
			builder.setSingleChoiceItems(choiceList, selected, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					alarmSet.snoozetime = Integer.parseInt(choiceList[which].toString());
					snoozetime.setText(alarmSet.snoozetime + " min");
					dialog.dismiss();
				}
			});
			return builder.create();
		}
	}
}
