package com.ansi.scilla.web.request;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
//import java.util.Date;
import java.util.Date;

import org.apache.commons.beanutils.BeanUtils;

import com.ansi.scilla.web.common.AppUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * 
 * @author jwlewis
 *
 *
 *
 */

public class QuoteSearchRequest extends AbstractRequest {

	private static final long serialVersionUID = 1L;
	
	private Integer addedBy;
	private Date addedDate;
	private String accountType;
	private String address;
	private Integer billToAddressId;
	private Integer copiedFromQuoteId;
	private Integer jobSiteAddressId;
	private String leadType;
	private Integer managerId;
	private String name;
	private String paymentTerms;
	private Date proposalDate;
	private Integer quoteGroupId;
	private Integer quoteId;
	private Integer quoteNumber;
	private String revision;
	private Integer signedByContactId;
	private Integer templateId;
	private Integer updatedBy;
	private Date updatedDate;
	
	public QuoteSearchRequest() {
		super();
	}
	
	public QuoteSearchRequest(String jsonString) throws IllegalAccessException, InvocationTargetException, JsonParseException, JsonMappingException, IOException {
		this();
		QuoteSearchRequest req = (QuoteSearchRequest) AppUtils.json2object(jsonString, QuoteSearchRequest.class);
		BeanUtils.copyProperties(this, req);
	}

	public void setAddedBy(Integer addedBy) {
		this.addedBy = addedBy;
	}

	public Integer getAddedBy() {
		return this.addedBy;
	}

	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy")
	public void setAddedDate(Date addedDate) {
		this.addedDate = addedDate;
	}

	public Date getAddedDate() {
		return this.addedDate;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAddress() {
		return this.address;
	}

	public void setBillToAddressId(Integer billToAddressId) {
		this.billToAddressId = billToAddressId;
	}

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

	public Integer getJobSiteAddressId() {
		return this.jobSiteAddressId;
	}

	public void setLeadType(String leadType) {
		this.leadType = leadType;
	}

	public String getLeadType() {
		return this.leadType;
	}

	public void setManagerId(Integer managerId) {
		this.managerId = managerId;
	}

	public Integer getManagerId() {
		return this.managerId;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public void setPaymentTerms(String paymentTerms) {
		this.paymentTerms = paymentTerms;
	}

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

	public void setQuoteGroupId(Integer quoteGroupId) {
		this.quoteGroupId = quoteGroupId;
	}

	public Integer getQuoteGroupId() {
		return this.quoteGroupId;
	}

	public void setQuoteId(Integer quoteId) {
		this.quoteId = quoteId;
	}

	public Integer getQuoteId() {
		return this.quoteId;
	}

	public void setQuoteNumber(Integer quoteNumber) {
		this.quoteNumber = quoteNumber;
	}

	public Integer getQuoteNumber() {
		return this.quoteNumber;
	}

	public void setRevision(String revision) {
		this.revision = revision;
	}

	public String getRevision() {
		return this.revision;
	}

	public void setSignedByContactId(Integer signedByContactId) {
		this.signedByContactId = signedByContactId;
	}

	public Integer getSignedByContactId() {
		return this.signedByContactId;
	}

	public void setTemplateId(Integer templateId) {
		this.templateId = templateId;
	}

	public Integer getTemplateId() {
		return this.templateId;
	}

	public void setUpdatedBy(Integer updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Integer getUpdatedBy() {
		return this.updatedBy;
	}

	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy")
	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	public Date getUpdatedDate() {
		return this.updatedDate;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public String getAccountType() {
		return this.accountType;
	}

	
}
