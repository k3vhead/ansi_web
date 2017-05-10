package com.ansi.scilla.web.request;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.beanutils.BeanUtils;

import com.ansi.scilla.web.common.AppUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.thewebthing.commons.lang.JsonException;

public class JobRequest extends AbstractRequest{

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
	
	public JobRequest() {
		super();
	}
	
	public JobRequest(String jsonString) throws JsonException, IllegalAccessException, InvocationTargetException, JsonParseException, JsonMappingException, IOException {
		this();
		JobRequest req = (JobRequest) AppUtils.json2object(jsonString, JobRequest.class);
		BeanUtils.copyProperties(this, req);
	}
	
	public Integer getAddedBy() {
		return this.addedBy;
	}
	
	public void setAddedBy(Integer addedBy) {
		this.addedBy = addedBy;
	}

	public Date getAddedDate() {
		return this.addedDate;
	}
	
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public void setAddedDate(Date addedDate) {
		this.addedDate = addedDate;
	}
	
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy/MM/dd", timezone="America/Chicago")
	public void setActivationDate(Date activationDate) {
		this.activationDate = activationDate;
	}

	@RequiredForAdd
	public Date getActivationDate() {
		return this.activationDate;
	}

	public void setBillingContactId(Integer billingContactId) {
		this.billingContactId = billingContactId;
	}

	@RequiredForAdd
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
	public BigDecimal getBudget() {
		return this.budget;
	}

	public void setBuildingType(String buildingType) {
		this.buildingType = buildingType;
	}

	@RequiredForAdd
	public String getBuildingType() {
		return this.buildingType;
	}

	public void setCancelDate(Date cancelDate) {
		this.cancelDate = cancelDate;
	}

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
	public Integer getContractContactId() {
		return this.contractContactId;
	}

	public void setDirectLaborPct(BigDecimal directLaborPct) {
		this.directLaborPct = directLaborPct;
	}

	@RequiredForAdd
	public BigDecimal getDirectLaborPct() {
		return this.directLaborPct;
	}

	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}

	@RequiredForAdd
	public Integer getDivisionId() {
		return this.divisionId;
	}

	public void setEquipment(String equipment) {
		this.equipment = equipment;
	}

	public String getEquipment() {
		return this.equipment;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

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
	public Integer getFloors() {
		return this.floors;
	}

	public void setInvoiceBatch(Integer invoiceBatch) {
		this.invoiceBatch = invoiceBatch;
	}

	@RequiredForAdd
	public Integer getInvoiceBatch() {
		return this.invoiceBatch;
	}

	public void setInvoiceGrouping(String invoiceGrouping) {
		this.invoiceGrouping = invoiceGrouping;
	}

	public String getInvoiceGrouping() {
		return this.invoiceGrouping;
	}

	public void setInvoiceStyle(String invoiceStyle) {
		this.invoiceStyle = invoiceStyle;
	}

	@RequiredForAdd
	public String getInvoiceStyle() {
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

	public Integer getJobContactId() {
		return this.jobContactId;
	}

	public void setJobFrequency(String jobFrequency) {
		this.jobFrequency = jobFrequency;
	}

	@RequiredForAdd
	public String getJobFrequency() {
		return this.jobFrequency;
	}

	public void setJobId(Integer jobId) {
		this.jobId = jobId;
	}

	@RequiredForAdd
	public Integer getJobId() {
		return this.jobId;
	}

	public void setJobNbr(Integer jobNbr) {
		this.jobNbr = jobNbr;
	}

	@RequiredForAdd
	public Integer getJobNbr() {
		return this.jobNbr;
	}

	public void setJobTypeId(Integer jobTypeId) {
		this.jobTypeId = jobTypeId;
	}

	public Integer getJobTypeId() {
		return this.jobTypeId;
	}

	public void setLastPriceChange(Date lastPriceChange) {
		this.lastPriceChange = lastPriceChange;
	}

	public Date getLastPriceChange() {
		return this.lastPriceChange;
	}

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
	public BigDecimal getPricePerCleaning() {
		return this.pricePerCleaning;
	}

	public void setQuoteId(Integer quoteId) {
		this.quoteId = quoteId;
	}

	@RequiredForAdd
	public Integer getQuoteId() {
		return this.quoteId;
	}

	public void setRepeatScheduleAnnually(Integer repeatScheduleAnnually) {
		this.repeatScheduleAnnually = repeatScheduleAnnually;
	}

	@RequiredForAdd
	public Integer getRepeatScheduleAnnually() {
		return this.repeatScheduleAnnually;
	}

	public void setRequestSpecialScheduling(Integer requestSpecialScheduling) {
		this.requestSpecialScheduling = requestSpecialScheduling;
	}

	@RequiredForAdd
	public Integer getRequestSpecialScheduling() {
		return this.requestSpecialScheduling;
	}

	public void setServiceDescription(String serviceDescription) {
		this.serviceDescription = serviceDescription;
	}

	@RequiredForAdd
	public String getServiceDescription() {
		return this.serviceDescription;
	}

	public void setSiteContact(Integer siteContact) {
		this.siteContact = siteContact;
	}

	public Integer getSiteContact() {
		return this.siteContact;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getStartDate() {
		return this.startDate;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@RequiredForAdd
	public String getStatus() {
		return this.status;
	}

	public void setTaxExempt(Integer taxExempt) {
		this.taxExempt = taxExempt;
	}

	@RequiredForAdd
	public Integer getTaxExempt() {
		return this.taxExempt;
	}

	public void setWasherNotes(String washerNotes) {
		this.washerNotes = washerNotes;
	}

	public String getWasherNotes() {
		return this.washerNotes;
	}
	
}
