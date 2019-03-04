package com.ansi.scilla.web.job.response;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.ansi.scilla.common.jsonFormat.AnsiCurrencyFormatter;
import com.ansi.scilla.common.queries.JobSearch;
import com.ansi.scilla.common.queries.ReportQuery;
import com.ansi.scilla.common.queries.JobSearch.JobSearchContact;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.thewebthing.commons.db2.DBColumn;

public class JobTableReturnItem extends ReportQuery {
	private static final long serialVersionUID = 1L;

	public static final String JOB_ID = "job_id";
	public static final String JOB_STATUS = "job_status";
	public static final String BILL_TO_NAME = "bill_to_name";
	public static final String JOB_SITE_NAME = "job_site_name";
	public static final String JOB_SITE_ADDRESS = "job_site_address";
	public static final String JOB_SITE_CITY = "job_site_city";
	public static final String JOB_SITE_STATE = "job_site_state";
	public static final String START_DATE = "start_date";
	public static final String JOB_NBR = "job_nbr";
	public static final String JOB_FREQUENCY = "job_frequency";
	public static final String PRICE_PER_CLEANING = "price_per_cleaning";
	public static final String SERVICE_DESCRIPTION = "service_description";
	public static final String PO_NUMBER = "po_number";
	public static final String BILL_TO_ADDRESS_ID = "bill_to_address_id";
	public static final String JOB_SITE_ADDRESS_ID = "job_site_address_id";
	public static final String DIVISION_ID = "division_id";
	public static final String DIVISION_NBR = "division_nbr";
	public static final String DIVISION_CODE = "division_code";
	public static final String QUOTE_ID = "quote_id";
	public static final String QUOTE_NUMBER = "quote_number";
	public static final String REVISION = "revision";

	public static final String JOB_CONTACT = "job_contact";
	public static final String SITE_CONTACT = "site_contact";
	public static final String CONTRACT_CONTACT = "contract_contact";
	public static final String BILLING_CONTACT = "billing_contact";

	public static final String PROPOSAL_DATE = "proposal_date";
	public static final String ACTIVATION_DATE = "activation_date";
	public static final String CANCEL_DATE = "cancel_date";
	public static final String CANCEL_REASON = "cancel_reason";

	private Integer jobId;
	private String jobStatus;
	private String billToName;
	private String jobSiteName;
	private String jobSiteAddress;
	private Date startDate;
	private Integer jobNbr;
	private String jobFrequency;
	private BigDecimal pricePerCleaning;
	private String serviceDescription;
	private String poNumber;
	private String DT_RowId;
	private Integer billToAddressId;
	private Integer jobSiteAddressId;
	private Integer divisionId;
	private Integer divisionNbr;
	private String divisionCode;
	private Integer quoteId;
	private Integer quoteNumber;
	private String revision;
	private String jobSiteCity;
	private String jobSiteState;

	private JobSearchContact jobContact;
	private JobSearchContact siteContact;
	private JobSearchContact contractContact;
	private JobSearchContact billingContact;

	private Date proposalDate;
	private Date activationDate;
	private Date cancelDate;
	private String cancelReason;

	public JobTableReturnItem() throws SQLException {
		super();
	}


	public JobTableReturnItem(JobSearch jobSearch) throws SQLException {
		this();
		this.jobId = jobSearch.getJob().getJobId();
		this.jobStatus = jobSearch.getJob().getStatus();
		this.billToName = jobSearch.getBillToName();
		this.jobSiteName = jobSearch.getJobSiteName();
		this.jobSiteCity = jobSearch.getJobSiteCity();
		this.jobSiteState = jobSearch.getJobSiteState();
		this.jobSiteAddress = jobSearch.getJobSiteAddress();
		this.startDate = jobSearch.getJob().getStartDate();
		this.jobFrequency = jobSearch.getJob().getJobFrequency();
		this.pricePerCleaning = jobSearch.getJob().getPricePerCleaning();
		this.jobNbr = jobSearch.getJob().getJobNbr();
		this.serviceDescription = jobSearch.getJob().getServiceDescription();
		this.poNumber = jobSearch.getJob().getPoNumber();
		this.billToAddressId = jobSearch.getQuote().getBillToAddressId();
		this.jobSiteAddressId = jobSearch.getQuote().getJobSiteAddressId();
		this.divisionId = jobSearch.getDivision().getDivisionId();
		this.divisionNbr = jobSearch.getDivision().getDivisionNbr();
		this.divisionCode = jobSearch.getDivision().getDivisionCode();
		this.DT_RowId = jobSearch.getJob().getJobId() + "";
		this.quoteId = jobSearch.getQuote().getQuoteId();
		this.quoteNumber = jobSearch.getQuote().getQuoteNumber();
		this.revision = jobSearch.getQuote().getRevision();

		this.jobContact = jobSearch.getJobContact();
		this.contractContact = jobSearch.getContractContact();
		this.billingContact = jobSearch.getBillingContact();
		this.siteContact = jobSearch.getSiteContact();

		this.proposalDate = jobSearch.getQuote().getProposalDate();
		this.activationDate = jobSearch.getJob().getActivationDate();
		this.cancelDate = jobSearch.getJob().getCancelDate();
		this.cancelReason = jobSearch.getJob().getCancelReason();
	
	}

	@DBColumn(JOB_ID)
	public Integer getJobId() {
		return jobId;
	}

	@DBColumn(JOB_ID)
	public void setJobId(Integer jobId) {
		this.jobId = jobId;
	}

	public String getDT_RowId() {
		return DT_RowId;
	}

	public void setDT_RowId(String DT_RowId) {
		this.DT_RowId = DT_RowId;
	}

	@DBColumn(JOB_STATUS)
	public String getJobStatus() {
		return jobStatus;
	}

	@DBColumn(JOB_STATUS)
	public void setJobStatus(String jobStatus) {
		this.jobStatus = jobStatus;
	}

	@DBColumn(BILL_TO_NAME)
	public String getBillToName() {
		return billToName;
	}

	@DBColumn(BILL_TO_NAME)
	public void setBillToName(String billToName) {
		this.billToName = billToName;
	}

	@DBColumn(JOB_SITE_NAME)
	public String getJobSiteName() {
		return jobSiteName;
	}

	@DBColumn(JOB_SITE_NAME)
	public void setJobSiteName(String jobSiteName) {
		this.jobSiteName = jobSiteName;
	}

	@DBColumn(JOB_SITE_ADDRESS)
	public String getJobSiteAddress() {
		return jobSiteAddress;
	}

	@DBColumn(JOB_SITE_ADDRESS)
	public void setJobSiteAddress(String jobSiteAddress) {
		this.jobSiteAddress = jobSiteAddress;
	}

	@DBColumn(START_DATE)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy")
	public Date getStartDate() {
		return startDate;
	}

	@DBColumn(START_DATE)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy")
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	@DBColumn(JOB_FREQUENCY)
	public String getJobFrequency() {
		return jobFrequency;
	}

	@DBColumn(JOB_FREQUENCY)
	public void setJobFrequency(String jobFrequency) {
		this.jobFrequency = jobFrequency;
	}

	@DBColumn(PRICE_PER_CLEANING)
	@JsonSerialize(using = AnsiCurrencyFormatter.class)
	public BigDecimal getPricePerCleaning() {
		return pricePerCleaning;
	}

	@DBColumn(PRICE_PER_CLEANING)
	public void setPricePerCleaning(BigDecimal pricePerCleaning) {
		this.pricePerCleaning = pricePerCleaning;
	}

	@DBColumn(JOB_NBR)
	public Integer getJobNbr() {
		return jobNbr;
	}

	@DBColumn(JOB_NBR)
	public void setJobNbr(Integer jobNbr) {
		this.jobNbr = jobNbr;
	}

	@DBColumn(SERVICE_DESCRIPTION)
	public String getServiceDescription() {
		return serviceDescription;
	}

	@DBColumn(SERVICE_DESCRIPTION)
	public void setServiceDescription(String serviceDescription) {
		this.serviceDescription = serviceDescription;
	}

	@DBColumn(PO_NUMBER)
	public String getPoNumber() {
		return poNumber;
	}

	@DBColumn(PO_NUMBER)
	public void setPoNumber(String poNumber) {
		this.poNumber = poNumber;
	}

	@DBColumn(BILL_TO_ADDRESS_ID)
	public Integer getBillToAddressId() {
		return billToAddressId;
	}

	@DBColumn(BILL_TO_ADDRESS_ID)
	public void setBillToAddressId(Integer billToAddressId) {
		this.billToAddressId = billToAddressId;
	}

	@DBColumn(JOB_SITE_ADDRESS_ID)
	public Integer getJobSiteAddressId() {
		return jobSiteAddressId;
	}

	@DBColumn(JOB_SITE_ADDRESS_ID)
	public void setJobSiteAddressId(Integer jobSiteAddressId) {
		this.jobSiteAddressId = jobSiteAddressId;
	}

	@DBColumn(DIVISION_ID)
	public Integer getDivisionId() {
		return divisionId;
	}

	@DBColumn(DIVISION_ID)
	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}

	@DBColumn(DIVISION_NBR)
	public Integer getDivisionNbr() {
		return divisionNbr;
	}

	@DBColumn(DIVISION_NBR)
	public void setDivisionNbr(Integer divisionNbr) {
		this.divisionNbr = divisionNbr;
	}

	@DBColumn(DIVISION_CODE)
	public String getDivisionCode() {
		return divisionCode;
	}

	@DBColumn(DIVISION_CODE)
	public void setDivisionCode(String divisionCode) {
		this.divisionCode = divisionCode;
	}

	@DBColumn(QUOTE_ID)
	public Integer getQuoteId() {
		return quoteId;
	}

	@DBColumn(QUOTE_ID)
	public void setQuoteId(Integer quoteId) {
		this.quoteId = quoteId;
	}

	@DBColumn(QUOTE_NUMBER)
	public Integer getQuoteNumber() {
		return quoteNumber;
	}

	@DBColumn(QUOTE_NUMBER)
	public void setQuoteNumber(Integer quoteNumber) {
		this.quoteNumber = quoteNumber;
	}

	@DBColumn(REVISION)
	public String getRevision() {
		return revision;
	}

	@DBColumn(REVISION)
	public void setRevision(String revision) {
		this.revision = revision;
	}

	@DBColumn(JOB_SITE_CITY)
	public String getJobSiteCity() {
		return jobSiteCity;
	}

	@DBColumn(JOB_SITE_CITY)
	public void setJobSiteCity(String jobSiteCity) {
		this.jobSiteCity = jobSiteCity;
	}

	@DBColumn(JOB_SITE_STATE)
	public String getJobSiteState() {
		return jobSiteState;
	}

	@DBColumn(JOB_SITE_STATE)
	public void setJobSiteState(String jobSiteState) {
		this.jobSiteState = jobSiteState;
	}

	@DBColumn(BILLING_CONTACT)
	public JobSearchContact getBillingContact() {
		return billingContact;
	}

	@DBColumn(BILLING_CONTACT)
	public void setBillingContact(JobSearchContact billingContact) {
		this.billingContact = billingContact;
	}

	@DBColumn(CONTRACT_CONTACT)
	public JobSearchContact getContractContact() {
		return contractContact;
	}

	@DBColumn(CONTRACT_CONTACT)
	public void setContractContact(JobSearchContact contractContact) {
		this.contractContact = contractContact;
	}

	@DBColumn(JOB_CONTACT)
	public JobSearchContact getJobContact() {
		return jobContact;
	}

	@DBColumn(JOB_CONTACT)
	public void setJobContact(JobSearchContact jobContact) {
		this.jobContact = jobContact;
	}

	@DBColumn(SITE_CONTACT)
	public JobSearchContact getSiteContact() {
		return siteContact;
	}

	@DBColumn(SITE_CONTACT)
	public void setSiteContact(JobSearchContact siteContact) {
		this.siteContact = siteContact;
	}

	@DBColumn(PROPOSAL_DATE)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy")
	public Date getProposalDate() {
		return proposalDate;
	}

	@DBColumn(PROPOSAL_DATE)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy")
	public void setProposalDate(Date proposalDate) {
		this.proposalDate = proposalDate;
	}

	@DBColumn(ACTIVATION_DATE)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy")
	public Date getActivationDate() {
		return activationDate;
	}

	@DBColumn(ACTIVATION_DATE)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy")
	public void setActivationDate(Date activationDate) {
		this.activationDate = activationDate;
	}

	@DBColumn(CANCEL_DATE)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy")
	public Date getCancelDate() {
		return cancelDate;
	}

	@DBColumn(CANCEL_DATE)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy")
	public void setCancelDate(Date cancelDate) {
		this.cancelDate = cancelDate;
	}

	@DBColumn(CANCEL_REASON)
	public String getCancelReason() {
		return cancelReason;
	}

	@DBColumn(CANCEL_REASON)
	public void setCancelReason(String cancelReason) {
		this.cancelReason = cancelReason;
	}

}