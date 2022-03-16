package com.ansi.scilla.web.locale.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;

import com.ansi.scilla.common.db.Locale;
import com.ansi.scilla.common.utils.LocaleType;
import com.ansi.scilla.web.common.response.AbstractAutocompleteReturnItem;
import com.ansi.scilla.web.common.servlet.AbstractAutocompleteServlet;
import com.ansi.scilla.web.common.utils.AppUtils;


public class LocaleAutocompleteServlet extends AbstractAutocompleteServlet {

	private static final long serialVersionUID = 1L;
	
	protected final String LOCALE_TYPE_ID = "localeTypeId";
	protected final String STATE_NAME = "stateName";
	

	public LocaleAutocompleteServlet() {
		super(null);
	}
	



	
	@Override
	protected String doSearch(Connection conn, Map<String, String[]> map) throws SQLException, IOException {
		String sql = makeSql(map);
		
		logger.log(Level.DEBUG, sql);
		Statement s = conn.createStatement();
		ResultSet rs = s.executeQuery(sql);
		List<ReturnItem> resultList = new ArrayList<ReturnItem>();
		while ( rs.next() ) {
			resultList.add(new ReturnItem(rs));
		}
		rs.close();
		
		String json = AppUtils.object2json(resultList);
		
		return json;
	}
	
	
	private String makeSql(Map<String, String[]> map) {
		String term = map.get(TERM)[0];
		String sqlTemplate = "select top(50) locale_id, name, state_name, locale_type_id from locale\n" + 
				"where lower(name) like '%" + term.toLowerCase() + "%'";
		
		if ( map.containsKey(STATE_NAME) ) {
			String stateName = map.get(STATE_NAME)[0];
			if ( ! StringUtils.isEmpty(stateName) ) {		
				sqlTemplate = sqlTemplate + "\n and lower(state_name)='"+ stateName.toLowerCase() +"'";
			}
		}
		
		if ( map.containsKey(LOCALE_TYPE_ID) ) {
			String localeTypeId = map.get(LOCALE_TYPE_ID)[0];
			if ( ! StringUtils.isEmpty(localeTypeId) ) {
				List<String> localeTypeList = new ArrayList<String>();
				LocaleType localeType = LocaleType.valueOf(localeTypeId);
				List<LocaleType> parentTypeList = Arrays.asList(localeType.getParentTypes());
				localeTypeList = CollectionUtils.collect(parentTypeList, new LocaleTypeTransformer(), localeTypeList);
				String parmList = StringUtils.join(localeTypeList.iterator(), ",");
	
				sqlTemplate = sqlTemplate + " \n and locale_type_id in (" + parmList + ")";
			}
		}
		
		return sqlTemplate;
	}

	
	

	public class ReturnItem extends AbstractAutocompleteReturnItem {

		private static final long serialVersionUID = 1L;

		public ReturnItem(ResultSet rs) throws SQLException {
			super(rs);
		}

		@Override
		protected Integer formatId(ResultSet rs) throws SQLException {
			return rs.getInt(Locale.LOCALE_ID);
		}

		@Override
		protected String formatLabel(ResultSet rs) throws SQLException {
			return rs.getString(Locale.NAME) + ", " + rs.getString(Locale.STATE_NAME) + " (" + rs.getString(Locale.LOCALE_TYPE_ID) + ")";
		}

		@Override
		protected String formatName(ResultSet rs) throws SQLException {
			return rs.getString(Locale.NAME);
		}
		
	}
	
	
	
	public class LocaleTypeTransformer implements Transformer<LocaleType, String> {

		@Override
		public String transform(LocaleType arg0) {
			return "'" + arg0.toString() + "'";
		}
		
	}
	
	
}
