package com.ansi.scilla.web.job.response;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.beanutils.PropertyUtils;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.db.Job;
import com.ansi.scilla.common.db.User;
import com.ansi.scilla.common.exceptions.InvalidJobStatusException;
import com.fasterxml.jackson.annotation.JsonFormat;

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
	private Boolean canDelete;
	private Boolean canReschedule;
	
	
	public JobDetail(Job job, User addedBy, User updatedBy) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, InvalidJobStatusException {
		super();
//		PropertyUtils.copyProperties(this, job);
		this.billingContactId = job.getBillingContactId();
		this.billingNotes = job.getBillingNotes();
		this.budget = job.getBudget();
		this.buildingType = job.getBuildingType();
		this.cancelReason = job.getCancelReason();
		this.contractContactId = job.getContractContactId();
		this.directLaborPct = job.getDirectLaborPct();
		this.divisionId = job.getDivisionId();
		this.equipment = job.getEquipment();
		this.expirationReason = job.getExpirationReason();
		this.floors = job.getFloors();
		this.invoiceBatch = job.getInvoiceBatch();
		this.invoiceGrouping = job.getInvoiceGrouping();
		this.invoiceStyle = job.getInvoiceStyle();
		this.invoiceTerms = job.getInvoiceTerms();
		this.jobContactId = job.getJobContactId();
		this.jobFrequency = job.getJobFrequency();
		this.jobId = job.getJobId();
		this.jobNbr = job.getJobNbr();
		this.jobTypeId = job.getJobTypeId();
		this.omNotes = job.getOmNotes();
		this.ourVendorNbr = job.getOurVendorNbr();
		this.paymentTerms = job.getPaymentTerms();
		this.poNumber = job.getPoNumber();
		this.pricePerCleaning = job.getPricePerCleaning();
		this.quoteId = job.getQuoteId();
		this.repeatScheduleAnnually = job.getRepeatScheduleAnnually();
		this.requestSpecialScheduling = job.getRequestSpecialScheduling();
		this.serviceDescription = job.getServiceDescription();
		this.siteContact = job.getSiteContact();
		this.status = job.getStatus();
		this.taxExempt = job.getTaxExempt();
		this.washerNotes = job.getWasherNotes();
		this.addedBy = job.getAddedBy();
		this.updatedBy = job.getUpdatedBy();
		this.canActivate = job.canActivate();
		this.canCancel = job.canCancel();
		this.canDelete = job.canDelete();
		this.canReschedule = job.canReschedule();
		
		/** There is weirdness in json formatting of dates. I don't know why, but this fixes it */
		Date date = new Date();
		if ( job.getActivationDate() != null ) {
			activationDate = new Date();
			date.setTime(job.getActivationDate().getTime());
			activationDate.setTime(date.getTime());		
		}
		if ( job.getCancelDate() != null ) {
			cancelDate = new Date();
			date.setTime(job.getCancelDate().getTime());
			cancelDate.setTime(date.getTime());
		}
		if ( job.getExpirationDate() != null ) {
			expirationDate = new Date();
			date.setTime(job.getExpirationDate().getTime());
			expirationDate.setTime(date.getTime());
		}
		if ( job.getLastPriceChange() != null ) {
			lastPriceChange = new Date();
			date.setTime(job.getLastPriceChange().getTime());
			lastPriceChange.setTime(date.getTime());
		}
		if ( job.getLastReviewDate() != null ) {
			lastReviewDate = new Date();
			date.setTime(job.getLastReviewDate().getTime());
			lastReviewDate.setTime(date.getTime());
		}
		if ( job.getStartDate() != null ) {
			startDate = new Date();
			date.setTime(job.getStartDate().getTime());
			startDate.setTime(date.getTime());
		}
		if ( job.getAddedDate() != null ) {
			addedDate = new Date();
			date.setTime(job.getAddedDate().getTime());
			addedDate.setTime(date.getTime());
		}
		if ( job.getUpdatedDate() != null ) {
			updatedDate = new Date();
			date.setTime(job.getUpdatedDate().getTime());
			updatedDate.setTime(date.getTime());
		}
		
		
		
		
		this.addedLastName = addedBy.getLastName();
		this.addedFirstName = addedBy.getFirstName();
		this.addedEmail = addedBy.getEmail();
		this.updatedLastName = updatedBy.getLastName();
		this.updatedFirstName = updatedBy.getFirstName();
		this.updatedEmail = updatedBy.getEmail();
		this.canActivate = job.canActivate();
		this.canCancel = job.canCancel();
		this.canDelete = job.canDelete();
		this.canReschedule = job.canReschedule();
	}
//	@JsonSerialize(using=AnsiDateFormatter.class)
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
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
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
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
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
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
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public Date getLastPriceChange() {
		return lastPriceChange;
	}

	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public void setLastPriceChange(Date lastPriceChange) {
		this.lastPriceChange = lastPriceChange;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public Date getLastReviewDate() {
		return lastReviewDate;
	}

	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
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
//	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
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
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
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
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
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
	public Boolean getCanDelete() {
		return canDelete;
	}
	public void setCanDelete(Boolean canDelete) {
		this.canDelete = canDelete;
	}
	public Boolean getCanReschedule() {
		return canReschedule;
	}
	public void setCanReschedule(Boolean canReschedule) {
		this.canReschedule = canReschedule;
	}

}
