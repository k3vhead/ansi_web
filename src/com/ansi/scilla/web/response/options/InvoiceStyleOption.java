package com.ansi.scilla.web.response.options;

import com.ansi.scilla.common.invoice.InvoiceStyle;
import com.ansi.scilla.web.common.ApplicationWebObject;

public class InvoiceStyleOption extends ApplicationWebObject {


	private static final long serialVersionUID = 1L;
	
	private String abbrev;
	private String display;

	public InvoiceStyleOption(InvoiceStyle j) {
		this.abbrev = j.toString();
		this.display = j.display();
	}

	public String getAbbrev() {
		return abbrev;
	}

	public void setAbbrev(String abbrev) {
		this.abbrev = abbrev;
	}

	public String getDisplay() {
		return display;
	}

	public void setDisplay(String display) {
		this.display = display;
	}

}
