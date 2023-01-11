package com.ansi.scilla.web.options.response;

import com.ansi.scilla.common.calendar.CalendarDateType;

public class CalendarTypeOption extends WebOption  {
	private static final long serialVersionUID = 1L;
	private String code;
	
	public CalendarTypeOption(CalendarDateType ticketStatus) {
		super();
		this.code = ticketStatus.toString();
		this.display = ticketStatus.description();
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}


}
