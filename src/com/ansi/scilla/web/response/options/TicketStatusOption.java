package com.ansi.scilla.web.response.options;

import java.util.ArrayList;
import java.util.List;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.jobticket.TicketStatus;

public class TicketStatusOption extends WebOption  {
	private static final long serialVersionUID = 1L;
	private String code;
	private List<String> nextValues;
	public TicketStatusOption(TicketStatus ticketStatus) {
		super();
		this.code = ticketStatus.code();
		this.display = ticketStatus.display();
		this.nextValues = new ArrayList<String>();
		for ( TicketStatus ts : ticketStatus.nextValues() ) {
			this.nextValues.add(ts.code());
		}
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public List<String> getNextValues() {
		return nextValues;
	}
	public void setNextValues(List<String> nextValues) {
		this.nextValues = nextValues;
	}

}
