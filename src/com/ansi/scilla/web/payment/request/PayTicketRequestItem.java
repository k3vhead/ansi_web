package com.ansi.scilla.web.payment.request;
import java.math.BigDecimal;

import com.ansi.scilla.common.jsonFormat.AnsiCurrencyFormatter;
import com.ansi.scilla.common.queries.ReportQuery;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class PayTicketRequestItem extends ReportQuery {

	private static final long serialVersionUID = 1L;

	private Integer ticketId;
	private BigDecimal payVolume;
	private BigDecimal payTax;
	
	
	public PayTicketRequestItem() {
		super();
	}

	public Integer getTicketId() {
		return ticketId;
	}
	
	public void setTicketId(Integer ticketId) {
		this.ticketId = ticketId;
	}
	
	@JsonSerialize(using=AnsiCurrencyFormatter.class)
	public BigDecimal getPayVolume() {
		return payVolume;
	}
	
	public void setPayVolume(BigDecimal payVolume) {
		this.payVolume = payVolume;
	}
	
	@JsonSerialize(using=AnsiCurrencyFormatter.class)
	public BigDecimal getPayTax() {
		return payTax;
	}
	
	public void setPayTax(BigDecimal payTax) {
		this.payTax = payTax;
	}

	
	
}
