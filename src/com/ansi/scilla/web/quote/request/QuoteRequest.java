package com.ansi.scilla.web.quote.request;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;

import com.ansi.scilla.web.common.request.AbstractRequest;
import com.ansi.scilla.web.common.request.RequiredForAdd;
import com.ansi.scilla.web.common.request.RequiredForUpdate;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.thewebthing.commons.lang.JsonException;


	public class QuoteRequest extends AbstractRequest{

		public static final String ADDRESS = "address";
		public static final String BILL_TO_ADDRESS_ID = "billToAddressId";
		public static final String COPIED_FROM_QUOTE_ID = "copiedFromQuoteId";
		public static final String JOB_SITE_ADDRESS_ID = "jobSiteAddressId";
		public static final String LEAD_TYPE = "leadType";
		public static final String MANAGER_ID = "managerId";
		public static final String NAME = "name";
		public static final String PAYMENT_TERMS = "paymentTerms";
		public static final String PROPOSAL_DATE = "proposalDate";
		public static final String QUOTE_ID = "quoteId";
		public static final String QUOTE_NUMBER = "quoteNumber";
		public static final String REVISION = "revision";
		public static final String SIGNED_BY_CONTACT_ID = "signedByContactId";
		public static final String STATUS = "status";
		public static final String TEMPLATE_ID = "templateId";
		public static final String DIVISION_ID = "divisionId";
		public static final String ACCOUNT_TYPE = "accountType";
		public static final String ADDED_BY = "addedBy";
		public static final String ADDED_DATE = "addedDate";
		public static final String CONTRACT_CONTACT_ID = "contractContactId";
		public static final String BILLING_CONTACT_ID = "billingContactId";
		public static final String JOB_CONTACT_ID = "jobContactId";
		public static final String SITE_CONTACT = "siteContact";
		public static final String TAX_EXEMPT = "taxExempt";
		public static final String TAX_EXEMPT_REASON = "taxExemptReason";
		public static final String INVOICE_BATCH = "invoiceBatch";
		public static final String INVOICE_STYLE = "invoiceStyle";
		public static final String BUILDING_TYPE = "buildingType";
		public static final String INVOICE_GROUPING = "invoiceGrouping";
		
		private static final long serialVersionUID = 1L;		

		private String address;
		private Integer billToAddressId;
		private Integer copiedFromQuoteId;
		private Integer jobSiteAddressId;
		private String leadType;
		private Integer managerId;
		private String name;
		private String paymentTerms;
		private Date proposalDate;
		private Integer quoteId;
		private Integer quoteNumber;
		private String revision;
		private Integer signedByContactId;
		private Integer status;
		private Integer templateId;
		private Integer divisionId;
		private String accountType;
		private Integer addedBy;
		private Date addedDate;
		private Integer contractContactId;
		private Integer billingContactId;
		private Integer jobContactId;
		private Integer siteContact;
		private Boolean taxExempt;
		private String taxExemptReason;
		private Boolean invoiceBatch;
		private String invoiceStyle;
		private String buildingType;
		private String invoiceGrouping;

		
		public QuoteRequest() {
			super();
		}
		
		public QuoteRequest(String jsonString) throws JsonException, IllegalAccessException, InvocationTargetException, JsonParseException, JsonMappingException, IOException, InstantiationException, NoSuchMethodException {
			this();
//			AppUtils.json2object(jsonString, QuoteRequest.class, this);
			QuoteRequest req = new QuoteRequest();
			AppUtils.json2object(jsonString, req);
			this.setAddress(req.getAddress());
			this.setBillToAddressId(req.getBillToAddressId());
			this.setCopiedFromQuoteId(req.getCopiedFromQuoteId());
			this.setJobSiteAddressId(req.getJobSiteAddressId());
			this.setLeadType(req.getLeadType());
			this.setManagerId(req.getManagerId());
			this.setName(req.getName());
			this.setPaymentTerms(req.getPaymentTerms());
			this.setProposalDate(req.getProposalDate());
			this.setQuoteId(req.getQuoteId());
			this.setQuoteNumber(req.getQuoteNumber());
			this.setRevision(req.getRevision());
			this.setSignedByContactId(req.getSignedByContactId());
			this.setStatus(req.getStatus());
			this.setTemplateId(req.getTemplateId());
			this.setDivisionId(req.getDivisionId());
			this.setAccountType(req.getAccountType());
			this.setAddedBy(req.getAddedBy());
			this.setAddedDate(req.getAddedDate());
			this.setContractContactId(req.getContractContactId());
			this.setBillingContactId(req.getBillingContactId());
			this.setJobContactId(req.getJobContactId());
			this.setSiteContact(req.getSiteContact());
			this.setBuildingType(req.getBuildingType());
			this.setInvoiceStyle(req.getInvoiceStyle());
			this.setInvoiceGrouping(req.getInvoiceGrouping());
			
			this.setTaxExempt(req.getTaxExempt());
			this.setTaxExemptReason(req.getTaxExemptReason());
			this.setInvoiceBatch(req.getInvoiceBatch());
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
		
		public void setAddress(String address) {
			this.address = address;
		}
 
//		@RequiredForAdd
//		@RequiredForUpdate
		public String getAddress() {
			return this.address;
		}

		
		public void setBillToAddressId(Integer billToAddressId) {
			this.billToAddressId = billToAddressId;
		}

		@RequiredForAdd
		@RequiredForUpdate
		public Integer getBillToAddressId() {
			return this.billToAddressId;
		}

		
		public void setCopiedFromQuoteId(Integer copiedFromQuoteId) {
			this.copiedFromQuoteId = copiedFromQuoteId;
		}

		
		public Integer getCopiedFromQuoteId() {
			return this.copiedFromQuoteId;
		}

		
		public void setJobSiteAddressId(Integer jobSiteAddressId) {
			this.jobSiteAddressId = jobSiteAddressId;
		}

		@RequiredForAdd
		@RequiredForUpdate
		public Integer getJobSiteAddressId() {
			return this.jobSiteAddressId;
		}

		
		public void setLeadType(String leadType) {
			this.leadType = leadType;
		}


		@RequiredForAdd
		@RequiredForUpdate
		public String getLeadType() {
			return this.leadType;
		}

		
		public void setManagerId(Integer managerId) {
			this.managerId = managerId;
		}

		@RequiredForAdd
		@RequiredForUpdate
		public Integer getManagerId() {
			return this.managerId;
		}

		public void setBillingContactId(Integer billingContactId) {
			this.billingContactId = billingContactId;
		}

		@RequiredForAdd
		@RequiredForUpdate
		public Integer getBillingContactId() {
			return this.billingContactId;
		}

		public void setContractContactId(Integer contractContactId) {
			this.contractContactId = contractContactId;
		}

		@RequiredForAdd
		@RequiredForUpdate
		public Integer getContractContactId() {
			return this.contractContactId;
		}

		public void setJobContactId(Integer jobContactId) {
			this.jobContactId = jobContactId;
		}

		@RequiredForAdd
		@RequiredForUpdate
		public Integer getJobContactId() {
			return this.jobContactId;
		}

		public void setSiteContact(Integer siteContact) {
			this.siteContact = siteContact;
		}

		@RequiredForAdd
		@RequiredForUpdate
		public Integer getSiteContact() {
			return this.siteContact;
		}

		public void setDivisionId(Integer divisionId) {
			this.divisionId = divisionId;
		}

		@RequiredForAdd
		@RequiredForUpdate
		public Integer getDivisionId() {
			return this.divisionId;
		}
		
		public void setName(String name) {
			this.name = name;
		}

//		@RequiredForAdd
//		@RequiredForUpdate
		public String getName() {
			return this.name;
		}

		@JsonProperty("invoiceTerms")
		public void setPaymentTerms(String paymentTerms) {
			this.paymentTerms = paymentTerms;
		}

//		@RequiredForAdd
//		@RequiredForUpdate
		@JsonProperty("invoiceTerms")
		@JobUpdateField
		public String getPaymentTerms() {
			return this.paymentTerms;
		}
		
		@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
		public void setProposalDate(Date proposalDate) {
			this.proposalDate = proposalDate;
		}


		@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
		public Date getProposalDate() {
			return this.proposalDate;
		}

		/*
		public void setQuoteGroupId(Integer quoteGroupId) {
			this.quoteGroupId = quoteGroupId;
		}

		
		@RequiredForAdd
		@RequiredForUpdate
		public Integer getQuoteGroupId() {
			return this.quoteGroupId;
		}
		*/
		
		public void setQuoteId(Integer quoteId) {
			this.quoteId = quoteId;
		}

		@RequiredForUpdate
		public Integer getQuoteId() {
			return this.quoteId;
		}
		
		public void setQuoteNumber(Integer quoteNumber) {
			this.quoteNumber = quoteNumber;
		}

		@RequiredForUpdate
		public Integer getQuoteNumber() {
			return this.quoteNumber;
		}

		
		public void setRevision(String revision) {
			this.revision = revision;
		}

		@RequiredForUpdate
		public String getRevision() {
			return this.revision;
		}

		
		public void setSignedByContactId(Integer signedByContactId) {
			this.signedByContactId = signedByContactId;
		}


		public Integer getSignedByContactId() {
			return this.signedByContactId;
		}
		
		public void setAccountType(String accountType) {
			this.accountType = accountType;
		}


		@RequiredForAdd
		@RequiredForUpdate
		public String getAccountType() {
			return this.accountType;
		}

		
		public void setStatus(Integer status) {
			this.status = status;
		}

//		@RequiredForUpdate
		public Integer getStatus() {
			return this.status;
		}

		
		public void setTemplateId(Integer templateId) {
			this.templateId = templateId;
		}

//		@RequiredForAdd
//		@RequiredForUpdate
		public Integer getTemplateId() {
			return this.templateId;
		}
		@JobUpdateField
		public Boolean getTaxExempt() {
			return taxExempt;
		}

		public void setTaxExempt(Boolean taxExempt) {
			this.taxExempt = taxExempt;
		}
		@JobUpdateField
		public String getTaxExemptReason() {
			return taxExemptReason;
		}

		public void setTaxExemptReason(String taxExemptReason) {
			this.taxExemptReason = taxExemptReason;
		}
		@JobUpdateField
		public Boolean getInvoiceBatch() {
			return invoiceBatch;
		}

		public void setInvoiceBatch(Boolean invoiceBatch) {
			this.invoiceBatch = invoiceBatch;
		}
		@JobUpdateField
		public String getInvoiceStyle() {
			return invoiceStyle;
		}

		public void setInvoiceStyle(String invoiceStyle) {
			this.invoiceStyle = invoiceStyle;
		}
		@JobUpdateField
		public String getBuildingType() {
			return buildingType;
		}

		public void setBuildingType(String buildingType) {
			this.buildingType = buildingType;
		}
		@JobUpdateField
		public String getInvoiceGrouping() {
			return invoiceGrouping;
		}

		public void setInvoiceGrouping(String invoiceGrouping) {
			this.invoiceGrouping = invoiceGrouping;
		}

		/**
		 * Loop through methods defined in this class. If the getter is annotated with @JobUpdateField
		 * and if the method returns a non-null value, this quote request contains values that need
		 * to go into the job table
		 * 
		 * @return
		 * @throws IllegalAccessException
		 * @throws IllegalArgumentException
		 * @throws InvocationTargetException
		 */
		public boolean hasJobUpdates() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
			boolean hasJobUpdate = false;
			for ( Method method : this.getClass().getMethods() ) {
				if ( method.getAnnotation(JobUpdateField.class) != null ) {
					Object returnValue = method.invoke(this, (Object[])null);
					if ( returnValue != null ) {
						hasJobUpdate = true;
					}
				}
			}
			
			return hasJobUpdate;
		}

		
	
}
