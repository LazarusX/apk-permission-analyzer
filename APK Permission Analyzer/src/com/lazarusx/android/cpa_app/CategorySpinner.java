package com.lazarusx.android.cpa_app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class CategorySpinner extends Activity implements OnClickListener {
	private String temp;
	Spinner spinner;
	private ArrayAdapter<String> adapter;
	private final String[] spinstr = { "GCasual", "GSports Games", "GLive Wallpaper", "GRacing", "GArcade & Action", "GBrain & Puzzle", "GWidgets", "GCards & Casino", "Libraries & Demo",
			"Personalization", "Transportation", "Sports", "Health & Fitness", "Business", "Wallpaper", "Comics", "Medical", "Books & Reference", "Weather", "Entertainment", "Media & Video", "Tools",
			"Photography", "Productivity", "Education", "News & Magazines", "Travel & Local", "Lifestyle", "Social", "Widgets", "Finance", "Shopping", "Communication", "Music & Audio" };
	private String ps;
	private String pw;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.categoryspinner);

		Bundle bundle = this.getIntent().getExtras();
		temp = bundle.getString("SelectedFilePath");
		ps = bundle.getString("PS");
		pw = bundle.getString("PW");

		spinner = (Spinner) findViewById(R.id.spinner);
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
		for (int i = 0; i < spinstr.length; i++) {
			adapter.add(spinstr[i]);
		}
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setPrompt("select");
		spinner.setSelection(0);

		View OkButton = this.findViewById(R.id.btn1);
		OkButton.setOnClickListener(this);

	}

	public void onClick(View v) {
		if (v.getId() == R.id.btn1) {
			Intent intent = new Intent(CategorySpinner.this, PolicyValidationActivity.class);
			intent.putExtra("SelectedFilePath", temp);
			intent.putExtra("Category", spinstr[spinner.getSelectedItemPosition()]);
			intent.putExtra("PS", ps);
			intent.putExtra("PW", pw);
			startActivity(intent);
		}
	}
}
