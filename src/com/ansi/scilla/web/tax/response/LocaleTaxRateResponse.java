package com.ansi.scilla.web.tax.response;

import java.math.BigDecimal;
//import java.sql.Date;
import java.util.Date;
import com.ansi.scilla.common.db.LocaleTaxRate;
import com.ansi.scilla.web.common.response.MessageResponse;
//import com.mysql.jdbc.Connection;
import java.sql.Connection;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class LocaleTaxRateResponse extends MessageResponse {

	private static final long serialVersionUID = 1L;
	
	public static final String LOCALE_ID = "locale.locale_id";
	public static final String STATE_NAME = "locale.state_name";
	public static final String NAME = "locale.name";	
	public static final String LOCALE_TYPE_ID = "locale.locale_type_id";	
	public static final String EFFECTIVE_DATE = "locale_tax_rate.effective_date";	
	public static final String RATE_VALUE = "locale_tax_rate.rate_value";	
	public static final String TYPE_ID = "locale_tax_rate.type_id";
	public static final String TYPE_NAME = "type_name";
		
	private Integer localeId;
	private String name;
	private String stateName;
	private Date effectiveDate;
	private String localeTypeId;
	private BigDecimal rateValue;
	private Integer typeId;
	private String typeName;
	
	public LocaleTaxRateResponse() {
		super();
	}
	
//	public LocaleTaxRateResponse(Integer localeId, String name, String stateName,
//			Date effectiveDate, String localeTypeId, BigDecimal rateValue,
//			Integer typeId, String typeName) {
//		this();
//		this.localeId = localeId;
//		this.name = name;
//		this.stateName = stateName;
//		this.effectiveDate = effectiveDate;
//		this.localeTypeId = localeTypeId;
//		this.rateValue = rateValue;
//		this.typeId = typeId;
//		this.typeName = typeName;
//	}
	
	public LocaleTaxRateResponse(LocaleTaxRate taxRate) {
		this();
		make(taxRate);
	}
	
	public LocaleTaxRateResponse(Connection conn, Integer localeId) throws RecordNotFoundException, Exception {
		this();
		LocaleTaxRate localeTaxRate = new LocaleTaxRate();
		localeTaxRate.setLocaleId(localeId);
		localeTaxRate.selectOne(conn);
		make(localeTaxRate);
	}
	
	private void make(LocaleTaxRate localeTaxRate) {
		this.localeId = localeTaxRate.getLocaleId();
		this.effectiveDate = localeTaxRate.getEffectiveDate();
		this.rateValue = localeTaxRate.getRateValue();
		this.typeId = localeTaxRate.getTypeId();
	}
	
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
	
}
