package com.ansi.scilla.web.request;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class InvoicePrintRequest extends AbstractRequest {

	private static final long serialVersionUID = 1L;

	private String divisionId;
	private Date printDate;
	private Date dueDate;
	
	@RequiredForAdd
	public String getDivisionId() {
		return divisionId;
	}
	public void setDivisionId(String divisionId) {
		this.divisionId = divisionId;
	}
	@RequiredForAdd
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public Date getPrintDate() {
		return printDate;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public void setPrintDate(Date printDate) {
		this.printDate = printDate;
	}
	@RequiredForAdd
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public Date getDueDate() {
		return dueDate;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}
	
	
	
	
	
	
}
