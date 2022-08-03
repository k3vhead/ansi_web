package com.ansi.scilla.web.payroll.response;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
//import java.util.Arrays;
//import java.util.Calendar;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.payroll.parser.worksheet.PayrollWorksheetEmployee.FieldLocation;
import com.ansi.scilla.common.payroll.validator.common.PayrollMessage;
import com.ansi.scilla.common.payroll.validator.worksheet.ValidatedWorksheet;
import com.ansi.scilla.common.payroll.validator.worksheet.ValidatedWorksheetEmployee;
import com.ansi.scilla.common.payroll.validator.worksheet.ValidatedWorksheetHeader;
import com.ansi.scilla.common.utils.ErrorLevel;
import com.ansi.scilla.web.common.response.MessageResponse;

public class TimesheetImportResponse extends MessageResponse  {
	private static final long serialVersionUID = 1L;

	private String fileName;
	private ValidatedWorksheetHeader worksheetHeader;
	private ErrorLevel headerError;
	private List<ResponseEmployee> employeeList;

	public TimesheetImportResponse() {	
		super();
	}

	public TimesheetImportResponse(Connection conn, String fileName, ValidatedWorksheet validatedWorksheet) {
		this();
		this.fileName = fileName;
		this.worksheetHeader = validatedWorksheet.getHeader();
		this.employeeList = IterableUtils.toList(CollectionUtils.collect(validatedWorksheet.getTimesheetRecords(), new EmployeeTransformer()));
		this.headerError = this.worksheetHeader.maxErrorLevel();
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public ValidatedWorksheetHeader getWorksheetHeader() {
		return worksheetHeader;
	}

	public void setWorksheetHeader(ValidatedWorksheetHeader worksheetHeader) {
		this.worksheetHeader = worksheetHeader;
	}

	public List<ResponseEmployee> getEmployeeList() {
		return employeeList;
	}

	public void setEmployeeList(List<ResponseEmployee> employeeList) {
		this.employeeList = employeeList;
	}

	public ErrorLevel getHeaderError() {
		return headerError;
	}

	public void setHeaderError(ErrorLevel headerError) {
		this.headerError = headerError;
	}

	public void addEmployeeRecord(ValidatedWorksheetEmployee record) {
		if ( this.employeeList == null ) {
			this.employeeList = new ArrayList<ResponseEmployee>();
		}
		this.employeeList.add(new ResponseEmployee(record));
	}


	public class ResponseEmployee extends ApplicationObject {

		private static final long serialVersionUID = 1L;
		
		private Integer row;
	    private String employeeName;
	    private Double regularHours;
	    private Double regularPay;
	    private Double expenses;
	    private Double otHours;
	    private Double otPay;
	    private Double vacationHours;
	    private Double vacationPay;
	    private Double holidayHours;
	    private Double holidayPay;
	    private Double grossPay;
	    private Double expensesSubmitted;
	    private Double expensesAllowed;
	    private Double volume;
	    private Double directLabor;
	    private Double productivity;
	    
	    private Integer divisionNbr;
	    private Integer divisionId;
	    private String divisionDisplay;
	    private Integer employeeCode;
	    private ErrorLevel errorLevel;
		private HashMap<String, List<PayrollMessage>> messageList;
		private Double unionRate;
		private Integer standardDivisionId;
		private Integer standardCompanyId;
		
		public ResponseEmployee(ValidatedWorksheetEmployee employee) {
			super();
			this.row = employee.getRow();
		    this.employeeName =
		    	StringUtils.isBlank(employee.getEmployeeFirstName()) && StringUtils.isBlank(employee.getEmployeeLastName()) ?
		    	employee.getEmployee().getEmployeeName() :
	    		employee.getEmployeeFirstName() + " " + employee.getEmployeeLastName();
		    this.regularHours = employee.getValues().get(FieldLocation.REGULAR_HOURS.fieldName());
		    this.regularPay = employee.getValues().get(FieldLocation.REGULAR_PAY.fieldName());
		    this.expenses = employee.getValues().get(FieldLocation.EXPENSES.fieldName());
		    this.otHours = employee.getValues().get(FieldLocation.OT_HOURS.fieldName());
		    this.otPay = employee.getValues().get(FieldLocation.OT_PAY.fieldName());
		    this.vacationHours = employee.getValues().get(FieldLocation.VACATION_HOURS.fieldName());
		    this.vacationPay = employee.getValues().get(FieldLocation.VACATION_PAY.fieldName());
		    this.holidayHours = employee.getValues().get(FieldLocation.HOLIDAY_HOURS.fieldName());
		    this.holidayPay = employee.getValues().get(FieldLocation.HOLIDAY_PAY.fieldName());
		    this.grossPay = employee.getValues().get(FieldLocation.GROSS_PAY.fieldName());
		    this.expensesSubmitted = employee.getValues().get(FieldLocation.EXPENSES_SUBMITTED.fieldName());
		    this.expensesAllowed = employee.getValues().get(FieldLocation.EXPENSES_ALLOWED.fieldName());
		    this.volume = employee.getValues().get(FieldLocation.VOLUME.fieldName());
		    this.directLabor = employee.getValues().get(FieldLocation.DIRECT_LABOR.fieldName());
		    this.productivity = employee.getValues().get(FieldLocation.PRODUCTIVITY.fieldName());
		    
		    this.divisionNbr = employee.getDivision().getDivisionNbr();
		    this.divisionId = employee.getDivision().getDivisionId();
		    this.divisionDisplay = employee.getDivision().getDivisionDisplay();
		    this.employeeCode = employee.getEmployeeCode();
		    this.errorLevel = employee.getErrorLevel();
			this.messageList = employee.getMessageList();
			this.unionRate = employee.getUnionRate();
			this.standardDivisionId = employee.getStandardDivisionId();
			this.standardCompanyId = employee.getStandardCompanyId();
		}

		public Integer getRow() {
			return row;
		}

		public String getEmployeeName() {
			return employeeName;
		}

		public Double getRegularHours() {
			return regularHours;
		}

		public Double getRegularPay() {
			return regularPay;
		}

		public Double getExpenses() {
			return expenses;
		}

		public Double getOtHours() {
			return otHours;
		}

		public Double getOtPay() {
			return otPay;
		}

		public Double getVacationHours() {
			return vacationHours;
		}

		public Double getVacationPay() {
			return vacationPay;
		}

		public Double getHolidayHours() {
			return holidayHours;
		}

		public Double getHolidayPay() {
			return holidayPay;
		}

		public Double getGrossPay() {
			return grossPay;
		}

		public Double getExpensesSubmitted() {
			return expensesSubmitted;
		}

		public Double getExpensesAllowed() {
			return expensesAllowed;
		}

		public Double getVolume() {
			return volume;
		}

		public Double getDirectLabor() {
			return directLabor;
		}

		public Double getProductivity() {
			return productivity;
		}

		public Integer getDivisionNbr() {
			return divisionNbr;
		}

		public Integer getDivisionId() {
			return divisionId;
		}

		public String getDivisionDisplay() {
			return divisionDisplay;
		}

		public Integer getEmployeeCode() {
			return employeeCode;
		}

		public ErrorLevel getErrorLevel() {
			return errorLevel;
		}

		public HashMap<String, List<PayrollMessage>> getMessageList() {
			return messageList;
		}

		public Double getUnionRate() {
			return unionRate;
		}

		public Integer getStandardDivisionId() {
			return standardDivisionId;
		}

		public Integer getStandardCompanyId() {
			return standardCompanyId;
		}
		
	}


	public class EmployeeTransformer implements Transformer<ValidatedWorksheetEmployee, ResponseEmployee> {
		@Override
		public ResponseEmployee transform(ValidatedWorksheetEmployee arg0) {
			return new ResponseEmployee(arg0);
		}

	}
	/*
	public class ValidatedEmployee extends PayrollWorksheetEmployee {

		private static final long serialVersionUID = 1L;
		private HashMap<String, List<PayrollMessage>> messages;
		private Map<String, HashMap<String, List<PayrollMessage>>> employeeMsgs;

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

			Logger logger = LogManager.getLogger(TimesheetImportResponse.class);			
			//logger.log(Level.DEBUG, "TimesheetImportResponse: ");
			//logger.log(Level.DEBUG, "TimesheetImportResponse : employeeMsgs " + employeeMsgs);

			if(employeeMsgs == null) {

			}

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

	 */

}