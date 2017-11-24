package com.ansi.scilla.web.invoice.actionForm;

import com.ansi.scilla.web.common.actionForm.AbstractActionForm;

public class InvoiceLookupForm extends AbstractActionForm {

	private static final long serialVersionUID = 1L;
	
	public static final String KEY = "invoiceLookupForm";
	public static final String ID = "id";
	public static final String PPC_FILTER = "ppcFilter";
	
	private String id;
	private String ppcFilter;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPpcFilter() {
		return ppcFilter;
	}

	public void setPpcFilter(String ppcFilter) {
		this.ppcFilter = ppcFilter;
	}
	
	

}
