package com.ansi.scilla.web.job.response;

import java.sql.Connection;
import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.db.Address;
import com.ansi.scilla.common.db.Job;
import com.ansi.scilla.common.db.Quote;
import com.ansi.scilla.common.db.Ticket;
import com.ansi.scilla.common.db.User;
import com.ansi.scilla.common.db.ViewTicketLog;
import com.ansi.scilla.web.address.response.AddressResponseItem;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.UserPermission;
import com.ansi.scilla.web.job.query.JobHeader;
import com.ansi.scilla.web.quote.response.QuoteDetail;
import com.ansi.scilla.web.ticket.response.TicketLogRecord;
import com.ansi.scilla.web.ticket.response.TicketRecord;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class JobDetailResponse extends ApplicationObject {

	private static final long serialVersionUID = 1L;
	
	private JobDetail job;
	private AddressResponseItem billTo;
	private AddressResponseItem jobSite;
	private TicketRecord lastRun;
	private TicketLogRecord nextDue;
	private QuoteDetail quote;
	private TicketLogRecord lastCreated;
	private List<JobHeader> jobHeaderList;
	
	public JobDetailResponse() {
		super();
	}
	
	public JobDetailResponse(Connection conn, Integer jobId, List<UserPermission> permissionList) throws RecordNotFoundException, Exception {
		this();
		Logger logger = AppUtils.getLogger();
		Job job = new Job();
		job.setJobId(jobId);
		job.selectOne(conn);
		
		User updatedBy = new User();
		try {
			updatedBy.setUserId(job.getUpdatedBy());
			updatedBy.selectOne(conn);
		} catch ( RecordNotFoundException e) {
			// not good, but not fatal
		}
		
		User addedBy = new User();
		try {
			addedBy.setUserId(job.getAddedBy());
			addedBy.selectOne(conn);
		} catch ( RecordNotFoundException e ) {
			// not good, but not fatal here either
		}

		
		this.job = new JobDetail(conn, job, addedBy, updatedBy);

		
		logger.log(Level.DEBUG, this.job.toJson());
		
		Quote quote = new Quote();
		quote.setQuoteId(job.getQuoteId());
		quote.selectOne(conn);		
		logger.log(Level.DEBUG, quote);
		this.quote = new QuoteDetail(quote);
		
		Address jobSiteAddress = new Address();
		jobSiteAddress.setAddressId(quote.getJobSiteAddressId());
		jobSiteAddress.selectOne(conn);
		this.jobSite = new AddressResponseItem(jobSiteAddress);
		
		Address billToAddress = new Address();
		billToAddress.setAddressId(quote.getBillToAddressId());
		billToAddress.selectOne(conn);
		this.billTo = new AddressResponseItem(billToAddress);
		
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
		
		this.jobHeaderList = JobHeader.getJobHeaderList(conn, quote, permissionList);	
		if ( this.jobHeaderList.size() == 1 ) {
			this.jobHeaderList.get(0).setCanDelete(false); // don't delete the only job you've got
		}
		
	}
	
	public JobDetail getJob() {
		return job;
	}
	public void setJob(JobDetail job) {
		this.job = job;
	}
	public AddressResponseItem getBillTo() {
		return billTo;
	}
	public void setBillTo(AddressResponseItem billTo) {
		this.billTo = billTo;
	}
	public AddressResponseItem getJobSite() {
		return jobSite;
	}
	public void setJobSite(AddressResponseItem jobSite) {
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

	public List<JobHeader> getJobHeaderList() {
		return jobHeaderList;
	}

	public void setJobHeaderList(List<JobHeader> jobHeaderList) {
		this.jobHeaderList = jobHeaderList;
	}



}
