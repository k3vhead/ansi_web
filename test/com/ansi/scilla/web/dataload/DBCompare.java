package com.ansi.scilla.web.dataload;

import java.awt.Color;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IterableUtils;
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

public class DBCompare {

	private final Integer TABLE = 0;
	private final Integer DEV = 1;
	private final Integer UAT = 2;
	private final Integer UAT_COLUMN = 3;
	private final Integer PROD = 4;
	private final Integer PROD_COLUMN = 5;
	
	private final String[] headers = new String[] {"Table", "DEV","UAT","Col", "PROD", "Col"};
	private final Integer[] columnWidths = new Integer[] {8000,8000,8000,8000,8000,8000};
	
	private XSSFCellStyle greenCell;
	private XSSFCellStyle yellowCell;
	private XSSFCellStyle redCell;
	private XSSFCellStyle headerCell;
	private XSSFCellStyle centeredCell;
	
	
	
	
	
	public void go() throws Exception {
		Connection uatConn = null;
		Connection devConn = null;
		Connection prodConn = null;
		
		try {
			XSSFWorkbook workbook = new XSSFWorkbook();			
			
			uatConn = AppUtils.getUATConn();
			devConn = AppUtils.getDevConn();
			prodConn = AppUtils.getProdConn();
			
			makeTableSheet(devConn, uatConn, prodConn, workbook);
			makeAppPropertiesSheet(devConn, uatConn, prodConn, workbook);
			makeCodeSheet(devConn, uatConn, prodConn, workbook);
			workbook.write(new FileOutputStream("/home/dclewis/Documents/webthing_v2/projects/ANSI/testresults/table_compare.xlsx"));
			
		} finally {
			AppUtils.closeQuiet(devConn);
			AppUtils.closeQuiet(uatConn);
			AppUtils.closeQuiet(prodConn);
		}
	}
	
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
	
	private void makeAppPropertiesSheet(Connection devConn, Connection uatConn, Connection prodConn, XSSFWorkbook workbook) throws Exception {
		ApplicationProperties appProperties = new ApplicationProperties();
		List<ApplicationProperties> devProperties = ApplicationProperties.cast(appProperties.selectAll(devConn));
		List<ApplicationProperties> uatProperties = ApplicationProperties.cast(appProperties.selectAll(uatConn));
		List<ApplicationProperties> prodProperties = ApplicationProperties.cast(appProperties.selectAll(prodConn));
		
		AppPropertyNameTransformer appPropertyNameTransformer = new AppPropertyNameTransformer();
		List<String> devNames = IterableUtils.toList(CollectionUtils.collect(devProperties, appPropertyNameTransformer));
		List<String> uatNames = IterableUtils.toList(CollectionUtils.collect(uatProperties, appPropertyNameTransformer));
		List<String> prodNames = IterableUtils.toList(CollectionUtils.collect(prodProperties, appPropertyNameTransformer));
		
		List<String> allNames = new ArrayList<String>();
		CollectionUtils.addAll(allNames, devNames);
		CollectionUtils.addAll(allNames, uatNames);
		CollectionUtils.addAll(allNames, prodNames);
		Collections.sort(allNames);
		
		XSSFSheet sheet = workbook.createSheet("AppProperties");
		XSSFRow row = null;
		XSSFCell cell = null;
		int rowNum = 1;
		
		row = sheet.createRow(0);
		String[] headers = new String[] {"Property","DEV","UAT","PROD"};
		for ( int i = 0; i < headers.length; i++ ) {
			cell = row.createCell(i);
			cell.setCellValue(headers[i]);
			cell.setCellStyle(headerCell);
			sheet.setColumnWidth(i, 10000);
		}
		
		for ( String property : IterableUtils.uniqueIterable(allNames) ) {
			row = sheet.createRow(rowNum);
			cell = row.createCell(0);
			cell.setCellValue(property);
			cell = row.createCell(1);
			if ( devNames.contains(property) ) {				
				cell.setCellValue(property);
			} else { 
				cell.setCellStyle(redCell);
			}
			cell = row.createCell(2);
			if ( uatNames.contains(property) ) {
				cell.setCellValue(property);
			} else { 
				cell.setCellStyle(redCell);
			}
			cell = row.createCell(3);
			if ( prodNames.contains(property) ) {
				cell.setCellValue(property);
			} else { 
				cell.setCellStyle(redCell);
			}
			rowNum++;
		}
		
	}

	private void makeCodeSheet(Connection devConn, Connection uatConn, Connection prodConn, XSSFWorkbook workbook) throws Exception {
		Code appProperties = new Code();
		List<Code> devCodes = Code.cast(appProperties.selectAll(devConn));
		List<Code> uatCodes = Code.cast(appProperties.selectAll(uatConn));
		List<Code> prodCodes = Code.cast(appProperties.selectAll(prodConn));
				
		
		List<Code> allCodes = new ArrayList<Code>();
		combineCodeList(allCodes, devCodes);
		combineCodeList(allCodes, uatCodes);
		combineCodeList(allCodes, prodCodes);
		Collections.sort(allCodes, new Comparator<Code>() {
			public int compare(Code o1, Code o2) {
				int ret = o1.getTableName().compareTo(o2.getTableName());
				if ( ret == 0 ) {
					ret = o1.getFieldName().compareTo(o2.getFieldName());
				}
				if ( ret == 0 ) {
					ret = o1.getValue().compareTo(o2.getValue());
				}
				return ret;
			}
		});
		
		XSSFSheet sheet = workbook.createSheet("Codes");
		XSSFRow row = null;
		XSSFCell cell = null;
		int rowNum = 1;
		CodeMatchPredicate predicate = new CodeMatchPredicate();
		
		row = sheet.createRow(0);
		String[] headers = new String[] {"Code","Field","Value","DEV","UAT","PROD"};
		Integer[] widths = new Integer[] {6000,6000,6000, 1300,1300,1300};
		for ( int i = 0; i < headers.length; i++ ) {
			cell = row.createCell(i);
			cell.setCellValue(headers[i]);
			cell.setCellStyle(headerCell);
			sheet.setColumnWidth(i, widths[i]);
		}
				
		for ( Code code : allCodes ) {
			row = sheet.createRow(rowNum);
			cell = row.createCell(0);
			cell.setCellValue(code.getTableName());
			cell = row.createCell(1);
			cell.setCellValue(code.getFieldName());
			cell = row.createCell(2);
			cell.setCellValue(code.getValue());

			predicate.tableName = code.getTableName();
			predicate.fieldName = code.getFieldName();
			predicate.value = code.getValue();
					
			cell = row.createCell(3);
			if ( IterableUtils.matchesAny(devCodes, predicate) ) {				
				cell.setCellValue("X");
				cell.setCellStyle(centeredCell);
			} else { 
				cell.setCellStyle(redCell);				
			}
			
			cell = row.createCell(4);
			if ( IterableUtils.matchesAny(uatCodes, predicate) ) {				
				cell.setCellValue("X");
				cell.setCellStyle(centeredCell);
			} else { 
				cell.setCellStyle(redCell);				
			}
			
			cell = row.createCell(5);
			if ( IterableUtils.matchesAny(prodCodes, predicate) ) {				
				cell.setCellValue("X");
				cell.setCellStyle(centeredCell);
			} else { 
				cell.setCellStyle(redCell);				
			}
			

			rowNum++;
		}
		
	}
	
	private List<String> compareColumns(String tableName, List<TableDescription> tableList1,  List<TableDescription> tableList2) {		
		List<String> mismatch = new ArrayList<String>();
		TableNameMatchPredicate tablePredicate = new TableNameMatchPredicate(tableName);
		ColumnNameMatchPredicate columnPredicate = new ColumnNameMatchPredicate();
		TableDescription table1 = IterableUtils.find(tableList1, tablePredicate);
		TableDescription table2 = IterableUtils.find(tableList2, tablePredicate);
		
		if ( table1 == null || table2 == null ) {
			throw new RuntimeException("This shouldn't happen");
		}
		List<ColumnDescription> allColumns = new ArrayList<ColumnDescription>();
		for ( ColumnDescription cd : table1.columns ) {
			columnPredicate.columnName = cd.columnName;
			if ( ! IterableUtils.matchesAny(allColumns, columnPredicate) ) {
				allColumns.add(cd);
			}
		}
		for ( ColumnDescription cd : table2.columns ) {
			columnPredicate.columnName = cd.columnName;
			if ( ! IterableUtils.matchesAny(allColumns, columnPredicate) ) {
				allColumns.add(cd);
			}
		}
		Collections.sort(allColumns);
		for ( ColumnDescription cd : allColumns ) {
			columnPredicate.columnName = cd.columnName;
			ColumnDescription cd1 = IterableUtils.find(table1.columns, columnPredicate);
			ColumnDescription cd2 = IterableUtils.find(table1.columns, columnPredicate);
			if ( cd1 == null || cd2 == null || !cd1.equals(cd2) ) {
				mismatch.add(cd.columnName);
			}
		}
		Collections.sort(mismatch);
		return mismatch;
	}



	private void combineTableList(List<TableDescription> allTables, List<TableDescription> newTables) {
		TableNameMatchPredicate predicate = new TableNameMatchPredicate();
		
		for ( TableDescription newTd : newTables ) {
			predicate.tableName = newTd.tableName;
			if ( ! IterableUtils.matchesAny(allTables, predicate) ) {
				allTables.add(newTd);
			}
		}
	}
	
	
	
	private void combineCodeList(List<Code> allTables, List<Code> newTables) {
		CodeMatchPredicate predicate = new CodeMatchPredicate();
		
		for ( Code newTd : newTables ) {
			predicate.tableName = newTd.getTableName();
			predicate.fieldName = newTd.getFieldName();
			predicate.value = newTd.getValue();
			if ( ! IterableUtils.matchesAny(allTables, predicate) ) {
				allTables.add(newTd);
			}
		}
	}



	private XSSFSheet makeSheet(XSSFWorkbook workbook) {
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
		
		
		XSSFSheet sheet = workbook.createSheet("Tables");
		XSSFRow row = sheet.createRow(0);
		XSSFCell cell = null;
		
		for ( int i = 0; i < headers.length; i++ ) {
			cell = row.createCell(i);
			cell.setCellValue(headers[i]);
			cell.setCellStyle(headerCell);
			sheet.setColumnWidth(i, columnWidths[i]);
		}
		
		return sheet;
	}

	
	private List<TableDescription> makeTableList(DatabaseMetaData dsmd) throws SQLException {
		List<TableDescription> tableList = new ArrayList<TableDescription>();
		ResultSet rs = dsmd.getTables(null,  null, "%", new String[] {"TABLE"});
		while ( rs.next() ) {
			tableList.add(new TableDescription(rs.getString("TABLE_NAME"), dsmd));
		}
		rs.close();
		Collections.sort(tableList);
		return tableList;
	}

	
	

	
	public static void main(String[] args) {
		System.out.println("Start");
		try {
			new DBCompare().go();
//			new DBCompare().test();
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
			return arg0.tableName.equals(tableName);
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
	
	
}
