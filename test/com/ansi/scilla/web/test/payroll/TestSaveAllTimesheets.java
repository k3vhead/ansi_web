package com.ansi.scilla.web.test.payroll;

import java.sql.Connection;
import java.util.Iterator;

import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.payroll.common.PayrollValidation;
import com.ansi.scilla.web.payroll.request.TimesheetRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestSaveAllTimesheets {

	private String jsonString = "{\"employeeList\":[{\"row\":1,\"weekEnding\":\"2022-09-16\",\"employeeCode\":1929,\"employeeName\":\"ANTJUAN COLEMAN\",\"regularHours\":31,\"regularPay\":840,\"expenses\":10,\"otHours\":0,\"otPay\":0,\"vacationHours\":0,\"vacationPay\":0,\"holidayHours\":0,\"holidayPay\":0,\"grossPay\":850,\"expensesSubmitted\":10,\"expensesAllowed\":10,\"volume\":3172,\"directLabor\":850,\"productivity\":26.8,\"divisionId\":117,\"state\":\"TN\"},{\"row\":2,\"weekEnding\":\"2022-09-16\",\"employeeCode\":1884,\"employeeName\":\"MICHAEL LEMONS\",\"regularHours\":22.5,\"regularPay\":640,\"expenses\":10,\"otHours\":0,\"otPay\":0,\"vacationHours\":0,\"vacationPay\":0,\"holidayHours\":0,\"holidayPay\":0,\"grossPay\":650,\"expensesSubmitted\":10,\"expensesAllowed\":10,\"volume\":2383,\"directLabor\":650,\"productivity\":27.28,\"divisionId\":117,\"state\":\"TN\"},{\"row\":3,\"weekEnding\":\"2022-09-16\",\"employeeCode\":1491,\"employeeName\":\"PHILIP MARTIN\",\"regularHours\":0,\"regularPay\":0,\"expenses\":0,\"otHours\":0,\"otPay\":0,\"vacationHours\":0,\"vacationPay\":0,\"holidayHours\":0,\"holidayPay\":0,\"grossPay\":0,\"expensesSubmitted\":0,\"expensesAllowed\":0,\"volume\":0,\"directLabor\":0,\"productivity\":0,\"divisionId\":117,\"state\":\"TN\"},{\"row\":4,\"weekEnding\":\"2022-09-16\",\"employeeCode\":1928,\"employeeName\":\"Adrian Ortiz\",\"regularHours\":0,\"regularPay\":0,\"expenses\":0,\"otHours\":0,\"otPay\":0,\"vacationHours\":0,\"vacationPay\":0,\"holidayHours\":0,\"holidayPay\":0,\"grossPay\":0,\"expensesSubmitted\":0,\"expensesAllowed\":0,\"volume\":0,\"directLabor\":0,\"productivity\":0,\"divisionId\":117,\"state\":\"TN\"},{\"row\":5,\"weekEnding\":\"2022-09-16\",\"employeeCode\":1974,\"employeeName\":\"SHAWN WHITT\",\"regularHours\":0,\"regularPay\":0,\"expenses\":0,\"otHours\":0,\"otPay\":0,\"vacationHours\":0,\"vacationPay\":0,\"holidayHours\":0,\"holidayPay\":0,\"grossPay\":0,\"expensesSubmitted\":0,\"expensesAllowed\":0,\"volume\":0,\"directLabor\":0,\"productivity\":0,\"divisionId\":117,\"state\":\"TN\"}]}";
	
	
	public static void main(String[] args) {
		try {
			new TestSaveAllTimesheets().go();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void go() throws Exception {
		Connection conn = null;
		try {
			conn = AppUtils.getDevConn();
			conn.setAutoCommit(false);
			doStuff(conn);
		} finally {
			conn.rollback();
			conn.close();
		}
	}
	
	private void doStuff(Connection conn) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readTree(jsonString);
		JsonNode employeeListNode = rootNode.get("employeeList");
		Iterator<JsonNode> nodeIterator = employeeListNode.elements();
		while ( nodeIterator.hasNext() ) {
			String employee = nodeIterator.next().toString();
//			System.out.println(node.toString());
			
			TimesheetRequest timesheetRequest = new TimesheetRequest();
			AppUtils.json2object(employee, timesheetRequest);
			System.out.println(timesheetRequest);
			PayrollValidation payrollValidation = timesheetRequest.validateAdd(conn);
			System.out.println("Errors: " + payrollValidation.getWebMessages().size() );
			System.out.println("*****************");
		}
	}

}
