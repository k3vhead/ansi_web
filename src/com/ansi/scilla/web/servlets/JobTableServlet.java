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

import com.ansi.scilla.common.queries.JobSearch;
import com.ansi.scilla.web.common.AppUtils;
import com.ansi.scilla.web.response.jobTable.JobTableJsonResponse;
import com.ansi.scilla.web.response.jobTable.JobTableReturnItem;

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
public class JobTableServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		int amount = 10;
		int start = 0;
		int draw = 0;
		int col = 0;
		String dir = "asc";
		String[] cols = { "job_id", "status", "bill_to_name", "job_site_name", "job_site_address", "start_date", "job_number", "frequency", "price_per_cleaning", "service_description", "po_number" };
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
					+ " from job";
			Statement s = conn.createStatement();
			ResultSet rs0 = s.executeQuery(sql);
			if(rs0.next()){
		        total = rs0.getInt(1);
		    }
			
		    int totalAfterFilter = total;
			
			List<JobTableReturnItem> resultList = new ArrayList<JobTableReturnItem>();
			sql = JobSearch.sql;

			String search = JobSearch.generateWhereClause(term);
			
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
				resultList.add(new JobTableReturnItem(rs));
			}
			
			String sql2 = "select count(*) "
					+ JobSearch.sqlFromClause;
			
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
		} catch ( Exception e ) {
			AppUtils.logException(e);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}
	}


}
