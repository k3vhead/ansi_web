package com.ansi.scilla.web.ticket.servlet;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.common.AnsiTime;
import com.ansi.scilla.common.db.PermissionLevel;
import com.ansi.scilla.common.queries.TicketLookupSearch;
import com.ansi.scilla.common.queries.TicketLookupSearchItem;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.Permission;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.ticket.response.TicketTableJsonResponse;

/**
 * The url for delete will return methodNotAllowed
 * 
 * The url for post will return methodNotAllowed
 * 
 * The url for get will be the following: 
 * 		/ticketTable  	See DataTables Table plug-in for jQuery at datatables.net for details
 * 
 * @author ggroce
 *
 */
public class TicketTableServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		super.sendNotAllowed(response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		super.sendNotAllowed(response);
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		int amount = 10;
		int start = 0;
		int draw = 0;
		int col = 0;
		String dir = "asc";
		String[] cols = { "view_ticket_log.ticket_id", "view_ticket_log.ticket_status", "division_nbr", "bill_to_name", "job_site_name", "job_site_address", "view_ticket_log.start_date", "job_frequency", "job.price_per_cleaning", "job_nbr", "job.job_id", "service_description","process_date", "invoice_id", "fleetmatics_id" };
		String sStart = request.getParameter("start");
		String sAmount = request.getParameter("length");
		String sDraw = request.getParameter("draw");
		String sCol = request.getParameter("order[0][column]");
		String sdir = request.getParameter("order[0][dir]");


		Connection conn = null;
		try {
			conn = AppUtils.getDBCPConn();
			AppUtils.validateSession(request, Permission.TICKET, PermissionLevel.PERMISSION_LEVEL_IS_READ);
			String term = "";
			Integer parmJobId = null;
			Integer parmDivisionId = null;
			Calendar parmStartDate = null;
			String parmStatus = null;

			if(request.getParameter("search[value]") != null){
				term = request.getParameter("search[value]");
			}
			if (! StringUtils.isBlank(request.getParameter("jobId"))) {
				parmJobId = Integer.valueOf(request.getParameter("jobId"));
			}
			if (! StringUtils.isBlank(request.getParameter("divisionId"))) {
				parmDivisionId = Integer.valueOf(request.getParameter("divisionId"));
			}
			if (! StringUtils.isBlank(request.getParameter("startDate"))) {
				SimpleDateFormat parmDateFormatter = new SimpleDateFormat("MM/dd/yyyy");
				Date parmDate = parmDateFormatter.parse(request.getParameter("startDate"));
				parmStartDate = Calendar.getInstance(new AnsiTime());
				parmStartDate.setTime(parmDate);
			}
			if ( ! StringUtils.isBlank(request.getParameter("status"))) {
				parmStatus = request.getParameter("status");
			}
			
			if (sStart != null) {
				start = Integer.parseInt(sStart);
				start = start < 0 ? 0 : start;
			}
			if (sAmount != null) {
				amount = Integer.parseInt(sAmount);
				if (amount < 10 ) {
					amount = 10;
				} else if (amount > 1000) {
					amount = 1000;
				}
			}
			if (sDraw != null) {
				draw = Integer.parseInt(sDraw);
			}
			if (sCol != null) {
				col = Integer.parseInt(sCol);
				if (col < 0 || col > 15) {
					col = 0;
				}
			}
			if (sdir != null) {
				if (!sdir.equals("asc")) {
					dir = "desc";
				}
			}

			String colName = cols[col];


			/*
			int total = 0;
			int totalAfterFilter = total;
			
			String sql = "select count(*)"
					+ " from ticket";
			Statement s = conn.createStatement();

			ResultSet rs0 = s.executeQuery(sql);
			if(rs0.next()){
				total = rs0.getInt(1);
			}

			List<TicketTableReturnItem> resultList = new ArrayList<TicketTableReturnItem>();
			sql = TicketSearch.sql;

			String search = TicketSearch.generateWhereClause(term);

			//			logger.log(Level.DEBUG, "search: " +search);
			sql += search;
			if (parmJobId != null) {
				sql += " and job.job_id=" + parmJobId + " ";
			}
			if ( parmDivisionId != null ) {
				sql += " and job.division_id=" + parmDivisionId + " ";
			}
			logger.log(Level.DEBUG, sql);
			sql += " order by " + colName + " " + dir;
			logger.log(Level.DEBUG, sql);
			if ( amount != -1) {
				sql += " OFFSET "+ start+" ROWS"
						+ " FETCH NEXT " + amount + " ROWS ONLY";
			}
			logger.log(Level.DEBUG, sql);

			s = conn.createStatement();
			ResultSet rs = s.executeQuery(sql);
			ResultSetMetaData rsmd = rs.getMetaData();
			while ( rs.next() ) {
				TicketTableReturnItem item = new TicketTableReturnItem();				
				TicketTableReturnItem.rs2Object(item, rsmd, rs);
				resultList.add(item);
				//resultList.add(new TicketTableReturnItem(rs));
			}

			String sql2 = "select count(*) "
					+ TicketSearch.sqlFromClause;

			if (parmJobId != null) {
				sql2 += " and job.job_id=" + parmJobId + " ";
			}

			if (search != "") {
				sql2 += search;
			}
			logger.log(Level.DEBUG, sql2);
			Statement s2 = conn.createStatement();
			ResultSet rs2 = s2.executeQuery(sql2);
			if(rs2.next()){
				totalAfterFilter = rs2.getInt(1);
				//totalAfterFilter = 1;
			}
			rs.close();
			rs0.close();
			rs2.close();
			*/
			
			
			TicketLookupSearch ticketSearch = new TicketLookupSearch(start, amount);
			ticketSearch.setSearchTerm(term);
			ticketSearch.setDivisionId(parmDivisionId);
			ticketSearch.setJobId(parmJobId);
			ticketSearch.setSortBy(colName);
			ticketSearch.setSortIsAscending(dir.equals("asc"));
			ticketSearch.setStartDate(parmStartDate);
			ticketSearch.setStatus(parmStatus);
			List<TicketLookupSearchItem> itemList = ticketSearch.select(conn);
			Integer filteredCount = ticketSearch.selectCount(conn);
			Integer totalCount = ticketSearch.countAll(conn);
			
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("application/json");

			TicketTableJsonResponse ticketTableJsonResponse = new TicketTableJsonResponse();
			ticketTableJsonResponse.setRecordsFiltered(filteredCount);
			ticketTableJsonResponse.setRecordsTotal(totalCount);
			ticketTableJsonResponse.makeData(itemList);
			ticketTableJsonResponse.setDraw(draw);

			String json = AppUtils.object2json(ticketTableJsonResponse);

			ServletOutputStream o = response.getOutputStream();
			OutputStreamWriter writer = new OutputStreamWriter(o);
			writer.write(json);
			writer.flush();
			writer.close();
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e) {
			super.sendForbidden(response);
		} catch ( Exception e ) {
			AppUtils.logException(e);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}
	}


}
