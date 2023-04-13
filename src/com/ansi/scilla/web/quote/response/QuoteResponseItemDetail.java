package com.ansi.scilla.web.quote.response;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.common.db.Job;
import com.ansi.scilla.common.db.Quote;
import com.ansi.scilla.common.db.User;
import com.fasterxml.jackson.annotation.JsonFormat;

public class QuoteResponseItemDetail extends ApplicationObject {
	private static final long serialVersionUID = 1L;
	
	private String address;
	private Integer billToAddressId;
	private Integer copiedFromQuoteId;
	private Integer jobSiteAddressId;
	private String leadType;
	private Integer managerId;
	private String name;
//	private String paymentTerms;
	private Date proposalDate;
	private Integer quoteId;
	private Integer divisionId;
	private String accountType;
	private Integer quoteNumber;
	private String revision;
	private Integer signedByContactId;
	private Integer status;
	private Integer templateId;
	private String managerFirstName;
	private String managerLastName;
	private String managerEmail;
	private String divisionCode;
//	private Date printDate;
//	private Date quoteDate;
	private Integer printCount;
	private String buildingType;
	private String invoiceTerms;
	private String[] invoiceStyle;
	private String invoiceGrouping;
	private Integer invoiceBatch;
	private Integer taxExempt;
	private String taxExemptReason;
	private String copiedFromQuoteNbrRev;
	
	public QuoteResponseItemDetail() {
		super();
	}
	
	public QuoteResponseItemDetail(Quote quote, User manager, Division division, Integer printCount) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		this();
		PropertyUtils.copyProperties(this, quote);
		this.managerLastName = manager.getLastName();
		this.managerFirstName = manager.getFirstName();
		this.managerEmail = manager.getEmail();
		this.divisionCode = division.getDivisionCode();
		//				this.printDate = printHistory.getPrintDate();
		//				this.quoteDate = printHistory.getQuoteDate();
		this.printCount = printCount;
				
	}

	public QuoteResponseItemDetail(Quote quote, User manager, Division division, Integer printCount, Job job) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		this(quote, manager, division, printCount);
		this.buildingType = job.getBuildingType();
		this.invoiceTerms = job.getInvoiceTerms();
		this.invoiceStyle = StringUtils.split(job.getInvoiceStyle(),",");
		this.invoiceGrouping = job.getInvoiceGrouping();
		this.invoiceBatch = job.getInvoiceBatch();
		this.taxExempt = job.getTaxExempt();
		this.taxExemptReason = job.getTaxExemptReason();
				
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
	//			@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	//@JsonSerialize(using=AnsiDateFormatter.class)
	//			public Date getPrintDate() {
	//				return this.printDate;
	//			}
	//			
	//			@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	//			public void setPrintDate(Date printDate) {
	//				this.printDate = printDate;
	//			}
	//			@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	//			public Date getQuoteDate() {
	//				return this.quoteDate;
	//			}
	//			
	//			@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	//			public void setQuoteDate(Date quoteDate) {
	//				this.quoteDate = quoteDate;
	//			}

	public String getDivisionCode() {
		return this.divisionCode;
	}
	public void setDivisionCode(String divisionCode) {
		this.divisionCode = divisionCode;
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
//	public void setPaymentTerms(String paymentTerms) {
//		this.paymentTerms = paymentTerms;
//	}
//	public String getPaymentTerms() {
//		return this.paymentTerms;
//	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public void setProposalDate(Date proposalDate) {
		this.proposalDate = proposalDate;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
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
	public String getManagerFirstName() {
		return managerFirstName;
	}
	public void setManagerFirstName(String managerFirstName) {
		this.managerFirstName = managerFirstName;
	}
	public String getManagerLastName() {
		return managerLastName;
	}
	public void setManagerLastName(String managerLastName) {
		this.managerLastName = managerLastName;
	}
	public String getManagerEmail() {
		return managerEmail;
	}
	public void setManagerEmail(String managerEmail) {
		this.managerEmail = managerEmail;
	}

	public Integer getPrintCount() {
		return printCount;
	}

	public void setPrintCount(Integer printCount) {
		this.printCount = printCount;
	}

	public String getBuildingType() {
		return buildingType;
	}

	public void setBuildingType(String buildingType) {
		this.buildingType = buildingType;
	}

	public String getInvoiceTerms() {
		return invoiceTerms;
	}

	public void setInvoiceTerms(String invoiceTerms) {
		this.invoiceTerms = invoiceTerms;
	}

	public String[] getInvoiceStyle() {
		return invoiceStyle;
	}

	public void setInvoiceStyle(String[] invoiceStyle) {
		this.invoiceStyle = invoiceStyle;
	}

	public String getInvoiceGrouping() {
		return invoiceGrouping;
	}

	public void setInvoiceGrouping(String invoiceGrouping) {
		this.invoiceGrouping = invoiceGrouping;
	}

	public Integer getInvoiceBatch() {
		return invoiceBatch;
	}

	public void setInvoiceBatch(Integer invoiceBatch) {
		this.invoiceBatch = invoiceBatch;
	}

	public Integer getTaxExempt() {
		return taxExempt;
	}

	public void setTaxExempt(Integer taxExempt) {
		this.taxExempt = taxExempt;
	}

	public String getTaxExemptReason() {
		return taxExemptReason;
	}

	public void setTaxExemptReason(String taxExemptReason) {
		this.taxExemptReason = taxExemptReason;
	}

	public String getCopiedFromQuoteNbrRev() {
		return copiedFromQuoteNbrRev;
	}

	public void setCopiedFromQuoteNbrRev(String copiedFromQuoteNbrRev) {
		this.copiedFromQuoteNbrRev = copiedFromQuoteNbrRev;
	}


}