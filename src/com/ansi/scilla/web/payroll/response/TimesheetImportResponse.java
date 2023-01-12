package com.ansi.scilla.web.payroll.response;

import java.sql.Connection;
import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Calendar;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IterableUtils;

import com.ansi.scilla.common.payroll.validator.worksheet.ValidatedWorksheet;
import com.ansi.scilla.common.payroll.validator.worksheet.ValidatedWorksheetEmployee;
import com.ansi.scilla.common.payroll.validator.worksheet.ValidatedWorksheetHeader;
import com.ansi.scilla.common.utils.ErrorLevel;
import com.ansi.scilla.web.common.response.MessageResponse;
import com.ansi.scilla.web.payroll.common.EmployeeTransformer;

public class TimesheetImportResponse extends MessageResponse  {
	private static final long serialVersionUID = 1L;

	private String fileName;
	private ValidatedWorksheetHeader worksheetHeader;
	private ErrorLevel headerError;
	private List<TimesheetEmployee> employeeList;

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

	public List<TimesheetEmployee> getEmployeeList() {
		return employeeList;
	}

	public void setEmployeeList(List<TimesheetEmployee> employeeList) {
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
			this.employeeList = new ArrayList<TimesheetEmployee>();
		}
		this.employeeList.add(new TimesheetEmployee(record));
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