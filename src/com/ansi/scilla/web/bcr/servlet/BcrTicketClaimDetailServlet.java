package com.ansi.scilla.web.bcr.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;

import com.ansi.scilla.web.common.query.LookupQuery;
import com.ansi.scilla.web.common.response.MessageResponse;
import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.ColumnFilter;
import com.ansi.scilla.web.ticket.response.TicketDetail;

public class BcrTicketClaimDetailServlet extends AbstractBcrTicketLookupServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void makeMyColumns() {
		cols = new String[] { 
				"job_site_name", //	Account
				"ticket_id",	//Ticket Number
				"claim_week",	// ClaimWeek
				"dl_amt", //"D/L"  
				"total_volume", //"Total Volume"  
				"volume_claimed", //"Volume Claimed" 
				"passthru_volume", // Expense
				"volume_remaining", //"Volume Remaining"  
				"notes", //"Notes"  
				"billed_amount", //"Billed Amount"  
				"claimed_vs_billed", //"Diff Clm/Bld"  
				"ticket_status", //"Ticket Status"  
				"service_tag_id", //"Service"  
				"equipment_tags", //"Equipment"  
				"employee", //"Employee"
		};
	}

	protected void processDetail(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Connection conn = null;
		try {
			conn = AppUtils.getDBCPConn();
			String[] uri = request.getRequestURI().split("/");
			String ticketParm = uri[uri.length-1];
			if ( (! StringUtils.isBlank(ticketParm) ) && StringUtils.isNumeric(ticketParm) ) {
				Integer ticketId = Integer.valueOf(ticketParm);
				LookupQuery query = super.makeQuery(conn, request);
				query.setSearchTerm("");
				ColumnFilter cf = new ColumnFilter("ticket.ticket_id", String.valueOf(ticketId));
				query.addColumnFilter(cf);
				ResultSet rs = query.select(conn, 0, 50);  // there had better only be one
				if ( rs.next() ) {
					sendDetail(conn, response, ticketId, rs);
				} else {
					super.sendNotFound(response);
				}
				rs.close();
			} else {
				super.sendNotFound(response);
			}
		} catch ( Exception e ) {
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}

	}

	private void sendDetail(Connection conn, HttpServletResponse response, Integer ticketId, ResultSet rs) throws Exception {
		List<HashMap<String, Object>> dataList = new ArrayList<HashMap<String, Object>>();

		ResultSetMetaData rsmd = rs.getMetaData();
		dataList.add(super.makeDataItem(rs, rsmd));

		if ( itemTransformer != null ) {
			CollectionUtils.transform(dataList, itemTransformer);
		}
		
		TicketDetail ticketDetail = new TicketDetail(conn, ticketId);
		ClaimDetailResponse data = new ClaimDetailResponse(ticketDetail, dataList.get(0));
		super.sendResponse(conn, response, ResponseCode.SUCCESS, data);
	}
	
	

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		logger.log(Level.DEBUG, request.getRequestURI());

		boolean errorFound = false;
		String divisionString = request.getParameter(DIVISION_ID);
		String workYearString = request.getParameter(WORK_YEAR);
		String workWeeks = request.getParameter(WORK_WEEKS);  // comma-delimited list of work weeks.
		if(StringUtils.isBlank(divisionString)) {
			errorFound = true;
		} 
		if(!StringUtils.isNumeric(divisionString)) {
			errorFound = true;
		}
		if(!StringUtils.isNumeric(workYearString)) {
			errorFound = true;
		}
		if(StringUtils.isBlank(workWeeks)) {
			errorFound = true;
		}
		if(errorFound) {
			super.sendNotFound(response);
		} else {
			processDetail(request, response);
		}	
	}


	public class ClaimDetailResponse extends MessageResponse {

		private static final long serialVersionUID = 1L;

		private TicketDetail ticketDetail;
		private HashMap<String, Object> claimDetail;

		public ClaimDetailResponse() {
			super();
		}

		public ClaimDetailResponse(TicketDetail ticketDetail, HashMap<String, Object> claimDetail) {
			this();
			this.ticketDetail = ticketDetail;
			this.claimDetail = claimDetail;
		}

		public TicketDetail getTicketDetail() {
			return ticketDetail;
		}

		public void setTicketDetail(TicketDetail ticketDetail) {
			this.ticketDetail = ticketDetail;
		}

		public HashMap<String, Object> getClaimDetail() {
			return claimDetail;
		}

		public void setClaimDetail(HashMap<String, Object> claimDetail) {
			this.claimDetail = claimDetail;
		}

		

		
		
	}

}
