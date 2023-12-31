package com.ansi.scilla.web.test.payroll;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import com.ansi.scilla.common.payroll.parser.worksheet.PayrollWorksheetEmployee;
import com.ansi.scilla.common.payroll.parser.worksheet.PayrollWorksheetParser;
import com.ansi.scilla.common.payroll.validator.common.ValidatorUtils;
import com.ansi.scilla.common.payroll.validator.worksheet.HeaderValidator;
import com.ansi.scilla.common.payroll.validator.worksheet.ValidatedWorksheetEmployee;
import com.ansi.scilla.common.payroll.validator.worksheet.ValidatedWorksheetHeader;
import com.ansi.scilla.common.utils.AppUtils;
import com.ansi.scilla.common.utils.ErrorLevel;
import com.ansi.scilla.web.payroll.response.TimesheetImportResponse;

public class TestWorksheetImportResponse {
	private final String fileName = "/home/dclewis/Documents/Dropbox/webthing_v2/projects/ANSI/data/20211211_payroll/payroll_worksheets_v2/Payroll 78 01.21.2022.ods";

	public static void main(String[] args) {
		try {
			new TestWorksheetImportResponse().go();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void go() throws Exception {
		Connection conn = null;
		TimesheetImportResponse data = new TimesheetImportResponse();
		
		try {
			conn = AppUtils.getDevConn();
			conn.setAutoCommit(false);
			
			List<ValidatedWorksheetEmployee> validatedEmployees = new ArrayList<ValidatedWorksheetEmployee>();

			PayrollWorksheetParser parser = new PayrollWorksheetParser(conn, fileName);
//			PayrollWorksheetParser parser = new PayrollWorksheetParser(requestFile.getName(), requestFile.getInputStream());
			
			ValidatedWorksheetHeader header = HeaderValidator.validateHeader(conn, parser.getHeader());
			
			if ( ! header.maxErrorLevel().equals(ErrorLevel.ERROR)) {
				validatedEmployees = ValidatorUtils.validatePayrollEmployees(conn, header, parser.getTimesheetRecords());
			}
			
			PayrollWorksheetEmployee before = parser.getTimesheetRecords().get(0);
			for ( ValidatedWorksheetEmployee after : validatedEmployees ) {
				String json = AppUtils.object2json(after, true);
				System.out.println(json);
				System.out.println("***************");
			}
			

//			ValidatedWorksheet validatedWorksheet = new ValidatedWorksheet(header, validatedEmployees);
//			data = new TimesheetImportResponse(conn, parser.getFileName(), validatedWorksheet);			
//			String json = AppUtils.object2json(data, true);
//			System.out.println(json);
		} finally {
			conn.rollback();
			conn.close();
		}
		
	}

}
