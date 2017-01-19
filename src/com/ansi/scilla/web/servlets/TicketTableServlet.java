package com.ansi.scilla.web.servlets;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ansi.scilla.web.common.AppUtils;
import com.ansi.scilla.web.response.quoteTable.QuoteTableJsonResponse;
import com.ansi.scilla.web.response.quoteTable.QuoteTableReturnItem;

/**
 * This url searches the following contact table fields for the search term:
 * 		name
 * 		address1
 * 		address2
 * 		city
 * 		country
 * 		state
 * 		zip
 * 		country_cod
 * 
 * The url for get will be one of:
 * 		/addressSearch    						(retrieves all records from address table)
 * 		/addressSearch?term=<searchTerm>		(returns all records containing <searchTerm>)
 * 
 * The servlet will return all records if there is no "term=" is found.
 * 

 *
 */
public class TicketTableServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		int amount = 10;
		int start = 0;
		int draw = 0;
		int col = 0;
		String dir = "asc";
		String[] cols = { "quote_id", "quote_code", "bill_to_name", "job_site_name", "job_site_address", "manager_name", "proposal_date", "quote_job_count", "quote_ppc_sum" };
		String sStart = request.getParameter("start");
	    String sAmount = request.getParameter("length");
	    String sDraw = request.getParameter("draw");
	    String sCol = request.getParameter("order[0][column]");
	    String sdir = request.getParameter("order[0][dir]");
	   //System.out.println(sCol);
	   
	   //list all passed header and paramaters
	   /* Enumeration headerNames = request.getHeaderNames();
	   while(headerNames.hasMoreElements()) {
	     String headerName = (String)headerNames.nextElement();
	     System.out.println("Header Name - " + headerName + ", Value - " + request.getHeader(headerName));
	   }
	   Enumeration params = request.getParameterNames(); 
	   while(params.hasMoreElements()){
	    String paramName = (String)params.nextElement();
	    System.out.println("Parameter Name - "+paramName+", Value - "+request.getParameter(paramName));
	   }*/
	   
		Connection conn = null;
		try {
			conn = AppUtils.getDBCPConn();
			String qs = request.getQueryString();

			String term = "";
			
			if(request.getParameter("search[value]") != null){
				term = request.getParameter("search[value]");
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
		        if (amount != -1) {
		        	if (amount < 10 || amount > 100)
		            amount = 10;
		        }
		    }
		    if (sDraw != null) {
		        draw = Integer.parseInt(sDraw);
		    }
		    if (sCol != null) {
		        col = Integer.parseInt(sCol);
		        if (col < 0 || col > 10)
		            col = 0;
		    }
		    if (sdir != null) {
		        if (!sdir.equals("asc"))
		            dir = "desc";
		    }
		    
		    String colName = cols[col];
		    int total = 0;
		    String sql = "select count(*)"
					+ " from quote";
			Statement s = conn.createStatement();
			ResultSet rs0 = s.executeQuery(sql);
			if(rs0.next()){
		        total = rs0.getInt(1);
		    }
			
		    int totalAfterFilter = total;
			
			List<QuoteTableReturnItem> resultList = new ArrayList<QuoteTableReturnItem>();
			sql = "SELECT quote.quote_id, CONCAT(quote.quote_number,quote.revision) as quote_code, quote.proposal_date, " +
					" CONCAT(ansi_user.first_name,' ',ansi_user.last_name) as manager_name, " +
					" a1.name as bill_to_name, a2.name as job_site_name, a2.address1 as job_site_address, " +
					" jobs.job_count as quote_job_count, jobs.job_ppc_sum as quote_ppc_sum " +
					" FROM quote " +
					" JOIN address a1 ON quote.bill_to_address_id = a1.address_id " +
					" JOIN address a2 ON quote.job_site_address_id = a2.address_id " +
					" JOIN ansi_user ON ansi_user.user_id = quote.manager_ID " +
					" inner join ( " +
						" select quote.quote_id, count(job.job_id) as job_count, sum(job.price_per_cleaning) as job_ppc_sum " +
						" from quote " +
						" left outer join job on job.quote_id=quote.quote_id " +
						" group by quote.quote_id " +
						" ) jobs on quote.quote_id=jobs.quote_id";

			String search = " where CONCAT(first_name,' ',last_name) like '%" + term + "%' " +
					" OR CONCAT(last_name,' ',first_name) like '%" + term + "%' " +
					" OR CONCAT(last_name,' ',first_name) like '%" + term + "%' " +
					" OR CONCAT(quote_number,revision) like '%" + term + "%'" +
					" OR a2.name like '%" + term + "%'" +
					" OR a2.address1 like '%" + term + "%'" +
					" OR a1.name like '%" + term + "%'";
			
			
			
			System.out.println(sql);
			sql += search;
			System.out.println(sql);
			sql += " order by " + colName + " " + dir;
			System.out.println(sql);
			if ( amount != -1) {
				sql += " OFFSET "+ start+" ROWS"
					+ " FETCH NEXT " + amount + " ROWS ONLY";
			}
			System.out.println(sql);
			
			s = conn.createStatement();
			ResultSet rs = s.executeQuery(sql);
			while ( rs.next() ) {
				resultList.add(new QuoteTableReturnItem(rs));
			}
			
			
			
			String sql2 = "select count(*)"
					+ " from quote"
					+ " JOIN address a1 ON quote.bill_to_address_id = a1.address_id "
					+ " JOIN address a2 ON quote.job_site_address_id = a2.address_id "
					+ " JOIN ansi_user ON ansi_user.user_id = quote.manager_ID "
					+ " inner join ( "
						+ " select quote.quote_id, count(job.job_id) as job_count, sum(job.price_per_cleaning) as job_ppc_sum "
						+ " from quote "
						+ " left outer join job on job.quote_id=quote.quote_id "
						+ " group by quote.quote_id "
						+ " ) jobs on quote.quote_id=jobs.quote_id";

			if (search != "") {
				sql2 += search;
			}
			Statement s2 = conn.createStatement();
			ResultSet rs2 = s2.executeQuery(sql2);
			if(rs2.next()){
				totalAfterFilter = rs2.getInt(1);
		    }
			rs.close();
			rs0.close();
			rs2.close();
			
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("application/json");
			
			QuoteTableJsonResponse quoteTableJsonResponse = new QuoteTableJsonResponse();
			quoteTableJsonResponse.setRecordsFiltered(totalAfterFilter);
			quoteTableJsonResponse.setRecordsTotal(total);
			quoteTableJsonResponse.setData(resultList);
			quoteTableJsonResponse.setDraw(draw);
			
			String json = AppUtils.object2json(quoteTableJsonResponse);
			
			
			
			
			ServletOutputStream o = response.getOutputStream();
			OutputStreamWriter writer = new OutputStreamWriter(o);
			writer.write(json);
			writer.flush();
			writer.close();
		} catch ( Exception e ) {
			AppUtils.logException(e);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}
	}


}
