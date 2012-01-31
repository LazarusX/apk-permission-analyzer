package com.lazarusx.android.cpa_app;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PermissionAdapter extends ArrayAdapter<PermissionItem> {
	int resource;
	private int ps;
	private int pw;

	public PermissionAdapter(Context context, int textViewResourceId, ArrayList<PermissionItem> objects, int s, int w) {
		super(context, textViewResourceId, objects);
		resource = textViewResourceId;
		ps = s;
		pw = w;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout infoView;

		PermissionItem item = getItem(position);
		String headerString = item.getInfoHeader();
		String descriptionString = item.getInfoDescription();
		int percentage = item.getPercentage();

		if (convertView == null) {
			infoView = new LinearLayout(getContext());
			String inflater = Context.LAYOUT_INFLATER_SERVICE;
			LayoutInflater vi = (LayoutInflater) getContext().getSystemService(inflater);
			vi.inflate(resource, infoView, true);
		} else {
			infoView = (LinearLayout) convertView;
		}

		TextView headerView = (TextView) infoView.findViewById(R.id.permission_header_view);
		TextView descriptionView = (TextView) infoView.findViewById(R.id.permission_descrption_view);
		TextView percentageView = (TextView) infoView.findViewById(R.id.permission_percent_view);

		headerView.setText(headerString);
		descriptionView.setText(descriptionString);
		percentageView.setText(percentage + "%");

		/* Need to change to the value from user preferences */
		if (percentage > ps)
			percentageView.setBackgroundColor(infoView.getResources().getColor(R.color.green));
		else if (percentage > pw)
			percentageView.setBackgroundColor(infoView.getResources().getColor(R.color.yellow));
		else
			percentageView.setBackgroundColor(infoView.getResources().getColor(R.color.red));
		return infoView;
	}
}
