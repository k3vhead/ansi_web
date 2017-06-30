package com.ansi.scilla.web.servlets.payment;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.db.PermissionLevel;
import com.ansi.scilla.common.db.Ticket;
import com.ansi.scilla.common.db.TicketPayment;
import com.ansi.scilla.common.jobticket.TicketStatus;
import com.ansi.scilla.common.jobticket.TicketType;
import com.ansi.scilla.common.jobticket.TicketUtils;
import com.ansi.scilla.web.common.AnsiURL;
import com.ansi.scilla.web.common.AppUtils;
import com.ansi.scilla.web.common.Permission;
import com.ansi.scilla.web.common.ResponseCode;
import com.ansi.scilla.web.common.WebMessages;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.ResourceNotFoundException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.request.payment.ApplyPaymentRequest;
import com.ansi.scilla.web.request.payment.ApplyPaymentRequestItem;
import com.ansi.scilla.web.response.invoice.InvoiceTicketResponse;
import com.ansi.scilla.web.response.payment.ApplyPaymentResponse;
import com.ansi.scilla.web.response.payment.PaymentResponse;
import com.ansi.scilla.web.servlets.AbstractServlet;
import com.ansi.scilla.web.struts.SessionData;
import com.ansi.scilla.web.struts.SessionUser;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.thewebthing.commons.db2.RecordNotFoundException;



public class ApplyPaymentServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;



	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Connection conn = null;
		AnsiURL url = null;
		try {
			try{
				conn = AppUtils.getDBCPConn();
				conn.setAutoCommit(false);
				String jsonString = super.makeJsonString(request);
				System.out.println(jsonString);			
				ApplyPaymentRequest paymentRequest = new  ApplyPaymentRequest();
				AppUtils.json2object(jsonString, paymentRequest);
				url = new AnsiURL(request, "applyPayment", new String[] {PaymentRequestType.VERIFY.name().toLowerCase(), PaymentRequestType.COMMIT.name().toLowerCase()});
				SessionData sessionData = AppUtils.validateSession(request, Permission.PAYMENT, PermissionLevel.PERMISSION_LEVEL_IS_WRITE);
				SessionUser sessionUser = sessionData.getUser();
				
				PaymentRequestType requestType = PaymentRequestType.valueOf(url.getCommand().toUpperCase());
				if ( StringUtils.isBlank(url.getCommand())) {
					throw new ResourceNotFoundException();				 
				} else {
					ApplyPaymentResponse data = processRequest(conn, requestType, paymentRequest, sessionUser);
					super.sendResponse(conn, response, ResponseCode.SUCCESS, data);
				}
			} catch ( InvalidFormatException e ) {
				String badField = super.findBadField(e.toString());
				ApplyPaymentResponse data = new ApplyPaymentResponse();
				WebMessages messages = new WebMessages();
				messages.addMessage(badField, "Invalid Format");
				data.setWebMessages(messages);
				super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, data);
			} catch (TimeoutException | NotAllowedException | ExpiredLoginException e1) {
				super.sendForbidden(response);
			} catch ( ResourceNotFoundException e) {
				System.out.println("*** ApplyPayment 404:  " + e + "****");
				super.sendNotFound(response);
			}
		} catch ( Exception e) {
			AppUtils.logException(e);
			AppUtils.rollbackQuiet(conn);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}
	}



	private ApplyPaymentResponse processRequest(Connection conn, PaymentRequestType requestType, ApplyPaymentRequest paymentRequest,
			SessionUser sessionUser) throws RecordNotFoundException, Exception {
		ApplyPaymentResponse data = new ApplyPaymentResponse();
		ApplyPaymentDetail detail =  new ApplyPaymentDetail(conn, paymentRequest);

		
		if ( requestType.equals(PaymentRequestType.VERIFY)) {
			data = doVerify(conn, detail);
		} else if ( requestType.equals(PaymentRequestType.COMMIT)) {
			data = doCommit(conn, detail, sessionUser);
		} else {
			throw new ResourceNotFoundException();
		}
		data.setApplyPaymentRequest(paymentRequest);				

		return data;
	}



	private ApplyPaymentResponse doVerify(Connection conn, ApplyPaymentDetail detail) throws RecordNotFoundException, Exception {
		ApplyPaymentResponse response = new ApplyPaymentResponse();
		response.setExcessCash(detail.excessCash);
		response.setFeeAmount(detail.feeAmount);
		response.setAvailableFromPayment(detail.availableFromPayment);
		response.setInvoiceBalance(detail.invoiceBalance);
		
		Double totalPayInvoice = 0.0D;
		Double totalPayTax = 0.0D;
		for ( ApplyPaymentRequestItem item : detail.ticketList) {
			Double payInvoice = item.getPayInvoice() == null ? 0.0D : item.getPayInvoice();
			Double payTax = item.getPayTax() == null ? 0.0D : item.getPayTax();
			totalPayInvoice = totalPayInvoice + payInvoice;
			totalPayTax = totalPayTax + payTax;
		}
		response.setTotalPayInvoice(new BigDecimal(totalPayInvoice));
		response.setTotalPayTax(new BigDecimal(totalPayTax));
		
		BigDecimal unappliedAmount = detail.availableFromPayment;
		unappliedAmount = unappliedAmount.subtract(new BigDecimal(totalPayInvoice));
		unappliedAmount = unappliedAmount.subtract(new BigDecimal(totalPayTax));
		unappliedAmount = unappliedAmount.subtract(detail.excessCash);
		unappliedAmount = unappliedAmount.subtract(detail.feeAmount);
		response.setUnappliedAmount(unappliedAmount);
		
		return response;
	}



	private ApplyPaymentResponse doCommit(Connection conn, ApplyPaymentDetail detail, SessionUser sessionUser) throws Exception {
		ApplyPaymentResponse response = new ApplyPaymentResponse(); 
		Double totalPayInvoice = 0.0D;
		Double totalPayTax = 0.0D;
		
		for ( ApplyPaymentRequestItem item : detail.ticketList) {
			if ( item.getPayInvoice() != null ) {
				totalPayInvoice = totalPayInvoice + item.getPayInvoice();
			}
			if ( item.getPayTax() != null ) {
				totalPayTax = totalPayTax + item.getPayTax();
			}
			makeTicketPayment(conn, detail.paymentId, detail.invoiceId, item, sessionUser);
		}
		if ( detail.excessCash.compareTo(BigDecimal.ZERO) != 0  || detail.feeAmount.compareTo(BigDecimal.ZERO) != 0 ) {
			Ticket ticketPattern = new Ticket();
			ticketPattern.setInvoiceId(detail.invoiceId);
			ticketPattern.selectOne(conn);
			if ( detail.excessCash.compareTo(BigDecimal.ZERO) != 0 ) {
				makeTicket(conn, TicketType.EXCESS, ticketPattern, detail.paymentId, detail.invoiceId, detail.excessCash, sessionUser);
			}
			if ( detail.feeAmount.compareTo(BigDecimal.ZERO) != 0 ) {
				makeTicket(conn, TicketType.FEE, ticketPattern, detail.paymentId, detail.invoiceId, detail.feeAmount, sessionUser);
			}
		}

		conn.commit();
		
		BigDecimal unappliedAmount = detail.availableFromPayment;
		unappliedAmount = unappliedAmount.subtract(new BigDecimal(totalPayInvoice));
		unappliedAmount = unappliedAmount.subtract(new BigDecimal(totalPayTax));
		unappliedAmount = unappliedAmount.subtract(detail.excessCash);
		unappliedAmount = unappliedAmount.subtract(detail.feeAmount);
		response.setUnappliedAmount(unappliedAmount);

		return response;
	}


	

	private void makeTicket(Connection conn, TicketType ticketType, Ticket ticketPattern, Integer paymentId, Integer invoiceId,
			BigDecimal amount, SessionUser sessionUser) throws Exception {

		BigDecimal pmtAmount = amount.setScale(2, RoundingMode.HALF_UP);

		Calendar today = Calendar.getInstance(new Locale("America/Chicago"));
		Ticket ticket = new Ticket();
		ticket.setActDivisionId(ticketPattern.getActDivisionId());
		ticket.setActDlAmt(pmtAmount);
		ticket.setActDlPct(BigDecimal.ZERO);
		if (ticketType == TicketType.FEE) {
			ticket.setActPricePerCleaning(amount);
		} else {
			ticket.setActPricePerCleaning(BigDecimal.ZERO);
		}
		ticket.setActTaxAmt(BigDecimal.ZERO);
		ticket.setActTaxRateId(ticketPattern.getActTaxRateId());
		ticket.setAddedBy(sessionUser.getUserId());
//		ticket.setAddedDate(addedDate);		// added by super
		ticket.setBillSheet(Ticket.BILL_SHEET_IS_NO);
		ticket.setCustomerSignature(Ticket.CUSTOMER_SIGNATURE_IS_NO);
		ticket.setFleetmaticsId(null);
		ticket.setInvoiceDate(ticketPattern.getInvoiceDate());
		ticket.setInvoiceId(invoiceId);
		ticket.setJobId(ticketPattern.getJobId());
		ticket.setMgrApproval(Ticket.MGR_APPROVAL_IS_NO);
		ticket.setPrintCount(0);
		ticket.setProcessDate(ticketPattern.getProcessDate());
		ticket.setProcessNotes(null);
		ticket.setStartDate(today.getTime());
		ticket.setStatus(TicketStatus.PAID.code());
//		ticket.setTicketId(ticketId);
		ticket.setUpdatedBy(sessionUser.getUserId());
//		ticket.setUpdatedDate(updatedDate);		// set by the super
		ticket.setTicketType(ticketType.code());
		Integer ticketId = ticket.insertWithKey(conn);
		System.out.println(ticket);
		

		TicketPayment ticketPayment = new TicketPayment();
		ticketPayment.setAddedBy(sessionUser.getUserId());
//		ticketPayment.setAddedDate(addedDate);  		// populated in the super
		ticketPayment.setAmount(pmtAmount);
		ticketPayment.setPaymentId(paymentId);
		ticketPayment.setStatus(0); // do we have values for this?
		ticketPayment.setTaxAmt(BigDecimal.ZERO);
		ticketPayment.setTicketId(ticketId);
		ticketPayment.setUpdatedBy(sessionUser.getUserId());
//		ticketPayment.setUpdatedDate(updatedDate); 			// populated in the super
		
		System.out.println(ticketPayment);
		ticketPayment.insertWithNoKey(conn);

		
		
	}



	private void makeTicketPayment(Connection conn, Integer paymentId, Integer invoiceId, ApplyPaymentRequestItem item, SessionUser sessionUser) throws Exception {
		Double payInvoice = item.getPayInvoice() == null ? 0.0D : item.getPayInvoice();
		Double payTax = item.getPayTax() == null ? 0.0D : item.getPayTax();
		TicketUtils.applyPayment(conn, paymentId, item.getTicketId(), payInvoice, payTax, sessionUser.getUserId());
	}




	public enum PaymentRequestType {
		VERIFY,
		COMMIT;
	}
	
	private class ApplyPaymentDetail extends ApplicationObject {
		private static final long serialVersionUID = 1L;

		public Integer paymentId;
		public Integer invoiceId;
		public BigDecimal excessCash;
		public BigDecimal feeAmount;
		public BigDecimal availableFromPayment;
		public BigDecimal invoiceBalance;
		List<ApplyPaymentRequestItem> ticketList;
//		public InvoiceTicketResponse invoiceTicketResponse; 
		
		public ApplyPaymentDetail(Connection conn, ApplyPaymentRequest paymentRequest) throws RecordNotFoundException, Exception {
			InvoiceTicketResponse invoiceData = new InvoiceTicketResponse(conn, paymentRequest.getInvoiceId());
			PaymentResponse paymentData = new PaymentResponse(conn, paymentRequest.getPaymentId());

			paymentId = paymentRequest.getPaymentId();
			invoiceId = paymentRequest.getInvoiceId();
			excessCash = paymentRequest.getExcessCash() == null ? BigDecimal.ZERO : new BigDecimal(paymentRequest.getExcessCash());
			feeAmount = paymentRequest.getFeeAmount() == null ? BigDecimal.ZERO : new BigDecimal(paymentRequest.getFeeAmount());
			availableFromPayment = paymentData.getPaymentTotals().getAvailable();
			invoiceBalance = invoiceData.getTotalBalance();
			ticketList = paymentRequest.getTicketList();
//			invoiceTicketResponse = invoiceData;

		}
	}
}
