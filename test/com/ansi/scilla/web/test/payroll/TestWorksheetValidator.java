package com.ansi.scilla.web.test.payroll;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.time.DateUtils;

import com.ansi.scilla.common.exceptions.InvalidValueException;
import com.ansi.scilla.common.exceptions.PayrollException;
import com.ansi.scilla.common.payroll.parser.worksheet.PayrollWorksheetEmployee;
import com.ansi.scilla.common.payroll.parser.worksheet.PayrollWorksheetHeader;
import com.ansi.scilla.common.payroll.parser.worksheet.PayrollWorksheetParser;
import com.ansi.scilla.common.payroll.validator.common.PayrollMessage;
import com.ansi.scilla.common.payroll.validator.worksheet.HeaderValidator;
import com.ansi.scilla.common.payroll.validator.worksheet.ValidatedWorksheetHeader;
import com.ansi.scilla.common.payroll.validator.worksheet.ValidatedWorksheetHeader.PayrollWorksheetFields;
import com.ansi.scilla.common.utils.AppUtils;
import com.ansi.scilla.web.payroll.request.TimesheetRequest;

public class TestWorksheetValidator {
	private final String fileName = "/home/dclewis/Documents/Dropbox/webthing_v2/projects/ANSI/data/20211211_payroll/payroll_worksheets_v2/Payroll 77 01.21.2022.ods";
//	private final String fileName = "/home/dclewis/Documents/Dropbox/webthing_v2/projects/ANSI/data/20211211_payroll/payroll_worksheets_v2/Weekly Payroll 1-21-22 Admin.ods";


	public static void main(String[] args) {
		try {
			new TestWorksheetValidator().go();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void go() throws Exception {
		Connection conn = null;
		try {
			conn = AppUtils.getDevConn();
			PayrollWorksheetParser parser = new PayrollWorksheetParser(fileName);
			testValidator(conn, parser);
//			System.out.println("**********************************");
//			System.out.println("**********************************");
//			System.out.println("**********************************");
//			testResponseStuff(conn, parser);
//			System.out.println("**********************************");
//			System.out.println("**********************************");
//			System.out.println("**********************************");
//			testCrud(conn, parser);
		} finally {
			conn.close();
		}
	}

	
	
	private void testValidator(Connection conn, PayrollWorksheetParser parser) throws PayrollException, InvalidValueException, SQLException, InterruptedException, Exception {
		
		PayrollWorksheetHeader worksheetHeader = parser.getHeader();
		ValidatedWorksheetHeader header = HeaderValidator.validateHeader(conn, worksheetHeader);
		
		Map<PayrollWorksheetFields, List<PayrollMessage>> validatorMsg = header.getMessages();
		Set<PayrollWorksheetFields> fieldNameList = validatorMsg.keySet();
		for ( PayrollWorksheetFields fieldName : fieldNameList ) {
			System.out.println(fieldName.name());
			for ( PayrollMessage msg : validatorMsg.get(fieldName) ) {
				System.out.println("\t" + msg.getErrorType() + "\t" + msg.getErrorMessage().getErrorLevel() + "\t" + msg.getErrorMessage().getMessage());
			}				
		}
		
		/**
		 * This bit has all changed because messages are now part of the ValidatedWorksheetEmployee object
		 */
//		Map<String,HashMap<String,List<PayrollMessage>>> employeeMsgs =  PayrollWorksheetValidator.validatePayrollEmployees(conn, header, parser);
//		for ( String rownum : employeeMsgs.keySet() ) {
//			System.out.println("Row: " + rownum);
//			for ( String fieldName : employeeMsgs.get(rownum).keySet() ) {
//				System.out.println("\t" + fieldName);
//				for ( PayrollMessage msg : employeeMsgs.get(rownum).get(fieldName) ) {
//					System.out.println("\t\t" + msg.getErrorType() + "\t" + msg.getErrorMessage().getErrorLevel() + "\t" + msg.getErrorMessage().getMessage());
//				}
//			}
//		}
		
	}

//	private void testResponseStuff(Connection conn, PayrollWorksheetParser parser) throws Exception {
//		TimesheetImportResponse response = new TimesheetImportResponse(conn, parser);
//		WebMessagesStatus wms = response.validate(conn);
//		System.out.println(wms.getResponseCode());
//		for ( String fieldName : wms.getWebMessages().keySet()) {
//			System.out.println("[" + fieldName + "]\t" + wms.getWebMessages().get(fieldName).get(0));
//		}
//		for ( PayrollWorksheetEmployee row : response.getEmployeeRecordList() ) {
//			System.out.println("Row: " + row.getRow() + "\t" + row.getErrorsFound());
//		}
//	}

	private void testCrud(Connection conn, PayrollWorksheetParser parser) throws Exception {
		PayrollWorksheetEmployee emp = parser.getTimesheetRecords().get(0);
		TimesheetRequest request = new TimesheetRequest();
		final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		request.setDivisionId(101);
		request.setState("MO");
		request.setWeekEnding(  DateUtils.toCalendar(dateFormat.parse("12/03/2021")));
		request.setEmployeeCode(1144);
		request.setCity("City of St. Louis");
		
		
	}

}
