package com.ansi.scilla.web.payroll.response;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.web.common.response.MessageResponse;
import com.ansi.scilla.web.payroll.common.ExceptionReportRecord;
import com.ansi.scilla.web.payroll.query.ExceptionReportQuery;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class ExceptionReportResponse extends MessageResponse {

	private static final long serialVersionUID = 1L;

	private String group;
	private String groupDescription;
	private List <ExceptionReportRecord> recordList;
	
	
	
	
	public ExceptionReportResponse() {
		super();
	}
	
	public ExceptionReportResponse(Connection conn, Integer groupId) throws RecordNotFoundException, Exception {
		this();
		Division division = new Division();
		division.setDivisionId(groupId);
		division.selectOne(conn);
		this.group = division.getDivisionDisplay();
		this.groupDescription = division.getDescription();
		this.recordList = new ArrayList<ExceptionReportRecord> ();
		ResultSet rs = ExceptionReportQuery.execute(conn, groupId);
		while (rs.next()) {
			ExceptionReportRecord record = new ExceptionReportRecord(rs);
			this.recordList.add(record);
		}
		rs.close();
	} 
	
	
	

	public List<ExceptionReportRecord> getRecordList() {
		return recordList;
	}

	public void setRecordList(List<ExceptionReportRecord> recordList) {
		this.recordList = recordList;
	}

	public String getDiv() {
		return group;
	}

	public void setDiv(String div) {
		this.group = div;
	}

	public String getDivDescription() {
		return groupDescription;
	}

	public void setDivDescription(String divDescription) {
		this.groupDescription = divDescription;
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
