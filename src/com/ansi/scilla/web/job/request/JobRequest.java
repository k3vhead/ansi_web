package com.ansi.scilla.web.job.request;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.common.db.Job;
import com.ansi.scilla.common.jobticket.JobStatus;
import com.ansi.scilla.web.common.request.AbstractRequest;
import com.ansi.scilla.web.common.request.RequestValidator;
import com.ansi.scilla.web.common.request.RequiredForAdd;
import com.ansi.scilla.web.common.request.RequiredForUpdate;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.thewebthing.commons.lang.JsonException;

public class JobRequest extends AbstractRequest{

	private static final long serialVersionUID = 1L;
	
	public static final String JOB_FREQUENCY = "jobFrequency";
	public static final String JOB_NBR = "jobNbr";
	public static final String PRICE_PER_CLEANING = "pricePerCleaning";
	public static final String SERVICE_DESCRIPTION = "serviceDescription";
	public static final String REQUEST_SPECIAL_SCHEDULING = "requestSpecialScheduling";
	public static final String DIRECT_LABOR_PCT = "directLaborPct";
	public static final String BUDGET = "budget";
	public static final String FLOORS = "floors";
	public static final String EQUIPMENT = "equipment";
	public static final String WASHER_NOTES = "washerNotes";
	public static final String OM_NOTES = "omNotes";
	public static final String BILLING_NOTES = "billingNotes";
	public static final String PO_NUMBER = "poNumber";
	public static final String OUR_VENDOR_NBR = "ourVendorNbr";
	public static final String EXPIRATION_DATE = "expirationDate";
	public static final String EXPIRATION_REASON = "expirationReason";
	public static final String REPEAT_SCHEDULE_ANNUALLY = "repeatScheduleAnnually";
	public static final String UPDATE_TYPE = "updateType";
	public static final String ACTION = "action";
	public static final String JOBTAGS = "jobtags";
		

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
	private String[] invoiceStyle;
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
//	private String status;
	private Integer taxExempt;
	private String taxExemptReason;
	private String washerNotes;
	private Integer addedBy;
	private Date addedDate;
	private String updateType;	
	private String action;
	private Date proposalDate;
	private Boolean annualRepeat;
	private Integer[] jobtags;
	
	private Logger logger;
	
	public JobRequest() {
		super();
		this.logger = LogManager.getLogger(this.getClass());
	}
	
	public JobRequest(String jsonString) throws JsonException, IllegalAccessException, InvocationTargetException, JsonParseException, JsonMappingException, IOException, NoSuchMethodException, InstantiationException {
		this();
		JobRequest req = new JobRequest();
		AppUtils.json2object(jsonString, req);
		PropertyUtils.copyProperties(this, req);
	}
	
	public Integer getAddedBy() {
		return this.addedBy;
	}
	
	public void setAddedBy(Integer addedBy) {
		this.addedBy = addedBy;
	}

	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public Date getAddedDate() {
		return this.addedDate;
	}
	
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public void setAddedDate(Date addedDate) {
		this.addedDate = addedDate;
	}
	
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public void setActivationDate(Date activationDate) {
		this.activationDate = activationDate;
	}

//	@RequiredForAdd
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public Date getActivationDate() {
		return this.activationDate;
	}

	public void setBillingContactId(Integer billingContactId) {
		this.billingContactId = billingContactId;
	}

	@RequiredForAdd
	@RequiredForUpdate
	public Integer getBillingContactId() {
		return this.billingContactId;
	}

	public void setBillingNotes(String billingNotes) {
		this.billingNotes = billingNotes;
	}

	public String getBillingNotes() {
		return this.billingNotes;
	}

	public void setBudget(BigDecimal budget) {
		this.budget = budget;
	}

	@RequiredForAdd
	@RequiredForUpdate
	public BigDecimal getBudget() {
		return this.budget;
	}

	public void setBuildingType(String buildingType) {
		this.buildingType = buildingType;
	}

	@RequiredForAdd
	@RequiredForUpdate
	public String getBuildingType() {
		return this.buildingType;
	}

	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public void setCancelDate(Date cancelDate) {
		this.cancelDate = cancelDate;
	}

	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public Date getCancelDate() {
		return this.cancelDate;
	}

	public void setCancelReason(String cancelReason) {
		this.cancelReason = cancelReason;
	}

	public String getCancelReason() {
		return this.cancelReason;
	}

	public void setContractContactId(Integer contractContactId) {
		this.contractContactId = contractContactId;
	}

	@RequiredForAdd
	@RequiredForUpdate
	public Integer getContractContactId() {
		return this.contractContactId;
	}

	public void setDirectLaborPct(BigDecimal directLaborPct) {
		this.directLaborPct = directLaborPct;
	}

	@RequiredForAdd
	@RequiredForUpdate
	public BigDecimal getDirectLaborPct() {
		return this.directLaborPct;
	}

	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}

	@RequiredForAdd
	@RequiredForUpdate
	public Integer getDivisionId() {
		return this.divisionId;
	}

	public void setEquipment(String equipment) {
		this.equipment = equipment;
	}

	@RequiredForAdd
	@RequiredForUpdate
	public String getEquipment() {
		return this.equipment;
	}

	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public Date getExpirationDate() {
		return this.expirationDate;
	}

	public void setExpirationReason(String expirationReason) {
		this.expirationReason = expirationReason;
	}

	public String getExpirationReason() {
		return this.expirationReason;
	}

	public void setFloors(Integer floors) {
		this.floors = floors;
	}

	@RequiredForAdd
	@RequiredForUpdate
	public Integer getFloors() {
		return this.floors;
	}

	public void setInvoiceBatch(Integer invoiceBatch) {
		this.invoiceBatch = invoiceBatch;
	}

	@RequiredForAdd
	@RequiredForUpdate
	public Integer getInvoiceBatch() {
		return this.invoiceBatch;
	}

	public void setInvoiceGrouping(String invoiceGrouping) {
		this.invoiceGrouping = invoiceGrouping;
	}

	@RequiredForAdd
	@RequiredForUpdate
	public String getInvoiceGrouping() {
		return this.invoiceGrouping;
	}

	public void setInvoiceStyle(String[] invoiceStyle) {
		this.invoiceStyle = invoiceStyle;
	}

	@RequiredForAdd
	@RequiredForUpdate
	public String[] getInvoiceStyle() {
		return this.invoiceStyle;
	}

	public void setInvoiceTerms(String invoiceTerms) {
		this.invoiceTerms = invoiceTerms;
	}

	public String getInvoiceTerms() {
		return this.invoiceTerms;
	}

	public void setJobContactId(Integer jobContactId) {
		this.jobContactId = jobContactId;
	}

	@RequiredForAdd
	@RequiredForUpdate
	public Integer getJobContactId() {
		return this.jobContactId;
	}

	public void setJobFrequency(String jobFrequency) {
		this.jobFrequency = jobFrequency;
	}

	@RequiredForAdd
	@RequiredForUpdate
	public String getJobFrequency() {
		return this.jobFrequency;
	}

	public void setJobId(Integer jobId) {
		this.jobId = jobId;
	}

//	@RequiredForAdd
//	@RequiredForUpdate
	public Integer getJobId() {
		return this.jobId;
	}

	public void setJobNbr(Integer jobNbr) {
		this.jobNbr = jobNbr;
	}

	@RequiredForAdd
	@RequiredForUpdate
	public Integer getJobNbr() {
		return this.jobNbr;
	}

	public void setJobTypeId(Integer jobTypeId) {
		this.jobTypeId = jobTypeId;
	}

	public Integer getJobTypeId() {
		return this.jobTypeId;
	}

	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public void setLastPriceChange(Date lastPriceChange) {
		this.lastPriceChange = lastPriceChange;
	}

	public Date getLastPriceChange() {
		return this.lastPriceChange;
	}

	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public void setLastReviewDate(Date lastReviewDate) {
		this.lastReviewDate = lastReviewDate;
	}

	public Date getLastReviewDate() {
		return this.lastReviewDate;
	}

	public void setOmNotes(String omNotes) {
		this.omNotes = omNotes;
	}

	public String getOmNotes() {
		return this.omNotes;
	}

	public void setOurVendorNbr(String ourVendorNbr) {
		this.ourVendorNbr = ourVendorNbr;
	}

	public String getOurVendorNbr() {
		return this.ourVendorNbr;
	}

	public void setPaymentTerms(String paymentTerms) {
		this.paymentTerms = paymentTerms;
	}

	public String getPaymentTerms() {
		return this.paymentTerms;
	}

	public void setPoNumber(String poNumber) {
		this.poNumber = poNumber;
	}

	public String getPoNumber() {
		return this.poNumber;
	}

	public void setPricePerCleaning(BigDecimal pricePerCleaning) {
		this.pricePerCleaning = pricePerCleaning;
	}

	@RequiredForAdd
	@RequiredForUpdate
	public BigDecimal getPricePerCleaning() {
		return this.pricePerCleaning;
	}

	public void setQuoteId(Integer quoteId) {
		this.quoteId = quoteId;
	}

	@RequiredForAdd
	@RequiredForUpdate
	public Integer getQuoteId() {
		return this.quoteId;
	}

	public void setRepeatScheduleAnnually(Integer repeatScheduleAnnually) {
		this.repeatScheduleAnnually = repeatScheduleAnnually;
	}

	@RequiredForAdd
	@RequiredForUpdate
	public Integer getRepeatScheduleAnnually() {
		return this.repeatScheduleAnnually;
	}

	public void setRequestSpecialScheduling(Integer requestSpecialScheduling) {
		this.requestSpecialScheduling = requestSpecialScheduling;
	}

	@RequiredForAdd
//	@RequiredForUpdate
	public Integer getRequestSpecialScheduling() {
		return this.requestSpecialScheduling;
	}

	public void setServiceDescription(String serviceDescription) {
		this.serviceDescription = serviceDescription;
	}

	@RequiredForAdd
	@RequiredForUpdate
	public String getServiceDescription() {
		return this.serviceDescription;
	}

	public void setSiteContact(Integer siteContact) {
		this.siteContact = siteContact;
	}

	@RequiredForAdd
	@RequiredForUpdate
	public Integer getSiteContact() {
		return this.siteContact;
	}

	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getStartDate() {
		return this.startDate;
	}

//	public void setStatus(String status) {
//		this.status = status;
//	}

//	@RequiredForAdd
//	public String getStatus() {
//		return this.status;
//	}

	public void setTaxExempt(Integer taxExempt) {
		this.taxExempt = taxExempt;
	}

	@RequiredForAdd
	@RequiredForUpdate
	public Integer getTaxExempt() {
		return this.taxExempt;
	}

	public String getTaxExemptReason() {
		return taxExemptReason;
	}

	public void setTaxExemptReason(String taxExemptReason) {
		this.taxExemptReason = taxExemptReason;
	}

	public void setWasherNotes(String washerNotes) {
		this.washerNotes = washerNotes;
	}

	public String getWasherNotes() {
		return this.washerNotes;
	}

	public String getUpdateType() {
		return updateType;
	}

	public void setUpdateType(String updateType) {
		this.updateType = updateType;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public Date getProposalDate() {
		return proposalDate;
	}

	public void setProposalDate(Date proposalDate) {
		this.proposalDate = proposalDate;
	}

	public Boolean getAnnualRepeat() {
		return annualRepeat;
	}

	public void setAnnualRepeat(Boolean annualRepeat) {
		this.annualRepeat = annualRepeat;
	}
	
	
	public WebMessages validateProposalUpdate(Connection conn) throws SQLException {
		WebMessages webMessages = new WebMessages();
		
		RequestValidator.validateJobFrequency(webMessages, "jobFrequency", this.jobFrequency, true);
		RequestValidator.validateInteger(webMessages, "jobNbr", this.jobNbr, 1, null, true);
		RequestValidator.validateBigDecimal(webMessages, "pricePerCleaning", this.pricePerCleaning, new BigDecimal(0), null, true);
		RequestValidator.validateString(webMessages, "serviceDescription", this.serviceDescription, true);
		RequestValidator.validateServiceTags(conn, webMessages, "jobtags", this.jobtags, true, "Service");
		
		return webMessages;
	}

	
	
	/**
{
	"requestSpecialScheduling": 0,
	"directLaborPct": "30",
	"budget": "618",
	"floors": "3",
	"equipment": "BASIC, IPC EAGLE HIGH RISE CLEANER",
	"washerNotes": "",
	"omNotes": "",
	"billingNotes": "",
	"updateType": "activation",
	"quoteId": 18888
}

	 * @return
	 */
	public WebMessages validateActivationUpdate() {
		WebMessages webMessages = new WebMessages();
		
		RequestValidator.validateBoolean(webMessages, "requestSpecialScheduling", this.requestSpecialScheduling, true);
		RequestValidator.validateBigDecimal(webMessages, "directLaborPct", this.directLaborPct, null, null, true);
		RequestValidator.validateBigDecimal(webMessages, "budget", this.budget, null, null, true);
		RequestValidator.validateInteger(webMessages, "floors", this.floors, 0, null, true);
		RequestValidator.validateString(webMessages, "equipment", this.equipment, true);
		RequestValidator.validateString(webMessages, "washerNotes", this.washerNotes, false);
		RequestValidator.validateString(webMessages, "omNotes", this.omNotes, false);
		RequestValidator.validateString(webMessages, "billingNotes", this.billingNotes, false);
		
		return webMessages;
	}

	
	/**
{
	"poNumber": "",
	"ourVendorNbr": "",
	"expirationDate": "",
	"expirationReason": "",
	"updateType": "invoice",
	"quoteId": 18888
}
	 * @return
	 */
	public WebMessages validateInvoiceUpdate() {
		WebMessages webMessages = new WebMessages();
		
		RequestValidator.validateString(webMessages, "poNumber", this.poNumber, false);
		RequestValidator.validateString(webMessages, "ourVendorNbr", this.ourVendorNbr, false);
		RequestValidator.validateDate(webMessages, "expirationDate", this.expirationDate, false, null, null);
		RequestValidator.validateString(webMessages, "expirationReason", this.expirationReason, false);
		
		return webMessages;
	}

	
	
	
	/**
{
	"repeatScheduleAnnually": 0,
	"updateType": "schedule",
	"quoteId": 18888
}
	 * @return
	 */
	public WebMessages validateScheduleUpdate() {
		WebMessages webMessages = new WebMessages();
		
		RequestValidator.validateBoolean(webMessages, "repeatScheduleAnnually", this.repeatScheduleAnnually, true);
		RequestValidator.validateString(webMessages, "cancelReason", this.cancelReason, true);
		
		return webMessages;
	}

	/**
	 * Validate input for adding a job to a new quote
	 * 
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public WebMessages validateNewJob(Connection conn) throws Exception {
		WebMessages webMessages = new WebMessages();
		
		WebMessages proposalMessages = validateProposalUpdate(conn);
		WebMessages activationMessages = validateProposalUpdate(conn);
		WebMessages invoiceMessages = validateProposalUpdate(conn);
		WebMessages scheduleMessages = validateProposalUpdate(conn);
		
		for ( WebMessages messages : new WebMessages[] {proposalMessages, activationMessages, invoiceMessages, scheduleMessages} ) {
			if ( ! messages.isEmpty() ) {
				webMessages.addAllMessages(messages);
			}
		}
		
		System.out.println("*******************************************");
		System.out.println("JobRequest 654");
		System.out.println("*******************************************");
		
		for ( Map.Entry<String,List<String>> entry : webMessages.entrySet() ) {
			System.out.println(entry.getKey());
			for ( String message : webMessages.get(entry.getKey())) {
				System.out.println("\t" + message);
			}			
		}
		System.out.println("*******************************************");
		System.out.println("*******************************************");
		System.out.println("*******************************************");

		RequestValidator.validateId(conn, webMessages, "contact", "contact_id", "billingContactId", this.billingContactId, true);
		RequestValidator.validateBuildingType(conn, webMessages, "buildingType", this.buildingType, true);
		RequestValidator.validateId(conn, webMessages, "contact", "contact_id", "contractContactId", this.contractContactId, true);
		RequestValidator.validateId(conn, webMessages, "division", "division_id", "divisionId", this.divisionId, true);
		RequestValidator.validateBoolean(webMessages, "invoiceBatch", this.getInvoiceBatch(), true);
		RequestValidator.validateInvoiceGrouping(webMessages, "invoiceGrouping", this.invoiceGrouping, true);
		RequestValidator.validateInvoiceStyle(webMessages, "invoiceStyle", this.invoiceStyle, true);
		RequestValidator.validateInvoiceTerms(webMessages, "invoiceTerms", this.invoiceTerms, true);
		RequestValidator.validateId(conn, webMessages, "contact", "contact_id", "jobContactId", this.jobContactId, true);
//		job.setJobTypeId(this.getJobTypeId());
		logger.log(Level.INFO, "Payment terms -- is that a thing?");
//		RequestValidator.validatePaymentTerms(webMessages, "paymentTerms", this.paymentTerms, true);
		RequestValidator.validateId(conn, webMessages, "quote", "quote_id", "quoteId", this.quoteId, true);
		RequestValidator.validateId(conn, webMessages, "contact", "contact_id", "siteContact", this.siteContact, true);
		RequestValidator.validateBoolean(webMessages, "taxExempt", this.taxExempt, true);
		if ( this.taxExempt != null && this.taxExempt == 1 ) {
			RequestValidator.validateString(webMessages, "taxExemptReason", this.taxExemptReason, true);
		}
		
		return webMessages;
	}
	
	
	
	
	/**
	 * Add a job to a new quote (ie a quote that is being created, but does not already exist. Since the quote stuff doesn't
	 * actually exist yet, this is a subset of the validateNewJob() method
	 * 
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public WebMessages validateNewQuote(Connection conn) throws Exception {
		WebMessages webMessages = new WebMessages();
		RequestValidator.validateJobFrequency(webMessages, JOB_FREQUENCY, this.getJobFrequency(), true);
		RequestValidator.validateInteger(webMessages, JOB_NBR, this.jobNbr, 1, null, true);
		RequestValidator.validateNumber(webMessages, PRICE_PER_CLEANING, this.getPricePerCleaning(), new BigDecimal(0), null, true, null);
		RequestValidator.validateString(webMessages, SERVICE_DESCRIPTION, this.getServiceDescription(), true);
		RequestValidator.validateBoolean(webMessages, REQUEST_SPECIAL_SCHEDULING, this.getRequestSpecialScheduling(), false);
		RequestValidator.validateNumber(webMessages, DIRECT_LABOR_PCT, this.getDirectLaborPct(), new BigDecimal(0), new BigDecimal(100), true, null);
		RequestValidator.validateNumber(webMessages,BUDGET, this.getBudget(), new BigDecimal(0), null, true, null);
		RequestValidator.validateInteger(webMessages,FLOORS, this.getFloors(), 0, null, true);
		RequestValidator.validateString(webMessages, EQUIPMENT, this.getEquipment(), true);
		RequestValidator.validateString(webMessages, WASHER_NOTES, this.getWasherNotes(), false);
		RequestValidator.validateString(webMessages, OM_NOTES, this.getOmNotes(), false);
		RequestValidator.validateString(webMessages, BILLING_NOTES, this.getBillingNotes(), false);
		RequestValidator.validateString(webMessages, PO_NUMBER, this.getPoNumber(), false);
		RequestValidator.validateString(webMessages, OUR_VENDOR_NBR, this.getOurVendorNbr(), false);
		RequestValidator.validateDate(webMessages, EXPIRATION_DATE, this.getExpirationDate(), false, null, null);
		boolean expireReasonIsRequired = this.getExpirationDate() != null;
		RequestValidator.validateString(webMessages, EXPIRATION_REASON, this.getExpirationReason(), expireReasonIsRequired);
		RequestValidator.validateBoolean(webMessages, REPEAT_SCHEDULE_ANNUALLY, this.getRepeatScheduleAnnually(), false);
		RequestValidator.validateServiceTags(conn, webMessages, JOBTAGS, this.jobtags, true, "Service Type");
			
		return webMessages;
	}
	
	
	
	
	public WebMessages validateDeleteJob(Job job) throws Exception {
		WebMessages webMessages = new WebMessages();
		
		JobStatus status = JobStatus.lookup(job.getStatus());
		if ( ! status.equals(JobStatus.NEW)) {
			webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "Only new jobs can be deleted");
		}
		
		return webMessages;
	}

	public Integer[] getJobtags() {
		return jobtags;
	}

	public void setJobtags(Integer[] jobtags) {
		this.jobtags = jobtags;
	}
}
