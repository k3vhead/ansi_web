package com.ansi.scilla.web.quote.response;

import com.ansi.scilla.web.common.response.MessageResponse;

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
	
	
}
