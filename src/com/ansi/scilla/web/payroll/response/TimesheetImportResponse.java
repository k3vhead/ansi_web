package com.ansi.scilla.web.payroll.response;

import java.sql.Connection;
import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Calendar;
import java.util.List;

import com.ansi.scilla.common.payroll.validator.worksheet.ValidatedWorksheet;
import com.ansi.scilla.common.payroll.validator.worksheet.ValidatedWorksheetEmployee;
import com.ansi.scilla.common.payroll.validator.worksheet.ValidatedWorksheetHeader;
import com.ansi.scilla.web.common.response.MessageResponse;

public class TimesheetImportResponse extends MessageResponse  {
	private static final long serialVersionUID = 1L;
	
	private String fileName;
	private ValidatedWorksheetHeader worksheetHeader;
	private List<ValidatedWorksheetEmployee> employeeList;
	
	public TimesheetImportResponse() {	
		super();
	}

	public TimesheetImportResponse(Connection conn, String fileName, ValidatedWorksheet validatedWorksheet) {
		this();
		this.fileName = fileName;
		this.worksheetHeader = validatedWorksheet.getHeader();
		this.employeeList = validatedWorksheet.getTimesheetRecords();
	}

//	public TimesheetImportResponse(Connection conn, PayrollWorksheetHeader header, PayrollWorksheetParser parser,
//		Map<String, HashMap<String, List<PayrollMessage>>> employeeMsgs) {
//		this();
//		PayrollWorksheetHeader worksheetHeader = parser.getHeader();
//		this.city = worksheetHeader.getCity();
//		this.division = worksheetHeader.getDivisionNbr();
//		this.operationsManagerName = worksheetHeader.getOperationsManagerName();
//		this.state = worksheetHeader.getState();
//		this.weekEnding = worksheetHeader.getWeekEnding();
//		this.fileName = parser.getFileName();
//		this.header = header;
//		EmployeeTransformer employeeTransformer = new EmployeeTransformer(employeeMsgs);
//		Collection<ValidatedEmployee> validatedEmployeeList = CollectionUtils.collect(worksheetHeader.getEmployeeRecordList(), employeeTransformer);
//		this.timesheetRecords = IterableUtils.toList(validatedEmployeeList);
//	}



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

	public List<ValidatedWorksheetEmployee> getEmployeeList() {
		return employeeList;
	}

	//	public TimesheetImportResponse(Connection conn, PayrollWorksheetHeader header, PayrollWorksheetParser parser,
	//		Map<String, HashMap<String, List<PayrollMessage>>> employeeMsgs) {
	//		this();
	//		PayrollWorksheetHeader worksheetHeader = parser.getHeader();
	//		this.city = worksheetHeader.getCity();
	//		this.division = worksheetHeader.getDivisionNbr();
	//		this.operationsManagerName = worksheetHeader.getOperationsManagerName();
	//		this.state = worksheetHeader.getState();
	//		this.weekEnding = worksheetHeader.getWeekEnding();
	//		this.fileName = parser.getFileName();
	//		this.header = header;
	//		EmployeeTransformer employeeTransformer = new EmployeeTransformer(employeeMsgs);
	//		Collection<ValidatedEmployee> validatedEmployeeList = CollectionUtils.collect(worksheetHeader.getEmployeeRecordList(), employeeTransformer);
	//		this.timesheetRecords = IterableUtils.toList(validatedEmployeeList);
	//	}
	
	
	
		public void setEmployeeList(List<ValidatedWorksheetEmployee> employeeList) {
		this.employeeList = employeeList;
	}

		public void addEmployeeRecord(ValidatedWorksheetEmployee record) {
			if ( this.employeeList == null ) {
				this.employeeList = new ArrayList<ValidatedWorksheetEmployee>();
			}
			this.employeeList.add(record);
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