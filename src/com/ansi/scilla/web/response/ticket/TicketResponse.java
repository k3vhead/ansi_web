package com.ansi.scilla.web.response.ticket;

import com.ansi.scilla.web.response.MessageResponse;

public class TicketResponse extends MessageResponse {
	
	private TicketDetailResponse ticketResponse;
	private InvoiceDetailResponse invoiceResponse;
	
	public TicketResponse(){
		super();
	}
	
	public TicketDetailResponse getTicketResponse() {
		return ticketResponse;
	}
	
	public void setTicketResponse(TicketDetailResponse ticketResponse) {
		this.ticketResponse = ticketResponse;
	}
	
	public InvoiceDetailResponse getInvoiceResponse() {
		return invoiceResponse;
	}
	
	public void setInvoiceResponse(InvoiceDetailResponse invoiceResponse) {
		this.invoiceResponse = invoiceResponse;
	}
	
	
}
