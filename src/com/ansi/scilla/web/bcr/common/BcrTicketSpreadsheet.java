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

import com.ansi.scilla.web.common.struts.SessionDivision;

public class BcrTicketSpreadsheet {
	/**
	 * @author jwlewis
	 * @throws Exception 
	 */
	
//	public BcrTicketSpreadsheet() {
//		//this.BcrTicketSpreadsheet(conn, year, weeks);
//	}
	
	private static final HashMap<String, String> headerMap;
	private static DecimalFormat df = new DecimalFormat("#0.00");
//	private static BCRHeader[] {
//		new BCRHeader("job_site_name","Account",123.4),
//		new BCRHeader("ticket_id","Ticket Id",123),
//		new BCRHeader("claim_id","Claim Id",123),
//		new BCRHeader("claim_week","Claim Week",123),
//		new BCRHeader("dl_amt","Dl Amount",123),
//		new BCRHeader("dl_expenses","Dl Expenses",123),
//		new BCRHeader("dl_total","Dl Total",123),
//		new BCRHeader("total_volume","Total Volume",123),
//		new BCRHeader("volume_claimed","Volume Claimed",123),
//		new BCRHeader("passthru_volume","PassThru Volume",123),
//		new BCRHeader("passthru_expense_type","PassThru Expense Type",123),
//		new BCRHeader("claimed_volume_total","Claimed Volume Total",123),
//		new BCRHeader("volume_remaining","Volume Remaining",123),
//		new BCRHeader("service_tag_id","Service Tag Id",123),
//		new BCRHeader("notes","Notes",123),
//		new BCRHeader("billed_amount","Billed Amount",123),
//		new BCRHeader("claimed_vs_billed","Claimed vs Billed",123),
//		new BCRHeader("ticket_status","Ticket Status",123),
//		new BCRHeader("employee","Employee",123),
//		new BCRHeader("equipment_tags","Equipment Tags",123),
//	}
	
	private XSSFWorkbook workbook;
	
	static {
		headerMap = new HashMap<String, String>();
		headerMap.put("job_site_name","Account");
		headerMap.put("ticket_id","Ticket Id");
		headerMap.put("claim_id","Claim Id");
		headerMap.put("claim_week","Claim Week");
		headerMap.put("dl_amt","Dl Amount");
		headerMap.put("dl_expenses","Dl Expenses");
		headerMap.put("dl_total","Dl Total");
		headerMap.put("total_volume","Total Volume");
		headerMap.put("volume_claimed","Volume Claimed");
		headerMap.put("passthru_volume","PassThru Volume");
		headerMap.put("passthru_expense_type","PassThru Expense Type");
		headerMap.put("claimed_volume_total","Claimed Volume Total");
		headerMap.put("volume_remaining","Volume Remaining");
		headerMap.put("service_tag_id","Service Tag Id");
		headerMap.put("notes","Notes");
		headerMap.put("billed_amount","Billed Amount");
		headerMap.put("claimed_vs_billed","Claimed vs Billed");
		headerMap.put("ticket_status","Ticket Status");
		headerMap.put("employee","Employee");
		headerMap.put("equipment_tags","Equipment Tags");
	}
	
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
//		while(!rsmd.getColumnName(colNum).isEmpty()) {
		XSSFCellStyle cellStyleRight = workbook.createCellStyle();
		cellStyleRight.setAlignment(CellStyle.ALIGN_RIGHT);
		
		XSSFCellStyle cellStyleCenter = workbook.createCellStyle();
		cellStyleCenter.setAlignment(HorizontalAlignment.CENTER);
		
		XSSFCellStyle headerStyle = workbook.createCellStyle();
		XSSFFont headerFont = workbook.createFont();
		headerFont.setBold(true);
		headerStyle.setFont(headerFont);
		
		for ( int colNum = 1; colNum<= rsmd.getColumnCount(); colNum++) {
			cell = row.createCell(colNum - 1);
			if(headerMap.get(rsmd.getColumnName(colNum)) != null) {
				cell.setCellValue(headerMap.get(rsmd.getColumnName(colNum)));
				cell.setCellStyle(headerStyle);
			} else {
				cell.setCellValue(rsmd.getColumnName(colNum));
				cell.setCellStyle(headerStyle);
			}
			System.out.println(rsmd.getColumnTypeName(colNum) + " : " + rsmd.getColumnClassName(colNum) + " : " + rsmd.getColumnName(colNum));
		}
		
		int rowNum = 1;	
		
		
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
		sheet.setDefaultColumnWidth(20);
		sheet.setColumnWidth(1, 30);
		
//		for(int i = 1; i <= rsmd.getColumnCount(); i++) {
//			sheet.autoSizeColumn(i);
//		}
		
//		workbook.write(new FileOutputStream("/home/jwlewis/Documents/projects/BCR_Spreadsheet.xlsx"));
//		workbook.write(new FileOutputStream("/home/dclewis/Documents/webthing_v2/projects/ANSI/testresults/BCR_Spreadsheet.xlsx"));
//		return workbook;
	}
	
	public void BCRHeader(String headerSql, String headerTitle, BigDecimal cellWidth) {
		
	}
	
}
