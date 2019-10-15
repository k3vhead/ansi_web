package com.ansi.scilla.web.locale.response;

import java.sql.Connection;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;

import com.ansi.scilla.common.db.Address;
import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.common.db.Locale;
import com.ansi.scilla.common.db.LocaleDivision;
import com.ansi.scilla.web.common.response.MessageResponse;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class LocaleDivisionResponse extends MessageResponse {

	private static final long serialVersionUID = 1L;
	
	public static final String DIVISION_ID = "division_id";
	public static final String LOCALE_ID = "locale_id";
	public static final String EFF_START_DATE = "effective_start_date";
	public static final String EFF_STOP_DATE = "effective_stop_date"; 
	public static final String ADDRESS_ID = "address_id"; 
	public static final String NAME = "name";
	public static final String LOCALE_STATE_NAME = "state_name";
	public static final String LOCALE_TYPE_ID = "locale_type_id";
	public static final String DIVISION_CODE = "division_code";
	public static final String DIVISION_NBR = "division_nbr";
	public static final String DIVISION_DISPLAY = "division_display";
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
	
	private Calendar effectiveStartDate;
	private Calendar effectiveStopDate;
	
	private Integer addressId;
	private String addressName;
	private String address1;
	private String address2;
	private String city;
	private String state;
	private String zip;
	
	public LocaleDivisionResponse() {
		super();
	}
	
	public LocaleDivisionResponse(Connection conn, LocaleDivision localeDivision) throws RecordNotFoundException, Exception {
		this();
		make(conn, localeDivision);		
	}

	public LocaleDivisionResponse(Connection conn, Integer localeId, Integer divisionId, Date effectiveStartDate) throws RecordNotFoundException, Exception {
		this();
		LocaleDivision localeDivision = new LocaleDivision();
		localeDivision.setLocaleId(localeId);
		localeDivision.setDivisionId(divisionId);
		localeDivision.setEffectiveStartDate(effectiveStartDate);
		localeDivision.selectOne(conn);
		make(conn, localeDivision);
		
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
	public Calendar getEffectiveStartDate() {
		return effectiveStartDate;
	}

	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public void setEffectiveStartDate(Calendar effectiveStartDate) {
		this.effectiveStartDate = effectiveStartDate;
	}
	
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public Calendar getEffectiveStopDate() {
		return effectiveStopDate;
	}

	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public void setEffectiveStopDate(Calendar effectiveStopDate) {
		this.effectiveStopDate = effectiveStopDate;
	}

	public Integer getAddressId() {
		return addressId;
	}

	public String getAddressName() {
		return addressName;
	}

	public void setAddressName(String addressName) {
		this.addressName = addressName;
	}

	public void setAddressId(Integer addressId) {
		this.addressId = addressId;
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

	private void make(Connection conn, LocaleDivision localeDivision) throws RecordNotFoundException, Exception {
		this.localeId = localeDivision.getLocaleId();
		Locale locale = new Locale();
		locale.setLocaleId(this.getLocaleId());
		locale.selectOne(conn);
		this.name = locale.getName();
		this.stateName = locale.getStateName();
		
		this.divisionId = localeDivision.getDivisionId();
		Division div = new Division();
		div.setDivisionId(this.getDivisionId());
		div.selectOne(conn);
		this.divisionCode = div.getDivisionCode();
		this.divisionNbr = div.getDivisionNbr();
		
		this.effectiveStartDate = DateUtils.toCalendar(localeDivision.getEffectiveStartDate());
		this.effectiveStopDate = localeDivision.getEffectiveStopDate() == null ? null : DateUtils.toCalendar(localeDivision.getEffectiveStopDate());
		
		this.addressId = localeDivision.getAddressId();
		Address address = new Address();
		address.setAddressId(this.getAddressId());
		address.selectOne(conn);
		this.addressName = address.getName();
		this.address1 = address.getAddress1();
		this.address2 = address.getAddress2();
		this.city = address.getCity();
		this.state = address.getState();
		this.zip = address.getZip();
	}
	
	
	
}
