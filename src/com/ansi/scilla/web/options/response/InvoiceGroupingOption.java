package com.ansi.scilla.web.options.response;

import com.ansi.scilla.common.invoice.InvoiceGrouping;

public class InvoiceGroupingOption extends WebOption  {


	private static final long serialVersionUID = 1L;
	
	private String abbrev;

	public InvoiceGroupingOption(InvoiceGrouping j) {
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
