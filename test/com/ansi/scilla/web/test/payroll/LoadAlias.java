package com.ansi.scilla.web.test.payroll;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.payroll.parser.worksheet.PayrollWorksheetEmployee;
import com.ansi.scilla.common.payroll.parser.worksheet.PayrollWorksheetParser;
import com.ansi.scilla.common.utils.AppUtils;


public class LoadAlias extends Loader {

	
	
	private PreparedStatement employeeCount;
	private PreparedStatement aliasCount;
	private PreparedStatement optionList;
	
	private void go() throws Exception {
		Connection conn = null;
		try {
			conn = AppUtils.getDevConn();
			conn.setAutoCommit(false);
			
			this.employeeCount = conn.prepareStatement("select count(*) as record_count from payroll_employee where lower(employee_first_name)=? and lower(employee_last_name)=?");
			this.aliasCount = conn.prepareStatement("select count(*) as record_count from employee_alias where lower(employee_name)=?");
			this.optionList = conn.prepareStatement("select employee_code, division, employee_first_name, employee_last_name from payroll_employee\n"
					+ "where lower(employee_first_name) in (?,?)\n"
					+ "or lower(employee_last_name) in  (?,?)");
			
			for ( String fileName : this.fileNames ) {
				String[] bits = StringUtils.split(fileName, "/");
				System.out.println("Processing: " + bits[bits.length-1]);
				processFile(conn, fileName);
				System.out.println("\n\n\n");
			}
						
		} catch ( Exception e) {
			conn.rollback();
			throw e;
		} finally {
			conn.close();
		}
	}

	private void processFile(Connection conn, String fileName) throws FileNotFoundException, Exception {
		PayrollWorksheetParser parser = new PayrollWorksheetParser(fileName);
		for ( PayrollWorksheetEmployee employee : parser.getEmployeeRecordList()) {
			if ( StringUtils.isBlank(employee.getEmployeeName()) || (StringUtils.split(employee.getEmployeeName(),",").length < 2 && StringUtils.split(employee.getEmployeeName()," ").length < 2)) {
				System.err.println("Empty name field: [" + employee.getEmployeeName() + "]");
			} else {				
				EmployeeName employeeName = new EmployeeName(StringUtils.trimToNull(employee.getEmployeeName()));
				if ( hasExactMatch(employeeName)) {
					System.out.println("Eact Match: " + employee.getEmployeeName());				
				} else {
					if ( hasAliasMatch(employee.getEmployeeName())) {
						System.out.println("Alias Match: " + employee.getEmployeeName());
					} else {
						List<AliasOption> optionList = makeOptionList(employeeName);
						if ( optionList.isEmpty() ) {
							System.err.println("No Match: " + employee.getEmployeeName());
						} else {
							System.out.println(employee.getEmployeeName());
							for ( AliasOption option : optionList ) {
								System.out.println( "\t" + option);
							}
						}
					}
				}
			}
		}
		
	}

	private boolean hasExactMatch(EmployeeName employeeName) throws SQLException {
		this.employeeCount.setString(1, employeeName.first.toLowerCase());
		this.employeeCount.setString(2, employeeName.last.toLowerCase());
		ResultSet rs = this.employeeCount.executeQuery();
		boolean hasMatch = rs.next() && rs.getInt("record_count") > 0;
		rs.close();
		return hasMatch;
	}

	private boolean hasAliasMatch(String employeeName) throws SQLException {
		String value = StringUtils.trimToEmpty(employeeName.toLowerCase());
		this.aliasCount.setString(1, value);
		ResultSet rs = this.aliasCount.executeQuery();
		boolean hasMatch = rs.next() && rs.getInt("record_count") > 0;		
		rs.close();
		return hasMatch;
	}

	private List<AliasOption> makeOptionList(EmployeeName employeeName) throws SQLException {
		List<AliasOption> optionList = new ArrayList<AliasOption>();
		this.optionList.setString(1, employeeName.first);
		this.optionList.setString(2, employeeName.last);
		this.optionList.setString(3, employeeName.first);
		this.optionList.setString(4, employeeName.last);
		ResultSet rs = this.optionList.executeQuery();
		while ( rs.next() ) {
			optionList.add(new AliasOption(rs));
		}
		rs.close();
		return optionList;
	}

	public static void main(String[] args) {
		try {
			new LoadAlias().go();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private class AliasOption extends ApplicationObject {
		private static final long serialVersionUID = 1L;
		
		public Integer employeeCode;
		public String division;
		public String first;
		public String last;
		public AliasOption(ResultSet rs) throws SQLException {
			super();
			this.employeeCode = rs.getInt("employee_code");
			this.division = rs.getString("division");
			this.first = rs.getString("employee_first_name");
			this.last = rs.getString("employee_last_name");
		}

		@Override
		public String toString() {
			return this.employeeCode + "\t" + this.division + "\t" + this.last + ", " + this.first;
		}
		
	}
}
