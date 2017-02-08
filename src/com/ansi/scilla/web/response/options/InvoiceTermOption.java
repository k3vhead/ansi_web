package com.ansi.scilla.web.response.options;

import com.ansi.scilla.common.invoice.InvoiceTerm;
import com.ansi.scilla.web.common.ApplicationWebObject;

public class InvoiceTermOption extends ApplicationWebObject {


	private static final long serialVersionUID = 1L;
	
	private String abbrev;
	private String display;

	public InvoiceTermOption(InvoiceTerm j) {
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
