package com.lazarusx.android.cpa_app;

import java.io.File;
import java.io.FileFilter;

public class APKFilter implements FileFilter {

	public boolean accept(File file) {
		if ((file.isFile() && !file.getName().toLowerCase().endsWith(".apk")) || !file.canRead())
			return false;
		return true;
	}

}
