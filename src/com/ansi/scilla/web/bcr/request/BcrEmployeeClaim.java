package com.ansi.scilla.web.bcr.request;

import java.sql.Connection;

import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.web.common.request.RequestValidator;
import com.ansi.scilla.web.common.response.WebMessages;

/**
 * Represents one employee row on the split claim modal
 *
 */
public class BcrEmployeeClaim extends ApplicationObject {
	private static final long serialVersionUID = 1L;
	
	public static final String EMPLOYEE = "employee";
	public static final String DL_AMT = "dlAmt";
	public static final String VOLUME_CLAIMED = "volumeClaimed";
	public static final String LABOR_NOTES = "laborNotes";

	private Integer index;
	private String employee;
	private Double dlAmt;
	private Double volumeClaimed;
	private String laborNotes;
	
	public Integer getIndex() {
		return index;
	}
	public void setIndex(Integer index) {
		this.index = index;
	}
	public String getEmployee() {
		return employee;
	}
	public void setEmployee(String employee) {
		this.employee = employee;
	}
	public Double getDlAmt() {
		return dlAmt;
	}
	public void setDlAmt(Double dlAmt) {
		this.dlAmt = dlAmt;
	}
	public Double getVolumeClaimed() {
		return volumeClaimed;
	}
	public void setVolumeClaimed(Double volumeClaimed) {
		this.volumeClaimed = volumeClaimed;
	}
	public String getLaborNotes() {
		return laborNotes;
	}
	public void setLaborNotes(String laborNotes) {
		this.laborNotes = laborNotes;
	}
	
	
	public boolean hasLaborClaim() {
		boolean hasLaborClaim = false;
		if ( ! StringUtils.isBlank(this.employee) ) { hasLaborClaim = true; }
		if ( ! StringUtils.isBlank(this.laborNotes) ) { hasLaborClaim = true; }
		if ( this.dlAmt != null ) { hasLaborClaim = true; }
		if ( this.volumeClaimed != null ) { hasLaborClaim = true; }
		return hasLaborClaim;
	}
	
	
	public WebMessages validate(Connection conn) {
		WebMessages webMessages = new WebMessages();
		RequestValidator.validateString(webMessages, EMPLOYEE, this.employee, 150, true, "Employee Name");
		RequestValidator.validateNumber(webMessages, DL_AMT, this.dlAmt, null, null, true, "D/L Amount");
		RequestValidator.validateNumber(webMessages, VOLUME_CLAIMED, this.volumeClaimed, null, null, true, "Volume Claimed");
		
		/*
		if ( ! webMessages.containsKey(TICKET_ID) ) {
			// we use the ticket to find the job to find the valid equipment tags, so a ticket error makes an
			// equipment tag validation impossible.
			RequestValidator.validateEquipmentTags(conn, webMessages, CLAIMED_EQUIPMENT, this.ticketId, this.claimedEquipment, false, "Equipment");
		}
		*/
		return webMessages;
	}
	
}
