package com.ansi.scilla.web.test.payroll;

import java.sql.Connection;

import com.ansi.scilla.common.payroll.parser.worksheet.PayrollWorksheetEmployee;
import com.ansi.scilla.common.payroll.parser.worksheet.PayrollWorksheetParser;
import com.ansi.scilla.web.test.common.AbstractTester;

public class TestWorksheetEmployeeUpdate extends AbstractTester {

	private final String fileName = "/home/dclewis/Documents/Dropbox/webthing_v2/projects/ANSI/data/20211211_payroll/payroll_worksheets_v2/Payroll 78 01.21.2022.ods";

	@Override
	protected void go(Connection conn) throws Exception {
		conn.setAutoCommit(false);
		PayrollWorksheetParser parser = new PayrollWorksheetParser(conn, fileName);
		PayrollWorksheetEmployee omar = null;
		for ( PayrollWorksheetEmployee employee : parser.getTimesheetRecords() ) {
			if ( employee.getEmployeeName().equalsIgnoreCase("OMAR DIAZ")) {
				omar = employee;
			}
		}
		if ( omar == null ) {
			throw new Exception("Couldn't find omar");
		}
		
	}

	public static void main(String[] args) {
		new TestWorksheetEmployeeUpdate().run();
	}
}
