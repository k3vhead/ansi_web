package com.ansi.scilla.web.locale.response;

import java.sql.Connection;
import java.util.Date;

import com.ansi.scilla.common.db.LocaleDivision;
import com.ansi.scilla.web.common.response.MessageResponse;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class LocaleDivisionResponse extends MessageResponse {

	private static final long serialVersionUID = 1L;
	
	public static final String DIVISION_ID = "division_id";
	public static final String LOCALE_ID = "locale_id";
	public static final String EFFECTIVE_START_DATE = "effective_start_date";
	public static final String EFFECTIVE_STOP_DATE = "effective_stop_date";
	public static final String ADDRESS_ID = "address_id";
		
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
