package com.ansi.scilla.web.quote.request;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;

import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.web.common.request.AbstractRequest;
import com.ansi.scilla.web.common.request.RequestValidator;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.thewebthing.commons.lang.JsonException;


	public class NewQuoteRequest extends AbstractRequest{

		public static final String ACTION_IS_VALIDATE = "validate";
		public static final String ACTION_IS_SAVE = "save";
		public static final String ACTION_IS_JOB = "job";
		
		public static final String ACTION = "action";
		public static final String BILL_TO_ADDRESS_ID = "billToAddressId";
		public static final String JOB_SITE_ADDRESS_ID = "jobSiteAddressId";
		public static final String LEAD_TYPE = "leadType";
		public static final String MANAGER_ID = "managerId";
		public static final String INVOICE_TERMS = "invoiceTerms";
		public static final String TEMPLATE_ID = "templateId";
		public static final String DIVISION_ID = "divisionId";
		public static final String ACCOUNT_TYPE = "accountType";
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

		private String action;
		private Integer billToAddressId;
		private Integer jobSiteAddressId;
		private String leadType;
		private Integer managerId;
		private String invoiceTerms;
		private Integer templateId;
		private Integer divisionId;
		private String accountType;
		private Integer contractContactId;
		private Integer billingContactId;
		private Integer jobContactId;
		private Integer siteContact;
		private Boolean taxExempt;
		private String taxExemptReason;
		private Boolean invoiceBatch;
		private String[] invoiceStyle;
		private String buildingType;
		private String invoiceGrouping;

		
		public NewQuoteRequest() {
			super();
		}
		
		public NewQuoteRequest(String jsonString) throws JsonException, IllegalAccessException, InvocationTargetException, JsonParseException, JsonMappingException, IOException, InstantiationException, NoSuchMethodException {
			this();
			NewQuoteRequest req = new NewQuoteRequest();
			AppUtils.json2object(jsonString, req);
			this.setAction(req.getAction());
			this.setBillToAddressId(req.getBillToAddressId());
			this.setJobSiteAddressId(req.getJobSiteAddressId());
			this.setLeadType(req.getLeadType());
			this.setManagerId(req.getManagerId());
			this.setInvoiceTerms(req.getInvoiceTerms());
			this.setTemplateId(req.getTemplateId());
			this.setDivisionId(req.getDivisionId());
			this.setAccountType(req.getAccountType());
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
		

		public String getAction() {
			return action;
		}

		public void setAction(String action) {
			this.action = action;
		}

		
		public void setBillToAddressId(Integer billToAddressId) {
			this.billToAddressId = billToAddressId;
		}

		public Integer getBillToAddressId() {
			return this.billToAddressId;
		}

		
		public void setJobSiteAddressId(Integer jobSiteAddressId) {
			this.jobSiteAddressId = jobSiteAddressId;
		}

		public Integer getJobSiteAddressId() {
			return this.jobSiteAddressId;
		}

		
		public void setLeadType(String leadType) {
			this.leadType = leadType;
		}


		@QuoteHeaderField
		public String getLeadType() {
			return this.leadType;
		}

		
		public void setManagerId(Integer managerId) {
			this.managerId = managerId;
		}

		@QuoteHeaderField
		public Integer getManagerId() {
			return this.managerId;
		}

		public void setBillingContactId(Integer billingContactId) {
			this.billingContactId = billingContactId;
		}

		public Integer getBillingContactId() {
			return this.billingContactId;
		}

		public void setContractContactId(Integer contractContactId) {
			this.contractContactId = contractContactId;
		}

		public Integer getContractContactId() {
			return this.contractContactId;
		}

		public void setJobContactId(Integer jobContactId) {
			this.jobContactId = jobContactId;
		}

		public Integer getJobContactId() {
			return this.jobContactId;
		}

		public void setSiteContact(Integer siteContact) {
			this.siteContact = siteContact;
		}

		public Integer getSiteContact() {
			return this.siteContact;
		}

		public void setDivisionId(Integer divisionId) {
			this.divisionId = divisionId;
		}

		@JobUpdateField
		public Integer getDivisionId() {
			return this.divisionId;
		}
		
		@QuoteHeaderField
		public String getInvoiceTerms() {
			return invoiceTerms;
		}

		public void setInvoiceTerms(String invoiceTerms) {
			this.invoiceTerms = invoiceTerms;
		}

		public void setAccountType(String accountType) {
			this.accountType = accountType;
		}


		@QuoteHeaderField
		public String getAccountType() {
			return this.accountType;
		}

		

		
		public void setTemplateId(Integer templateId) {
			this.templateId = templateId;
		}

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
		public String[] getInvoiceStyle() {
			return invoiceStyle;
		}

		public void setInvoiceStyle(String[] invoiceStyle) {
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
		
		
		/**
		 * Loops through getter methods to see which ones have been annotated with @QuoteHeaderField. If executing the
		 * method returns a non-null value, then QuoteHeader is processed.
		 * @return
		 * @throws IllegalAccessException
		 * @throws IllegalArgumentException
		 * @throws InvocationTargetException
		 */
		public boolean hasQuoteHeaderUpdates() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
			boolean hasUpdate = false;
			for ( Method method : this.getClass().getMethods() ) {
				if ( method.getAnnotation(QuoteHeaderField.class) != null ) {
					Object returnValue = method.invoke(this, (Object[])null);
					if ( returnValue != null ) {
						hasUpdate = true;
					}
				}
			}
			
			return hasUpdate;
		}

		public WebMessages validateQuoteHeader(Connection conn) throws Exception {
			WebMessages webMessages = new WebMessages();
			RequestValidator.validateLeadType(conn, webMessages, NewQuoteRequest.LEAD_TYPE, this.getLeadType(), true);
			RequestValidator.validateId(conn, webMessages, "ansi_user", "user_id", NewQuoteRequest.MANAGER_ID, this.getManagerId(), true);
			RequestValidator.validateInvoiceTerms(webMessages, NewQuoteRequest.INVOICE_TERMS, this.getInvoiceTerms(), true);
			RequestValidator.validateAccountType(conn, webMessages, NewQuoteRequest.ACCOUNT_TYPE, this.getAccountType(), true);
			RequestValidator.validateId(conn, webMessages, Division.TABLE, Division.DIVISION_ID, NewQuoteRequest.DIVISION_ID, this.getDivisionId(), true);
			RequestValidator.validateBoolean(webMessages, NewQuoteRequest.TAX_EXEMPT, this.getTaxExempt(), false);
			if ( this.getTaxExempt() != null && this.getTaxExempt() ) {
				RequestValidator.validateString(webMessages, NewQuoteRequest.TAX_EXEMPT_REASON, this.getTaxExemptReason(), true);
			}
			RequestValidator.validateBoolean(webMessages, NewQuoteRequest.INVOICE_BATCH, this.getInvoiceBatch(), false);
			RequestValidator.validateInvoiceStyle(webMessages, NewQuoteRequest.INVOICE_STYLE, this.getInvoiceStyle(), true);
			RequestValidator.validateBuildingType(conn, webMessages, NewQuoteRequest.BUILDING_TYPE, this.getBuildingType(), true);
			RequestValidator.validateInvoiceGrouping(webMessages, NewQuoteRequest.INVOICE_GROUPING, this.getInvoiceGrouping(), true);
			
			return webMessages;
		}

		
	
}
