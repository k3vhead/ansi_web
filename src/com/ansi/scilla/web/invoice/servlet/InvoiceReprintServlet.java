package com.ansi.scilla.web.invoice.servlet;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ansi.scilla.common.AnsiTime;
import com.ansi.scilla.common.db.Invoice;
import com.ansi.scilla.common.invoice.InvoiceUtils;
import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.common.utils.AnsiURL;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.Permission;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.invoice.response.InvoicePrintResponse;
import com.thewebthing.commons.db2.RecordNotFoundException;



public class InvoiceReprintServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;
	
	
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		AnsiURL ansiURL = null; 
		Connection conn = null;
		try {
			try{
				conn = AppUtils.getDBCPConn();
				conn.setAutoCommit(false);
				ansiURL = new AnsiURL(request, "invoiceReprint", (String[])null); //  .../ticket/etc
				SessionData sessionData = AppUtils.validateSession(request, Permission.INVOICE_WRITE);
				Invoice invoice = validateInvoice(conn, ansiURL.getId());
				SessionUser sessionUser = sessionData.getUser();				
				processUpdate(conn, request, response, invoice, sessionUser);
				conn.commit();
			} catch ( RecordNotFoundException e ) {
				super.sendNotFound(response);
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


	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {		
		super.sendNotAllowed(response);
	}


	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		super.sendNotAllowed(response);
	}


	private Invoice validateInvoice(Connection conn, Integer id) throws RecordNotFoundException, Exception {
		Invoice invoice = new Invoice();
		invoice.setInvoiceId(id);
		invoice.selectOne(conn);
		return invoice;
	}


	private void processUpdate(Connection conn, HttpServletRequest request, HttpServletResponse response, Invoice invoice, SessionUser sessionUser) throws Exception {
		WebMessages webMessages = new WebMessages();
		Date today = Calendar.getInstance(new AnsiTime()).getTime();
		SimpleDateFormat fileDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		InvoicePrintResponse data = new InvoicePrintResponse();
		String pathTranslated = request.getPathTranslated();
		Integer invoiceId = invoice.getInvoiceId();
		String invoicePathName = pathTranslated.substring(0, pathTranslated.length() - String.valueOf(invoiceId).length());
		invoicePathName = invoicePathName + "invoicePDF";
		File invoicePath = new File(invoicePathName);
		if ( ! invoicePath.exists()) {
			invoicePath.mkdirs();
		}
		String fileDate = fileDateFormat.format(today);
		String invoiceFileName = invoice.getInvoiceId() + "_" + fileDate + ".pdf";


		ByteArrayOutputStream baos = InvoiceUtils.reprintInvoice(conn, invoice, sessionUser.getUserId());
		
		
		FileOutputStream os = new FileOutputStream(new File(invoicePathName + "/" + invoiceFileName));
		baos.writeTo(os);
		os.flush();
		os.close();
		
		
		data.setInvoiceFile("invoicePDF/" + invoiceFileName);
		data.setWebMessages(webMessages);
		super.sendResponse(conn, response, ResponseCode.SUCCESS, data);

		
	}


	


	
	
}
