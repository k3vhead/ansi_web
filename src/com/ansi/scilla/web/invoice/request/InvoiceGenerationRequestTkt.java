package com.ansi.scilla.web.invoice.request;

import java.sql.Connection;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.common.db.Ticket;
import com.ansi.scilla.common.jobticket.TicketStatus;
import com.ansi.scilla.web.common.request.AbstractRequest;
import com.ansi.scilla.web.common.request.RequestValidator;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.invoice.response.InvoiceGenerationTktResponse;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class InvoiceGenerationRequestTkt extends AbstractRequest {

	private static final long serialVersionUID = 1L;

	public static final String INVOICE_DATE = "invoiceDate";
	public static final String TICKET_LIST = "ticketList";
	
	private Date invoiceDate;
	private Integer[] ticketList;
	
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd", timezone="America/Chicago")
	public Date getInvoiceDate() {
		return invoiceDate;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd", timezone="America/Chicago")
	public void setInvoiceDate(Date invoiceDate) {
		this.invoiceDate = invoiceDate;
	}
	public Integer[] getTicketList() {
		return ticketList;
	}
	public void setTicketList(Integer[] ticketList) {
		this.ticketList = ticketList;
	}
	
	
	public InvoiceGenerationTktResponse validate(Connection conn) throws Exception {
		InvoiceGenerationTktResponse response = new InvoiceGenerationTktResponse();
		WebMessages webMessages = new WebMessages();
		RequestValidator.validateDate(webMessages, INVOICE_DATE, this.invoiceDate, true, null, null);
		if ( this.ticketList == null || this.ticketList.length == 0 ) {
			webMessages.addMessage(TICKET_LIST, "At least one ticket must be selected");
		} else {
			for ( Integer ticketId : this.ticketList ) {
				String message = validateTicket(conn, ticketId);
				if ( StringUtils.isNotBlank(message) ) {
					response.addTicketError(ticketId, message);
				}
			}
		}
		
		response.setWebMessages(webMessages);
		return response;
	}
	
	
	private String validateTicket(Connection conn, Integer ticketId) throws Exception {
		String message = null;
		
		try {
			Ticket ticket = new Ticket();
			ticket.setTicketId(ticketId);
			ticket.selectOne(conn);
			if ( ticket.getInvoiceId() != null ) {
				message = "Ticket already invoiced";
			} else if ( ! ticket.getStatus().equals(TicketStatus.COMPLETED.code()) ) {
				message = "Invalid ticket status";
			}
		} catch ( RecordNotFoundException e) {
			message = "Invalid Ticket ID";
		}
		
		return message;
	}
}
