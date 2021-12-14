package com.ansi.scilla.web.address.servlet;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;

import com.ansi.scilla.web.address.response.AddressJsonResponse;
import com.ansi.scilla.web.address.response.AddressReturnItem;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.Permission;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;

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
		String[] cols = { "address_id", "name", "address_status", "address1", "address2", "city", "county", "state", "zip" };
		String sStart = request.getParameter("start");
	    String sAmount = request.getParameter("length");
	    String sDraw = request.getParameter("draw");
	    String sCol = request.getParameter("order[0][column]");
	    String sdir = request.getParameter("order[0][dir]");
	   

		Connection conn = null;
		try {
			conn = AppUtils.getDBCPConn();
			AppUtils.validateSession(request, Permission.ADDRESS_READ);
//			String qs = request.getQueryString();

			String term = "";
			
			if(request.getParameter("search[value]") != null){
				term = request.getParameter("search[value]");
			}
			logger.log(Level.INFO, term);
			if (sStart != null) {
		        start = Integer.parseInt(sStart);
		        if (start < 0)
		            start = 0;
		    }
		    if (sAmount != null) {
		    	amount = Integer.parseInt(sAmount);
		    	logger.log(Level.DEBUG, sAmount);
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
		        if (sdir.equals("asc")) {
		            dir = "asc";
		        } else if (sdir.equals("desc")) {
		            dir = "desc";
		        }
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
			rs0.close();
			
		    int totalAfterFilter = total;
			term = term.replaceAll("'","''").toLowerCase();
			List<AddressReturnItem> resultList = new ArrayList<AddressReturnItem>();
			/*
			sql = "select a.address_id, a.name, a.address_status, a.address1, a.address2, a.city, a.county, a.state, a.zip, a.country_code, (a3.jobCount + a3.billCount) as count, "
					+ "a.invoice_style_default, a.invoice_grouping_default, a.invoice_batch_default, a.invoice_terms_default, a.our_vendor_nbr_default, a.billto_tax_exempt "
					+ "\n from address a"
					+ "\n left join (select a2.address_id, count(q1.job_site_address_id) as jobCount, count(q1.bill_to_address_id) as billCount from address a2"
					+ "\n inner join quote q1 on (a2.address_id = q1.job_site_address_id or a2.address_id = q1.bill_to_address_id) group by a2.address_id ) a3 on a.address_id = a3.address_id";
			*/
			sql = "select a.address_id, a.name, a.address_status, a.address1, a.address2, a.city, a.county, a.state, a.zip, \n" + 
					"	a.country_code, (a3.jobCount + a3.billCount) as count, \n" + 
					"	a.invoice_style_default, a.invoice_grouping_default, \n" + 
					"	a.invoice_batch_default, a.invoice_terms_default, a.our_vendor_nbr_default, count(document.document_id) as document_count\n" + 
					" from address a\n" + 
					" left outer join document on document.xref_id=a.address_id and document.xref_type='TAX_EXEMPT'\n" + 
					" left join (select a2.address_id, count(q1.job_site_address_id) as jobCount, count(q1.bill_to_address_id) as billCount from address a2\n" + 
					" inner join quote q1 on (a2.address_id = q1.job_site_address_id or a2.address_id = q1.bill_to_address_id) group by a2.address_id ) a3 on a.address_id = a3.address_id\n"; 
					
			String groupBy = "group by a.address_id, a.name, a.address_status, a.address1, a.address2, a.city, a.county, a.state, a.zip, \n" + 
					"	a.country_code, (a3.jobCount + a3.billCount), \n" + 
					"	a.invoice_style_default, a.invoice_grouping_default, \n" + 
					"	a.invoice_batch_default, a.invoice_terms_default, a.our_vendor_nbr_default";
			
			String search = "\n where lower(a.name) like '%" + term + "%'"
					+ "\n OR lower(a.address1) like '%" + term + "%'"
					+ "\n OR lower(a.address2) like '%" + term + "%'"
					+ "\n OR lower(a.city) like '%" + term + "%'"
					+ "\n OR lower(a.county) like '%" + term + "%'"
					+ "\n OR lower(a.state) like '%" + term + "%'"
					+ "\n OR lower(a.zip) like '%" + term + "%'"
					+ "\n OR lower(a.country_code) like '%" + term + "%'";
//					String group = " group by a.address_id, a.name, a.status, a.address1, a.address2, a.city, a.county, a.state, a.zip, a.country_code";
			
			
			if ( ! StringUtils.isBlank(term)) {
				sql += search ;
			}
			sql += groupBy;
			sql += " order by a." + colName + " " + dir;
			
			
			if ( amount != -1) {
				sql += " OFFSET "+ start+" ROWS"
					+ " FETCH NEXT " + amount + " ROWS ONLY";
			}
			logger.log(Level.DEBUG, sql);
			s = conn.createStatement();
			ResultSet rs = s.executeQuery(sql);
			while ( rs.next() ) {
				resultList.add(new AddressReturnItem(rs));
			}
			rs.close();
			
			
			
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
			rs2.close();
			
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("application/json");
			
			AddressJsonResponse addressJsonResponse = new AddressJsonResponse();
			addressJsonResponse.setRecordsFiltered(totalAfterFilter);
			addressJsonResponse.setRecordsTotal(total);
			addressJsonResponse.setData(resultList);
			addressJsonResponse.setDraw(draw);
			
			String json = AppUtils.object2json(addressJsonResponse);
			
					
			
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

	public class WhereClauseMaker implements Transformer<String, String> {

		@Override
		public String transform(String arg0) {			
			return "lower(" + arg0 + ") like ?";
		}
		
	}

}
