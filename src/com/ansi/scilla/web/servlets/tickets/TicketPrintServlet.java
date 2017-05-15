package com.ansi.scilla.web.servlets.tickets;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.common.db.PermissionLevel;
import com.ansi.scilla.common.jobticket.TicketPrinter;
import com.ansi.scilla.common.queries.TicketPrint;
import com.ansi.scilla.common.queries.TicketPrintResult;
import com.ansi.scilla.web.common.AppUtils;
import com.ansi.scilla.web.common.Permission;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.servlets.AbstractServlet;
import com.ansi.scilla.web.struts.SessionData;
import com.ansi.scilla.web.struts.SessionUser;
import com.thewebthing.commons.db2.RecordNotFoundException;



public class TicketPrintServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;
	public static final String PRINT_DATE = "printDate"; 
	public static final String DIVISION_ID = "divisionId";
	
	private final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

	
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {		
		super.sendNotAllowed(response);
	}


	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Connection conn = null;
		
		try {
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);

			SessionData sessionData = AppUtils.validateSession(request, Permission.TICKET, PermissionLevel.PERMISSION_LEVEL_IS_WRITE);
			String dateString = request.getParameter(PRINT_DATE);
			Integer divisionId = Integer.valueOf(request.getParameter(DIVISION_ID));

			SessionUser sessionUser = sessionData.getUser();			
			Date endDate = makeDate(dateString);
			
			try {
				processPrint(conn, response, divisionId, endDate, sessionUser);
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

	
	
	private Date makeDate(String dateString) {
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


	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		super.sendNotAllowed(response);
	}


	private void processPrint(Connection conn, HttpServletResponse response, Integer divisionId, Date endDate, SessionUser sessionUser) throws Exception {
		TicketPrint tp = new TicketPrint();
		tp.setWhereClause("\nwhere ticket.act_division_id=10 and ticket.start_date<EOMONTH(SYSDATETIME(),2) and ticket.ticket_status='N'\n");
		tp.setSortField(new String[] { "ticket.start_date" });
		List<TicketPrintResult> ticketList = tp.selectSome(conn);
		
		TicketPrinter ticketPrinter = new TicketPrinter();
		ByteArrayOutputStream baos = ticketPrinter.makeTickets(conn, ticketList);
		
		Calendar today = Calendar.getInstance(TimeZone.getTimeZone("America/Chicago"));
		SimpleDateFormat fileDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String fileDate = fileDateFormat.format(today.getTime());

		String fileName = "tickets_" + divisionId + "_" + fileDate + ".pdf";
		
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
