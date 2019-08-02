package com.ansi.scilla.web.locale.request;

import java.sql.Connection;
import java.util.Date;

import com.ansi.scilla.common.db.Address;
import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.common.db.Locale;
import com.ansi.scilla.web.common.request.AbstractRequest;
import com.ansi.scilla.web.common.request.RequestValidator;
import com.ansi.scilla.web.common.response.WebMessages;

public class LocaleDivisionRequest extends AbstractRequest {

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

	public WebMessages validateAdd(Connection conn) throws Exception {
		WebMessages webMessages = new WebMessages();
		RequestValidator.validateId(conn, webMessages, "locale", Locale.LOCALE_ID, "localeId", localeId, true);
		RequestValidator.validateId(conn, webMessages, "division", Division.DIVISION_ID, "divisionId", divisionId, true);
		RequestValidator.validateDate(webMessages, EFFECTIVE_START_DATE, effectiveStartDate, true, getEffectiveStartDate(), getEffectiveStopDate());
		RequestValidator.validateDate(webMessages, EFFECTIVE_STOP_DATE, effectiveStopDate, false, getEffectiveStartDate(), getEffectiveStopDate());
		RequestValidator.validateId(conn, webMessages, "address", Address.ADDRESS_ID, "addressId", addressId, true);
		return webMessages;
	}
	
	public WebMessages validateUpdate(Connection conn) throws Exception {
		WebMessages webMessages = validateAdd(conn);
		
		
		return webMessages;
	}
	
}
