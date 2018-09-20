package com.ansi.scilla.web.job.request;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.beanutils.PropertyUtils;

import com.ansi.scilla.web.common.request.AbstractRequest;
import com.ansi.scilla.web.common.request.RequiredForAdd;
import com.ansi.scilla.web.common.request.RequiredForUpdate;
import com.ansi.scilla.web.common.utils.AppUtils;
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
	
	public JobRequest() {
		super();
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

	public void setInvoiceStyle(String invoiceStyle) {
		this.invoiceStyle = invoiceStyle;
	}

	@RequiredForAdd
	@RequiredForUpdate
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
	
}
