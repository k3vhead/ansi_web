package com.ansi.scilla.web.user.servlet;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.common.db.UserFavorite;
import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.Menu;
import com.ansi.scilla.web.common.utils.UserPermission;
import com.ansi.scilla.web.exceptions.ResourceNotFoundException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.report.common.ReportType;
import com.ansi.scilla.web.user.request.DashboardFavoriteRequest;
import com.ansi.scilla.web.user.response.DashboardFavoriteResponse;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class DashboardFavoritesServlet extends AbstractServlet {
	private static final long serialVersionUID = 1L;
	
	public static final String OPTION_TYPE = "type";
	public static final String REALM = "dashboardFavorite";

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
				optionTypeParm = StringUtils.isBlank(optionTypeParm) ? OptionType.LOOKUP.getKey() : optionTypeParm;
				OptionType optionType = OptionType.lookup(optionTypeParm) == null ? OptionType.LOOKUP : OptionType.lookup(optionTypeParm);
				List<String> dashboardFavoriteList = makeDashboardFavoriteList(conn, sessionData.getUser());
				DashboardFavoriteResponse data = new DashboardFavoriteResponse(optionType.getMenu(), dashboardFavoriteList, permissionList);
				
				super.sendResponse(conn, response, ResponseCode.SUCCESS, data);				
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
	
	
	
	

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			SessionData sessionData = AppUtils.validateSession(request);
			SessionUser sessionUser = sessionData.getUser();
			toggleFavorite(request, response, sessionUser);
		} catch (TimeoutException e) {
			super.sendForbidden(response);
		} catch ( Exception e) {
			AppUtils.logException(e);
			throw new ServletException(e);
		} 
	}


	private void toggleFavorite(HttpServletRequest request, HttpServletResponse response, SessionUser sessionUser) throws ServletException, UnsupportedEncodingException, IOException {
		Connection conn = null;
		DashboardFavoriteRequest dashboardFavoriteRequest = new DashboardFavoriteRequest();
		String jsonString = super.makeJsonString(request);

		try {
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);
			AppUtils.json2object(jsonString, dashboardFavoriteRequest);
			
			try {
				Menu menu = Menu.valueOf(dashboardFavoriteRequest.getMenu());
				processMenuRequest(conn, menu, response, sessionUser);
			} catch ( IllegalArgumentException iae ) {
				ReportType reportType = ReportType.valueOf(dashboardFavoriteRequest.getMenu());
				processReportRequest(conn, reportType, response, sessionUser);
			}
	
		} catch(IllegalArgumentException e) {
			// bad menu name & bad report name
			super.sendNotFound(response);
		} catch (Exception e) {
			AppUtils.logException(e);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}
	}


	private void processMenuRequest(Connection conn, Menu menu, HttpServletResponse response, SessionUser sessionUser) throws Exception {
		WebMessages messages = new WebMessages();
		Boolean selected = makeUserFavorite(conn, menu.getLink(), sessionUser);

		DashboardFavoriteResponse dashboardFavoriteResonse = new DashboardFavoriteResponse(menu, selected);
		messages.addMessage(WebMessages.GLOBAL_MESSAGE, "Success");
		dashboardFavoriteResonse.setWebMessages(messages);
		super.sendResponse(conn, response, ResponseCode.SUCCESS, dashboardFavoriteResonse);
		
	}





	private void processReportRequest(Connection conn, ReportType reportType, HttpServletResponse response, SessionUser sessionUser) throws Exception {
		WebMessages messages = new WebMessages();
		Boolean selected = makeUserFavorite(conn, reportType.getLink(), sessionUser);

		DashboardFavoriteResponse dashboardFavoriteResonse = new DashboardFavoriteResponse(reportType, selected);
		messages.addMessage(WebMessages.GLOBAL_MESSAGE, "Success");
		dashboardFavoriteResonse.setWebMessages(messages);
		super.sendResponse(conn, response, ResponseCode.SUCCESS, dashboardFavoriteResonse);
		
	}





	private Boolean makeUserFavorite(Connection conn, String link, SessionUser sessionUser) throws Exception {
		Boolean selected = null;
		UserFavorite userFavorite = new UserFavorite();
		userFavorite.setReportId(link);
		userFavorite.setUserId(sessionUser.getUserId());
		
		try {
			userFavorite.selectOne(conn);
			// this menu item is already favorited, so we're going to turn it off
			selected = false;
			userFavorite.delete(conn);
		} catch ( RecordNotFoundException notFound ) {
			// this menu item is not favorited, so we're going to turn it on
			selected = true;
			Date today = new Date();
			userFavorite.setAddedBy(sessionUser.getUserId());
			userFavorite.setAddedDate(today);
			userFavorite.setUpdatedBy(sessionUser.getUserId());
			userFavorite.setUpdatedDate(today);
			userFavorite.insertWithNoKey(conn);
		}
		conn.commit();		
		return selected;
	}





	/**
	 * Figure out which items have already been favorited
	 * @param conn
	 * @param user
	 * @return
	 * @throws Exception
	 */
	private List<String> makeDashboardFavoriteList(Connection conn, SessionUser user) throws Exception {
		UserFavorite userFavorite = new UserFavorite();
		userFavorite.setUserId(user.getUserId());
		List<UserFavorite> userFavoriteList = UserFavorite.cast(userFavorite.selectSome(conn));		
		return (List<String>)CollectionUtils.collect(userFavoriteList.iterator(), new FavoriteTransformer());
	}

	
	
	public class FavoriteTransformer implements Transformer<UserFavorite, String> {

		@Override
		public String transform(UserFavorite arg0) {
			return arg0.getReportId();
		}
		
	}
	
	public class PermissionTransformer implements Transformer<UserPermission, String> {

		@Override
		public String transform(UserPermission arg0) {
			return arg0.getPermissionName();
		}
		
	}
	
	public enum OptionType {
		LOOKUP("lookup", Menu.LOOKUPS),
		REPORT("report", Menu.REPORTS),
		QUICK_LINK("quickLink", Menu.QUICK_LINKS),
		;
		
		private static final HashMap<String, OptionType> lookup = new HashMap<String, OptionType>();
		private final String key;
		private final Menu menu;
		private OptionType(String key, Menu menu) {
			this.key = key;
			this.menu = menu;
		}
		
		static {
			for (OptionType type : OptionType.values() ) {
				lookup.put(type.getKey(), type);
			}
		}
		public static OptionType lookup(String key) { return lookup.get(key); }
		public String getKey() { return this.key; }
		public Menu getMenu() { return this.menu; }
		
		
		
	}	
}
