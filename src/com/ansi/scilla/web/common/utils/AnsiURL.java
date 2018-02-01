package com.ansi.scilla.web.common.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.common.utils.PropertyNames;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.exceptions.ResourceNotFoundException;

/**
 * Parses a "standard" url following the pattern:<br>
 * /installation/realm/command/filter1/filter2?parm1=value1&amp;parm2=value2<br>
 * where installation is determined by the deployment paradigm, "realm" is the servlet we're working with 
 * (like "division" or "ticket"). The command will either be a numeric database keyfield or a
 * command like "list" or "add". Filters are optional, and are servlet-specific. Query String
 * parameters are also optional and servlet-specific. Both are parsed, but not validated.
 * 
 * Note that posted values (like the JSON in an "ADD") must be processed before invoking the constructor.
 * 
 * @author dclewis
 *
 */
public class AnsiURL extends ApplicationWebObject {

	private static final long serialVersionUID = 1L;

	private String realm;
	private Integer id;
	private String command;
	private List<String> filterList;
	private Map<String, String[]> queryParameterMap;
	
	/**
	 * Parse the URI from the given request.
	 * Note that posted values (like the JSON in and "ADD") must be processed before invoking this constructor
	 * 
	 * @param request The unchanged HTTP request object, after posted values are processed 
	 * @param expectedRealm Typically the servlet name, eg "ticket" or "division"
	 * @param expectedCommandList A list of valid commands like "list" or "add". A null value means no commands are accepted
	 * @throws ResourceNotFoundException Thrown if the realm is incorrect or the URI format is wrong, or if the command 
	 * does not validate against the list of expected values.
	 */
	public AnsiURL(HttpServletRequest request, String expectedRealm, String[] expectedCommandList) throws ResourceNotFoundException {
		super();
		
        SessionUser user = AppUtils.getSessionUser(request);
        String userEmail = user == null ? "n/a" : user.getEmail();
        Logger logger = LogManager.getLogger(PropertyNames.TRANSACTION_LOG.toString());
        logger.info("User: " + userEmail + "\tURL: " + request.getRequestURI());

		this.filterList = new ArrayList<String>();
		this.queryParameterMap = request.getParameterMap();

		
		String searchString = "/" + expectedRealm + "/";
		int idx = request.getRequestURI().indexOf(searchString);
		if ( idx < 0 ) {
			throw new ResourceNotFoundException("No realm");
		}
		String myString = request.getRequestURI().substring(idx + 1);
		String[] pieces = myString.split("/");

		this.realm = pieces[0];
		if ( ! this.realm.equals(expectedRealm)) {
			// this shouldn't ever happen, because we just looked for the realm, but it makes me happy to have it here
			throw new ResourceNotFoundException("Wrong realm");
		}
		if ( pieces.length == 1 && expectedCommandList != null ) {
			// we have no command, but we're expecting one
			throw new ResourceNotFoundException("Extra command");
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
				throw new ResourceNotFoundException("Missing Command");
			}
		} else {
			if ( expectedCommandList == null ) {
				// we have a command, but we're not expecting anything
				throw new ResourceNotFoundException("Extra command");
			} else {
				// we have a command, and we're expecting something; do they match?
				if ( ! ArrayUtils.contains(expectedCommandList, this.command)) {
					throw new ResourceNotFoundException("Invalid command");
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
