package com.ansi.scilla.web.invoice.servlet;

import java.sql.Connection;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections4.Transformer;
import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.common.jobticket.TicketStatus;
import com.ansi.scilla.common.jobticket.TicketType;
import com.ansi.scilla.web.common.query.LookupQuery;
import com.ansi.scilla.web.common.servlet.AbstractLookupServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionDivision;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.common.utils.AnsiURL;
import com.ansi.scilla.web.common.utils.Permission;
import com.ansi.scilla.web.exceptions.ResourceNotFoundException;
import com.ansi.scilla.web.invoice.query.InvoiceDetailLookupQuery;

public class InvoiceDetailLookupServlet extends AbstractLookupServlet {

	private static final long serialVersionUID = 1L;
	
	public static final String REALM = "invoiceDetailLookup";
	
	public static final String COMPLETED_DATE = "completed_date";
	public static final String TICKET_STATUS_DISPLAY = "ticket_status_display";
	public static final String TICKET_TYPE_DISPLAY = "ticket_type_display";
	
	
	public InvoiceDetailLookupServlet() {
		super(Permission.INVOICE_READ);
		cols = new String[] { 
				InvoiceDetailLookupQuery.INVOICE_ID,
				InvoiceDetailLookupQuery.DIV,
				InvoiceDetailLookupQuery.BILL_TO_NAME,
				InvoiceDetailLookupQuery.INVOICE_DATE,
				InvoiceDetailLookupQuery.INVOICE_PPC,
				InvoiceDetailLookupQuery.INVOICE_TAX,
				InvoiceDetailLookupQuery.JOB_SITE_NAME,
				InvoiceDetailLookupQuery.JOB_SITE_ADDRESS,
				InvoiceDetailLookupQuery.TICKET_ID,
				InvoiceDetailLookupQuery.TICKET_TYPE,
				InvoiceDetailLookupQuery.TICKET_STATUS,
				InvoiceDetailLookupQuery.COMPLETED_DATE,
				InvoiceDetailLookupQuery.PPC,
				InvoiceDetailLookupQuery.TAXES,
				InvoiceDetailLookupQuery.TOTAL,
				InvoiceDetailLookupQuery.PAID,
				InvoiceDetailLookupQuery.DUE,

				};
		super.itemTransformer = new ItemTransformer();
	}


	


	@Override
	public LookupQuery makeQuery(Connection conn, HttpServletRequest request) {
		HttpSession session = request.getSession();
		SessionData sessionData = (SessionData)session.getAttribute(SessionData.KEY);
		
		SessionUser user = sessionData.getUser();
		List<SessionDivision> divisionList = sessionData.getDivisionList();
		try {
			AnsiURL url = new AnsiURL(request, REALM, (String[])null);
			String searchTerm = null;
			if(request.getParameter("search[value]") != null){
				searchTerm = request.getParameter("search[value]");
			}
//			Integer jobIdFilterValue = null;
//			Integer ticketIdFilterValue = null;
//			Integer divisionIdFilterValue = url.getId();
//			Integer washerIdFilterValue = null;
			InvoiceDetailLookupQuery lookupQuery = new InvoiceDetailLookupQuery(user.getUserId(), divisionList);
//			lookupQuery.setDivisionId(divisionIdFilterValue);
			if ( searchTerm != null ) {
				lookupQuery.setSearchTerm(searchTerm);
			}
//			if (! StringUtils.isBlank(request.getParameter("jobId"))) {
//				jobIdFilterValue = Integer.valueOf(request.getParameter("jobId"));
//				lookupQuery.setJobId(jobIdFilterValue);
//			}
//			if (! StringUtils.isBlank(request.getParameter("ticketId"))) {
//				ticketIdFilterValue = Integer.valueOf(request.getParameter("ticketId"));
//				lookupQuery.setTicketId(ticketIdFilterValue);
//			}
//			if (! StringUtils.isBlank(request.getParameter("washerId"))) {
//				washerIdFilterValue = Integer.valueOf(request.getParameter("washerId"));
//				lookupQuery.setWasherId(washerIdFilterValue);
//			}
			
			return lookupQuery;
			
		} catch (ResourceNotFoundException e) { 
			// parse exception is thrown by SimpleDateFormat
			throw new RuntimeException(e);
		}
	}




	public class ItemTransformer implements Transformer<HashMap<String, Object>, HashMap<String, Object>> {
		private SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy"); 

		@Override
		public HashMap<String, Object> transform(HashMap<String, Object> arg0) {
			
			Timestamp completedDate = (Timestamp)arg0.get(COMPLETED_DATE);
			if ( completedDate != null ) {
				String completedDateDisplay = sdf.format(completedDate);
				arg0.put(COMPLETED_DATE, completedDateDisplay);
			}
			
			Timestamp invoiceDate = (Timestamp)arg0.get(InvoiceDetailLookupQuery.INVOICE_DATE);
			if ( invoiceDate != null ) {
				String invoiceDateDisplay = sdf.format(invoiceDate);
				arg0.put(InvoiceDetailLookupQuery.INVOICE_DATE, invoiceDateDisplay);
			}
			
			String ticketStatusString = (String)arg0.get(InvoiceDetailLookupQuery.TICKET_STATUS);
			if ( ! StringUtils.isBlank(ticketStatusString) ) {
				TicketStatus ticketStatus = TicketStatus.lookup(ticketStatusString);
				arg0.put(TICKET_STATUS_DISPLAY, ticketStatus.display());
			}
			
			String ticketTypeString = (String)arg0.get(InvoiceDetailLookupQuery.TICKET_TYPE);
			if ( ! StringUtils.isBlank(ticketTypeString) ) {
				TicketType ticketType = TicketType.lookup(ticketTypeString);
				arg0.put(TICKET_TYPE_DISPLAY, ticketType.display());
			}
			
			return arg0;
		}
		
	}


	
	
}
