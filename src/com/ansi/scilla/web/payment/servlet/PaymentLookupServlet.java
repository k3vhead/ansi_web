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

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;

import com.ansi.scilla.common.db.PermissionLevel;
import com.ansi.scilla.common.queries.PaymentSearch;
import com.ansi.scilla.common.queries.PaymentSearchResult;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.Permission;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.payment.response.PaymentLookupResponse;
import com.ansi.scilla.web.payment.response.PaymentLookupResponseItem;

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

	    logger.log(Level.INFO, "sStart: " + sStart );
	    logger.log(Level.INFO, "sAmount: " +  sAmount );
	    logger.log(Level.INFO, "sDraw: " +  sDraw );
	    logger.log(Level.INFO, "sCol: " +  sCol );
	    logger.log(Level.INFO, "sdir: " +  sdir );
	    
	    
		Connection conn = null;
		try {
			conn = AppUtils.getDBCPConn();
			SessionData sessionData = AppUtils.validateSession(request, Permission.PAYMENT, PermissionLevel.PERMISSION_LEVEL_IS_READ);
			SessionUser user = sessionData.getUser();

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
		        if (col < 0 || col > (cols.length-1))
		            col = 0;
		    }
		    if (sdir != null) {
		    	logger.log(Level.DEBUG, "sdir: " + sdir);
		        if (sdir.equals("asc")) {
		            dir = "asc";
		        } else if (sdir.equals("desc")) {
		            dir = "desc";
		        }
		    }
		    
		    String colName = cols[col];
		    
		    logger.log(Level.INFO, "term: " + term);
		    logger.log(Level.INFO, "colName: " + colName);
		    logger.log(Level.INFO, "dir: " + dir);
		    
		    PaymentSearch paymentSearch = new PaymentSearch(user.getUserId());
		    int total = paymentSearch.selectCountAll(conn);
		    if ( ! StringUtils.isBlank(term)) {
		    	paymentSearch.setSearchTerm(term);
		    }
		    paymentSearch.setSortBy(colName);
		    paymentSearch.setSortIsAscending(dir.equals("asc"));
//		    int total = 0;
//		    total = new PaymentSearch().getSearchCount(conn, term); 
		    int totalAfterFilter = total;
			
			List<PaymentLookupResponseItem> resultList = new ArrayList<PaymentLookupResponseItem>();

			List<PaymentSearchResult> searchResultList = null;
//			if ( amount != -1 ) {
//				searchResultList = new PaymentSearch().search(conn, term, start, amount, colName, dir);
//			} else {
//				searchResultList = new PaymentSearch().search(conn, term);
//			}
			Integer rowCount = amount == -1 ? total : amount;
			searchResultList = paymentSearch.select(conn, start, rowCount);
			for ( PaymentSearchResult result : searchResultList ) {
				resultList.add(new PaymentLookupResponseItem(result));
			}
			


//			totalAfterFilter = new PaymentSearch().getSearchCount(conn, term);
			totalAfterFilter = paymentSearch.selectCount(conn);
			
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
