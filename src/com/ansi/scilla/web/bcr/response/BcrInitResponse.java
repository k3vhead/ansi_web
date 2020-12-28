package com.ansi.scilla.web.bcr.response;

import java.util.Calendar;
import java.util.List;

import com.ansi.scilla.web.bcr.common.BcrUtils;
import com.ansi.scilla.web.common.response.MessageResponse;
import com.ansi.scilla.web.division.response.DivisionCountRecord;

public class BcrInitResponse extends MessageResponse {

	private static final long serialVersionUID = 1L;

	private List<DivisionCountRecord> divisionList;
	private List<DisplayMonth> displayMonthList;
	
	public BcrInitResponse() {
		super();
	}
	
	public BcrInitResponse(List<DivisionCountRecord> divisionList, Calendar workDay) {
		this();
		this.divisionList = divisionList;
		this.displayMonthList = makeDisplayYear(workDay);
	}

	public List<DivisionCountRecord> getDivisionList() {
		return divisionList;
	}

	public void setDivisionList(List<DivisionCountRecord> divisionList) {
		this.divisionList = divisionList;
	}

	public List<DisplayMonth> getDisplayMonthList() {
		return displayMonthList;
	}

	public void setDisplayMonthList(List<DisplayMonth> displayMonthList) {
		this.displayMonthList = displayMonthList;
	}

	private List<DisplayMonth> makeDisplayYear(Calendar workDay) {
		return BcrUtils.makeDisplayYear(workDay);
	}

	
	
}
