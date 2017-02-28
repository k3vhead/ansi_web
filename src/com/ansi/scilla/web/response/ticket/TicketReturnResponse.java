package com.ansi.scilla.web.response.ticket;

import com.ansi.scilla.web.response.MessageResponse;

public class TicketReturnResponse extends MessageResponse {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private TicketDetail ticketResponse;
	private InvoiceDetail invoiceResponse;
	
	public TicketReturnResponse(){
		super();
	}
	
	public TicketDetail getTicketResponse() {
		return ticketResponse;
	}
	
	public void setTicketResponse(TicketDetail ticketResponse) {
		this.ticketResponse = ticketResponse;
	}
	
	public InvoiceDetail getInvoiceResponse() {
		return invoiceResponse;
	}
	
	public void setInvoiceResponse(InvoiceDetail invoiceResponse) {
		this.invoiceResponse = invoiceResponse;
	}
	
	
}
