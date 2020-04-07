package com.ansi.scilla.web.division.request;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.web.common.request.AbstractRequest;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.exceptions.MissingRequiredDataException;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class DivisionUncloseRequest extends AbstractRequest {

	private static final long serialVersionUID = 1L;

	public static final String DIVISION_ID = "divisionId";
	public static final String ACT_CLOSE_DATE = "actCloseDate";
	
	private Integer divisionId;
	private Calendar actCloseDate;

	public DivisionUncloseRequest() {
		super();
	}

	public Integer getDivisionId() {
		return divisionId;
	}

	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}
	
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public Calendar getActCloseDate() {
		return actCloseDate;
	}

	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public void setActCloseDate(Calendar actCloseDate) {
		this.actCloseDate = actCloseDate;
	}

	public WebMessages validate(Connection conn) throws Exception {
		WebMessages webMessages = new WebMessages();
		
		try {
			Division division = validateDivision(conn);
			if ( division.getActCloseDate() == null ) {
				webMessages.addMessage(DIVISION_ID, "Cannot Unclose a division that hasn't been closed");
			} else {
//				validateDivisionDate(conn, webMessages, division);
				if ( webMessages.isEmpty() && actCloseDate != null ) {
					validateActCloseDate(conn, webMessages);
				}
			}
		} catch ( MissingRequiredDataException e) {
			webMessages.addMessage(DIVISION_ID,"Required entry");
		} catch ( RecordNotFoundException e ) {
			webMessages.addMessage(DIVISION_ID,"Invalid Division ID");
		}
		
		return webMessages;
	}

	private Division validateDivision(Connection conn) throws RecordNotFoundException, MissingRequiredDataException, Exception {
		if ( this.divisionId == null ) {
			throw new MissingRequiredDataException();
		}
		Division division = new Division();
		division.setDivisionId(this.divisionId);
		division.selectOne(conn);
		return division;
	}

//	private void validateDivisionDate(Connection conn, WebMessages webMessages, Division division) throws SQLException {
//		String sql = "select top(1) ansi_calendar.ansi_date \n" + 
//				"from ansi_calendar\n" + 
//				"where ansi_calendar.date_type='DIVISION_CLOSE' \n" + 
//				"and ansi_calendar.ansi_date > ?\n" + 
//				"order by ansi_date asc";
//		PreparedStatement ps = conn.prepareStatement(sql);
//		ps.setDate(1, new java.sql.Date(division.getActCloseDate().getTime()));
//		ResultSet rs = ps.executeQuery();
//		if ( rs.next() ) {
//			Date maxDate = rs.getDate("ansi_date");
//			
//		} 
//		rs.close();
//	}

	
	private void validateActCloseDate(Connection conn, WebMessages webMessages) throws SQLException {
		String sql = "select top(2) ansi_calendar.ansi_date \n" + 
				"		from ansi_calendar\n" + 
				"		where ansi_calendar.date_type='DIVISION_CLOSE' \n" + 
				"		and ansi_calendar.ansi_date < (select act_close_date from division where division_id=?)\n" + 
				"		order by ansi_date desc";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setInt(1, this.divisionId);
		ResultSet rs = ps.executeQuery();
		if ( rs.next() ) {
			// this is the first record -- the latest close date in the past
			Date maxDate = rs.getDate("ansi_date");
			rs.next();
			// this is the 2nd record -- the 2nd-latest close date in the past
			Date minDate = rs.getDate("ansi_date");
			if ( this.actCloseDate.after(maxDate) || this.actCloseDate.before(minDate)) {
				webMessages.addMessage(ACT_CLOSE_DATE, "Close Date Out of Range");
			}
		} 
		rs.close();
	}

	
	
	

	

}
