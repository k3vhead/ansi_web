package com.ansi.scilla.web.servlets.invoice;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.common.db.PermissionLevel;
import com.ansi.scilla.common.invoice.InvoiceUtils;
import com.ansi.scilla.common.jsonFormat.AnsiFormat;
import com.ansi.scilla.web.common.AppUtils;
import com.ansi.scilla.web.common.Permission;
import com.ansi.scilla.web.common.ResponseCode;
import com.ansi.scilla.web.common.WebMessages;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.request.InvoicePrintRequest;
import com.ansi.scilla.web.response.invoice.InvoicePrintResponse;
import com.ansi.scilla.web.servlets.AbstractServlet;
import com.ansi.scilla.web.struts.SessionData;
import com.ansi.scilla.web.struts.SessionUser;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;



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
			conn.setAutoCommit(false);
			String jsonString = super.makeJsonString(request);
			InvoicePrintRequest invoicePrintRequest = (InvoicePrintRequest)AppUtils.json2object(jsonString, InvoicePrintRequest.class);
//			ansiURL = new AnsiURL(request, "invoiceGeneration", (String[])null); //  .../ticket/etc
			SessionData sessionData = AppUtils.validateSession(request, Permission.INVOICE, PermissionLevel.PERMISSION_LEVEL_IS_WRITE);

			SessionUser sessionUser = sessionData.getUser(); 
			List<String> addErrors = super.validateRequiredAddFields(invoicePrintRequest);
			HashMap<String, String> errors = new HashMap<String, String>();
			if ( addErrors.isEmpty() ) {
				errors = validateDates(invoicePrintRequest);
			}
			if (addErrors.isEmpty() && errors.isEmpty()) {
				processUpdate(conn, request, response, invoicePrintRequest, sessionUser);
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

	
	
	private HashMap<String, String> validateDates(InvoicePrintRequest invoicePrintRequest) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss.S");
		System.out.println("Print: " + sdf.format(invoicePrintRequest.getPrintDate()));
		System.out.println("Due: " + sdf.format(invoicePrintRequest.getDueDate()));
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
		Calendar invoiceDate = Calendar.getInstance();
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

		Integer divisionId = invoicePrintRequest.getDivisionId();
		Division division = new Division();
		division.setDivisionId(divisionId);
		division.selectOne(conn);

		Date printDate = invoicePrintRequest.getPrintDate();				
		Date dueDate = invoicePrintRequest.getDueDate();		
		String fileDate = fileDateFormat.format(printDate);
		String invoiceFileName = division.getDivisionNbr() + "-" + division.getDivisionCode() + "_" + fileDate + ".pdf";

		Calendar printCalendar = Calendar.getInstance();
		printCalendar.setTime(printDate);
		Calendar dueCalendar = Calendar.getInstance();
		dueCalendar.setTime(dueDate);
		ByteArrayOutputStream baos = InvoiceUtils.printInvoices(conn, division, printCalendar, dueCalendar);
		
		
		FileOutputStream os = new FileOutputStream(new File(invoicePathName + "/" + invoiceFileName));
		baos.writeTo(os);
		os.flush();
		os.close();
		
		
		data.setInvoiceFile("invoicePDF/" + invoiceFileName);
		data.setWebMessages(webMessages);
		super.sendResponse(conn, response, ResponseCode.SUCCESS, data);

		
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




	private void fakeThePDF(Connection conn, HttpServletRequest request, HttpServletResponse response, InvoicePrintRequest invoicePrintRequest ) throws Exception {
		SimpleDateFormat fileDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		InvoicePrintResponse data = new InvoicePrintResponse();
		WebMessages webMessages = new WebMessages();
		String invoicePathName = request.getPathTranslated() + "invoicePDF";
		File invoicePath = new File(invoicePathName);
		if ( ! invoicePath.exists()) {
			invoicePath.mkdirs();
		}

		Integer divisionId = invoicePrintRequest.getDivisionId();
		Division division = new Division();
		division.setDivisionId(divisionId);
		division.selectOne(conn);

		Date printDate = invoicePrintRequest.getPrintDate();				
		Date dueDate = invoicePrintRequest.getDueDate();		
		String fileDate = fileDateFormat.format(printDate);
		String invoiceFileName = division.getDivisionNbr() + "-" + division.getDivisionCode() + "_" + fileDate + ".pdf";

		
		Font fontSplashTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
		Font fontSplashText = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
		float marginLeft = 30F;
		float marginRight = 30F;
		float marginTop = 36F;
		float marginBottom = 36F;
		SimpleDateFormat invoiceDateFormat = new SimpleDateFormat(AnsiFormat.DATE.pattern());

		Document document = new Document(PageSize.LETTER, marginLeft, marginRight, marginTop, marginBottom);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PdfWriter pdfWriter = PdfWriter.getInstance(document, baos);

		document.open();

		Paragraph p = new Paragraph();
		p.add(new Chunk("Invoices for ",fontSplashTitle));
		p.add(new Chunk(String.valueOf(divisionId),fontSplashTitle));
		p.add(Chunk.NEWLINE);
		p.add(new Chunk("Print date: " + invoiceDateFormat.format(printDate),fontSplashText));
		p.add(Chunk.NEWLINE);
		p.add(new Chunk("Due date: " + invoiceDateFormat.format(dueDate),fontSplashText));
		document.add(p);
		document.add(Chunk.NEXTPAGE);

		document.close();

		

		FileOutputStream os = new FileOutputStream(new File(invoicePathName + "/" + invoiceFileName));
		baos.writeTo(os);
		os.flush();
		os.close();
		
		
		data.setInvoiceFile("invoicePDF/" + invoiceFileName);
		data.setWebMessages(webMessages);
		super.sendResponse(conn, response, ResponseCode.SUCCESS, data);

	}
	
}
