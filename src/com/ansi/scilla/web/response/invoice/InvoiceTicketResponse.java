package com.ansi.scilla.web.response.invoice;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
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
	private List<TicketPaymentTotalItem> ticketList;
	private BigDecimal totalBalance;
	private BigDecimal totalPayInvoice;
	private BigDecimal totalPayTax;
	private BigDecimal totalWriteOff;
	
	public InvoiceTicketResponse() {
		super();
		this.totalBalance = new BigDecimal(0);
		this.totalPayInvoice = new BigDecimal(0);
		this.totalPayTax = new BigDecimal(0);
		this.totalWriteOff = new BigDecimal(0);
	}
	public InvoiceTicketResponse(Connection conn, Integer invoiceId) throws RecordNotFoundException, Exception {
		this();
		Address address = AddressUtils.getBillToForInvoice(conn, invoiceId);
		this.address = new AddressResponseRecord(address);
		List<TicketPaymentTotals> totalsList = TicketPaymentTotals.selectByInvoice(conn, invoiceId);
		makeTicketList(totalsList);
	}
	public InvoiceTicketResponse(AddressResponseRecord address, List<TicketPaymentTotals> totalsList) throws IllegalAccessException, InvocationTargetException {
		this();
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

	public BigDecimal getTotalBalance() {
		return totalBalance;
	}
	public void setTotalBalance(BigDecimal totalBalance) {
		this.totalBalance = totalBalance;
	}
	public BigDecimal getTotalPayInvoice() {
		return totalPayInvoice;
	}
	public void setTotalPayInvoice(BigDecimal totalPayInvoice) {
		this.totalPayInvoice = totalPayInvoice;
	}
	public BigDecimal getTotalPayTax() {
		return totalPayTax;
	}
	public void setTotalPayTax(BigDecimal totalPayTax) {
		this.totalPayTax = totalPayTax;
	}
	public BigDecimal getTotalWriteOff() {
		return totalWriteOff;
	}
	public void setTotalWriteOff(BigDecimal totalWriteOff) {
		this.totalWriteOff = totalWriteOff;
	}
	private void makeTicketList(List<TicketPaymentTotals> totalsList) throws IllegalAccessException, InvocationTargetException {
		this.ticketList = new ArrayList<TicketPaymentTotalItem>();
		for ( TicketPaymentTotals ticket : totalsList ) {
			TicketPaymentTotalItem total = new TicketPaymentTotalItem(ticket);
			this.ticketList.add(total);
			this.totalBalance = this.totalBalance.add(total.getTotalBalance());
			this.totalPayInvoice = this.totalPayInvoice.add(total.getTotalVolPaid());
			this.totalPayTax = this.totalPayTax.add(total.getTotalTaxPaid());
//			this.totalWriteOff = this.totalWriteOff.add(total.getWriteOff());   this is for version 2
		}
		Collections.sort(this.ticketList, new Comparator<TicketPaymentTotalItem>() {
			public int compare(TicketPaymentTotalItem o1, TicketPaymentTotalItem o2) {
	
				int ret = o1.getStartDate().compareTo(o2.getStartDate());
				return ret;
			}
		});		
	}
	

}