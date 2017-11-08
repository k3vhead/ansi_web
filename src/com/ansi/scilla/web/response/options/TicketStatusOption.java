package com.ansi.scilla.web.response.options;

import java.util.ArrayList;
import java.util.List;

import com.ansi.scilla.common.jobticket.TicketStatus;

public class TicketStatusOption extends WebOption  {
	private static final long serialVersionUID = 1L;
	private String code;
	private String name;
	private List<String> nextValues;
	private List<String> previousValues;
	private List<String> allPreviousValues;
	public TicketStatusOption(TicketStatus ticketStatus) {
		super();
		this.code = ticketStatus.code();
		this.display = ticketStatus.display();
		this.nextValues = new ArrayList<String>();
		this.name = ticketStatus.name();
		for ( TicketStatus ts : ticketStatus.nextValues() ) {
			this.nextValues.add(ts.code());
		}
		this.previousValues = new ArrayList<String>();
		for ( TicketStatus ts : ticketStatus.previousValues() ) {
			this.previousValues.add(ts.code());
		}
		this.allPreviousValues = new ArrayList<String>();
		for ( TicketStatus ts : ticketStatus.allPreviousValues() ) {
			this.allPreviousValues.add(ts.code());
		}
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<String> getNextValues() {
		return nextValues;
	}
	public void setNextValues(List<String> nextValues) {
		this.nextValues = nextValues;
	}
	public List<String> getPreviousValues() {
		return previousValues;
	}
	public void setPreviousValues(List<String> previousValues) {
		this.previousValues = previousValues;
	}
	public List<String> getAllPreviousValues() {
		return allPreviousValues;
	}
	public void setAllPreviousValues(List<String> allPreviousValues) {
		this.allPreviousValues = allPreviousValues;
	}

}
