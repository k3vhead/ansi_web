package com.ansi.scilla.web.test.payroll;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections4.IterableUtils;

import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.common.db.PayrollEmployee;
import com.ansi.scilla.common.payroll.common.EmployeeStatus;
import com.ansi.scilla.common.payroll.parser.employee.EmployeeImportParser;
import com.ansi.scilla.common.payroll.parser.employee.EmployeeImportRecord;
import com.ansi.scilla.common.utils.AppUtils;
import com.ansi.scilla.web.payroll.response.EmployeeImportResponseRec;
import com.ansi.scilla.web.payroll.response.EmployeeRecordTransformer;
import com.ansi.scilla.web.payroll.servlet.EmployeeImportServlet;

public class TestEmployeeValidation extends EmployeeImportServlet {

	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		try {
			new TestEmployeeValidation().go();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void go() throws Exception {
		Connection conn = null;
		try {
			conn = AppUtils.getDevConn();
			conn.setAutoCommit(false);
			
			HashMap<Integer, Division> divMap = makeDivMap(conn);
			HashMap<String, EmployeeStatus> employeeStatusMap = makeEmployeeStatusMap();
			HashMap<Integer, PayrollEmployee> employeeMap = makeEmployeeMap(conn);
			
			String fileName = "/home/dclewis/Documents/Dropbox/webthing_v2/projects/ANSI/data/20211211_payroll/payroll_employee/term_date_check.csv";
			InputStream inputStream = new FileInputStream( new File(fileName) );
			
			
			EmployeeImportParser parser = new EmployeeImportParser(conn, fileName, inputStream);
			List<EmployeeImportRecord> employeeRecords = parser.getEmployeeRecords();	
			EmployeeRecordTransformer betterTransformer = new EmployeeRecordTransformer(employeeMap, divMap, employeeStatusMap);
			List<EmployeeImportResponseRec> matchedRecords = IterableUtils.toList(IterableUtils.transformedIterable(employeeRecords, betterTransformer));
			for ( EmployeeImportResponseRec rec : matchedRecords ) {
				System.out.println(rec.getEmployeeCode() + "\t" + rec.getLastName() + "\t" + rec.getRecordMatches());
			}
		} finally {
			conn.close();
		}
		
	}
}
