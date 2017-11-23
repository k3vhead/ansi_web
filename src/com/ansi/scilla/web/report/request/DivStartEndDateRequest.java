package com.ansi.scilla.web.report.request;

import java.util.Calendar;

import com.ansi.scilla.web.common.request.AbstractRequest;
import com.ansi.scilla.web.common.request.RequiredForAdd;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Generic request for reports requiring a division id, start date, and end date
 * @author dclewis
 *
 */
public class DivStartEndDateRequest extends AbstractRequest {

	private static final long serialVersionUID = 1L;

	private Integer divisionId;
	private Calendar startDate;
	private Calendar endDate;
	
	/**
	 * This doesn't have the required annotation because it will frequently be included in the URL
	 * and therefore redundant in the json
	 * @return
	 */
	public Integer getDivisionId() {
		return divisionId;
	}

	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}

	@RequiredForAdd
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public Calendar getStartDate() {
		return startDate;
	}

	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public void setStartDate(Calendar startDate) {
		this.startDate = startDate;
	}

	@RequiredForAdd
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public Calendar getEndDate() {
		return endDate;
	}

	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public void setEndDate(Calendar endDate) {
		this.endDate = endDate;
	}
	
	
}
