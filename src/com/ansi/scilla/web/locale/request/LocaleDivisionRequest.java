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
	
	
//	public void generateLocaleId(Connection conn, String localeName) throws RecordNotFoundException, Exception {
//		Locale locale = new Locale();
//		locale.setName(localeName);
//		locale.selectOne(conn);
//		this.setLocaleId(locale.getLocaleId());
//	}
//	
//	public void generateAddressIdFromOne(Connection conn, String addressName) throws RecordNotFoundException, Exception {
//		Address address = new Address();
//		address.setAddress1(addressName);
//		address.selectOne(conn);
//		this.setAddressId(address.getAddressId());
//	}
//	
//	public void generateAddressIdFromTwo(Connection conn, String addressName) throws RecordNotFoundException, Exception {
//		Address address = new Address();
//		address.setAddress2(addressName);
//		address.selectOne(conn);
//		this.setAddressId(address.getAddressId());
//	}
	
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

	public WebMessages validate(Connection conn) throws Exception {
		WebMessages webMessages = new WebMessages();
		RequestValidator.validateId(conn, webMessages, "locale", Locale.LOCALE_ID, "localeId", localeId, true);
		RequestValidator.validateId(conn, webMessages, "division", Division.DIVISION_ID, "divisionId", divisionId, true);
		RequestValidator.validateDate(webMessages, EFF_START_DATE, effectiveStartDate, true, null, null);
		RequestValidator.validateDate(webMessages, EFF_STOP_DATE, effectiveStopDate, true, getEffectiveStartDate(), null);
		RequestValidator.validateId(conn, webMessages, "address", Address.ADDRESS_ID, "addressId", addressId, true);
		
		if ( webMessages.isEmpty() && hasOverlappingDates(conn) ) {
			webMessages.addMessage(EFF_START_DATE, "Date Range already in use");
		}
		
		return webMessages;
	}

	private boolean hasOverlappingDates(Connection conn) throws SQLException {
		String sql = "select count(*) as record_count from locale_division \n" + 
				"where division_id=? and locale_id=? and (\n" + 
				"(effective_start_date <= ? and effective_stop_date > ?)   \n" +   //-- start date is in range 
				"or ( effective_start_date < ? and effective_stop_date > ?) \n" + //   -- stop date is in range
				"or ( effective_start_date > ? and effective_stop_date < ?) \n" + //-- start is before, stop is after 
				")";
		PreparedStatement ps = conn.prepareStatement(sql);	
		
		java.sql.Date startDate = new java.sql.Date( DateUtils.truncate(this.effectiveStartDate, Calendar.DAY_OF_MONTH).getTime());
		java.sql.Date stopDate = new java.sql.Date( DateUtils.truncate(this.effectiveStopDate, Calendar.DAY_OF_MONTH).getTime());
		int n = 1;
		ps.setInt(n, this.divisionId);
		n++;
		ps.setInt(n, this.localeId);
		n++;
		ps.setDate(n, startDate);
		n++;
		ps.setDate(n, startDate);
		n++;
		ps.setDate(n, stopDate);
		n++;
		ps.setDate(n, stopDate);
		n++;
		ps.setDate(n, startDate);
		n++;
		ps.setDate(n, stopDate);
		n++;
		
		ResultSet rs = ps.executeQuery();
		Integer count = 0;
		if ( rs.next() ) {
			count = rs.getInt("record_count");
		}
		rs.close();
		return count > 0;
	}
	
}
