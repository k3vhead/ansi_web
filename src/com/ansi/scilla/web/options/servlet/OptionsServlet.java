package com.ansi.scilla.web.options.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.options.response.OptionsListResponse;
import com.ansi.scilla.web.options.response.ResponseOption;

/**
 * The url for HTTP get will be of the form /options/?&lt;name&gt;,&lt;name&gt;,&lt;name&gt;
 * where &lt;name&gt; is the name of an value from OptionResponseOptions
 * eg. /options?JOB_FREQUENCY,TICKET_STATUS
 * Case matters
 * 
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
			SessionData sessionData = AppUtils.validateSession(request);
			
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
					OptionsListResponse optionsListResponse = new OptionsListResponse(requestedOptions, sessionData);
					super.sendResponse(conn, response, ResponseCode.SUCCESS, optionsListResponse);
				} catch ( Exception e) {
					AppUtils.logException(e);
					throw new ServletException(e);
				} finally {
					AppUtils.closeQuiet(conn);
				}
			}	
		} catch (TimeoutException e) {
			super.sendForbidden(response);
		}

	}




}
