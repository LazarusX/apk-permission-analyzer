package com.lazarusx.android.cpa_app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;

public class Preferences extends PreferenceActivity implements OnPreferenceChangeListener {
	public static final String SAFE_PER = "SAFE_PER";
	public static final String WARNING_PER = "WARNING_PER";

	private String ps;
	private String pw;

	private ListPreference lp;
	private ListPreference lp2;
	SharedPreferences settings;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.userpreferences);

		lp = (ListPreference) findPreference("PS");
		lp.setOnPreferenceChangeListener(this);
		lp2 = (ListPreference) findPreference("PW");
		lp2.setOnPreferenceChangeListener(this);
		settings = PreferenceManager.getDefaultSharedPreferences(this);
		ps = settings.getString("PS", "80");
		pw = settings.getString("PW", "50");
		lp.setDefaultValue(ps);
		lp2.setDefaultValue(pw);
	}

	public boolean onPreferenceChange(Preference preference, Object newValue) {
		return true;
	}

	public void onDestroy() {
		super.onDestroy();
		lp.setOnPreferenceChangeListener(null);
		lp = null;
		lp2.setOnPreferenceChangeListener(null);
		lp2 = null;
	}

	public void onRestart() {
		super.onRestart();
		lp.setEnabled(true);
		lp2.setEnabled(true);
	}

	public void onStop() {
		super.onStop();
		lp.setEnabled(false);
		lp2.setEnabled(false);
	}
}
