package com.ansi.scilla.web.servlets.payment;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.common.db.PermissionLevel;
import com.ansi.scilla.web.common.AnsiURL;
import com.ansi.scilla.web.common.AppUtils;
import com.ansi.scilla.web.common.Permission;
import com.ansi.scilla.web.common.ResponseCode;
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
import com.thewebthing.commons.db2.RecordNotFoundException;



public class ApplyPaymentServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;



	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Connection conn = null;
		AnsiURL url = null;
		try {
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);
			String jsonString = super.makeJsonString(request);
			System.out.println(jsonString);			
			ApplyPaymentRequest paymentRequest = (ApplyPaymentRequest)AppUtils.json2object(jsonString, ApplyPaymentRequest.class);
			url = new AnsiURL(request, "applyPayment", new String[] {PaymentRequestType.VERIFY.name().toLowerCase(), PaymentRequestType.COMMIT.name().toLowerCase()});
			SessionData sessionData = AppUtils.validateSession(request, Permission.PAYMENT, PermissionLevel.PERMISSION_LEVEL_IS_WRITE);
			SessionUser sessionUser = sessionData.getUser();

			PaymentRequestType requestType = PaymentRequestType.valueOf(url.getCommand().toUpperCase());
			if ( ! StringUtils.isBlank(url.getCommand())) {
				ApplyPaymentResponse data = new ApplyPaymentResponse();
				if ( requestType.equals(PaymentRequestType.VERIFY)) {
					data = doVerify(conn, paymentRequest);
				} else if ( requestType.equals(PaymentRequestType.COMMIT)) {
					doCommit();
				} else {
					throw new ResourceNotFoundException();
				}
				data.setApplyPaymentRequest(paymentRequest);				
				super.sendResponse(conn, response, ResponseCode.SUCCESS, data); 
			} else {
				throw new ResourceNotFoundException();
			}
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e1) {
			super.sendForbidden(response);
		} catch ( ResourceNotFoundException e) {
			System.out.println("*** ApplyPayment 404:  " + e + "****");
			super.sendNotFound(response);
		} catch ( Exception e) {
			AppUtils.logException(e);
			AppUtils.rollbackQuiet(conn);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}
	}



	private ApplyPaymentResponse doVerify(Connection conn, ApplyPaymentRequest paymentRequest) throws RecordNotFoundException, Exception {
		System.out.println("Doing Verify");
		System.out.println(paymentRequest);
		InvoiceTicketResponse invoiceData = new InvoiceTicketResponse(conn, paymentRequest.getInvoiceId());
		PaymentResponse paymentData = new PaymentResponse(conn, paymentRequest.getPaymentId());

		Double excessCash = paymentRequest.getExcessCash() == null ? 0.0D : paymentRequest.getExcessCash();
		Double feeAmount = paymentRequest.getFeeAmount() == null ? 0.0D : paymentRequest.getFeeAmount();
		ApplyPaymentResponse response = new ApplyPaymentResponse();
		response.setExcessCash(excessCash);
		response.setFeeAmount(feeAmount);
		response.setAvailableFromPayment(paymentData.getPaymentTotals().getAvailable().doubleValue());
		response.setInvoiceBalance(invoiceData.getTotalBalance().doubleValue());
		
		Double totalPayInvoice = 0.0D;
		Double totalPayTax = 0.0D;
		for ( ApplyPaymentRequestItem item : paymentRequest.getTicketList()) {
			Double payInvoice = item.getPayInvoice() == null ? 0.0D : item.getPayInvoice();
			Double payTax = item.getPayTax() == null ? 0.0D : item.getPayTax();
			totalPayInvoice = totalPayInvoice + payInvoice;
			totalPayTax = totalPayTax + payTax;
		}
		response.setTotalPayInvoice(totalPayInvoice);
		response.setTotalPayTax(totalPayTax);
		
		Double unappliedAmount = paymentData.getPaymentTotals().getAvailable().doubleValue() - totalPayInvoice - totalPayTax - paymentRequest.getExcessCash() - paymentRequest.getFeeAmount();
		response.setUnappliedAmount(unappliedAmount);
		
		return response;
	}



	private void doCommit() {
		throw new RuntimeException("Not yet coded");
	}



	public enum PaymentRequestType {
		VERIFY,
		COMMIT;
	}
}
