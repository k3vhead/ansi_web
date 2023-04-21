package com.ansi.scilla.web.invoice.request;

import java.sql.Connection;
import java.util.Date;

import com.ansi.scilla.web.common.request.AbstractRequest;
import com.ansi.scilla.web.common.request.RequestValidator;
import com.ansi.scilla.web.common.response.WebMessages;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

public class InvoiceGenerationRequest extends AbstractRequest {

	private static final long serialVersionUID = 1L;

	public static final String INVOICE_DATE = "invoiceDate";
	public static final String MONTHLY_FLAG = "monthlyFlag";
	public static final String DIVISION_ID = "divisionId";
	
	private Date invoiceDate;
	private Boolean monthlyFlag;
	private Integer[] divisionList;
	
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd", timezone="America/Chicago")
	public Date getInvoiceDate() {
		return invoiceDate;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd", timezone="America/Chicago")
	public void setInvoiceDate(Date invoiceDate) {
		this.invoiceDate = invoiceDate;
	}
	public Boolean getMonthlyFlag() {
		return monthlyFlag;
	}
	public void setMonthlyFlag(Boolean monthlyFlag) {
		this.monthlyFlag = monthlyFlag;
	}
	@JsonProperty("divisionId")
	public Integer[] getDivisionList() {
		return divisionList;
	}
	@JsonProperty("divisionId")
	public void setDivisionList(Integer[] divisionList) {
		this.divisionList = divisionList;
	}

	public WebMessages validate(Connection conn) {
		WebMessages webMessages = new WebMessages();
		RequestValidator.validateDate(webMessages, INVOICE_DATE, invoiceDate, true, (Date)null, (Date)null);
		if ( divisionList == null || divisionList.length == 0 ) {
			webMessages.addMessage(DIVISION_ID, "Select at least 1 division");
		}
		return webMessages;
	}
	
	
}
