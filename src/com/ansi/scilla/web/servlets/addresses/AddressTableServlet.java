package com.ansi.scilla.web.servlets.addresses;

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

import com.ansi.scilla.common.db.PermissionLevel;
import com.ansi.scilla.web.common.AppUtils;
import com.ansi.scilla.web.common.Permission;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.response.address.AddressJsonResponse;
import com.ansi.scilla.web.response.address.AddressReturnItem;
import com.ansi.scilla.web.servlets.AbstractServlet;

/**
 * This url searches the following contact table fields for the search term:
 * 		name
 * 		address1
 * 		address2
 * 		city
 * 		country
 * 		state
 * 		zip
 * 		country_code
 * 
 * The url for get will be one of:
 * 		/addressSearch    						(retrieves all records from address table)
 * 		/addressSearch?term=<searchTerm>		(returns all records containing <searchTerm>)
 * 
 * The servlet will return all records if there is no "term=" is found.
 * 
 * The url for delete will return methodNotAllowed
 * 
 * The url for post will return methodNotAllowed
 * 
 *
 */
public class AddressTableServlet extends AbstractServlet {

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
		String[] cols = { "address_id", "name", "status", "address1", "address2", "city", "county", "state", "zip", "country_code" };
		String sStart = request.getParameter("start");
	    String sAmount = request.getParameter("length");
	    String sDraw = request.getParameter("draw");
	    String sCol = request.getParameter("order[0][column]");
	    String sdir = request.getParameter("order[0][dir]");
	   //System.out.println(sCol);
	   
	   //list all passed header and paramaters
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
//	   
		Connection conn = null;
		try {
			conn = AppUtils.getDBCPConn();
			AppUtils.validateSession(request, Permission.JOB, PermissionLevel.PERMISSION_LEVEL_IS_READ);
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
					+ " from address";
			Statement s = conn.createStatement();
			ResultSet rs0 = s.executeQuery(sql);
			if(rs0.next()){
		        total = rs0.getInt(1);
		    }
			
		    int totalAfterFilter = total;
			term = term.toLowerCase();
			List<AddressReturnItem> resultList = new ArrayList<AddressReturnItem>();
			sql = "select a.address_id, a.name, a.address_status, a.address1, a.address2, a.city, a.county, a.state, a.zip, a.country_code, (a3.jobCount + a3.billCount) as count"
					+ " from address a"
					+ " left join (select a2.address_id, count(q1.job_site_address_id) as jobCount, count(q1.bill_to_address_id) as billCount from address a2"
					+ " inner join quote q1 on (a2.address_id = q1.job_site_address_id or a2.address_id = q1.bill_to_address_id) group by a2.address_id ) a3 on a.address_id = a3.address_id";
			String search = " where lower(a.name) like '%" + term + "%'"
					+ " OR lower(a.address1) like '%" + term + "%'"
					+ " OR lower(a.address2) like '%" + term + "%'"
					+ " OR lower(a.city) like '%" + term + "%'"
					+ " OR lower(a.county) like '%" + term + "%'"
					+ " OR lower(a.state) like '%" + term + "%'"
					+ " OR lower(a.zip) like '%" + term + "%'"
					+ " OR lower(a.country_code) like '%" + term + "%'";
//					String group = " group by a.address_id, a.name, a.status, a.address1, a.address2, a.city, a.county, a.state, a.zip, a.country_code";
			
			
			
			sql += search ;
			sql += " order by a." + colName + " " + dir;
			
			
			if ( amount != -1) {
				sql += " OFFSET "+ start+" ROWS"
					+ " FETCH NEXT " + amount + " ROWS ONLY";
			}
			System.out.println(sql);
			s = conn.createStatement();
			ResultSet rs = s.executeQuery(sql);
			while ( rs.next() ) {
				resultList.add(new AddressReturnItem(rs));
			}
			
			
			
			String sql2 = "select count(*)"
					+ " from address a";
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
			
			AddressJsonResponse addressJsonResponse = new AddressJsonResponse();
			addressJsonResponse.setRecordsFiltered(totalAfterFilter);
			addressJsonResponse.setRecordsTotal(total);
			addressJsonResponse.setData(resultList);
			addressJsonResponse.setDraw(draw);
			
			String json = AppUtils.object2json(addressJsonResponse);
			
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
