package com.ansi.scilla.web.claims.query;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.claims.WorkHoursType;
import com.fasterxml.jackson.annotation.JsonFormat;

public class NonDirectLaborSearchResult extends ApplicationObject {

	private static final long serialVersionUID = 1L;

	public static final String LABOR_ID = "labor_id";
	public static final String DIVISION_ID = "division_id";
	public static final String DIV = "div";
	public static final String WASHER_ID = "washer_id";
	public static final String LAST_NAME = "last_name";
	public static final String FIRST_NAME = "first_name";
	public static final String WORK_DATE = "work_date";
	public static final String HOURS = "hours";
	public static final String HOURS_TYPE = "hours_type";
	public static final String NOTES = "notes";
	public static final String ACT_PAYOUT_AMT = "act_payout_amt";
	public static final String CALC_PAYOUT_AMT = "calc_payout_amt";
	
	private Integer laborId;
	private Integer divisionId;
	private String div;
	private Integer washerId;
	private String lastName;
	private String firstName;
	private Date workDate;
	private Integer hours;
	private String hoursType;
	private String hoursDescription;
	private String notes;
	private BigDecimal actPayoutAmt;
	private BigDecimal calcPayoutAmt;
	
	public NonDirectLaborSearchResult() {
		super();
	}
	
	public NonDirectLaborSearchResult(ResultSet rs) throws SQLException {
		this();
		this.laborId = rs.getInt(LABOR_ID);
		this.divisionId=rs.getInt(DIVISION_ID);
		this.div = rs.getString(DIV);
		this.washerId = rs.getInt(WASHER_ID);		
		this.lastName = rs.getString(LAST_NAME);	
		this.firstName = rs.getString(FIRST_NAME);	
		java.sql.Date workDate = rs.getDate(WORK_DATE);
		this.workDate = new Date(workDate.getTime());	
		this.hours = rs.getInt(HOURS);	
		this.hoursType = rs.getString(HOURS_TYPE);	
		this.notes = rs.getString(NOTES);	
		if ( this.hoursType != null ) {
			try {
				WorkHoursType workHoursType = WorkHoursType.valueOf(this.hoursType);
				this.hoursDescription = workHoursType.getDescription();
			} catch ( IllegalArgumentException e) {
				this.hoursDescription = "Invalid value: " + this.hoursType;
			}
		}
		this.actPayoutAmt = rs.getBigDecimal(ACT_PAYOUT_AMT);
		this.calcPayoutAmt = rs.getBigDecimal(CALC_PAYOUT_AMT);
		
	}
	
	public Integer getLaborId() {
		return laborId;
	}

	public void setLaborId(Integer laborId) {
		this.laborId = laborId;
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
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public Date getWorkDate() {
		return workDate;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
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
	public String getHoursDescription() {
		return hoursDescription;
	}

	public void setHoursDescription(String hoursDescription) {
		this.hoursDescription = hoursDescription;
	}

	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	public Integer getWeek() {
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.setTime(this.workDate);
		return calendar.get(Calendar.WEEK_OF_YEAR);
	}

	public BigDecimal getActPayoutAmt() {
		return actPayoutAmt;
	}

	public void setActPayoutAmt(BigDecimal actPayoutAmt) {
		this.actPayoutAmt = actPayoutAmt;
	}

	public BigDecimal getCalcPayoutAmt() {
		return calcPayoutAmt;
	}

	public void setCalcPayoutAmt(BigDecimal calcPayoutAmt) {
		this.calcPayoutAmt = calcPayoutAmt;
	}
	
	
}
