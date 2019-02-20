package com.ansi.scilla.web.quote.response;

import com.ansi.scilla.web.common.response.MessageResponse;
import com.ansi.scilla.web.job.query.ContactItem;

public class NewQuoteDisplayResponse extends MessageResponse {

	private static final long serialVersionUID = 1L;

	private Integer quoteId;
	private String invoiceGrouping;
	private String invoiceStyle;
	private String buildingType;
	private Boolean invoiceBatch;
	private String invoiceTerms;
	private Boolean taxExempt;
	private String taxExemptReason;
	
	private ContactItem jobContact;
	private ContactItem siteContact;
	private ContactItem contractContact;
	private ContactItem billingContact;
	
	public Integer getQuoteId() {
		return quoteId;
	}
	public void setQuoteId(Integer quoteId) {
		this.quoteId = quoteId;
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
	public String getBuildingType() {
		return buildingType;
	}
	public void setBuildingType(String buildingType) {
		this.buildingType = buildingType;
	}
	public Boolean getInvoiceBatch() {
		return invoiceBatch;
	}
	public void setInvoiceBatch(Boolean invoiceBatch) {
		this.invoiceBatch = invoiceBatch;
	}
	public String getInvoiceTerms() {
		return invoiceTerms;
	}
	public void setInvoiceTerms(String invoiceTerms) {
		this.invoiceTerms = invoiceTerms;
	}
	public Boolean getTaxExempt() {
		return taxExempt;
	}
	public void setTaxExempt(Boolean taxExempt) {
		this.taxExempt = taxExempt;
	}
	public String getTaxExemptReason() {
		return taxExemptReason;
	}
	public void setTaxExemptReason(String taxExemptReason) {
		this.taxExemptReason = taxExemptReason;
	}
	public ContactItem getJobContact() {
		return jobContact;
	}
	public void setJobContact(ContactItem jobContact) {
		this.jobContact = jobContact;
	}
	public ContactItem getSiteContact() {
		return siteContact;
	}
	public void setSiteContact(ContactItem siteContact) {
		this.siteContact = siteContact;
	}
	public ContactItem getContractContact() {
		return contractContact;
	}
	public void setContractContact(ContactItem contractContact) {
		this.contractContact = contractContact;
	}
	public ContactItem getBillingContact() {
		return billingContact;
	}
	public void setBillingContact(ContactItem billingContact) {
		this.billingContact = billingContact;
	}

	
	
}
