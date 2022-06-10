package com.ansi.scilla.web.payroll.response;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
//import java.util.Arrays;
//import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.Transformer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.common.payroll.common.PayrollUtils;
import com.ansi.scilla.common.payroll.common.PayrollWorksheetHeader;
import com.ansi.scilla.common.payroll.parser.PayrollWorksheetEmployee;
import com.ansi.scilla.common.payroll.parser.PayrollWorksheetParser;
import com.ansi.scilla.common.payroll.validator.PayrollMessage;
import com.ansi.scilla.common.utils.ErrorLevel;
import com.ansi.scilla.web.common.response.MessageResponse;

public class TimesheetImportResponse extends MessageResponse  {
	private static final long serialVersionUID = 1L;
	
	public static final String CITY = "city";
	public static final String DIVISION = "division";
	public static final String OPERATIONS_MANAGER_NAME = "operationsManagerName";
	public static final String STATE = "state";
	public static final String WEEK_ENDING = "weekEnding";
	public static final String FILENAME = "fileName";

	
	final Logger logger = LogManager.getLogger(TimesheetImportResponse.class);	

	private String city;
	private String division;
	private String operationsManagerName;
	private String state;
	private List<ValidatedEmployee> timesheetRecords;
	private String weekEnding;
	private String fileName;
//	private Map<String, HashMap<String, List<PayrollMessage>>> employeeValidationMessages;	
	private PayrollWorksheetHeader header;
	
		
	public TimesheetImportResponse() {	
		super();
	}

	public TimesheetImportResponse(Connection conn, PayrollWorksheetHeader header, PayrollWorksheetParser parser,
			Map<String, HashMap<String, List<PayrollMessage>>> employeeMsgs) {
		this();
		this.city = parser.getCity();
		this.division = parser.getDivision();
		this.operationsManagerName = parser.getOperationsManagerName();
		this.state = parser.getState();
		this.weekEnding = parser.getWeekEnding();
		this.fileName = parser.getFileName();
		this.header = header;
		EmployeeTransformer employeeTransformer = new EmployeeTransformer(employeeMsgs);
		Collection<ValidatedEmployee> validatedEmployeeList = CollectionUtils.collect(parser.getEmployeeRecordList(), employeeTransformer);
		this.timesheetRecords = IterableUtils.toList(validatedEmployeeList);
	}





	public List<ValidatedEmployee> getEmployeeRecordList() {
		return timesheetRecords;
	}
	public void setEmployeeRecordList(List<ValidatedEmployee> employeeRecordList) {
		this.timesheetRecords = employeeRecordList;
	}

	public void addEmployeeRecord(ValidatedEmployee record) {
		if ( this.timesheetRecords == null ) {
			this.timesheetRecords = new ArrayList<ValidatedEmployee>();
		}
		this.timesheetRecords.add(record);
	}
	
	
	

//	public Map<String, HashMap<String, List<PayrollMessage>>> getEmployeeValidationMessages(){
//		return this.employeeValidationMessages;
//	}
	
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
	public List<ValidatedEmployee> getTimesheetRecords() {
		return timesheetRecords;
	}
	public void setTimesheetRecords(List<ValidatedEmployee> timesheetRecords) {
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

	public PayrollWorksheetHeader getHeader() {
		return header;
	}

	public void setHeader(PayrollWorksheetHeader header) {
		this.header = header;
	}
	
	

	public class ValidatedEmployee extends PayrollWorksheetEmployee {

		private static final long serialVersionUID = 1L;
		private HashMap<String, List<PayrollMessage>> messages;
		
		public ValidatedEmployee(PayrollWorksheetEmployee employee)  {
			super();
			this.reportTitle = employee.getReportTitle();
			this.row = employee.getRow();
			this.employeeName = employee.getEmployeeName();
			this.expenses = employee.getExpenses();
			this.expensesAllowed = employee.getExpensesAllowed();
			this.expensesSubmitted = employee.getExpensesSubmitted();
			this.grossPay = employee.getGrossPay();
			this.holidayHours = employee.getHolidayHours();
			this.holidayPay = employee.getHolidayPay();
			this.otHours = employee.getOtHours();
			this.otPay = employee.getOtPay();
			this.directLabor = employee.getDirectLabor();
			this.productivity = employee.getProductivity();
			this.regularHours = employee.getRegularHours();
			this.regularPay = employee.getRegularPay();
			this.state = employee.getState();
			this.vacationHours = employee.getVacationHours();
			this.vacationPay = employee.getVacationPay();
			this.volume = employee.getVolume();
		}
		
		public ValidatedEmployee(PayrollWorksheetEmployee employee, HashMap<String, List<PayrollMessage>> messages) {
			this(employee);
			this.messages = messages;
		}
		public HashMap<String, List<PayrollMessage>> getMessages() {
			return messages;
		}
		public void setMessages(HashMap<String, List<PayrollMessage>> messages) {
			this.messages = messages;
		}
	}

	
	public class EmployeeTransformer implements Transformer<PayrollWorksheetEmployee, ValidatedEmployee> {
		private Map<String, HashMap<String, List<PayrollMessage>>> employeeMsgs;
				
		public EmployeeTransformer(Map<String, HashMap<String, List<PayrollMessage>>> employeeMsgs) {
			super();
			this.employeeMsgs = employeeMsgs;
		}


		@Override
		public ValidatedEmployee transform(PayrollWorksheetEmployee arg0) {
			ValidatedEmployee validatedEmployee = null;
			ErrorLevel maxErrorLevel = ErrorLevel.OK;
						
			if ( employeeMsgs.containsKey(arg0.getRow())) {
				validatedEmployee = new ValidatedEmployee(arg0, employeeMsgs.get(arg0.getRow()));
				
				for ( List<PayrollMessage> msgList : employeeMsgs.get(arg0.getRow()).values() ) {
					ErrorLevel errorLevel = PayrollUtils.maxErrorLevel(msgList);
					if ( errorLevel.level() > maxErrorLevel.level() ) {
						maxErrorLevel = errorLevel;
					}
				}
			} else {
				validatedEmployee = new ValidatedEmployee(arg0);
			}

			validatedEmployee.setErrorsFound(maxErrorLevel.level() > ErrorLevel.OK.level());
			return validatedEmployee;
		}
		
	}
	
	
	
}