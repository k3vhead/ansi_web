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

import com.ansi.scilla.common.db.PermissionLevel;
import com.ansi.scilla.common.queries.InvoiceSearch;
import com.ansi.scilla.web.actionForm.InvoiceLookupForm;
import com.ansi.scilla.web.common.AppUtils;
import com.ansi.scilla.web.common.Permission;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.invoice.response.InvoiceLookupResponse;
import com.ansi.scilla.web.invoice.response.InvoiceLookupResponseItem;
import com.ansi.scilla.web.servlets.AbstractServlet;
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

			Integer totalFiltered = InvoiceSearch.makeFilteredCount(conn, term, filterDivisionId, filterPPC);		    
			List<InvoiceLookupResponseItem> resultList = makeFetchData(conn, amount, start, term, filterDivisionId, filterPPC, colName, dir);
			Integer totalUnfiltered = InvoiceSearch.makeUnfilteredCount(conn);


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



	private List<InvoiceLookupResponseItem> makeFetchData(Connection conn, Integer amount, Integer start, String term, String filterDivisionId, Boolean filterPPC, String colName, String dir) throws Exception {
		System.out.println("Getting fetch data");
		System.out.println("Amount: " + amount);
		System.out.println("Start: " + start);
		System.out.println("term: " + term);
		System.out.println("filterDivisionId: " + filterDivisionId);
		System.out.println("filterPPC: " + filterPPC);
		System.out.println("colName: " + colName);
		System.out.println("dir: " + dir);
				
		List<InvoiceSearch> invoiceSearchList = InvoiceSearch.makeFetchData(conn, amount, start, term, filterDivisionId, filterPPC, colName, dir);
		List<InvoiceLookupResponseItem> resultList = new ArrayList<InvoiceLookupResponseItem>();
		for ( InvoiceSearch invoiceSearch : invoiceSearchList ) {
			resultList.add(new InvoiceLookupResponseItem(invoiceSearch));
		}
		return resultList;
		
	}
	



}
