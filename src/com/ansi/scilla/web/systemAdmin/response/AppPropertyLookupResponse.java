package com.ansi.scilla.web.systemAdmin.response;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.common.exceptions.InvalidValueException;
import com.ansi.scilla.common.utils.ApplicationPropertyName;
import com.ansi.scilla.web.common.response.MessageResponse;
import com.ansi.scilla.web.common.utils.AppUtils;

public class AppPropertyLookupResponse extends MessageResponse {

	private static final long serialVersionUID = 1L;
	
	private final String VALUE_STRING = "value_string";
	private final String VALUE_INT = "value_int";
	private final String VALUE_FLOAT = "value_float";
	private final String VALUE_DATE = "value_date";
	private final String VALUE = "value";
	private final String DESC = "desc";
	private final String SOURCE_DB = "db";
	private final String SOURCE_ENUM = "enum";
	private final String FORMAT = "format";
	private final String FORMAT_IS_STRING = "String";
	private final String FORMAT_IS_FLOAT = "Decimal";
	private final String FORMAT_IS_INT = "Integer";
	private final String FORMAT_IS_DATE = "Date";
	private final String VALUE_TRUNC = "value_trunc";
	
	
	private List<HashMap<String, Object>> propertyList;
	
	public AppPropertyLookupResponse() {
		super();
	}
	
	public AppPropertyLookupResponse(Statement s) throws SQLException {
		this();
		// step 1 - figure out what's in the database
		List<HashMap<String, Object>> propertyList = makeDatabaseList(s);
		// step 2 - format for usage in datatables
		CollectionUtils.transform(propertyList, new ItemTransformer());
		// step 3 - figure out what we're missing
		for ( ApplicationPropertyName prop : ApplicationPropertyName.values() ) {
			long matchCount = IterableUtils.countMatches(propertyList, new IdPredicate(prop));
			if ( matchCount == 0l) {
				HashMap<String, Object> row = new HashMap<String, Object>();
				row.put("property_id", prop.propertyId());
				row.put(DESC, prop.helpText());
				row.put(SOURCE_DB, false);
				row.put(SOURCE_ENUM, true);
				row.put(FORMAT, makePropertyFormat(prop));
				propertyList.add(row);
			}
		}
		this.propertyList = propertyList;
	}
	
	public List<HashMap<String, Object>> getPropertyList() {
		return propertyList;
	}
	public void setPropertyList(List<HashMap<String, Object>> propertyList) {
		this.propertyList = propertyList;
	}

	
	private List<HashMap<String, Object>> makeDatabaseList(Statement s) throws SQLException {
		List<HashMap<String, Object>> propertyList = new ArrayList<HashMap<String, Object>>();				
		ResultSet rs = s.executeQuery("select property_id,\n"
				+ "	value_string,\n"
				+ "	value_int,\n"
				+ "	value_float,\n"
				+ "	value_date\n"
				+ "from application_properties\n"
				+ "order by application_properties.property_id");
		ResultSetMetaData rsmd = rs.getMetaData();
		
		while ( rs.next() ) {
			HashMap<String, Object> row = AppUtils.rs2Map(rs, rsmd);
			propertyList.add(row);
		}
		rs.close();
		return propertyList;
	}


	private String makePropertyFormat(ApplicationPropertyName propName) {
		String format = null;
		switch (propName.fieldName()) {
		case "value_float":
			format = FORMAT_IS_FLOAT;
			break;
		case "value_int":
			format =  FORMAT_IS_INT;
			break;
		case "value_string":
			format =  FORMAT_IS_STRING;
			break;
		case "value_date":
			format =  FORMAT_IS_DATE;
			break;
		default:
			format =  "Invalid: " + propName.fieldName();
			break;	
		}
		return format;
	}

	

	

	
	
	
	public class ItemTransformer implements Transformer<HashMap<String, Object>, HashMap<String,Object>> {

		private final DecimalFormat intFormat = new DecimalFormat("#,##0");
		private final DecimalFormat floatFormat = new DecimalFormat("#,##0.000");
//		private final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
		private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm");
		
		@Override
		public HashMap<String, Object> transform(HashMap<String, Object> arg0) {
			arg0.put(SOURCE_DB, false);
			arg0.put(SOURCE_ENUM, false);
			arg0.put(VALUE_TRUNC, null);
			
			int valueCount = 0;
			if ( arg0.containsKey(VALUE_STRING)) {
				String value = (String)arg0.get(VALUE_STRING);
				if ( ! StringUtils.isBlank(value)) {
					valueCount++;
					arg0.put(VALUE, arg0.get(VALUE_STRING));
					arg0.put(FORMAT, FORMAT_IS_STRING);
					arg0.put(VALUE_TRUNC, StringUtils.abbreviate(value, 50));
				}
			}
			if ( arg0.containsKey(VALUE_INT)) {
				Integer value = (Integer)arg0.get(VALUE_INT);
				if ( value != null ) {
					valueCount++;
					arg0.put(VALUE, intFormat.format(value));
					arg0.put(FORMAT, FORMAT_IS_INT);
					arg0.put(VALUE_TRUNC, arg0.get(VALUE));
				}
			}
			if ( arg0.containsKey(VALUE_FLOAT)) {
				BigDecimal value = (BigDecimal)arg0.get(VALUE_FLOAT);
				if ( value != null ) {
					valueCount++;
					arg0.put(VALUE, floatFormat.format(value.floatValue()));
					arg0.put(FORMAT, FORMAT_IS_FLOAT);
					arg0.put(VALUE_TRUNC, arg0.get(VALUE));
				}
			}
			if ( arg0.containsKey(VALUE_DATE)) {
				Date value = (Date)arg0.get(VALUE_DATE);
				if ( value != null ) {
					valueCount++;
					arg0.put(VALUE, sdf.format(value));
					arg0.put(FORMAT, FORMAT_IS_DATE);
					arg0.put(VALUE_TRUNC, arg0.get(VALUE));
				}
			}
			if ( valueCount > 1 ) {
				arg0.put(FORMAT, "Multiple");				
			}
			arg0.put(SOURCE_DB, true);
			
			try {
				ApplicationPropertyName propName = ApplicationPropertyName.lookup((String)arg0.get("property_id"));
				arg0.put(DESC, propName.helpText());
				arg0.put(SOURCE_ENUM, true);
				arg0.put(FORMAT, makePropertyFormat(propName));
			} catch (InvalidValueException e ) {
				// just don't do the processing
			}
			return arg0;
		}
		
	}
	
	private class IdPredicate implements Predicate<HashMap<String, Object>> {
		ApplicationPropertyName appProperty;
		
		public IdPredicate(ApplicationPropertyName appProperty) {
			super();
			this.appProperty = appProperty;
		}
		
		@Override
		public boolean evaluate(HashMap<String, Object> arg0) {
			return arg0.containsKey("property_id") && ((String)arg0.get("property_id")).equals(this.appProperty.propertyId());
		}
	}

}
