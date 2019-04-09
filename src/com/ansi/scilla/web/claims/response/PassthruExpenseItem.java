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
import com.ansi.scilla.common.db.TicketClaimPassthru;
import com.fasterxml.jackson.annotation.JsonFormat;

public class PassthruExpenseItem extends ApplicationObject {

	public static final String WORK_DATE="work_date";
	public static final String PASSTHRU_EXPENSE_VOLUME ="passthru_expense_volume";
	public static final String NOTES = "notes";
	public static final String PASSTHRU_EXPENSE_TYPE = "passthru_expense_type";
	public static final String WASHER_FIRST_NAME = "washer_first_name";
	public static final String WASHER_LAST_NAME = "washer_last_name";
		
	protected static final String sql = "select ticket_claim_passthru.work_date, \n" + 
			"	ticket_claim_passthru.passthru_expense_volume,\n" + 
			"	ticket_claim_passthru.notes,\n" + 
			"	code.display_value as passthru_expense_type,\n" + 
			"	ansi_user.first_name as washer_first_name, \n" + 
			"	ansi_user.last_name as washer_last_name\n" + 
			"from ticket_claim_passthru\n" + 
			"inner join code on code.table_name='"+TicketClaimPassthru.TABLE+"' \n" + 
			"	and code.field_name='"+TicketClaimPassthru.PASSTHRU_EXPENSE_TYPE+"' \n" + 
			"	and code.value=ticket_claim_passthru.passthru_expense_type\n" + 
			"inner join ansi_user on ansi_user.user_id=ticket_claim_passthru.washer_id\n" + 
			"where ticket_claim_passthru.ticket_id=?\n" + 
			"order by ticket_claim_passthru.work_date asc";
	
	
	private static final long serialVersionUID = 1L;

	private Date workDate;
	private Double passthruExpenseVolume;
	private String notes;
	private String passthruExpenseType;
	private String washerFirstName;
	private String washerLastName;
	
	public PassthruExpenseItem(ResultSet rs, ResultSetMetaData rsmd ) throws SQLException {
		super();
		for ( int i=0; i< rsmd.getColumnCount();i++) {
			int idx=i+1;
			if ( rsmd.getColumnName(idx).equalsIgnoreCase(WORK_DATE)) { this.workDate = new Date(rs.getDate(idx).getTime()); }
			if ( rsmd.getColumnName(idx).equalsIgnoreCase(PASSTHRU_EXPENSE_VOLUME)) { this.passthruExpenseVolume = rs.getBigDecimal(idx).doubleValue(); }
			if ( rsmd.getColumnName(idx).equalsIgnoreCase(NOTES)) { this.notes = rs.getString(idx); }
			if ( rsmd.getColumnName(idx).equalsIgnoreCase(PASSTHRU_EXPENSE_TYPE)) { this.passthruExpenseType = rs.getString(idx); }
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

	public Double getPassthruExpenseVolume() {
		return passthruExpenseVolume;
	}

	public void setPassthruExpenseVolume(Double passthruExpenseVolume) {
		this.passthruExpenseVolume = passthruExpenseVolume;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getPassthruExpenseType() {
		return passthruExpenseType;
	}

	public void setPassthruExpenseType(String passthruExpenseType) {
		this.passthruExpenseType = passthruExpenseType;
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
	
	
	public static List<PassthruExpenseItem> makePassthruExpenseList(Connection conn, Integer ticketId) throws SQLException {
		List<PassthruExpenseItem> expenseList = new ArrayList<PassthruExpenseItem>();
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setInt(1, ticketId);
		ResultSet rs = ps.executeQuery();
		ResultSetMetaData rsmd = rs.getMetaData();
		while (rs.next()) {
			expenseList.add(new PassthruExpenseItem(rs,rsmd));
		}		
		return expenseList;
	}
}
