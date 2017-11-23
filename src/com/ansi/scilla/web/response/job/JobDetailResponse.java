package com.ansi.scilla.web.response.job;

import java.sql.Connection;

import com.ansi.scilla.common.db.Address;
import com.ansi.scilla.common.db.Job;
import com.ansi.scilla.common.db.Quote;
import com.ansi.scilla.common.db.Ticket;
import com.ansi.scilla.common.db.User;
import com.ansi.scilla.common.db.ViewTicketLog;
import com.ansi.scilla.common.utils.AppUtils;
import com.ansi.scilla.web.address.response.AddressResponseRecord;
import com.ansi.scilla.web.response.MessageResponse;
import com.ansi.scilla.web.response.ticket.TicketLogRecord;
import com.ansi.scilla.web.response.ticket.TicketRecord;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class JobDetailResponse extends MessageResponse {

	private static final long serialVersionUID = 1L;
	
	private JobDetail job;
	private AddressResponseRecord billTo;
	private AddressResponseRecord jobSite;
	private TicketRecord lastRun;
	private TicketLogRecord nextDue;
	private QuoteDetail quote;
	private TicketLogRecord lastCreated;
	
	public JobDetailResponse() {
		super();
	}
	
	public JobDetailResponse(Connection conn, Integer jobId) throws RecordNotFoundException, Exception {
		this();
		Job job = new Job();
		job.setJobId(jobId);
		job.selectOne(conn);
		
		User updatedBy = new User();
		updatedBy.setUserId(job.getUpdatedBy());
		updatedBy.selectOne(conn);
		
		User addedBy = new User();
		addedBy.setUserId(job.getAddedBy());
		addedBy.selectOne(conn);

		this.job = new JobDetail(job, addedBy, updatedBy);

		
		System.out.println("JobDetailResponse 44");
		System.out.println(this.job.toJson());
		
		Quote quote = new Quote();
		quote.setQuoteId(job.getQuoteId());
		quote.selectOne(conn);		
		System.out.println("JobDetailResponse 49");
		System.out.println(quote);
		this.quote = new QuoteDetail(quote);
		
		Address jobSiteAddress = new Address();
		jobSiteAddress.setAddressId(quote.getJobSiteAddressId());
		jobSiteAddress.selectOne(conn);
		this.jobSite = new AddressResponseRecord(jobSiteAddress);
		
		Address billToAddress = new Address();
		billToAddress.setAddressId(quote.getBillToAddressId());
		billToAddress.selectOne(conn);
		this.billTo = new AddressResponseRecord(billToAddress);
		
		try {
			Ticket lastRunTicket = AppUtils.getLastRunTicket(conn, jobId);
			this.lastRun = new TicketRecord(lastRunTicket);
		} catch ( RecordNotFoundException e ) {
			// this is OK, just means job has never run
			this.lastRun = new TicketRecord();
		}
		
		try {
			ViewTicketLog nextDueTicket = AppUtils.getNextDueTicketLog(conn, jobId);
			this.nextDue = new TicketLogRecord(nextDueTicket);
		} catch ( RecordNotFoundException e) {
			// this is OK, just means job is not scheduled to run again
			this.nextDue = new TicketLogRecord();
		}
		
		try {
			ViewTicketLog lastCreatedTicket = AppUtils.getLastCreatedTicketLog(conn, jobId);
			this.lastCreated = new TicketLogRecord(lastCreatedTicket);
		} catch ( RecordNotFoundException e) {
			this.lastCreated = new TicketLogRecord();
		}
		
	}
	
	public JobDetail getJob() {
		return job;
	}
	public void setJob(JobDetail job) {
		this.job = job;
	}
	public AddressResponseRecord getBillTo() {
		return billTo;
	}
	public void setBillTo(AddressResponseRecord billTo) {
		this.billTo = billTo;
	}
	public AddressResponseRecord getJobSite() {
		return jobSite;
	}
	public void setJobSite(AddressResponseRecord jobSite) {
		this.jobSite = jobSite;
	}
	public TicketRecord getLastRun() {
		return lastRun;
	}
	public void setLastRun(TicketRecord lastRun) {
		this.lastRun = lastRun;
	}
	public TicketLogRecord getNextDue() {
		return nextDue;
	}
	public void setNextDue(TicketLogRecord nextDue) {
		this.nextDue = nextDue;
	}




	public QuoteDetail getQuote() {
		return quote;
	}

	public void setQuote(QuoteDetail quote) {
		this.quote = quote;
	}




	public TicketLogRecord getLastCreated() {
		return lastCreated;
	}

	public void setLastCreated(TicketLogRecord lastCreated) {
		this.lastCreated = lastCreated;
	}



}
