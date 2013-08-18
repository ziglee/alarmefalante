package net.cassiolandim.alarmefalante;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;

public class MainListActivity extends ListActivity {

	private MyDatabase db;
	private AlarmSetCursorAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_activity_main);
		MyApplication app = (MyApplication) getApplication();
		this.db = app.getDb();
		this.adapter = new AlarmSetCursorAdapter(this, db.query(), db);
		setListAdapter(adapter);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			adapter.changeCursor(db.query());
			adapter.notifyDataSetChanged();
		}
	}
}
