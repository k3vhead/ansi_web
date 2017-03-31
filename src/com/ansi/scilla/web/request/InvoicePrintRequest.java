package com.ansi.scilla.web.request;

import java.util.Date;

import com.ansi.scilla.common.jsonFormat.AnsiDateFormatter;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class InvoicePrintRequest extends AbstractRequest {

	private static final long serialVersionUID = 1L;

	private Integer divisionId;
	private Date printDate;
	private Date dueDate;
	
	@RequiredForAdd
	public Integer getDivisionId() {
		return divisionId;
	}
	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}
	@RequiredForAdd
	@JsonSerialize(using = AnsiDateFormatter.class)
	public Date getPrintDate() {
		return printDate;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy")
	public void setPrintDate(Date printDate) {
		this.printDate = printDate;
	}
	@RequiredForAdd
	@JsonSerialize(using = AnsiDateFormatter.class)
	public Date getDueDate() {
		return dueDate;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy")
	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}
	
	
	
	
	
	
}
