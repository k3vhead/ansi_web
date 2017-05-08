package com.ansi.scilla.web.servlets.quote;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ansi.scilla.common.db.PermissionLevel;
import com.ansi.scilla.common.quote.QuotePrinter;
import com.ansi.scilla.web.common.AnsiURL;
import com.ansi.scilla.web.common.AppUtils;
import com.ansi.scilla.web.common.Permission;
import com.ansi.scilla.web.common.ResponseCode;
import com.ansi.scilla.web.common.WebMessages;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.request.quote.QuotePrintRequest;
import com.ansi.scilla.web.response.quote.QuotePrintResponse;
import com.ansi.scilla.web.servlets.AbstractServlet;
import com.ansi.scilla.web.struts.SessionData;
import com.ansi.scilla.web.struts.SessionUser;



public class QuotePrintServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;
	
	
	
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {		
		super.sendNotAllowed(response);
	}


	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		AnsiURL ansiURL = null; 
		Connection conn = null;
		try {
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);
			String jsonString = super.makeJsonString(request);
			QuotePrintRequest quotePrintRequest = new QuotePrintRequest();
			AppUtils.json2object(jsonString, QuotePrintRequest.class, quotePrintRequest);
			ansiURL = new AnsiURL(request, "printQuote", (String[])null); //  .../ticket/etc
			SessionData sessionData = AppUtils.validateSession(request, Permission.QUOTE, PermissionLevel.PERMISSION_LEVEL_IS_WRITE);

			SessionUser sessionUser = sessionData.getUser(); 
			List<String> addErrors = super.validateRequiredAddFields(quotePrintRequest);
			if (addErrors.isEmpty()) {
				processUpdate(conn, request, response, ansiURL.getId(), quotePrintRequest, sessionUser);
			} else {
				processError(conn, response, addErrors);
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

	
	
	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		super.sendNotAllowed(response);
	}


	private void processUpdate(Connection conn, HttpServletRequest request, HttpServletResponse response, Integer quoteId, QuotePrintRequest quotePrintRequest, SessionUser sessionUser) throws Exception {
		Calendar today = Calendar.getInstance(new Locale("America/Chicago"));
		
		SimpleDateFormat fileDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		QuotePrintResponse data = new QuotePrintResponse();
		WebMessages webMessages = new WebMessages();
		String fileDate = fileDateFormat.format(today.getTime());

		Date printDate = quotePrintRequest.getQuoteDate();				
		String invoiceFileName = "quote" + quoteId + "_" + fileDate + ".pdf";

		ByteArrayOutputStream baos = QuotePrinter.printQuote(conn, quoteId, quotePrintRequest.getQuoteDate(), sessionUser.getUserId());
		
		
//		FileOutputStream os = new FileOutputStream(new File(invoicePathName + "/" + invoiceFileName));
//		baos.writeTo(os);
//		os.flush();
//		os.close();
		
		
		data.setWebMessages(webMessages);
		super.sendResponse(conn, response, ResponseCode.SUCCESS, data);

		
	}


	private void processError(Connection conn, HttpServletResponse response, List<String> addErrors) throws Exception {
		WebMessages webMessages = new WebMessages();
		for ( String error : addErrors ) {
			webMessages.addMessage(error, "Required field");
		}
		QuotePrintResponse data = new QuotePrintResponse();
		data.setWebMessages(webMessages);
		super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, data);
	}





	
}
