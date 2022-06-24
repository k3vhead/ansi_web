package com.ansi.scilla.web.test.payroll;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections4.IterableUtils;

import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.common.db.PayrollEmployee;
import com.ansi.scilla.common.payroll.common.EmployeeStatus;
import com.ansi.scilla.common.payroll.parser.EmployeeImportParser;
import com.ansi.scilla.common.payroll.parser.EmployeeImportRecord;
import com.ansi.scilla.common.utils.compare.BooleanIshComparison;
import com.ansi.scilla.common.utils.compare.IntComparison;
import com.ansi.scilla.common.utils.compare.String2DateComparison;
import com.ansi.scilla.common.utils.compare.String2IntComparison;
import com.ansi.scilla.common.utils.compare.String2NumberComparison;
import com.ansi.scilla.common.utils.compare.StringComparison;
import com.ansi.scilla.web.payroll.response.EmployeeImportResponseRec;
import com.ansi.scilla.web.payroll.response.EmployeeRecordTransformer;
import com.ansi.scilla.web.test.common.AbstractTester;

public class TestEmployeeComparison extends AbstractTester {

	private final String employeeFileName = "/home/dclewis/Documents/Dropbox/webthing_v2/projects/ANSI/data/20211211_payroll/payroll_employee/PAYCOM_1_update.csv";

	public static void main(String[] args) {
		new TestEmployeeComparison().run();
	}

	
	@Override
	protected void go(Connection conn) throws Exception {
		EmployeeImportParser parser = new EmployeeImportParser(conn, employeeFileName);
		List<EmployeeImportRecord> employeeRecords = parser.getEmployeeRecords();	

		HashMap<Integer, PayrollEmployee> employeeMap = makeEmployeeMap(conn);
		List<Division> divisionList = Division.cast( new Division().selectAll(conn) );
		HashMap<Integer, Division> divMap = new HashMap<Integer, Division>();
		for ( Division d : divisionList ) {
			divMap.put(d.getDivisionNbr(), d );
		}
		
		EmployeeStatus[] employeeStatusList = EmployeeStatus.values();
		HashMap<String, EmployeeStatus> employeeStatusMap = new HashMap<String, EmployeeStatus>();
		for ( EmployeeStatus s : employeeStatusList ) {
			employeeStatusMap.put(s.display(), s );
		}

		EmployeeRecordTransformer betterTransformer = new EmployeeRecordTransformer(employeeMap, divMap, employeeStatusMap);
		List<EmployeeImportResponseRec> matchedRecords = IterableUtils.toList(IterableUtils.transformedIterable(employeeRecords, betterTransformer));

		StringComparison stringComp = new StringComparison(false);
		BooleanIshComparison boolComp = new BooleanIshComparison();
		String2IntComparison s2i = new String2IntComparison();
		IntComparison i2iComp = new IntComparison();
		String2DateComparison s2dComp = new String2DateComparison(EmployeeImportRecord.EMPLOYEE_RECORD_DATE_FORMAT);
		String2NumberComparison s2nComp = new String2NumberComparison();
		
		
		for (EmployeeImportResponseRec importEmp : matchedRecords ) {
			PayrollEmployee pe = employeeMap.get(Integer.valueOf(importEmp.getEmployeeCode()));
			System.out.println("1.\t" + importEmp.getFirstName() + "\t" + pe.getEmployeeFirstName() + "\t" + stringComp.fieldsAreEqual(importEmp.getFirstName(), pe.getEmployeeFirstName()));
			System.out.println("2.\t" + importEmp.getLastName() + "\t" + pe.getEmployeeLastName() + "\t" + stringComp.fieldsAreEqual(importEmp.getLastName(), pe.getEmployeeLastName()));
			System.out.println("3.\t" + importEmp.getStatus() + "\t" + pe.getEmployeeStatus() + "\t" + stringComp.fieldsAreEqual(importEmp.getStatus(), pe.getEmployeeStatus()));
			System.out.println("4.\t" + importEmp.getUnionMember() + "\t" + pe.getUnionMember() + "\t" + boolComp.fieldsAreEqual(importEmp.getUnionMember(), pe.getUnionMember()));
			
			System.out.println("5.\t" + importEmp.getEmployeeCode() + "\t" + pe.getEmployeeCode() + "\t" + s2i.fieldsAreEqual(importEmp.getEmployeeCode(), pe.getEmployeeCode()));
			System.out.println("6.\t" + importEmp.getCompanyCode() + "\t" + pe.getCompanyCode() + "\t" + stringComp.fieldsAreEqual(importEmp.getCompanyCode(), pe.getCompanyCode()));
			System.out.println("7.\t" + importEmp.getDivisionNbr() + "\t" + pe.getDivision() + "\t" + stringComp.fieldsAreEqual(importEmp.getDivisionNbr(), pe.getDivision()));
			System.out.println("8.\t" + importEmp.getDepartmentDescription() + "\t" + pe.getDeptDescription() + "\t" + stringComp.fieldsAreEqual(importEmp.getDepartmentDescription(), pe.getDeptDescription()));
			System.out.println("9.\t[" + importEmp.getUnionCode() + "]\t[" + pe.getUnionCode() + "]\t" + stringComp.fieldsAreEqual(importEmp.getUnionCode(), pe.getUnionCode()));
			System.out.println("10.\t[" + importEmp.getDivisionId() + "]\t[" + pe.getDivisionId() + "]\t" + i2iComp.fieldsAreEqual(importEmp.getDivisionId(), pe.getDivisionId()));
			System.out.println("11.\t[" + importEmp.getNotes() + "]\t[" + pe.getNotes() + "]\t" + stringComp.fieldsAreEqual(importEmp.getNotes(), pe.getNotes()));
			System.out.println("12.\t[" + importEmp.getTerminationDate() + "]\t[" + pe.getEmployeeTerminationDate() + "]\t" + s2dComp.fieldsAreEqual(importEmp.getTerminationDate(), pe.getEmployeeTerminationDate()));
			System.out.println("13.\t[" + importEmp.getUnionRate() + "]\t[" + pe.getUnionRate() + "]\t" + s2nComp.fieldsAreEqual(importEmp.getUnionRate(), pe.getUnionRate()));
			System.out.println("****************** " + importEmp.ansiEquals(pe));
		}
		
//		
//		for ( EmployeeImportResponseRec emp : matchedRecords ) {
//			System.out.println(emp.getLastName() + " " + emp.getRecordMatches());
//		}
	}


	private HashMap<Integer, PayrollEmployee> makeEmployeeMap(Connection conn) throws Exception {
		HashMap<Integer, PayrollEmployee> employeeMap = new HashMap<Integer, PayrollEmployee>();
		List<PayrollEmployee> employeeList = PayrollEmployee.cast(new PayrollEmployee().selectAll(conn));
		for ( PayrollEmployee employee : employeeList ) {
			employeeMap.put(employee.getEmployeeCode(), employee);
		}
		return employeeMap;
	}



/*
	private void compareMe() {
		new AnsiComparison("firstName", "employeeFirstName", new StringComparison(false)),	
		new AnsiComparison("lastName", "employeeLastName", new StringComparison(false)),
		new AnsiComparison("status", "employeeStatus", new StringComparison(false)),
		new AnsiComparison("unionMember", "unionMember", new BooleanIshComparison()),


		new AnsiComparison("employeeCode","employeeCode", new String2IntComparison()),
		new AnsiComparison("companyCode","companyCode", new StringComparison(false)),
		new AnsiComparison("divisionNbr","division", new StringComparison(false)),
		new AnsiComparison("departmentDescription","deptDescription", new StringComparison(false)),
		new AnsiComparison("unionCode","unionCode", new StringComparison(false)),
		new AnsiComparison("divisionId","divisionId", new IntComparison()),
		new AnsiComparison("notes","notes", new StringComparison(false)),
		new AnsiComparison("terminationDate","employeeTerminationDate", new String2DateComparison(EmployeeImportRecord.EMPLOYEE_RECORD_DATE_FORMAT)),
		new AnsiComparison("unionRate","unionRate", new String2NumberComparison()),
	}
*/

	

}
