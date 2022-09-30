package com.ansi.scilla.web.payroll.response;

import java.util.ArrayList;
import java.util.List;

import com.ansi.scilla.web.common.response.MessageResponse;

public class TimesheetSaveResponse extends MessageResponse {

	private static final long serialVersionUID = 1L;
	private List<TimesheetResponseEmployee> timesheetList;
	
	public List<TimesheetResponseEmployee> getTimesheetList() {
		return timesheetList;
	}

	public void setTimesheetList(List<TimesheetResponseEmployee> timesheetList) {
		this.timesheetList = timesheetList;
	}

	public void addTimesheet(TimesheetResponseEmployee timesheetResponse) {
		if ( this.timesheetList == null ) {
			this.timesheetList = new ArrayList<TimesheetResponseEmployee>();
		}
		this.timesheetList.add(timesheetResponse);
	}

}
