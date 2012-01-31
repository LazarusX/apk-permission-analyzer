package com.lazarusx.android.cpa_app;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FileItemAdapter extends ArrayAdapter<FileItem> {

	int resource;
	
	public FileItemAdapter(Context context, int textViewResourceId,
			ArrayList<FileItem> objects) {
		super(context, textViewResourceId, objects);
		resource = textViewResourceId;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout fileView;
		
		FileItem item = getItem(position);
		String displayString = item.getDisplayName();
		
		if (convertView == null) {
			fileView = new LinearLayout(getContext());
			String inflater = Context.LAYOUT_INFLATER_SERVICE;
			LayoutInflater vi = (LayoutInflater)getContext().getSystemService(inflater);
			vi.inflate(resource, fileView, true);
		} else {
			fileView = (LinearLayout)convertView;
		}
		
		TextView nameView = (TextView)fileView.findViewById(R.id.displayName);
		ImageView iconView = (ImageView)fileView.findViewById(R.id.fileIcon);
		
		nameView.setText(displayString);
		if (item.getDisplayName().equals(fileView.getResources().getString(R.string.parentDir)))
			iconView.setImageDrawable(null);
		else if (item.getFile().isDirectory())
			iconView.setImageDrawable(fileView.getResources().getDrawable(R.drawable.folder));
		else 
			iconView.setImageDrawable(fileView.getResources().getDrawable(R.drawable.app));
		return fileView;
	}
}
