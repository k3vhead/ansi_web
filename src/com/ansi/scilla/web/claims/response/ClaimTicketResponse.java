package com.ansi.scilla.web.claims.response;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.web.claims.query.TicketAssignmentQuery;
import com.ansi.scilla.web.common.response.MessageResponse;

public class ClaimTicketResponse extends MessageResponse {

	private static final long serialVersionUID = 1L;

	private List<ClaimTicketResponseItem> claims;
	
	private ClaimTicketResponse(ResultSet rs) throws SQLException {
		super();
		this.claims = new ArrayList<ClaimTicketResponseItem>();
		while ( rs.next() ) {
			claims.add(new ClaimTicketResponseItem(rs)) ;
		}
	}

	public List<ClaimTicketResponseItem> getClaims() {
		return claims;
	}
	public void setClaims(List<ClaimTicketResponseItem> claims) {
		this.claims = claims;
	}


	public static ClaimTicketResponse makeFromTicket(Connection conn, Integer ticketId) throws SQLException {
		ResultSet rs = TicketAssignmentQuery.makeTicketQuery(conn, ticketId);
		return new ClaimTicketResponse(rs);
	}
	
	public static ClaimTicketResponse makeFromWasher(Connection conn, Integer washerId) throws SQLException {
		ResultSet rs = TicketAssignmentQuery.makeWasherQuery(conn, washerId);
		return new ClaimTicketResponse(rs);
	}

	public class ClaimTicketResponseItem extends ApplicationObject {
		private static final long serialVersionUID = 1L;
		
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
		
		public ClaimTicketResponseItem(ResultSet rs) {
			super();
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

		public Date getWorkDate() {
			return workDate;
		}

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
		
		
	}
}
