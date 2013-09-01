package net.cassiolandim.alarmefalante;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

import com.flurry.android.FlurryAgent;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class AlarmSetCursorAdapter extends CursorAdapter {

	private LayoutInflater lf;
	private NumberFormat nf;
	private MyDatabase db;

	public AlarmSetCursorAdapter(Context context, Cursor cursor, MyDatabase db) {
		super(context, cursor, true);
		this.lf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.nf = NumberFormat.getInstance();
		nf.setMinimumIntegerDigits(2);
		this.db = db;
	}
	
	@Override
	protected void onContentChanged() {
		changeCursor(db.query());
	}
	
	@Override
	public void bindView(View arg0, Context arg1, Cursor arg2) {
	}

	@Override
	public View newView(final Context context, Cursor cursor, ViewGroup viewGroup) {
		final AlarmSet alarmSet = MyDatabase.populateModel(cursor);
		View view = lf.inflate(R.layout.list_item, null);
		
		CheckBox enabled = (CheckBox) view.findViewById(R.id.checkbox);
		enabled.setChecked(alarmSet.enabled);
		enabled.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				db.updateEnabled(alarmSet.id, isChecked);
				
				Intent service = new Intent(context, AlarmSetterService.class);
				service.putExtra("id", alarmSet.id);
				service.setAction(isChecked ? AlarmSetterService.CREATE : AlarmSetterService.CANCEL);
				context.startService(service);
				
				Map<String, String> eventParams = new HashMap<String, String>();
				eventParams.put("Action", isChecked ? "Enable" : "Disable");
				FlurryAgent.logEvent("AlarmList", eventParams);
			}
		});
		
		TextView name = (TextView) view.findViewById(R.id.name);
		name.setText(alarmSet.name);
		
		TextView time = (TextView) view.findViewById(R.id.time);
		time.setText(nf.format(alarmSet.hour) + ":" + nf.format(alarmSet.minute));
		
		view.findViewById(R.id.domingo).setEnabled(alarmSet.weekdays.contains("1"));
		view.findViewById(R.id.segunda).setEnabled(alarmSet.weekdays.contains("2"));
		view.findViewById(R.id.terca).setEnabled(alarmSet.weekdays.contains("3"));
		view.findViewById(R.id.quarta).setEnabled(alarmSet.weekdays.contains("4"));
		view.findViewById(R.id.quinta).setEnabled(alarmSet.weekdays.contains("5"));
		view.findViewById(R.id.sexta).setEnabled(alarmSet.weekdays.contains("6"));
		view.findViewById(R.id.sabado).setEnabled(alarmSet.weekdays.contains("7"));
		
		return view;
	}
}
