package com.ansi.scilla.web.tax.request;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;

import com.ansi.scilla.common.db.Locale;
import com.ansi.scilla.common.db.TaxRateType;
import com.ansi.scilla.web.common.request.AbstractRequest;
import com.ansi.scilla.web.common.request.RequestValidator;
import com.ansi.scilla.web.common.response.WebMessages;

public class LocaleTaxRateRequest extends AbstractRequest {

	private static final long serialVersionUID = 1L;

	public static final String LOCALE_ID = "localeId";
	public static final String STATE_NAME = "stateName";
	public static final String NAME = "name";	
	public static final String LOCALE_TYPE_ID = "localeTypeId";	
	public static final String EFFECTIVE_DATE = "effectiveDate";	
	public static final String RATE_VALUE = "rateValue";	
	public static final String TYPE_ID = "typeId";
	public static final String TYPE_NAME = "typeName";
		
	private Integer localeId;
	private String name;
	private String stateName;
	private Date effectiveDate;
	private String localeTypeId;
	private BigDecimal rateValue;
	private Integer typeId;
	private String typeName;
	
	public Integer getLocaleId() {
		return localeId;
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
	public Date getEffectiveDate() {
		return effectiveDate;
	}
	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}
	public void setLocaleId(Integer localeId) {
		this.localeId = localeId;
	}
	public String getLocaleTypeId() {
		return localeTypeId;
	}
	public void setLocaleTypeId(String localeTypeId) {
		this.localeTypeId = localeTypeId;
	}
	public BigDecimal getRateValue() {
		return rateValue;
	}
	public void setRateValue(BigDecimal rateValue) {
		this.rateValue = rateValue;
	}
	public Integer getTypeId() {
		return typeId;
	}
	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
	}
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	
	public WebMessages validateAdd(Connection conn) throws Exception {
		WebMessages webMessages = new WebMessages();
		
		//RequestValidator.validateString(webMessages, NAME, this.name, true);
		//RequestValidator.validateString(webMessages, STATE_NAME, this.stateName, true);
		//RequestValidator.validateString(webMessages, LOCALE_TYPE_ID, this.localeTypeId, true);
		RequestValidator.validateDate(webMessages, EFFECTIVE_DATE, effectiveDate, true, effectiveDate, effectiveDate);
		RequestValidator.validateBigDecimal(webMessages, RATE_VALUE, rateValue, rateValue, rateValue, true);
		RequestValidator.validateId(conn, webMessages, TaxRateType.TABLE, TaxRateType.TYPE_ID, TYPE_ID, this.typeId, true);
		RequestValidator.validateString(webMessages, TYPE_NAME, this.typeName, true);
		
		return webMessages;
	}
	
	public WebMessages validateUpdate(Connection conn, Integer localeId) throws Exception {
		WebMessages webMessages = validateAdd(conn);
		
		RequestValidator.validateId(conn, webMessages, Locale.TABLE, Locale.LOCALE_ID, LOCALE_ID, this.localeId, true);
		
		return webMessages;
	}
	
}
