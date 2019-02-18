package com.ansi.scilla.web.quote.actionForm;

import com.ansi.scilla.web.common.actionForm.AbstractActionForm;

public class NewQuoteDisplayForm extends AbstractActionForm {
	
	private static final long serialVersionUID = 1L;
	
	public static final String KEY = "newQuoteDisplayForm";
	
	private String quoteId;
	private String invoiceGrouping;
	private String invoiceStyle;
	private String buildingType;
	private String invoiceBatch;
	private String invoiceTerms;
	private String taxExempt;
	private String taxExemptReason;
	public String getQuoteId() {
		return quoteId;
	}
	public void setQuoteId(String quoteId) {
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
	public String getInvoiceBatch() {
		return invoiceBatch;
	}
	public void setInvoiceBatch(String invoiceBatch) {
		this.invoiceBatch = invoiceBatch;
	}
	public String getInvoiceTerms() {
		return invoiceTerms;
	}
	public void setInvoiceTerms(String invoiceTerms) {
		this.invoiceTerms = invoiceTerms;
	}
	public String getTaxExempt() {
		return taxExempt;
	}
	public void setTaxExempt(String taxExempt) {
		this.taxExempt = taxExempt;
	}
	public String getTaxExemptReason() {
		return taxExemptReason;
	}
	public void setTaxExemptReason(String taxExemptReason) {
		this.taxExemptReason = taxExemptReason;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	


	
}
