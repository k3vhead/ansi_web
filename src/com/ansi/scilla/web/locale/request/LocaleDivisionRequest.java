package com.ansi.scilla.web.locale.request;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;

import com.ansi.scilla.common.db.Address;
import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.common.db.Locale;
import com.ansi.scilla.web.common.request.AbstractRequest;
import com.ansi.scilla.web.common.request.RequestValidator;
import com.ansi.scilla.web.common.response.WebMessages;
import com.fasterxml.jackson.annotation.JsonFormat;

public class LocaleDivisionRequest extends AbstractRequest {

	private static final long serialVersionUID = 1L;

	public static final String ACTION_IS_ADD = "add";
	public static final String ACTION_IS_UPDATE = "update";
	public static final String ACTION_IS_DELETE = "delete";
	
	public static final String ACTION = "action";
	public static final String DIVISION_ID = "divisionId";
	public static final String LOCALE_ID = "localeId";
	public static final String EFF_START_DATE = "effectiveStartDate";
	public static final String EFF_STOP_DATE = "effectiveStopDate"; 
	public static final String ADDRESS_ID = "addressId"; 
	public static final String NAME = "name";
	public static final String LOCALE_STATE_NAME = "stateName";
	public static final String LOCALE_TYPE_ID = "localeTypeId";
	public static final String DIVISION_CODE = "divisionCode";
	public static final String DIVISION_NBR = "divisionNbr";
	public static final String DIVISION_DISPLAY = "divisionDisplay";
	public static final String DESCRIPTION = "description"; 
	public static final String ADDRESS1 = "address1";
	public static final String ADDRESS2 = "address2";
	public static final String CITY = "city";
	public static final String STATE = "state";
	public static final String ZIP = "zip";
	
	private String action;
	
	private Integer divisionId;
	private String divisionCode;
	private Integer divisionNbr;
	
	private Integer localeId;
	private String name; //locale name
	private String stateName;//locale state name
	
	private Date effectiveStartDate;
	private Date effectiveStopDate;
	
	private Integer addressId;
	private String address1;
	private String address2;
	private String city;
	private String state;
	private String zip;
	
	
	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public Integer getLocaleId() {
		return localeId;
	}
	
	public void setLocaleId(Integer localeId) {
		this.localeId = localeId;
	}
	
	public Integer getDivisionId() {
		return divisionId;
	}

	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}

	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public Date getEffectiveStartDate() {
		return effectiveStartDate;
	}

	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public void setEffectiveStartDate(Date effectiveStartDate) {
		this.effectiveStartDate = effectiveStartDate;
	}

	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public Date getEffectiveStopDate() {
		return effectiveStopDate;
	}

	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public void setEffectiveStopDate(Date effectiveStopDate) {
		this.effectiveStopDate = effectiveStopDate;
	}

	public Integer getAddressId() {
		return addressId;
	}

	public void setAddressId(Integer addressId) {
		this.addressId = addressId;
	}

	public String getDivisionCode() {
		return divisionCode;
	}

	public void setDivisionCode(String divisionCode) {
		this.divisionCode = divisionCode;
	}

	public Integer getDivisionNbr() {
		return divisionNbr;
	}

	public void setDivisionNbr(Integer divisionNbr) {
		this.divisionNbr = divisionNbr;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStateName() {
		return stateName;
	}

	public void setStateName(String stateName) {
		this.stateName = stateName;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public WebMessages validateAdd(Connection conn) throws Exception {
		WebMessages webMessages = new WebMessages();
		RequestValidator.validateId(conn, webMessages, "locale", Locale.LOCALE_ID, "localeId", localeId, true);
		RequestValidator.validateId(conn, webMessages, "division", Division.DIVISION_ID, "divisionId", divisionId, true);
		RequestValidator.validateDate(webMessages, EFF_START_DATE, effectiveStartDate, true, null, null);
//		RequestValidator.validateDate(webMessages, EFF_STOP_DATE, effectiveStopDate, false, getEffectiveStartDate(), null);
		RequestValidator.validateId(conn, webMessages, "address", Address.ADDRESS_ID, "addressId", addressId, true);
		
		if ( webMessages.isEmpty() && hasOverlappingStartDate(conn, effectiveStartDate) ) {
			webMessages.addMessage(EFF_START_DATE, "Date Range already in use");
		}
		
		return webMessages;
	}
	
	
	public WebMessages validateUpdate(Connection conn) throws Exception {
		WebMessages webMessages = new WebMessages();
		RequestValidator.validateId(conn, webMessages, "locale", Locale.LOCALE_ID, "localeId", localeId, true);
		RequestValidator.validateId(conn, webMessages, "division", Division.DIVISION_ID, "divisionId", divisionId, true);
		RequestValidator.validateDate(webMessages, EFF_STOP_DATE, effectiveStopDate, true, getEffectiveStartDate(), null);
		RequestValidator.validateId(conn, webMessages, "address", Address.ADDRESS_ID, "addressId", addressId, true);
		
		if ( webMessages.isEmpty() && hasOverlappingStopDate(conn, effectiveStartDate, effectiveStopDate) ) {
			webMessages.addMessage(EFF_STOP_DATE, "Date Range already in use");
		}
		
		return webMessages;
	}

	public WebMessages validateDelete(Connection conn) throws Exception {
		WebMessages webMessages = new WebMessages();
		RequestValidator.validateId(conn, webMessages, "locale", Locale.LOCALE_ID, "localeId", localeId, true);
		RequestValidator.validateId(conn, webMessages, "division", Division.DIVISION_ID, "divisionId", divisionId, true);
		RequestValidator.validateDate(webMessages, EFF_START_DATE, effectiveStartDate, true, null, null);
		return webMessages;
	}

	private boolean hasOverlappingStartDate(Connection conn, Date effectiveDate) throws SQLException {
		String sql = "select count(*) as record_count from locale_division where\n" + 
				"locale_id=? and division_id=? and\n" + 
				"((effective_stop_date is not null and effective_start_date <= ? and effective_stop_date >= ?)\n" + 
				" or (effective_stop_date is null and effective_start_date=?))";
		PreparedStatement ps = conn.prepareStatement(sql);	
		
		java.sql.Date testDate = new java.sql.Date( DateUtils.truncate(effectiveDate, Calendar.DAY_OF_MONTH).getTime());
		ps.setInt(1,this.localeId);
		ps.setInt(2,this.divisionId);
		ps.setDate(3,testDate);
		ps.setDate(4,testDate);
		ps.setDate(5,testDate);
		
		ResultSet rs = ps.executeQuery();
		Integer count = 0;
		if ( rs.next() ) {
			count = rs.getInt("record_count");
		}
		rs.close();
		return count > 0;
	}
	
	
	private boolean hasOverlappingStopDate(Connection conn, Date effectiveStartDate, Date effectiveStopDate) throws SQLException {
		String sql = "select count(*) as record_count from locale_division where\n" + 
				"locale_id=? and division_id=? and\n" + 
				"((effective_stop_date is not null and effective_start_date <= ? and effective_stop_date >= ?)\n" + 
				" or (effective_stop_date is null and effective_start_date=?)) and \n" +
				"effective_start_date!=?";
		PreparedStatement ps = conn.prepareStatement(sql);	
		
		java.sql.Date testStopDate = new java.sql.Date( DateUtils.truncate(effectiveStopDate, Calendar.DAY_OF_MONTH).getTime());
		java.sql.Date testStartDate = new java.sql.Date( DateUtils.truncate(effectiveStartDate, Calendar.DAY_OF_MONTH).getTime());
		ps.setInt(1,this.localeId);
		ps.setInt(2,this.divisionId);
		ps.setDate(3,testStopDate);
		ps.setDate(4,testStopDate);
		ps.setDate(5,testStopDate);
		ps.setDate(6,testStartDate);
		
		ResultSet rs = ps.executeQuery();
		Integer count = 0;
		if ( rs.next() ) {
			count = rs.getInt("record_count");
		}
		rs.close();
		return count > 0;
	}
	
}
