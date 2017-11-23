package com.ansi.scilla.web.ticket.response;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.ansi.scilla.web.common.response.MessageResponse;

public class TicketPrintLookupResponse extends MessageResponse {

	private static final long serialVersionUID = 1L;

	private final String sql = "select division.division_id, \n" 
    + " concat(division.division_nbr, '-',  \n"
    + " division.division_code) as div,  \n"
    + " sum(ticket_count) as ticket_count,  \n"
    + " sum(price_per_cleaning) as total_ppc \n"
    + " from division  \n"
    + " left outer join \n"
    + " (select ticket.act_division_id, count(*) as ticket_count, sum(job.price_per_cleaning) as price_per_cleaning \n"
    + " from ticket  \n"
    + " inner join job on job.job_id=ticket.job_id \n"
    + " where ticket.ticket_status='N' and ticket.start_date<=? \n"
    + " group by ticket.act_division_id) as xyz on act_division_id=division.division_id \n"
    + " group by division.division_id, division.division_nbr, division.division_code \n"
    + " order by div";

	private List<TicketPrintLookupResponseItem> ticketList;
	
	public TicketPrintLookupResponse() {
		super();
	}
	
	public TicketPrintLookupResponse(Connection conn, Calendar endDate) throws Exception {
		this();
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setDate(1, new java.sql.Date(endDate.getTimeInMillis()));
		ResultSet rs = ps.executeQuery();
		ResultSetMetaData rsmd = rs.getMetaData();
		this.ticketList = new ArrayList<TicketPrintLookupResponseItem>();
		while ( rs.next() ) {
			this.ticketList.add(new TicketPrintLookupResponseItem(rsmd, rs));
		}
		rs.close();
	}

	public List<TicketPrintLookupResponseItem> getTicketList() {
		return ticketList;
	}

	public void setTicketList(List<TicketPrintLookupResponseItem> ticketList) {
		this.ticketList = ticketList;
	}


}
