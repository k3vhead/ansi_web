package com.ansi.scilla.web.invoice.request;

import java.util.Date;

import com.ansi.scilla.common.jsonFormat.AnsiDateFormatter;
import com.ansi.scilla.web.common.request.AbstractRequest;
import com.ansi.scilla.web.common.request.RequiredForAdd;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class InvoiceGenerationRequest extends AbstractRequest {

	private static final long serialVersionUID = 1L;

	private Date invoiceDate;
	private Boolean monthlyFlag;
	
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	@RequiredForAdd
	public Date getInvoiceDate() {
		return invoiceDate;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
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
