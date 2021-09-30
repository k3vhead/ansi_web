package com.ansi.scilla.web.payroll.response;

import java.sql.Connection;

import org.apache.commons.lang3.time.DateUtils;

import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.common.db.PayrollEmployee;
import com.ansi.scilla.web.common.response.MessageResponse;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class ExceptionReportResponse extends MessageResponse {

	private static final long serialVersionUID = 1L;

	private String div;
	private String divDescription;
	/*
	private Integer groupId; //group_id, \n"
	 private String groupName; // "	division_group.name as group_name, \n"
	 private Double vacationHours;	
	 private String companyCode;
	 private Integer divisionId;
	 private String description; // replaced by div description?
	 private String employeeFirstName;
	 private String employeeMi;
	 private String employeeLastName;
	 private String empolyeeStatus; //not used 
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
	 private Integer vacationPay; //not used
	 private Integer holidayHours;
	 private Integer holidayPay;
	 private Integer grossPay; //not used 
	 private Integer expensesSubmitted;
	 private Integer expensesAllowed;
	 private Integer volume;
	 private Integer directLabor;
	 private String productivity;
	 */
	
	public ExceptionReportResponse() {
		super();
	}
	
	public ExceptionReportResponse(Connection conn, Integer divisionId) throws RecordNotFoundException, Exception {
		this();
		Division division = new Division();
		division.setDivisionId(divisionId);
		division.selectOne(conn);
		this.div = division.getDivisionDisplay();
		this.divDescription = division.getDescription();
		
		
	} 
	
	/* public ExceptionReportResponse(Connection conn, Integer employeeCode) throws RecordNotFoundException, Exception {
		this();
		PayrollEmployee employee = new PayrollEmployee();
		employee.setEmployeeCode(employeeCode);
		employee.selectOne(conn);
		this.employeeCode = employeeCode;
		this.employeeFirstName = employee.getEmployeeFirstName();
		this.employeeLastName = employee.getEmployeeLastName();
		this.employeeMi = employee.getEmployeeMi();
		this.employeeStatus = employee.getEmployeeStatus();
		if ( employee.getEmployeeTermination() != null ) {
			this.termination = DateUtils.toCalendar(employee.getEmployeeTermination());
		}		
		this.weekEnding = DateUtils.toCalendar(employee.getWeekEnding());
		this.state = employee.getState();
		this.city = employee.getCity();
		this.employeeName = employee.getEmployeeName();
		this.regularHours = employee.getRegularHours();
		this.expenses = employee.getExpenses();
		this.otHours = employee.getOtHours();
		this.otPay = employee.getOtPay();
		this.holidayHours = employee.getHolidayHours();
		this.holidayPay = employee.getHolidayPay();
		this.grossPay = employee.grossPay();
		this.expensesSubmitted = employee.getExpensesSubmitted();
		this.expensesAllowed = employee.getExpensesAllowed();
		this.volume = employee.getVolume();
		this.directLabor = employee.getDirectLabor();
		this.productivity = employee.getProductivity();
	} */

	public String getDiv() {
		return div;
	}

	public void setDiv(String div) {
		this.div = div;
	}

	public String getDivDescription() {
		return divDescription;
	}

	public void setDivDescription(String divDescription) {
		this.divDescription = divDescription;
	}
	
	/*
	 

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
	public String getEmployeeStatus() {
		return employeeStatus; 
	} 
	public Calendar getEmployeeTermination() {
		return employeeTermination;
	}
	public Calendar getWeekEnding() {
		return weekEnding;
	} 
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
	public Integer getGrossPay() {
		return grossPay;
	} 
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
	 */
	
	
}
