package com.ansi.scilla.web.response.job;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;

import com.ansi.scilla.common.AnsiTime;
import com.ansi.scilla.common.db.ViewTicketLog;
import com.ansi.scilla.common.jobticket.TicketType;
import com.ansi.scilla.web.response.MessageResponse;

public class JobScheduleResponse extends MessageResponse {

	private static final long serialVersionUID = 1L;
	
	private List<JobScheduleResponseItem> ticketList;
	
	public JobScheduleResponse() {
		super();
	}
	
	public JobScheduleResponse(Connection conn, Integer jobId) throws Exception {
		this(conn, jobId, Calendar.getInstance());
	}
	
	public JobScheduleResponse(Connection conn, Integer jobId, Calendar startDate) throws Exception {
		this();
		make(conn, jobId, startDate);
	}

	public JobScheduleResponse(Connection conn, Integer jobId, Date startDate) throws Exception {
		this();
		Calendar start = Calendar.getInstance(new AnsiTime());
		start.setTime(startDate);
		make(conn, jobId, start);
	}
	
	public List<JobScheduleResponseItem> getTicketList() {
		return ticketList;
	}

	public void setTicketList(List<JobScheduleResponseItem> ticketList) {
		this.ticketList = ticketList;
	}

	@SuppressWarnings("unchecked")
	private void make(Connection conn, Integer jobId, Calendar startDate) throws Exception {
		String sql = "select  * " 
				+ "\nfrom view_ticket_log "
				+ "\nwhere job_id=? "
				+ "\n\tand start_date!<? "
				+ "\n\tand ticket_type in ('" + TicketType.JOB.code() +"','" + TicketType.RUN.code() + "') "
				+ "\norder by start_date ";
		startDate.set(Calendar.HOUR_OF_DAY, 0);
		startDate.set(Calendar.MINUTE, 0);
		startDate.set(Calendar.SECOND, 0);
		startDate.set(Calendar.MILLISECOND, 0);
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setInt(1, jobId);
		ps.setDate(2, new java.sql.Date(startDate.getTimeInMillis()));
	
		ResultSet rs = ps.executeQuery();
		ResultSetMetaData rsmd = rs.getMetaData();
		List<ViewTicketLog> ticketList = new ArrayList<ViewTicketLog>();
		while ( rs.next() ) {
			ticketList.add( new ViewTicketLog(rsmd, rs));
		}
		rs.close();
		this.ticketList = (List<JobScheduleResponseItem>)CollectionUtils.collect(ticketList, new TicketTransformer());
		Collections.sort(this.ticketList);
	}
	



	public class TicketTransformer implements Transformer {

		@Override
		public Object transform(Object arg0) {
			ViewTicketLog viewTicketLog = (ViewTicketLog)arg0;
			return new JobScheduleResponseItem(viewTicketLog);
		}
		
	}

}
