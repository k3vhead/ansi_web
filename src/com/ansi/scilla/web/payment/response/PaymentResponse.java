package com.ansi.scilla.web.payment.response;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import com.ansi.scilla.common.queries.PaymentTotals;
import com.ansi.scilla.common.queries.TicketPaymentTotals;
import com.ansi.scilla.web.response.MessageResponse;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class PaymentResponse extends MessageResponse {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private PaymentTotalsResponseItem paymentTotals;
	private List<PaymentTicketDetailResponseItem> ticketList;
	
	
	public PaymentResponse(){
		super();
	}
	
	public PaymentResponse(Connection conn, Integer paymentId) throws RecordNotFoundException, Exception{
		PaymentTotals paymentTotals = PaymentTotals.select(conn, paymentId);
		if ( paymentTotals.getAppliedAmount() == null ) {
			paymentTotals.setAppliedAmount(BigDecimal.ZERO);
		}
		if ( paymentTotals.getAppliedTaxAmt() == null ) {
			paymentTotals.setAppliedTaxAmt(BigDecimal.ZERO);
		}
		if ( paymentTotals.getAppliedTotal() == null ) {
			paymentTotals.setAppliedTotal(BigDecimal.ZERO);
		}
		if ( paymentTotals.getAvailable() == null ) {
			paymentTotals.setAvailable(paymentTotals.getPaymentAmount());
		}
		this.paymentTotals = new PaymentTotalsResponseItem(paymentTotals);

	}
	
	public PaymentResponse(Connection conn, Integer paymentId, Integer invoiceId) throws RecordNotFoundException, Exception{
		if (paymentId != null) {
			PaymentTotals paymentTotals = PaymentTotals.select(conn, paymentId);
			this.paymentTotals = new PaymentTotalsResponseItem(paymentTotals);
		}
		if (invoiceId != null) {
			ticketList = new ArrayList<PaymentTicketDetailResponseItem>();
			List<TicketPaymentTotals> queryList = TicketPaymentTotals.selectByInvoice( conn, invoiceId );
			
			for(TicketPaymentTotals query : queryList) {
				PaymentTicketDetailResponseItem item = new PaymentTicketDetailResponseItem(query);
				ticketList.add(item);
			}
		}
	}
	
	public PaymentTotalsResponseItem getPaymentTotals() {
		return paymentTotals;
	}
	
	public void setPaymentTotals(PaymentTotalsResponseItem paymentTotals) {
		this.paymentTotals = paymentTotals;
	}
	
}
