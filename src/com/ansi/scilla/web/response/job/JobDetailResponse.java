package com.ansi.scilla.web.response.job;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.Date;

import org.apache.commons.beanutils.PropertyUtils;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.db.Address;
import com.ansi.scilla.common.db.Job;
import com.ansi.scilla.common.db.Quote;
import com.ansi.scilla.common.db.Ticket;
import com.ansi.scilla.common.db.User;
import com.ansi.scilla.common.db.ViewTicketLog;
import com.ansi.scilla.common.jsonFormat.AnsiDateFormatter;
import com.ansi.scilla.common.utils.AppUtils;
import com.ansi.scilla.web.response.MessageResponse;
import com.ansi.scilla.web.response.address.AddressResponseRecord;
import com.ansi.scilla.web.response.ticket.TicketLogRecord;
import com.ansi.scilla.web.response.ticket.TicketRecord;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
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




	
	/**
	 * Everything from DB Job except audit fields.
	 * @author dclewis
	 *
	 */
	public class JobDetail extends ApplicationObject {

		private static final long serialVersionUID = 1L;

		
		private Date activationDate;
		private Integer billingContactId;
		private String billingNotes;
		private BigDecimal budget;
		private String buildingType;
		private Date cancelDate;
		private String cancelReason;
		private Integer contractContactId;
		private BigDecimal directLaborPct;
		private Integer divisionId;
		private String equipment;
		private Date expirationDate;
		private String expirationReason;
		private Integer floors;
		private Integer invoiceBatch;
		private String invoiceGrouping;
		private String invoiceStyle;
		private String invoiceTerms;
		private Integer jobContactId;
		private String jobFrequency;
		private Integer jobId;
		private Integer jobNbr;
		private Integer jobTypeId;
		private Date lastPriceChange;
		private Date lastReviewDate;
		private String omNotes;
		private String ourVendorNbr;
		private String paymentTerms;
		private String poNumber;
		private BigDecimal pricePerCleaning;
		private Integer quoteId;
		private Integer repeatScheduleAnnually;
		private Integer requestSpecialScheduling;
		private String serviceDescription;
		private Integer siteContact;
		private Date startDate;
		private String status;
		private Integer taxExempt;
		private String washerNotes;
		private Integer addedBy;
		private Date addedDate;
		private Integer updatedBy;
		private Date updatedDate;
		private String addedFirstName;
		private String addedLastName;
		private String addedEmail;
		private String updatedFirstName;
		private String updatedLastName;
		private String updatedEmail;
		private Boolean canActivate;
		private Boolean canCancel;
		private Boolean canReschedule;
		
		
		public JobDetail(Job job, User addedBy, User updatedBy) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
			super();
			PropertyUtils.copyProperties(this, job);
			this.addedLastName = addedBy.getLastName();
			this.addedFirstName = addedBy.getFirstName();
			this.addedEmail = addedBy.getEmail();
			this.updatedLastName = updatedBy.getLastName();
			this.updatedFirstName = updatedBy.getFirstName();
			this.updatedEmail = updatedBy.getEmail();
			this.canActivate = job.canActivate();
			this.canCancel = job.canCancel();
			this.canReschedule = job.canReschedule();
		}
		@JsonSerialize(using=AnsiDateFormatter.class)
		public Date getActivationDate() {
			return activationDate;
		}

		@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
		public void setActivationDate(Date activationDate) {
			this.activationDate = activationDate;
		}

		public Integer getBillingContactId() {
			return billingContactId;
		}

		public void setBillingContactId(Integer billingContactId) {
			this.billingContactId = billingContactId;
		}

		public String getBillingNotes() {
			return billingNotes;
		}

		public void setBillingNotes(String billingNotes) {
			this.billingNotes = billingNotes;
		}

		public BigDecimal getBudget() {
			return budget;
		}

		public void setBudget(BigDecimal budget) {
			this.budget = budget;
		}

		public String getBuildingType() {
			return buildingType;
		}

		public void setBuildingType(String buildingType) {
			this.buildingType = buildingType;
		}
		@JsonSerialize(using=AnsiDateFormatter.class)
		public Date getCancelDate() {
			return cancelDate;
		}

		@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
		public void setCancelDate(Date cancelDate) {
			this.cancelDate = cancelDate;
		}

		public String getCancelReason() {
			return cancelReason;
		}

		public void setCancelReason(String cancelReason) {
			this.cancelReason = cancelReason;
		}

		public Integer getContractContactId() {
			return contractContactId;
		}

		public void setContractContactId(Integer contractContactId) {
			this.contractContactId = contractContactId;
		}

		public BigDecimal getDirectLaborPct() {
			return directLaborPct;
		}

		public void setDirectLaborPct(BigDecimal directLaborPct) {
			this.directLaborPct = directLaborPct;
		}

		public Integer getDivisionId() {
			return divisionId;
		}

		public void setDivisionId(Integer divisionId) {
			this.divisionId = divisionId;
		}

		public String getEquipment() {
			return equipment;
		}

		public void setEquipment(String equipment) {
			this.equipment = equipment;
		}
		@JsonSerialize(using=AnsiDateFormatter.class)
		public Date getExpirationDate() {
			return expirationDate;
		}

		@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
		public void setExpirationDate(Date expirationDate) {
			this.expirationDate = expirationDate;
		}

		public String getExpirationReason() {
			return expirationReason;
		}

		public void setExpirationReason(String expirationReason) {
			this.expirationReason = expirationReason;
		}

		public Integer getFloors() {
			return floors;
		}

		public void setFloors(Integer floors) {
			this.floors = floors;
		}

		public Integer getInvoiceBatch() {
			return invoiceBatch;
		}

		public void setInvoiceBatch(Integer invoiceBatch) {
			this.invoiceBatch = invoiceBatch;
		}

		public String getInvoiceGrouping() {
			return invoiceGrouping;
		}

		public void setInvoiceGrouping(String invoiceGrouping) {
			this.invoiceGrouping = invoiceGrouping;
		}

		public String getInvoiceStyle() {
			return invoiceStyle;
		}

		public void setInvoiceStyle(String invoiceStyle) {
			this.invoiceStyle = invoiceStyle;
		}

		public String getInvoiceTerms() {
			return invoiceTerms;
		}

		public void setInvoiceTerms(String invoiceTerms) {
			this.invoiceTerms = invoiceTerms;
		}

		public Integer getJobContactId() {
			return jobContactId;
		}

		public void setJobContactId(Integer jobContactId) {
			this.jobContactId = jobContactId;
		}

		public String getJobFrequency() {
			return jobFrequency;
		}

		public void setJobFrequency(String jobFrequency) {
			this.jobFrequency = jobFrequency;
		}

		public Integer getJobId() {
			return jobId;
		}

		public void setJobId(Integer jobId) {
			this.jobId = jobId;
		}

		public Integer getJobNbr() {
			return jobNbr;
		}

		public void setJobNbr(Integer jobNbr) {
			this.jobNbr = jobNbr;
		}

		public Integer getJobTypeId() {
			return jobTypeId;
		}

		public void setJobTypeId(Integer jobTypeId) {
			this.jobTypeId = jobTypeId;
		}
		@JsonSerialize(using=AnsiDateFormatter.class)
		public Date getLastPriceChange() {
			return lastPriceChange;
		}

		@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
		public void setLastPriceChange(Date lastPriceChange) {
			this.lastPriceChange = lastPriceChange;
		}
		@JsonSerialize(using=AnsiDateFormatter.class)
		public Date getLastReviewDate() {
			return lastReviewDate;
		}

		public void setLastReviewDate(Date lastReviewDate) {
			this.lastReviewDate = lastReviewDate;
		}

		public String getOmNotes() {
			return omNotes;
		}

		public void setOmNotes(String omNotes) {
			this.omNotes = omNotes;
		}

		public String getOurVendorNbr() {
			return ourVendorNbr;
		}

		public void setOurVendorNbr(String ourVendorNbr) {
			this.ourVendorNbr = ourVendorNbr;
		}

		public String getPaymentTerms() {
			return paymentTerms;
		}

		public void setPaymentTerms(String paymentTerms) {
			this.paymentTerms = paymentTerms;
		}

		public String getPoNumber() {
			return poNumber;
		}

		public void setPoNumber(String poNumber) {
			this.poNumber = poNumber;
		}

		public BigDecimal getPricePerCleaning() {
			return pricePerCleaning;
		}

		public void setPricePerCleaning(BigDecimal pricePerCleaning) {
			this.pricePerCleaning = pricePerCleaning;
		}

		public Integer getQuoteId() {
			return quoteId;
		}

		public void setQuoteId(Integer quoteId) {
			this.quoteId = quoteId;
		}

		public Integer getRepeatScheduleAnnually() {
			return repeatScheduleAnnually;
		}

		public void setRepeatScheduleAnnually(Integer repeatScheduleAnnually) {
			this.repeatScheduleAnnually = repeatScheduleAnnually;
		}

		public Integer getRequestSpecialScheduling() {
			return requestSpecialScheduling;
		}

		public void setRequestSpecialScheduling(Integer requestSpecialScheduling) {
			this.requestSpecialScheduling = requestSpecialScheduling;
		}

		public String getServiceDescription() {
			return serviceDescription;
		}

		public void setServiceDescription(String serviceDescription) {
			this.serviceDescription = serviceDescription;
		}

		public Integer getSiteContact() {
			return siteContact;
		}

		public void setSiteContact(Integer siteContact) {
			this.siteContact = siteContact;
		}
//		@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
		@JsonSerialize(using=AnsiDateFormatter.class)
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

		public Integer getTaxExempt() {
			return taxExempt;
		}

		public void setTaxExempt(Integer taxExempt) {
			this.taxExempt = taxExempt;
		}

		public String getWasherNotes() {
			return washerNotes;
		}

		public void setWasherNotes(String washerNotes) {
			this.washerNotes = washerNotes;
		}
		public Integer getAddedBy() {
			return addedBy;
		}
		public void setAddedBy(Integer addedBy) {
			this.addedBy = addedBy;
		}
		@JsonSerialize(using=AnsiDateFormatter.class)
		public Date getAddedDate() {
			return addedDate;
		}
		@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
		public void setAddedDate(Date addedDate) {
			this.addedDate = addedDate;
		}
		public Integer getUpdatedBy() {
			return updatedBy;
		}
		public void setUpdatedBy(Integer updatedBy) {
			this.updatedBy = updatedBy;
		}
		@JsonSerialize(using=AnsiDateFormatter.class)
		public Date getUpdatedDate() {
			return updatedDate;
		}
		@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
		public void setUpdatedDate(Date updatedDate) {
			this.updatedDate = updatedDate;
		}
		public String getAddedFirstName() {
			return addedFirstName;
		}
		public void setAddedFirstName(String addedFirstName) {
			this.addedFirstName = addedFirstName;
		}
		public String getAddedLastName() {
			return addedLastName;
		}
		public void setAddedLastName(String addedLastName) {
			this.addedLastName = addedLastName;
		}
		public String getAddedEmail() {
			return addedEmail;
		}
		public void setAddedEmail(String addedEmail) {
			this.addedEmail = addedEmail;
		}
		public String getUpdatedFirstName() {
			return updatedFirstName;
		}
		public void setUpdatedFirstName(String updatedFirstName) {
			this.updatedFirstName = updatedFirstName;
		}
		public String getUpdatedLastName() {
			return updatedLastName;
		}
		public void setUpdatedLastName(String updateLastName) {
			this.updatedLastName = updateLastName;
		}
		public String getUpdatedEmail() {
			return updatedEmail;
		}
		public void setUpdatedEmail(String updatedEmail) {
			this.updatedEmail = updatedEmail;
		}
		public Boolean getCanActivate() {
			return canActivate;
		}
		public void setCanActivate(Boolean canActivate) {
			this.canActivate = canActivate;
		}
		public Boolean getCanCancel() {
			return canCancel;
		}
		public void setCanCancel(Boolean canCancel) {
			this.canCancel = canCancel;
		}
		public Boolean getCanReschedule() {
			return canReschedule;
		}
		public void setCanReschedule(Boolean canReschedule) {
			this.canReschedule = canReschedule;
		}

	}

	public class QuoteDetail extends ApplicationObject {
		private static final long serialVersionUID = 1L;
		
		private Date proposalDate;
		
		public QuoteDetail() {
			super();
		}
		public QuoteDetail(Quote quote) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
			this();
			PropertyUtils.copyProperties(this, quote);
		}
		@JsonSerialize(using=AnsiDateFormatter.class)
		public Date getProposalDate() {
			return proposalDate;
		}
		public void setProposalDate(Date proposalDate) {
			this.proposalDate = proposalDate;
		}		
	}
}
