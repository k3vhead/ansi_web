package com.ansi.scilla.web.servlets.invoice;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ansi.scilla.common.db.PermissionLevel;
import com.ansi.scilla.common.invoice.InvoiceStatus;
import com.ansi.scilla.common.queries.InvoiceSearch;
import com.ansi.scilla.web.actionForm.InvoiceLookupForm;
import com.ansi.scilla.web.common.AppUtils;
import com.ansi.scilla.web.common.Permission;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.response.invoice.InvoiceLookupResponse;
import com.ansi.scilla.web.response.invoice.InvoiceLookupResponseItem;
import com.ansi.scilla.web.servlets.AbstractServlet;
import com.thewebthing.commons.lang.StringUtils;

public class InvoiceLookupServlet extends AbstractServlet {

//	private final String lookupSql = "select invoice.invoice_id, sum(ticket.act_price_per_cleaning) as invoice_total, " + 
//			" count(ticket.ticket_id) as ticket_count, ticket.invoice_date, bill_to.bill_to_name, " + 
//			" concat(division.division_nbr, '-', division.division_code) as div " +
//			" from invoice, ticket, division, job, quote, address as bill_to " +
//			" where ticket.invoice_id=invoice.invoice_id " +
//			" and division.division_id=ticket.act_division_id " +
//			" and job.job_id = ticket.job_id " +
//			" and quote.quote_id=job.quote_id " +
//			" and bill_to.address_id=quote.bill_to_address_id " +
//			" group by invoice.invoice_id, ticket.invoice_date, bill_to.name, division.division_nbr, division.division_code " +
//			" order by invoice_id desc";
	
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
		/** 
		 * This parameter is passed from a link on the Print Invoice page, so it is used to filter 
		 * for unprinted invoices (Status='N') within a division 
		 **/
		String filterDivisionId = request.getParameter("divisionId");
		String filterPPCValue = request.getParameter(InvoiceLookupForm.PPC_FILTER);
		System.out.println("filterPPCValue: " + filterPPCValue);
		Boolean filterPPC = ! StringUtils.isBlank(filterPPCValue) &&  filterPPCValue.equalsIgnoreCase("yes");
		
		int amount = 10;
		int start = 0;
		int draw = 0;
		int col = 0;
		String dir = "asc";
		String[] cols = { "invoice_id", "fleetmatics_invoice_nbr", "div", "bill_to_name", 
						"ticket_count", "invoice_date", 
						"invoice_amount", "invoice_tax", "invoice_total", "invoice_paid", "invoice_balance" };
		String sStart = request.getParameter("start");
	    String sAmount = request.getParameter("length");
	    String sDraw = request.getParameter("draw");
	    String sCol = request.getParameter("order[0][column]");
	    String sdir = request.getParameter("order[0][dir]");

	   
		Connection conn = null;
		try {
			conn = AppUtils.getDBCPConn();
			AppUtils.validateSession(request, Permission.INVOICE, PermissionLevel.PERMISSION_LEVEL_IS_READ);

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
				if (col < 0 || col > 10) {
					col = 0;
				}
			}
			if (sdir != null) {
				System.out.println("sdir: " + sdir);
				if (!sdir.equals("asc")) {
					dir = "desc";
				}
			}

			String colName = cols[col];

			Integer totalFiltered = makeFilteredCount(conn, filterDivisionId, filterPPC);		    
			List<InvoiceLookupResponseItem> resultList = makeFetchData(conn, amount, start, term, filterDivisionId, filterPPC, colName, dir);
			Integer totalUnfiltered = makeUnfilteredCount(conn);


			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("application/json");

			InvoiceLookupResponse jobTableJsonResponse = new InvoiceLookupResponse();
			jobTableJsonResponse.setRecordsFiltered(totalFiltered);
			jobTableJsonResponse.setRecordsTotal(totalUnfiltered);
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

	private Integer makeUnfilteredCount(Connection conn) throws SQLException {
		Integer totalUnfiltered = null;
		String sql2 = "select count(1) from ("
				+ InvoiceSearch.sql;
		
		sql2 += ") t";
		
//	    if ( ! StringUtils.isBlank(filterDivisionId)) {
//	    	sql2 = sql2.replaceAll("inner join division on", "inner join division on division.division_id=" + filterDivisionId + " and ");
//	    	sql2 = sql2.replaceAll("group by", "where invoice.invoice_status='" + InvoiceStatus.NEW.code() + "' group by");
//	    }

		System.out.println("*****");
		System.out.println("filtered count:\n" + sql2);
		System.out.println("*****");
		Statement s2 = conn.createStatement();
		ResultSet rs2 = s2.executeQuery(sql2);
		if(rs2.next()){
			totalUnfiltered = rs2.getInt(1);
	    }
		rs2.close();
		/** End of Section **/
		
		return totalUnfiltered;
	}

	private List<InvoiceLookupResponseItem> makeFetchData(Connection conn, Integer amount, Integer start, String term, String filterDivisionId, Boolean filterPPC, String colName, String dir) throws Exception {
		System.out.println("Getting fetch data");
		System.out.println("Amount: " + amount);
		System.out.println("Start: " + start);
		System.out.println("term: " + term);
		System.out.println("filterDivisionId: " + filterDivisionId);
		System.out.println("filterPPC: " + filterPPC);
		System.out.println("colName: " + colName);
		System.out.println("dir: " + dir);
				
		
		List<InvoiceLookupResponseItem> resultList = new ArrayList<InvoiceLookupResponseItem>();
		String sql = InvoiceSearch.sql;
		String search = InvoiceSearch.generateWhereClause(term);
		
//		System.out.println("****** Search (Not executed until fetch)");
//		System.out.println(sql);
//		System.out.println("*******");
		sql += search;
//		System.out.println(sql);
//		System.out.println(sql);
		System.out.println("**** Before *****\n" + sql + "\n**** /Before ******");

		if ( filterPPC ) {			
			if ( StringUtils.isBlank(filterDivisionId)) {
				sql = "select * from (\n" + sql + "\n) invoice_search \nwhere invoice_search.invoice_balance != 0";
			} else {
				// filter on both PPC & division
		    	sql = sql.replaceAll("inner join division on", "inner join division on division.division_id=" + filterDivisionId + " and ");
		    	sql = sql.replaceAll("group by", "where invoice.invoice_status='" + InvoiceStatus.NEW.code() + "'\n group by");
				sql = "select * from (\n" + sql + "\n) invoice_search \nwhere invoice_search.invoice_balance != 0";
			}
		} else {
			if ( StringUtils.isBlank(filterDivisionId)) {
				// no filters on anything, so just go
			} else {
				// filter on division, not PPC, so this is copied from old version
		    	sql = sql.replaceAll("inner join division on", "inner join division on division.division_id=" + filterDivisionId + " and ");
		    	sql = sql.replaceAll("group by", "where invoice.invoice_status='" + InvoiceStatus.NEW.code() + "'\n group by");
			}
		}
		
		sql += "\n order by " + colName + " " + dir;

		if ( amount != -1) {
			sql += "\n OFFSET "+ start+" ROWS" + " FETCH NEXT " + amount + " ROWS ONLY";
		}
//	    if ( ! StringUtils.isBlank(filterDivisionId)) {
//	    	sql = sql.replaceAll("inner join division on", "inner join division on division.division_id=" + filterDivisionId + " and ");
//	    	sql = sql.replaceAll("group by", "where invoice.invoice_status='" + InvoiceStatus.NEW.code() + "' group by");
//	    }
		System.out.println("*****\nfetch:\n");
		System.out.println(sql);
		System.out.println("*****");
		
		Statement s = conn.createStatement();
		ResultSet rs = s.executeQuery(sql);
		while ( rs.next() ) {
			resultList.add(new InvoiceLookupResponseItem(rs));
		}
		rs.close();

		/** End of section **/

		return resultList;
	}

	private Integer makeFilteredCount(Connection conn, String filterDivisionId, Boolean filterPPC) throws SQLException {
	    String sql = InvoiceSearch.sql;
	    Integer total = null;
	    
	    
		if ( filterPPC ) {			
			if ( StringUtils.isBlank(filterDivisionId)) {
				sql = "select * from (\n" + sql + "\n) invoice_search \nwhere invoice_search.invoice_balance != 0";
			} else {
				// filter on both PPC & division
		    	sql = sql.replaceAll("inner join division on", "inner join division on division.division_id=" + filterDivisionId + " and ");
		    	sql = sql.replaceAll("group by", "where invoice.invoice_status='" + InvoiceStatus.NEW.code() + "'\n group by");
				sql = "select * from (\n" + sql + "\n) invoice_search \nwhere invoice_search.invoice_balance != 0";
			}
		} else {
			if ( StringUtils.isBlank(filterDivisionId)) {
				// no filters on anything, so just go
			} else {
				// filter on division, not PPC, so this is copied from old version
		    	sql = sql.replaceAll("inner join division on", "inner join division on division.division_id=" + filterDivisionId + " and ");
		    	sql = sql.replaceAll("group by", "where invoice.invoice_status='" + InvoiceStatus.NEW.code() + "'\n group by");
			}
		}
		
		sql = "select count(1) from ("+ sql + ") t";
	    
//	    if ( ! StringUtils.isBlank(filterDivisionId)) {
//	    	sql = sql.replaceAll("inner join division on", "inner join division on division.division_id=" + filterDivisionId + " and ");
//	    	sql = sql.replaceAll("group by", "where invoice.invoice_status='" + InvoiceStatus.NEW.code() + "' group by");
//	    }
//	    System.out.println("*****");
//		System.out.println("total count:\t" + sql);
//		System.out.println("*****");
		Statement s = conn.createStatement();
		ResultSet rs0 = s.executeQuery(sql);
		if(rs0.next()){
	        total = rs0.getInt(1);
	    }
		rs0.close();

	    return total;
	}


}
