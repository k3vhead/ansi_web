package com.ansi.scilla.web.invoice.servlet;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections4.Transformer;
import org.apache.logging.log4j.Level;

import com.ansi.scilla.common.queries.InvoiceSearch;
import com.ansi.scilla.common.utils.Permission;
import com.ansi.scilla.web.common.query.LookupQuery;
import com.ansi.scilla.web.common.servlet.AbstractLookupServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionDivision;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.ColumnFilter;
import com.ansi.scilla.web.common.utils.ColumnFilter.ComparisonType;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.invoice.actionForm.InvoiceLookupForm;
import com.ansi.scilla.web.invoice.query.InvoiceDetailLookupQuery;
import com.ansi.scilla.web.invoice.query.InvoiceLookupQuery;
import com.ansi.scilla.web.invoice.response.InvoiceLookupResponse;
import com.ansi.scilla.web.invoice.response.InvoiceLookupResponseItem;
import com.thewebthing.commons.lang.StringUtils;

public class InvoiceLookupServlet extends AbstractLookupServlet {

	
	private static final long serialVersionUID = 1L;

	
	public InvoiceLookupServlet() {
		super(Permission.INVOICE_READ);
		cols = new String[] { 
				InvoiceLookupQuery.INVOICE_ID,
				InvoiceLookupQuery.FLEETMATICS_INVOICE_NBR,
				InvoiceLookupQuery.DIV,
				InvoiceLookupQuery.BILL_TO_NAME,
				InvoiceLookupQuery.TICKET_COUNT,
				InvoiceLookupQuery.INVOICE_DATE,
				InvoiceLookupQuery.INVOICE_AMOUNT,
				InvoiceLookupQuery.INVOICE_TAX,
				InvoiceLookupQuery.INVOICE_TOTAL,
				InvoiceLookupQuery.INVOICE_PAID,
				InvoiceLookupQuery.INVOICE_BALANCE,
				};
		super.itemTransformer = new ItemTransformer();
	}
	
	@Override
	public LookupQuery makeQuery(Connection conn, HttpServletRequest request) {
		HttpSession session = request.getSession();
		SessionData sessionData = (SessionData)session.getAttribute(SessionData.KEY);

		SessionUser user = sessionData.getUser();
		List<SessionDivision> divisionList = sessionData.getDivisionList();
		
		/** 
		 * This parameter is passed from a link on the Print Invoice page, so it is used to filter 
		 * for unprinted invoices (Status='N') within a division 
		 **/
		String filterDivisionId = request.getParameter("divisionId");
		String filterPPCValue = request.getParameter(InvoiceLookupForm.PPC_FILTER);
		logger.log(Level.DEBUG, "filterPPCValue: " + filterPPCValue);
		Boolean filterPPC = ! StringUtils.isBlank(filterPPCValue) &&  filterPPCValue.equalsIgnoreCase("yes");
		
		
		String searchTerm = null;
		if(request.getParameter("search[value]") != null){
			searchTerm = request.getParameter("search[value]");
		}
		
		
		InvoiceLookupQuery lookupQuery = new InvoiceLookupQuery(user.getUserId(), divisionList);
		lookupQuery.addBaseFilter(user.getUserId());
		
		if ( StringUtils.isNotBlank(filterDivisionId) && StringUtils.isNumeric(filterDivisionId)) {
			lookupQuery.addColumnFilter(new ColumnFilter("division.division_id", filterDivisionId, ComparisonType.EQUAL_NUMBER));			
		}
		if ( filterPPC ) {
			lookupQuery.addColumnFilter(new ColumnFilter(InvoiceLookupQuery.INVOICE_BALANCE, Integer.valueOf(0), ComparisonType.NOTEQUAL_NUMBER));
		}
		
		if ( searchTerm != null ) {
			lookupQuery.setSearchTerm(searchTerm);
		}

		return lookupQuery;

	}
	
	@Deprecated
	protected void doGetX(HttpServletRequest request, HttpServletResponse response)
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


	@Deprecated
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
	

	public class ItemTransformer implements Transformer<HashMap<String, Object>, HashMap<String, Object>> {
		private SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy"); 

		@Override
		public HashMap<String, Object> transform(HashMap<String, Object> arg0) {
			
			Timestamp invoiceDate = (Timestamp)arg0.get(InvoiceDetailLookupQuery.INVOICE_DATE);
			if ( invoiceDate != null ) {
				String invoiceDateDisplay = sdf.format(invoiceDate);
				arg0.put(InvoiceDetailLookupQuery.INVOICE_DATE, invoiceDateDisplay);
			}
			
			return arg0;
		}
		
	}


}
