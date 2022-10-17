package com.ansi.scilla.web.locale.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.common.AnsiTime;
import com.ansi.scilla.common.db.LocaleAlias;
import com.ansi.scilla.common.exceptions.DuplicateEntryException;
import com.ansi.scilla.web.common.request.RequestValidator;
import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.common.utils.AnsiURL;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.Permission;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.locale.response.LocaleAliasResponse;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class LocaleAliasServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;

	public static final String REALM = "alias";
	public static final String ALIAS_NAME = "aliasName";

	private final Logger logger = LogManager.getLogger(LocaleAliasServlet.class);

	/**
	 * 
	 * Note to avoid some confusion ("some", not necessarily "all")
	 * 
	 * There is no "update" function for the alias, only update and delete
	 * the url will be of the form "locale/alias/n" where "n" is some sort of ID
	 * 
	 *  If this is a "GET", the ID will be the locale alias id
	 *  If this is a "DELETE", the ID will be the locale alias id
	 *  If this is a "POST", we're creating a new alias (remember, no update function), so the ID is 
	 *        the ID for the locale for which we're creating an alias
	 * 
	 * 
	 */
	
	
	
	
	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		AnsiURL ansiURL = null;
		Connection conn = null;
		try {
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);
//			String jsonString = super.makeJsonString(request);
//			logger.log(Level.DEBUG, "jsonstring1:"+jsonString);
			
			ansiURL = new AnsiURL(request, REALM, (String[])null); 

			AppUtils.validateSession(request, Permission.TAX_WRITE);
			LocaleAliasResponse data = new LocaleAliasResponse();
			
			if(ansiURL.getId() == null) {
				super.sendNotFound(response);
			} else {
				LocaleAlias alias = new LocaleAlias();
				alias.setLocaleAliasId(ansiURL.getId());
				alias.delete(conn);
				conn.commit();
				super.sendResponse(conn, response, ResponseCode.SUCCESS, data);
			}
						
		} catch ( RecordNotFoundException e ) {
			super.sendNotFound(response);			
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e1) {
			super.sendForbidden(response);
		} catch ( Exception e) {
			AppUtils.logException(e);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doGet(request, response);
	}

	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		logger.log(Level.DEBUG, "Alias post");
		Connection conn = null;
		WebMessages webMessages = new WebMessages();
		ResponseCode responseCode = null;
		LocaleAliasResponse data = new LocaleAliasResponse();
		try {
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);
			try {
				String uri = request.getRequestURI();
				String[] uriPath = uri.split("/");
				Integer localeId = Integer.valueOf(uriPath[uriPath.length - 1]);
				Calendar today = Calendar.getInstance(new AnsiTime());
				
//				String jsonString = super.makeJsonString(request);
//				logger.log(Level.DEBUG, jsonString);
				SessionData sessionData = AppUtils.validateSession(request, Permission.PAYROLL_WRITE);
				String aliasName = request.getParameter(ALIAS_NAME);
				RequestValidator.validateString(webMessages, ALIAS_NAME, aliasName, true);
//				EmployeeRequest employeeRequest = new EmployeeRequest();
//				AppUtils.json2object(jsonString, employeeRequest);
//				webMessages = employeeRequest.validate(conn);
				if ( webMessages.isEmpty() ) {
					try {
						Integer localeAliasId = doUpdate(conn, localeId, aliasName, sessionData.getUser(), today);
						conn.commit();
						responseCode = ResponseCode.SUCCESS;
						data = new LocaleAliasResponse(conn, localeAliasId);
					} catch ( DuplicateEntryException e ) {
						webMessages.addMessage(ALIAS_NAME, "Duplicate Alias");
						responseCode = ResponseCode.EDIT_FAILURE;
					}
				} else {
					responseCode = ResponseCode.EDIT_FAILURE;
				}
				data.setWebMessages(webMessages);
				super.sendResponse(conn, response, responseCode, data);
			} catch ( RecordNotFoundException e) {
				super.sendNotFound(response);
			} finally {
				conn.close();
			}
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e1) {
			super.sendForbidden(response);
		} catch ( Exception e) {
			AppUtils.logException(e);
			AppUtils.rollbackQuiet(conn);
			throw new ServletException(e);
		}
	}
	
	
	private Integer doUpdate(Connection conn, Integer localeId, String aliasName, SessionUser sessionUser, Calendar today) throws DuplicateEntryException, RecordNotFoundException, Exception {
		Integer localeAliasId = null;
		
		LocaleAlias alias = new LocaleAlias();
		alias.setLocaleName(aliasName);
		alias.setLocaleId(localeId);
		
		try {
			alias.selectOne(conn);
			throw new DuplicateEntryException();
		} catch ( RecordNotFoundException e) {
			alias.setAddedBy(sessionUser.getUserId());
			alias.setAddedDate(today.getTime());
			alias.setUpdatedBy(sessionUser.getUserId());
			alias.setUpdatedDate(today.getTime());
			localeAliasId = alias.insertWithKey(conn);
		}
		
		return localeAliasId;
		
	}

}
