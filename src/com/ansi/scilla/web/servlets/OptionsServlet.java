package com.ansi.scilla.web.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.common.db.PermissionLevel;
import com.ansi.scilla.web.common.AppUtils;
import com.ansi.scilla.web.common.Permission;
import com.ansi.scilla.web.common.ResponseCode;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.response.options.ResponseOption;
import com.ansi.scilla.web.response.options.OptionsListResponse;

/**
 * The url for HTTP get will be of the form /options/?&lt;name&gt;,&lt;name&gt;,&lt;name&gt;<br />
 * where &lt;name&gt; is the name of an value from OptionResponseOptions<br />
 * eg. /options?JOB_FREQUENCY,TICKET_STATUS<br />
 * Case matters<br />
 * <br />
 * HTTP POST and HTTP DELETE are not supported
 * 
 * @author dclewis
 *
 */
public class OptionsServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		try {
			AppUtils.validateSession(request, Permission.SYSADMIN, PermissionLevel.PERMISSION_LEVEL_IS_READ);
			List<ResponseOption> requestedOptions = new ArrayList<ResponseOption>();
			if ( StringUtils.isBlank(request.getQueryString())) {
				super.sendNotFound(response);
			} else {
				String[] requestedValues = request.getQueryString().split(",");
				Connection conn = null;
				try {
					conn = AppUtils.getDBCPConn();
					for ( String requestedValue : requestedValues ) {
						String key = StringUtils.trimToEmpty(requestedValue);
						if ( ! StringUtils.isBlank(key)) {
							ResponseOption o = ResponseOption.valueOf(key);
							requestedOptions.add(o);
						}
					}
					OptionsListResponse optionsListResponse = new OptionsListResponse(requestedOptions);
					super.sendResponse(conn, response, ResponseCode.SUCCESS, optionsListResponse);
				} catch ( Exception e) {
					AppUtils.logException(e);
					throw new ServletException(e);
				} finally {
					AppUtils.closeQuiet(conn);
				}
			}	
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e) {
			super.sendForbidden(response);
		}

	}




}
