package com.ansi.scilla.web.user.servlet;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.UserPermission;
import com.ansi.scilla.web.exceptions.ResourceNotFoundException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.user.query.DashboardFavoriteQuery;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class DashboardFavoritesServlet extends AbstractServlet {
	private static final long serialVersionUID = 1L;
	
	public static final String OPTION_TYPE = "type";

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		try {
			SessionData sessionData = AppUtils.validateSession(request);
			
			Connection conn = null;

			try {
				conn = AppUtils.getDBCPConn();
				List<String> permissionList = (List<String>) CollectionUtils.collect(sessionData.getUserPermissionList().iterator(), new PermissionTransformer());
				String optionTypeParm = request.getParameter(OPTION_TYPE);
				String optionType = OptionType.isValidKey(optionTypeParm) ? optionTypeParm : OptionType.LOOKUP.getKey();
				DashboardFavoriteQuery query = new DashboardFavoriteQuery(sessionData.getUser().getUserId(), permissionList, optionType);
				
				ResultSet rs = query.select(conn);
				String responseString = makeResponse(rs);
				
				response.setStatus(HttpServletResponse.SC_OK);
				response.setContentType("application/json");
				ServletOutputStream o = response.getOutputStream();
				OutputStreamWriter writer = new OutputStreamWriter(o);
				writer.write(responseString);
				writer.flush();
				writer.close();
			} catch(RecordNotFoundException e) {
				super.sendNotFound(response);
			} catch(ResourceNotFoundException e) {
				super.sendNotFound(response);
			} catch ( Exception e) {
				AppUtils.logException(e);
				throw new ServletException(e);
			} finally {
				AppUtils.closeQuiet(conn);
			}
			
		} catch (TimeoutException e) {
			super.sendForbidden(response);
		}
	}

	private String makeResponse(ResultSet rs) throws SQLException, JsonProcessingException {
		List<HashMap<String, Object>> dataList = new ArrayList<HashMap<String, Object>>();
		ResultSetMetaData rsmd = rs.getMetaData();
		while ( rs.next() ) {
			dataList.add(makeDataItem(rs, rsmd));
		}
		return AppUtils.object2json(dataList);
	}

	
	private HashMap<String, Object> makeDataItem(ResultSet rs, ResultSetMetaData rsmd) throws SQLException {
		HashMap<String, Object> dataItem = new HashMap<String, Object>();
		for ( int i = 0; i < rsmd.getColumnCount(); i++ ) {
			int idx = i + 1;
			String column = rsmd.getColumnName(idx);
			Object value = rs.getObject(idx);
			
			dataItem.put(column, value);
		}
		return dataItem;
	}
	
	public class PermissionTransformer implements Transformer<UserPermission, String> {

		@Override
		public String transform(UserPermission arg0) {
			return arg0.getPermissionName();
		}
		
	}
	
	public enum OptionType {
		LOOKUP("lookup"),
		REPORT("report"),
		QUICK_LINK("quickLink"),
		;
		
		private final String key;
		private OptionType(String key) {
			this.key = key;
		}
		
		public String getKey() { return this.key; }
		
		public static boolean isValidKey(String key) {
			boolean isValid = false;
			if ( ! StringUtils.isBlank(key) ) {
				for (OptionType type : OptionType.values() ) {
					if ( type.getKey().equalsIgnoreCase(key)) {
						isValid = true;
					}
				}
			}
			return isValid;
		}
	}
	
}
