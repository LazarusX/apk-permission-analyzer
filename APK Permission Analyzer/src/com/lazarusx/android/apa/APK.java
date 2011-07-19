package com.lazarusx.android.apa;

import java.util.zip.*;
import java.io.*;

public class APK {
	String filename;
	public APK(String filename) {
		this.filename = filename;
	}
	
	public void extract () throws IOException {
		ZipInputStream in = new ZipInputStream(new FileInputStream(filename));
		ZipEntry z;
		String xmlName = "AndroidManifest.xml";
		while ((z = in.getNextEntry()) != null) {
			if (z.getName().equalsIgnoreCase(xmlName)) {
				File file = new File("raw.xml");
				file.createNewFile();
				FileOutputStream out = new FileOutputStream(file);
				int b;
				while ((b = in.read()) != -1) {
					out.write(b);
				}
				out.close();
				break;
			}
		}
		in.close();
	}
}
