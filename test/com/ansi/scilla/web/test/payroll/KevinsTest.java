package com.ansi.scilla.web.test.payroll;

import java.io.File;
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

	public enum wpr_cols{
		EmployeeRow  	("B")		
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
		
		private final String text;	
		 /**
	     * @param text
	     */
		wpr_cols(final String text) {
	        this.text = text;
	    }

	    /* (non-Javadoc)
	     * @see java.lang.Enum#toString()
	     */
	    @Override
	    public String toString() {
	        return text;
	    }
	}

	public void go() throws Exception {
		File odsFile = new File(odsFilePath1);
		SpreadsheetDocument oDoc = SpreadsheetDocument.loadDocument(odsFile);
		
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
		System.out.println("Divsion - " + smry.getCellByPosition("L3").getDisplayText());
		System.out.println("OM Name - " + smry.getCellByPosition("O3").getDisplayText());
		System.out.println("Week Ending - " + smry.getCellByPosition("X3").getDisplayText());
		/* debug msgs */
		
		List<TimesheetRecord> oTimesheetSummaryRows = new ArrayList<TimesheetRecord>();
		
		String CellAddr="";
		for(Integer sdRow=6; sdRow<39;sdRow++) {
			TimesheetRecord TimeRecRow = new TimesheetRecord();

			TimeRecRow.setEmployeeName(summary_tab.getCellByPosition		(wpr_cols.EmployeeName	+ sdRow.toString()).getDisplayText());
			TimeRecRow.setDirectLabor(summary_tab.getCellByPosition			(wpr_cols.DL			+ sdRow.toString()).getDisplayText());
			TimeRecRow.setExpenses(summary_tab.getCellByPosition			(wpr_cols.Expenses		+ sdRow.toString()).getDisplayText());
			TimeRecRow.setExpensesAllowed(summary_tab.getCellByPosition		(wpr_cols.ExpAllowed	+ sdRow.toString()).getDisplayText());
			TimeRecRow.setExpensesSubmitted(summary_tab.getCellByPosition	(wpr_cols.ExpSubmitted	+ sdRow.toString()).getDisplayText());
			TimeRecRow.setGrossPay(summary_tab.getCellByPosition			(wpr_cols.GrossPay		+ sdRow.toString()).getDisplayText());
			TimeRecRow.setHolidayHours(summary_tab.getCellByPosition		(wpr_cols.HolPay		+ sdRow.toString()).getDisplayText());
			TimeRecRow.setHolidayPay(summary_tab.getCellByPosition			(wpr_cols.OTPay			+ sdRow.toString()).getDisplayText());
			TimeRecRow.setOtHours(summary_tab.getCellByPosition				(wpr_cols.OTHours		+ sdRow.toString()).getDisplayText());
			TimeRecRow.setOtPay(summary_tab.getCellByPosition				(wpr_cols.OTPay			+ sdRow.toString()).getDisplayText());
			TimeRecRow.setProductivity(summary_tab.getCellByPosition		(wpr_cols.Prod 			+ sdRow.toString()).getDisplayText());
			TimeRecRow.setRegularHours(summary_tab.getCellByPosition		(wpr_cols.RegularHours	+ sdRow.toString()).getDisplayText());
			TimeRecRow.setRegularPay(summary_tab.getCellByPosition			(wpr_cols.RegularPay	+ sdRow.toString()).getDisplayText());
			//TimeRecRow.setState(smry.getCellByPosition					(wpr_cols.EmployeeName+sdRow.toString()).getDisplayText());
			TimeRecRow.setVacationHours(summary_tab.getCellByPosition		(wpr_cols.VacHours		+ sdRow.toString()).getDisplayText());
			TimeRecRow.setVacationPay(summary_tab.getCellByPosition			(wpr_cols.VacPay		+ sdRow.toString()).getDisplayText());
			TimeRecRow.setVolume(summary_tab.getCellByPosition				(wpr_cols.Volume		+ sdRow.toString()).getDisplayText());
			
			oTimesheetSummaryRows.add(TimeRecRow);
			
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
			new KevinsTest().go();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("end");
	}	
}






