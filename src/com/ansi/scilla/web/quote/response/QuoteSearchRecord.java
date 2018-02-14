package com.ansi.scilla.web.quote.response;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.beanutils.PropertyUtils;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.queries.QuoteSearch;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 
 * @author dclewis
 *
 */

public class QuoteSearchRecord extends ApplicationObject {
	private static final long serialVersionUID = 1L;
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
	
	private String billToName;
	private String jobSiteAddress;
	private String jobSiteName;
	private String managerName;
	private Integer quoteJobCount;
	private BigDecimal quotePpcSum;
	
	public QuoteSearchRecord(QuoteSearch quoteSearch) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		this();
		PropertyUtils.copyProperties(this, quoteSearch.getQuote());
		this.billToName = quoteSearch.getBillToName();			
		this.jobSiteName = quoteSearch.getJobSiteName();			
		this.jobSiteAddress = quoteSearch.getJobSiteAddress();			
		this.managerName = quoteSearch.getManagerName();			
		this.quoteJobCount = quoteSearch.getQuoteJobCount();			
		this.quotePpcSum = quoteSearch.getQuotePpcSum();			
	}

	public QuoteSearchRecord() {
		super();
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

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public String getAccountType() {
		return this.accountType;
	}

	public String getBillToName() {
		return billToName;
	}
	public void setBillToName(String billToName) {
		this.billToName = billToName;
	}
	public String getJobSiteName() {
		return jobSiteName;
	}
	public void setJobSiteName(String jobSiteName) {
		this.jobSiteName = jobSiteName;
	}
	public String getJobSiteAddress() {
		return jobSiteAddress;
	}
	public void setJobSiteAddress(String jobSiteAddress) {
		this.jobSiteAddress = jobSiteAddress;
	}
	public String getManagerName() {
		return managerName;
	}
	public void setManagerName(String managerName) {
		this.managerName = managerName;
	}
	public Integer getQuoteJobCount() {
		return quoteJobCount;
	}
	public void setQuoteJobCount(Integer quoteJobCount) {
		this.quoteJobCount = quoteJobCount;
	}
	public BigDecimal getQuotePpcSum() {
		return quotePpcSum;
	}
	public void setQuotePpcSum(BigDecimal quotePpcSum) {
		this.quotePpcSum = quotePpcSum;
	}
}
