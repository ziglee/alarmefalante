package net.cassiolandim.alarmefalante;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

public class MainListActivity extends ActionBarActivity {

	private MyDatabase db;
	private AlarmSetCursorAdapter adapter;
	private ListView listView;
	private ActionMode mActionMode;
	private Long idSelected;
	private View viewSelected;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_activity_main);

		MyApplication app = (MyApplication) getApplication();
		this.db = app.getDb();
		this.listView = (ListView) findViewById(android.R.id.list);

		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
				if (mActionMode != null)
					return;

				Intent intent = new Intent(MainListActivity.this, DetailsActivity.class);
				intent.putExtra("id", id);
				startActivityForResult(intent, 0);
			}
		});
		
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
				if (mActionMode != null) {
					return false;
				}
				mActionMode = startSupportActionMode(mActionModeCallback);
				viewSelected = view;
				view.setBackgroundColor(getResources().getColor(R.color.row_selected));
				idSelected = id;
				return true;
			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		adapter = new AlarmSetCursorAdapter(this, db.query(), db);
		listView.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menu_add) {
			startActivity(new Intent(MainListActivity.this, DetailsActivity.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			switch (item.getItemId()) {
			case R.id.menu_remove:
				db.remove(idSelected);
				mode.finish();
				viewSelected = null;
				idSelected = null;
				adapter = new AlarmSetCursorAdapter(MainListActivity.this, db.query(), db);
				listView.setAdapter(adapter);
				return true;
			default:
				return false;
			}
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			MenuInflater inflater = mode.getMenuInflater();
			inflater.inflate(R.menu.context_menu, menu);
			return true;
		}

		@Override
		public void onDestroyActionMode(ActionMode arg0) {
			mActionMode = null;
			if (viewSelected != null)
				viewSelected.setBackgroundColor(getResources().getColor(android.R.color.transparent));
		}

		@Override
		public boolean onPrepareActionMode(ActionMode arg0, Menu arg1) {
			return false;
		}
	};
}
