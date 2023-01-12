package com.ansi.scilla.web.division.response;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

import org.apache.commons.lang3.time.DateUtils;

import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.web.common.response.MessageResponse;

public class DivisionCloseResponse extends MessageResponse {

	private static final long serialVersionUID = 1L;

	public static final String DIVISION_ID = "divisionId";
	public static final String DIVISION_DISPLAY = "divisionDisplay";
	public static final String CLOSED_THRU_DATE = "closedThruDate";
	public static final String ACT_CLOSE_DATE = "actCloseDate";
	public static final String NEXT_CLOSE_DATE = "nextCloseDate";
	
	private Integer divisionId;
	private String divisionDisplay;
	private Calendar closedThruDate;
	private Calendar actCloseDate;
	private Calendar nextCloseDate;
	
	public DivisionCloseResponse() {
		super();
	}
	
	public DivisionCloseResponse(Connection conn, Integer divisionId) throws Exception {
		this();
		makeDivision(conn, divisionId);
		makeCalendar(conn);
	}

	private void makeDivision(Connection conn, Integer divisionId) throws Exception {
		Division division = new Division();
		division.setDivisionId(divisionId);
		division.selectOne(conn);
		this.actCloseDate = DateUtils.toCalendar(division.getActCloseDate());
		this.divisionDisplay = division.getDivisionDisplay();		
	}

	private void makeCalendar(Connection conn) throws SQLException {
		java.sql.Date actCloseDate = new java.sql.Date(this.actCloseDate.getTimeInMillis());

		PreparedStatement nextCloseStatement = conn.prepareStatement("select top(1) ansi_date \n" + 
				"from ansi_calendar\n" + 
				"where ansi_date > ? and date_type='DIVISION_CLOSE'\n" + 
				"order by ansi_date asc");
		nextCloseStatement.setDate(1, actCloseDate);
		ResultSet nextClose = nextCloseStatement.executeQuery();
		if ( nextClose.next() ) {
			this.nextCloseDate = DateUtils.toCalendar(nextClose.getDate("ansi_date"));
		}
		nextClose.close();
		
		PreparedStatement lastCloseStatement = conn.prepareStatement("select top(1) ansi_date \n" + 
				"from ansi_calendar\n" + 
				"where ansi_date < ? and date_type='DIVISION_CLOSE'\n" + 
				"order by ansi_date desc");
		nextCloseStatement.setDate(1, actCloseDate);
		ResultSet lastClose = lastCloseStatement.executeQuery();
		if ( lastClose.next() ) {
			this.closedThruDate = DateUtils.toCalendar(lastClose.getDate("ansi_date"));
		}
		lastClose.close();
	}

	public Integer getDivisionId() {
		return divisionId;
	}

	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}

	public String getDivisionDisplay() {
		return divisionDisplay;
	}

	public void setDivisionDisplay(String divisionDisplay) {
		this.divisionDisplay = divisionDisplay;
	}

	public Calendar getClosedThruDate() {
		return closedThruDate;
	}

	public void setClosedThruDate(Calendar closedThruDate) {
		this.closedThruDate = closedThruDate;
	}

	public Calendar getActCloseDate() {
		return actCloseDate;
	}

	public void setActCloseDate(Calendar actCloseDate) {
		this.actCloseDate = actCloseDate;
	}

	public Calendar getNextCloseDate() {
		return nextCloseDate;
	}

	public void setNextCloseDate(Calendar nextCloseDate) {
		this.nextCloseDate = nextCloseDate;
	}
	
}
