package com.ansi.scilla.web.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.web.common.AppUtils;
import com.ansi.scilla.web.common.MessageKey;
import com.ansi.scilla.web.common.ResponseCode;
import com.ansi.scilla.web.common.WebMessages;
import com.ansi.scilla.web.response.ticket.TicketDRVResponse;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class TicketDRV extends AbstractServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		Calendar today = Calendar.getInstance();
		Integer thisMonth = today.get(Calendar.MONTH);
		Integer thisYear = today.get(Calendar.YEAR);



		String url = request.getRequestURI();

		Connection conn = null;
		try {

			conn = AppUtils.getDBCPConn();
			String month = request.getParameter("month");
			if(StringUtils.isBlank(month)||!StringUtils.isNumeric(month)||Integer.valueOf(month)>12){
				month = String.valueOf(thisMonth);
			}
			Integer chosenMonth = Integer.valueOf(month);
			if(chosenMonth<=thisMonth){
				thisYear++;
			}

			String divisionId = request.getParameter("divisionId");
			WebMessages webMessages = new WebMessages();
			ResponseCode responseCode = null;
			TicketDRVResponse ticketDRVResponse = null;
			if(StringUtils.isBlank(divisionId)||!StringUtils.isNumeric(divisionId)){
				String messageText = AppUtils.getMessageText(conn, MessageKey.INVALID_DATA, "Invalid Data");
				webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, messageText);
				responseCode = ResponseCode.EDIT_FAILURE;
			}else{
				Integer chosenDivisionId = Integer.valueOf(divisionId);
				try{
					ticketDRVResponse = new TicketDRVResponse(conn, chosenDivisionId, chosenMonth, thisYear);
					String messageText = AppUtils.getMessageText(conn, MessageKey.SUCCESS, "Success");
					webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, messageText);
					responseCode = ResponseCode.SUCCESS;
				}catch(RecordNotFoundException e) {
					String messageText = AppUtils.getMessageText(conn, MessageKey.INVALID_DATA, "Invalid Data");
					webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, messageText);
					responseCode = ResponseCode.EDIT_FAILURE;
				}
			}
			super.sendResponse(conn, response, responseCode, ticketDRVResponse);
		} catch(RecordNotFoundException recordNotFoundEx) {
			super.sendNotFound(response);
		} catch ( Exception e) {
			AppUtils.logException(e);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}


	}


}
