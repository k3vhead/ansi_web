package com.ansi.scilla.web.invoice.response;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.web.common.response.MessageResponse;

public class InvoiceGenerationTktResponse extends MessageResponse {

	private static final long serialVersionUID = 1L;
	
	private List<TicketError> ticketErrorList;
	private List<InvoiceDisplay> invoiceList;
		
	public List<TicketError> getTicketErrorList() {
		return ticketErrorList;
	}
	public void setTicketErrorList(List<TicketError> ticketErrorList) {
		this.ticketErrorList = ticketErrorList;
	}

	public List<InvoiceDisplay> getInvoiceList() {
		return invoiceList;
	}
	public void setInvoiceList(List<InvoiceDisplay> invoiceList) {
		this.invoiceList = invoiceList;
	}
	
	public void addTicketError(Integer ticketId, String message) {
		if ( this.ticketErrorList == null ) {
			this.ticketErrorList = new ArrayList<TicketError>();
		}
		this.ticketErrorList.add( new TicketError(ticketId, message) );
		Collections.sort(this.ticketErrorList);
	}
	
	public void addInvoiceDisplay(ResultSet rs) throws SQLException {
		if ( this.invoiceList == null ) {
			this.invoiceList = new ArrayList<InvoiceDisplay>();
		}
		this.invoiceList.add( new InvoiceDisplay(rs) );
		Collections.sort(this.invoiceList);
	}

	
	public class TicketError extends ApplicationObject implements Comparable<TicketError> {
		private static final long serialVersionUID = 1L;
		public Integer ticketId;
		public String message;
		public TicketError(Integer ticketId, String message) {
			super();
			this.ticketId = ticketId;
			this.message = message;
		}
		public Integer getTicketId() {
			return ticketId;
		}
		public void setTicketId(Integer ticketId) {
			this.ticketId = ticketId;
		}
		public String getMessage() {
			return message;
		}
		public void setMessage(String message) {
			this.message = message;
		}
		@Override
		public int compareTo(TicketError o) {
			return o.ticketId.compareTo(this.ticketId);
		}
		
	}

	public class InvoiceDisplay extends ApplicationObject implements Comparable<InvoiceDisplay> {

		private static final long serialVersionUID = 1L;
		public Integer invoiceId;
		public String div;
		public Integer ticketCount;
		public BigDecimal invoiceAmt;
		
		public InvoiceDisplay(ResultSet rs) throws SQLException {
			super();
			this.invoiceId = rs.getInt("invoice_id");
			this.div = rs.getString("div");
			this.ticketCount = rs.getInt("ticket_count");
			this.invoiceAmt = rs.getBigDecimal("invoice_amount");
		}

		public Integer getInvoiceId() {
			return invoiceId;
		}

		public String getDiv() {
			return div;
		}

		public Integer getTicketCount() {
			return ticketCount;
		}

		public BigDecimal getInvoiceAmt() {
			return invoiceAmt;
		}

		@Override
		public int compareTo(InvoiceDisplay o) {
			return this.invoiceId.compareTo(o.getInvoiceId());
		}
		
	}
}
