package com.ansi.scilla.web.test.bcr;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.ansi.scilla.common.utils.AppUtils;
import com.ansi.scilla.web.bcr.query.BcrTicketLookupQuery;
import com.ansi.scilla.web.common.query.LookupQuery;
import com.ansi.scilla.web.common.servlet.AbstractAutoCompleteItem;
import com.ansi.scilla.web.common.struts.SessionDivision;
import com.ansi.scilla.web.common.utils.ColumnFilter;
import com.ansi.scilla.web.test.TesterUtils;

public class TestTicketAutoComplete {

	private final Integer userId = 5;
	private final Integer divisionId = 102;
	private final Integer workYear = 2021;
	private final String workWeeks = "48,49,50,51,52";
	private final Boolean monthlyFilter = false;
	
	
	
	private void go() throws Exception {
		Connection conn = null;
		try {
			conn = AppUtils.getDevConn();
			List<SessionDivision> divisionList = TesterUtils.makeSessionDivisionList(conn, userId);
			LookupQuery lookupQuery = new BcrTicketLookupQuery(userId, divisionList, divisionId, workYear, workWeeks, monthlyFilter);
			lookupQuery.setSearchTerm("");
			ColumnFilter cf = new ColumnFilter("ticket.ticket_id", "8");
			lookupQuery.addColumnFilter(cf);
			ResultSet rs = lookupQuery.select(conn, 0, 50);
			List<BcrTicket> values = new ArrayList<BcrTicket>();
			while ( rs.next() ) {
				values.add(new BcrTicket(rs));
			}
			rs.close();
			for ( BcrTicket x : values ) {
				System.out.println(x);
			}
		} finally {
			conn.close();
		}
	}
	
	
	public static void main(String[] args) {
		try {
			new TestTicketAutoComplete().go();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public class BcrTicket extends AbstractAutoCompleteItem {

		private static final long serialVersionUID = 1L;

		public BcrTicket(ResultSet rs) throws Exception {
			super(rs);
		}

		@Override
		protected void make(ResultSet rs) throws Exception {
			this.id = rs.getInt("ticket_id");
			this.label = rs.getInt("ticket_id") + "(" + rs.getString("service_tag_id") + ")";
			this.value = String.valueOf(rs.getInt("ticket_id"));			
		}
		
	}

}
