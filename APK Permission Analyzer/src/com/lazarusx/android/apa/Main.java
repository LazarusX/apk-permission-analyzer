package com.lazarusx.android.apa;

import java.io.*;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import test.AXMLPrinter;

public class Main {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
	public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
		APK apk = new APK("Cut the Rope.apk");
		apk.extract();
		String arg[] = {"raw.xml"};
		PrintStream ps = new PrintStream(new FileOutputStream("AndroidManifest.xml"));
		PrintStream systemOut = System.out;
		System.setOut(ps);
		AXMLPrinter.main(arg);
		System.setOut(systemOut);
		XML manifestXML = new XML();
		manifestXML.readXML("AndroidManifest.xml");
	}
}
