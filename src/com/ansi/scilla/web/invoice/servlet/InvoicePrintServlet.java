package com.ansi.scilla.web.invoice.servlet;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Level;

import com.ansi.scilla.common.AnsiTime;
import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.common.invoice.InvoiceUtils;
import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.common.utils.Permission;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.invoice.request.InvoicePrintRequest;
import com.ansi.scilla.web.invoice.response.InvoicePrintLookupResponse;
import com.ansi.scilla.web.invoice.response.InvoicePrintLookupResponseItem;
import com.ansi.scilla.web.invoice.response.InvoicePrintResponse;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;



public class InvoicePrintServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;
	
	
	
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {		
		super.sendNotAllowed(response);
	}


	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
//		AnsiURL ansiURL = null; 
		Connection conn = null;
		try {
			try{
				conn = AppUtils.getDBCPConn();
				conn.setAutoCommit(false);
				String jsonString = super.makeJsonString(request);
				InvoicePrintRequest invoicePrintRequest = new InvoicePrintRequest();
				AppUtils.json2object(jsonString, invoicePrintRequest);
//				ansiURL = new AnsiURL(request, "invoiceGeneration", (String[])null); //  .../ticket/etc
				SessionData sessionData = AppUtils.validateSession(request, Permission.INVOICE_WRITE);
				
				SessionUser sessionUser = sessionData.getUser(); 
				List<String> addErrors = super.validateRequiredAddFields(invoicePrintRequest);
				HashMap<String, String> errors = new HashMap<String, String>();
				if ( addErrors.isEmpty() ) {
					errors = validateDates(invoicePrintRequest);
				}
				if (addErrors.isEmpty() && errors.isEmpty()) {
					processUpdate(conn, request, response, invoicePrintRequest, sessionUser);
					conn.commit();
				} else {
					processError(conn, response, addErrors, errors);
					conn.rollback();
				}
			} catch ( InvalidFormatException e ) {
				String badField = super.findBadField(e.toString());
				InvoicePrintResponse data = new InvoicePrintResponse();
				WebMessages messages = new WebMessages();
				messages.addMessage(badField, "Invalid Format");
				data.setWebMessages(messages);
				super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, data);
			} catch (TimeoutException | NotAllowedException | ExpiredLoginException e1) {
				super.sendForbidden(response);
			}
		} catch ( Exception e) {
			AppUtils.logException(e);
			AppUtils.rollbackQuiet(conn);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}
	}

	
	
	private HashMap<String, String> validateDates(InvoicePrintRequest invoicePrintRequest) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss.S");
		logger.log(Level.DEBUG, "Print: " + sdf.format(invoicePrintRequest.getPrintDate()));
		logger.log(Level.DEBUG, "Due: " + sdf.format(invoicePrintRequest.getDueDate()));
		HashMap<String, String> dateErrors = new HashMap<String, String>();
		if ( ! invoicePrintRequest.getDueDate().after(invoicePrintRequest.getPrintDate())) {
			dateErrors.put("dueDate", "Due Date Must be after Print Date");
		}
		return dateErrors;
	}


	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		super.sendNotAllowed(response);
	}


	private void processUpdate(Connection conn, HttpServletRequest request, HttpServletResponse response, InvoicePrintRequest invoicePrintRequest, SessionUser sessionUser) throws Exception {
//		Calendar invoiceDate = Calendar.getInstance(new Locale("America/Chicago"));
//		invoiceDate.setTime(invoiceGenerationRequest.getInvoiceDate());
//		Boolean monthlyFlag = invoiceGenerationRequest.getMonthlyFlag();
//		Integer userId = sessionUser.getUserId();
//		InvoiceUtils.generateInvoices(conn, invoiceDate, monthlyFlag, userId);
		
		SimpleDateFormat fileDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		InvoicePrintResponse data = new InvoicePrintResponse();
		WebMessages webMessages = new WebMessages();
		String invoicePathName = request.getPathTranslated() + "invoicePDF";
		File invoicePath = new File(invoicePathName);
		if ( ! invoicePath.exists()) {
			invoicePath.mkdirs();
		}

		Date printDate = invoicePrintRequest.getPrintDate();				
		Date dueDate = invoicePrintRequest.getDueDate();		
		String fileDate = fileDateFormat.format(printDate);

		List<Integer> divisionList = makeDivisionList(conn, invoicePrintRequest.getDivisionId());
		String invoiceFileName = null;
		if ( divisionList.size() == 1 ) {
			Division division = new Division();
			division.setDivisionId(divisionList.get(0));
			division.selectOne(conn);
			invoiceFileName = division.getDivisionNbr() + "-" + division.getDivisionCode();
		} else {
			invoiceFileName = "allDivisions";
		}
		invoiceFileName = invoiceFileName + "_" + fileDate + ".zip";


		Calendar printCalendar = Calendar.getInstance(new AnsiTime());
		printCalendar.setTime(printDate);
		Calendar dueCalendar = Calendar.getInstance(new AnsiTime());
		dueCalendar.setTime(dueDate);
		ByteArrayOutputStream baos = InvoiceUtils.printInvoices(conn, divisionList, printCalendar, dueCalendar, sessionUser.getUserId());
		
		
		FileOutputStream os = new FileOutputStream(new File(invoicePathName + "/" + invoiceFileName));
		baos.writeTo(os);
		os.flush();
		os.close();
		
		
		data.setInvoiceFile("invoicePDF/" + invoiceFileName);
		data.setWebMessages(webMessages);
		super.sendResponse(conn, response, ResponseCode.SUCCESS, data);

		
	}


	private List<Integer> makeDivisionList(Connection conn, String divisionId) throws Exception {
		List<Integer> divisionList = new ArrayList<Integer>();
		if ( divisionId.equalsIgnoreCase("all")) {
			InvoicePrintLookupResponse response = new InvoicePrintLookupResponse(conn);
			for ( InvoicePrintLookupResponseItem item : response.getInvoiceList()) {
				if ( item.getInvoiceCount() > 0 ) {
					divisionList.add(item.getDivisionId());
				}
			}			
		} else {
			divisionList.add(Integer.valueOf(divisionId));
		}
		return divisionList;
	}


	private void processError(Connection conn, HttpServletResponse response, List<String> addErrors, HashMap<String,String> errors) throws Exception {
		WebMessages webMessages = new WebMessages();
		for ( String error : addErrors ) {
			webMessages.addMessage(error, "Required field");
		}
		for ( String key : errors.keySet() ) {
			webMessages.addMessage(key, errors.get(key));
		}
		InvoicePrintResponse data = new InvoicePrintResponse();
		data.setWebMessages(webMessages);
		super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, data);
	}
	
}
