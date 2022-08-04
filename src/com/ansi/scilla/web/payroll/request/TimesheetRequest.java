package com.ansi.scilla.web.payroll.request;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.common.db.ApplicationProperties;
import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.common.exceptions.InvalidValueException;
import com.ansi.scilla.common.exceptions.PayrollException;
import com.ansi.scilla.common.payroll.parser.worksheet.PayrollWorksheetEmployee.FieldLocation;
import com.ansi.scilla.common.payroll.parser.worksheet.PayrollWorksheetHeader;
import com.ansi.scilla.common.payroll.validator.common.EmployeeValidation;
import com.ansi.scilla.common.payroll.validator.common.PayrollMessage;
import com.ansi.scilla.common.payroll.validator.common.YtdValues;
import com.ansi.scilla.common.utils.ApplicationProperty;
import com.ansi.scilla.common.utils.ErrorLevel;
import com.ansi.scilla.web.common.exception.InvalidFormatException;
import com.ansi.scilla.web.common.request.AbstractRequest;
import com.ansi.scilla.web.common.request.RequestValidator;
import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.payroll.common.PayrollValidation;
import com.ansi.scilla.web.payroll.common.TimesheetValidator;
import com.fasterxml.jackson.annotation.JsonFormat;


/**
 * Used for adding/editing a payroll timesheet record, most likely from the Payroll Timesheet Lookup page
 * 
 * @author dclewis
 *
 */
public class TimesheetRequest extends AbstractRequest implements EmployeeValidation {


	private static final long serialVersionUID = 1L;

	public static final String ACTION_IS_ADD = "ADD";
	public static final String ACTION_IS_UPDATE = "UPDATE";
	public static final String ACTION_IS_VALIDATE = "VALIDATE";
	
	
	public static final String ACTION = "action";
	
	public static final String DIVISION_ID = "divisionId";
	public static final String WEEK_ENDING = "weekEnding";
	public static final String STATE = "state";
	public static final String EMPLOYEE_CODE = "employeeCode";
	public static final String CITY = "city";
	
	public static final String EMPLOYEE_NAME = "employeeName";
	public static final String PRODUCTIVITY = "productivity";
	


	
	
	
	private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	// this field is used to see what step in CRUD we're working with
	private String action;
	
	// these fields are used in the get to identify a unique record
	private Integer divisionId;
	private Calendar weekEnding;
	private String state;  // abbreviation
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
	private Integer row;

	// indicate whether values or OK / Error (stop processing) / Warning (throw a tantrum, but keep going)
	private ErrorLevel errorLevel;
	
	// Stuff used for validation	
	private HashMap<String, List<PayrollMessage>> messageList;    // message map: Field Name -> list of messages for that field
	private Double unionRate;
	private Integer standardDivisionId;
	private Integer standardCompanyId;
	private String employeeFirstName;
	private String employeeLastName;
	private HashMap<String, Double> values;
	
	private Logger logger;
	
	
	
	public TimesheetRequest() {
		super();
		this.logger = LogManager.getLogger(TimesheetRequest.class);
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
	
	public Integer getRow() {
		return row;
	}

	/**
	 * There are 2 steps to the validation when adding a new worksheet record:
	 * 1. Are all the required fields in place, and valid (eg Is division id populated and in the division table?)
	 * 2. Add this record to the system and do a "what if" to see if we are creating an error / warning situation
	 * Then we try to take that info and present it to the user in a useful way.
	 * 
	 * @param conn
	 * @param webMessages 
	 * @return
	 * @throws PayrollException
	 */
	public PayrollValidation validateAdd(Connection conn) throws PayrollException, Exception {
		ResponseCode responseCode = null;
		WebMessages webMessages = new WebMessages();
		
		// step 1 : the "normal" validation of values
		validateNumbers(webMessages);
		RequestValidator.validateId(conn, webMessages, Division.TABLE, Division.DIVISION_ID, DIVISION_ID, this.divisionId, true);
		RequestValidator.validateStateLocale(conn, webMessages, STATE, this.state, true, null);
		if ( this.state == null ) {
			RequestValidator.validateCity(conn, webMessages, CITY, this.city, 255, false, null);
		} else {
			RequestValidator.validateCityState(conn, webMessages, CITY, this.city, this.state, 255, false, null);
		}
		RequestValidator.validateDate(webMessages, WEEK_ENDING, this.weekEnding, true, null, null);
		RequestValidator.validateEmployeeCode(conn, webMessages, EMPLOYEE_CODE, this.employeeCode, true, null);
		if ( webMessages.isEmpty() ) {
			RequestValidator.validateEmployeeName(conn, webMessages, EMPLOYEE_CODE, employeeCode, employeeName);
			makeValues();
		} else {
			responseCode = ResponseCode.EDIT_FAILURE;
		}
		
		
		
		// step 2 : the payroll validation (steal code from ansi_common/payroll/PayrollEmployeeValidator.validate() as needed)
		//          yes, we have 2 identical if statements. It's less efficient, but it makes the code easier to understand so get over it
		if ( webMessages.isEmpty() ) {
			// division id has already been validated, so if this query fails we have bigger problems than a 500 return code
			Division division = new Division();
			division.setDivisionId(divisionId);
			division.selectOne(conn);
	
			ApplicationProperties maxExpenseProperty = ApplicationProperty.get(conn, ApplicationProperty.EXPENSE_MAX);
			Double maxExpenseRate = maxExpenseProperty.getValueFloat().doubleValue();
	
			// We're not validating name because the name is just used to get the code, and we validate the code before we get here
//			addMessage(WprCols.EMPLOYEE_NAME, validateEmployeeName(conn));  
			addMessage(FieldLocation.GROSS_PAY, validateMinimumGovtPay(division)); 
			addMessage(FieldLocation.EXPENSES_SUBMITTED, validateExcessExpense(maxExpenseRate));
			if ( this.employeeCode != null ) {
				makeEmployeeDefaults(conn, this.employeeCode);
				YtdValues ytdValues = makeYtdValues(conn, this.employeeCode);
				if ( ytdValues.isTrue(YtdValues.FieldName.union_member)) {
					addMessage(FieldLocation.GROSS_PAY, validateMinimumUnionPay(this.unionRate));
					addMessage(FieldLocation.GROSS_PAY, validateYtdMinimumUnionPay(ytdValues));
				}
				addMessage(FieldLocation.GROSS_PAY, validateYtdMinimumGovtPay(ytdValues));
				addMessage(PayrollWorksheetHeader.FieldLocation.DIVISION_NBR, validateHomeDivision(this.standardDivisionId));
				addMessage(PayrollWorksheetHeader.FieldLocation.DIVISION_NBR, validateHomeCompany(division.getGroupId(), this.standardCompanyId));
				addMessage(FieldLocation.EXPENSES_SUBMITTED, validateYtdExcessExpense(ytdValues));
				addMessage(PayrollWorksheetHeader.FieldLocation.WEEK_ENDING, validateLatePay(ytdValues));
			}
			
			
			//
			// Now we have potential for errors & warnings in getMessages()
			// Set the message response level, then convert PayrollMessage to web messages
			// Then figure out what to do in the JSP
			
			switch ( getErrorLevel() ) {
			case ERROR:
				responseCode = ResponseCode.EDIT_FAILURE;
				webMessages = makeWebMessages(getMessageList());
				break;
			case OK:
				responseCode = ResponseCode.SUCCESS;
				break;
			case WARNING:
				responseCode = ResponseCode.EDIT_WARNING;
				webMessages = makeWebMessages(getMessageList());
				break;
			default:
				throw new InvalidValueException(getErrorLevel() + " is not a valid response");
			}
			

		}		
		
		return new PayrollValidation(responseCode, webMessages);
	}
	

	
	
	
	
	/**
	 * There are 2 steps to the validation when update a worksheet record:
	 * 1. Are all the required fields in place, and valid (eg Is division id populated and in the division table?)
	 * 2. Add this record to the system and do a "what if" to see if we are creating an error / warning situation
	 * Then we try to take that info and present it to the user in a useful way.
	 * 
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public PayrollValidation validateUpdate(Connection conn) throws Exception {
		Logger logger = LogManager.getLogger(this.getClass());
		ResponseCode responseCode = null;
		WebMessages webMessages = new WebMessages();	

		makeValues();
		validateNumbers(webMessages);



		// division id has already been validated, so if this query fails we have bigger problems than a 500 return code
		Division division = new Division();
		division.setDivisionId(divisionId);
		division.selectOne(conn);

		ApplicationProperties maxExpenseProperty = ApplicationProperty.get(conn, ApplicationProperty.EXPENSE_MAX);
		Double maxExpenseRate = maxExpenseProperty.getValueFloat().doubleValue();


		addMessage(FieldLocation.GROSS_PAY, validateMinimumGovtPay(division)); 
		addMessage(FieldLocation.EXPENSES_SUBMITTED, validateExcessExpense(maxExpenseRate));
		if ( this.employeeCode != null ) {
			makeEmployeeDefaults(conn, this.employeeCode);
			YtdValues ytdValues = makeYtdValues(conn, this.employeeCode);
			if ( ytdValues.isTrue(YtdValues.FieldName.union_member)) {
				addMessage(FieldLocation.GROSS_PAY, validateMinimumUnionPay(this.unionRate));
				addMessage(FieldLocation.GROSS_PAY, validateYtdMinimumUnionPay(ytdValues));
			}
			addMessage(FieldLocation.GROSS_PAY, validateYtdMinimumGovtPay(ytdValues));
			addMessage(PayrollWorksheetHeader.FieldLocation.DIVISION_NBR, validateHomeDivision(this.standardDivisionId));
			addMessage(PayrollWorksheetHeader.FieldLocation.DIVISION_NBR, validateHomeCompany(division.getGroupId(), this.standardCompanyId));
			addMessage(FieldLocation.EXPENSES_SUBMITTED, validateYtdExcessExpense(ytdValues));
			addMessage(PayrollWorksheetHeader.FieldLocation.WEEK_ENDING, validateLatePay(ytdValues));
		}


		// Now we have potential for errors & warnings in getMessages()
		// Set the message response level, then convert PayrollMessage to web messages
		// Then figure out what to do in the JSP

		switch ( getErrorLevel() ) {
		case ERROR:
			responseCode = ResponseCode.EDIT_FAILURE;
			webMessages = makeWebMessages(getMessageList());
			break;
		case OK:
			responseCode = ResponseCode.SUCCESS;
			break;
		case WARNING:
			responseCode = ResponseCode.EDIT_WARNING;
			webMessages = makeWebMessages(getMessageList());
			break;
		default:
			throw new InvalidValueException(getErrorLevel() + " is not a valid response");
		}
		
		return new PayrollValidation(responseCode, webMessages);
	}
	
	

	/**
	 * Convert payroll messages to something the JSP will understand
	 * @param messageList
	 * @return
	 */
	private WebMessages makeWebMessages(HashMap<String, List<PayrollMessage>> messageList) {
		WebMessages webMessages = new WebMessages();
		
		for ( String messageKey : messageList.keySet() ) {
			logger.log(Level.DEBUG, "MessageKey: " + messageKey);
			String fieldName = messageKey;
			try {
				FieldLocation wpr = FieldLocation.valueOf(messageKey);
				logger.log(Level.DEBUG, "WPR: " + wpr.name());
				fieldName = wpr == null || StringUtils.isBlank(wpr.fieldName()) ? messageKey : PayrollField.lookup(wpr).fieldName();
				logger.log(Level.DEBUG, "New fieldname: " + fieldName);
			} catch ( IllegalArgumentException e ) {
				// we don't care. If the fieldname is not a FieldLocation value, we've already defaulted to the fieldname
			}
			
			for ( PayrollMessage message : messageList.get(messageKey) ) {
				if ( message.getErrorMessage().getErrorLevel() != ErrorLevel.OK ) {
					webMessages.addMessage(fieldName, message.getErrorMessage().getMessage());
				}
			}
		}
		
		return webMessages;
	}

	
	
	/**
	 * THis is just here so we can steal code -- erase it before we go prod
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	@Deprecated
	public WebMessages validateMe(Connection conn) throws Exception {
		Logger logger = LogManager.getLogger(this.getClass());
		WebMessages webMessages = new WebMessages();		
		validateNumbers(webMessages);
		RequestValidator.validateId(conn, webMessages, Division.TABLE, Division.DIVISION_ID, DIVISION_ID, this.divisionId, true);
//		RequestValidator.validateState(webMessages, STATE, this.state, true, null);
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
			TimesheetValidator.validateExpenses(conn, webMessages, PayrollField.EXPENSES_SUBMITTED.fieldName(), expensesAllowed, expensesSubmitted, grossPay);
			TimesheetValidator.validateExpensesYTD(conn, webMessages, PayrollField.EXPENSES_SUBMITTED.fieldName(), employeeCode, weekEnding);
		}
		return webMessages;
	}

	
	public WebMessages validateDelete(Connection conn) throws Exception {
		WebMessages webMessages = new WebMessages();		
		RequestValidator.validateId(conn, webMessages, Division.TABLE, Division.DIVISION_ID, DIVISION_ID, this.divisionId, true);
		RequestValidator.validateStateLocale(conn, webMessages, STATE, this.state, true, null);
		RequestValidator.validateString(webMessages, CITY, this.city, 255, false, null);
		RequestValidator.validateDate(webMessages, WEEK_ENDING, this.weekEnding, true, null, null);
		RequestValidator.validateEmployeeCode(conn, webMessages, EMPLOYEE_CODE, this.employeeCode, true, null);
		return webMessages;
	}

	private void validateNumbers(WebMessages webMessages) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		for (PayrollField payrollField : PayrollField.values() ) {
			Field field = this.getClass().getDeclaredField(payrollField.fieldName());
//			logger.log(Level.DEBUG, payrollField);
			Double value = (Double)field.get(this);
			RequestValidator.validateDouble(webMessages, payrollField.fieldName(), value, 0.0D, (Double)null, false, null);			
		}
		RequestValidator.validateDouble(webMessages, PRODUCTIVITY, this.productivity, 0.0D, 1.0D, false, null);
	}

	
	
	private void makeValues() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		if ( this.values == null ) {
			this.values = new HashMap<String, Double>();
		}
		for (PayrollField payrollField : PayrollField.values() ) {
			String keyValue = payrollField.wpr().fieldName();
			Field field = this.getClass().getDeclaredField(payrollField.fieldName());
			Double value = (Double)field.get(this);
			this.values.put(keyValue, value == null ? 0.0D : value);			
		}
	}
	
	
	
	@Override
	public ErrorLevel getErrorLevel() {
		return this.errorLevel;
	}

	@Override
	public void setErrorLevel(ErrorLevel errorLevel) {
		this.errorLevel = errorLevel;		
	}

	@Override
	public HashMap<String, List<PayrollMessage>> getMessageList() {
		return this.messageList;
	}

	@Override
	public void setMessageList(HashMap<String, List<PayrollMessage>> messageList) {
		this.messageList = messageList;		
	}

	

	@Override
	public HashMap<String, Double> getValues() {		
		return this.values;
	}

	@Override
	public void setEmployeeFirstName(String emplyeeFirstName) {
		this.employeeFirstName = emplyeeFirstName;
	}

	@Override
	public void setEmployeeLastName(String employeeLastName) {
		this.employeeLastName = employeeLastName;
	}

	@Override
	public void setRow(Integer row) {
		this.row = row;		
	}

	@Override
	public void setStandardCompanyId(Integer companyId) {
		this.standardCompanyId = companyId;		
	}

	@Override
	public void setStandardDivisionId(Integer divisionId) {
		this.standardDivisionId = divisionId;
		
	}

	@Override
	public void setUnionRate(Double unionRate) {
		this.unionRate = unionRate;
		
	}
	
	
	
	/**
	 * This list is used to for validation (here) and to populate the payroll_worksheet DB record (in TimesheetServlet)
	 * The mapping is from local field names to FieldLocation values for the validation utility to work
	 * 
	 * Make note that productivity is not listed here
	 * 
	 */
	public enum PayrollField {
			DIRECT_LABOR("directLabor", FieldLocation.DIRECT_LABOR),
			EXPENSES("expenses", FieldLocation.EXPENSES),
			EXPENSES_ALLOWED("expensesAllowed", FieldLocation.EXPENSES_ALLOWED),
			EXPENSES_SUBMITTED("expensesSubmitted", FieldLocation.EXPENSES_SUBMITTED),
			GROSS_PAY("grossPay", FieldLocation.GROSS_PAY),
			HOLIDAY_HOURS("holidayHours", FieldLocation.HOLIDAY_HOURS),
			HOLIDAY_PAY("holidayPay", FieldLocation.HOLIDAY_PAY),
			OT_HOURS("otHours", FieldLocation.OT_HOURS),
			OT_PAY("otPay", FieldLocation.OT_PAY),
			REGULAR_HOURS("regularHours", FieldLocation.REGULAR_HOURS),
			REGULAR_PAY("regularPay", FieldLocation.REGULAR_PAY),
			VACATION_HOURS("vacationHours", FieldLocation.VACATION_HOURS),
			VACATION_PAY("vacationPay", FieldLocation.VACATION_PAY),
			VOLUME("volume", FieldLocation.VOLUME),
			;
		
		
		private String fieldName;
		private FieldLocation wprCols;
		
		private static HashMap<FieldLocation, PayrollField> lookup;
		
		static {
			lookup = new HashMap<FieldLocation, PayrollField>();
			for ( PayrollField payrollField : PayrollField.values() ) {
				lookup.put(payrollField.wpr(), payrollField);
			}
		}
		
		private PayrollField(String fieldName, FieldLocation wprCols) {
			this.fieldName = fieldName;
			this.wprCols = wprCols;
		}
		
		public String fieldName() { return this.fieldName; }
		public FieldLocation wpr() { return this.wprCols; }
		public static PayrollField lookup(FieldLocation wpr) { return lookup.get(wpr); }
	};
}
