package com.ansi.scilla.web.invoice.servlet;

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

import com.ansi.scilla.common.queries.InvoiceSearch;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.common.utils.Permission;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.invoice.actionForm.InvoiceLookupForm;
import com.ansi.scilla.web.invoice.response.InvoiceLookupResponse;
import com.ansi.scilla.web.invoice.response.InvoiceLookupResponseItem;
import com.thewebthing.commons.lang.StringUtils;

public class InvoiceLookupServlet extends AbstractServlet {

	
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
		logger.log(Level.DEBUG, "filterPPCValue: " + filterPPCValue);
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
			SessionData sessionData = AppUtils.validateSession(request, Permission.INVOICE_READ);
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
				if (col < 0 || col > 10) {
					col = 0;
				}
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

			logger.log(Level.DEBUG, "InvoiceLookupServlet 114");
			logger.log(Level.DEBUG, term);
			logger.log(Level.DEBUG, filterDivisionId);
			logger.log(Level.DEBUG, filterPPC);
			Integer totalFiltered = InvoiceSearch.makeFilteredCount(conn, user.getUserId(), term, filterDivisionId, filterPPC);		    
			List<InvoiceLookupResponseItem> resultList = makeFetchData(conn, user.getUserId(), amount, start, term, filterDivisionId, filterPPC, colName, dir);
			Integer totalUnfiltered = InvoiceSearch.makeUnfilteredCount(conn, user.getUserId());


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



	private List<InvoiceLookupResponseItem> makeFetchData(Connection conn, Integer userId, Integer amount, Integer start, String term, String filterDivisionId, Boolean filterPPC, String colName, String dir) throws Exception {
		logger.log(Level.DEBUG, "Getting fetch data");
		logger.log(Level.DEBUG, "Amount: " + amount);
		logger.log(Level.DEBUG, "Start: " + start);
		logger.log(Level.DEBUG, "term: " + term);
		logger.log(Level.DEBUG, "filterDivisionId: " + filterDivisionId);
		logger.log(Level.DEBUG, "filterPPC: " + filterPPC);
		logger.log(Level.DEBUG, "colName: " + colName);
		logger.log(Level.DEBUG, "dir: " + dir);
				
		List<InvoiceSearch> invoiceSearchList = InvoiceSearch.makeFetchData(conn, userId, amount, start, term, filterDivisionId, filterPPC, colName, dir);
		List<InvoiceLookupResponseItem> resultList = new ArrayList<InvoiceLookupResponseItem>();
		for ( InvoiceSearch invoiceSearch : invoiceSearchList ) {
			resultList.add(new InvoiceLookupResponseItem(invoiceSearch));
		}
		return resultList;
		
	}
	



}
