package com.ansi.scilla.web.locale.response;

import java.sql.Connection;
import java.util.Date;

import com.ansi.scilla.common.db.LocaleDivision;
import com.ansi.scilla.web.common.response.MessageResponse;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class LocaleDivisionResponse extends MessageResponse {

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
	private Integer localeId;
	private Date effectiveStartDate;
	private Date effectiveStopDate;
	private Integer addressId;
	
	public LocaleDivisionResponse() {
		super();
	}
	
	public LocaleDivisionResponse(LocaleDivision localeDivision) {
		this();
		make(localeDivision);		
	}

	public LocaleDivisionResponse(Connection conn, Integer localeId) throws RecordNotFoundException, Exception {
		this();
		LocaleDivision localeDivision = new LocaleDivision();
		localeDivision.setLocaleId(localeId);
		localeDivision.selectOne(conn);
		make(localeDivision);
		
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

	public Date getEffectiveStartDate() {
		return effectiveStartDate;
	}

	public void setEffectiveStartDate(Date effectiveStartDate) {
		this.effectiveStartDate = effectiveStartDate;
	}

	public Date getEffectiveStopDate() {
		return effectiveStopDate;
	}

	public void setEffectiveStopDate(Date effectiveStopDate) {
		this.effectiveStopDate = effectiveStopDate;
	}

	public Integer getAddressId() {
		return addressId;
	}

	public void setAddressId(Integer addressId) {
		this.addressId = addressId;
	}

	private void make(LocaleDivision localeDivision) {
		this.localeId = localeDivision.getLocaleId();
		this.divisionId = localeDivision.getDivisionId();
		this.effectiveStartDate = localeDivision.getEffectiveStartDate();
		this.effectiveStopDate = localeDivision.getEffectiveStopDate();
		this.addressId = localeDivision.getAddressId();
	}
	
}
