package com.lazarusx.android.cpa_app;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class InfoAdapter extends ArrayAdapter<InfoItem> {
	int resource;
	
	public InfoAdapter(Context context, int textViewResourceId,
			ArrayList<InfoItem> objects) {
		super(context, textViewResourceId, objects);
		resource = textViewResourceId;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout infoView;
		
		InfoItem item = getItem(position);
		String headerString = item.getInfoHeader();
		String descriptionString = item.getInfoDescription();
		
		if (convertView == null) {
			infoView = new LinearLayout(getContext());
			String inflater = Context.LAYOUT_INFLATER_SERVICE;
			LayoutInflater vi = (LayoutInflater)getContext().getSystemService(inflater);
			vi.inflate(resource, infoView, true);
		} else {
			infoView = (LinearLayout)convertView;
		}
		
		TextView headerView = (TextView)infoView.findViewById(R.id.info_header_view);
		TextView descriptionView = (TextView)infoView.findViewById(R.id.info_descrption_view);
		
		headerView.setText(headerString);
		descriptionView.setText(descriptionString);
		return infoView;
	}
}
