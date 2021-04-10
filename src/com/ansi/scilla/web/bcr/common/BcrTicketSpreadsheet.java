package com.ansi.scilla.web.bcr.common;

import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.web.common.struts.SessionDivision;

public class BcrTicketSpreadsheet {
	/**
	 * @author jwlewis
	 * @throws Exception 
	 */
	
//	public BcrTicketSpreadsheet() {
//		//this.BcrTicketSpreadsheet(conn, year, weeks);
//	}
	
//	private static final HashMap<String, String> headerMap;
	private static DecimalFormat df = new DecimalFormat("#0.00");
	private static XSSFWorkbook workbook;
	
//	static {
//		XSSFCellStyle cellStyleRight = workbook.createCellStyle();
//		cellStyleRight.setAlignment(CellStyle.ALIGN_RIGHT);
//		
//		XSSFCellStyle cellStyleCenter = workbook.createCellStyle();
//		cellStyleCenter.setAlignment(HorizontalAlignment.CENTER);
//		
//		XSSFCellStyle headerStyle = workbook.createCellStyle();
//		XSSFFont headerFont = workbook.createFont();
//		headerFont.setBold(true);
//		headerStyle.setFont(headerFont);
//	}
	
	private static BCRHeader[] headerMap = new BCRHeader[] {
		new BCRHeader("job_site_name","Account",123.4D),
		new BCRHeader("ticket_id","Ticket Id",123D),
		new BCRHeader("claim_id","Claim Id",123D),
		new BCRHeader("claim_week","Claim Week",123D),
		new BCRHeader("dl_amt","Dl Amount",123D),
		new BCRHeader("dl_expenses","Dl Expenses",123D),
		new BCRHeader("dl_total","Dl Total",123D),
		new BCRHeader("total_volume","Total Volume",123D),
		new BCRHeader("volume_claimed","Volume Claimed",123D),
		new BCRHeader("passthru_volume","PassThru Volume",123D),
		new BCRHeader("passthru_expense_type","PassThru Expense Type",123D),
		new BCRHeader("claimed_volume_total","Claimed Volume Total",123D),
		new BCRHeader("volume_remaining","Volume Remaining",123D),
		new BCRHeader("service_tag_id","Service Tag Id",123D),
		new BCRHeader("notes","Notes",123D),
		new BCRHeader("billed_amount","Billed Amount",123D),
		new BCRHeader("claimed_vs_billed","Claimed vs Billed",123D),
		new BCRHeader("ticket_status","Ticket Status",123D),
		new BCRHeader("employee","Employee",123D),
		new BCRHeader("equipment_tags","Equipment Tags",123D),
	};
	
	
	
	
	
	public BcrTicketSpreadsheet(Connection conn, List<SessionDivision> divisionList, Integer divisionId, Integer claimYear, String workWeeks) 
			throws Exception {
//		String sql = BcrTicketSql.makeBaseWhereClause(workWeeks);
		String baseSql = BcrTicketSql.sqlSelectClause + BcrTicketSql.makeFilteredFromClause(divisionList) + BcrTicketSql.makeBaseWhereClause(workWeeks);
		System.out.println(baseSql);
		createSpreadsheet(conn, baseSql, divisionList, divisionId, claimYear, workWeeks);
	}
	
	
	public XSSFWorkbook getWorkbook() {
		return workbook;
	}


	
	private void createSpreadsheet(Connection conn, String sql, List<SessionDivision> divisionList, Integer divisionId, Integer year, String workWeekList) 
			throws Exception {
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setInt(1,  divisionId);
		ps.setInt(2, year);
		ResultSet rs = ps.executeQuery();
		ResultSetMetaData rsmd = rs.getMetaData();
		
		this.workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet();
		Row row = null;
		Cell cell = null;
		workbook.setSheetName(0, "BCR Ticket Spreadsheet");
		/*
		 * Column Headers:
		 * Job Site Name, String
		 * Ticket Id, Integer
		 * Claim Id, Integer
		 * Claim Week, String
		 * Dl Amt, BigDecimal
		 * Dl Expenses, BigDecimal
		 * Dl Total, BigDecimal
		 * Total Volume, BigDecimal
		 * Volume Claimed, BigDecimal
		 * PassThru Volume, BigDecimal
		 * PassThru Expense Type, String
		 * Claimed Volume Total, BigDecimal
		 * Volume Remaining, BigDecimal
		 * Service Tag Id, String
		 * Notes, String
		 * Billed Amount, BigDecimal
		 * Claimed vs Billed, BigDecimal
		 * Ticket Status, String
		 * Employee, String
		 * Equipment Tags, String
		 */
		row = sheet.createRow(0);

		XSSFCellStyle cellStyleRight = workbook.createCellStyle();
		cellStyleRight.setAlignment(CellStyle.ALIGN_RIGHT);
		
		XSSFCellStyle cellStyleCenter = workbook.createCellStyle();
		cellStyleCenter.setAlignment(HorizontalAlignment.CENTER);
		
		XSSFCellStyle headerStyle = workbook.createCellStyle();
		XSSFFont headerFont = workbook.createFont();
		headerFont.setBold(true);
		headerStyle.setFont(headerFont);
		
		for (int colNum = 0; colNum < headerMap.length; colNum++ ) {
			BCRHeader header = headerMap[colNum];
			cell = row.createCell(colNum);
			cell.setCellValue(header.getHeaderName());
			cell.setCellStyle(headerStyle);
		}
		
		Integer rowNum = 1;
		
		while(rs.next()) {
			row = sheet.createRow(rowNum);
			for (int colNum = 0; colNum < headerMap.length; colNum++ ) {
				BCRHeader header = headerMap[colNum];
				Object obj = rs.getObject(header.dbField);
				if ( obj == null ) {
					// ignore it and go on with life
					cell = row.createCell(colNum);
					cell.setCellValue("null");
				} else if ( obj instanceof String ) {
					String value = (String)obj;
					cell = row.createCell(colNum);
					cell.setCellValue(value);
				} else if ( obj instanceof BigDecimal ) {
					BigDecimal value = (BigDecimal)obj;
					cell = row.createCell(colNum);
					cell.setCellValue(df.format(value));
					cell.setCellStyle(cellStyleRight);
				} else if ( obj instanceof Integer ) {
					Integer value = (Integer)obj;
					cell = row.createCell(colNum);
					cell.setCellValue(value);
					cell.setCellStyle(cellStyleCenter);
				} else {
					throw new Exception("Joshua didn't code this one: " + obj.getClass().getCanonicalName());
				}
			}
			rowNum++;
		}
		
		
		
		while(rs.next()) {
			row = sheet.createRow(rowNum);
			for(int i = 1; i <= rsmd.getColumnCount(); i++) {
				cell = row.createCell(i - 1);
				String[] sub = rsmd.getColumnClassName(i).split("\\.");
				if(sub[sub.length - 1].equalsIgnoreCase("String")) {
					cell.setCellValue(rs.getString(i));
				} else if(sub[sub.length - 1].equalsIgnoreCase("BigDecimal")) {
					BigDecimal x = rs.getBigDecimal(i);
					double d = x.doubleValue();
					cell.setCellValue(df.format(d));
					cell.setCellStyle(cellStyleRight);
				} else if(sub[sub.length - 1].equalsIgnoreCase("Integer")) {
					cell.setCellValue(rs.getInt(i));
					cell.setCellStyle(cellStyleCenter);
				} else {
					throw new Exception("Unexpected value format" + rsmd.getColumnClassName(i));
				}
				if(i == 18) {
					cell.setCellStyle(cellStyleCenter);
				}
			}
			rowNum++;
			
		}
		rs.close();
		conn.close();
		sheet.setDefaultColumnWidth(20);
		sheet.setColumnWidth(1, 30);
		
//		for(int i = 1; i <= rsmd.getColumnCount(); i++) {
//			sheet.autoSizeColumn(i);
//		}
		
//		workbook.write(new FileOutputStream("/home/jwlewis/Documents/projects/BCR_Spreadsheet.xlsx"));
//		workbook.write(new FileOutputStream("/home/dclewis/Documents/webthing_v2/projects/ANSI/testresults/BCR_Spreadsheet.xlsx"));
//		return workbook;
	}
	
	public static class BCRHeader extends ApplicationObject {
		private static final long serialVersionUID = 1L;
		private String dbField;
		private String headerName;
		private Double columnWidth;
		//private XSSFCellStyle cellStyle;
		public BCRHeader(String dbField, String headerName, Double columnWidth) {
			super();
			this.dbField = dbField;
			this.headerName = headerName;
			this.columnWidth = columnWidth;
//			this.cellStyle = cellStyle;
		}
		public String getDbField() {
			return dbField;
		}
		public void setDbField(String dbField) {
			this.dbField = dbField;
		}
		public String getHeaderName() {
			return headerName;
		}
		public void setHeaderName(String headerName) {
			this.headerName = headerName;
		}
		public Double getColumnWidth() {
			return columnWidth;
		}
		public void setColumnWidth(Double columnWidth) {
			this.columnWidth = columnWidth;
		}
//		public XSSFCellStyle getCellStyle() {
//			return cellStyle;
//		}
//		public void setCellStyle(XSSFCellStyle cellStyle) {
//			this.cellStyle = cellStyle;
//		}
		
	}

}
