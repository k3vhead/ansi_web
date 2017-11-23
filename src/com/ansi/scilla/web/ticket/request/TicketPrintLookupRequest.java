package com.ansi.scilla.web.ticket.request;

import java.util.Calendar;

import com.ansi.scilla.web.common.response.MessageResponse;
import com.fasterxml.jackson.annotation.JsonFormat;

public class TicketPrintLookupRequest extends MessageResponse {

	private static final long serialVersionUID = 1L;

	private Calendar printDate;

	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public Calendar getPrintDate() {
		return printDate;
	}

	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public void setPrintDate(Calendar printDate) {
		this.printDate = printDate;
	}
	
	
}
