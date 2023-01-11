package com.ansi.scilla.web.payroll.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;

import com.ansi.scilla.common.AnsiTime;
import com.ansi.scilla.common.db.PayrollTaxProfile;
import com.ansi.scilla.common.utils.Permission;
import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.payroll.request.TaxProfileRequest;
import com.ansi.scilla.web.payroll.response.TaxProfileResponse;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class TaxProfileServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		logger.log(Level.DEBUG, "Tax Profile delete");
		Connection conn = null;
		try {
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);
			try {
				String uri = request.getRequestURI();  // this will be something like xxxx/taxProfile or xxxx/taxProfile/ or xxxx/taxProfile/123
				String[] uriPath = uri.split("/");
				String id = uriPath[uriPath.length - 1];				
				
				
//				String jsonString = super.makeJsonString(request);
//				logger.log(Level.DEBUG, jsonString);
				AppUtils.validateSession(request, Permission.PAYROLL_WRITE);

//				TaxProfileRequest profileRequest = new TaxProfileRequest();
//				AppUtils.json2object(jsonString, profileRequest);
//				webMessages = employeeRequest.validate(conn);
				if ( StringUtils.isBlank(id) || id.equals(PayrollServlet.TAX_PROFILE) ) {					
					super.sendNotFound(response);
				} else {
					Integer profileId = Integer.valueOf(id);
					processDelete(conn, response, profileId);
				}
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

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		logger.log(Level.DEBUG, "Tax Profile get");
		Connection conn = null;
		try {
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);
			try {
				String uri = request.getRequestURI();  // this will be something like xxxx/taxProfile or xxxx/taxProfile/ or xxxx/taxProfile/123
				String[] uriPath = uri.split("/");
				String id = uriPath[uriPath.length - 1];				
				
				
//				String jsonString = super.makeJsonString(request);
//				logger.log(Level.DEBUG, jsonString);
				AppUtils.validateSession(request, Permission.PAYROLL_READ);

//				TaxProfileRequest profileRequest = new TaxProfileRequest();
//				AppUtils.json2object(jsonString, profileRequest);
//				webMessages = employeeRequest.validate(conn);
				if ( StringUtils.isBlank(id) || id.equals(PayrollServlet.TAX_PROFILE) ) {					
					super.sendNotFound(response);
				} else {
					Integer profileId = Integer.valueOf(id);
					processGet(conn, response, profileId);
				}
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

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		logger.log(Level.DEBUG, "Tax Profile post");
		Connection conn = null;
		try {
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);
			try {
				String uri = request.getRequestURI();  // this will be something like xxxx/taxProfile or xxxx/taxProfile/ or xxxx/taxProfile/123
				String[] uriPath = uri.split("/");
				String id = uriPath[uriPath.length - 1];				
				
				
				String jsonString = super.makeJsonString(request);
				logger.log(Level.DEBUG, jsonString);
				SessionData sessionData = AppUtils.validateSession(request, Permission.PAYROLL_WRITE);

				TaxProfileRequest profileRequest = new TaxProfileRequest();
				AppUtils.json2object(jsonString, profileRequest);
//				webMessages = employeeRequest.validate(conn);
				if ( StringUtils.isBlank(id) || id.equals(PayrollServlet.TAX_PROFILE) ) {					
					processAdd(conn, response, sessionData, profileRequest);
				} else {
					Integer profileId = Integer.valueOf(id);
					processUpdate(conn, response, sessionData, profileId, profileRequest);
				}
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

	private void processGet(Connection conn, HttpServletResponse response, Integer profileId) throws Exception {
		try {
			TaxProfileResponse data = new TaxProfileResponse(conn, profileId);
			super.sendResponse(conn, response, ResponseCode.SUCCESS, data);
		} catch ( RecordNotFoundException e) {
			super.sendNotFound(response);
		}
		
	}

	private void processAdd(Connection conn, HttpServletResponse response, SessionData sessionData, TaxProfileRequest profileRequest) throws Exception {
		ResponseCode responseCode = null;
		TaxProfileResponse data = new TaxProfileResponse();
		Calendar today = Calendar.getInstance(new AnsiTime());

		WebMessages webMessages = profileRequest.validateAdd();
		if ( webMessages.isEmpty() ) {
			PayrollTaxProfile profile = new PayrollTaxProfile();
			profile.setAddedBy(sessionData.getUser().getUserId());
			profile.setAddedDate(today.getTime());
			profile.setDesc(profileRequest.getProfileDesc());
			profile.setOtHours(profileRequest.getOtHours());
			profile.setOtPay(profileRequest.getOtPay());
//			profile.setProfileId(null);
			profile.setRegularHours(profileRequest.getRegularHours());
			profile.setRegularPay(profileRequest.getRegularPay());
			profile.setUpdatedBy(sessionData.getUser().getUserId());
			profile.setUpdatedDate(today.getTime());
			profile.insertWithKey(conn);
			conn.commit();
			responseCode = ResponseCode.SUCCESS;
		} else {
			responseCode = ResponseCode.EDIT_FAILURE;
		}
		data.setWebMessages(webMessages);
		super.sendResponse(conn, response, responseCode, data);		
	}

	private void processUpdate(Connection conn, HttpServletResponse response, SessionData sessionData, Integer profileId,TaxProfileRequest profileRequest) throws Exception {
		Calendar today = Calendar.getInstance(new AnsiTime());
		
		try {
			PayrollTaxProfile profile = new PayrollTaxProfile();
			profile.setProfileId(profileId);
			profile.selectOne(conn);
			BeanUtils.copyProperties(profile, profileRequest);
			profile.setProfileId(profileId);
			profile.setDesc(profileRequest.getProfileDesc());
			profile.setUpdatedBy(sessionData.getUser().getUserId());
			profile.setUpdatedDate(today.getTime());
			
			PayrollTaxProfile key = new PayrollTaxProfile();
			key.setProfileId(profileId);
			profile.update(conn, key);
			conn.commit();			
			
			TaxProfileResponse data = new TaxProfileResponse(conn, profileId);			
			super.sendResponse(conn, response, ResponseCode.SUCCESS, data);
		} catch ( RecordNotFoundException e) {
			super.sendNotFound(response);
		}
		
	}

	private void processDelete(Connection conn, HttpServletResponse response, Integer profileId) throws Exception {
		try {
			PayrollTaxProfile profile = new PayrollTaxProfile();
			profile.setProfileId(profileId);
			profile.delete(conn);
			conn.commit();
			super.sendResponse(conn, response, ResponseCode.SUCCESS, new TaxProfileResponse());
		} catch ( RecordNotFoundException e ) {
			// we're trying to delete something that isn't there -- we don't care;
			super.sendResponse(conn, response, ResponseCode.SUCCESS, new TaxProfileResponse());
		}
	}

	

	
}
