package com.ansi.scilla.web.payment.servlet;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ansi.scilla.common.db.PermissionLevel;
import com.ansi.scilla.common.queries.PaymentSearch;
import com.ansi.scilla.common.queries.PaymentSearchResult;
import com.ansi.scilla.web.common.AppUtils;
import com.ansi.scilla.web.common.Permission;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.payment.response.PaymentLookupResponse;
import com.ansi.scilla.web.payment.response.PaymentLookupResponseItem;
import com.ansi.scilla.web.servlets.AbstractServlet;

public class PaymentLookupServlet extends AbstractServlet {

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
		int amount = 10;
		int start = 0;
		int draw = 0;
		int col = 0;
		String dir = "asc";
		String[] cols = { "payment.payment_id", "payment.amount", "payment.payment_date", "payment.type", "payment.check_nbr", 
						"payment.check_date", "ticket_payment.ticket_id", "ticket_payment.amount", "ticket_payment.tax_amt", 
						"ticket_div", "ticket.invoice_id", "bill_to.name", "job_site.name",  "payment.note" };
		String sStart = request.getParameter("start");
	    String sAmount = request.getParameter("length");
	    String sDraw = request.getParameter("draw");
	    String sCol = request.getParameter("order[0][column]");
	    String sdir = request.getParameter("order[0][dir]");

	   
		Connection conn = null;
		try {
			conn = AppUtils.getDBCPConn();
			AppUtils.validateSession(request, Permission.PAYMENT, PermissionLevel.PERMISSION_LEVEL_IS_READ);

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
		        if (col < 0 || col > 10)
		            col = 0;
		    }
		    if (sdir != null) {
				System.out.println("sdir: " + sdir);
		        if (!sdir.equals("asc"))
		            dir = "desc";
		    }
		    
		    String colName = cols[col];
		    int total = 0;
//		    String sql = "select count(1) from ("
//					+ PaymentSearch.sql
//					+ ") t";
//			System.out.println("total count: " + sql);
//
//			Statement s = conn.createStatement();
//			ResultSet rs0 = s.executeQuery(sql);
//			if(rs0.next()){
//		        total = rs0.getInt(1);
//		    }
		    total = new PaymentSearch().getSearchCount(conn, term); 
		    int totalAfterFilter = total;
			
			List<PaymentLookupResponseItem> resultList = new ArrayList<PaymentLookupResponseItem>();
//			sql = PaymentSearch.sql;
//
//			String search = PaymentSearch.generateWhereClause(term);
//			
//			System.out.println(sql);
//			sql += search;
//			System.out.println(sql);
//			sql += " order by " + colName + " " + dir;
//			System.out.println(sql);
//			if ( amount != -1) {
//				sql += " OFFSET "+ start+" ROWS"
//					+ " FETCH NEXT " + amount + " ROWS ONLY";
//			}
//			System.out.println(sql);
//			
//			s = conn.createStatement();
//			ResultSet rs = s.executeQuery(sql);
//			while ( rs.next() ) {
//				resultList.add(new PaymentLookupResponseItem(rs));
//			}
			List<PaymentSearchResult> searchResultList = null;
			if ( amount != -1 ) {
				searchResultList = new PaymentSearch().search(conn, term, start, amount, colName, dir);
			} else {
				searchResultList = new PaymentSearch().search(conn, term);
			}
			for ( PaymentSearchResult result : searchResultList ) {
				resultList.add(new PaymentLookupResponseItem(result));
			}
			
//			String sql2 = "select count(1) from ("
//					+ PaymentSearch.sql;
//			
//			if (search != "") {
//				sql2 += search;
//			}
//			sql2 += ") t";
//			
//			System.out.println("filtered count: " + sql2);
//			Statement s2 = conn.createStatement();
//			ResultSet rs2 = s2.executeQuery(sql2);
//			if(rs2.next()){
//				totalAfterFilter = rs2.getInt(1);
//		    }
//			rs.close();
//			rs0.close();
//			rs2.close();
			
			totalAfterFilter = new PaymentSearch().getSearchCount(conn, term);
			
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("application/json");
			
			PaymentLookupResponse jobTableJsonResponse = new PaymentLookupResponse();
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
