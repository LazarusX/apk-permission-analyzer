package com.lazarusx.android.cpa_app;

public class InfoItem {
	String infoHeader;
	String infoDescription;
	
	public InfoItem(String _infoHeader, String _infoDescription) {
		infoHeader = _infoHeader;
		infoDescription = _infoDescription;
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
}
