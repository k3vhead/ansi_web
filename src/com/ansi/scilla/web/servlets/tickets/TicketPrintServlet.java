package com.ansi.scilla.web.servlets.tickets;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.common.db.PermissionLevel;
import com.ansi.scilla.common.db.Ticket;
import com.ansi.scilla.common.jobticket.TicketPrinter;
import com.ansi.scilla.common.jobticket.TicketStatus;
import com.ansi.scilla.common.queries.TicketPrint;
import com.ansi.scilla.common.queries.TicketPrintResult;
import com.ansi.scilla.web.common.AnsiURL;
import com.ansi.scilla.web.common.AppUtils;
import com.ansi.scilla.web.common.Permission;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.servlets.AbstractServlet;
import com.ansi.scilla.web.struts.SessionData;
import com.ansi.scilla.web.struts.SessionUser;



public class TicketPrintServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;
	public static final String PRINT_DATE = "printDate"; 
	public static final String DIVISION_ID = "divisionId";
	
	private final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
	private static final String TICKET_WHERE_CLAUSE = "\nwhere ticket.act_division_id=? and ticket.start_date<? and ticket.ticket_status=?\n";

	
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {		
		Connection conn = null;

		AnsiURL url = null;
		try {
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);

			url = new AnsiURL(request, "ticketPrint", new String[] {} );
			SessionData sessionData = AppUtils.validateSession(request, Permission.TICKET, PermissionLevel.PERMISSION_LEVEL_IS_WRITE);
			SessionUser sessionUser = sessionData.getUser();			

			if ( url.getId() == null ) {
				super.sendNotFound(response);
			} else {
				processTicketPrint(conn, response, url.getId(), sessionUser);
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
			Date endDate = makeEndDate(dateString);
			
			processDivisionPrint(conn, response, divisionId, endDate, sessionUser);
			
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


	private void processTicketPrint(Connection conn, HttpServletResponse response, Integer ticketId, SessionUser sessionUser) throws Exception {
		TicketPrint tp = new TicketPrint();
		TicketPrintResult result = tp.selectOne(conn, ticketId);
		List<TicketPrintResult> ticketList = Arrays.asList(new TicketPrintResult[] { result });

		Calendar today = Calendar.getInstance(TimeZone.getTimeZone("America/Chicago"));
		SimpleDateFormat fileDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String fileDate = fileDateFormat.format(today.getTime());
	
		String fileName = "tickets_" + ticketId + "_" + fileDate + ".pdf";

		doTicketPrint(conn, response, ticketList, fileName);
		updateTicketStatus(conn, ticketList, sessionUser);

		
	}


	private void processDivisionPrint(Connection conn, HttpServletResponse response, Integer divisionId, Date endDate, SessionUser sessionUser) throws Exception {
		
		
		System.out.println("Division: " + divisionId + " Date: " + endDate);
		
		Division division = new Division();
		division.setDivisionId(divisionId);
		division.selectOne(conn);
		String divisionString = division.getDivisionNbr() + "-" + division.getDivisionCode();
		
		TicketPrint tp = new TicketPrint();
		tp.setWhereClause(TICKET_WHERE_CLAUSE);
		tp.setSortField(new String[] { "ticket.start_date" });
		List<Object> boundVariables = new ArrayList<Object>();
		boundVariables.add(divisionId);
		boundVariables.add(endDate);
		boundVariables.add(TicketStatus.NOT_DISPATCHED.code());
		List<TicketPrintResult> ticketList = tp.selectSome(conn, boundVariables);

		Calendar today = Calendar.getInstance(TimeZone.getTimeZone("America/Chicago"));
		SimpleDateFormat fileDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String fileDate = fileDateFormat.format(today.getTime());
	
		String fileName = "tickets_" + divisionString + "_" + fileDate + ".pdf";

		doTicketPrint(conn, response, ticketList, fileName);
		updateTicketStatus(conn, ticketList, sessionUser);

	}
	
	private void doTicketPrint(Connection conn, HttpServletResponse response, List<TicketPrintResult> ticketList, String fileName) throws Exception {
		System.out.println("Printing " + ticketList.size() + " tickets");
		TicketPrinter ticketPrinter = new TicketPrinter();
		ByteArrayOutputStream baos = ticketPrinter.makeTickets(conn, ticketList);
		
		
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


	private Date makeEndDate(String dateString) {
		Calendar endDate = new GregorianCalendar(TimeZone.getTimeZone("America/Chicago"));
		try {
			if ( ! StringUtils.isBlank(dateString)) {
				endDate.setTime(sdf.parse(dateString));
				DateUtils.round(endDate, Calendar.DAY_OF_MONTH);
				endDate.add(Calendar.DAY_OF_YEAR, 1);
			}
		} catch ( ParseException e ) {
			// just use current date
		}
		return endDate.getTime();
	}


	private void updateTicketStatus(Connection conn, List<TicketPrintResult> ticketList, SessionUser sessionUser) throws Exception {

		Ticket ticket = new Ticket();
		Ticket key = new Ticket();
		for ( TicketPrintResult result : ticketList ) {
			ticket = new Ticket();
			ticket.setTicketId(result.getTicketNumber());
			ticket.selectOne(conn);
			Integer printCount = ticket.getPrintCount() == null ? 1 : ticket.getPrintCount() + 1;
			ticket.setStatus(TicketStatus.DISPATCHED.code());
			ticket.setUpdatedBy(sessionUser.getUserId());
			ticket.setPrintCount(printCount);
			//ticket.setUpdatedBy(updatedBy);  // set by the super
			
			key.setTicketId(result.getTicketNumber());
			ticket.update(conn, key);
		}

	}


}
