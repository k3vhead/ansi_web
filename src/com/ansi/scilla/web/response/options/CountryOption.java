package com.ansi.scilla.web.response.options;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.address.Country;
import com.ansi.scilla.common.address.State;

public class CountryOption extends ApplicationObject {
	private static final long serialVersionUID = 1L;
	private String abbrev;
	private String display;
	private List<State> stateList;

	public CountryOption(Country country) throws IOException {
		super();
		this.abbrev = country.abbrev();
		this.display = country.display();
		this.stateList = country.getStates();
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
	
	public List<State> getStateList(){
		return stateList;
	}
	

	
}
