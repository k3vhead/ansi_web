package com.ansi.scilla.web.servlets.quote;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.common.db.PermissionLevel;
import com.ansi.scilla.common.db.Quote;
import com.ansi.scilla.common.quote.QuotePrinter;
import com.ansi.scilla.web.common.AnsiURL;
import com.ansi.scilla.web.common.AppUtils;
import com.ansi.scilla.web.common.Permission;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.servlets.AbstractServlet;
import com.ansi.scilla.web.struts.SessionData;
import com.ansi.scilla.web.struts.SessionUser;
import com.thewebthing.commons.db2.RecordNotFoundException;



public class QuotePrintServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;
	public static final String QUOTE_DATE = "quoteDate"; 
	private final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

	
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
			ansiURL = new AnsiURL(request, "quotePrint", (String[])null); 
			SessionData sessionData = AppUtils.validateSession(request, Permission.QUOTE, PermissionLevel.PERMISSION_LEVEL_IS_WRITE);
			Integer quoteId = ansiURL.getId();
			String dateString = request.getParameter(QUOTE_DATE);

			SessionUser sessionUser = sessionData.getUser();			
			Date quoteDate = makeQuoteDate(dateString);
			
			try {
				validateQuote(conn, quoteId);
				processPrint(conn, response, quoteId, quoteDate, sessionUser);
			} catch ( RecordNotFoundException e) {
				super.sendNotFound(response);
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

	
	
	private Date makeQuoteDate(String dateString) {
		Date quoteDate = new GregorianCalendar(new Locale("America/Chicago")).getTime();
		try {
			if ( ! StringUtils.isBlank(dateString)) {
				quoteDate = sdf.parse(dateString);
			}
		} catch ( ParseException e ) {
			// just use current date
		}
		return quoteDate;
	}


	private void validateQuote(Connection conn, Integer quoteId) throws Exception {
		Quote quote = new Quote();
		quote.setQuoteId(quoteId);
		quote.selectOne(conn);
	}


	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		super.sendNotAllowed(response);
	}


	private void processPrint(Connection conn, HttpServletResponse response, Integer quoteId, Date quoteDate, SessionUser sessionUser) throws Exception {
		
		ByteArrayOutputStream baos = QuotePrinter.printQuote(conn, quoteId, quoteDate, sessionUser.getUserId());
		
		Calendar today = Calendar.getInstance(new Locale("America/Chicago"));
		SimpleDateFormat fileDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String fileDate = fileDateFormat.format(today.getTime());

		String fileName = "quote" + quoteId + "_" + fileDate + ".pdf";
		
        response.setHeader("Expires", "0");
        response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        response.setHeader("Pragma", "public");
		String dispositionHeader = "attachment; filename=" + fileName;

		response.setHeader("Content-disposition",dispositionHeader);
        // setting the content type
        response.setContentType("application/pdf");
        // the contentlength
        response.setContentLength(baos.size());
        // write ByteArrayOutputStream to the ServletOutputStream
        OutputStream os = response.getOutputStream();
        baos.writeTo(os);
        os.flush();
        os.close();
	}


}
