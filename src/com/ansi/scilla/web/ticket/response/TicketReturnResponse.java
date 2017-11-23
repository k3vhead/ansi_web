package com.ansi.scilla.web.ticket.response;

import java.sql.Connection;

import com.ansi.scilla.web.common.response.MessageResponse;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class TicketReturnResponse extends MessageResponse {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private TicketDetail ticketDetail;
	private InvoiceDetail invoiceDetail;
	
	public TicketReturnResponse(){
		super();
	}
	
	public TicketReturnResponse(Connection conn, Integer ticketId) throws RecordNotFoundException, Exception{
		this.ticketDetail = new TicketDetail(conn, ticketId);
		if(ticketDetail.getInvoiceId() != null){
			this.invoiceDetail = new InvoiceDetail(conn, ticketDetail.getInvoiceId());
		}
	}
	
	public TicketDetail getTicketDetail() {
		return ticketDetail;
	}
	
	public void setTicketDetail(TicketDetail ticketResponse) {
		this.ticketDetail = ticketResponse;
	}
	
	public InvoiceDetail getInvoiceDetail() {
		return invoiceDetail;
	}
	
	public void setInvoiceDetail(InvoiceDetail invoiceResponse) {
		this.invoiceDetail = invoiceResponse;
	}
	
	
}
