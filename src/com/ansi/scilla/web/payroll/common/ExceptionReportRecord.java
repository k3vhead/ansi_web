package com.ansi.scilla.web.payroll.common;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

import com.ansi.scilla.common.ApplicationObject;
import com.fasterxml.jackson.annotation.JsonFormat;

public class ExceptionReportRecord extends ApplicationObject {

	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer groupId; //group_id, \n"
	 private String groupName; // "	division_group.name as group_name, \n"
	 private Double vacationHours;	
	 private String companyCode;
	 private Integer divisionId;
	 private String description;
	 private String div;
	 private String employeeFirstName;
	 private String employeeMi;
	 private String employeeLastName;
	/* private String empolyeeStatus; //not used */
	 private Calendar employeeTermination;
	 private Calendar weekEnding;
	 private String state;
	 private String city;
	 private String employeeName;
	 private Integer regularHours;
	 private Integer regularPay;
	 private Integer expenses;
	 private Integer otHours;
	 private Integer otPay;
	/* private Integer vacationPay; //not used */
	 private Integer holidayHours;
	 private Integer holidayPay;
	/* private Integer grossPay; //not used */
	 private Integer expensesSubmitted;
	 private Integer expensesAllowed;
	 private Integer volume;
	 private Integer directLabor;
	 private String productivity;
	 
	 
	 public ExceptionReportRecord(ResultSet rs) throws SQLException {
		 super();
		 this.groupId = rs.getInt("group_id");
		 this.groupName = rs.getString("group_name");
		 this.vacationHours = rs.getDouble("vacation_hours");
		 
		 this.companyCode = rs.getString("company_code");
		 this.divisionId = rs.getInt("division_id");
		 this.description = rs.getString("description");
		 this.div = rs.getString("div");
		 this.employeeFirstName = rs.getString("employee_first_name");
		 this.employeeMi = rs.getString("employee_mi");
		 this.employeeLastName = rs.getString("employee_last_name");
		/* this.employeeStatus = rs.getString("employee_status"); */
		/*this.employeeTerminationDate = rs.getCalendar("employee_termination_date");
		this.weekEnding = rs.getCalendar("week_ending"); */
		this.state = rs.getString("state");
		this.city = rs.getString("city");
		this.employeeName = rs.getString("employee_name");
		this.regularHours = rs.getInt("regular_hours");
		this.regularPay = rs.getInt("regular_pay");
		this.expenses = rs.getInt("expenses");
		this.otHours = rs.getInt("ot_hours");
		this.otPay = rs.getInt("ot_pay");
		this.holidayHours = rs.getInt("holiday_hours");
		this.holidayPay = rs.getInt("holiday_pay");
		/*this.grossPay = rs.getInt("gross_pay"); */
		this.expensesSubmitted = rs.getInt("expenses_submitted");
		this.expensesAllowed = rs.getInt("expenses_allowed");
		this.volume = rs.getInt("volume");
		this.directLabor = rs.getInt("direct_labor");
		this.productivity = rs.getString("productivity");
		
		
	 }

	public Integer getGroupId() {
		return groupId;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	
	public boolean hasVacationError() {
		return this.vacationHours != null && this.vacationHours > 40.0;
	}
	
	 
	public String getCompanyCode() {
		return companyCode;
	}
	public Integer getDivisionId() {
		return divisionId;
	}
	public String getDescription() {
		return description;
	}
	public String getDiv() {
		return div;
	}
	public String getEmployeeFirstName() {
		return employeeFirstName;
	}
	public String getEmployeeMi() {
		return employeeMi;
	}
	public String getEmployeeLastName() {
		return employeeLastName;
	}
/*	public String getEmployeeStatus() {
		return employeeStatus; 
	} */
/*	public Calendar getEmployeeTermination() {
		return employeeTermination;
	}
	public Calendar getWeekEnding() {
		return weekEnding;
	} */
	public String getState() {
		return state;
	}
	public String getCity() {
		return city;
	}
	public String getEmployeeName() {
		return employeeName;
	}
	public Integer getRegularHours() {
		return regularHours;
	}
	public Integer getRegularPay() {
		return regularPay;
	}
	public Integer getExpenses() {
		return expenses;
	}
	public Integer getOtHours() {
		return otHours;
	}
	public Integer getOtPay() {
		return otPay;
	}
	public Integer getHolidayHours() {
		return holidayHours;
	}
	public Integer getHolidayPay() {
		return holidayPay;
	}
	/*public Integer getGrossPay() {
		return grossPay;
	} */
	public Integer getExpensesSubmitted() {
		return expensesSubmitted;
	}
	public Integer getExpensesAllowed() {
		return expensesAllowed;
	}
	public Integer getVolume() {
		return volume;
	}
	public Integer getDirectLabor() {
		return directLabor;
	}
	public String getProductivity() {
		return productivity;
	}

	public Double getVacationHours() {
		return vacationHours;
	}

	public void setVacationHours(Double vacationHours) {
		this.vacationHours = vacationHours;
	}

	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}

	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setDiv(String div) {
		this.div = div;
	}

	public void setEmployeeFirstName(String employeeFirstName) {
		this.employeeFirstName = employeeFirstName;
	}

	public void setEmployeeMi(String employeeMi) {
		this.employeeMi = employeeMi;
	}

	public void setEmployeeLastName(String employeeLastName) {
		this.employeeLastName = employeeLastName;
	}

	public void setState(String state) {
		this.state = state;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public void setRegularHours(Integer regularHours) {
		this.regularHours = regularHours;
	}

	public void setRegularPay(Integer regularPay) {
		this.regularPay = regularPay;
	}

	public void setExpenses(Integer expenses) {
		this.expenses = expenses;
	}

	public void setOtHours(Integer otHours) {
		this.otHours = otHours;
	}

	public void setOtPay(Integer otPay) {
		this.otPay = otPay;
	}

	public void setHolidayHours(Integer holidayHours) {
		this.holidayHours = holidayHours;
	}

	public void setHolidayPay(Integer holidayPay) {
		this.holidayPay = holidayPay;
	}

	public void setExpensesSubmitted(Integer expensesSubmitted) {
		this.expensesSubmitted = expensesSubmitted;
	}

	public void setExpensesAllowed(Integer expensesAllowed) {
		this.expensesAllowed = expensesAllowed;
	}

	public void setVolume(Integer volume) {
		this.volume = volume;
	}

	public void setDirectLabor(Integer directLabor) {
		this.directLabor = directLabor;
	}

	public void setProductivity(String productivity) {
		this.productivity = productivity;
	}
	
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public Calendar getEmployeeTermination() {
		return employeeTermination;
	}
	
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public void setEmployeeTermination(Calendar employeeTermination) {
		this.employeeTermination = employeeTermination;
	}
	
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public Calendar getWeekEnding() {
		return weekEnding;
	}

	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public void setWeekEnding(Calendar weekEnding) {
		this.weekEnding = weekEnding;
	}
	 
	 
	 /*
	 
	 		  
				+ "	division_group.company_code,\n"
				+ "	division.division_id, \n"
				+ "	division.description, \n"
				+ "	CONCAT(division.division_nbr, '-', division.division_code) as div,\n"
				+ "	payroll_employee.employee_code, \n"
				+ "	payroll_employee.employee_first_name, \n"
				+ "	payroll_employee.employee_last_name, \n"
				+ "	payroll_employee.employee_mi, \n"
				+ "	payroll_employee.employee_status, \n"
				+ "	payroll_employee.employee_termination_date,\n"
				+ "	payroll_worksheet.week_ending, \n"
				+ "	payroll_worksheet.state, \n"
				+ "	payroll_worksheet.city, \n"
				+ "	payroll_worksheet.employee_name, \n"
				+ "	payroll_worksheet.regular_hours, \n"
				+ "	payroll_worksheet.regular_pay,	\n"
				+ "	payroll_worksheet.expenses, \n"
				+ "	payroll_worksheet.ot_hours,	\n"
				+ "	payroll_worksheet.ot_pay,	\n"
				+ "	payroll_worksheet.vacation_hours,	\n"
				+ "	payroll_worksheet.vacation_pay,\n"
				+ "	payroll_worksheet.holiday_hours,	\n"
				+ "	payroll_worksheet.holiday_pay, \n"
				+ "	payroll_worksheet.gross_pay,	\n"
				+ "	payroll_worksheet.expenses_submitted,	\n"
				+ "	payroll_worksheet.expenses_allowed, \n"
				+ "	payroll_worksheet.volume, \n"
				+ "	payroll_worksheet.direct_labor,	\n"
				+ "	payroll_worksheet.productivit
				
				*/
	
}
