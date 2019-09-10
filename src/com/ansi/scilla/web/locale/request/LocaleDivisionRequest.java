package com.ansi.scilla.web.locale.request;

import java.sql.Connection;
import java.util.Date;

import com.ansi.scilla.common.db.Address;
import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.common.db.Locale;
import com.ansi.scilla.web.common.request.AbstractRequest;
import com.ansi.scilla.web.common.request.RequestValidator;
import com.ansi.scilla.web.common.response.WebMessages;
import com.fasterxml.jackson.annotation.JsonFormat;

public class LocaleDivisionRequest extends AbstractRequest {

	private static final long serialVersionUID = 1L;

	public static final String DIVISION_ID = "locale_division.division_id";
	public static final String LOCALE_ID = "locale_division.locale_id";
	public static final String EFF_START_DATE = "locale_division.effective_start_date";
	public static final String EFF_STOP_DATE = "locale_division.effective_stop_date"; 
	public static final String ADDRESS_ID = "locale_division.address_id"; 
	public static final String NAME = "locale.name";
	public static final String LOCALE_STATE_NAME = "locale.state_name";
	public static final String LOCALE_TYPE_ID = "locale.locale_type_id";
	public static final String DIVISION_CODE = "division.division_code";
	public static final String DIVISION_NBR = "division.division_nbr";
	public static final String DIVISION_DISPLAY = "concat(division_nbr,'-',division_code)";
	public static final String DESCRIPTION = "division.description"; 
	public static final String ADDRESS1 = "address.address1";
	public static final String ADDRESS2 = "address.address2";
	public static final String CITY = "address.city";
	public static final String STATE = "address.state";
	public static final String ZIP = "address.zip";
	
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

	public WebMessages validateAdd(Connection conn) throws Exception {
		WebMessages webMessages = new WebMessages();
//		if((this.getLocaleId() == null) && (this.getName() != null)) {
//			this.generateLocaleId(conn, this.getName());
//		}
//		if((this.getAddressId() == null) && (this.getAddress1() != null)) {
//			this.generateAddressIdFromOne(conn, this.getAddress1());
//		} else if ((this.getAddressId() == null) && (this.getAddress2() != null)) {
//			this.generateAddressIdFromTwo(conn, this.getAddress2());
//		}
		RequestValidator.validateId(conn, webMessages, "locale", Locale.LOCALE_ID, "localeId", localeId, true);
		RequestValidator.validateId(conn, webMessages, "division", Division.DIVISION_ID, "divisionId", divisionId, true);
		RequestValidator.validateDate(webMessages, EFF_START_DATE, effectiveStartDate, true, null, null);
		RequestValidator.validateDate(webMessages, EFF_STOP_DATE, effectiveStopDate, false, getEffectiveStartDate(), null);
		RequestValidator.validateId(conn, webMessages, "address", Address.ADDRESS_ID, "addressId", addressId, true);
		
		
		return webMessages;
	}
	
	public WebMessages validateUpdate(Connection conn) throws Exception {
		WebMessages webMessages = validateAdd(conn);
		
		
		return webMessages;
	}
	
}
