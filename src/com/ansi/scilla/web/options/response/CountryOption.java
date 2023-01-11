package com.ansi.scilla.web.options.response;

import java.io.IOException;
import java.util.List;

import com.ansi.scilla.common.address.Country;
import com.ansi.scilla.common.address.State;

public class CountryOption extends WebOption  {
	private static final long serialVersionUID = 1L;
	private String abbrev;
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
	
	public List<State> getStateList(){
		return stateList;
	}
	

	
}
