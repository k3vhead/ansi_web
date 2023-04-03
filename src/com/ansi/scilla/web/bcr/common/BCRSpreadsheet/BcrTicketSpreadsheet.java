package com.ansi.scilla.web.bcr.common.BCRSpreadsheet;

import java.sql.Connection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import com.ansi.scilla.common.utils.WorkWeek;
import com.ansi.scilla.web.bcr.response.BudgetControlActualDlResponse.ActualDL;
import com.ansi.scilla.web.bcr.response.BudgetControlEmployeesResponse;
import com.ansi.scilla.web.bcr.response.BudgetControlEmployeesResponse.EmployeeClaim;
import com.ansi.scilla.web.bcr.response.BudgetControlTotalsResponse;
import com.ansi.scilla.web.bcr.response.BudgetControlTotalsResponse.BCRTotalsDetail;
import com.ansi.scilla.web.common.struts.SessionDivision;

public class BcrTicketSpreadsheet extends AbstractBCRSpreadsheet {
	
	private static final long serialVersionUID = 1L;		

	public BcrTicketSpreadsheet(Connection conn, Integer userId, List<SessionDivision> divisionList, Integer divisionId, Integer claimYear, String workWeeks) 
			throws Exception {
		super(conn, userId, divisionList, divisionId, claimYear, workWeeks);		
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
		
		// Make header row
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
		
		// make data rows
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
	
	@Override
	protected void makeBudgetControlTotalsTab(int tabNumber, List<WorkWeek> workCalendar, BudgetControlTotalsResponse bctr, BudgetControlEmployeesResponse employeeResponse) {
		List<BCRTotalsDetail> weekTotals = bctr.getWeekTotals();
		BCRTotalsPredicate totalsPredicate = new BCRTotalsPredicate();

		String tabName = TabName.MONTHLY_BUDGET_CONTROL_SUMMARY.label(); //"Monthly Budget Control Summary";
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

		Double dlDisplay = monthTotal.getDlPercentage().isInfinite() ? 0.0D : monthTotal.getDlPercentage();
		populateTotalsValue(sheet, monthTotalColNum, TotalsRow.DL_PERCENTAGE, dlDisplay);
		Double actualDlDisplay = monthTotal.getActualDlPercentage().isInfinite() ? 0.0D : monthTotal.getActualDlPercentage();
		populateTotalsValue(sheet, monthTotalColNum, TotalsRow.ACTUAL_DL_PERCENTAGE, actualDlDisplay);
		
		
		
		
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



	
	@Override
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
		cell.setCellValue(ALL_EMPLOYEE_DL_LABEL);
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
	
	
	

	
	
	
	
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
