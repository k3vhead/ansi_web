package com.ansi.scilla.web.bcr.common;

import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
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
	
	private void createSpreadsheet(Connection conn, String sql, List<SessionDivision> divisionList, Integer divisionId, Integer year, String workWeekList) 
			throws Exception {
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setInt(1,  divisionId);
		ps.setInt(2, year);
		ResultSet rs = ps.executeQuery();
		ResultSetMetaData rsmd = rs.getMetaData();
		
		XSSFWorkbook workbook = new XSSFWorkbook();
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
//		int colNum = 1;
//		while(!rsmd.getColumnName(colNum).isEmpty()) {
		for ( int colNum = 1; colNum<= rsmd.getColumnCount(); colNum++) {
			cell = row.createCell(colNum - 1);
			if(headerMap.get(rsmd.getColumnName(colNum)) != null) {
				cell.setCellValue(headerMap.get(rsmd.getColumnName(colNum)));
			} else {
				cell.setCellValue(rsmd.getColumnName(colNum));
			}
			System.out.println(rsmd.getColumnTypeName(colNum) + " : " + rsmd.getColumnClassName(colNum) + " : " + rsmd.getColumnName(colNum));
//			colNum++;
		}
		int colNum = 1;
		int rowNum = 1;
		
		while(rs.next()) {
			row = sheet.createRow(rowNum);
			for(int i = 1; i <= rsmd.getColumnCount(); i++) {
				cell = row.createCell(i - 1);
				String[] sub = rsmd.getColumnClassName(i).split(".");
				System.out.println(rsmd.getColumnClassName(i).substring(10));
				if(rsmd.getColumnClassName(i).substring(10).equalsIgnoreCase("String")) {
					cell.setCellValue(rs.getString(i));
				} else if(rsmd.getColumnClassName(i).substring(10).equalsIgnoreCase("BigDecimal")) {
					BigDecimal x = rs.getBigDecimal(i);
					cell.setCellValue(x.doubleValue());
				} else if(rsmd.getColumnClassName(i).substring(10).equalsIgnoreCase("Integer")) {
					cell.setCellValue(rs.getInt(i));
				} else {
					throw new Exception("Unexpected value format" + rsmd.getColumnClassName(i));
				}
			}
			rowNum++;
			
		}
		
		
		
		workbook.write(new FileOutputStream("/home/jwlewis/Documents/projects/BCR_Spreadsheet.xlsx"));
//		workbook.write(new FileOutputStream("/home/dclewis/Documents/webthing_v2/projects/ANSI/testresults/BCR_Spreadsheet.xlsx"));
//		return workbook;
	}
	
	
}
