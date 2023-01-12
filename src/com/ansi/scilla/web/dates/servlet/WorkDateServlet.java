package com.ansi.scilla.web.dates.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.dates.response.DateResponse;

/**
 * Given a calendar date, return everything we know about how ANSI will use that date
 * 
 * @author dclewis
 *
 */
public class WorkDateServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;
	
	public static final String WORK_DATE = "workDate";
	public static final String FORMAT = "format";
	
	private final String[] standardFormatList = new String[] {"yyyy-MM-dd","MM/dd/yyyy"};

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		String dateParm = request.getParameter(WORK_DATE);
		String dateFormat = request.getParameter(FORMAT);
		WebMessages webMessages = new WebMessages();
		ResponseCode responseCode = null;
		DateResponse dateResponse = new DateResponse();
		
		Connection conn = null;
		try {
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);
			Date workDate = makeDate(conn, webMessages, dateParm, dateFormat);
			if ( webMessages.isEmpty() ) {
				dateResponse = new DateResponse(conn, workDate);
				responseCode = ResponseCode.SUCCESS;
			} else {
				responseCode = ResponseCode.EDIT_FAILURE;				
			}
			dateResponse.setWebMessages(webMessages);
			super.sendResponse(conn, response, responseCode, dateResponse);
		} catch ( Exception e) {
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}
		
		
		
		
		
	}

	private Date makeDate(Connection conn, WebMessages webMessages, String dateParm, String dateFormat) {
		Date date = new Date();
		if ( ! StringUtils.isBlank(dateParm) ) {
			if ( StringUtils.isBlank(dateFormat)) {
				boolean foundOne = false;
				for ( String standardFormat : standardFormatList ) {
					try {
						SimpleDateFormat sdf = new SimpleDateFormat(standardFormat);
						date = sdf.parse(dateParm);
						foundOne = true;
					} catch (ParseException e) {
						// we don't care; just try the next one
					}
				}
				if (foundOne == false ) {
					webMessages.addMessage(WORK_DATE, "Invalid date/format combination");
				}
			} else {
				try {
					SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
					date = sdf.parse(dateParm);
				} catch (Exception e) {	
					webMessages.addMessage(WORK_DATE, "Invalid date/format combination");
				}
			}
		}
		return date;
	}

}
