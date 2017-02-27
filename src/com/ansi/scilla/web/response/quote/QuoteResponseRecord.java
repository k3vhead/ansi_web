package com.ansi.scilla.web.response.quote;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;

import org.apache.commons.beanutils.BeanUtils;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.db.Quote;
import com.ansi.scilla.common.jsonFormat.AnsiDateFormatter;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;


public class QuoteResponseRecord extends ApplicationObject implements Comparable<QuoteResponseRecord> {

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
	private Integer divisionId;
	private String accountType;
	private Integer quoteNumber;
	private String revision;
	private Integer signedByContactId;
	private Integer status;
	private Integer templateId;
	
	public QuoteResponseRecord(Quote quote) throws IllegalAccessException, InvocationTargetException {
		super();
		BeanUtils.copyProperties(this, quote);
	}
		
	public void setAddress(String address) {
		this.address = address;
	}


	public String getAddress() {
		return this.address;
	}
	
	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}


	public String getAccountType() {
		return this.accountType;
	}
	
	public void setBillToAddressId(Integer billToAddressId) {
		this.billToAddressId = billToAddressId;
	}

	public Integer getBillToAddressId() {
		return this.billToAddressId;
	}
	
	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}

	public Integer getDivisionId() {
		return this.divisionId;
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

	
	public void setProposalDate(Date proposalDate) {
		this.proposalDate = proposalDate;
	}

	@JsonSerialize(using=AnsiDateFormatter.class)
	public Date getProposalDate() {
		return this.proposalDate;
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

	
	public void setRevisionNumber(String revision) {
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

	
	public void setStatus(Integer status) {
		this.status = status;
	}


	public Integer getStatus() {
		return this.status;
	}

	
	public void setTemplateId(Integer templateId) {
		this.templateId = templateId;
	}


	public Integer getTemplateId() {
		return this.templateId;
	}
	
	@Override
	public int compareTo(QuoteResponseRecord o) {
		int ret = this.quoteId.compareTo(o.getQuoteId());
		if ( ret == 0 ) {
			ret = this.quoteNumber.compareTo(o.getQuoteNumber());
		}
		if ( ret == 0 ) {
			ret = this.revision.compareTo(o.getRevision());
		}
		return ret;
	}

	
}
