package com.ansi.scilla.web.bcr.common;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
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
		int colNum = 1;
		while(!rsmd.getColumnName(colNum).isEmpty()) {
			cell = row.createCell(colNum - 1);
			cell.setCellValue(rsmd.getColumnName(colNum));
			System.out.println(rsmd.getColumnClassName(colNum) + " : " + rsmd.getColumnName(colNum));
			colNum++;
		}
		colNum = 1;
		int rowNum = 1;
		row = sheet.createRow(rowNum);
		while(rs.next()) {
			for(int i = 1; i <= rsmd.getColumnCount(); i++) {
				cell = row.createCell(i - 1);
				if(rsmd.getColumnClassName(colNum).substring(10).equalsIgnoreCase("String")) {
					cell.setCellValue(rs.getString(i));
				} else if(rsmd.getColumnClassName(colNum).substring(10).equalsIgnoreCase("BigDecimal")) {
					BigDecimal x = rs.getBigDecimal(i);
					cell.setCellValue(x.doubleValue());
				} else if(rsmd.getColumnClassName(colNum).substring(10).equalsIgnoreCase("Integer")) {
					cell.setCellValue(rs.getInt(i));
				} else {
					throw new Exception();
				}
			}
			rowNum++;
			
		}
		
		
		
		workbook.write(new FileOutputStream("/Users/jwlewis/Documents/projects/BCR_Spreadsheet.xlsx"));
//		return workbook;
	}
	
	
}
