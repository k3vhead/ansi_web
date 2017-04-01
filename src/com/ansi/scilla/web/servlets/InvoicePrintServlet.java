package com.ansi.scilla.web.servlets;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.Calendar;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ansi.scilla.common.db.PermissionLevel;
import com.ansi.scilla.web.common.AppUtils;
import com.ansi.scilla.web.common.Permission;
import com.ansi.scilla.web.common.ResponseCode;
import com.ansi.scilla.web.common.WebMessages;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.request.InvoicePrintRequest;
import com.ansi.scilla.web.response.invoice.InvoicePrintResponse;
import com.ansi.scilla.web.struts.SessionData;
import com.ansi.scilla.web.struts.SessionUser;



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
			conn = AppUtils.getDBCPConn();
			String jsonString = super.makeJsonString(request);
			InvoicePrintRequest invoicePrintRequest = (InvoicePrintRequest)AppUtils.json2object(jsonString, InvoicePrintRequest.class);
//			ansiURL = new AnsiURL(request, "invoiceGeneration", (String[])null); //  .../ticket/etc
			SessionData sessionData = AppUtils.validateSession(request, Permission.INVOICE, PermissionLevel.PERMISSION_LEVEL_IS_WRITE);

			SessionUser sessionUser = sessionData.getUser(); 
			List<String> addErrors = super.validateRequiredAddFields(invoicePrintRequest);
			if ( addErrors.isEmpty() ) {
//				processUpdate(conn, response, invoiceGenerationRequest, sessionUser);
				fakeThePDF(conn, request, response, invoicePrintRequest);
			} else {
				processError(conn, response, addErrors);
			}
			conn.commit();
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


	private void processUpdate(Connection conn, HttpServletResponse response, InvoicePrintRequest invoicePrintRequest, SessionUser sessionUser) throws Exception {
		Calendar invoiceDate = Calendar.getInstance();
//		invoiceDate.setTime(invoiceGenerationRequest.getInvoiceDate());
//		Boolean monthlyFlag = invoiceGenerationRequest.getMonthlyFlag();
//		Integer userId = sessionUser.getUserId();
//		InvoiceUtils.generateInvoices(conn, invoiceDate, monthlyFlag, userId);
		
		WebMessages webMessages = new WebMessages();
		webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "Update Successful");
		InvoicePrintResponse data = new InvoicePrintResponse();
		data.setWebMessages(webMessages);
		super.sendResponse(conn, response, ResponseCode.SUCCESS, data);
		
	}


	private void processError(Connection conn, HttpServletResponse response, List<String> addErrors) throws Exception {
		WebMessages webMessages = new WebMessages();
		for ( String error : addErrors ) {
			webMessages.addMessage(error, "Required field");
		}
		
		InvoicePrintResponse data = new InvoicePrintResponse();
		data.setWebMessages(webMessages);
		super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, data);
	}




	private void fakeThePDF(Connection conn, HttpServletRequest request, HttpServletResponse response, InvoicePrintRequest invoicePrintRequest ) throws Exception {
		InvoicePrintResponse data = new InvoicePrintResponse();
		WebMessages webMessages = new WebMessages();
		System.out.println("Path trans: " +request.getPathTranslated()); // absolute path on filesystem
		data.setInvoiceFile("xxx");
		data.setWebMessages(webMessages);
		super.sendResponse(conn, response, ResponseCode.SUCCESS, data);
		
//		Integer divisionId = invoicePrintRequest.getDivisionId();
//		Date printDate = invoicePrintRequest.getPrintDate();
//		Date dueDate = invoicePrintRequest.getDueDate();
//		Font fontSplashTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
//		Font fontSplashText = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
//		float marginLeft = 30F;
//		float marginRight = 30F;
//		float marginTop = 36F;
//		float marginBottom = 36F;
//		SimpleDateFormat invoiceDateFormat = new SimpleDateFormat(AnsiFormat.DATE.pattern());
//
//		Document document = new Document(PageSize.LETTER, marginLeft, marginRight, marginTop, marginBottom);
//
//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		PdfWriter pdfWriter = PdfWriter.getInstance(document, baos);
//
//		document.open();
//
//		Paragraph p = new Paragraph();
//		p.add(new Chunk("Invoices for ",fontSplashTitle));
//		p.add(new Chunk(String.valueOf(divisionId),fontSplashTitle));
//		p.add(Chunk.NEWLINE);
//		p.add(new Chunk("Print date: " + invoiceDateFormat.format(printDate),fontSplashText));
//		p.add(Chunk.NEWLINE);
//		p.add(new Chunk("Due date: " + invoiceDateFormat.format(dueDate),fontSplashText));
//		document.add(p);
//		document.add(Chunk.NEXTPAGE);
//
//		document.close();
//
//		
//
//		FileOutputStream os = new FileOutputStream(new File("/home/dclewis/Documents/projects/ANSI_Scheduling/invoice_test_" + divisionId + ".pdf"));
//		baos.writeTo(os);
//		os.flush();
//		os.close();

	}
	
}
