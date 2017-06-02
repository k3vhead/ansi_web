package com.ansi.scilla.web.response.options;

import com.ansi.scilla.common.invoice.InvoiceStyle;

public class InvoiceStyleOption extends WebOption  {


	private static final long serialVersionUID = 1L;
	
	private String abbrev;

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



}
