package com.ansi.scilla.web.payroll.response;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
//import java.util.Arrays;
//import java.util.Calendar;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.common.db.Locale;
import com.ansi.scilla.common.exceptions.InvalidValueException;
import com.ansi.scilla.common.payroll.common.PayrollUtils;
import com.ansi.scilla.common.payroll.parser.NotATimesheetException;
import com.ansi.scilla.common.payroll.parser.PayrollWorksheetEmployee;
import com.ansi.scilla.common.payroll.parser.PayrollWorksheetParser;
import com.ansi.scilla.common.utils.LocaleType;
import com.ansi.scilla.web.common.request.RequestValidator;
import com.ansi.scilla.web.common.response.MessageResponse;
import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.response.WebMessagesStatus;
import com.ansi.scilla.web.payroll.request.TimesheetImportRequest;

public class TimesheetImportResponse extends MessageResponse {
	
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
	
	private TimesheetImportResponse(Connection conn, PayrollWorksheetParser parser ) {
		this();
		this.city = parser.getCity();
		this.setDivision(division);  division = parser.getDivision();
		this.operationsManagerName = parser.getOperationsManagerName();
		this.state = parser.getState();
		this.timesheetRecords = parser.getTimesheetRecords();
		this.weekEnding = parser.getWeekEnding();
		this.fileName = parser.getFileName();
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

		// we can only validate the city if the state is legit
		if ( ! webMessages.containsKey(STATE)) {
			if ( ! this.state.equalsIgnoreCase(stateLocale.getName() )) {
				webMessages.addMessage(STATE, "Suggested change");
				webMessagesStatus.setResponseCode(ResponseCode.EDIT_WARNING);
			}
			try {
				Locale locale = PayrollUtils.alias2Locale(conn, this.city, stateLocale.getStateName());
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

		return webMessagesStatus;		
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
		
		public Integer getDivisionId() {
			return divisionId;
		}
		public String getCity() {
			return city;
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