package com.ansi.scilla.web.bcr.common;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.web.common.struts.SessionDivision;

public class BcrTicketSpreadsheet {
	private XSSFWorkbook workbook;
	
	private BCRHeader[] headerMap;
	
	public BcrTicketSpreadsheet(Connection conn, List<SessionDivision> divisionList, Integer divisionId, Integer claimYear, String workWeeks) 
			throws Exception {
		this.workbook = new XSSFWorkbook();
		doInit();
		String baseSql = BcrTicketSql.sqlSelectClause + 
				BcrTicketSql.makeFilteredFromClause(divisionList) + 
				BcrTicketSql.makeBaseWhereClause(workWeeks) + 
				"\norder by " + BcrTicketSql.JOB_SITE_NAME;
		System.out.println(baseSql);
		createSpreadsheet(conn, baseSql, divisionList, divisionId, claimYear, workWeeks);
	}
	
	
	public XSSFWorkbook getWorkbook() {
		return workbook;
	}


	private void doInit() {
		XSSFCellStyle cellStyleRight = workbook.createCellStyle();
		cellStyleRight.setAlignment(CellStyle.ALIGN_RIGHT);
		cellStyleRight.setDataFormat(workbook.createDataFormat().getFormat("#0.00"));
		
		XSSFCellStyle cellStyleCenter = workbook.createCellStyle();
		cellStyleCenter.setAlignment(HorizontalAlignment.CENTER);
		
		XSSFCellStyle cellStyleLeft = workbook.createCellStyle();
		cellStyleLeft.setAlignment(HorizontalAlignment.LEFT);
		
		
		this.headerMap = new BCRHeader[] {				
				new BCRHeader("job_site_name","Account",10000, cellStyleLeft),
				new BCRHeader("ticket_id","Ticket Number",3500, cellStyleCenter),
				new BCRHeader("claim_week","Claim Week",3500, cellStyleCenter),
				new BCRHeader("dl_amt","D/L",3000, cellStyleRight),
				new BCRHeader("total_volume","Total Volume",3500, cellStyleRight),
				new BCRHeader("volume_claimed","Volume Claimed",4000, cellStyleRight),
				new BCRHeader("passthru_volume","Expense Volume",4000, cellStyleRight),
				new BCRHeader("volume_remaining","Volume Remaining",4500, cellStyleRight),
				new BCRHeader("notes","Notes",10000, cellStyleLeft),
				new BCRHeader("billed_amount","Billed Amount",3500, cellStyleRight),
				new BCRHeader("claimed_vs_billed","Diff Clm/Bld",3500, cellStyleRight),
				new BCRHeader("ticket_status","Ticket Status",3000, cellStyleRight),
				new BCRHeader("service_tag_id","Service",3000, cellStyleCenter),
				new BCRHeader("equipment_tags","Equipment",3000, cellStyleCenter),
				new BCRHeader("employee","Employee",4500, cellStyleLeft),
				
				
//				new BCRHeader("job_id","Job Id",123D),
//				new BCRHeader("claim_id","Claim Id",123D),
//				new BCRHeader("service_type_id","Service Type Id",123D),
//				new BCRHeader("claim_year","Claim Year",123D),
//				new BCRHeader("dl_expenses","Dl Expenses",123D),
//				new BCRHeader("dl_total","Dl Total",123D),
//				new BCRHeader("passthru_expense_type","PassThru Expense Type",123D),
//				new BCRHeader("claimed_volume_total","Claimed Volume Total",123D),
				
		};		
	}


	private void createSpreadsheet(Connection conn, String sql, List<SessionDivision> divisionList, Integer divisionId, Integer year, String workWeekList) 
			throws Exception {
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setInt(1,  divisionId);
		ps.setInt(2, year);
		ResultSet rs = ps.executeQuery();
		
		makeAllTicketSheet(rs);
		String[] workWeeks = workWeekList.split(",");
		for ( int i = 0; i < workWeeks.length; i++ ) {
			rs.absolute(1);
			makeWeeklySheet(rs, i, year, workWeeks[i]);
		}
		rs.close();
		conn.close();

	}
	
	private void makeWeeklySheet(ResultSet rs, int index, Integer year, String string) {
		XSSFSheet sheet = makeSheet(index, year + "-" + string);		
	}


	private void makeAllTicketSheet(ResultSet rs) throws SQLException, Exception {
		XSSFSheet sheet = makeSheet(0, "BCR Ticket Spreadsheet");

		XSSFRow row = null;
		XSSFCell cell = null;

		Integer rowNum = 1;

		while(rs.next()) {
			int ticketId = rs.getInt("ticket_id");
			if ( ticketId == 845442 || ticketId == 848805 ) {
				Object vr = rs.getObject("volume_remaining");
				System.out.println(ticketId + "\t" + vr.toString() + "\t" + vr.getClass().getCanonicalName() );
			}
			row = sheet.createRow(rowNum);
			for (int colNum = 0; colNum < headerMap.length; colNum++ ) {
				BCRHeader header = headerMap[colNum];
				Object obj = rs.getObject(header.dbField);
				if ( obj == null ) {
					// ignore it and go on with life
				} else if ( obj instanceof String ) {
					String value = (String)obj;
					cell = row.createCell(colNum);
					cell.setCellValue(value);
				} else if ( obj instanceof BigDecimal ) {
					BigDecimal value = (BigDecimal)obj;
					cell = row.createCell(colNum);
					cell.setCellValue(value.doubleValue());
				} else if ( obj instanceof Integer ) {
					Integer value = (Integer)obj;
					cell = row.createCell(colNum);
					cell.setCellValue(value);
				} else {
					throw new Exception("Joshua didn't code this one: " + obj.getClass().getCanonicalName());
				}
				cell.setCellStyle(header.cellStyle);
				sheet.setColumnWidth(colNum, header.columnWidth);
			}
			rowNum++;
		}

	}


	private XSSFSheet makeSheet(Integer index, String title) {
		XSSFSheet sheet = workbook.createSheet();
		workbook.setSheetName(index, title);
		
		XSSFRow row = sheet.createRow(0);
		XSSFCell cell = null;
		
		XSSFCellStyle headerStyle = workbook.createCellStyle();
		XSSFFont headerFont = workbook.createFont();
		headerFont.setBold(true);
		headerStyle.setFont(headerFont);
		headerStyle.setAlignment(HorizontalAlignment.CENTER);
		
		
		for (int colNum = 0; colNum < headerMap.length; colNum++ ) {
			BCRHeader header = headerMap[colNum];
			cell = row.createCell(colNum);
			cell.setCellValue(header.headerName);
			cell.setCellStyle(headerStyle);
			sheet.setColumnWidth(colNum, header.columnWidth);
	
		}		
		
		return sheet;
	}


	public static class BCRHeader extends ApplicationObject {
		private static final long serialVersionUID = 1L;
		public String dbField;
		public String headerName;
		public Integer columnWidth;
		public XSSFCellStyle cellStyle;
		
		public BCRHeader(String dbField, String headerName, Integer columnWidth, XSSFCellStyle cellStyle) {
			super();
			this.dbField = dbField;
			this.headerName = headerName;
			this.columnWidth = columnWidth;
			this.cellStyle = cellStyle;
		}
		
		
	}

}
