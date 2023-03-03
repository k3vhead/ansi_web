package com.ansi.scilla.web.bcr.common.BCRSpreadsheet;

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

import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.utils.WorkWeek;
import com.ansi.scilla.common.utils.WorkYear;
import com.ansi.scilla.web.bcr.common.BcrTicketSql;
import com.ansi.scilla.web.bcr.response.BudgetControlEmployeesResponse;
import com.ansi.scilla.web.bcr.response.BudgetControlEmployeesResponse.EmployeeClaim;
import com.ansi.scilla.web.bcr.response.BudgetControlTotalsResponse;
import com.ansi.scilla.web.common.struts.SessionDivision;

public abstract class AbstractBCRSpreadsheet extends ApplicationObject {

	private static final long serialVersionUID = 1L;

	protected final SimpleDateFormat mmdd = new SimpleDateFormat("MM/dd");
//	private final SimpleDateFormat mmddyyyy = new SimpleDateFormat("MM/dd/yyyy");
	protected final HashMap<CellFormat, XSSFCellStyle> cellFormats = new HashMap<CellFormat, XSSFCellStyle>();
	
	protected XSSFWorkbook workbook;
	
	protected BCRHeader[] headerMap;
	protected Logger logger = LogManager.getLogger(BcrTicketSpreadsheet.class);
	
	public AbstractBCRSpreadsheet(Connection conn, Integer userId, List<SessionDivision> divisionList,
			Integer divisionId, Integer claimYear, String workWeeks) throws Exception {
		
		
		List<BCRRow> data = makeData(conn, divisionList, divisionId, claimYear, workWeeks);
		String[] weekList = workWeeks.split(",");
		
		this.workbook = new XSSFWorkbook();
		makeStyles();
		initWorkbook();
		BCRRowTabPredicate filter = new BCRRowTabPredicate();	
		List<WorkWeek> workCalendar = makeWorkCalendar(claimYear, weekList);
		int tabNumber = 0;
		
		BudgetControlTotalsResponse bctr = new BudgetControlTotalsResponse(conn, userId, divisionList, divisionId, claimYear, workWeeks);
		makeActualDLTotalsTab(tabNumber, workCalendar, bctr);
		tabNumber++;
		
		makeBudgetControlTotalsTab(tabNumber, workCalendar, bctr);
		tabNumber++;
		
		BudgetControlEmployeesResponse employeeResponse = new BudgetControlEmployeesResponse(conn, userId, divisionList, divisionId, claimYear, workWeeks);
		makeBudgetControlEmployeesTab(tabNumber, claimYear, workCalendar, employeeResponse);
		tabNumber++;
		
		conn.close();

		makeTicketTab(data, tabNumber, TabName.ALL_TICKETS.label());
		tabNumber++;
		for ( int i = 0; i < weekList.length; i++ ) {
			String weekNum = Integer.valueOf(weekList[i]) < 10 ? "0" + weekList[i] : weekList[i];
			String tabName = claimYear + "-" + weekNum;
			filter.setTabName(tabName);
			List<BCRRow> weeklyData = IterableUtils.toList(IterableUtils.filteredIterable(data, filter));
			makeTicketTab(weeklyData, tabNumber, tabName);
			tabNumber++;
		}
		
		
		makeUnclaimedEquipmentTab(data, tabNumber);

	}

	public XSSFWorkbook getWorkbook() {
		return workbook;
	}

	protected abstract void makeActualDLTotalsTab(int tabNumber, List<WorkWeek> workCalendar, BudgetControlTotalsResponse bctr);
	
	protected abstract void makeBudgetControlTotalsTab(int tabNumber, List<WorkWeek> workCalendar, BudgetControlTotalsResponse bctr);
	
		
	
	
	protected void makeBudgetControlEmployeesTab(Integer tabNumber, Integer claimYear, List<WorkWeek> workCalendar, BudgetControlEmployeesResponse employeeResponse) {
		String tabName = TabName.EMPLOYEES.label(); //"Employees";
		XSSFSheet sheet = this.workbook.createSheet(tabName);
		this.workbook.setSheetOrder(tabName, tabNumber);
		XSSFRow row = null;
		XSSFCell cell = null;
		int rowNum = 0;
		int colNum = 0;
		
		XSSFRow headerRow1 = sheet.createRow(0);
		XSSFRow headerRow2 = sheet.createRow(1);
		XSSFRow headerRow3 = sheet.createRow(2);

		cell = headerRow1.createCell(0);
		cell.setCellValue("Week:");
		cell.setCellStyle(cellFormats.get(CellFormat.HEADER));
		
		
		colNum = 1;
		for ( int i = 0; i < workCalendar.size(); i++ ) {
			Date firstOfWeek = workCalendar.get(i).getFirstOfWeek().getTime();
			Date lastOfWeek = workCalendar.get(i).getLastOfWeek().getTime();
			
			cell = headerRow1.createCell(colNum+1);
			cell.setCellStyle(cellFormats.get(CellFormat.CENTER_BORDER));
			cell = headerRow1.createCell(colNum);
			sheet.addMergedRegion(new CellRangeAddress(0,0,colNum,colNum+1));
			cell.setCellValue(mmdd.format(firstOfWeek) + "-" + mmdd.format(lastOfWeek));
			cell.setCellStyle(cellFormats.get(CellFormat.CENTER_BORDER));
			
			
			cell = headerRow2.createCell(colNum+1);
			cell.setCellStyle(cellFormats.get(CellFormat.CENTER_BORDER));
			cell = headerRow2.createCell(colNum);
			sheet.addMergedRegion(new CellRangeAddress(1,1,colNum,colNum+1));
			String weekOfYear = workCalendar.get(i).getWeekOfYear() < 10 ? "0" + workCalendar.get(i).getWeekOfYear() : String.valueOf(workCalendar.get(i).getWeekOfYear());
			cell.setCellValue(claimYear + "-" + weekOfYear);
			cell.setCellStyle(cellFormats.get(CellFormat.HEADER_CENTER_BORDER));
			
			cell = headerRow3.createCell(colNum);
			cell.setCellValue("Volume");
			cell.setCellStyle(cellFormats.get(CellFormat.HEADER_CENTER));
			cell = headerRow3.createCell(colNum+1);
			cell.setCellValue("D/L");
			cell.setCellStyle(cellFormats.get(CellFormat.HEADER_CENTER_BORDER));
			
			colNum = colNum+2;
		}
		
		cell = headerRow1.createCell(colNum);
		sheet.addMergedRegion(new CellRangeAddress(0,0,colNum,colNum+1));
		cell.setCellValue("Month");
		cell.setCellStyle(cellFormats.get(CellFormat.HEADER_CENTER));
		
		cell = headerRow2.createCell(colNum);
		sheet.addMergedRegion(new CellRangeAddress(1,1,colNum,colNum+1));
		cell.setCellValue("Total");
		cell.setCellStyle(cellFormats.get(CellFormat.HEADER_CENTER));
		
		cell = headerRow3.createCell(colNum);
		cell.setCellValue("Volume");
		cell.setCellStyle(cellFormats.get(CellFormat.HEADER_CENTER));
		cell = headerRow3.createCell(colNum+1);
		cell.setCellValue("D/L");
		cell.setCellStyle(cellFormats.get(CellFormat.HEADER_CENTER));
		
		
		rowNum = 3;
		
		for ( EmployeeClaim claim : employeeResponse.getEmployees() ) {
			row = sheet.createRow(rowNum);
			cell = row.createCell(0);
			cell.setCellValue(StringUtils.isBlank(claim.getEmployee()) ? "unspecified" : claim.getEmployee());
			
			colNum = 1;
			for ( String claimWeek : employeeResponse.getClaimWeeks() ) {
				cell = row.createCell(colNum);
				if ( claim.getWeeklyClaimedVolume().containsKey(claimWeek)) {
					cell.setCellValue(claim.getWeeklyClaimedVolume().get(claimWeek));
				} else {
					cell.setCellValue(0.0D);
				}
				cell.setCellStyle(cellFormats.get(CellFormat.RIGHT));
				colNum++;
				cell = row.createCell(colNum);
				if ( claim.getWeeklyClaimedDL().containsKey(claimWeek)) {
					cell.setCellValue(claim.getWeeklyClaimedDL().get(claimWeek));
				} else {
					cell.setCellValue(0.0D);
				}
				cell.setCellStyle(cellFormats.get(CellFormat.RIGHT_BORDER));
				colNum++;
			}
			cell = row.createCell(colNum);
			cell.setCellValue(claim.getTotalClaimedVolume());
			cell.setCellStyle(cellFormats.get(CellFormat.RIGHT));
			colNum++;
			cell = row.createCell(colNum);
			cell.setCellValue(claim.getTotalClaimedDL());
			cell.setCellStyle(cellFormats.get(CellFormat.RIGHT));
			colNum++;
			rowNum++;
		}
		
		row = sheet.createRow(rowNum);
		cell = row.createCell(0);
		cell.setCellValue("Total Assigned D/L - All Employees");
		colNum = 1;
		for ( String claimWeek : employeeResponse.getClaimWeeks() ) {
			cell = row.createCell(colNum);
			if ( employeeResponse.getMonthlyTotal().getWeeklyClaimedVolume().containsKey(claimWeek)) {
				cell.setCellValue(employeeResponse.getMonthlyTotal().getWeeklyClaimedVolume().get(claimWeek));
			} else {
				cell.setCellValue(0.0D);
			}
			cell.setCellStyle(cellFormats.get(CellFormat.RIGHT));
			colNum++;
			cell = row.createCell(colNum);
			if ( employeeResponse.getMonthlyTotal().getWeeklyClaimedDL().containsKey(claimWeek)) {
				cell.setCellValue(employeeResponse.getMonthlyTotal().getWeeklyClaimedDL().get(claimWeek));
			} else {
				cell.setCellValue(0.0D);
			}
			cell.setCellStyle(cellFormats.get(CellFormat.RIGHT_BORDER));
			colNum++;
		}
		cell = row.createCell(colNum);
		cell.setCellValue(employeeResponse.getMonthlyTotal().getTotalClaimedVolume());
		cell.setCellStyle(cellFormats.get(CellFormat.RIGHT));
		colNum++;
		cell = row.createCell(colNum);
		cell.setCellValue(employeeResponse.getMonthlyTotal().getTotalClaimedDL());
		cell.setCellStyle(cellFormats.get(CellFormat.RIGHT));
		colNum++;

		
		sheet.setColumnWidth(0, 7500);
		for ( int i = 1; i <= sheet.getRow(0).getLastCellNum(); i++ ) {
			sheet.setColumnWidth(i, 2500);
		}
	}


	protected void makeTicketTab(List<BCRRow> data, Integer index, String title) throws SQLException, Exception {
		XSSFSheet sheet = initTicketTab(index, title);
	
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


	protected void makeUnclaimedEquipmentTab(List<BCRRow> data, int tabNumber) throws Exception {
		BCRHeader[] unclaimedTicketHeader = new BCRHeader[] {				
				new BCRHeader(BCRRow.class.getField("jobSiteName"),"Account",10000, this.cellFormats.get(CellFormat.LEFT)),
				new BCRHeader(BCRRow.class.getField("ticketId"),"Ticket Number",3500, this.cellFormats.get(CellFormat.CENTER)),
				new BCRHeader(BCRRow.class.getField("claimWeek"),"Claim Week",3500, this.cellFormats.get(CellFormat.CENTER)),
				new BCRHeader(BCRRow.class.getField("notes"),"Notes",10000, this.cellFormats.get(CellFormat.LEFT)),
//				new BCRHeader(BCRRow.class.getField("equipment"),"Equipment",4000, this.cellFormats.get(CellFormat.LEFT)),
				new BCRHeader(BCRRow.class.getField("service"),"Service",3000, this.cellFormats.get(CellFormat.CENTER)),
				new BCRHeader(BCRRow.class.getField("claimedEquipment"),"Claimed",4000, this.cellFormats.get(CellFormat.LEFT)),
				new BCRHeader(BCRRow.class.getField("unclaimedEquipment"),"Unclaimed",4000, this.cellFormats.get(CellFormat.LEFT)),
				new BCRHeader(BCRRow.class.getField("employee"),"Employee",4500, this.cellFormats.get(CellFormat.LEFT)),
		};
		
		String tabName = "Unclaimed Equipment";
		XSSFSheet sheet = this.workbook.createSheet(tabName);
		this.workbook.setSheetOrder(tabName, tabNumber);
		XSSFRow row = null;
		XSSFCell cell = null;
		int rowNum = 1;
		List<BCRRow> unclaimedEquipmentData = IterableUtils.toList(IterableUtils.filteredIterable(data, new BCRRowUnclaimedEquipmentPredicate()));
		
		XSSFRow headerRow1 = sheet.createRow(0);
		for ( int i = 0; i < unclaimedTicketHeader.length; i++ ) {
			cell = headerRow1.createCell(i);
			cell.setCellValue(unclaimedTicketHeader[i].headerName);
			cell.setCellStyle(this.cellFormats.get(CellFormat.HEADER_CENTER));
			sheet.setColumnWidth(i, unclaimedTicketHeader[i].columnWidth);
		}
		
		for ( BCRRow dataRow : unclaimedEquipmentData ) {
			row = sheet.createRow(rowNum);
			for (int colNum = 0; colNum < unclaimedTicketHeader.length; colNum++ ) {
				BCRHeader header = unclaimedTicketHeader[colNum];
				Object obj = header.field.get(dataRow);
				cell = row.createCell(colNum);
				cell.setCellStyle(header.cellStyle);
				sheet.setColumnWidth(colNum, header.columnWidth);
				if ( obj == null ) {
					// ignore it and go on with life
				} else if ( obj instanceof String ) {
					String value = (String)obj;
					cell.setCellValue(value);
				} else if ( obj instanceof BigDecimal ) {
					BigDecimal value = (BigDecimal)obj;
					cell.setCellValue(value.doubleValue());
				} else if ( obj instanceof Double ) {
					Double value = (Double)obj;
					cell.setCellValue(value);
				} else if ( obj instanceof Integer ) {
					Integer value = (Integer)obj;
					cell.setCellValue(value);
				} else {
					throw new Exception("Joshua didn't code this one: " + obj.getClass().getCanonicalName());
				}	
			}
			rowNum++;
		}
	}


	protected XSSFSheet initTicketTab(Integer index, String title) {
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


	protected void populateTotalsValue(XSSFSheet sheet, int colNum, TotalsRow totalsRow, Double value) {
		XSSFRow row = sheet.getRow(totalsRow.rowNum());
		XSSFCell cell = row.getCell(colNum);
		
		// set cell format to right-justified, 2 decimals
		cell.setCellValue(value);
//		cell.setCellStyle(cellStyleRight);
	}


	protected void populateTotalsWeek(XSSFSheet sheet, int colNum, Integer claimWeek) {
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


	


	protected List<BCRRow> makeData(Connection conn, List<SessionDivision> divisionList, Integer divisionId, Integer claimYear, String workWeeks) throws SQLException {
		String baseSql = BcrTicketSql.sqlSelectClause + 
				BcrTicketSql.makeFilteredFromClause(divisionList) + 
				BcrTicketSql.makeBaseWhereClause(workWeeks) + 
				"\norder by " + BcrTicketSql.JOB_SITE_NAME;
//		logger.log(Level.DEBUG, baseSql);
		
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


	protected void initWorkbook() throws NoSuchFieldException, SecurityException {

		XSSFCellStyle cellStyleLeft = this.cellFormats.get(CellFormat.LEFT);
		XSSFCellStyle cellStyleCenter = this.cellFormats.get(CellFormat.CENTER);
		XSSFCellStyle cellStyleRight = this.cellFormats.get(CellFormat.RIGHT);
		
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
				new BCRHeader(BCRRow.class.getField("equipment"),"Equipment",4000, cellStyleLeft),
				new BCRHeader(BCRRow.class.getField("employee"),"Employee",4500, cellStyleLeft),
		};		
	}


	
	protected void makeStyles() {
		XSSFCellStyle headerStyle = workbook.createCellStyle();
		XSSFFont headerFont = workbook.createFont();
		headerFont.setBold(true);
		headerStyle.setFont(headerFont);
		headerStyle.setAlignment(HorizontalAlignment.LEFT);
		this.cellFormats.put(CellFormat.HEADER, headerStyle);
		
		XSSFCellStyle headerStyleCenter = workbook.createCellStyle();
		XSSFFont headerCenterFont = workbook.createFont();
		headerCenterFont.setBold(true);
		headerStyleCenter.setFont(headerFont);
		headerStyleCenter.setAlignment(HorizontalAlignment.CENTER);
		this.cellFormats.put(CellFormat.HEADER_CENTER, headerStyleCenter);
		
		XSSFCellStyle headerStyleCenterBorder = workbook.createCellStyle();
		XSSFFont headerCenterBorderFont = workbook.createFont();
		headerCenterBorderFont.setBold(true);
		headerStyleCenterBorder.setFont(headerCenterBorderFont);
		headerStyleCenterBorder.setAlignment(HorizontalAlignment.CENTER);
		headerStyleCenterBorder.setBorderRight(BorderStyle.MEDIUM);
		this.cellFormats.put(CellFormat.HEADER_CENTER_BORDER, headerStyleCenterBorder);
		
		XSSFCellStyle cellStyleCenter = workbook.createCellStyle();
		cellStyleCenter.setAlignment(HorizontalAlignment.CENTER);
		this.cellFormats.put(CellFormat.CENTER, cellStyleCenter);
		
		XSSFCellStyle cellStyleCenterBorder = workbook.createCellStyle();
		cellStyleCenterBorder.setAlignment(HorizontalAlignment.CENTER);
		cellStyleCenterBorder.setBorderRight(BorderStyle.MEDIUM);
		this.cellFormats.put(CellFormat.CENTER_BORDER, cellStyleCenterBorder);
		
		XSSFCellStyle cellStyleLeft = workbook.createCellStyle();
		cellStyleLeft.setAlignment(CellStyle.ALIGN_LEFT);
		this.cellFormats.put(CellFormat.LEFT, cellStyleLeft);
		
		XSSFCellStyle cellStyleRight = workbook.createCellStyle();
		cellStyleRight.setAlignment(CellStyle.ALIGN_RIGHT);
		cellStyleRight.setDataFormat(workbook.createDataFormat().getFormat("#0.00"));
		this.cellFormats.put(CellFormat.RIGHT, cellStyleRight);
		
		XSSFCellStyle cellStyleRightBorder = workbook.createCellStyle();
		cellStyleRightBorder.setAlignment(CellStyle.ALIGN_RIGHT);
		cellStyleRightBorder.setDataFormat(workbook.createDataFormat().getFormat("#0.00"));
		cellStyleRightBorder.setBorderRight(BorderStyle.MEDIUM);
		this.cellFormats.put(CellFormat.RIGHT_BORDER, cellStyleRightBorder);
		
		XSSFCellStyle yellowBackgroundNumeric = this.workbook.createCellStyle();
		yellowBackgroundNumeric.setFillForegroundColor(IndexedColors.YELLOW.index);
		yellowBackgroundNumeric.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		yellowBackgroundNumeric.setDataFormat(workbook.createDataFormat().getFormat("#0.00"));
		this.cellFormats.put(CellFormat.YELLOW_BACKGROUND_NUMERIC, yellowBackgroundNumeric);
	}


	protected List<WorkWeek> makeWorkCalendar(Integer claimYear, String[] weekList) {
		List<WorkWeek> workCalendar = new ArrayList<WorkWeek>();
		WorkWeekDatePredicate workWeekPredicate = new WorkWeekDatePredicate();
		
		WorkYear workYear = new WorkYear(claimYear);
		Collection<HashMap<String, Object>> values = workYear.values();
		for ( HashMap<String, Object> value : values ) {
			Integer weekOfYear = (Integer)value.get(WorkYear.WEEK_OF_YEAR);
//			String matcher = weekOfYear < 10 ? "0" + weekOfYear : String.valueOf(weekOfYear);	
			String matcher = String.valueOf(weekOfYear);
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
	
	
	protected enum CellFormat {
		HEADER,
		HEADER_CENTER,
		HEADER_CENTER_BORDER,
		CENTER,
		CENTER_BORDER,
		LEFT,
		RIGHT,
		RIGHT_BORDER,
		YELLOW_BACKGROUND_NUMERIC,
		;		
	}
	
	protected enum TabName {
		ACTUAL_DIRECT_LABOR_TOTALS("Actual Direct Labor Totals"),
		MONTHLY_BUDGET_CONTROL_SUMMARY("Monthly Budget Control Summary"),
		EMPLOYEES("Employees"),
		ALL_TICKETS("All Tickets"),
		;
		private String label;
		private TabName(String label) {this.label = label;}
		public String label() { return this.label; }
		
	}
}
