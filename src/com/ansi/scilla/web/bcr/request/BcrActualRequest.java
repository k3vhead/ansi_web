package com.ansi.scilla.web.bcr.request;

import java.sql.Connection;
import java.util.Calendar;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.web.common.request.AbstractRequest;
import com.ansi.scilla.web.common.request.RequestValidator;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.struts.SessionUser;

public class BcrActualRequest extends AbstractRequest {

	private static final long serialVersionUID = 1L;

	public static final String TYPE_IS_ACTUAL = "actualDL";
	public static final String TYPE_IS_OM = "omDL";
	
	public static final String DIVISION_ID = "divisionId";
	public static final String CLAIM_WEEK = "claimWeek";
	public static final String CLAIM_YEAR = "claimYear";
	public static final String VALUE = "value";
	public static final String TYPE = "type";
	
	private Integer divisionId;
	private Integer claimWeek;
	private Integer claimYear;
	private Double value;
	private String type;
	private String claimWeeks;  //comma-delimited list of claim week numbers
	
	public Integer getDivisionId() {
		return divisionId;
	}
	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}
	public Integer getClaimWeek() {
		return claimWeek;
	}
	public void setClaimWeek(Integer claimWeek) {
		this.claimWeek = claimWeek;
	}
	public Integer getClaimYear() {
		return claimYear;
	}
	public void setClaimYear(Integer claimYear) {
		this.claimYear = claimYear;
	}
	public Double getValue() {
		return value;
	}
	public void setValue(Double value) {
		this.value = value;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}	
	public String getClaimWeeks() {
		return claimWeeks;
	}
	public void setClaimWeeks(String claimWeeks) {
		this.claimWeeks = claimWeeks;
	}
	
	public WebMessages validate(Connection conn, SessionUser sessionUser) throws Exception {
		WebMessages webMessages = new WebMessages();

		
		if ( ! this.type.equalsIgnoreCase(TYPE_IS_ACTUAL) && ! this.type.equalsIgnoreCase(TYPE_IS_OM) ) {
			webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "Invalidate Update Type");
		}
		
		// we're doing some finagling because we need special message text (besides "invalid value");
		WebMessages tempMessages = new WebMessages();
		RequestValidator.validateInteger(tempMessages, CLAIM_WEEK, claimWeek, 1, 53, true);
		if ( ! tempMessages.isEmpty() ) {
			webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "Invalid Claim Week. Reload page and try again");
		}
		
		Calendar calendar = Calendar.getInstance();
		tempMessages = new WebMessages();
		RequestValidator.validateInteger(tempMessages, CLAIM_YEAR, claimYear, calendar.get(Calendar.YEAR)-1, calendar.get(Calendar.YEAR)+1, true);
		if ( ! tempMessages.isEmpty() ) {
			webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "Invalid Claim Year. Reload page and try again");
		}
		
		tempMessages = new WebMessages();
		RequestValidator.validateId(conn, tempMessages, Division.TABLE, Division.DIVISION_ID, DIVISION_ID, divisionId, true, null);
		if ( ! tempMessages.isEmpty() ) {
			webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "Invalid Division. Reload page and try again");
		}
		
		tempMessages = new WebMessages();
		RequestValidator.validateDivisionUser(conn, tempMessages, divisionId, DIVISION_ID, sessionUser.getUserId(), DIVISION_ID, true);
		if ( ! tempMessages.isEmpty() ) {
			webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "User not assigned to division");
		}
		
		String[] validClaimWeeks = StringUtils.split(claimWeeks, ",");
		boolean isValidClaimWeek = false;
		for ( String validClaimWeek : validClaimWeeks ) {
			if ( claimWeek == Integer.valueOf(validClaimWeek).intValue() ) {
				isValidClaimWeek = true;
			}
		}
		if ( ! isValidClaimWeek ) {
			webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "Invalid Claim Week. Reload page and try again");
		}

	
		
		return webMessages;
	}
}
