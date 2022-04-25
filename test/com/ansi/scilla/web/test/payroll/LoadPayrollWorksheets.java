package com.ansi.scilla.web.test.payroll;

import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.common.AnsiTime;
import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.db.PayrollWorksheet;
import com.ansi.scilla.common.payroll.parser.PayrollWorksheetEmployee;
import com.ansi.scilla.common.payroll.parser.PayrollWorksheetParser;
import com.ansi.scilla.common.utils.AppUtils;

public class LoadPayrollWorksheets extends Loader {

	private final Boolean commitAnyway = true;
//	private final int fileNumber = 5;
	
	private HashMap<Integer, String> defaultState = new HashMap<Integer, String>();
	private HashMap<Integer, String> defaultCity = new HashMap<Integer, String>();
	private List<String> unmatched = new ArrayList<String>();
	private final DecimalFormat productivityFormat = new DecimalFormat("###.00%");
	private final SimpleDateFormat weekEndingFormat = new SimpleDateFormat("MM/dd/yy");
	
	
	
	
	public LoadPayrollWorksheets() {
		super();
		this.defaultState.put(101, "IL");				
		this.defaultState.put(102, "IL");				
		this.defaultState.put(103, "IL");				
		this.defaultState.put(104, "IL");				
		this.defaultState.put(105, "IL");				
		this.defaultState.put(106, "IN");				
		this.defaultState.put(107, "IN");				
		this.defaultState.put(108, "IN");				
		this.defaultState.put(109, "MO");				
		this.defaultState.put(110, "OH");				
		this.defaultState.put(111, "KY");				
		this.defaultState.put(112, "OH");				
		this.defaultState.put(113, "PA");				
		this.defaultState.put(114, "PA");				
		this.defaultState.put(115, "OH");				
		this.defaultState.put(116, "OH");				
		this.defaultState.put(118, "AR");				
		this.defaultState.put(117, "TN");
		
		this.defaultCity.put(103, "Bloomington");				
		this.defaultCity.put(104, "Bloomington");				
		this.defaultCity.put(105, "Chicago");				
		this.defaultCity.put(107, "Indianapolis");				
		this.defaultCity.put(109, "St. Louis");				
		this.defaultCity.put(110, "Cincinnati");				
		this.defaultCity.put(112, "Columbus");				
		this.defaultCity.put(113, "Pittsburgh");				
		this.defaultCity.put(114, "Pittsburgh");				
		this.defaultCity.put(115, "Cleveland");				
		this.defaultCity.put(116, "Cleveland");				
		this.defaultCity.put(117, "Memphis");
		throw new RuntimeException("This program won't work. Not updated for new payroll_worksheet/locale FK");

	}



	public void go() throws Exception {
		Connection conn = null;
		try {
			conn = AppUtils.getDevConn();
			conn.setAutoCommit(false);
			makeDivMap(conn);			
			Date now = Calendar.getInstance(new AnsiTime()).getTime();

			PreparedStatement washerSql = conn.prepareStatement("select * from (\n"
					+ "	select payroll_employee.employee_code, payroll_employee.division,payroll_employee.employee_first_name, payroll_employee.employee_last_name , employee_alias.employee_name as alias\n"
					+ "	from payroll_employee\n"
					+ "	left outer join employee_alias on employee_alias.employee_code =payroll_employee.employee_code\n"
					+ "	) washer\n"
					+ "where (lower(washer.employee_first_name)=? and lower(washer.employee_last_name)=?)\n"
					+ "	or lower(alias)=?\n"
					+ "order by washer.employee_last_name, washer.employee_first_name, washer.alias");
			

			for ( int fileNumber = 0; fileNumber < this.fileNames.length; fileNumber++ ) {
				System.out.println("Files to process: " + fileNumber + " of " + this.fileNames.length);
				processFile(conn, this.fileNames[fileNumber], washerSql, now);				
				if ( this.unmatched.size() == 0 || commitAnyway == true) {
					System.err.println("Committing");
					conn.commit();
				} else {
					System.err.println("Rolling back");
					conn.rollback();
				}
			}

			
		}
		catch ( Exception e) {
			conn.rollback();
			throw e;
		} finally {
			conn.close();
		}
	}
	

	
	private void processFile(Connection conn, String fileName, PreparedStatement washerSql, Date now) throws FileNotFoundException, Exception {
		String[] bits = fileName.split("/");
		System.out.println("Processing file: " + bits[bits.length-1]);
		PayrollWorksheetParser parser = new PayrollWorksheetParser(fileName);
		String file = parser.getFileName();
		Integer divNbr = Integer.valueOf( parser.getDivision() );
		String omName = parser.getOperationsManagerName();
		Date weekEnding = weekEndingFormat.parse(parser.getWeekEnding());
		Integer divisionId = divMap.get(divNbr).getDivisionId();
		String city = makeCity(divisionId, parser.getCity());
		String state = makeState(divisionId, parser.getState());
		List<PayrollWorksheetEmployee> employeeList = parser.getTimesheetRecords();
		System.out.println("[" + file + "]\t[" + divNbr + "]\t[" + city + "]\t[" + state + "]\t[" + omName + "]\t[" + weekEnding + "]");
		for ( PayrollWorksheetEmployee employee : employeeList ) {
			List<Washer> washerList = makeWasherList(washerSql, employee);
			if ( washerList.isEmpty() || washerList.size() > 1 ) {
				System.out.println("Skipping: " + employee.getEmployeeName());
				this.unmatched.add(employee.getEmployeeName());
			} else {			
				Washer washer = washerList.get(0);
				System.out.println("Processing: " + employee.getEmployeeName());
				PayrollWorksheet pw = new PayrollWorksheet();
				pw.setAddedBy(5);
				pw.setAddedDate(now);
//				pw.setCity(city);
				pw.setDirectLabor(super.makeBD(employee.getDirectLabor()));
				pw.setDivisionId(divisionId);
				pw.setEmployeeCode(washer.employeeCode);
				pw.setEmployeeName(washer.last + ", " + washer.first);
				pw.setExpenses(super.makeBD(employee.getExpenses()));
				pw.setExpensesAllowed(super.makeBD(employee.getExpensesAllowed()));
				pw.setExpensesSubmitted(super.makeBD(employee.getExpensesSubmitted()));
				pw.setGrossPay(super.makeBD(employee.getGrossPay()));
				pw.setHolidayHours(super.makeBD(employee.getHolidayHours()));
				pw.setHolidayPay(super.makeBD(employee.getHolidayPay()));
				pw.setOtHours(super.makeBD(employee.getOtHours()));
				pw.setOtPay(super.makeBD(employee.getOtPay()));
				pw.setProductivity(makeProductivity(employee.getProductivity()));
				pw.setRegularHours(super.makeBD(employee.getRegularHours()));
				pw.setRegularPay(super.makeBD(employee.getRegularPay()));
//				pw.setState(state);
				pw.setUpdatedBy(5);
				pw.setUpdatedDate(now);
				pw.setVacationHours(super.makeBD(employee.getVacationHours()));
				pw.setVacationPay(super.makeBD(employee.getVacationPay()));
				pw.setVolume(super.makeBD(employee.getVolume()));
				pw.setWeekEnding(weekEnding);
//				System.out.println(pw);
				
				System.out.println("Inserting: " + pw.getEmployeeName());
				pw.insertWithNoKey(conn);
			}
		}		
	}



	private List<Washer> makeWasherList(PreparedStatement washerSql, PayrollWorksheetEmployee employee) throws SQLException {
		List<Washer> washerList = new ArrayList<Washer>();
		EmployeeName employeeName = new EmployeeName(employee.getEmployeeName());
		washerSql.setString(1, employeeName.first.toLowerCase());
		washerSql.setString(2, employeeName.last.toLowerCase());
		washerSql.setString(3, employee.getEmployeeName().toLowerCase());
		ResultSet rs = washerSql.executeQuery();
		while ( rs.next() ) {
			washerList.add( new Washer(rs) );
		}
		rs.close();
		return washerList;
	}



	private BigDecimal makeProductivity(String value) throws ParseException {
		Double productivity = null;
		if ( ! StringUtils.isBlank(value)) {
			Number number = productivityFormat.parse(value);
			productivity = number.doubleValue();
		}
		return new BigDecimal(productivity);
	}



	private String makeCity(Integer divisionId, String value) {
		String city = value;
		if ( StringUtils.isBlank(value)) {
			if ( this.defaultCity.containsKey(divisionId)) {
				city = this.defaultCity.get(divisionId);
			}
		}
		return city;
	}



	private String makeState(Integer divisionId, String value) {
		String state = value;
		if ( StringUtils.isBlank(value)) {			
			state = this.defaultState.get(divisionId);
		}
		return state;
	}



	public static void main(String[] args) {
		try {
			new LoadPayrollWorksheets().go();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private class Washer extends ApplicationObject {
		private static final long serialVersionUID = 1L;
		public Integer employeeCode;
		public String division;
		public String first;
		public String last;
		public String alias;
		
		public Washer(ResultSet rs) throws SQLException {
			super();
			this.employeeCode = rs.getInt("employee_code");
			this.division = rs.getString("division");
			this.first = rs.getString("employee_first_name");
			this.last = rs.getString("employee_last_name");
			this.alias = rs.getString("alias");
		}
				
	}
}
