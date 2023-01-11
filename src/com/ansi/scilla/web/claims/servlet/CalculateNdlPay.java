package com.ansi.scilla.web.claims.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.common.claims.WorkHoursType;
import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.common.db.User;
import com.ansi.scilla.web.common.request.RequestValidator;
import com.ansi.scilla.web.common.response.MessageResponse;
import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.common.utils.Permission;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.ResourceNotFoundException;
import com.ansi.scilla.web.exceptions.ScillaException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thewebthing.commons.db2.DBTable;

/**
 * Based on input, calculate the pay for non-direct labor. Input will be validated, but no
 * error messages generated. Any invalid or missing data will cause the return value to be
 * "N/A". Else pay is calculated based on the non-direct labor type (WorkHoursType enum)
 * 
 * @author dclewis
 *
 */
public class CalculateNdlPay extends AbstractServlet {

	private static final long serialVersionUID = 1L;
	
	private static final String WASHER_ID = "washerId";
	private static final String DIVISION_ID =  "divisionId";
	private static final String WORK_DATE =  "workDate";
	private static final String HOURS =  "hours";
	private static final String HOURS_TYPE =  "hoursType";
	
	private Logger logger;
	
	

	public CalculateNdlPay() {
		super();
		this.logger = LogManager.getLogger(this.getClass());
	}



	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		logger.log(Level.DEBUG, "Processing Post");
//		SessionUser sessionUser = AppUtils.getSessionUser(request);
		try {
			String jsonString = super.makeJsonString(request);
			logger.log(Level.DEBUG, jsonString);
			AppUtils.validateSession(request, Permission.CLAIMS_READ);
			Connection conn = null;
			try {
				conn = AppUtils.getDBCPConn();
				conn.setAutoCommit(false);

				ObjectMapper mapper = new ObjectMapper();
				mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
				JsonNode jsonNode = mapper.readTree(jsonString);
				
				
				CalculatedPayout calculatedPayout = new CalculatedPayout("N/A");
				try {
					validateInput(conn, jsonNode);
					calculatedPayout = calculatePay(conn, jsonNode);
				} catch ( InvalidNdlException e ) {
					// there was something wrong with the input, so send the default "N/A" response
				}
				super.sendResponse(conn, response, ResponseCode.SUCCESS, calculatedPayout);
				
			} catch ( IOException formatException) {
				AppUtils.logException(formatException);
				super.sendResponse(conn, response, ResponseCode.SYSTEM_FAILURE, null);
			} catch ( Exception e ) {
				AppUtils.logException(e);
				AppUtils.rollbackQuiet(conn);
				throw new ServletException(e);
			} finally {
				AppUtils.closeQuiet(conn);
			}

		} catch ( ResourceNotFoundException e) {
			super.sendNotFound(response);
		} catch (TimeoutException  | NotAllowedException | ExpiredLoginException e1) {
			super.sendForbidden(response);
		} catch ( Exception e) {
			AppUtils.logException(e);
			throw new ServletException(e);
		}
	}



	private void validateInput(Connection conn, JsonNode jsonNode) throws InvalidNdlException {
		WebMessages webMessages = new WebMessages();
		try {
			RequestValidator.validateId(conn, webMessages, User.class.getAnnotation(DBTable.class).value(), User.USER_ID, WASHER_ID, jsonNode.get(WASHER_ID).asInt(), true, null);
			RequestValidator.validateId(conn, webMessages, Division.TABLE, Division.DIVISION_ID, DIVISION_ID, jsonNode.get(DIVISION_ID).asInt(), true, null);
			RequestValidator.validateDate(webMessages, WORK_DATE, jsonNode.get(WORK_DATE).asText(), "MM/dd/yyyy", true, null, null);
			RequestValidator.validateNumber(webMessages, HOURS, jsonNode.get(HOURS).asDouble(), null, null, true, null);
			RequestValidator.validateWorkHoursType(webMessages, HOURS_TYPE, jsonNode.get(HOURS_TYPE).asText(), true);
		} catch ( Exception e ) {
			AppUtils.logException(e);
			throw new InvalidNdlException();
		}
		
		if ( ! webMessages.isEmpty() ) {
			throw new InvalidNdlException();
		}
		
	}



	private CalculatedPayout calculatePay(Connection conn, JsonNode jsonNode) {

		CalculatedPayout calculatedPayout = new CalculatedPayout();
		try {
			Integer washerId = jsonNode.get(WASHER_ID).asInt();
			Integer divisionId = jsonNode.get(DIVISION_ID).asInt();
			String dateText = jsonNode.get(WORK_DATE).asText();
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
			Date workDate = sdf.parse(dateText);
			Double hours =  jsonNode.get(HOURS).asDouble();
			WorkHoursType workHoursType = WorkHoursType.valueOf(jsonNode.get(HOURS_TYPE).asText());
			Double payoutAmt = workHoursType.calculateDefaultPay(conn, divisionId, washerId, workDate, hours);
			DecimalFormat decimalFormat = new DecimalFormat("$#0.00");
			String calcPay = decimalFormat.format(payoutAmt);
			calculatedPayout.setCalculatedPay(calcPay);
		} catch ( Exception e) {
			calculatedPayout.setCalculatedPay("N/A");
		}
		return calculatedPayout;
	}



	public class CalculatedPayout extends MessageResponse {

		private static final long serialVersionUID = 1L;

		private String calculatedPay;

		public CalculatedPayout() {
			super();
		}

		public CalculatedPayout(WebMessages webMessages) {
			super(webMessages);
		}

		public CalculatedPayout(String calculatedPay) {
			super();
			this.calculatedPay = calculatedPay;
		}

		public String getCalculatedPay() {
			return calculatedPay;
		}

		public void setCalculatedPay(String calculatedPay) {
			this.calculatedPay = calculatedPay;
		}
		
	}
	
	public class InvalidNdlException extends ScillaException {
		private static final long serialVersionUID = 1L;
	}
}
