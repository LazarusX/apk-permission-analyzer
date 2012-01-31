package com.lazarusx.android.cpa_app;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class AboutActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.about);
		TextView aboutInfoView = (TextView)findViewById(R.id.about_info_textview);
		aboutInfoView.setMovementMethod(LinkMovementMethod.getInstance());
		TextView aboutIconCreditsView = (TextView)findViewById(R.id.about_icon_credits_textview);
		aboutIconCreditsView.setMovementMethod(LinkMovementMethod.getInstance());
	}

}
