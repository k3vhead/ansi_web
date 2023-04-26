package com.ansi.scilla.web.invoice.servlet;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections4.Transformer;
import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.common.jobticket.TicketStatus;
import com.ansi.scilla.common.jobticket.TicketType;
import com.ansi.scilla.common.utils.Permission;
import com.ansi.scilla.web.common.query.LookupQuery;
import com.ansi.scilla.web.common.servlet.AbstractLookupServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionDivision;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.common.utils.ColumnFilter;
import com.ansi.scilla.web.common.utils.ColumnFilter.ComparisonType;
import com.ansi.scilla.web.ticket.query.TicketLookupQuery;

/**
 * Display tickets that can be invoiced
 * @author dclewis
 *
 */
public class TicketInvoiceLookupServlet extends AbstractLookupServlet {

	private static final long serialVersionUID = 1L;

	public static final String REALM = "ticketInvoiceLookup";

	public static final String DISPLAY_START_DATE = "display_start_date";
	public static final String TICKET_TYPE_DESC = "ticket_type_desc";
	public static final String PROCESS_DATE = "process_date";
	public static final String TICKET_TYPE = "view_ticket_type";
	public static final String TICKET_STATUS = "view_ticket_status";
	public static final String TICKET_STATUS_DESC = "ticket_status_desc";
	public static final String START_DATE = "start_date";
	public static final String VIEW_LOG_START_DATE = "view_start_date";
	public static final String VIEW_START_DATE = "view_start_date";

	public TicketInvoiceLookupServlet() {
		super(Permission.TICKET_READ);
		cols = new String[] { 
				"", // this is the select column
				TicketLookupQuery.TICKET_ID,
				TicketLookupQuery.TYPE,
				TicketLookupQuery.DIV,
				TicketLookupQuery.BILL_TO,
				TicketLookupQuery.JOB_SITE,
				TicketLookupQuery.ADDRESS,
				TicketLookupQuery.VIEW_TICKET_START_DATE,
				TicketLookupQuery.FREQ,
				TicketLookupQuery.PPC,
				TicketLookupQuery.JOB,
				TicketLookupQuery.JOB_ID,
				TicketLookupQuery.SERVICE_DESCRIPTION,
				TicketLookupQuery.PROCES_DATE,
				TicketLookupQuery.AMOUNT_DUE,
		};
		super.itemTransformer = new ItemTransformer();
	}





	@Override
	public LookupQuery makeQuery(Connection conn, HttpServletRequest request) {
		HttpSession session = request.getSession();
		SessionData sessionData = (SessionData)session.getAttribute(SessionData.KEY);

		SessionUser user = sessionData.getUser();
		List<SessionDivision> divisionList = sessionData.getDivisionList();
		//			AnsiURL url = new AnsiURL(request, REALM, (String[])null);
		String searchTerm = null;
		if(request.getParameter("search[value]") != null){
			searchTerm = request.getParameter("search[value]");
		}
		LookupQuery lookupQuery = new TicketLookupQuery(user.getUserId(), divisionList);
		lookupQuery.setSearchTerm(searchTerm);
		
		ColumnFilter ticketStatusFilter = new ColumnFilter("view_ticket_log.ticket_status",TicketStatus.COMPLETED.code(),ComparisonType.EQUAL_STRING);
		ColumnFilter invoiceFilter = new ColumnFilter("ticket.invoice_id", null, ComparisonType.IS_NULL);
		
		lookupQuery.addColumnFilter(ticketStatusFilter);
		lookupQuery.addColumnFilter(invoiceFilter);
		
		return lookupQuery;

	}




	public class ItemTransformer implements Transformer<HashMap<String, Object>, HashMap<String, Object>> {

		private SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy");

		@Override
		public HashMap<String, Object> transform(HashMap<String, Object> arg0) {
			Date startDate = (Date)arg0.get(VIEW_START_DATE);
			if ( startDate != null ) {
				arg0.put(DISPLAY_START_DATE, dateFormatter.format(startDate));
			}
			Date processDate = (Date)arg0.get(PROCESS_DATE);
			if ( processDate != null ) {
				arg0.put(PROCESS_DATE, dateFormatter.format(processDate));
			}
			String ticketType = (String)arg0.get(TICKET_TYPE);
			if ( ! StringUtils.isBlank(ticketType)) {
				TicketType type = TicketType.lookup(ticketType);
				arg0.put(TICKET_TYPE_DESC, type.display());
			}
			String ticketStatus = (String)arg0.get(TICKET_STATUS);
			if ( ! StringUtils.isBlank(ticketStatus) ) {
				TicketStatus status = TicketStatus.lookup(ticketStatus);
				arg0.put(TICKET_STATUS_DESC, status.display());
			}
			return arg0;
		}

	}




}
