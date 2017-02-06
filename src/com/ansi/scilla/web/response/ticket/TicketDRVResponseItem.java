package com.ansi.scilla.web.response.ticket;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.invoice.InvoiceTerm;
import com.ansi.scilla.common.queries.TicketDRVQuery;
import com.fasterxml.jackson.annotation.JsonFormat;

public class TicketDRVResponseItem extends ApplicationObject {

	private static final long serialVersionUID = 1L;
	
	private Integer ticketId;
	private String status;
	private String address1;
	private String name;
	private String city;
	private Date lastDone;
	private Date startDate;
	private Integer jobNum;
	private String frequency;
	private BigDecimal budget;
	private BigDecimal ppc;
	private String cod;
	
	public TicketDRVResponseItem() {
		super();
	}
	
	public TicketDRVResponseItem(TicketDRVQuery record) {
		this();
		this.ticketId = record.getTicketId();
		this.status = record.getStatus();
		this.address1 = record.getAddress1();
		this.budget = record.getBudget();
		this.city = record.getCity();
		if ( ! StringUtils.isBlank(record.getInvoiceStyle())) {
			this.cod = InvoiceTerm.valueOf(record.getInvoiceStyle()).display();
		}
		this.frequency = record.getJobFrequency();
		this.jobNum = record.getJobNbr();
		this.lastDone = record.getLastDone();
		this.name = record.getName();
		this.ppc = record.getPricePerCleaning();
		this.startDate = record.getStartDate();
	}
	
	public Integer getTicketId() {
		return ticketId;
	}
	public void setTicketId(Integer ticketId) {
		this.ticketId = ticketId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getAddress1() {
		return address1;
	}
	public void setAddress1(String address1) {
		this.address1 = address1;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy")
	public Date getLastDone() {
		return lastDone;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy")
	public void setLastDone(Date lastDone) {
		this.lastDone = lastDone;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy")
	public Date getStartDate() {
		return startDate;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy")
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Integer getJobNum() {
		return jobNum;
	}
	public void setJobNum(Integer jobNum) {
		this.jobNum = jobNum;
	}
	public String getFrequency() {
		return frequency;
	}
	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}
	public BigDecimal getBudget() {
		return budget;
	}
	public void setBudget(BigDecimal budget) {
		this.budget = budget;
	}
	public BigDecimal getPpc() {
		return ppc;
	}
	public void setPpc(BigDecimal ppc) {
		this.ppc = ppc;
	}
	public String getCod() {
		return cod;
	}
	public void setCod(String cod) {
		this.cod = cod;
	}
	
}
