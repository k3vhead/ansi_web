package com.ansi.scilla.web.claims.common;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.web.claims.query.TicketAssignmentQuery;
import com.ansi.scilla.web.common.request.RequestValidator;
import com.ansi.scilla.web.common.response.WebMessages;
import com.fasterxml.jackson.annotation.JsonFormat;

public class ClaimTicketItem extends ApplicationObject {
	private static final long serialVersionUID = 1L;
	
	public static final String TICKET_ID = "ticketId"; 
	public static final String WASHER_ID = "washerId";
	public static final String FIRST_NAME = "firstName"; 
	public static final String LAST_NAME = "lastName";
	public static final String CLAIM_ID = "claimId"; 
	public static final String WORK_DATE = "workDate"; 
	public static final String VOLUME = "volume"; 
	public static final String DL_AMT = "dlAmt";
	public static final String HOURS = "hours"; 
	public static final String NOTES = "notes";
	
	private Integer ticketId; 
	private Integer washerId;
	private String firstName; 
	private String lastName;
	private Integer claimId; 
	private Date workDate; 
	private BigDecimal volume; 
	private BigDecimal dlAmt;
	private BigDecimal hours; 
	private String notes;
	
	public ClaimTicketItem() {
		super();
	}
	
	public ClaimTicketItem(ResultSet rs) throws SQLException {
		this();
		this.ticketId = rs.getInt(TicketAssignmentQuery.TICKET_ID);
		this.washerId = rs.getInt(TicketAssignmentQuery.WASHER_ID);
		this.firstName = rs.getString(TicketAssignmentQuery.FIRST_NAME);
		this.lastName = rs.getString(TicketAssignmentQuery.LAST_NAME);
		Object claimId = rs.getObject(TicketAssignmentQuery.CLAIM_ID);
		if ( claimId == null ) {
			this.workDate = new Date();
		} else {
			this.claimId = (Integer)claimId;
			this.workDate = rs.getDate(TicketAssignmentQuery.WORK_DATE);
			this.volume = rs.getBigDecimal(TicketAssignmentQuery.VOLUME); 
			this.dlAmt = rs.getBigDecimal(TicketAssignmentQuery.DL_AMT); 
			this.hours = rs.getBigDecimal(TicketAssignmentQuery.HOURS); 
			this.notes = rs.getString(TicketAssignmentQuery.NOTES); 
		}
		
		
	}

	public Integer getTicketId() {
		return ticketId;
	}

	public void setTicketId(Integer ticketId) {
		this.ticketId = ticketId;
	}

	public Integer getWasherId() {
		return washerId;
	}

	public void setWasherId(Integer washerId) {
		this.washerId = washerId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Integer getClaimId() {
		return claimId;
	}

	public void setClaimId(Integer claimId) {
		this.claimId = claimId;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public Date getWorkDate() {
		return workDate;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public void setWorkDate(Date workDate) {
		this.workDate = workDate;
	}
	
	public BigDecimal getVolume() {
		return volume;
	}

	public void setVolume(BigDecimal volume) {
		this.volume = volume;
	}

	public BigDecimal getDlAmt() {
		return dlAmt;
	}

	public void setDlAmt(BigDecimal dlAmt) {
		this.dlAmt = dlAmt;
	}

	public BigDecimal getHours() {
		return hours;
	}

	public void setHours(BigDecimal hours) {
		this.hours = hours;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}
	
	/**
	 * Since this is an item in a list, the rowNum is used as a qualifier, to indentify which (for example) particular
	 * VOLUME is invalid.  If this item is to be validated individually -- not part of a list -- the rowNum can be omitted.
	 * @param conn
	 * @param rowNum - Used to make the field name unique when validating a list of claims.
	 * @return
	 * @throws Exception
	 */
	public WebMessages validateAdd(Connection conn, Integer rowNum) throws Exception {
		WebMessages webMessages = new WebMessages();
		String qualifier = rowNum == null ? "" : "-" + rowNum;
		RequestValidator.validateTicketId(conn, webMessages, TICKET_ID + qualifier, this.ticketId, true);
		RequestValidator.validateWasherId(conn, webMessages, WASHER_ID + qualifier, this.washerId, true);
		RequestValidator.validateDate(webMessages, WORK_DATE + qualifier, this.workDate, true, null, null);
		RequestValidator.validateBigDecimal(webMessages, ClaimTicketItem.VOLUME + qualifier, this.volume, null, null, true);
		RequestValidator.validateBigDecimal(webMessages, ClaimTicketItem.DL_AMT + qualifier, this.dlAmt, null, null, true);
		RequestValidator.validateBigDecimal(webMessages, ClaimTicketItem.HOURS + qualifier, this.hours, null, null, true);
		
		return webMessages;		
	}
	
}
