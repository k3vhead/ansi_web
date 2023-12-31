package com.ansi.scilla.web.payment.servlet;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;

import com.ansi.scilla.common.AnsiTime;
import com.ansi.scilla.common.db.Payment;
import com.ansi.scilla.common.db.User;
import com.ansi.scilla.common.payment.PaymentType;
import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.common.utils.AnsiURL;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.common.utils.Permission;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.ResourceNotFoundException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.payment.request.PaymentRequest;
import com.ansi.scilla.web.payment.response.DuplicatePaymentResponse;
import com.ansi.scilla.web.payment.response.PaymentResponse;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.thewebthing.commons.db2.RecordNotFoundException;


/**
 * Use cases:
 * 1) user types in payment information and selects an invoiceId to pay against
 * 		parameters:	payment values + invoice id
 * 
 * 2) user selects a payment from the payment lookup screen
 * 		parameters: payment id
 * 
 *
 * 
 * 
 * 
 * 
 * 
 * 
 * The url for get will be one of:
 * 		/payment?paymentId=&lt;paymentId&gt;  (returns payment totals for paymentId)
 * 		/payment?invoiceId=&lt;invoiceId&gt;
 * 		/payment?invoiceId=&lt;invoiceId&gt;&amp;amount=&lt;availablePayment&gt;
 * 
 * The url for update will be a POST to:
 * 		/ticketReturn/&lt;ticket&gt;/&lt;action&gt; with parameters in the JSON
 * 			Action 		Parameters
 * 			complete	&lt;processDate&gt;&lt;processNotes&gt;&lt;actualPricePerCleaning&gt;
 * 						&lt;actualDirectLaborPct&gt;&lt;actualDirectLabor&gt;
 * 						&lt;customerSignature&gt;&lt;billSheet&gt;&lt;managerApproval&gt;
 * 			skip		&lt;processDate&gt;&lt;processNotes&gt;
 * 			void		&lt;processDate&gt;&lt;processNotes&gt;
 * 			reject		&lt;processDate&gt;&lt;processNotes&gt;
 * 			re-queue	-none-
 * 
 * 
 * @author ggroce
 */
public class PaymentServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;


	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		Connection conn = null;
		AnsiURL url = null;
		try {
			conn = AppUtils.getDBCPConn();
			AppUtils.validateSession(request, Permission.PAYMENT_READ);
			url = new AnsiURL(request, "payment", (String[])null);
			if ( url.getId() != null ) {
				PaymentResponse data = new PaymentResponse(conn, url.getId());
				logger.log(Level.DEBUG, data);
				super.sendResponse(conn, response, ResponseCode.SUCCESS, data);				
			} else {
				throw new ResourceNotFoundException(); 
			}
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e1) {
			super.sendForbidden(response);
		} catch (ResourceNotFoundException | RecordNotFoundException e) {
			super.sendNotFound(response);
		} catch (Exception e) {
			AppUtils.logException(e);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}

	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Connection conn = null;
		AnsiURL url = null;
		try {
			try{
				conn = AppUtils.getDBCPConn();
				conn.setAutoCommit(false);
				String jsonString = super.makeJsonString(request);
				logger.log(Level.DEBUG, jsonString);
				PaymentRequest paymentRequest = new PaymentRequest();
				AppUtils.json2object(jsonString, paymentRequest);
				logger.log(Level.DEBUG, paymentRequest);
				url = new AnsiURL(request, "payment", new String[] {PaymentRequestType.ADD.name().toLowerCase()});
				SessionData sessionData = AppUtils.validateSession(request, Permission.PAYMENT_WRITE);
				SessionUser sessionUser = sessionData.getUser();
				
				if ( ! StringUtils.isBlank(url.getCommand())) {
					processAdd(conn, paymentRequest, sessionUser, response);
				} else if ( url.getId() != null ) {
					processUpdate(conn, url.getId(), paymentRequest, sessionUser, response);
				} else {
					throw new ResourceNotFoundException();
				}
			} catch ( InvalidFormatException e ) {
				String badField = super.findBadField(e.toString());
				PaymentResponse data = new PaymentResponse();
				WebMessages messages = new WebMessages();
				messages.addMessage(badField, "Invalid Format");
				data.setWebMessages(messages);
				super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, data);
			} catch (TimeoutException | NotAllowedException | ExpiredLoginException e1) {
				super.sendForbidden(response);
			} catch ( ResourceNotFoundException e) {
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



	/*

	protected WebMessages validateUpdate(Connection conn, TaxRate key, TaxRateRequest taxRateRequest) throws RecordNotFoundException, Exception {
		WebMessages webMessages = new WebMessages();
		logger.log(Level.DEBUG, "TaxRateServlet: validateUpdate() before");
		List<String> missingFields = super.validateRequiredUpdateFields(taxRateRequest);
		logger.log(Level.DEBUG, "TaxRateServlet: validateUpdate() after");
		if ( ! missingFields.isEmpty() ) {
			logger.log(Level.DEBUG, "TaxRateServlet: validateUpdate() missing fields");
			String messageText = AppUtils.getMessageText(conn, MessageKey.MISSING_DATA, "Required Entry");
			for ( String field : missingFields ) {
				webMessages.addMessage(field, messageText);
			}
		}
		// if we "select" the key, and it isn't found, a "RecordNotFoundException" is thrown.
		// That exception will propagate up the tree until it turns into a 404 message sent to the client
		TaxRate testKey = (TaxRate)key.clone(); 
		logger.log(Level.DEBUG, "TaxRateServlet: validateUpdate() testKey:" + testKey.getTaxRateId());
		testKey.selectOne(conn);
		return webMessages;
	}
	 */

	private void processAdd(Connection conn, PaymentRequest paymentRequest, SessionUser sessionUser, HttpServletResponse response) throws Exception {
		List<String> addErrors = super.validateRequiredAddFields(paymentRequest);
		Payment duplicatePayment = null;
		HashMap<String, String> errors = new HashMap<String, String>();
		if ( addErrors.isEmpty() ) {
			errors = validateValues(paymentRequest);
		}
		if (addErrors.isEmpty() && errors.isEmpty()) {
			duplicatePayment = checkForDupe(conn, paymentRequest);
		}
		 
		if (addErrors.isEmpty() && errors.isEmpty()) {
			boolean doAdd = false;
			if ( duplicatePayment == null ) {
				// no duplicate payment was found
				doAdd = true;
			} else {
				if ( paymentRequest.getConfirmDuplicate() ) {
					// duplicate was found, enter it anyway
					doAdd = true;
				}
			}
			if ( doAdd ) {
				doAdd(conn, paymentRequest, response, sessionUser);
				conn.commit();
			} else {
				doWarningResponse(conn, response, duplicatePayment);
			}
		} else {
			doErrorResponse(conn, response, addErrors, errors);
		}
	}

	private Payment checkForDupe(Connection conn, PaymentRequest paymentRequest) throws Exception {
		Payment payment = new Payment();
		payment.setPaymentMethod(paymentRequest.getPaymentMethod());
		payment.setCheckDate(paymentRequest.getCheckDate());
		payment.setAmount(paymentRequest.getPaymentAmount());
		try {
			payment.selectOne(conn);
		} catch ( RecordNotFoundException e ){
			payment = null;
		}
		return payment;
	}

	private void doAdd(Connection conn, PaymentRequest paymentRequest, HttpServletResponse response, SessionUser sessionUser) throws Exception {
		Payment payment = new Payment();
		payment.setAddedBy(sessionUser.getUserId());
		payment.setAmount(paymentRequest.getPaymentAmount());
		payment.setCheckDate(paymentRequest.getCheckDate());
		payment.setCheckNumber(paymentRequest.getCheckNumber());
		payment.setPaymentDate(paymentRequest.getPaymentDate());
		payment.setPaymentNote(paymentRequest.getPaymentNote());
		payment.setStatus(0);
		if ( paymentRequest.getPaymentAmount().compareTo(BigDecimal.ZERO) > 0 ) {
			payment.setType(PaymentType.PAYMENT.name());
		} else if ( paymentRequest.getPaymentAmount().compareTo(BigDecimal.ZERO) < 0 ) {
			payment.setType(PaymentType.REFUND.name());
		} else { 
			// Payment amount is 0
			payment.setType(PaymentType.MOVE.name());
		}
		payment.setPaymentMethod(paymentRequest.getPaymentMethod());
		payment.setUpdatedBy(sessionUser.getUserId());
		
		logger.log(Level.DEBUG, payment);

		Integer paymentId = payment.insertWithKey(conn);
		payment.setPaymentId(paymentId);
		conn.commit();
		
		WebMessages webMessages = new WebMessages();
		webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "Success! Payment Added");
		PaymentResponse data = new PaymentResponse(conn, paymentId);
		data.setWebMessages(webMessages);
		super.sendResponse(conn, response, ResponseCode.SUCCESS, data);
	}

	private void processUpdate(Connection conn, Integer id, PaymentRequest paymentRequest, SessionUser sessionUser, HttpServletResponse response) {
		throw new RuntimeException("Not Coded yet");
	}

	private HashMap<String, String> validateValues(PaymentRequest paymentRequest) {
		HashMap<String, String> errors = new HashMap<String, String>();
		Calendar today = Calendar.getInstance(new AnsiTime());
	
		// if we have a check date or a check number, we need the other one also
		if ( paymentRequest.getCheckDate() != null ) {
			if (paymentRequest.getCheckNumber() == null ) {
				errors.put(PaymentRequest.CHECK_NUMBER, "Required value");
			}
	
			// no post-dated checks
			if ( paymentRequest.getPaymentDate().before(paymentRequest.getCheckDate())) {
				errors.put(PaymentRequest.CHECK_DATE, "Payment Date before check date");
//	2017/10/25  Post-dating payments is OK per Danielle
//			} else if ( ! paymentRequest.getCheckDate().before(today.getTime())) {
//				errors.put(PaymentRequest.CHECK_DATE, "Check Date must be on or before today");
			}
		} else {
			if (paymentRequest.getCheckNumber() != null ) {
				errors.put(PaymentRequest.CHECK_DATE, "Required value");
			}
		}
	
		return errors;
	}

	private void doWarningResponse(Connection conn, HttpServletResponse response, Payment duplicatePayment) throws Exception {
		User user = new User();
		user.setUserId(duplicatePayment.getAddedBy());
		try {
			user.selectOne(conn);
		} catch ( RecordNotFoundException e) {
			// this is weird, but not a killer situation
			user.setLastName("Unknown");
		}
		PreparedStatement ps = conn.prepareStatement("select distinct ticket.invoice_id " +
												" from ticket_payment " +
												" inner join ticket on ticket.ticket_id=ticket_payment.ticket_id " +
												" where ticket_payment.payment_id=?");
		ps.setInt(1, duplicatePayment.getPaymentId());
		ResultSet rs = ps.executeQuery();
		List<Integer> invoiceIdList = new ArrayList<Integer>();
		while ( rs.next() ) {
			invoiceIdList.add(rs.getInt(1));
		}
		rs.close();
		DuplicatePaymentResponse data = new DuplicatePaymentResponse(duplicatePayment, user, invoiceIdList);
		super.sendResponse(conn, response, ResponseCode.EDIT_WARNING, data);
	}

	
	private void doErrorResponse(Connection conn, HttpServletResponse response, List<String> addErrors, HashMap<String, String> errors) throws Exception {
		WebMessages webMessages = new WebMessages();
		for ( String error : addErrors ) {
			webMessages.addMessage(error, "Required field");
		}
		for ( String key : errors.keySet() ) {
			webMessages.addMessage(key, errors.get(key));
		}
		PaymentResponse data = new PaymentResponse();
		data.setWebMessages(webMessages);
		super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, data);

	}

	public enum PaymentRequestType {
		ADD,
		UPDATE,
		DELETE;
	}
	
	
}
