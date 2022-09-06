package com.ansi.scilla.web.test.payroll;

import java.io.FileNotFoundException;
import java.sql.Connection;

import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.Predicate;

import com.ansi.scilla.common.db.PayrollEmployee;
import com.ansi.scilla.common.payroll.parser.employee.EmployeeImportParser;
import com.ansi.scilla.common.payroll.parser.employee.EmployeeImportRecord;
import com.ansi.scilla.common.utils.AppUtils;
import com.ansi.scilla.web.payroll.response.EmployeeImportResponseRec;

public class TestEmployeeCompare {

	private void go() throws Exception {
		Connection conn = null;
		try {
			conn = AppUtils.getDevConn();
			conn.setAutoCommit(false);
			Integer employeeCode = 1357;
			String fileName = "/home/dclewis/Documents/webthing_v2/projects/ANSI/data/20211211_payroll/payroll_employee/PAYCOM Import Template-NEW-GEN (1).csv";
			
			EmployeeImportRecord csvRecord = makeCsvRecord(conn, fileName, employeeCode); 
//			System.out.println(csvRecord);
			
			EmployeeImportResponseRec respRecord = new EmployeeImportResponseRec(csvRecord);
//			System.out.println(respRecord);
			
			PayrollEmployee dbRecord = makeDbRecord(conn, employeeCode);
//			System.out.println(dbRecord);
			
			
			System.out.println(respRecord.ansiEquals(dbRecord));
		} finally {
			conn.close();
		}
	}

	private EmployeeImportRecord makeCsvRecord(Connection conn, String fileName, Integer employeeCode) throws FileNotFoundException, Exception {
		EmployeeImportParser parser = new EmployeeImportParser(conn, fileName);
		return IterableUtils.find(parser.getEmployeeRecords(), new CSVFilter(String.valueOf(employeeCode)));
	}

	private PayrollEmployee makeDbRecord(Connection conn, Integer employeeCode) throws Exception {
		PayrollEmployee dbRecord = new PayrollEmployee();
		dbRecord.setEmployeeCode(employeeCode);
		dbRecord.selectOne(conn);
		return dbRecord;
	}

	public static void main(String[] args) {
		try {
			new TestEmployeeCompare().go();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	public class CSVFilter implements Predicate<EmployeeImportRecord> {

		private String employeeCode;
		public CSVFilter(String employeeCode) {
			super();
			this.employeeCode = employeeCode;
		}


		@Override
		public boolean evaluate(EmployeeImportRecord arg0) {
			return arg0.getEmployeeCode().equals(this.employeeCode);
		}
		
	}
}
