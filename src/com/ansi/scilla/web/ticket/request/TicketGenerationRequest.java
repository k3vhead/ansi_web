package com.ansi.scilla.web.ticket.request;

import java.util.Date;

import com.ansi.scilla.web.common.request.AbstractRequest;
import com.ansi.scilla.web.common.request.RequiredForAdd;
import com.fasterxml.jackson.annotation.JsonFormat;

public class TicketGenerationRequest extends AbstractRequest {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer divisionId;
	private Date startDate;
	private Date endDate;
	
	@RequiredForAdd
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public Date getStartDate() {
		return startDate;
	}
	
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	@RequiredForAdd
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public Date getEndDate() {
		return endDate;
	}
	
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Integer getDivisionId() {
		return divisionId;
	}

	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}
	
	
	
}
