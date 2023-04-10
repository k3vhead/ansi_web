package com.ansi.scilla.web.common.response;

import java.sql.ResultSet;

import com.ansi.scilla.common.ApplicationObject;

public abstract class AbstractAutoCompleteItem extends ApplicationObject {

	private static final long serialVersionUID = 1L;
	
	protected Integer id;
	protected String label;   // this is what shows up in the auto-complete dropdown
	protected String value;	  // this is what goes into the text form field

	public AbstractAutoCompleteItem(ResultSet rs) throws Exception {
		super();
		make(rs);
	}
	
	
	public Integer getId() {
		return id;
	}


	public void setId(Integer id) {
		this.id = id;
	}


	public String getLabel() {
		return label;
	}


	public void setLabel(String label) {
		this.label = label;
	}


	public String getValue() {
		return value;
	}


	public void setValue(String value) {
		this.value = value;
	}


	abstract protected void make(ResultSet rs) throws Exception;
	
}
