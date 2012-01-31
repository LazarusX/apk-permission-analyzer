package com.lazarusx.android.cpa_app;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class CPAActivity extends Activity {

	private ListView fileListView;
	private TextView currentDirView;
	private FileItemAdapter aa;
	private ArrayList<FileItem> files = new ArrayList<FileItem>();
	private FileItem currentDir = new FileItem(Environment.getExternalStorageDirectory());
	private FileItem selectedItem;
	private Stack<FileItem> viewHistory;

	static final private APKFilter APK_FILTER = new APKFilter();;
	static final private String EXTERNAL_STORAGE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();

	static final private int PREFERENCES = Menu.FIRST;
	static final private int ABOUT = Menu.FIRST + 1;

	private String ps;
	private String pw;
	private boolean dat = false;
	SharedPreferences settings;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		settings = PreferenceManager.getDefaultSharedPreferences(this);
		ps = settings.getString("PS", "80");
		pw = settings.getString("PW", "50");
		dat = settings.getBoolean("DAT", false);

		currentDirView = (TextView) this.findViewById(R.id.currentDir);
		fileListView = (ListView) this.findViewById(R.id.fileListView);
		fileListView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int index, long arg3) {
				selectedItem = files.get(index);
				if (selectedItem.getFile().isDirectory()) {
					browseTo(selectedItem);
				} else {
					Intent intent = new Intent(CPAActivity.this, CategorySpinner.class);
					intent.putExtra("SelectedFilePath", selectedItem.getFile().getAbsolutePath());
					intent.putExtra("PS", ps);
					intent.putExtra("PW", pw);
					startActivity(intent);
				}
			}
		});

		int layoutID = R.layout.file_item;
		aa = new FileItemAdapter(this, layoutID, files);
		fileListView.setAdapter(aa);

		viewHistory = new Stack<FileItem>();

		browseTo(currentDir);
	}

	@Override
	public void onResume() {
		super.onResume();
		settings = PreferenceManager.getDefaultSharedPreferences(this);
		ps = settings.getString("PS", "80");
		pw = settings.getString("PW", "50");
		dat = settings.getBoolean("DAT", false);
	}

	@Override
	public void onPause() {
		super.onPause();
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("PS", ps);
		editor.putString("PW", pw);
		editor.putBoolean("DAT", dat);
		editor.commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		MenuItem preferencesItem = menu.add(0, PREFERENCES, Menu.NONE, R.string.preferences_menu);
		MenuItem aboutItem = menu.add(0, ABOUT, Menu.NONE, R.string.about_menu);

		preferencesItem.setIcon(android.R.drawable.ic_menu_preferences);
		aboutItem.setIcon(android.R.drawable.ic_menu_info_details);

		preferencesItem.setShortcut('0', 'p');
		aboutItem.setShortcut('1', 'a');
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		switch (item.getItemId()) {
		case (PREFERENCES): {
			Intent intent = new Intent(this, Preferences.class);
			startActivity(intent);
			return true;
		}
		case (ABOUT): {
			Intent intent = new Intent(this, AboutActivity.class);
			startActivity(intent);
			return true;
		}
		}
		return false;
	}

	@Override
	public void onBackPressed() {
		if (viewHistory.size() > 1) {
			viewHistory.pop();
			browseTo(viewHistory.pop());
		} else
			super.onBackPressed();
	}

	private void browseTo(final FileItem newDir) {
		if (newDir.getFile().isDirectory()) {
			currentDir = newDir;
			currentDirView.setText(currentDir.getFile().getAbsolutePath());
			if (newDir.getDisplayName().equals(getString(R.string.parentDir)))
				viewHistory.pop();
			else
				viewHistory.push(newDir);
			fill(newDir.getFile().listFiles(APK_FILTER));
		}
	}

	private void fill(File[] newFiles) {
		files.clear();
		if (!currentDir.getFile().getAbsolutePath().equals(EXTERNAL_STORAGE_PATH)) {
			files.add(new FileItem(currentDir.getFile().getParentFile()));
			files.get(0).setDisplayName(getString(R.string.parentDir));
		}

		for (File newFile : newFiles) {
			files.add(new FileItem(newFile));
		}

		Collections.sort(files);

		aa.notifyDataSetChanged();
	}

}