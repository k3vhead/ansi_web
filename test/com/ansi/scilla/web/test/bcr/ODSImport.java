package com.ansi.scilla.web.test.bcr;

import java.io.File;
import java.util.Date;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang3.StringUtils;
//import org.w3c.dom.Document;
//import org.w3c.dom.Element;
//import org.w3c.dom.NamedNodeMap;
//import org.w3c.dom.Node;
//import org.w3c.dom.NodeList;

import com.ansi.scilla.common.ApplicationObject;

public class ODSImport {

//	private final String filePath = "/home/dclewis/Documents/webthing_v2/projects/ANSI/data/20210711_bcr_spreadsheet_examples/indy/work/content.xml";
	private final String filePath = "test/com/ansi/scilla/web/test/bcr/content.xml";
	
	private final NodeReference divisionRef = new NodeReference(3, 4);
	private final NodeReference managerRef = new NodeReference(4, 3);
	private final NodeReference yearRef = new NodeReference(5, 1);
	private final NodeReference monthRef = new NodeReference(6, 1);
	private final Integer week1Row = 11;
	private final Integer weekNumberCol = 1;
	private final Integer actualDLCol = 4;
	private final Integer omDLCol = 5;
	
	
	public void go() throws Exception {
//		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
//		File file = new File(filePath);
//		Document doc = builder.parse(file);
//		doc.getDocumentElement().normalize();  // fix up whitespace weirdness
//
//		Element documentElement = doc.getDocumentElement();
//		//		NodeList nodeList = documentElement.getChildNodes();
//
//		NodeList tabList = documentElement.getElementsByTagName("table:table");
//		for ( int i = 0; i < tabList.getLength(); i++ ) {
//			Node node = tabList.item(i);
//			NamedNodeMap attributes = node.getAttributes();
//			Node tableName = attributes.getNamedItem("table:name");
//			String tabName = tableName.getNodeValue();
//			switch(tabName) {
//			case "Monthly Budget Control Summary":
//				processSummary((Element)node);
//				break;
//			case "DOData":
//				break;
//			case "Budget Control":
//				processBudgetControl((Element)node);
//				break;
//			case "TSR":
//				break;
//			case "ludata":
//				break;
//			default:
//				throw new Exception("unexpected tab name: " + tabName);
//			}
//			
//		}
	}
	
	
	
//	private void processSummary(Element summaryTab) {
//			System.out.println("Processing Summary");
//			Node divisionNode = getNodeByReference(summaryTab, divisionRef);
//			String divisionName = divisionNode.getTextContent();
//			System.out.println("\tdivision name: " + divisionName);
//			Node managerNode = getNodeByReference(summaryTab, managerRef);
//			String managerName = managerNode.getTextContent();
//			System.out.println("\tmanager: " + managerName);
//			Integer year = Integer.valueOf(getNodeByReference(summaryTab, yearRef).getTextContent());
//			System.out.println("Year: " + year);
//			Integer month = Integer.valueOf(getNodeByReference(summaryTab, monthRef).getTextContent());
//			System.out.println("Month: " + month);
	
	//		System.out.println("**************");
	//		Node row = summaryTab.getElementsByTagName("table:table-row").item(week1Row);
	//		NodeList cellList = ((Element)row).getElementsByTagName("table:table-cell");
	//		for ( int i = 0; i < cellList.getLength(); i++ ) {
	//			System.out.println( i + "\t" + cellList.item(i).getTextContent());
	//		}
	//		System.out.println("**************");
	//		for ( int idx = 0; idx < 5; idx++ ) {
	//			Integer weekRow = week1Row + idx;
	//			String weekNumberValue = getNodeByReference(summaryTab, new NodeReference(weekRow, weekNumberCol)).getTextContent();
	//			String actualDLValue = getNodeByReference(summaryTab, new NodeReference(weekRow, actualDLCol)).getTextContent();
	//			String actualOMValue = getNodeByReference(summaryTab, new NodeReference(weekRow, omDLCol)).getTextContent();
	//			System.out.println("Week: " + weekNumberValue + "\tActual: " + actualDLValue + "\tOM: " + actualOMValue);
	//		}
//		}



//	private void processBudgetControl(Element node) {
//		NodeList rowList = node.getElementsByTagName("table:table-row");
//		Integer firstDataRow = 7;
//		for ( int i = firstDataRow; i < rowList.getLength(); i++ ) {
//			Node row = rowList.item(i);
//			Node firstColumn = ((Element)row).getElementsByTagName("table:table-cell").item(1);
//			if ( firstColumn != null ) {
//				String firstColumnText = firstColumn.getTextContent();
//				if ( ! StringUtils.isBlank(firstColumnText) ) {
//					System.out.println(i + ": " + firstColumnText);
//				}
//			}
//		}
//	}



//	private Node getNodeByReference(Element tab, NodeReference reference) {
//		Node rowNode = tab.getElementsByTagName("table:table-row").item(reference.row());
//		Node cell = ((Element)rowNode).getElementsByTagName("table:table-cell").item(reference.column());		
//		return cell;
//	}



	public static void main(String[] args) {
		System.out.println(new Date());
		try {
			new ODSImport().go();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("end");
	}

	
	
	public class NodeReference extends ApplicationObject {
		private static final long serialVersionUID = 1L;
		private Integer row;
		private Integer column;
		/**
		 * 
		 * @param row
		 * @param column
		 */
		public NodeReference(Integer row, Integer column) {
			this.row = row;
			this.column = column;
		}
		public Integer row() { return row; }
		public Integer column() { return column; }
	}
	
	public class BCRImportRow extends ApplicationObject {
		private static final long serialVersionUID = 1L;
		public String account;
		public Integer ticketNumber;
		public Integer claimWeek;
		public String type;
		public Float directLabor;
		public Float expense;
		public Float totalDirectLabor;
		public Float totalVolume;
		public Float volumeClaimed;
		public Float volumeRemaining;
		public String notes;
		public Float billedAmount;
		public Float diffClmBilled;
		public String ticketStatus;
		public String employee;
		
//		public BCRImportRow(Element node) {
//			super();
//			NodeList cellList = node.getElementsByTagName("table:table-cell");
//		}
	}
}
