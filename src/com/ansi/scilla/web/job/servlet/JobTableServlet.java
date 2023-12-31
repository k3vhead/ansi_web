package com.ansi.scilla.web.job.servlet;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Level;

import com.ansi.scilla.common.db.Job;
import com.ansi.scilla.common.queries.JobSearch;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.common.utils.Permission;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.job.response.JobTableJsonResponse;
import com.ansi.scilla.web.job.response.JobTableReturnItem;

/**
 * Use JobLookupServlet instead of JobTableServlet
 * 
 * The url for delete will return methodNotAllowed
 * 
 * The url for post will return methodNotAllowed
 * 
 * The url for get will be the following: 
 * 		/jobTable  	See DataTables Table plug-in for jQuery at datatables.net for details
 * 
 * @author ggroce
 *
 */
@Deprecated
public class JobTableServlet extends AbstractServlet {

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
		String[] cols = { "job_id"	// column: job
				, "job.quote_id"	// column: quote
				, "job.job_status" 	// column: status
				, "division_nbr"	// column: div
				, "bill_to_name"	// column: bill to
				, "job_site_name"	// column: job site
				, "job_site_address"	// column: address
				, "start_date"	// column: start date
				,"job_frequency"	// column: freq
				, "price_per_cleaning"	// column: PPC
				, "job_nbr"	// column: job#
				, "service_description"	// column: service description
				, "po_number"	// column: PO
				, "concat(job_contact.last_name,', ',job_contact.first_name)"	// column: job contact
				, "concat(site_contact.last_name,', ',site_contact.first_name)"	// column: site contact
				, "concat(contract_contact.last_name,', ',contract_contact.first_name)"	// column: contract contact
				, "concat(billing_contact.last_name,', ',billing_contact.first_name)"	// column: billing contact
				, "quote.proposal_date"	// column: proposed
				, "job.activation_date"	// column: active
				, "job.cancel_date"	// column: cancel
				, "job.cancel_reason"	// column: reason
				};
		String sStart = request.getParameter("start");
	    String sAmount = request.getParameter("length");
	    String sDraw = request.getParameter("draw");
	    String sCol = request.getParameter("order[0][column]");
	    String sdir = request.getParameter("order[0][dir]");
	   
	   
		Connection conn = null;
		try {
			conn = AppUtils.getDBCPConn();
			SessionData sessionData = AppUtils.validateSession(request, Permission.QUOTE_READ);
			SessionUser user = sessionData.getUser();
//			String qs = request.getQueryString();

			String term = "";
			
			if(request.getParameter("search[value]") != null){
				term = request.getParameter("search[value]");
			}
			logger.log(Level.INFO, term);
			if (sStart != null) {
		        start = Integer.parseInt(sStart);
		        if (start < 0) {
		            start = 0;
		        }
		    }
		    if (sAmount != null) {
		    	amount = Integer.parseInt(sAmount);
		    	logger.log(Level.DEBUG, sAmount);
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
		        if (col < 0 || col > 20)
		            col = 0;
		    }
		    if (sdir != null) {
		        if (sdir.equals("asc")) {
		            dir = "asc";
		        } else if (sdir.equals("desc")) {
		            dir = "desc";
		        }
		    }
		    
		    String colName = cols[col];
		    int total = Job.selectCount(conn, user.getUserId());
//		    String sql = "select count(*)"
//					+ " from job";
//			Statement s = conn.createStatement();
//			ResultSet rs0 = s.executeQuery(sql);
//			if(rs0.next()){
//		        total = rs0.getInt(1);
//		    }
			
		    int totalAfterFilter = JobSearch.selectFilteredCount(conn, user.getUserId(), term);
			
			List<JobTableReturnItem> resultList = new ArrayList<JobTableReturnItem>();
			List<JobSearch> jobSearchList = JobSearch.select(conn, user.getUserId(), term, start, amount, colName, dir);
			for ( JobSearch jobSearch : jobSearchList) {
				resultList.add(new JobTableReturnItem(jobSearch));
			}
//			sql = JobSearch.sql;
//
//			String search = JobSearch.generateWhereClause(term);
//			
//			logger.log(Level.DEBUG, sql);
//			sql += search;
//			logger.log(Level.DEBUG, sql);
//			sql += " order by " + colName + " " + dir;
//			logger.log(Level.DEBUG, sql);
//			if ( amount != -1) {
//				sql += " OFFSET "+ start+" ROWS"
//					+ " FETCH NEXT " + amount + " ROWS ONLY";
//			}
//			logger.log(Level.DEBUG, sql);
//			
//			s = conn.createStatement();
//			ResultSet rs = s.executeQuery(sql);
//			ResultSetMetaData rsmd = rs.getMetaData();
//			while ( rs.next() ) {
//				JobTableReturnItem item = new JobTableReturnItem();				
//				JobTableReturnItem.rs2Object(item, rsmd, rs);
//				resultList.add(item);
//				// resultList.add(new JobTableReturnItem(rs));
//			}
			
//			String sql2 = "select count(*) "
//					+ JobSearch.sqlFromClause;
//			
//			if (search != "") {
//				sql2 += search;
//			}
//			Statement s2 = conn.createStatement();
//			ResultSet rs2 = s2.executeQuery(sql2);
//			if(rs2.next()){
//				totalAfterFilter = rs2.getInt(1);
//		    }
//			rs.close();
//			rs0.close();
//			rs2.close();
			
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("application/json");
			
			JobTableJsonResponse jobTableJsonResponse = new JobTableJsonResponse();
			jobTableJsonResponse.setRecordsFiltered(totalAfterFilter);
			jobTableJsonResponse.setRecordsTotal(total);
			jobTableJsonResponse.setData(resultList);
			jobTableJsonResponse.setDraw(draw);
			
			String json = AppUtils.object2json(jobTableJsonResponse);
			
			
			
			
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
