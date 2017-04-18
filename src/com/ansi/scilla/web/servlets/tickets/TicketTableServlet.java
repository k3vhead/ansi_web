package com.ansi.scilla.web.servlets.tickets;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.common.db.PermissionLevel;
import com.ansi.scilla.common.queries.TicketSearch;
import com.ansi.scilla.web.common.AppUtils;
import com.ansi.scilla.web.common.Permission;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.response.ticketTable.TicketTableJsonResponse;
import com.ansi.scilla.web.response.ticketTable.TicketTableReturnItem;
import com.ansi.scilla.web.servlets.AbstractServlet;

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
		String[] cols = { "ticket_id", "ticket.ticket_status", "division_nbr", "bill_to_name", "job_site_name", "job_site_address", "start_date", "job_frequency", "act_price_per_cleaning", "job_nbr", "job.job_id", "service_description","process_date", "invoice_id", "fleetmatics_id" };
		String sStart = request.getParameter("start");
	    String sAmount = request.getParameter("length");
	    String sDraw = request.getParameter("draw");
	    String sCol = request.getParameter("order[0][column]");
	    String sdir = request.getParameter("order[0][dir]");
	   //System.out.println(sCol);
	   
	   //list all passed header and parameters
//	    Enumeration headerNames = request.getHeaderNames();
//	   while(headerNames.hasMoreElements()) {
//	     String headerName = (String)headerNames.nextElement();
//	     System.out.println("Header Name - " + headerName + ", Value - " + request.getHeader(headerName));
//	   }
//	   Enumeration params = request.getParameterNames(); 
//	   while(params.hasMoreElements()){
//	    String paramName = (String)params.nextElement();
//	    System.out.println("Parameter Name - "+paramName+", Value - "+request.getParameter(paramName));
//	   }
	   
		Connection conn = null;
		try {
			conn = AppUtils.getDBCPConn();
			AppUtils.validateSession(request, Permission.TICKET, PermissionLevel.PERMISSION_LEVEL_IS_READ);
//			String qs = request.getQueryString();
			Enumeration<String> e = request.getParameterNames();
			while ( e.hasMoreElements() ) {
				String name = e.nextElement();
				System.out.println(name);
			}
			String term = "";
			Integer jobId = null;
			
			if(request.getParameter("search[value]") != null){
				term = request.getParameter("search[value]");
			}
			if (! StringUtils.isBlank(request.getParameter("jobId"))) {
				jobId = Integer.valueOf(request.getParameter("jobId"));
			}
			System.out.println(term);
			if (sStart != null) {
		        start = Integer.parseInt(sStart);
		        if (start < 0)
		            start = 0;
		    }
		    if (sAmount != null) {
		    	amount = Integer.parseInt(sAmount);
				System.out.println(sAmount);
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
		        if (col < 0 || col > 15)
		            col = 0;
		    }
		    if (sdir != null) {
		        if (!sdir.equals("asc"))
		            dir = "desc";
		    }
		    
		    String colName = cols[col];
		    int total = 0;
		    String sql = "select count(*)"
					+ " from ticket";
			Statement s = conn.createStatement();
					
			ResultSet rs0 = s.executeQuery(sql);
			if(rs0.next()){
		        total = rs0.getInt(1);
		    }
			
		    int totalAfterFilter = total;
			
			List<TicketTableReturnItem> resultList = new ArrayList<TicketTableReturnItem>();
			sql = TicketSearch.sql;

			String search = TicketSearch.generateWhereClause(term);
			
//			System.out.println("search: " +search);
			sql += search;
			if (jobId != null) {
				sql += " and job.job_id=" + jobId + " ";
			}
			System.out.println(sql);
			sql += " order by " + colName + " " + dir;
			System.out.println(sql);
			if ( amount != -1) {
				sql += " OFFSET "+ start+" ROWS"
					+ " FETCH NEXT " + amount + " ROWS ONLY";
			}
//			System.out.println(sql);
			
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
			
			if (jobId != null) {
				sql2 += " and job.job_id=" + jobId + " ";
			}
			
			if (search != "") {
				sql2 += search;
			}
			System.out.println(sql2);
			Statement s2 = conn.createStatement();
			ResultSet rs2 = s2.executeQuery(sql2);
			if(rs2.next()){
				totalAfterFilter = rs2.getInt(1);
				//totalAfterFilter = 1;
		    }
			rs.close();
			rs0.close();
			rs2.close();
			
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("application/json");
			
			TicketTableJsonResponse ticketTableJsonResponse = new TicketTableJsonResponse();
			ticketTableJsonResponse.setRecordsFiltered(totalAfterFilter);
			ticketTableJsonResponse.setRecordsTotal(total);
			ticketTableJsonResponse.setData(resultList);
			ticketTableJsonResponse.setDraw(draw);
			
//			System.out.println("Total:"+total);
//			System.out.println("TotalAfterFilter:"+totalAfterFilter);
			String json = AppUtils.object2json(ticketTableJsonResponse);
			
//			System.out.println(json);
			
			
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