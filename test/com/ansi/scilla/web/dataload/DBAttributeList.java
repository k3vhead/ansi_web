package com.ansi.scilla.web.dataload;

import java.awt.Color;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.Transformer;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.extensions.XSSFCellBorder.BorderSide;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.db.ApplicationProperties;
import com.ansi.scilla.common.db.Code;
import com.ansi.scilla.common.utils.AppUtils;

public class DBAttributeList {

	
	private static final Database useThisDB = Database.DEV;
	private final String destinationFile = "/home/dclewis/Documents/Dropbox/webthing_v2/projects/ANSI/testresults/table_definition.xlsx";
	
	
	private final String[] headers = new String[] {"Name","Format","Size"};
	private final Integer[] columnWidths = new Integer[] {8000,8000,2500,8000,8000,8000,8000};
	
	private XSSFCellStyle greenCell;
	private XSSFCellStyle yellowCell;
	private XSSFCellStyle redCell;
	private XSSFCellStyle headerCell;
	private XSSFCellStyle centeredCell;
	private XSSFCellStyle redCenteredCell;
	
	
	
	
	public void go(Database db) throws Exception {
		
		Connection conn = null;
		
		try {
			XSSFWorkbook workbook = new XSSFWorkbook();			
			initFormats(workbook);
			
			conn = Database.getConn(db);
			DatabaseMetaData dsmd = conn.getMetaData();
			List<TableDescription> tableList = makeTableList(dsmd);
			for ( TableDescription td : tableList ) {
				makeSheet(workbook, td);
			}

			workbook.write(new FileOutputStream(destinationFile));
			
		} finally {
			AppUtils.closeQuiet(conn);
		}
	}
	
	private void initFormats(XSSFWorkbook workbook) {
		headerCell = workbook.createCellStyle();
		headerCell.setAlignment(HorizontalAlignment.LEFT);
		XSSFFont headerFont = workbook.createFont();
		headerFont.setBold(true);
		headerCell.setFont(headerFont);
		
		
		centeredCell = workbook.createCellStyle();
		centeredCell.setAlignment(HorizontalAlignment.CENTER);		
		
		
		XSSFColor red = new XSSFColor(Color.RED); 
		redCell = workbook.createCellStyle();
		redCell.setAlignment(HorizontalAlignment.LEFT);
		redCell.setBorderBottom(BorderStyle.MEDIUM);
		redCell.setBorderTop(BorderStyle.MEDIUM);
		redCell.setBorderLeft(BorderStyle.MEDIUM);
		redCell.setBorderRight(BorderStyle.MEDIUM);
		redCell.setBorderColor(BorderSide.BOTTOM, red);
		redCell.setBorderColor(BorderSide.TOP, red);
		redCell.setBorderColor(BorderSide.LEFT, red);
		redCell.setBorderColor(BorderSide.RIGHT, red);
		
		redCenteredCell = workbook.createCellStyle();
		redCenteredCell.setAlignment(HorizontalAlignment.LEFT);
		redCenteredCell.setBorderBottom(BorderStyle.MEDIUM);
		redCenteredCell.setBorderTop(BorderStyle.MEDIUM);
		redCenteredCell.setBorderLeft(BorderStyle.MEDIUM);
		redCenteredCell.setBorderRight(BorderStyle.MEDIUM);
		redCenteredCell.setBorderColor(BorderSide.BOTTOM, red);
		redCenteredCell.setBorderColor(BorderSide.TOP, red);
		redCenteredCell.setBorderColor(BorderSide.LEFT, red);
		redCenteredCell.setBorderColor(BorderSide.RIGHT, red);
		redCenteredCell.setAlignment(HorizontalAlignment.CENTER);
		
		XSSFColor yellow = new XSSFColor(Color.YELLOW); 
		yellowCell = workbook.createCellStyle();
		yellowCell.setAlignment(HorizontalAlignment.LEFT);
		yellowCell.setFillBackgroundColor(yellow);
		yellowCell.setBorderBottom(BorderStyle.MEDIUM);
		yellowCell.setBorderTop(BorderStyle.MEDIUM);
		yellowCell.setBorderLeft(BorderStyle.MEDIUM);
		yellowCell.setBorderRight(BorderStyle.MEDIUM);
		yellowCell.setBorderColor(BorderSide.BOTTOM, yellow);
		yellowCell.setBorderColor(BorderSide.TOP, yellow);
		yellowCell.setBorderColor(BorderSide.LEFT, yellow);
		yellowCell.setBorderColor(BorderSide.RIGHT, yellow);
		
		XSSFColor green = new XSSFColor(Color.GREEN); 
		greenCell = workbook.createCellStyle();
		greenCell.setAlignment(HorizontalAlignment.LEFT);
		greenCell.setFillBackgroundColor(green);
		greenCell.setBorderBottom(BorderStyle.MEDIUM);
		greenCell.setBorderTop(BorderStyle.MEDIUM);
		greenCell.setBorderLeft(BorderStyle.MEDIUM);
		greenCell.setBorderRight(BorderStyle.MEDIUM);
		greenCell.setBorderColor(BorderSide.BOTTOM, green);
		greenCell.setBorderColor(BorderSide.TOP, green);
		greenCell.setBorderColor(BorderSide.LEFT, green);
		greenCell.setBorderColor(BorderSide.RIGHT, green);		
	}

	private List<TableDescription> makeTableList(DatabaseMetaData dsmd) throws SQLException {
		List<TableDescription> tableList = new ArrayList<TableDescription>();
		ResultSet rs = dsmd.getTables(null,  null, "%", new String[] {"TABLE"});
		while ( rs.next() ) {
			String tableName = rs.getString("TABLE_NAME");
			if ( ! tableName.endsWith("_history")) {
				tableList.add(new TableDescription(tableName, dsmd));
			}
		}
		rs.close();
		Collections.sort(tableList);
		return tableList;
	}

	private XSSFSheet makeSheet(XSSFWorkbook workbook, TableDescription td) {
		
		System.out.println("Making sheet: " + td.tableName);
		
		XSSFSheet sheet = workbook.createSheet(td.tableName);
		XSSFRow row = sheet.createRow(0);
		XSSFCell cell = null;
		
		for ( int i = 0; i < headers.length; i++ ) {
			cell = row.createCell(i);
			cell.setCellValue(headers[i]);
			cell.setCellStyle(headerCell);
			sheet.setColumnWidth(i, columnWidths[i]);
		}
		
		int rowNum = 1;		
		for ( ColumnDescription cd : td.columns ) {
			row = sheet.createRow(rowNum);
			cell = row.createCell(0);
			cell.setCellValue(cd.columnName);

			cell = row.createCell(1);
			cell.setCellValue(cd.typeName);

			cell = row.createCell(2);
			cell.setCellValue(cd.columnSize);
			
			rowNum++;
		}
		
		return sheet;
	}

	/*
	private void makeTableSheet(Connection devConn, Connection uatConn, Connection prodConn, XSSFWorkbook workbook) throws Exception {
		XSSFSheet sheet = makeSheet(workbook);
		XSSFRow row = null;
		XSSFCell cell = null;
		int rowNum = 1;
		
		DatabaseMetaData devDsmd = devConn.getMetaData();
		DatabaseMetaData uatDsmd = uatConn.getMetaData();
		DatabaseMetaData prodDsmd = prodConn.getMetaData();

		List<TableDescription> devTables = makeTableList(devDsmd);
		List<TableDescription> uatTables = makeTableList(uatDsmd);
		List<TableDescription> prodTables = makeTableList(prodDsmd);

		List<TableDescription> allTables = new ArrayList<TableDescription>();
		combineTableList(allTables, devTables);
		combineTableList(allTables, uatTables);
		combineTableList(allTables, prodTables);

		Collections.sort(allTables);

		for ( TableDescription tableDescription : allTables ) {
			String tableName = tableDescription.tableName;
			row = sheet.createRow(rowNum);
			cell = row.createCell(TABLE);
			cell.setCellValue(tableName);

			cell = row.createCell(DEV);
			if ( devTables.contains(tableDescription) ) {					
				cell.setCellValue(tableName);
			} else {
				cell.setCellStyle(this.redCell);
			}

			cell = row.createCell(UAT);
			if ( uatTables.contains(tableDescription) ) {					
				cell.setCellValue(tableName);					
			} else {
				cell.setCellStyle(this.redCell);
			}

			cell = row.createCell(PROD);
			if ( prodTables.contains(tableDescription) ) {					
				cell.setCellValue(tableName);
			} else {
				cell.setCellStyle(this.redCell);
			}

			cell = row.createCell(DEV_HISTORY);
			Boolean hasHistory = hasHistory(tableDescription, devTables);
			if ( hasHistory != null ) {
				if ( hasHistory ) {
					cell.setCellStyle(greenCell);
				} else {
					cell.setCellValue("X");
					cell.setCellStyle(redCenteredCell);
				}
			}

			if ( devTables.contains(tableDescription) && uatTables.contains(tableDescription)) {
				List<String> mismatch = compareColumns(tableDescription.tableName, devTables, uatTables);
				if ( mismatch.isEmpty() ) {
					cell = row.createCell(UAT_COLUMN);
					cell.setCellStyle(greenCell);
				} else {
					int mismatchRow = rowNum;
					for ( String columnName : mismatch ) {
						cell = row.createCell(UAT_COLUMN);
						cell.setCellStyle(redCell);
						cell.setCellValue(columnName);
						mismatchRow++;
						row = sheet.createRow(mismatchRow);
					}
					rowNum = mismatchRow - 1;
				}
			}

			if ( uatTables.contains(tableDescription) && prodTables.contains(tableDescription)) {
				List<String> mismatch = compareColumns(tableDescription.tableName, uatTables, prodTables);
				if ( mismatch.isEmpty() ) {
					cell = row.createCell(PROD_COLUMN);
					cell.setCellStyle(greenCell);
				} else {
					int mismatchRow = rowNum;
					for ( String columnName : mismatch ) {
						cell = row.createCell(PROD_COLUMN);
						cell.setCellStyle(redCell);
						cell.setCellValue(columnName);
						mismatchRow++;
						row = sheet.createRow(mismatchRow);
					}
					rowNum = mismatchRow - 1;
				}
			}

			rowNum++;
		}
			
		
	}
	
	*/
	
	



	public static void main(String[] args) {
		System.out.println("Start");
		try {
			new DBAttributeList().go(useThisDB);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("End");
	}

	
	
	public class ColumnDescription extends ApplicationObject implements Comparable<ColumnDescription> {
		private static final long serialVersionUID = 1L;
		public String columnName;
		public Integer dataType;
		public String typeName;
		public Integer columnSize;
		
		public ColumnDescription(ResultSet rs) throws SQLException {
			super();
			this.columnName = rs.getString("COLUMN_NAME");
			this.dataType = rs.getInt("DATA_TYPE");
			this.typeName = rs.getString("TYPE_NAME");
			this.columnSize = rs.getInt("COLUMN_SIZE");
		}

		@Override
		public int compareTo(ColumnDescription o) {
			return this.columnName.compareTo(o.columnName);
		}

		@Override
		public boolean equals(Object obj) {
			boolean equals = true;
			if ( obj instanceof ColumnDescription ) {
				ColumnDescription cd = (ColumnDescription)obj;
				if ( ! columnName.equals(cd.columnName)) { equals = false; } 
				if ( ! dataType.equals(cd.dataType)) { equals = false; } 
				if ( ! typeName.equals(cd.typeName)) { equals = false; } 
				if ( ! columnSize.equals(cd.columnSize)) { equals = false; } 
			} else {
				equals = false;
			}
			return equals;
		}
		
		
	}
	
	
	public class TableDescription extends ApplicationObject implements Comparable<TableDescription> {
		private static final long serialVersionUID = 1L;
		public String tableName;
		public List<ColumnDescription> columns;
		
		
		public TableDescription(String tableName, DatabaseMetaData dsmd) throws SQLException {
			super();
			this.tableName = tableName;
			this.columns = makeColumnList(dsmd, tableName);
			Collections.sort(this.columns);
		}

		private List<ColumnDescription> makeColumnList(DatabaseMetaData devDsmd, String tableName) throws SQLException {
			List<ColumnDescription> columnList = new ArrayList<ColumnDescription>();
			ResultSet rs = devDsmd.getColumns(null, null, tableName, null);
			while ( rs.next() ) {
				columnList.add(new ColumnDescription(rs));
			}		
			rs.close();
			Collections.sort(columnList);
			return columnList;
		}
		
		
		@Override
		public int compareTo(TableDescription o) {
			return this.tableName.compareTo(o.tableName);
		}

		@Override
		public boolean equals(Object obj) {
			boolean equals = true;
			if ( obj instanceof TableDescription ) {
				TableDescription td = (TableDescription)obj;
				equals = tableName.contentEquals(td.tableName);
				if ( columns.size() == td.columns.size() ) {
					HashMap<String, ColumnDescription> hm = new HashMap<String, ColumnDescription>();
					for ( ColumnDescription cd : td.columns ) {
						hm.put(cd.columnName, cd);
					}

					for ( ColumnDescription cd : this.columns ) {	
						ColumnDescription cd2 = hm.get(cd.columnName);
						if ( ! cd.equals(cd2) ) {
							equals = false;
						}
					}
				} else {
					equals = false;
				}
			} else {
				equals = false;
			}
			return equals;
		}
		
	}
	
	
	
	public class TableNameMatchPredicate implements Predicate<TableDescription> {
		public String tableName;
		
		public TableNameMatchPredicate() { 
			super(); 
		}
		public TableNameMatchPredicate(String tableName) {
			this();
			this.tableName = tableName;
		}
		@Override
		public boolean evaluate(TableDescription arg0) {
			return arg0.tableName.equalsIgnoreCase(tableName);
		}
		
	}
	
	public class CodeMatchPredicate implements Predicate<Code> {
		public String tableName;
		public String fieldName;
		public String value;
		
		
		@Override
		public boolean evaluate(Code arg0) {
			return arg0.getTableName().equals(tableName) && arg0.getFieldName().equals(fieldName) && arg0.getValue().equals(value);
		}
		
	}
	
	
	public class ColumnNameMatchPredicate implements Predicate<ColumnDescription> {
		public String columnName;
		
		public ColumnNameMatchPredicate() { 
			super(); 
		}
		public ColumnNameMatchPredicate(String tableName) {
			this();
			this.columnName = tableName;
		}
		@Override
		public boolean evaluate(ColumnDescription arg0) {
			return arg0.columnName.equals(columnName);
		}		
	}
	
	
	public class AppPropertyNameTransformer implements Transformer<ApplicationProperties, String> {
		@Override
		public String transform(ApplicationProperties arg0) {
			return arg0.getPropertyId();
		}
		
	}
	
	public enum Database {
		DEV,
		UAT,
		PROD,
		;
		
		public static Connection getConn(Database db) throws Exception {
			Connection conn = null;
			switch (db) {
			case DEV:
				conn = AppUtils.getDevConn();
				break;
			case PROD:
				conn = AppUtils.getProdConn();
				break;
			case UAT:
				conn = AppUtils.getUATConn();
				break;
			default:
				throw new Exception("Invalid db");
			}
			return conn;
		}
	}
}
