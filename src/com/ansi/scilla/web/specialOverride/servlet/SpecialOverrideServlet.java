package com.ansi.scilla.web.specialOverride.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;

import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.utils.AnsiURL;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.Permission;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.ResourceNotFoundException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.specialOverride.common.ParameterType;
import com.ansi.scilla.web.specialOverride.common.SpecialOverrideType;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class SpecialOverrideServlet extends AbstractServlet {

	
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException { //  Note : modeled after recommended uri parsing pattern 2018-04-19 kjw
		AnsiURL url = null;
		Connection conn = null;
		WebMessages webMessages = new WebMessages();
		try {
			conn = AppUtils.getDBCPConn();
			AppUtils.validateSession(request, Permission.OVERRIDE_UPDATE_PAYMENTS);

			url = new AnsiURL(request, "specialOverrides", new String[] { ACTION_IS_LIST });	
			if ( StringUtils.isBlank(url.getCommand() )) {
				validateGroupId(conn, url.getId());
			}			
			//PermissionListResponse permissionListResponse = makePermissionListResponse(conn, url);
			webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "Success");										// add messages to the response
			
			//permissionListResponse.setWebMessages(webMessages);
			
			//super.sendResponse(conn, response, ResponseCode.SUCCESS, permissionListResponse);					// send the response
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e) {							// these are thrown by session validation
			super.sendForbidden(response);
		} catch ( RecordNotFoundException e ) {			// if they're asking for an id that doesn't exist
			super.sendNotFound(response);						
		} catch ( ResourceNotFoundException e) {		
			super.sendNotFound(response);
		} catch ( Exception e) {						// something bad happened
			AppUtils.logException(e);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);					// return the connection to the pool
		}
	}
	
	@SuppressWarnings("null")
	protected void validateGroupId(Connection conn, Integer paymentId) throws RecordNotFoundException, Exception{
		logger.log(Level.DEBUG, "validating group id: " + paymentId);
		SpecialOverrideType specialOverrideType = null;// = new SpecialOverrideType();
		ParameterType parameterType = null;
		parameterType.validateInteger(paymentId);
//		permissionGroup.setPermissionGroupId(paymentId);
//		permissionGroup.selectOne(conn);		// this throws RecordNotFound, which is propagated up the line into a 404 return
	}
	
	
	
}
