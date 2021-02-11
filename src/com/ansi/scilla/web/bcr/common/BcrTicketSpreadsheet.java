package com.ansi.scilla.web.bcr.common;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
	 */
	
	public BcrTicketSpreadsheet() {
		//this.BcrTicketSpreadsheet(conn, year, weeks);
	}
	
	public BcrTicketSpreadsheet(Connection conn, List<SessionDivision> divisionList, Integer divisionId, Integer claimYear, String workWeeks) 
			throws FileNotFoundException, IOException, SQLException {
//		String sql = BcrTicketSql.makeBaseWhereClause(workWeeks);
		String baseSql = BcrTicketSql.sqlSelectClause + BcrTicketSql.makeFilteredFromClause(divisionList) + BcrTicketSql.makeBaseWhereClause(workWeeks);
		System.out.println(baseSql);
		createSpreadsheet(conn, baseSql, divisionList, divisionId, claimYear, workWeeks);
	}
	
	private void createSpreadsheet(Connection conn, String sql, List<SessionDivision> divisionList, Integer divisionId, Integer year, String workWeekList) 
			throws SQLException, FileNotFoundException, IOException {
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
		row = sheet.createRow(0);
		int n = 0;
		while(rs.next()) {
			cell = row.createCell(n);
			cell.setCellValue(rsmd.getColumnClassName(n));
			n++;
		}
		
		
		
		workbook.write(new FileOutputStream("/Users/dlewis/Documents/projects/pinpoint/google_cat/catdump_20131203.xlsx"));
//		return workbook;
	}
	
	
}
