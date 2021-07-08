package com.ansi.scilla.web.common.utils;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.common.ApplicationObject;

/**
 * Used in LookupQuery objects to filter results based on column name/value. Essentially this maps a screen-based
 * filter to a value in a particular column of the query
 * @author dclewis
 *
 */
public class ColumnFilter extends ApplicationObject {
	private static final long serialVersionUID = 1L;
	private String columnName;
	private Object searchValue;
	private ComparisonType comparisonType;
	
	public ColumnFilter(String columnName, String searchValue) {
		this(columnName, searchValue, ComparisonType.LIKE);		
	}
	
	public ColumnFilter(String columnName, Object searchValue, ComparisonType comparisonType) {
		super();
		this.columnName = columnName;
		this.searchValue = searchValue;
		this.comparisonType = comparisonType;
	}

	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	public Object getSearchValue() {
		return searchValue;
	}
	public void setSearchValue(Object searchValue) {
		this.searchValue = searchValue;
	}	
	public ComparisonType getComparisonType() {
		return comparisonType;
	}
	public void setComparisonType(ComparisonType comparisonType) {
		this.comparisonType = comparisonType;
	}

	/**
	 * Explicitly case-insensitive pattern match
	 * @return  lower(columName) like '%searchValue%'
	 */
	public String toLike() {
		String term = String.valueOf(searchValue);
		if ( term.indexOf("'") > -1 ) {
			term = term.replaceAll("'", "''");
		}
		return "lower(" + columnName + ") like '%" + term.toLowerCase() + "%'";
	}
	public String toEqNumber() {
		return columnName + "=" + searchValue;
	}
	/**
	 * Explicity case-insensitive equal match
	 * @return lower(columnName)='searchValue'
	 */
	public String toEqString() {
		String term = String.valueOf(searchValue);
		if ( term.indexOf("'") > -1 ) {
			term = term.replaceAll("'", "''");
		}
		return "lower(" + columnName + ")='" + term.toLowerCase() + "'";
	}
	/**
	 * Matches on date portion of a date or datetime column
	 * @return
	 */
	public String toEqDate() {
		String term = null;
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		if ( searchValue instanceof java.util.Date ) {
			term = sdf.format((java.util.Date)searchValue);
		} else if ( searchValue instanceof java.sql.Date ) {
			term = sdf.format((java.sql.Date)searchValue);
		} else if ( searchValue instanceof Calendar ) {
			term = sdf.format(((Calendar)searchValue).getTime());
		} else if ( searchValue instanceof String) {
			term = (String)searchValue;
		} else {
			throw new RuntimeException("Unknown date type: " + searchValue.getClass().getCanonicalName());
		}
		return "(convert(date, " + columnName + ",101))=convert(date,'" + term + "',101)";
	}
	
	/**
	 * Makes a "where columnName in (1,2,3, ...)" filter
	 * @return
	 */
	public String toListNumber() {
		throw new RuntimeException("Not coded yet");
	}
	
	/**
	 * Makes a "where columnName in ('a','b','c', ....)" filter
	 * @return
	 */
	public String toListString() {
		List<String> term = null;
		if ( searchValue instanceof String ) {
			term = Arrays.asList(((String)searchValue).split(","));
		} else if ( searchValue instanceof List ) {
			term = (List<String>)CollectionUtils.collect( ((List<?>)searchValue).iterator(), new Object2StringTranformer());
		} else if ( searchValue instanceof String[] ) {
			term = Arrays.asList((String[])searchValue);
		} else {
			throw new RuntimeException("Write a new list conversion: " + searchValue.getClass().getCanonicalName());
		}
		
		List<String> valueList = (List<String>)CollectionUtils.collect(term, new String2QuoteTransformer());
		return columnName + " in (" + StringUtils.join(valueList, ",") + ")";
	}
	
	
	public enum ComparisonType {
		LIKE("toLike"),
		EQUAL_NUMBER("toEqNumber"),
		EQUAL_STRING("toEqString"),
		EQUAL_DATE("toEqDate"),
		LIST_NUMBER("toListNumber"),
		LIST_STRING("toListString"),
		;
		
		private final String methodName;
		private ComparisonType(String methodName) { this.methodName = methodName; }
		public String getMethodName() { return methodName; }
	}
	
	public class Object2StringTranformer implements Transformer<Object, String> {
		@Override
		public String transform(Object arg0) {
			return String.valueOf(arg0);
		}
	}
	
	public class String2QuoteTransformer implements Transformer<String, String> {
		@Override
		public String transform(String arg0) {
			return "'" + arg0 + "'";
		}
		
	}
}


