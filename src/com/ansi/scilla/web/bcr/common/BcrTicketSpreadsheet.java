package com.ansi.scilla.web.bcr.common;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.web.bcr.response.BudgetControlTotalsResponse;
import com.ansi.scilla.web.bcr.response.BudgetControlTotalsResponse.BCRTotalsDetail;
import com.ansi.scilla.web.common.struts.SessionDivision;

public class BcrTicketSpreadsheet {
	private XSSFWorkbook workbook;
	
	private BCRHeader[] headerMap;
	private Logger logger = LogManager.getLogger(BcrTicketSpreadsheet.class);
	
	public BcrTicketSpreadsheet(Connection conn, Integer userId, List<SessionDivision> divisionList, Integer divisionId, Integer claimYear, String workWeeks) 
			throws Exception {
		super();		
		List<BCRRow> data = makeData(conn, divisionList, divisionId, claimYear, workWeeks);
		String[] weekList = workWeeks.split(",");
		
		this.workbook = new XSSFWorkbook();
		initWorkbook();
		BCRRowPredicate filter = new BCRRowPredicate();
		
		BudgetControlTotalsResponse bctr = new BudgetControlTotalsResponse(conn, userId, divisionList, divisionId, claimYear, weekList[0]);
		makeBudgetControlTotalsTab(bctr);
		
		
		makeSheet(data, 1, "All Tickets");
		for ( int i = 0; i < weekList.length; i++ ) {
			String tabName = claimYear + "-" + weekList[i];
			filter.setTabName(tabName);
			List<BCRRow> weeklyData = IterableUtils.toList(IterableUtils.filteredIterable(data, filter));
			makeSheet(weeklyData, i+2, tabName);
		}
		
		
	}
	
	
	public XSSFWorkbook getWorkbook() {
		return workbook;
	}


	private void makeBudgetControlTotalsTab(BudgetControlTotalsResponse bctr) {
		List<BCRTotalsDetail> weekTotals = bctr.getWeekTotals();

		XSSFSheet bctrSheet = this.workbook.createSheet("Monthly Budget Control Summary");
		int rowNum = 0;
		int colNum = 0;
		
		XSSFRow row = null;
		XSSFCell cell = null;
		
		for(rowNum = 0; rowNum < getBctrLabels().size(); rowNum++) {
			row = bctrSheet.createRow(rowNum);
			cell = row.createCell(colNum);
			cell.setCellValue(getBctrLabels().get(rowNum));
		}
		colNum++;
		
		for(rowNum = 0; rowNum < weekTotals.size(); rowNum++) {
			row = bctrSheet.createRow(rowNum);
			cell = row.createCell(colNum);
			cell.setCellValue(weekTotals.get(rowNum).toString());
		}
		colNum++;
		
		
	}
	
	private List<String> getBctrLabels() {
		List<String> bctrLabels = new ArrayList<String>();
		bctrLabels.add("Week: ");
		bctrLabels.add("Total Volume: ");
		bctrLabels.add("Volume Claimed: ");
		bctrLabels.add("Claimed Volume Remaining: ");
		bctrLabels.add("Total Billed: ");
		bctrLabels.add("Variance: ");
		bctrLabels.add("Total D/L Claimed: ");
		bctrLabels.add("Actual D/L: ");
		bctrLabels.add("Actual OM D/L: ");
		bctrLabels.add("Total Actual D/L: ");
		bctrLabels.add("D/L Percentage: ");
		bctrLabels.add("Actual D/L Percentage: ");
		return bctrLabels;
	}


	private List<BCRRow> makeData(Connection conn, List<SessionDivision> divisionList, Integer divisionId, Integer claimYear, String workWeeks) throws SQLException {
		String baseSql = BcrTicketSql.sqlSelectClause + 
				BcrTicketSql.makeFilteredFromClause(divisionList) + 
				BcrTicketSql.makeBaseWhereClause(workWeeks) + 
				"\norder by " + BcrTicketSql.JOB_SITE_NAME;
		logger.log(Level.DEBUG, baseSql);
		
		List<BCRRow> data = new ArrayList<BCRRow>();
		PreparedStatement ps = conn.prepareStatement(baseSql);
		ps.setInt(1,  divisionId);
		ps.setInt(2, claimYear);
		
		ResultSet rs = ps.executeQuery();
		while ( rs.next() ) {
			data.add(new BCRRow(rs));
		}
		
		rs.close();
		conn.close();
		return data;
	}


	private void initWorkbook() throws NoSuchFieldException, SecurityException {
		XSSFCellStyle cellStyleRight = workbook.createCellStyle();
		cellStyleRight.setAlignment(CellStyle.ALIGN_RIGHT);
		cellStyleRight.setDataFormat(workbook.createDataFormat().getFormat("#0.00"));
		
		XSSFCellStyle cellStyleCenter = workbook.createCellStyle();
		cellStyleCenter.setAlignment(HorizontalAlignment.CENTER);
		
		XSSFCellStyle cellStyleLeft = workbook.createCellStyle();
		cellStyleLeft.setAlignment(HorizontalAlignment.LEFT);
		
		
		this.headerMap = new BCRHeader[] {				
				new BCRHeader(BCRRow.class.getField("jobSiteName"),"Account",10000, cellStyleLeft),
				new BCRHeader(BCRRow.class.getField("ticketId"),"Ticket Number",3500, cellStyleCenter),
				new BCRHeader(BCRRow.class.getField("claimWeek"),"Claim Week",3500, cellStyleCenter),
				new BCRHeader(BCRRow.class.getField("dlAmt"),"D/L",3000, cellStyleRight),
				new BCRHeader(BCRRow.class.getField("totalVolume"),"Total Volume",3500, cellStyleRight),
				new BCRHeader(BCRRow.class.getField("volumeClaimed"),"Volume Claimed",4000, cellStyleRight),
				new BCRHeader(BCRRow.class.getField("expenseVolume"),"Expense Volume",4000, cellStyleRight),
				new BCRHeader(BCRRow.class.getField("volumeRemaining"),"Volume Remaining",4500, cellStyleRight),
				new BCRHeader(BCRRow.class.getField("notes"),"Notes",10000, cellStyleLeft),
				new BCRHeader(BCRRow.class.getField("billedAmount"),"Billed Amount",3500, cellStyleRight),
				new BCRHeader(BCRRow.class.getField("diffClmBld"),"Diff Clm/Bld",3500, cellStyleRight),
				new BCRHeader(BCRRow.class.getField("ticketStatus"),"Ticket Status",3000, cellStyleCenter),
				new BCRHeader(BCRRow.class.getField("service"),"Service",3000, cellStyleCenter),
				new BCRHeader(BCRRow.class.getField("equipment"),"Equipment",3000, cellStyleCenter),
				new BCRHeader(BCRRow.class.getField("employee"),"Employee",4500, cellStyleLeft),
		};		
	}


	
	
	

	private void makeSheet(List<BCRRow> data, Integer index, String title) throws SQLException, Exception {
		XSSFSheet sheet = initSheet(index, title);

		XSSFRow row = null;
		XSSFCell cell = null;

		Integer rowNum = 1;

		for ( BCRRow dataRow : data ) {		
			row = sheet.createRow(rowNum);
			for (int colNum = 0; colNum < headerMap.length; colNum++ ) {
				BCRHeader header = headerMap[colNum];
				Object obj = header.field.get(dataRow);
				if ( obj == null ) {
					// ignore it and go on with life
				} else if ( obj instanceof String ) {
					String value = (String)obj;
					cell = row.createCell(colNum);
					cell.setCellStyle(header.cellStyle);
					sheet.setColumnWidth(colNum, header.columnWidth);
					cell.setCellValue(value);
				} else if ( obj instanceof BigDecimal ) {
					BigDecimal value = (BigDecimal)obj;
					cell = row.createCell(colNum);
					cell.setCellStyle(header.cellStyle);
					sheet.setColumnWidth(colNum, header.columnWidth);
					cell.setCellValue(value.doubleValue());
				} else if ( obj instanceof Double ) {
					Double value = (Double)obj;
					cell = row.createCell(colNum);
					cell.setCellStyle(header.cellStyle);
					sheet.setColumnWidth(colNum, header.columnWidth);
					cell.setCellValue(value);
				} else if ( obj instanceof Integer ) {
					Integer value = (Integer)obj;
					cell = row.createCell(colNum);
					cell.setCellStyle(header.cellStyle);
					sheet.setColumnWidth(colNum, header.columnWidth);
					cell.setCellValue(value);
				} else {
					throw new Exception("Joshua didn't code this one: " + obj.getClass().getCanonicalName());
				}				
			}
			rowNum++;
		}

	}


	private XSSFSheet initSheet(Integer index, String title) {
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
	
	/**
	 * Creates BCR Totals Tab
	 */
//	public static class BCRTotals {
//		BudgetControlTotalsResponse bctr = new BudgetControlTotalsResponse(conn, userId, divisionList, divisionId, workYear, workWeek);
//		List<BCRTotalsDetail> weekTotals = bctr.getWeekTotals();
//		
//	}
	
	/**
	 * Repreasents one column of the spreadsheet tab. 
	 * The Field is the BCRRow source for the data
	 *
	 */
	public static class BCRHeader extends ApplicationObject {
		private static final long serialVersionUID = 1L;
		public Field field;
		public String headerName;
		public Integer columnWidth;
		public XSSFCellStyle cellStyle;
		
		public BCRHeader(Field field, String headerName, Integer columnWidth, XSSFCellStyle cellStyle) {
			super();
			this.field = field;
			this.headerName = headerName;
			this.columnWidth = columnWidth;
			this.cellStyle = cellStyle;
		}
	}

	/**
	 * Represents one row of a spreadsheet tab, equivalent to one row from the database query
	 *
	 */
	public class BCRRow extends ApplicationObject {
		private static final long serialVersionUID = 1L;
		public String jobSiteName; 
		public Integer ticketId;
		public String claimWeek;
		public Double dlAmt;
		public Double totalVolume;
		public Double volumeClaimed;
		public Double expenseVolume;
		public Double volumeRemaining;
		public String notes;
		public Double billedAmount;
		public Double diffClmBld;
		public String ticketStatus;
		public String service;
		public String equipment;
		public String employee;
		
		public BCRRow(ResultSet rs) throws SQLException {
			super();
			this.jobSiteName = rs.getString("job_site_name");
			this.ticketId = rs.getInt("ticket_id");
			this.claimWeek = rs.getString("claim_week");
			this.dlAmt = rs.getDouble("dl_amt");
			this.totalVolume = rs.getDouble("total_volume");
			this.volumeClaimed = rs.getDouble("volume_claimed");
			this.expenseVolume = rs.getDouble("passthru_volume");
			this.volumeRemaining = rs.getDouble("volume_remaining");
			this.notes = rs.getString("notes");
			this.billedAmount = rs.getDouble("billed_amount");
			this.diffClmBld = rs.getDouble("claimed_vs_billed");
			this.ticketStatus = rs.getString("ticket_status");
			this.service = rs.getString("service_tag_id");
			this.equipment = rs.getString("equipment_tags");
			this.employee = rs.getString("employee");
		}
	}
	
	/**
	 * Filters by claim week. Used to create week-specific tabs in the spreadsheet
	 *
	 */
	public class BCRRowPredicate implements Predicate<BCRRow> {
		private String tabName;
		
		public void setTabName(String tabName) {
			this.tabName = tabName;
		}

		@Override
		public boolean evaluate(BCRRow arg0) {
			return arg0.claimWeek.equals(tabName);
		}
		
	}
}
