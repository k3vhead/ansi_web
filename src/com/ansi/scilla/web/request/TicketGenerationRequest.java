package com.ansi.scilla.web.request;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class TicketGenerationRequest extends AbstractRequest {
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
	
	
	
}
