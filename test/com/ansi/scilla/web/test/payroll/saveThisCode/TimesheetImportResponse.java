package com.ansi.scilla.web.test.payroll.saveThisCode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
//import java.util.Arrays;
//import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.db.ApplicationProperties;
import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.common.db.Locale;
import com.ansi.scilla.common.exceptions.InvalidValueException;
import com.ansi.scilla.common.exceptions.PayrollException;
import com.ansi.scilla.common.payroll.common.PayrollUtils;
import com.ansi.scilla.common.payroll.parser.NotATimesheetException;
import com.ansi.scilla.common.payroll.parser.PayrollWorksheetEmployee;
import com.ansi.scilla.common.payroll.parser.PayrollWorksheetParser;

import com.ansi.scilla.common.payroll.common.PayrollWorksheetHeader;

import com.ansi.scilla.common.payroll.validator.PayrollEmployeeValidator;
import com.ansi.scilla.common.payroll.validator.PayrollErrorType;
import com.ansi.scilla.common.payroll.validator.PayrollMessage;
import com.ansi.scilla.common.payroll.validator.PayrollWorksheetValidator;

import com.ansi.scilla.common.utils.ApplicationProperty;
import com.ansi.scilla.common.utils.LocaleType;
import com.ansi.scilla.web.common.request.RequestValidator;
import com.ansi.scilla.web.common.response.MessageResponse;
import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.response.WebMessagesStatus;
import com.ansi.scilla.web.payroll.common.TimesheetValidator;
import com.ansi.scilla.web.payroll.request.TimesheetImportRequest;

public class TimesheetImportResponse extends MessageResponse  {
	
	public static final String CITY = "city";
	public static final String DIVISION = "division";
	public static final String OPERATIONS_MANAGER_NAME = "operationsManagerName";
	public static final String STATE = "state";
	public static final String WEEK_ENDING = "weekEnding";
	public static final String FILENAME = "fileName";
	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	final Logger logger = LogManager.getLogger(TimesheetImportResponse.class);	
	private String city;
	private String division;
	private String operationsManagerName;
	private String state;
	private List<PayrollWorksheetEmployee> timesheetRecords = new ArrayList<PayrollWorksheetEmployee>();
	private String weekEnding;
	private String fileName;
	private WebMessages webMessages = new WebMessages();
	private NormalizedValues normal = new NormalizedValues();
	private Map<String, HashMap<String, List<PayrollMessage>>> employeeValidationMessages;	
	
	
		
	public TimesheetImportResponse() {	
		super();
	}

	public TimesheetImportResponse(Connection conn, TimesheetImportRequest request) throws IOException, NotATimesheetException, Exception {
		this(conn, request.getTimesheetFile());
		
	}
	
	public TimesheetImportResponse(Connection conn, String fileName) throws FileNotFoundException, NotATimesheetException, Exception {
		this(conn, new File(fileName));
	}
	
	public TimesheetImportResponse(Connection conn, File file) throws FileNotFoundException, NotATimesheetException, Exception {
		this(conn, new PayrollWorksheetParser(file));
	}
	
	public TimesheetImportResponse(Connection conn, FileItem file) throws IOException, NotATimesheetException, Exception {
		this(conn, new PayrollWorksheetParser(file.getName(), file.getInputStream()));
	}
	
	public TimesheetImportResponse(Connection conn, PayrollWorksheetParser parser ) throws Exception {
		this();
		this.city = parser.getCity();
		division = parser.getDivision();
		this.operationsManagerName = parser.getOperationsManagerName();
		this.state = parser.getState();
		this.timesheetRecords = parser.getTimesheetRecords();
		this.weekEnding = parser.getWeekEnding();
		this.fileName = parser.getFileName();
		
		validateRows(conn, parser);
	}
	
	/**
	 * Set "errorsFound" field in each of the employee rows
	 * Make sure this stays in sync with TimesheetRequest.validateAdd()
	 * @param conn
	 * @param parser 
	 * @throws Exception 
	 */
	private void validateRows(Connection conn, PayrollWorksheetParser parser) throws PayrollException, Exception {
		Division division = new Division();
		division.setDivisionNbr(Integer.valueOf(this.division));
		division.selectOne(conn);
					
		ApplicationProperties maxExpenseProperty = ApplicationProperty.get(conn, ApplicationProperty.EXPENSE_MAX);
		Double maxExpenseRate = maxExpenseProperty.getValueFloat().doubleValue();
		PayrollMessage payrollMessage = null;

		PayrollWorksheetHeader header = PayrollWorksheetValidator.validateHeader(conn, parser);
		this.employeeValidationMessages = PayrollWorksheetValidator.validatePayrollEmployees(conn, header, parser);
		
		for ( String key1 : this.employeeValidationMessages.keySet() ) {
			System.out.println("Emp: " + key1);
			HashMap<String, List<PayrollMessage>> rowMsgList = this.employeeValidationMessages.get(key1);					
			for ( String key2 : rowMsgList.keySet() ) {
				System.out.println("\tField " + key2);
				for ( PayrollMessage msg : rowMsgList.get(key2)) {
					System.out.println("\t\t" + msg.getErrorMessage().getErrorLevel().name() + "\t" + msg.getErrorMessage().getMessage());
				}
			}
		}
		
		/*
		for ( PayrollWorksheetEmployee row : employee ) {
			if ( ! row.isBlankRow() ) {		
				payrollMessage = TimesheetValidator.validateMinimumGovtPay(
						division, 
						Double.valueOf(row.getGrossPay()), 
						Double.valueOf(row.getExpenses()),
						Double.valueOf(row.getRegularHours()), 
						Double.valueOf(row.getVacationHours()), 
						Double.valueOf(row.getHolidayHours()));
				logger.log(Level.DEBUG, "MinGovtPay: " +  row.getRow() + ":" + payrollMessage.getErrorType() + ": " + payrollMessage.getErrorMessage().getMessage());
				if ( payrollMessage.getErrorType().equals(PayrollErrorType.OK)) {
					payrollMessage = TimesheetValidator.validateExcessExpense(maxExpenseRate, Double.valueOf(row.getExpensesSubmitted()), Double.valueOf(row.getGrossPay()));
					logger.log(Level.DEBUG, "ExcessExpense: "  +  row.getRow() + ":" +  payrollMessage.getErrorType() + ": " + payrollMessage.getErrorMessage().getMessage());
				}
				
				Boolean errorFound = ! payrollMessage.getErrorType().equals(PayrollErrorType.OK);
				
				// we'll figure out aliases later; if this isn't a legit name, a human needs to review it
				if ( ! TimesheetValidator.isEmployeeName(conn, row.getEmployeeName())) {
					errorFound = true;
					logger.log(Level.DEBUG, "Bad Name "  +  row.getRow());
				}
				
				row.setErrorsFound(errorFound);
			}
		}
			*/
		
	}

	public List<PayrollWorksheetEmployee> getEmployeeRecordList() {
		return timesheetRecords;
	}
	public void setEmployeeRecordList(List<PayrollWorksheetEmployee> employeeRecordList) {
		this.timesheetRecords = employeeRecordList;
	}

	public void addEmployeeRecord(PayrollWorksheetEmployee record) {
		if ( this.timesheetRecords == null ) {
			this.timesheetRecords = new ArrayList<PayrollWorksheetEmployee>();
		}
		this.timesheetRecords.add(record);
	}
	
	
	public WebMessagesStatus validate(Connection conn) throws Exception {
		WebMessages webMessages = new WebMessages();
		RequestValidator.validateDivisionNumber(conn, webMessages, DIVISION, division, true);
		if ( ! webMessages.containsKey(DIVISION)) { normalizeDivision(conn); }
		RequestValidator.validateString(webMessages, OPERATIONS_MANAGER_NAME, this.operationsManagerName, true);
		RequestValidator.validateDate(webMessages, WEEK_ENDING, this.weekEnding, "MM/dd/yy", true, null, null);
		if ( ! webMessages.containsKey(WEEK_ENDING)) { normalizeWeekEnding(); }
//		RequestValidator.validateStateLocale(conn, webMessages, STATE, this.state, true, (String)null);
		
		Locale stateLocale = null;
		try {
			stateLocale = PayrollUtils.alias2State(conn, this.state);
		} catch ( InvalidValueException e ) {
			webMessages.addMessage(STATE, "Invalid Value");
		}
		
		
		
		// any problems up to here are errors
		WebMessagesStatus webMessagesStatus = new WebMessagesStatus(webMessages, webMessages.isEmpty() ? ResponseCode.SUCCESS : ResponseCode.EDIT_FAILURE);

		// defining this outside the try so it can be used later.
		Locale locale = null;
		
		// we can only validate the city if the state is legit
		if ( ! webMessages.containsKey(STATE)) {
			if ( ! this.state.equalsIgnoreCase(stateLocale.getStateName() )) {
				webMessages.addMessage(STATE, "Suggested change");
				webMessagesStatus.setResponseCode(ResponseCode.EDIT_WARNING);
			}
			try {
				locale = PayrollUtils.alias2Locale(conn, this.city, stateLocale.getStateName());
				normalizeLocale(locale);
				if ( ! this.state.equalsIgnoreCase(this.normal.state) || ! this.city.equalsIgnoreCase(this.normal.city) ) {
					// the normalization process has changed something
					webMessagesStatus.addMessage(CITY, "Suggested locale change");
					if ( webMessagesStatus.getResponseCode().equals(ResponseCode.SUCCESS) ) {
						webMessagesStatus.setResponseCode(ResponseCode.EDIT_WARNING);
					}
				}
			} catch (InvalidValueException e) {
				webMessagesStatus.addMessage(CITY, "Invalid City/Jurisdiction");
				webMessagesStatus.setResponseCode(ResponseCode.EDIT_FAILURE);
			}
		}

		// check to see if a weekly payroll report has already been uploaded
		// for this division/week/locale id combination 
		// in the payroll_worksheet table
		if ( webMessages.isEmpty() || webMessagesStatus.getResponseCode() == ResponseCode.EDIT_WARNING ) { 
			Integer localeId = locale == null ? stateLocale.getLocaleId() : locale.getLocaleId();
			Integer returnCount=0;
			
				String sql = "select count(*) as record_count from payroll_worksheet where division_id=? and week_ending=? and locale_id=?";
							
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setInt (1, this.normal.divisionId);
				ps.setDate(2, new java.sql.Date(normal.weekEnding.getTimeInMillis()));
				ps.setInt (3, localeId);
								
				ResultSet rs = ps.executeQuery();
				
				if (rs.next()) {
					returnCount=rs.getInt("record_count");
				}
				rs.close();
				
				if(returnCount > 0) {
					webMessagesStatus.addMessage(FILENAME, "Duplicate Upload. Existing records will be replaced");
					webMessagesStatus.setResponseCode(ResponseCode.EDIT_WARNING);
				}
			
		}			
		return webMessagesStatus;		
	}

	public Map<String, HashMap<String, List<PayrollMessage>>> getEmployeeValidationMessages(){
		return this.employeeValidationMessages;
	}
	
	public String getCity() {
		return this.city;
	}
	public void setCity(String city) {	
		this.city = city;
	}
	public String getDivision() {
		return division;
	}
	public void setDivision(String division) {
		this.division = division;
	}
	
	public String getOperationsManagerName() {
		return operationsManagerName;
	}
	public void setOperationsManagerName(String operationsManagerName) {
		this.operationsManagerName = operationsManagerName;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public List<PayrollWorksheetEmployee> getTimesheetRecords() {
		return timesheetRecords;
	}
	public void setTimesheetRecords(List<PayrollWorksheetEmployee> timesheetRecords) {
		this.timesheetRecords = timesheetRecords;
	}
	public String getWeekEnding() {
		return weekEnding;
	}
	public void setWeekEnding(String weekEnding) {
		this.weekEnding = weekEnding;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public NormalizedValues getNormal() {
		return normal;
	}

	public void setNormal(NormalizedValues normal) {
		this.normal = normal;
	}

	public WebMessages getWebMessages() {
		return webMessages;
	}

	public void setWebMessages(WebMessages webMessages) {
		this.webMessages = webMessages;
	}

	
	private void normalizeDivision(Connection conn) throws Exception {
		try {
			Division division = new Division();
			division.setDivisionNbr(Integer.valueOf(this.division));
			division.selectOne(conn);
			normal.divisionId = division.getDivisionId();
		} catch ( InvalidValueException e) {
			// don't populate the normalized value if we can't translate
		}
	}
	
	private void normalizeWeekEnding() {
		try {
			SimpleDateFormat inbound = new SimpleDateFormat("MM/dd/yy");
			SimpleDateFormat outbound = new SimpleDateFormat("yyyy-MM-dd");
			normal.weekEnding = DateUtils.toCalendar(inbound.parse(this.weekEnding));
			normal.weekEndingDisplay = outbound.format(normal.weekEnding.getTime());
		} catch ( ParseException e) {
			// don't populate the normalized value if we can't translate
		}
	}
	
	private void normalizeLocale(Locale locale) {
		if ( locale.getLocaleTypeId().equals(LocaleType.STATE.name())) {
			normal.city = null;
			normal.state = locale.getStateName();
		} else {
			normal.city = locale.getName();
			normal.state = locale.getStateName();
		}
	}

	
	public class NormalizedValues extends ApplicationObject {
		private static final long serialVersionUID = 1L;
		public Integer divisionId;
		public String city;
		public String state;
		public Calendar weekEnding;
		public String weekEndingDisplay;
		public int localeId;
		
		public Integer getDivisionId() {
			return divisionId;
		}
		public String getCity() {
			return city;
		}

		public Integer getLocaleID() {
			return localeId;
		}
		
		public String getState() {
			return state;
		}
		public Calendar getWeekEnding() {
			return weekEnding;
		}
		public String getWeekEndingDisplay() {
			return weekEndingDisplay;
		}
		
		
		
	}
	
}