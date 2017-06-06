package com.ansi.scilla.web.servlets.payment;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.common.db.Payment;
import com.ansi.scilla.common.db.PermissionLevel;
import com.ansi.scilla.common.db.User;
import com.ansi.scilla.common.payment.PaymentType;
import com.ansi.scilla.web.common.AnsiURL;
import com.ansi.scilla.web.common.AppUtils;
import com.ansi.scilla.web.common.Permission;
import com.ansi.scilla.web.common.ResponseCode;
import com.ansi.scilla.web.common.WebMessages;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.ResourceNotFoundException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.request.payment.PaymentRequest;
import com.ansi.scilla.web.response.payment.DuplicatePaymentResponse;
import com.ansi.scilla.web.response.payment.PaymentResponse;
import com.ansi.scilla.web.servlets.AbstractServlet;
import com.ansi.scilla.web.struts.SessionData;
import com.ansi.scilla.web.struts.SessionUser;
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
 * 		/payment?paymentId=<paymentId>  (returns payment totals for paymentId)
 * 		/payment?invoiceId=<invoiceId>
 * 		/payment?invoiceId=<invoiceId>&amount=<availablePayment>
 * 
 * The url for update will be a POST to:
 * 		/ticketReturn/<ticket>/<action> with parameters in the JSON
 * 			Action 		Parameters
 * 			complete	<processDate><processNotes><actualPricePerCleaning>
 * 						<actualDirectLaborPct><actualDirectLabor>
 * 						<customerSignature><billSheet><managerApproval>
 * 			skip		<processDate><processNotes>
 * 			void		<processDate><processNotes>
 * 			reject		<processDate><processNotes>
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
			AppUtils.validateSession(request, Permission.PAYMENT, PermissionLevel.PERMISSION_LEVEL_IS_READ);
			url = new AnsiURL(request, "payment", (String[])null);
			if ( url.getId() != null ) {
				PaymentResponse data = new PaymentResponse(conn, url.getId());
				System.out.println(data);
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
				System.out.println(jsonString);
				PaymentRequest paymentRequest = new PaymentRequest();
				AppUtils.json2object(jsonString, paymentRequest);
				url = new AnsiURL(request, "payment", new String[] {PaymentRequestType.ADD.name().toLowerCase()});
				SessionData sessionData = AppUtils.validateSession(request, Permission.PAYMENT, PermissionLevel.PERMISSION_LEVEL_IS_WRITE);
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
		System.out.println("TaxRateServlet: validateUpdate() before");
		List<String> missingFields = super.validateRequiredUpdateFields(taxRateRequest);
		System.out.println("TaxRateServlet: validateUpdate() after");
		if ( ! missingFields.isEmpty() ) {
			System.out.println("TaxRateServlet: validateUpdate() missing fields");
			String messageText = AppUtils.getMessageText(conn, MessageKey.MISSING_DATA, "Required Entry");
			for ( String field : missingFields ) {
				webMessages.addMessage(field, messageText);
			}
		}
		// if we "select" the key, and it isn't found, a "RecordNotFoundException" is thrown.
		// That exception will propagate up the tree until it turns into a 404 message sent to the client
		TaxRate testKey = (TaxRate)key.clone(); 
		System.out.println("TaxRateServlet: validateUpdate() testKey:" + testKey.getTaxRateId());
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
		
		System.out.println(payment);

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
		Calendar today = Calendar.getInstance(new Locale("America/Chicago"));
	
		// if we have a check date or a check number, we need the other one also
		if ( paymentRequest.getCheckDate() != null ) {
			if (paymentRequest.getCheckNumber() == null ) {
				errors.put(PaymentRequest.CHECK_NUMBER, "Required value");
			}
	
			// no post-dated checks
			if ( paymentRequest.getPaymentDate().before(paymentRequest.getCheckDate())) {
				errors.put(PaymentRequest.CHECK_DATE, "Payment Date before check date");
			} else if ( ! paymentRequest.getPaymentDate().before(today.getTime())) {
				errors.put(PaymentRequest.CHECK_DATE, "Check Date must be on or before today");
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
		DuplicatePaymentResponse data = new DuplicatePaymentResponse(duplicatePayment, user);
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
