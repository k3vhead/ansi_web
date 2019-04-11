package com.ansi.scilla.web.claims.response;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.db.TicketClaim;
import com.ansi.scilla.common.db.User;
import com.fasterxml.jackson.annotation.JsonFormat;

public class DirectLaborItem extends ApplicationObject {

	public static final String WORK_DATE="work_date";
	public static final String VOLUME = "volume";
	public static final String DL_AMT = "dl_amt";
	public static final String HOURS = "hours";
	public static final String HOURS_TYPE = "hours_type";
	public static final String EXPENSE = "expense";
	public static final String NOTES = "notes";
	public static final String WASHER_FIRST_NAME = "washer_first_name";
	public static final String WASHER_LAST_NAME = "washer_last_name";
		
	protected static final String sql = "select ticket_claim.work_date,\n" + 
			"		ansi_user.last_name as washer_last_name,\n" + 
			"		ansi_user.first_name as washer_first_name,\n" + 
			"		ticket_claim.volume,\n" + 
			"		ticket_claim.dl_amt,\n" + 
			"		ticket_claim.hours,\n" + 
			"		'GAG What Goes Here' as hours_type,\n" + 
			"       -1.0 as expense,\n" +
			"		ticket_claim.notes\n" + 
			"from "+TicketClaim.TABLE+"\n" + 
			"left outer join "+User.TABLE+" on ansi_user.user_id=ticket_claim.washer_id\n" + 
			"where ticket_claim.ticket_id=?\n" +
			"order by ticket_claim.work_date asc, ansi_user.last_name asc, ansi_user.first_name asc";
	
	
	private static final long serialVersionUID = 1L;

	private Date workDate;
	private Double volume;
	private Double dlAmt;
	private Double hours;
	private Double expense;
	private String notes;
	private String hoursType;
	private String washerFirstName;
	private String washerLastName;
	
	public DirectLaborItem(ResultSet rs, ResultSetMetaData rsmd ) throws SQLException {
		super();
		for ( int i=0; i< rsmd.getColumnCount();i++) {
			int idx=i+1;
			if ( rsmd.getColumnName(idx).equalsIgnoreCase(WORK_DATE)) { this.workDate = new Date(rs.getDate(idx).getTime()); }
			if ( rsmd.getColumnName(idx).equalsIgnoreCase(VOLUME)) { this.volume = rs.getBigDecimal(idx).doubleValue(); }
			if ( rsmd.getColumnName(idx).equalsIgnoreCase(DL_AMT)) { this.dlAmt = rs.getBigDecimal(idx).doubleValue(); }
			if ( rsmd.getColumnName(idx).equalsIgnoreCase(EXPENSE)) { this.expense = rs.getBigDecimal(idx).doubleValue(); }
			if ( rsmd.getColumnName(idx).equalsIgnoreCase(HOURS)) { this.hours = rs.getBigDecimal(idx).doubleValue(); }
			if ( rsmd.getColumnName(idx).equalsIgnoreCase(NOTES)) { this.notes = rs.getString(idx); }
			if ( rsmd.getColumnName(idx).equalsIgnoreCase(HOURS_TYPE)) { this.hoursType = rs.getString(idx); }
			if ( rsmd.getColumnName(idx).equalsIgnoreCase(WASHER_FIRST_NAME)) { this.washerFirstName = rs.getString(idx); }
			if ( rsmd.getColumnName(idx).equalsIgnoreCase(WASHER_LAST_NAME)) { this.washerLastName = rs.getString(idx); }
		}
	}
	
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public Date getWorkDate() {
		return workDate;
	}
	public void setWorkDate(Date workDate) {
		this.workDate = workDate;
	}
	public Double getVolume() {
		return volume;
	}
	public void setVolume(Double volume) {
		this.volume = volume;
	}
	public Double getDlAmt() {
		return dlAmt;
	}
	public void setDlAmt(Double dlAmt) {
		this.dlAmt = dlAmt;
	}
	public Double getHours() {
		return hours;
	}
	public void setHours(Double hours) {
		this.hours = hours;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	public String getHoursType() {
		return hoursType;
	}
	public void setHoursType(String hoursType) {
		this.hoursType = hoursType;
	}
	public String getWasherFirstName() {
		return washerFirstName;
	}
	public void setWasherFirstName(String washerFirstName) {
		this.washerFirstName = washerFirstName;
	}
	public String getWasherLastName() {
		return washerLastName;
	}
	public void setWasherLastName(String washerLastName) {
		this.washerLastName = washerLastName;
	}
	public Double getExpense() {
		return expense;
	}
	public void setExpense(Double expense) {
		this.expense = expense;
	}

	public static List<DirectLaborItem> makeDirectLaborList(Connection conn, Integer ticketId) throws SQLException {
		List<DirectLaborItem> expenseList = new ArrayList<DirectLaborItem>();
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setInt(1, ticketId);
		ResultSet rs = ps.executeQuery();
		ResultSetMetaData rsmd = rs.getMetaData();
		while (rs.next()) {
			expenseList.add(new DirectLaborItem(rs,rsmd));
		}		
		return expenseList;
	}
}
