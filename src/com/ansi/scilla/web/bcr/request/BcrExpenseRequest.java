package com.ansi.scilla.web.bcr.request;

import java.sql.Connection;

import com.ansi.scilla.web.common.request.AbstractRequest;
import com.ansi.scilla.web.common.response.WebMessages;

public class BcrExpenseRequest extends AbstractRequest {

	private static final long serialVersionUID = 1L;

	private Integer ticketId;
	private String serviceType;
	private String claimWeek;
	private Double volume;
	private String expenseType;
	public Integer getTicketId() {
		return ticketId;
	}
	public void setTicketId(Integer ticketId) {
		this.ticketId = ticketId;
	}
	public String getServiceType() {
		return serviceType;
	}
	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}
	public String getClaimWeek() {
		return claimWeek;
	}
	public void setClaimWeek(String claimWeek) {
		this.claimWeek = claimWeek;
	}
	public Double getVolume() {
		return volume;
	}
	public void setVolume(Double volume) {
		this.volume = volume;
	}
	public String getExpenseType() {
		return expenseType;
	}
	public void setExpenseType(String expenseType) {
		this.expenseType = expenseType;
	}
	
	public WebMessages validateAdd(Connection conn) {
		WebMessages webMessages = new WebMessages();
		return webMessages;
	}
	
	public WebMessages validateUpdate(Connection conn, Integer expenseId) {
		WebMessages webMessages = new WebMessages();
		return webMessages;
	}
	
	public WebMessages validateDelete(Connection conn, Integer expenseId) {
		WebMessages webMessages = new WebMessages();
		return webMessages;
	}
}
