package com.lazarusx.android.cpa_app;

public class PermissionItem {
	String infoHeader;
	String infoDescription;
	int percentage;
	
	public PermissionItem(String _infoHeader, String _infoDescription, int _percentage) {
		infoHeader = _infoHeader;
		infoDescription = _infoDescription;
		percentage = _percentage;
	}

	public String getInfoHeader() {
		return infoHeader;
	}

	public void setInfoHeader(String infoHeader) {
		this.infoHeader = infoHeader;
	}

	public String getInfoDescription() {
		return infoDescription;
	}

	public void setInfoDescription(String infoDescription) {
		this.infoDescription = infoDescription;
	}
	
	public int getPercentage() {
		return percentage;
	}
	
	public void setPercentage(int percentage) {
		this.percentage = percentage;
	}
}
