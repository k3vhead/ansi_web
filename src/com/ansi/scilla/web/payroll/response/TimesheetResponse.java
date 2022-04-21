package com.ansi.scilla.web.payroll.response;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.Calendar;

import com.ansi.scilla.common.db.Locale;
import com.ansi.scilla.common.db.PayrollWorksheet;
import com.ansi.scilla.common.utils.LocaleType;
import com.ansi.scilla.web.common.response.MessageResponse;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class TimesheetResponse extends MessageResponse {

	private static final long serialVersionUID = 1L;
	
	private Integer divisionId;
	private Calendar weekEnding;
	private String state;
	private Integer employeeCode;
	private String city;
	
	private BigDecimal directLabor;
	private String employeeName;
	private BigDecimal expenses;
	private BigDecimal expensesAllowed;
	private BigDecimal expensesSubmitted;
	private BigDecimal grossPay;
	private BigDecimal holidayHours;
	private BigDecimal holidayPay;
	private BigDecimal otHours;
	private BigDecimal otPay;
	private BigDecimal productivity;
	private BigDecimal regularHours;
	private BigDecimal regularPay;
	private BigDecimal vacationHours;
	private BigDecimal vacationPay;
	private BigDecimal volume;

	
	public TimesheetResponse() {
		super();
	}

	

	public TimesheetResponse(Connection conn, Integer divisionId, Calendar weekEnding, Integer employeeCode, Locale locale) throws RecordNotFoundException, Exception {
		this();
		this.divisionId = divisionId;
		this.weekEnding = weekEnding;
		this.state = locale.getStateName();
		this.employeeCode = employeeCode;
		this.city = locale.getLocaleTypeId().equals(LocaleType.STATE.name()) ? null : locale.getName();;
		
		PayrollWorksheet timesheet = new PayrollWorksheet();
		timesheet.setDivisionId(divisionId);
		timesheet.setWeekEnding(weekEnding.getTime());
//		timesheet.setState(state);
		timesheet.setEmployeeCode(employeeCode);
//		timesheet.setCity(city);
		timesheet.setLocaleId(locale.getLocaleId());
		timesheet.selectOne(conn);
		
		this.employeeName = timesheet.getEmployeeName();
		
		for (String fieldName : new String[] {
				"DirectLabor",
				"Expenses",
				"ExpensesAllowed",
				"ExpensesSubmitted",
				"GrossPay",
				"HolidayHours",
				"HolidayPay",
				"OtHours",
				"OtPay",
				"Productivity",
				"RegularHours",
				"RegularPay",
				"VacationHours",
				"VacationPay",
				"Volume",
		}) {
			String getterName = "get" + fieldName;
			String setterName = "set" + fieldName;
			Method getter = PayrollWorksheet.class.getMethod(getterName, (Class<?>[])null);
			BigDecimal value = (BigDecimal)getter.invoke(timesheet, (Object[])null);
			Method setter = TimesheetResponse.class.getMethod(setterName, new Class<?>[] {BigDecimal.class});
			setter.invoke(this, new Object[] {value});
		}
		
	}



	public Integer getDivisionId() {
		return divisionId;
	}

	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}

	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd", timezone="America/Chicago")
	public Calendar getWeekEnding() {
		return weekEnding;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd", timezone="America/Chicago")
	public void setWeekEnding(Calendar weekEnding) {
		this.weekEnding = weekEnding;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public Integer getEmployeeCode() {
		return employeeCode;
	}

	public void setEmployeeCode(Integer employeeCode) {
		this.employeeCode = employeeCode;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public BigDecimal getDirectLabor() {
		return directLabor;
	}
	public void setDirectLabor(BigDecimal directLabor) {
		this.directLabor = directLabor;
	}
	public String getEmployeeName() {
		return employeeName;
	}
	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}
	public BigDecimal getExpenses() {
		return expenses;
	}
	public void setExpenses(BigDecimal expenses) {
		this.expenses = expenses;
	}
	public BigDecimal getExpensesAllowed() {
		return expensesAllowed;
	}
	public void setExpensesAllowed(BigDecimal expensesAllowed) {
		this.expensesAllowed = expensesAllowed;
	}
	public BigDecimal getExpensesSubmitted() {
		return expensesSubmitted;
	}
	public void setExpensesSubmitted(BigDecimal expensesSubmitted) {
		this.expensesSubmitted = expensesSubmitted;
	}
	public BigDecimal getGrossPay() {
		return grossPay;
	}
	public void setGrossPay(BigDecimal grossPay) {
		this.grossPay = grossPay;
	}
	public BigDecimal getHolidayHours() {
		return holidayHours;
	}
	public void setHolidayHours(BigDecimal holidayHours) {
		this.holidayHours = holidayHours;
	}
	public BigDecimal getHolidayPay() {
		return holidayPay;
	}
	public void setHolidayPay(BigDecimal holidayPay) {
		this.holidayPay = holidayPay;
	}
	public BigDecimal getOtHours() {
		return otHours;
	}
	public void setOtHours(BigDecimal otHours) {
		this.otHours = otHours;
	}
	public BigDecimal getOtPay() {
		return otPay;
	}
	public void setOtPay(BigDecimal otPay) {
		this.otPay = otPay;
	}
	public BigDecimal getProductivity() {
		return productivity;
	}
	public void setProductivity(BigDecimal productivity) {
		this.productivity = productivity;
	}
	public BigDecimal getRegularHours() {
		return regularHours;
	}
	public void setRegularHours(BigDecimal regularHours) {
		this.regularHours = regularHours;
	}
	public BigDecimal getRegularPay() {
		return regularPay;
	}
	public void setRegularPay(BigDecimal regularPay) {
		this.regularPay = regularPay;
	}
	public BigDecimal getVacationHours() {
		return vacationHours;
	}
	public void setVacationHours(BigDecimal vacationHours) {
		this.vacationHours = vacationHours;
	}
	public BigDecimal getVacationPay() {
		return vacationPay;
	}
	public void setVacationPay(BigDecimal vacationPay) {
		this.vacationPay = vacationPay;
	}
	public BigDecimal getVolume() {
		return volume;
	}
	public void setVolume(BigDecimal volume) {
		this.volume = volume;
	}
}
