package com.ansi.scilla.web.test.payroll;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.table.Table;

import com.ansi.scilla.web.payroll.response.TimesheetImportResponse;
import com.ansi.scilla.web.payroll.response.TimesheetRecord;

public class KevinsTest{

	//	private final String filePath = "/home/dclewis/Documents/webthing_v2/projects/ANSI/data/20210711_bcr_spreadsheet_examples/indy/work/content.xml";
	//  private final String filePath = "test/com/ansi/scilla/web/test/bcr/content.xml";
	//private final String filePath1 = "test/com/ansi/scilla/web/test/payroll/data/1/extracted/Payroll 77 09.24.2021/content.xml";
	private final String odsFilePath1 = "test/com/ansi/scilla/web/test/payroll/data/1/ods/Payroll 77 09.24.2021.ods";

	public enum WprCols{
		EMPLOYEE_ROW  	("B")		
		,EmployeeName 	("D")
		,RegularHours 	("F")
		,RegularPay		("H")
		,Expenses 		("J")
		,OTHours 		("L")
		,OTPay 			("N")
		,VacHours 		("P")
		,VacPay			("R")
		,HolHours 		("T")
		,HolPay  		("V")
		,GrossPay 		("X")
		,ExpSubmitted 	("Z")		
		,ExpAllowed 	("AB")
		,Volume 		("AD")
		,DL 			("AF")
		,Prod 			("AH")
		,Division		("L3")
		,OM_Name		("O3")
		,WeekEnding		("X3");
		
		private final String cellLocation;	
		 /**
	     * @param cellLocation
	     */
		private WprCols(final String cellLocation) {
	        this.cellLocation = cellLocation;
	    }

		public String cellLocation() {
			return this.cellLocation;
		}
	   
	}

	public void go() throws Exception {
		File odsFile = new File(odsFilePath1);
//		SpreadsheetDocument oDoc = SpreadsheetDocument.loadDocument(odsFile);
		InputStream inputStream = new FileInputStream(odsFile);
		SpreadsheetDocument oDoc = SpreadsheetDocument.loadDocument(inputStream);
		
		List<Table> oTables = oDoc.getTableList();
				
		/* debug msgs */
		System.out.println("Tables Found " + oTables.size() );
		for(int i=0; i <oTables.size();i++) {
			Table tab = oTables.get(i);	
			System.out.println("Tab Name - " + tab.getTableName().toString());
		}
		/* debug msgs */
		
		Table summary_tab = oTables.get(2);

		/* debug msgs */
		System.out.println("Divsion - " + summary_tab.getCellByPosition("L3").getDisplayText());
		System.out.println("OM Name - " + summary_tab.getCellByPosition("O3").getDisplayText());
		System.out.println("Week Ending - " + summary_tab.getCellByPosition("X3").getDisplayText());
		/* debug msgs */
		
		List<TimesheetRecord> oTimesheetSummaryRows = new ArrayList<TimesheetRecord>();
		
		String cellAddr="";
		for(Integer sdRow=6; sdRow<39;sdRow++) {
			TimesheetRecord timeRecRow = new TimesheetRecord();

			timeRecRow.setEmployeeName(summary_tab.getCellByPosition		(WprCols.EmployeeName	+ sdRow.toString()).getDisplayText());
			timeRecRow.setDirectLabor(summary_tab.getCellByPosition			(WprCols.DL			+ sdRow.toString()).getDisplayText());
			timeRecRow.setExpenses(summary_tab.getCellByPosition			(WprCols.Expenses		+ sdRow.toString()).getDisplayText());
			timeRecRow.setExpensesAllowed(summary_tab.getCellByPosition		(WprCols.ExpAllowed	+ sdRow.toString()).getDisplayText());
			timeRecRow.setExpensesSubmitted(summary_tab.getCellByPosition	(WprCols.ExpSubmitted	+ sdRow.toString()).getDisplayText());
			timeRecRow.setGrossPay(summary_tab.getCellByPosition			(WprCols.GrossPay		+ sdRow.toString()).getDisplayText());
			timeRecRow.setHolidayHours(summary_tab.getCellByPosition		(WprCols.HolPay		+ sdRow.toString()).getDisplayText());
			timeRecRow.setHolidayPay(summary_tab.getCellByPosition			(WprCols.OTPay			+ sdRow.toString()).getDisplayText());
			timeRecRow.setOtHours(summary_tab.getCellByPosition				(WprCols.OTHours		+ sdRow.toString()).getDisplayText());
			timeRecRow.setOtPay(summary_tab.getCellByPosition				(WprCols.OTPay			+ sdRow.toString()).getDisplayText());
			timeRecRow.setProductivity(summary_tab.getCellByPosition		(WprCols.Prod 			+ sdRow.toString()).getDisplayText());
			timeRecRow.setRegularHours(summary_tab.getCellByPosition		(WprCols.RegularHours	+ sdRow.toString()).getDisplayText());
			timeRecRow.setRegularPay(summary_tab.getCellByPosition			(WprCols.RegularPay	+ sdRow.toString()).getDisplayText());
			//TimeRecRow.setState(smry.getCellByPosition					(wpr_cols.EmployeeName+sdRow.toString()).getDisplayText());
			timeRecRow.setVacationHours(summary_tab.getCellByPosition		(WprCols.VacHours		+ sdRow.toString()).getDisplayText());
			timeRecRow.setVacationPay(summary_tab.getCellByPosition			(WprCols.VacPay		+ sdRow.toString()).getDisplayText());
			timeRecRow.setVolume(summary_tab.getCellByPosition				(WprCols.Volume		+ sdRow.toString()).getDisplayText());
			
			oTimesheetSummaryRows.add(timeRecRow);
			
			/* debug msgs... 
			System.out.println("Employee Name for Row " + sdRow + " is " + TimeRecRow.getEmployeeName());
			System.out.println("Volume for Row " + sdRow + " is " + TimeRecRow.getVolume());
			*/
		}
		
		TimesheetImportResponse oTimeSheetImportResponse = new TimesheetImportResponse();
		oTimeSheetImportResponse.setEmployeeRecordList(oTimesheetSummaryRows);

		// don't see any place to set division, week_ending or om_name
		// oTimeSheetImportResponse.
		//smry.getCellByPosition		(wpr_cols.EmployeeName	+ sdRow.toString()).getDisplayText());		
		
		/* debug msgs */
		System.out.println("Rows stored = " + oTimesheetSummaryRows.size());
		/* debug msgs */

	}

	public static void main(String[] args) {
		System.out.println(new Date());
		try {
			Connection conn = null;
			InputStream inputStream = null;
//			new KevinsTest().go();
			TimesheetImportResponse x = new TimesheetImportResponse(conn, inputStream);
			System.out.println(x);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("end");
	}	
}






