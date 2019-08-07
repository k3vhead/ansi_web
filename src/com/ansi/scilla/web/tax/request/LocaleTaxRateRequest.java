package com.ansi.scilla.web.tax.request;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.common.db.Locale;
import com.ansi.scilla.common.db.TaxRateType;
import com.ansi.scilla.web.common.request.AbstractRequest;
import com.ansi.scilla.web.common.request.RequestValidator;
import com.ansi.scilla.web.common.response.WebMessages;
import com.fasterxml.jackson.annotation.JsonFormat;

public class LocaleTaxRateRequest extends AbstractRequest {

	private static final long serialVersionUID = 1L;

	public static final String LOCALE_ID = "localeId";
	public static final String EFFECTIVE_DATE = "effectiveDate";	
	public static final String RATE_VALUE = "rateValue";	
	public static final String TYPE_ID = "typeId";
	public static final String TYPE_NAME = "typeName";
	// these 2 fields are used (with the locale id) to id existing rows for update
	public static final String KEY_EFFECTIVE_DATE = "keyEffectiveDate";
	public static final String KEY_RATE_TYPE_ID = "keyRateTypeId";
		
	private Integer localeId;
	private Date effectiveDate;
	private BigDecimal rateValue;
	private Integer typeId;
	private String typeName;
	private Date keyEffectiveDate;
	private Integer keyRateTypeId;
	
	public Integer getLocaleId() {
		return localeId;
	}
	public void setLocaleId(Integer localeId) {
		this.localeId = localeId;
	}	
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public Date getEffectiveDate() {
		return effectiveDate;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
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
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public Date getKeyEffectiveDate() {
		return keyEffectiveDate;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public void setKeyEffectiveDate(Date keyEffectiveDate) {
		this.keyEffectiveDate = keyEffectiveDate;
	}
	public Integer getKeyRateTypeId() {
		return keyRateTypeId;
	}
	public void setKeyRateTypeId(Integer keyRateTypeId) {
		this.keyRateTypeId = keyRateTypeId;
	}
	public WebMessages validateAdd(Connection conn) throws Exception {
		Logger logger = LogManager.getLogger(this.getClass());
		WebMessages webMessages = new WebMessages();
		RequestValidator.validateDate(webMessages, EFFECTIVE_DATE, effectiveDate, true, null, null);
		RequestValidator.validateBigDecimal(webMessages, RATE_VALUE, rateValue, rateValue, rateValue, true);
		if ( StringUtils.isBlank(this.typeName )) {
			if ( this.typeId == null ) {
				webMessages.addMessage(TYPE_ID, "Required Value");
			} else {
				RequestValidator.validateId(conn, webMessages, TaxRateType.TABLE, TaxRateType.TYPE_ID, TYPE_ID, this.typeId, true);
			}
		} else {
			if ( this.typeId == null ) {
				if ( duplicateExists(conn, this.typeName) ) {
					webMessages.addMessage(LocaleTaxRateRequest.TYPE_ID, "Duplicate value");
				}
			} else {
				logger.log(Level.DEBUG, "ID is not blank");
				webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "Invalid Request. Reload page and try again");
			}
		}
	
		return webMessages;
	}
	
	
	public WebMessages validateUpdate(Connection conn, Integer localeId) throws Exception {
		WebMessages webMessages = validateAdd(conn);
		
		RequestValidator.validateId(conn, webMessages, Locale.TABLE, Locale.LOCALE_ID, LOCALE_ID, this.localeId, true);
		
		return webMessages;
	}
	
	
	private boolean duplicateExists(Connection conn, String name) throws Exception {
		Logger logger = LogManager.getLogger(this.getClass());
		String sql = "select count(*) as record_count from tax_rate_type where lower(type_name)=?";
		logger.log(Level.DEBUG, sql);
		logger.log(Level.DEBUG, name);
		
		String value = name.toLowerCase();
		boolean duplicateFound = false;
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, value);
		ResultSet rs = ps.executeQuery();
		if ( rs.next() ) {
			duplicateFound = rs.getInt("record_count") > 0;
		}
		rs.close();
		return duplicateFound;
	}
	
}
