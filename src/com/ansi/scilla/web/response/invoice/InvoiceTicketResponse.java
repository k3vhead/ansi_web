package com.ansi.scilla.web.response.invoice;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.ansi.scilla.common.address.AddressUtils;
import com.ansi.scilla.common.db.Address;
import com.ansi.scilla.common.queries.TicketPaymentTotals;
import com.ansi.scilla.web.response.MessageResponse;
import com.ansi.scilla.web.response.address.AddressResponseRecord;
import com.ansi.scilla.web.response.payment.TicketPaymentTotalItem;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class InvoiceTicketResponse extends MessageResponse {

	private static final long serialVersionUID = 1L;

	private AddressResponseRecord address;
	List<TicketPaymentTotalItem> ticketList;
	
	public InvoiceTicketResponse() {
		super();
	}
	public InvoiceTicketResponse(Connection conn, Integer invoiceId) throws RecordNotFoundException, Exception {
		Address address = AddressUtils.getBillToForInvoice(conn, invoiceId);
		this.address = new AddressResponseRecord(address);
		List<TicketPaymentTotals> totalsList = TicketPaymentTotals.selectByInvoice(conn, invoiceId);
		makeTicketList(totalsList);
	}
	public InvoiceTicketResponse(AddressResponseRecord address, List<TicketPaymentTotals> totalsList) throws IllegalAccessException, InvocationTargetException {
		this.address = address;
		makeTicketList(totalsList);
	}
	
	public AddressResponseRecord getAddress() {
		return address;
	}
	public void setAddress(AddressResponseRecord address) {
		this.address = address;
	}
	public List<TicketPaymentTotalItem> getTicketList() {
		return ticketList;
	}
	public void setTicketList(List<TicketPaymentTotalItem> ticketList) {
		this.ticketList = ticketList;
	}
	private void makeTicketList(List<TicketPaymentTotals> totalsList) throws IllegalAccessException, InvocationTargetException {
		this.ticketList = new ArrayList<TicketPaymentTotalItem>();
		for ( TicketPaymentTotals ticket : totalsList ) {
			this.ticketList.add(new TicketPaymentTotalItem(ticket));
		}
		Collections.sort(this.ticketList, new Comparator<TicketPaymentTotalItem>() {
			public int compare(TicketPaymentTotalItem o1, TicketPaymentTotalItem o2) {
	
				int ret = o1.getStartDate().compareTo(o2.getStartDate());
				return ret;
			}
		});		
	}
	
	
}
