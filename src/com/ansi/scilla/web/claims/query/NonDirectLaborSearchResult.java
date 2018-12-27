package com.ansi.scilla.web.claims.query;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.ansi.scilla.common.ApplicationObject;

public class NonDirectLaborSearchResult extends ApplicationObject {

	private static final long serialVersionUID = 1L;

	public static final String DIVISION_ID = "division_id";
	public static final String DIV = "div";
	public static final String WASHER_ID = "washer_id";
	public static final String LAST_NAME = "last_name";
	public static final String FIRST_NAME = "first_name";
	public static final String WORK_DATE = "work_date";
	public static final String HOURS = "hours";
	public static final String HOURS_TYPE = "hours_type";
	public static final String NOTES = "notes";
	
	private Integer divisionId;
	private String div;
	private Integer washerId;
	private String lastName;
	private String firstName;
	private Date workDate;
	private Integer hours;
	private String hoursType;
	private String notes;
	
	public NonDirectLaborSearchResult() {
		super();
	}
	
	public NonDirectLaborSearchResult(ResultSet rs) throws SQLException {
		this();
		this.divisionId=123;
		this.div = "99-XX88";
		this.washerId = rs.getInt(WASHER_ID);		
		this.lastName = rs.getString(LAST_NAME);	
		this.firstName = rs.getString(FIRST_NAME);	
		java.sql.Date workDate = rs.getDate(WORK_DATE);
		this.workDate = new Date(workDate.getTime());	
		this.hours = rs.getInt(HOURS);	
		this.hoursType = rs.getString(HOURS_TYPE);	
		this.notes = rs.getString(NOTES);	
		
	}
	
	public Integer getDivisionId() {
		return divisionId;
	}

	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}

	public String getDiv() {
		return div;
	}

	public void setDiv(String div) {
		this.div = div;
	}

	public Integer getWasherId() {
		return washerId;
	}
	public void setWasherId(Integer washerId) {
		this.washerId = washerId;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public Date getWorkDate() {
		return workDate;
	}
	public void setWorkDate(Date workDate) {
		this.workDate = workDate;
	}
	public Integer getHours() {
		return hours;
	}
	public void setHours(Integer hours) {
		this.hours = hours;
	}
	public String getHoursType() {
		return hoursType;
	}
	public void setHoursType(String hoursType) {
		this.hoursType = hoursType;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	
	
}
