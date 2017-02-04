package com.ansi.scilla.web.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.web.exceptions.ResourceNotFoundException;

public class AnsiURL extends ApplicationWebObject {

	private static final long serialVersionUID = 1L;

	private String realm;
	private Integer id;
	private String command;
	private List<String> filterList;
	private Map<String, String[]> queryParameterMap;
	
	
	public AnsiURL(HttpServletRequest request, String expectedRealm, String[] expectedCommandList) throws ResourceNotFoundException {
		super();
		this.filterList = new ArrayList<String>();
		this.queryParameterMap = request.getParameterMap();

		
		String searchString = "/" + expectedRealm + "/";
		int idx = request.getRequestURI().indexOf(searchString);
		if ( idx < 0 ) {
			throw new ResourceNotFoundException();
		}
		String myString = request.getRequestURI().substring(idx + 1);
		String[] pieces = myString.split("/");

		this.realm = pieces[0];
		if ( ! this.realm.equals(expectedRealm)) {
			// this shouldn't ever happen, because we just looked for the realm, but it makes me happy to have it here
			throw new ResourceNotFoundException();
		}
		if ( pieces.length == 1 && expectedCommandList != null ) {
			// we have no command, but we're expecting one
			throw new ResourceNotFoundException();
		}
		if ( pieces.length > 1 ) {
			if ( StringUtils.isNumeric(pieces[1])) {
				this.id = Integer.valueOf(pieces[1]);
			} else {
				this.command = pieces[1];
				validateCommand(expectedCommandList);
			}
		}
		if (pieces.length > 2 ) {			
			for ( int i = 2; i < pieces.length; i++ ) {
				filterList.add(pieces[i]);
			}
		}		
	}

	/**
	 * Make sure the command from the URL is present if required, and is one of the valid values
	 * @param expectedCommandList
	 * @throws ResourceNotFoundException
	 */
	private void validateCommand(String[] expectedCommandList) throws ResourceNotFoundException {
		if ( StringUtils.isBlank(this.command)) {
			if ( expectedCommandList == null ) {
				// both command and expected are empty; we're getting what we expected
			} else {
				// no command, but we're expecting something
				throw new ResourceNotFoundException();
			}
		} else {
			if ( expectedCommandList == null ) {
				// we have a command, but we're not expecting anything
				throw new ResourceNotFoundException();
			} else {
				// we have a command, and we're expecting something; do they match?
				if ( ! ArrayUtils.contains(expectedCommandList, this.command)) {
					throw new ResourceNotFoundException();
				}
			}
		}
	}

	public String getRealm() {
		return realm;
	}

	public void setRealm(String realm) {
		this.realm = realm;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public List<String> getFilterList() {
		return filterList;
	}

	public void setFilterList(List<String> filterList) {
		this.filterList = filterList;
	}

	public Map<String, String[]> getQueryParameterMap() {
		return queryParameterMap;
	}

	public void setQueryParameterMap(Map<String, String[]> queryParameterMap) {
		this.queryParameterMap = queryParameterMap;
	}
	
	
}
