package com.ansi.scilla.web.request;

import java.util.Date;

import com.ansi.scilla.common.jsonFormat.AnsiDateFormatter;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class InvoiceGenerationRequest extends AbstractRequest {

	private static final long serialVersionUID = 1L;

	private Date invoiceDate;
	private Boolean monthlyFlag;
	
	@JsonSerialize(using = AnsiDateFormatter.class)
	@RequiredForAdd
	public Date getInvoiceDate() {
		return invoiceDate;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy")
	public void setInvoiceDate(Date invoiceDate) {
		this.invoiceDate = invoiceDate;
	}
	public Boolean getMonthlyFlag() {
		return monthlyFlag;
	}
	public void setMonthlyFlag(Boolean monthlyFlag) {
		this.monthlyFlag = monthlyFlag;
	}
	
	
}
