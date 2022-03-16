package com.ansi.scilla.web.locale.response;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.ansi.scilla.common.queries.ReportQuery;
import com.thewebthing.commons.db2.DBColumn;

public class LocaleLookupResponseItem extends ReportQuery {
	
	private static final long serialVersionUID = 1L;
	
	public static final String LOCALE_ID = "locale_id";
	public static final String NAME = "name";
	public static final String STATE_NAME = "state_name";
	public static final String ABBREVIATION = "abbreviation";
	public static final String LOCALE_TYPE_ID = "locale_type_id";
	
	private Integer localeId;
	private String name;
	private String stateName;
	private String abbreviation;
	private String localeTypeId;
	
	public LocaleLookupResponseItem() {
		super();
	}
	
//	public LocaleLookupResponseItem(LocaleLookupQuery localeLookupQuery) {
//		this();
//		this.localeId = 
//		this.name = invoiceSearch.getFleetmaticsInvoiceNbr();
//		this.stateName = invoiceSearch.getInvoiceAmount();
//		this.abbreviation = invoiceSearch.getInvoiceTax();
//		this.localeTypeId = invoiceSearch.getInvoiceTotal();		
//	}
	
	public LocaleLookupResponseItem(ResultSet rs) throws SQLException {
		this();
		this.localeId = rs.getInt(LOCALE_ID);
		this.name = rs.getString(NAME);
		this.stateName = rs.getString(STATE_NAME);
		this.abbreviation = rs.getString(ABBREVIATION);
		this.localeTypeId = rs.getString(LOCALE_TYPE_ID);
	}
	
	@DBColumn(LOCALE_ID)
	public Integer getLocaleId() {
		return localeId;
	}

	@DBColumn(LOCALE_ID)
	public void setLocaleId(Integer localeId) {
		this.localeId = localeId;
	}
	
	@DBColumn(NAME)
	public String getName() {
		return name;
	}
	
	@DBColumn(NAME)
	public void setName(String name) {
		this.name = name;
	}
	
	@DBColumn(STATE_NAME)
	public String getStateName() {
		return stateName;
	}
	
	@DBColumn(STATE_NAME)
	public void setStateName(String stateName) {
		this.stateName = stateName;
	}
	
	@DBColumn(ABBREVIATION)
	public String getAbbreviation() {
		return abbreviation;
	}
	
	@DBColumn(ABBREVIATION)
	public void setAbbreviation(String abbreviation) {
		this.abbreviation = abbreviation;
	}
	
	@DBColumn(LOCALE_TYPE_ID)
	public String getLocaleTypeId() {
		return localeTypeId;
	}
	
	@DBColumn(LOCALE_TYPE_ID)
	public void setLocaleTypeId(String localeTypeId) {
		this.localeTypeId = localeTypeId;
	}
	
	
	
}
