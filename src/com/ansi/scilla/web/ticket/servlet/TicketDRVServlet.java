package com.ansi.scilla.web.ticket.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.ansi.scilla.common.AnsiTime;
import com.ansi.scilla.web.common.response.MessageKey;
import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.common.utils.Permission;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.ticket.response.TicketDRVResponse;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class TicketDRVServlet extends AbstractServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		super.sendNotAllowed(response);
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		super.sendNotAllowed(response);
	}

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		Calendar today = Calendar.getInstance(new AnsiTime());
		Integer thisMonth = today.get(Calendar.MONTH);
		Integer thisYear = today.get(Calendar.YEAR);



		Connection conn = null;
		try {

			conn = AppUtils.getDBCPConn();
			AppUtils.validateSession(request, Permission.TICKET_READ);
			String month = request.getParameter("month");
			String format = request.getParameter("format");
			if(StringUtils.isBlank(month)||!StringUtils.isNumeric(month)||Integer.valueOf(month)>12){
				month = String.valueOf(thisMonth);
				logger.log(Level.DEBUG, "TicketDRVServlet: this month:" + month);

			}
			logger.log(Level.DEBUG, "TicketDRVServlet: this month:" + month);
			Integer chosenMonth = Integer.valueOf(month);
			if(chosenMonth<thisMonth){
				thisYear++;
			}

			String divisionId = request.getParameter("divisionId");
			logger.log(Level.DEBUG, "TicketDRVServlet: divisionId:" + divisionId);

			WebMessages webMessages = new WebMessages();
			ResponseCode responseCode = null;
			TicketDRVResponse ticketDRVResponse = new TicketDRVResponse();
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
			ticketDRVResponse.setWebMessages(webMessages);
			if(!StringUtils.isBlank(format) && format.equalsIgnoreCase("xls")){
				sendAsXLS(response, chosenMonth, thisYear, ticketDRVResponse);
			} else{
				super.sendResponse(conn, response, responseCode, ticketDRVResponse);
			}
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e) {
			super.sendForbidden(response);
		} catch(RecordNotFoundException recordNotFoundEx) {
			super.sendNotFound(response);
		} catch ( Exception e) {
			AppUtils.logException(e);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}


	}

	public void sendAsXLS(HttpServletResponse response, Integer month, Integer year, TicketDRVResponse ticketDRVResponse) throws IOException{
		SimpleDateFormat runDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String runDate = runDateFormat.format(ticketDRVResponse.getRunDate());
		
		SimpleDateFormat reportDateFormat = new SimpleDateFormat("yyyy-MM");
		String reportDate = reportDateFormat.format((new GregorianCalendar(year, month, 1)).getTime());
		
		String fileName = "DRV for Division " + ticketDRVResponse.getDivision().getDivisionNbr() + " for " + reportDate + " as of " + runDate + ".xlsx";
		fileName = fileName.replaceAll(" ","_");
		
		XSSFWorkbook workbook = ticketDRVResponse.toXLSX();
		OutputStream out = response.getOutputStream();
		
	    response.setHeader("Expires", "0");
	    response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
	    response.setHeader("Pragma", "public");
	    // setting the content type
	    response.setContentType("application/vnd.ms-excel");
	    String dispositionHeader = "attachment; filename=" + fileName;
		response.setHeader("Content-disposition",dispositionHeader);
	    // the contentlength
//	    response.setContentLength(baos.size());

		workbook.write(out);
		out.close();
	}

}
