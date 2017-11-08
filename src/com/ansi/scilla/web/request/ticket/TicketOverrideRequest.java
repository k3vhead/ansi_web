package com.ansi.scilla.web.request.ticket;

import java.util.Calendar;

import com.fasterxml.jackson.annotation.JsonFormat;

public class TicketOverrideRequest extends TicketReturnRequest {

	private static final long serialVersionUID = 1L;

	public static final String DIVISION_ID = "divisionId";
	
	private Integer divisionId;
	private Integer invoiceId;
	private Calendar invoiceDate;
	
	public TicketOverrideRequest() {
		super();
	}

	public Integer getDivisionId() {
		return divisionId;
	}

	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}

	public Integer getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(Integer invoiceId) {
		this.invoiceId = invoiceId;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public Calendar getInvoiceDate() {
		return invoiceDate;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public void setInvoiceDate(Calendar invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	
}
