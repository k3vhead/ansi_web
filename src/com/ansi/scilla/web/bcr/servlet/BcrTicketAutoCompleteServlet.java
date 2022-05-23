package com.ansi.scilla.web.bcr.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ansi.scilla.common.utils.Permission;
import com.ansi.scilla.web.bcr.query.BcrTicketLookupQuery;
import com.ansi.scilla.web.common.query.LookupQuery;
import com.ansi.scilla.web.common.servlet.AbstractAutoCompleteItem;
import com.ansi.scilla.web.common.servlet.AbstractAutoCompleteServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionDivision;
import com.ansi.scilla.web.common.utils.ColumnFilter;

public class BcrTicketAutoCompleteServlet extends AbstractAutoCompleteServlet {

	private static final long serialVersionUID = 1L;

	public BcrTicketAutoCompleteServlet() {
		super(Permission.CLAIMS_READ);
	}
	
	
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		super.doGet(request, response);
	}



	@Override
	protected List<AbstractAutoCompleteItem> makeResultList(Connection conn, HttpServletRequest request) throws Exception {

		SessionData sessionData = (SessionData)request.getSession().getAttribute(SessionData.KEY);
		Integer userId = sessionData.getUser().getUserId();
		Integer divisionId = Integer.valueOf(request.getParameter("divisionId"));
		Integer workYear = Integer.valueOf(request.getParameter("workYear"));
		String workWeeks = request.getParameter("workWeeks");
		String term = request.getParameter("term").toLowerCase();
		Boolean monthlyFilter = false;
		
		List<SessionDivision> divisionList = sessionData.getDivisionList();
		
		List<AbstractAutoCompleteItem> itemList = new ArrayList<AbstractAutoCompleteItem>();
		
		LookupQuery lookupQuery = new BcrTicketLookupQuery(userId, divisionList, divisionId, workYear, workWeeks, monthlyFilter);
		lookupQuery.setSearchTerm("");
		ColumnFilter cf = new ColumnFilter("ticket.ticket_id", term);
		lookupQuery.addColumnFilter(cf);
		ResultSet rs = lookupQuery.select(conn, 0, 50);
		while ( rs.next() ) {
			itemList.add(new BcrTicket(rs));
		}
		rs.close();
		
		/**
		 * We sort this way because the database won't let us sort in the query:
		 * com.microsoft.sqlserver.jdbc.SQLServerException: ORDER BY items must appear in the select list if SELECT DISTINCT is specified
		 */
		Collections.sort(itemList, new Comparator<AbstractAutoCompleteItem>() {
			public int compare(AbstractAutoCompleteItem o1, AbstractAutoCompleteItem o2) {				
				return o1.getValue().compareTo(o2.getValue());
			}
		});
		
		
		return itemList;
	}

	
	
	public class BcrTicket extends AbstractAutoCompleteItem {

		private static final long serialVersionUID = 1L;

		public BcrTicket(ResultSet rs) throws Exception {
			super(rs);
		}

		@Override
		protected void make(ResultSet rs) throws Exception {
			this.id = rs.getInt("ticket_id");
			this.label = rs.getInt("ticket_id") + " (" + rs.getString("service_tag_id") + ") " 
					+ rs.getString("job_site_name") + ":TV " + rs.getBigDecimal("total_volume") 
					+ ":VR " + rs.getBigDecimal("volume_remaining");
			this.value = String.valueOf(rs.getInt("ticket_id"));			
		}
		
	}
}
