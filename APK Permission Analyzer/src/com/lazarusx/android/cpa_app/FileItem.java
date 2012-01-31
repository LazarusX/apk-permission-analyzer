package com.lazarusx.android.cpa_app;

import java.io.File;

import android.graphics.drawable.Drawable;

public class FileItem implements Comparable<FileItem>{
	private File file;
	private String displayName;
	private Drawable icon;
		
	public File getFile() {
		return file;
	}
	
	public String getFileName() {
		return file.getName();
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public void setDisplayName(String _displayName) {
		displayName = _displayName;
	}
	
	public Drawable getDrawable() {
		return icon;
	}
	
	public void setIcon(Drawable _icon) {
		icon = _icon;
	}
	
	public FileItem(File _file) {
		file = _file;
		displayName = _file.getName();
	}

	@Override
	public String toString() {
		return displayName;
	}

	public int compareTo(FileItem another) {
		return displayName.compareToIgnoreCase(another.getDisplayName());
	}
}
