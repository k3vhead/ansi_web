package com.ansi.scilla.web.test.bcr;

import java.io.File;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ODSImport {

//	private final String filePath = "/home/dclewis/Documents/webthing_v2/projects/ANSI/data/20210711_bcr_spreadsheet_examples/indy/work/content.xml";
	private final String filePath = "test/com/ansi/scilla/web/test/bcr/content.xml";
	
	public void go() throws Exception {
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		File file = new File(filePath);
		Document doc = builder.parse(file);
		doc.getDocumentElement().normalize();  // fix up whitespace weirdness

		Element documentElement = doc.getDocumentElement();
		//		NodeList nodeList = documentElement.getChildNodes();

		NodeList tabList = documentElement.getElementsByTagName("table:table");
		for ( int i = 0; i < tabList.getLength(); i++ ) {
			Node node = tabList.item(i);
			NamedNodeMap attributes = node.getAttributes();
			Node tableName = attributes.getNamedItem("table:name");
			String tabName = tableName.getNodeValue();
			System.out.println( tabName ); //+ ":" + node.getTextContent() );
		}
	}
	
	
	public static void main(String[] args) {
		System.out.println(new Date());
		try {
			new ODSImport().go();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("end");
	}

}
