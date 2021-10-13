package com.ansi.scilla.web.payroll.request;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.web.common.exception.InvalidFormatException;
import com.ansi.scilla.web.common.request.AbstractRequest;
import com.ansi.scilla.web.common.request.RequestValidator;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.payroll.common.TimesheetValidator;
import com.fasterxml.jackson.annotation.JsonFormat;


/**
 * Used for adding/editing a payroll timesheet record, most likely from the Payroll Timesheet Lookup page
 * 
 * @author dclewis
 *
 */
public class TimesheetRequest extends AbstractRequest {


	private static final long serialVersionUID = 1L;

	public static final String ACTION_IS_ADD = "ADD";
	public static final String ACTION_IS_UPDATE = "UPDATE";
	
	
	public static final String ACTION = "action";
	
	public static final String DIVISION_ID = "divisionId";
	public static final String WEEK_ENDING = "weekEnding";
	public static final String STATE = "state";
	public static final String EMPLOYEE_CODE = "employeeCode";
	public static final String CITY = "city";
	
	public static final String DIRECT_LABOR = "directLabor";
	public static final String EMPLOYEE_NAME = "employeeName";
	public static final String EXPENSES = "expenses";
	public static final String EXPENSES_ALLOWED = "expensesAllowed";
	public static final String EXPENSES_SUBMITTED = "expensesSubmitted";
	public static final String GROSS_PAY = "grossPay";
	public static final String HOLIDAY_HOURS = "holidayHours";
	public static final String HOLIDAY_PAY = "holidayPay";
	public static final String OT_HOURS = "otHours";
	public static final String OT_PAY = "otPay";
	public static final String PRODUCTIVITY = "productivity";
	public static final String REGULAR_HOURS = "regularHours";
	public static final String REGULAR_PAY = "regularPay";
	public static final String VACATION_HOURS = "vacationHours";
	public static final String VACATION_PAY = "vacationPay";
	public static final String VOLUME = "volume";

	
	
	
	private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	// this field is used to see what step in CRUD we're working with
	private String action;
	
	// these fields are used in the get to identify a unique record
	private Integer divisionId;
	private Calendar weekEnding;
	private String state;
	private Integer employeeCode;
	private String city;
	
	// these fields are used in the post to update a record
	private Double directLabor;
	private String employeeName;
	private Double expenses;
	private Double expensesAllowed;
	private Double expensesSubmitted;
	private Double grossPay;
	private Double holidayHours;
	private Double holidayPay;
	private Double otHours;
	private Double otPay;
	private Double productivity;
	private Double regularHours;
	private Double regularPay;
	private Double vacationHours;
	private Double vacationPay;
	private Double volume;

	
	// This list is used to for validation (here) and to populate
	// the payroll_worksheet DB record (in TimesheetServlet)
	// Make note that productivity is not listed here
	public static final String[] PAYROLL_FIELDNAMES = new String[] {
		DIRECT_LABOR,
		EXPENSES,
		EXPENSES_ALLOWED,
		EXPENSES_SUBMITTED,
		GROSS_PAY,
		HOLIDAY_HOURS,
		HOLIDAY_PAY,
		OT_HOURS,
		OT_PAY,
		REGULAR_HOURS,
		REGULAR_PAY,
		VACATION_HOURS,
		VACATION_PAY,
		VOLUME,
	};
	
	
	
	public TimesheetRequest() {
		super();
	}

	public TimesheetRequest(HttpServletRequest request) throws InvalidFormatException {
		this();
		String divisionId = request.getParameter(DIVISION_ID);
		if ( ! StringUtils.isBlank(divisionId)) {
			try {
				this.divisionId = Integer.valueOf(divisionId);
			} catch ( NumberFormatException e) {
				throw new InvalidFormatException(DIVISION_ID);
			}
		}
		this.state = request.getParameter(STATE);
		String weekEnding = request.getParameter(WEEK_ENDING);
		if ( ! StringUtils.isBlank(weekEnding)) {			
			try {
				this.weekEnding = DateUtils.toCalendar(dateFormat.parse(weekEnding));
			} catch (ParseException e) {
				throw new InvalidFormatException(WEEK_ENDING);
			}
		}
		String employeeCode = request.getParameter(EMPLOYEE_CODE);
		if ( ! StringUtils.isBlank(employeeCode) ) {
			try {
				this.employeeCode = Integer.valueOf(employeeCode);
			} catch ( NumberFormatException e ) {
				throw new InvalidFormatException(EMPLOYEE_CODE);
			}
		}
		this.city = request.getParameter(CITY);
	}

	
	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
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

	public Double getDirectLabor() {
		return directLabor;
	}

	public void setDirectLabor(Double directLabor) {
		this.directLabor = directLabor;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public Double getExpenses() {
		return expenses;
	}

	public void setExpenses(Double expenses) {
		this.expenses = expenses;
	}

	public Double getExpensesAllowed() {
		return expensesAllowed;
	}

	public void setExpensesAllowed(Double expensesAllowed) {
		this.expensesAllowed = expensesAllowed;
	}

	public Double getExpensesSubmitted() {
		return expensesSubmitted;
	}

	public void setExpensesSubmitted(Double expensesSubmitted) {
		this.expensesSubmitted = expensesSubmitted;
	}

	public Double getGrossPay() {
		return grossPay;
	}

	public void setGrossPay(Double grossPay) {
		this.grossPay = grossPay;
	}

	public Double getHolidayHours() {
		return holidayHours;
	}

	public void setHolidayHours(Double holidayHours) {
		this.holidayHours = holidayHours;
	}

	public Double getHolidayPay() {
		return holidayPay;
	}

	public void setHolidayPay(Double holidayPay) {
		this.holidayPay = holidayPay;
	}

	public Double getOtHours() {
		return otHours;
	}

	public void setOtHours(Double otHours) {
		this.otHours = otHours;
	}

	public Double getOtPay() {
		return otPay;
	}

	public void setOtPay(Double otPay) {
		this.otPay = otPay;
	}

	public Double getProductivity() {
		return productivity;
	}

	public void setProductivity(Double productivity) {
		this.productivity = productivity;
	}

	public Double getRegularHours() {
		return regularHours;
	}

	public void setRegularHours(Double regularHours) {
		this.regularHours = regularHours;
	}

	public Double getRegularPay() {
		return regularPay;
	}

	public void setRegularPay(Double regularPay) {
		this.regularPay = regularPay;
	}

	public Double getVacationHours() {
		return vacationHours;
	}

	public void setVacationHours(Double vacationHours) {
		this.vacationHours = vacationHours;
	}

	public Double getVacationPay() {
		return vacationPay;
	}

	public void setVacationPay(Double vacationPay) {
		this.vacationPay = vacationPay;
	}

	public Double getVolume() {
		return volume;
	}

	public void setVolume(Double volume) {
		this.volume = volume;
	}
	
	
	
	public WebMessages validate(Connection conn) throws Exception {
		Logger logger = LogManager.getLogger(this.getClass());
		WebMessages webMessages = new WebMessages();		
		validateNumbers(webMessages);
		RequestValidator.validateId(conn, webMessages, Division.TABLE, Division.DIVISION_ID, DIVISION_ID, this.divisionId, true);
		RequestValidator.validateState(webMessages, STATE, this.state, true, null);
		RequestValidator.validateString(webMessages, CITY, this.city, 255, false, null);
		RequestValidator.validateDate(webMessages, WEEK_ENDING, this.weekEnding, true, null, null);
		RequestValidator.validateEmployeeCode(conn, webMessages, EMPLOYEE_CODE, this.employeeCode, true, null);

		for ( String x : webMessages.keySet() ) {
			logger.log(Level.DEBUG, x + "=>" + webMessages.get(x));
		}
		// Validate code/name combo
		// Handles: Franklin Roosevelt
		//			Franklin D Roosevelt
		//			Franklin D. Roosevelt
		//			Roosevelt, Franklin
		//			Roosevelt, Franklin D
		//			Roosevelt, Franklin D.
		if ( ! webMessages.containsKey(EMPLOYEE_CODE) ) {
			String sql = "select count(*) as record_count from payroll_employee pe where pe.employee_code = ? \n" + 
					"	and (\n" + 
					"		lower(concat(pe.employee_first_name,' ',pe.employee_last_name)) = ?\n" + 
					"		or lower(concat(pe.employee_first_name,' ',pe.employee_mi,' ',pe.employee_last_name)) = ?\n" + 
					"		or lower(concat(pe.employee_first_name,' ',pe.employee_mi,'. ',pe.employee_last_name)) = ?\n" + 
					"		or lower(concat(pe.employee_last_name,', ',pe.employee_first_name)) = ?\n" + 
					"		or lower(concat(pe.employee_last_name,', ',pe.employee_first_name,' ',pe.employee_mi)) = ?\n" + 
					"		or lower(concat(pe.employee_last_name,', ',pe.employee_first_name,' ',pe.employee_mi,'.')) = ?\n" + 
					"	)";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, employeeCode);
			for ( int n = 2; n < 8; n++ ) {
				ps.setString(n, this.employeeName.toLowerCase());
			}
			ResultSet rs = ps.executeQuery();
			if ( rs.next() ) {
				if ( rs.getInt("record_count") == 0 ) {
					webMessages.addMessage(EMPLOYEE_CODE, "Invalid EmployeeCode/Employee Name combination");
				}
			} else {
				webMessages.addMessage(EMPLOYEE_CODE, "Error while validating");
			}
			rs.close();
		}
		
		if ( webMessages.isEmpty() ) {
			TimesheetValidator.validateEmployeeName(conn, webMessages, EMPLOYEE_NAME, this.employeeName, "Employee Name");
			TimesheetValidator.validateExpenses(conn, webMessages, EXPENSES_SUBMITTED, expensesAllowed, expensesSubmitted, grossPay);
			TimesheetValidator.validateExpensesYTD(conn, webMessages, EXPENSES_SUBMITTED, employeeCode, weekEnding);
		}
		return webMessages;
	}

	private void validateNumbers(WebMessages webMessages) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		for (String fieldName : PAYROLL_FIELDNAMES ) {
			Field field = this.getClass().getDeclaredField(fieldName);
			Double value = (Double)field.get(this);
			RequestValidator.validateDouble(webMessages, fieldName, value, 0.0D, (Double)null, false, null);			
		}
		RequestValidator.validateDouble(webMessages, PRODUCTIVITY, this.productivity, 0.0D, 1.0D, false, null);
	}

	public WebMessages validateDelete(Connection conn) throws Exception {
		WebMessages webMessages = new WebMessages();		
		RequestValidator.validateId(conn, webMessages, Division.TABLE, Division.DIVISION_ID, DIVISION_ID, this.divisionId, true);
		RequestValidator.validateState(webMessages, STATE, this.state, true, null);
		RequestValidator.validateString(webMessages, CITY, this.city, 255, false, null);
		RequestValidator.validateDate(webMessages, WEEK_ENDING, this.weekEnding, true, null, null);
		RequestValidator.validateEmployeeCode(conn, webMessages, EMPLOYEE_CODE, this.employeeCode, true, null);
		return webMessages;
	}
}
