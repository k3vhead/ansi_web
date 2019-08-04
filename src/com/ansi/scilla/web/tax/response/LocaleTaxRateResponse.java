package com.ansi.scilla.web.tax.response;

import java.math.BigDecimal;
//import com.mysql.jdbc.Connection;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.db.Locale;
import com.ansi.scilla.common.db.LocaleTaxRate;
import com.ansi.scilla.common.db.RateType;
import com.ansi.scilla.common.db.TaxRateType;
import com.ansi.scilla.web.common.response.MessageResponse;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class LocaleTaxRateResponse extends MessageResponse {

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
	private Calendar effectiveDate;
	private String localeTypeId;
	private BigDecimal rateValue;
	private Integer typeId;
	private String typeName;
	private List<TaxType> taxTypeList;
	
	public LocaleTaxRateResponse() {
		super();
	}
	
	public LocaleTaxRateResponse(LocaleTaxRate taxRate, Connection conn) throws RecordNotFoundException, Exception {
		this();
		Locale locale = new Locale();
		locale.setLocaleId(taxRate.getLocaleId());
		locale.selectOne(conn);
		makeLocale(conn, taxRate, locale);
		makeTypeList(conn);
	}
	
	
	
	public LocaleTaxRateResponse(Connection conn, Integer localeId) throws RecordNotFoundException, Exception {
		this();
		LocaleTaxRate localeTaxRate = new LocaleTaxRate();
		localeTaxRate.setLocaleId(localeId);
		localeTaxRate.selectOne(conn);
		Locale locale = new Locale();
		locale.setLocaleId(localeId);
		locale.selectOne(conn);
		makeLocale(conn, localeTaxRate, locale);
		makeTypeList(conn);
	}
	
	
	
	private void makeLocale(Connection conn, LocaleTaxRate localeTaxRate, Locale locale) throws Exception {
		this.localeId = localeTaxRate.getLocaleId();
		this.name = locale.getName();
		this.stateName = locale.getStateName();
		this.effectiveDate = Calendar.getInstance();
		this.effectiveDate.setTime(localeTaxRate.getEffectiveDate());
		this.localeTypeId = locale.getLocaleTypeId();
		this.rateValue = localeTaxRate.getRateValue();
		RateType rateType = new RateType();
		this.typeId = localeTaxRate.getTypeId();
		rateType.setTypeId(this.typeId);
		rateType.selectOne(conn);
		this.typeName = rateType.getTypeName();
		
	}
	
	private void makeTypeList(Connection conn) throws Exception {
		List<TaxRateType> typeList = TaxRateType.cast(new TaxRateType().selectAll(conn));
		this.taxTypeList = new ArrayList<TaxType>();
		for ( TaxRateType type : typeList ) {
			this.taxTypeList.add(new TaxType(type));
		}
		Collections.sort(this.taxTypeList);
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
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public Calendar getEffectiveDate() {
		return effectiveDate;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public void setEffectiveDate(Calendar effectiveDate) {
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
	public List<TaxType> getTaxTypeList() {
		return taxTypeList;
	}
	public void setTaxTypeList(List<TaxType> taxTypeList) {
		this.taxTypeList = taxTypeList;
	}


	public class TaxType extends ApplicationObject implements Comparable<TaxType> {
		private static final long serialVersionUID = 1L;
		private Integer typeId;
		private String typeName;
		public TaxType(TaxRateType taxRateType) {
			this.typeId = taxRateType.getTypeId();
			this.typeName = taxRateType.getTypeName();
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
		@Override
		public int compareTo(TaxType o) {
			return this.typeName.compareTo(o.getTypeName());
		}
	}
	
	
}
