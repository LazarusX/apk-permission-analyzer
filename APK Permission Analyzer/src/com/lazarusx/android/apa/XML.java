package com.lazarusx.android.apa;

import org.w3c.dom.*;
import org.xml.sax.SAXException;
import javax.xml.parsers.*;
import java.io.*;

public class XML {
	private Document doc = null;
	
	private void init(String xmlFile) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		doc = db.parse(new File(xmlFile));
	}
	
	public void readXML(String xmlFile) throws ParserConfigurationException, SAXException, IOException {
		this.init(xmlFile);
		
		NodeList nodeList = doc.getElementsByTagName("uses-permission");
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			System.out.println(node.getAttributes().item(0).getNodeValue());
		}
	}
}
