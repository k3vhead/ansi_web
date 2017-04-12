package com.ansi.scilla.web.servlets.payment;

import java.io.IOException;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ansi.scilla.common.db.PermissionLevel;
import com.ansi.scilla.web.common.AnsiURL;
import com.ansi.scilla.web.common.AppUtils;
import com.ansi.scilla.web.common.Permission;
import com.ansi.scilla.web.common.ResponseCode;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.ResourceNotFoundException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.request.PaymentRequest;
import com.ansi.scilla.web.response.payment.PaymentResponse;
import com.ansi.scilla.web.servlets.AbstractServlet;
import com.ansi.scilla.web.struts.SessionData;
import com.ansi.scilla.web.struts.SessionUser;
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
			System.out.println("Getting: " + url.getId());
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

	/*
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Connection conn = null;
		try {
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);
			String jsonString = super.makeJsonString(request);
			PaymentRequest paymentRequest = (PaymentRequest)AppUtils.json2object(jsonString, PaymentRequest.class);
			SessionData sessionData = AppUtils.validateSession(request, Permission.PAYMENT, PermissionLevel.PERMISSION_LEVEL_IS_WRITE);

			SessionUser sessionUser = sessionData.getUser(); 
			List<String> addErrors = super.validateRequiredAddFields(paymentRequest);
			HashMap<String, String> errors = new HashMap<String, String>();
			if ( addErrors.isEmpty() ) {
				errors = validateDates(paymentRequest);
			}
			if (addErrors.isEmpty() && errors.isEmpty()) {
				processUpdate(conn, request, response, paymentRequest, sessionUser);
//				fakeThePDF(conn, request, response, invoicePrintRequest);
			} else {
				processError(conn, response, addErrors, errors);
			}
			conn.rollback();
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e1) {
			super.sendForbidden(response);
		} catch ( Exception e) {
			AppUtils.logException(e);
			AppUtils.rollbackQuiet(conn);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}
	}
	*/
	
	

/*

	protected TaxRate doUpdate(Connection conn, TaxRate key, TaxRateRequest taxRateRequest, SessionUser sessionUser) throws Exception {
		System.out.println("This is the key:");
		System.out.println(key);
		System.out.println("************");
		Date today = new Date();
		TaxRate taxRate = new TaxRate();
		taxRate.setAmount(taxRateRequest.getAmount());
		taxRate.setEffectiveDate(taxRateRequest.getEffectiveDate());
		if ( ! StringUtils.isBlank(taxRateRequest.getLocation())) {
			taxRate.setLocation(taxRateRequest.getLocation());
		}
		taxRate.setRate(taxRateRequest.getRate());
		//taxRate.setTaxRateId(taxRateRequest.getTaxRateId()); //key is passed in from the url
		taxRate.setUpdatedBy(sessionUser.getUserId());
		taxRate.setUpdatedDate(today);
		// if we update something that isn't there, a RecordNotFoundException gets thrown
		// that exception get propagated and turned into a 404
		taxRate.update(conn, key);		
		return taxRate;
	}

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
	
}
