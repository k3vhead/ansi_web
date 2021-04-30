package com.ansi.scilla.web.bcr.common;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.time.DateUtils;
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
import com.ansi.scilla.common.utils.AppUtils;
import com.ansi.scilla.common.utils.WorkWeek;
import com.ansi.scilla.common.utils.WorkYear;
import com.ansi.scilla.web.bcr.response.BudgetControlActualDlResponse.ActualDL;
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
		
		List<WorkWeek> workCalendar = makeWorkCalendar(claimYear, weekList);
		BudgetControlTotalsResponse bctr = new BudgetControlTotalsResponse(conn, userId, divisionList, divisionId, claimYear, workWeeks);
		makeBudgetControlTotalsTab(workCalendar, bctr);
		
		conn.close();

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


	private List<WorkWeek> makeWorkCalendar(Integer claimYear, String[] weekList) {
		List<WorkWeek> workCalendar = new ArrayList<WorkWeek>();
		WorkWeekDatePredicate workWeekPredicate = new WorkWeekDatePredicate();
		
		WorkYear workYear = new WorkYear(claimYear);
		Collection<HashMap<String, Object>> values = workYear.values();
		for ( HashMap<String, Object> value : values ) {
			Integer weekOfYear = (Integer)value.get(WorkYear.WEEK_OF_YEAR);
			String matcher = weekOfYear < 10 ? "0" + weekOfYear : String.valueOf(weekOfYear);	
			if ( ArrayUtils.contains(weekList, matcher)) {
				Calendar firstOfWeek = (Calendar)value.get(WorkYear.FIRST_OF_WEEK);
				workWeekPredicate.firstOfWeek = firstOfWeek;
				if ( IterableUtils.countMatches(workCalendar, workWeekPredicate) == 0 ) {
					workCalendar.add(new WorkWeek(firstOfWeek));
				}				
			}
		}

		Collections.sort(workCalendar, new Comparator<WorkWeek>() {
			public int compare(WorkWeek o1, WorkWeek o2) {
				return o1.getFirstOfWeek().compareTo(o2.getFirstOfWeek());
			}
		});
		return workCalendar;
	}


	private void makeBudgetControlTotalsTab(List<WorkWeek> workCalendar, BudgetControlTotalsResponse bctr) {
		List<BCRTotalsDetail> weekTotals = bctr.getWeekTotals();
		BCRTotalsPredicate totalsPredicate = new BCRTotalsPredicate();

		XSSFSheet sheet = this.workbook.createSheet("Monthly Budget Control Summary");
		int rowNum = 0;
		int colNum = 0;
		Integer monthTotalColNum = workCalendar.size() + 2;  //row label + "unclaimed" + each week gives us 2 cells extra
		
		XSSFRow row = null;
		XSSFCell cell = null;
		SimpleDateFormat mmdd = new SimpleDateFormat("MM/dd");
		
		XSSFCellStyle headerStyle = workbook.createCellStyle();
		XSSFFont headerFont = workbook.createFont();
		headerFont.setBold(true);
		headerStyle.setFont(headerFont);
		headerStyle.setAlignment(HorizontalAlignment.LEFT);
		
		XSSFCellStyle headerStyleCenter = workbook.createCellStyle();
		XSSFFont headerCenterFont = workbook.createFont();
		headerCenterFont.setBold(true);
		headerStyleCenter.setFont(headerFont);
		headerStyleCenter.setAlignment(HorizontalAlignment.CENTER);
		
		XSSFCellStyle cellStyleCenter = workbook.createCellStyle();
		cellStyleCenter.setAlignment(HorizontalAlignment.CENTER);
		
		XSSFCellStyle cellStyleRight = workbook.createCellStyle();
		cellStyleRight.setAlignment(CellStyle.ALIGN_RIGHT);
		cellStyleRight.setDataFormat(workbook.createDataFormat().getFormat("#0.00"));

		
		// make Date Row
		colNum = 2;
		row = sheet.createRow(0);
		for ( int i = 0; i < workCalendar.size(); i++ ) {
			cell = row.createCell(i+2);
			Date firstOfWeek = workCalendar.get(i).getFirstOfWeek().getTime();
			Date lastOfWeek = workCalendar.get(i).getLastOfWeek().getTime();
			cell.setCellValue(mmdd.format(firstOfWeek) + "-" + mmdd.format(lastOfWeek));
		}
		cell = row.createCell(workCalendar.size() + 2);
		cell.setCellValue("Month");	
		cell.setCellStyle(headerStyleCenter);
				
				
		// initialize weekly columns
		for ( TotalsRow totalsRow : TotalsRow.values() ) {
			row = sheet.createRow(totalsRow.rowNum);
			cell = row.createCell(0);  // label
			cell.setCellValue(totalsRow.label());
			cell.setCellStyle(headerStyle);
			
			cell = row.createCell(1); // unclaimed			
			if ( totalsRow.equals(TotalsRow.WEEK) ) {
				cell.setCellValue("Unclaimed");
				cell.setCellStyle(headerStyleCenter);
			} else if ( totalsRow.equals(TotalsRow.TOTAL_VOLUME)) {
				cell.setCellValue(0.0D);
				cell.setCellStyle(cellStyleRight);				
			} else {
				cell.setCellValue("n/a");
				cell.setCellStyle(cellStyleCenter);
			}
			for (colNum = 0; colNum < workCalendar.size(); colNum++ ) {
				cell = row.createCell(colNum + 2);
				cell.setCellValue(0D);
				cell.setCellStyle(cellStyleRight);
			}

			cell = row.createCell(monthTotalColNum);

			if ( totalsRow.equals(TotalsRow.WEEK) ) {
				cell.setCellValue("Total");
				cell.setCellStyle(headerStyleCenter);
			} else {
				cell.setCellValue(0.0D);
				cell.setCellStyle(cellStyleRight);
			}
			
		}
		
		// make Weekly columns
		for ( int i = 0; i < workCalendar.size(); i++ ) {
			colNum = i + 2;
			totalsPredicate.workWeek = workCalendar.get(i);
			BCRTotalsDetail detail = IterableUtils.find(weekTotals, totalsPredicate);
			populateTotalsWeek(sheet, colNum, workCalendar.get(i).getWeekOfYear());
			if ( detail != null ) {
				populateTotalsValue(sheet, colNum, TotalsRow.TOTAL_VOLUME, detail.getTotalVolume());
				populateTotalsValue(sheet, colNum, TotalsRow.VOLUME_CLAIMED, detail.getVolumeClaimed());
				populateTotalsValue(sheet, colNum, TotalsRow.CLAIMED_VOLUME_REMAINING, detail.getVolumeRemaining());
				
				populateTotalsValue(sheet, colNum, TotalsRow.TOTAL_BILLED, detail.getBilledAmount());
				populateTotalsValue(sheet, colNum, TotalsRow.VARIANCE, detail.getClaimedVsBilled());
				
				populateTotalsValue(sheet, colNum, TotalsRow.TOTAL_DL_CLAIMED, detail.getDlTotal());

				populateTotalsValue(sheet, colNum, TotalsRow.DL_PERCENTAGE, detail.getDlPercentage());
				populateTotalsValue(sheet, colNum, TotalsRow.ACTUAL_DL_PERCENTAGE, detail.getActualDlPercentage());
			}
			sheet.setColumnWidth(colNum, 3000);
		}
		
		
		// populate monthly column		
		BCRTotalsDetail monthTotal = bctr.getMonthTotal();		
		populateTotalsValue(sheet, monthTotalColNum, TotalsRow.TOTAL_VOLUME, monthTotal.getTotalVolume());
		populateTotalsValue(sheet, monthTotalColNum, TotalsRow.VOLUME_CLAIMED, monthTotal.getVolumeClaimed());
		populateTotalsValue(sheet, monthTotalColNum, TotalsRow.CLAIMED_VOLUME_REMAINING, monthTotal.getVolumeRemaining());
		
		populateTotalsValue(sheet, monthTotalColNum, TotalsRow.TOTAL_BILLED, monthTotal.getBilledAmount());
		populateTotalsValue(sheet, monthTotalColNum, TotalsRow.VARIANCE, monthTotal.getClaimedVsBilled());
		
		populateTotalsValue(sheet, monthTotalColNum, TotalsRow.TOTAL_DL_CLAIMED, monthTotal.getDlTotal());

		populateTotalsValue(sheet, monthTotalColNum, TotalsRow.DL_PERCENTAGE, monthTotal.getDlPercentage());
		populateTotalsValue(sheet, monthTotalColNum, TotalsRow.ACTUAL_DL_PERCENTAGE, monthTotal.getActualDlPercentage());
		
		
		
		
		// populate Totals & Actuals
		HashMap<Integer, ActualDL> actuals = bctr.getActualDl().getWeekActualDL();
		List<Integer> weekNums = IterableUtils.toList(actuals.keySet());
		Collections.sort(weekNums);
		colNum = 2;
		for (Integer weekNum : weekNums ) {
			ActualDL actual = actuals.get(weekNum);
			populateTotalsValue(sheet, colNum, TotalsRow.ACTUAL_DL, actual.getActualDL());
			populateTotalsValue(sheet, colNum, TotalsRow.ACTUAL_OM_DL, actual.getOmDL());
			populateTotalsValue(sheet, colNum, TotalsRow.TOTAL_ACTUAL_DL, actual.getActualDL() + actual.getOmDL());
			colNum++;
		}
		

		// populate budget control panel actual dl totals
		ActualDL totalActualDL = bctr.getActualDl().getTotalActualDL();
		populateTotalsValue(sheet, monthTotalColNum, TotalsRow.ACTUAL_DL, totalActualDL.getActualDL());
		populateTotalsValue(sheet, monthTotalColNum, TotalsRow.ACTUAL_OM_DL, totalActualDL.getOmDL());
		populateTotalsValue(sheet, monthTotalColNum, TotalsRow.TOTAL_ACTUAL_DL, totalActualDL.getTotalDL());

		
		sheet.setColumnWidth(0, 7000);
		sheet.setColumnWidth(1, 3000);
		
	}
	
	private void populateTotalsValue(XSSFSheet sheet, int colNum, TotalsRow totalsRow, Double value) {
		XSSFRow row = sheet.getRow(totalsRow.rowNum());
		XSSFCell cell = row.getCell(colNum);
		
		// set cell format to right-justified, 2 decimals
		cell.setCellValue(value);
//		cell.setCellStyle(cellStyleRight);
	}


	private void populateTotalsWeek(XSSFSheet sheet, int colNum, Integer claimWeek) {
		XSSFRow row = sheet.getRow(TotalsRow.WEEK.rowNum());
		XSSFCell cell = row.getCell(colNum);
		// set cell format to right-justified, bold
		XSSFCellStyle headerStyle = workbook.createCellStyle();
		XSSFFont headerFont = workbook.createFont();
		headerFont.setBold(true);
		headerStyle.setFont(headerFont);
		headerStyle.setAlignment(HorizontalAlignment.RIGHT);
		
		cell.setCellValue(claimWeek);
		cell.setCellStyle(headerStyle);
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
	
	
	/**
	 * Filters by matching first of week.
	 *
	 */
	public class WorkWeekDatePredicate implements Predicate<WorkWeek> {

		public Calendar firstOfWeek;
		
		@Override
		public boolean evaluate(WorkWeek arg0) {
			return DateUtils.isSameDay(firstOfWeek, arg0.getFirstOfWeek());
		}
		
	}
	
	
	public class BCRTotalsPredicate implements Predicate<BCRTotalsDetail> {

		public WorkWeek workWeek;
		
		@Override
		public boolean evaluate(BCRTotalsDetail arg0) {
			String[] cw = arg0.getClaimWeek().split("-");
			return workWeek.getWeekOfYear().equals(Integer.valueOf(cw[1]));
		}
		
	}
	
	private enum TotalsRow {
		WEEK(1, "Week"),
		TOTAL_VOLUME(2, "Total Volume: "),
		VOLUME_CLAIMED(3, "Volume Claimed: "),
		CLAIMED_VOLUME_REMAINING(4, "Claimed Volume Remaining: "),
		TOTAL_BILLED(6, "Total Billed: "),
		VARIANCE(7, "Variance: "),
		TOTAL_DL_CLAIMED(9, "Total D/L Claimed: "),
		ACTUAL_DL(10, "Actual D/L: "),
		ACTUAL_OM_DL(11, "Actual OM D/L: "),
		TOTAL_ACTUAL_DL(12, "Total Actual D/L: "),
		DL_PERCENTAGE(14, "D/L Percentage: "),
		ACTUAL_DL_PERCENTAGE(15, "Actual D/L Percentage: "),
		;
		
		private Integer rowNum;
		private String label;
		
		private TotalsRow(Integer rowNum, String label) {
			this.rowNum = rowNum;
			this.label = label;
		}
		
		public Integer rowNum() { return this.rowNum; }
		public String label() { return this.label; }
	}
}
