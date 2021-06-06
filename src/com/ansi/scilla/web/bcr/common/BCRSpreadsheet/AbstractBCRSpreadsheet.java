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
import org.apache.poi.ss.usermodel.HorizontalAlignment;
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
import com.ansi.scilla.web.bcr.response.BudgetControlActualDlResponse.ActualDL;
import com.ansi.scilla.web.bcr.response.BudgetControlEmployeesResponse;
import com.ansi.scilla.web.bcr.response.BudgetControlEmployeesResponse.EmployeeClaim;
import com.ansi.scilla.web.bcr.response.BudgetControlTotalsResponse;
import com.ansi.scilla.web.bcr.response.BudgetControlTotalsResponse.BCRTotalsDetail;
import com.ansi.scilla.web.common.struts.SessionDivision;

public abstract class AbstractBCRSpreadsheet extends ApplicationObject {

	private static final long serialVersionUID = 1L;

	protected final SimpleDateFormat mmdd = new SimpleDateFormat("MM/dd");
//	private final SimpleDateFormat mmddyyyy = new SimpleDateFormat("MM/dd/yyyy");
	protected final HashMap<CellFormat, XSSFCellStyle> cellFormats = new HashMap<CellFormat, XSSFCellStyle>();
	
	protected XSSFWorkbook workbook;
	
	protected BCRHeader[] headerMap;
	protected Logger logger = LogManager.getLogger(BcrTicketSpreadsheet.class);
	
	public XSSFWorkbook getWorkbook() {
		return workbook;
	}

	protected void makeActualDLTotalsTab(int tabNumber, List<WorkWeek> workCalendar, BudgetControlTotalsResponse bctr) {
		String tabName = "Actual Direct Labor Totals";
		XSSFSheet sheet = this.workbook.createSheet(tabName);
		this.workbook.setSheetOrder(tabName, tabNumber);
		String[] weekLabel = new String[] {"1st","2nd","3rd","4th","5th"};
		String[] columnLabel = new String[] {"Week",null,"Begins","Ends","Actual D/L","OM D/L"};
		short dateFormat = this.workbook.createDataFormat().getFormat("mm/dd/yyyy");
		XSSFCellStyle dateCellStyle =  this.workbook.createCellStyle();
		dateCellStyle.setDataFormat(dateFormat);
		dateCellStyle.setAlignment(HorizontalAlignment.CENTER);
		
		XSSFRow row = null;
		XSSFCell cell = null;
		int rowNum = 0;
		int colNum = 0;
		
		row = sheet.createRow(rowNum);
		for ( int i = 0; i < columnLabel.length; i++ ) {
			if ( ! StringUtils.isBlank(columnLabel[i])) {
				cell = row.createCell(i);
				cell.setCellValue(columnLabel[i]);
				if ( i == 0 ) {
					sheet.addMergedRegion(new CellRangeAddress(rowNum,rowNum,i,i+1));
				}
				cell.setCellStyle(this.cellFormats.get(CellFormat.HEADER_CENTER));
			}
		}
		rowNum++;
		
		for ( WorkWeek value : workCalendar ) {
			colNum = 0;
			row = sheet.createRow(rowNum);
			cell = row.createCell(colNum);
			cell.setCellValue(weekLabel[rowNum - 1] + " Wk in Mo");
			colNum++;
			cell = row.createCell(colNum);
			cell.setCellValue(value.getWeekOfYear());
			cell.setCellStyle(this.cellFormats.get(CellFormat.CENTER));
			colNum++;
			cell = row.createCell(colNum);
			cell.setCellType(XSSFCell.CELL_TYPE_NUMERIC);
			cell.setCellValue(value.getFirstOfWeek());
			cell.setCellStyle(dateCellStyle);
			colNum++;
			cell = row.createCell(colNum);
			cell.setCellType(XSSFCell.CELL_TYPE_NUMERIC);
			cell.setCellValue(value.getLastOfWeek());
			cell.setCellStyle(dateCellStyle);
			colNum++;
			
			ActualDL actualDL = bctr.getActualDl().getWeekActualDL().get(value.getWeekOfYear());
			cell = row.createCell(colNum);			
			cell.setCellValue(actualDL == null ? 0.0D : actualDL.getActualDL());
			cell.setCellStyle(this.cellFormats.get(CellFormat.RIGHT));
			colNum++;
			cell = row.createCell(colNum);
			cell.setCellValue(actualDL == null ? 0.0D : actualDL.getOmDL());
			cell.setCellStyle(this.cellFormats.get(CellFormat.RIGHT));
			colNum++;
			
			rowNum++;
		}
		
		sheet.setColumnWidth(0, 6000);	// wk in month
		sheet.setColumnWidth(1, 2000);	// wk in year
		sheet.setColumnWidth(2, 4000);	// begin date
		sheet.setColumnWidth(3, 4000);	// end date
		sheet.setColumnWidth(4, 3000);	// actual D/L
		sheet.setColumnWidth(5, 3000);	// OM D/L
		
		
	}

	protected void makeBudgetControlTotalsTab(int tabNumber, List<WorkWeek> workCalendar, BudgetControlTotalsResponse bctr) {
		List<BCRTotalsDetail> weekTotals = bctr.getWeekTotals();
		BCRTotalsPredicate totalsPredicate = new BCRTotalsPredicate();

		String tabName = "Monthly Budget Control Summary";
		XSSFSheet sheet = this.workbook.createSheet(tabName);
		this.workbook.setSheetOrder(tabName, tabNumber);
		
//		int rowNum = 0;
		int colNum = 0;
		Integer monthTotalColNum = workCalendar.size() + 2;  //row label + "unclaimed" + each week gives us 2 cells extra
		
		XSSFRow row = null;
		XSSFCell cell = null;
		
		
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
		cell.setCellStyle(cellFormats.get(CellFormat.HEADER_CENTER));
				
				
		// initialize weekly columns
		for ( TotalsRow totalsRow : TotalsRow.values() ) {
			row = sheet.createRow(totalsRow.rowNum());
			cell = row.createCell(0);  // label
			cell.setCellValue(totalsRow.label());
			cell.setCellStyle(cellFormats.get(CellFormat.HEADER));
			
			cell = row.createCell(1); // unclaimed			
			if ( totalsRow.equals(TotalsRow.WEEK) ) {
				cell.setCellValue("Unclaimed");
				cell.setCellStyle(cellFormats.get(CellFormat.HEADER_CENTER));
			} else if ( totalsRow.equals(TotalsRow.TOTAL_VOLUME)) {
				cell.setCellValue(0.0D);
				cell.setCellStyle(cellFormats.get(CellFormat.RIGHT));				
			} else {
				cell.setCellValue("n/a");
				cell.setCellStyle(cellFormats.get(CellFormat.CENTER));
			}
			for (colNum = 0; colNum < workCalendar.size(); colNum++ ) {
				cell = row.createCell(colNum + 2);
				cell.setCellValue(0D);
				cell.setCellStyle(cellFormats.get(CellFormat.RIGHT));
			}

			cell = row.createCell(monthTotalColNum);

			if ( totalsRow.equals(TotalsRow.WEEK) ) {
				cell.setCellValue("Total");
				cell.setCellStyle(cellFormats.get(CellFormat.HEADER_CENTER));
			} else {
				cell.setCellValue(0.0D);
				cell.setCellStyle(cellFormats.get(CellFormat.RIGHT));
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
	
	
	
	protected void makeBudgetControlEmployeesTab(Integer tabNumber, Integer claimYear, List<WorkWeek> workCalendar, BudgetControlEmployeesResponse employeeResponse) {
		String tabName = "Employees";
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
			cell.setCellValue(claimYear + "-" + workCalendar.get(i).getWeekOfYear());
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
				new BCRHeader(BCRRow.class.getField("equipment"),"Equipment",4000, this.cellFormats.get(CellFormat.CENTER)),
				new BCRHeader(BCRRow.class.getField("employee"),"Employee",4500, this.cellFormats.get(CellFormat.LEFT)),
				new BCRHeader(BCRRow.class.getField("claimedEquipment"),"Claimed",4000, this.cellFormats.get(CellFormat.LEFT)),
		};
		
		String tabName = "Unclaimed Equipment";
		XSSFSheet sheet = this.workbook.createSheet(tabName);
		this.workbook.setSheetOrder(tabName, tabNumber);
		XSSFRow row = null;
		XSSFCell cell = null;
		int rowNum = 1;
		
		XSSFRow headerRow1 = sheet.createRow(0);
		for ( int i = 0; i < unclaimedTicketHeader.length; i++ ) {
			cell = headerRow1.createCell(i);
			cell.setCellValue(unclaimedTicketHeader[i].headerName);
			cell.setCellStyle(this.cellFormats.get(CellFormat.HEADER_CENTER));
			sheet.setColumnWidth(i, unclaimedTicketHeader[i].columnWidth);
		}
		
		for ( BCRRow dataRow : data ) {
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
				new BCRHeader(BCRRow.class.getField("equipment"),"Equipment",4000, cellStyleCenter),
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
	}


	protected List<WorkWeek> makeWorkCalendar(Integer claimYear, String[] weekList) {
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
	
	
	protected enum CellFormat {
		HEADER,
		HEADER_CENTER,
		HEADER_CENTER_BORDER,
		CENTER,
		CENTER_BORDER,
		LEFT,
		RIGHT,
		RIGHT_BORDER,
		;		
	}
}
