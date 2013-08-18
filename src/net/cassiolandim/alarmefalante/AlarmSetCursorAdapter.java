package net.cassiolandim.alarmefalante;

import java.text.NumberFormat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class AlarmSetCursorAdapter extends CursorAdapter {

	private LayoutInflater lf;
	private NumberFormat nf;
	private MyDatabase db;

	public AlarmSetCursorAdapter(Context context, Cursor cursor, MyDatabase db) {
		super(context, cursor, FLAG_REGISTER_CONTENT_OBSERVER);
		this.lf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.nf = NumberFormat.getInstance();
		nf.setMinimumIntegerDigits(2);
		this.db = db;
	}
	
	@Override
	public void bindView(View arg0, Context arg1, Cursor arg2) {
	}

	@Override
	public View newView(final Context context, Cursor cursor, ViewGroup viewGroup) {
		final AlarmSet alarmSet = MyDatabase.populateModel(cursor);
		View view = lf.inflate(R.layout.list_item, null);
		
		view.findViewById(R.id.info).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, DetailsActivity.class);
		        intent.putExtra("id", alarmSet.id);
				((Activity)context).startActivityForResult(intent, 0);
			}
		});
		
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
			}
		});
		
		TextView name = (TextView) view.findViewById(R.id.name);
		name.setText(alarmSet.name);
		
		TextView time = (TextView) view.findViewById(R.id.time);
		time.setText(nf.format(alarmSet.hour) + ":" + nf.format(alarmSet.minute));
		
		TextView weekdays = (TextView) view.findViewById(R.id.weekdays);
		weekdays.setText(alarmSet.weekdays);
		
		return view;
	}
}
