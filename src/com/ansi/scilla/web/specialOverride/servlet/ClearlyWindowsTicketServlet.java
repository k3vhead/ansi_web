package com.ansi.scilla.web.specialOverride.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;

import com.ansi.scilla.common.AnsiTime;
import com.ansi.scilla.common.utils.Permission;
import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.ResourceNotFoundException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.specialOverride.response.SpecialOverrideResponse;
import com.ansi.scilla.web.specialOverride.response.SpecialOverrideSelectItem;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class ClearlyWindowsTicketServlet extends AbstractOverrideServlet {

	private static final long serialVersionUID = 1L;
	
	public static final String REALM = "CLEARLY_WINDOWS_TICKETS";
	public static final String REALM_DESC = "Create 25 Clearly Windows On-Call Tickets";
	
	private final String selectSql = "select "
			+ " ticket_id,\n"
			+ "	job_id,\n"
			+ "	concat(division.division_nbr,'-',division.division_code) as div,\n"
			+ "	ticket_type,\n"
			+ "	act_po_number,\n"
			+ "	ticket_status,\n"
			+ "	format(start_date,'MM/dd/yyyy') as start_dt,\n"
			+ "	invoice_id,\n"
			+ "	format(invoice_date,'MM/dd/yyyy') as invoice_dt,\n"
			+ "	print_count,\n"
			+ "	act_tax_amt,\n"
			+ "	act_tax_rate,\n"
			+ "	act_tax_rate_id,\n"
			+ "	process_notes,\n"
			+ "	process_date,\n"
			+ "	act_dl_pct,\n"
			+ "	act_dl_amt,\n"
			+ "	act_price_per_cleaning,\n"
			+ "	customer_signature,\n"
			+ "	bill_sheet,\n"
			+ "	mgr_approval\n"
			+ " from ticket\n"
			+ " inner join division on division.division_id=ticket.act_division_id\n"
			+ " where job_id = 79041 and start_date = (select max(start_date) from ticket where job_id = 79041)";
	
	private final String updateSql = "insert into ticket (\n"
			+ "	job_id, \n"
			+ "	act_division_id, \n"
			+ "	ticket_type, \n"
			+ "	ticket_status, \n"
			+ "	start_date, \n"
			+ "	act_tax_amt, \n"
			+ "	act_tax_rate_id, \n"
			+ "	act_dl_pct, \n"
			+ "	act_dl_amt, \n"
			+ "	act_price_per_cleaning, \n"
			+ "	added_by, \n"
			+ "	added_date, \n"
			+ "	updated_by, \n"
			+ "	updated_date\n"
			+ "	)\n"
			+ "select top 25 \n"
			+ "	ticket.job_id as job_id, \n"
			+ "	job.division_id as act_division_id, \n"
			+ "	'job' as ticket_type, \n"
			+ "	'N' as ticket_status, \n"
			+ "	? as start_date, \n"
			+ "	0.00 as act_tax_amt, \n"
			+ "	ticket.act_tax_rate_id as act_tax_rate_id, \n"
			+ "	ticket.act_dl_pct as act_dl_pct, \n"
			+ "	ticket.act_dl_amt as act_dl_amt, \n"
			+ "	ticket.act_price_per_cleaning as act_price_per_cleaning, \n"
			+ "	? as added_by, \n"
			+ "	? as added_date, \n"
			+ " ? as updated_by, \n"
			+ "	? as updated_date\n"
			+ "from ticket\n"
			+ "inner join job on job.job_id=ticket.job_id\n"
			+ "where ticket.job_id=79041 and ticket.ticket_type='job'\n"
			+ "order by ticket.ticket_id desc";
	
	private final String updateSelectSql = "select * from ticket where job_id = 79041 and start_date = ?";
	
	private final String FIELD_START_DATE = "start_date";
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Connection conn = null;
		try {
			conn = AppUtils.getDBCPConn();
			AppUtils.validateSession(request, Permission.QUOTE_CREATE);
			sendSelectResults(conn, response);
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e) {	
			logger.log(Level.DEBUG, e);
			super.sendForbidden(response);			
		} catch ( RecordNotFoundException | ResourceNotFoundException e ) {					
			logger.log(Level.DEBUG, e);			
			super.sendNotFound(response);						
		} catch ( Exception e) {
			AppUtils.logException(e);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);	
		}
	}
	
	
	
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		logger.log(Level.DEBUG, "You are here");
		Connection conn = null;
		try {
			SessionData sessionData = AppUtils.validateSession(request, Permission.QUOTE_CREATE);
			SessionUser user = sessionData.getUser();
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);
				
			Date startDate = makeStartDate(request.getParameter(FIELD_START_DATE));
			if ( startDate == null ) {
				sendEditErrors(conn, response);
			} else {
				doUpdate(conn, response, user, startDate );
				sendUpdateResults(conn, response, startDate);
			}
			
			
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e) {							// these are thrown by session validation
			super.sendForbidden(response);
		} catch ( RecordNotFoundException | ResourceNotFoundException e) {		
			super.sendNotFound(response);
		} catch ( Exception e) {						// something bad happened
			AppUtils.logException(e);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);					// return the connection to the pool
		}	
	}
	
	
	@Override
	public void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		super.sendForbidden(response);
	}


	private Date makeStartDate(String parameter) {
		logger.log(Level.DEBUG, "start date: " + parameter);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date startDate = null;
		if ( ! StringUtils.isBlank(parameter) ) {
			try {
				startDate = sdf.parse(parameter);
			} catch (ParseException e) {
				// means invalid data, so startDate stays null;
			}
		}
		return startDate;
	}



	private void sendSelectResults(Connection conn, HttpServletResponse response) throws Exception {
		WebMessages webMessages = new WebMessages();
		Statement ps = conn.createStatement();
		ResultSet rs = ps.executeQuery(this.selectSql);
		
		SpecialOverrideResponse data = new SpecialOverrideResponse(rs);
		rs.close();
		List<SpecialOverrideSelectItem> updateList = new ArrayList<SpecialOverrideSelectItem>();
		updateList.add(new SpecialOverrideSelectItem("Start Date",FIELD_START_DATE,java.sql.Date.class.getSimpleName()));
		data.setUpdateList( updateList );
		
		webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "Success");
		data.setWebMessages(webMessages);
		super.sendResponse(conn, response, ResponseCode.SUCCESS, data);
	}
	
	
	private void sendEditErrors(Connection conn, HttpServletResponse response) throws Exception {
		logger.log(Level.DEBUG,"sendEditErrors");
		WebMessages webMessages =new WebMessages();
		webMessages.addMessage(FIELD_START_DATE, "Invalid date");
		SpecialOverrideResponse data = new SpecialOverrideResponse();
		data.setWebMessages(webMessages);
		super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, data);		
	}



	private void doUpdate(Connection conn, HttpServletResponse response, SessionUser user, Date startDate) throws SQLException {
		PreparedStatement ps = conn.prepareStatement(this.updateSql);
		Calendar today = Calendar.getInstance(new AnsiTime());
		java.sql.Date rightNow = new java.sql.Date(today.getTime().getTime());
		ps.setDate(1, rightNow);   // start date
		ps.setInt(2, user.getUserId()); // added by
		ps.setDate(3, rightNow); // added date
		ps.setInt(4, user.getUserId()); // updated by
		ps.setDate(5, rightNow); // updated date
		ps.executeUpdate();
		conn.commit();
	}



	private void sendUpdateResults(Connection conn, HttpServletResponse response, Date startDate) throws Exception {
		PreparedStatement ps = conn.prepareStatement(this.updateSelectSql);
		ps.setDate(1, new java.sql.Date(startDate.getTime()));
		ResultSet rs = ps.executeQuery();
		
		SpecialOverrideResponse data = new SpecialOverrideResponse(rs);
		rs.close();
		
		WebMessages webMessages = new WebMessages();
		webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "Success");
		data.setWebMessages(webMessages);
		super.sendResponse(conn, response, ResponseCode.SUCCESS, data);
	}
}
