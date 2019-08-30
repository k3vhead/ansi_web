package com.ansi.scilla.web.tax.response;

import java.math.BigDecimal;
//import com.mysql.jdbc.Connection;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
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
	private String parentName;
	private String parentStateName;
	private String parentLocaleTypeId;
	
	public LocaleTaxRateResponse() {
		super();
	}
	
	public LocaleTaxRateResponse(Connection conn, LocaleTaxRate taxRate) throws RecordNotFoundException, Exception {
		this();
		Locale locale = new Locale();
		locale.setLocaleId(taxRate.getLocaleId());
		locale.selectOne(conn);
		makeLocale(conn, taxRate, locale);
		makeTypeList(conn);
	}
	
	
	
	public LocaleTaxRateResponse(Connection conn, Integer localeId, Date effectiveDate, Integer rateTypeId) throws RecordNotFoundException, Exception {
		this();
		Locale locale = new Locale();
		locale.setLocaleId(localeId);
		locale.selectOne(conn);
		LocaleTaxRate localeTaxRate = null;
		if ( effectiveDate != null & rateTypeId != null) {
			try {
				localeTaxRate = new LocaleTaxRate();
				localeTaxRate.setLocaleId(localeId);
				localeTaxRate.setEffectiveDate(effectiveDate);
				localeTaxRate.setTypeId(rateTypeId);
				localeTaxRate.selectOne(conn);
			} catch ( RecordNotFoundException e) {
				// this is OK
				localeTaxRate = null;
			}
		}
		makeLocale(conn, localeTaxRate, locale);
			
		makeTypeList(conn);
	}
	
	
	
	private void makeLocale(Connection conn, LocaleTaxRate localeTaxRate, Locale locale) throws Exception {
		this.localeId = locale.getLocaleId();
		this.name = locale.getName();
		this.stateName = locale.getStateName();
		this.localeTypeId = locale.getLocaleTypeId();
		
		if ( locale.getLocaleParentId() != null ) {
			Locale parent = new Locale();
			parent.setLocaleId(locale.getLocaleParentId());
			parent.selectOne(conn);
			this.parentName = parent.getName();
			this.parentStateName = parent.getStateName();
			this.parentLocaleTypeId = parent.getLocaleTypeId();
		}
		
		if ( localeTaxRate != null ) {
			this.effectiveDate = Calendar.getInstance();
			this.effectiveDate.setTime(localeTaxRate.getEffectiveDate());
			this.rateValue = localeTaxRate.getRateValue();
			RateType rateType = new RateType();
			this.typeId = localeTaxRate.getTypeId();
			rateType.setTypeId(this.typeId);
			rateType.selectOne(conn);
			this.typeName = rateType.getTypeName();
		}		
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
	public String getParentName() {
		return parentName;
	}
	public void setParentName(String parentName) {
		this.parentName = parentName;
	}
	public String getParentStateName() {
		return parentStateName;
	}
	public void setParentStateName(String parentStateName) {
		this.parentStateName = parentStateName;
	}
	public String getParentLocaleTypeId() {
		return parentLocaleTypeId;
	}
	public void setParentLocaleTypeId(String parentLocaleTypeId) {
		this.parentLocaleTypeId = parentLocaleTypeId;
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
