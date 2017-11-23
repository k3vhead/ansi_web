package com.ansi.scilla.web.job.response;

import java.util.Date;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.db.ViewTicketLog;
import com.fasterxml.jackson.annotation.JsonFormat;

public class JobScheduleResponseItem extends ApplicationObject implements Comparable<JobScheduleResponseItem> {

	private static final long serialVersionUID = 1L;

	public static final String JOB_ID = "job_id";
	public static final String START_DATE = "start_date";
	public static final String STATUS = "ticket_status";
	public static final String TICKET_ID = "ticket_id";
	public static final String TICKET_TYPE = "ticket_type";

	private Integer jobId;
	private Date startDate;
	private String status;
	private Integer ticketId;
	private String ticketType;
	
	public JobScheduleResponseItem() {
		super();
	}
	public JobScheduleResponseItem(ViewTicketLog ticket) {
		this();
		this.jobId = ticket.getJobId();
		this.startDate = ticket.getStartDate();
		this.status = ticket.getStatus();
		this.ticketId = ticket.getTicketId();
		this.ticketType = ticket.getTicketType();
	}
	
	public Integer getJobId() {
		return jobId;
	}
	public void setJobId(Integer jobId) {
		this.jobId = jobId;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public Date getStartDate() {
		return startDate;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Integer getTicketId() {
		return ticketId;
	}
	public void setTicketId(Integer ticketId) {
		this.ticketId = ticketId;
	}
	public String getTicketType() {
		return ticketType;
	}
	public void setTicketType(String ticketType) {
		this.ticketType = ticketType;
	}
	@Override
	public int compareTo(JobScheduleResponseItem o) {
		return this.startDate.compareTo(o.getStartDate());
	}

}
