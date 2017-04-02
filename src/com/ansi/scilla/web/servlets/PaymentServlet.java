package com.ansi.scilla.web.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ansi.scilla.common.db.PermissionLevel;
import com.ansi.scilla.web.common.AppUtils;
import com.ansi.scilla.web.common.Permission;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;


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
		String url = request.getRequestURI();
		try {
			AppUtils.validateSession(request, Permission.PAYMENT, PermissionLevel.PERMISSION_LEVEL_IS_READ);
			int idx = url.indexOf("/payment/");
			if ( idx > -1 ) {
				String myString = url.substring(idx + "/payment/".length());
				String[] urlPieces = myString.split("/");
				System.out.println("Payment(): doGet(): myString:" + myString);
//				if ( StringUtils.isBlank(urlPieces[0])) { // there is nothing to do
//					System.out.println("Payment(): doGet(): nothing to do");
//					super.sendNotFound(response);
//				} else { // Figure out what we've got
//					// if urlPieces[0] is numeric this is a ticketId
//					if ( StringUtils.isNumeric(urlPieces[0])) { // this is a ticket id
//						Integer ticketId = Integer.valueOf(urlPieces[0]);
//						System.out.println("Payment(): doGet(): ticketId:" + ticketId);
//						Connection conn = null;
//						try {
//							conn = AppUtils.getDBCPConn();

//							PaymentListResponse paymentReturnListResponse = new PaymentListResponse(conn, ticketId);
//							super.sendResponse(conn, response, ResponseCode.SUCCESS, paymentReturnListResponse);
//						} catch(RecordNotFoundException recordNotFoundEx) {
//							super.sendNotFound(response);
//						} catch ( Exception e) {
//							AppUtils.logException(e);
//							throw new ServletException(e);
//						} finally {
//							AppUtils.closeQuiet(conn);
//						}
//
//					} else { // not a valid ticketId so we cannot find it
//						System.out.println("Payment(): doGet(): not a valid ticket");
//						super.sendNotFound(response);
//					}
//				}
			} else {
				super.sendNotFound(response);
			}
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e1) {
			super.sendForbidden(response);
		}

	}


/*	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		SessionUser sessionUser = AppUtils.getSessionUser(request);
		String url = request.getRequestURI();
//		String queryString = request.getQueryString();
		System.out.println("TaxRateServlet: doPost() Url:" + url);
		
		Connection conn = null;
		try {
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);

			// figure out if this is an "add" or an "update"
			int idx = url.indexOf("/taxRate/");
			String myString = url.substring(idx + "/taxRate/".length());				
			String[] urlPieces = myString.split("/");
			String command = urlPieces[0];
			System.out.println("TaxRateServlet: doPost() command:"+command);

			String jsonString = super.makeJsonString(request);
			System.out.println("TaxRateServlet: doPost() jsonString:"+jsonString);
			TaxRateRequest taxRateRequest = new TaxRateRequest(jsonString);
			
			TaxRate taxRate = null;
			ResponseCode responseCode = null;
			if ( command.equals(ACTION_IS_ADD) ) {
				System.out.println("TaxRateServlet: doPost() action is add");
				WebMessages webMessages = validateAdd(conn, taxRateRequest);
				if (webMessages.isEmpty()) {
					try {
						taxRate = doAdd(conn, taxRateRequest, sessionUser);
						String message = AppUtils.getMessageText(conn, MessageKey.SUCCESS, "Success!");
						responseCode = ResponseCode.SUCCESS;
						webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, message);
					} catch ( DuplicateEntryException e ) {
						String messageText = AppUtils.getMessageText(conn, MessageKey.DUPLICATE_ENTRY, "Record already Exists");
						webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, messageText);
						responseCode = ResponseCode.EDIT_FAILURE;
					} catch ( Exception e ) {
						responseCode = ResponseCode.SYSTEM_FAILURE;
						AppUtils.logException(e);
						String messageText = AppUtils.getMessageText(conn, MessageKey.INSERT_FAILED, "Insert Failed");
						webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, messageText);
					}
				} else {
					System.out.println("TaxRateServlet: doPost() add failed");
					responseCode = ResponseCode.EDIT_FAILURE;
				}
				System.out.println("TaxRateServlet: doPost() prepare response");
				TaxRateResponse taxRateResponse = new TaxRateResponse(taxRate, webMessages);
				System.out.println("TaxRateServlet: doPost() send response");
				super.sendResponse(conn, response, responseCode, taxRateResponse);
				System.out.println("TaxRateServlet: doPost() response sent");
				
			} else if ( urlPieces.length == 1 ) {   //  /<taxRateId> = 1 pieces
				System.out.println("TaxRateServlet: doPost() action is update");
				WebMessages webMessages = validateAdd(conn, taxRateRequest);
				if (webMessages.isEmpty()) {
					System.out.println("passed validation");
					try {
						TaxRate key = new TaxRate();
						if ( StringUtils.isNumeric(urlPieces[0]) ) {//looks like a taxRateId
							System.out.println("TaxRateServlet: doPost() trying to update:"+urlPieces[0]);
							key.setTaxRateId(Integer.valueOf(urlPieces[0]));
							taxRate = doUpdate(conn, key, taxRateRequest, sessionUser);
							String message = AppUtils.getMessageText(conn, MessageKey.SUCCESS, "Success!");
							responseCode = ResponseCode.SUCCESS;
							webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, message);
						} else { //non-integer taxRateId, probably a bad thing
							throw new RecordNotFoundException();
						}
					} catch ( RecordNotFoundException e ) {
						System.out.println("Doing 404");
						super.sendNotFound(response);						
					} catch ( Exception e) {
						System.out.println("Doing SysFailure");
						responseCode = ResponseCode.SYSTEM_FAILURE;
						AppUtils.logException(e);
						String messageText = AppUtils.getMessageText(conn, MessageKey.INSERT_FAILED, "Insert Failed");
						webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, messageText);
					}
				} else {
					System.out.println("Doing Edit Fail");
					responseCode = ResponseCode.EDIT_FAILURE;
				}
				System.out.println("TaxRateServlet: doPost() prepare response");
				TaxRateResponse taxRateResponse = new TaxRateResponse(taxRate, webMessages);
				System.out.println("TaxRateServlet: doPost() send response");
				super.sendResponse(conn, response, responseCode, taxRateResponse);
				System.out.println("TaxRateServlet: doPost() response sent");
			} else {
				super.sendNotFound(response);
			}
			
			conn.commit();
		} catch ( Exception e ) {
			AppUtils.logException(e);
			AppUtils.rollbackQuiet(conn);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}
		
	}


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
