package com.ansi.scilla.web.request;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;

import org.apache.commons.beanutils.BeanUtils;

import com.ansi.scilla.web.common.AppUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.thewebthing.commons.lang.JsonException;


	public class QuoteRequest extends AbstractRequest{

	
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
		private String revisionNumber;
		private Integer signedByContactId;
		private Integer status;
		private Integer templateId;
		private Integer divisionId;
		private String accountType;
		private Integer addedBy;
		private Date addedDate;
		private Integer contractContactId;
		private Integer billingContactId;

		
		public QuoteRequest() {
			super();
		}
		
		public QuoteRequest(String jsonString) throws JsonException, IllegalAccessException, InvocationTargetException, JsonParseException, JsonMappingException, IOException {
			this();
			QuoteRequest req = (QuoteRequest) AppUtils.json2object(jsonString, QuoteRequest.class);
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
		
		@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy")
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

		
		public void setPaymentTerms(String paymentTerms) {
			this.paymentTerms = paymentTerms;
		}

//		@RequiredForAdd
//		@RequiredForUpdate
		public String getPaymentTerms() {
			return this.paymentTerms;
		}
		
		@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy")
		public void setProposalDate(Date proposalDate) {
			this.proposalDate = proposalDate;
		}


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
		
	//	public void setQuoteId(Integer quoteId) {
	//		this.quoteId = quoteId;
	//	}

	
		public Integer getQuoteId() {
			return this.quoteId;
		}
		
		@RequiredForUpdate
		public void setQuoteId(Integer quoteId) {
			this.quoteId = quoteId;
		}

		
		public void setQuoteNumber(Integer quoteNumber) {
			this.quoteNumber = quoteNumber;
		}

		@RequiredForUpdate
		public Integer getQuoteNumber() {
			return this.quoteNumber;
		}

		
		public void setRevisionNumber(String revisionNumber) {
			this.revisionNumber = revisionNumber;
		}

		@RequiredForUpdate
		public String getRevisionNumber() {
			return this.revisionNumber;
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

		@RequiredForUpdate
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

		
	
}
