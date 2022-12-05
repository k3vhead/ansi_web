package com.ansi.scilla.web.user.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;

import com.ansi.scilla.common.db.User;
import com.ansi.scilla.common.utils.Permission;
import com.ansi.scilla.web.ansiUser.AnsiUserResponse;
import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.common.utils.AnsiURL;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.ResourceNotFoundException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.report.common.SubscriptionUtils;
import com.ansi.scilla.web.user.request.AnsiUserRequest;
import com.ansi.scilla.web.user.response.UserResponse;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class AnsiUserServlet extends AbstractServlet {

	/**
	 * 
	 * @author jwlewis
	 */
	private static final long serialVersionUID = 1L;

	public static final String REALM = "user";
	public static final String FILTER_IS_MANAGER = "manager";
	public static final String FILTER_IS_LIST = "list";
	
	private static HashMap<Integer, Integer> statusMap = new HashMap<Integer, Integer>();
	
	static {
		statusMap.put(AnsiUserRequest.USER_STATUS_IS_ACTIVE, User.STATUS_IS_GOOD);
		statusMap.put(AnsiUserRequest.USER_STATUS_IS_INACTIVE, User.STATUS_IS_INACTIVE);
		statusMap.put(AnsiUserRequest.USER_STATUS_IS_LOCKED, User.STATUS_IS_LOCKED);
	}

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		try {
			AppUtils.validateSession(request);
			doGetWork(request, response);
		} catch (TimeoutException e) {
			super.sendForbidden(response);
		}
	}

	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		AnsiURL url = null;
		Connection conn = null;
		
		try {
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);
			SessionData sessionData = AppUtils.validateSession(request, Permission.USER_ADMIN_WRITE);
			SessionUser sessionUser = sessionData.getUser();
			String jsonString = super.makeJsonString(request);
			url = new AnsiURL(request, REALM, new String[] {ACTION_IS_ADD});
			if ( url.getId() == null && StringUtils.isBlank(url.getCommand())) {
				super.sendNotFound(response);
			} else {
				try {
					AnsiUserRequest userRequest = new AnsiUserRequest();
					AppUtils.json2object(jsonString, userRequest);
					User user = new User();
					if ( url.getId() == null ) {
						addUser(conn, request, response, user, userRequest, sessionUser);
					} else {
						user.setUserId(url.getId());
						user.selectOne(conn);
						updateUser(conn, request, response, user, userRequest, sessionUser);
					}
				}  catch ( InvalidFormatException e ) {
					String badField = super.findBadField(e.toString());
					AnsiUserResponse data = new AnsiUserResponse();
					WebMessages messages = new WebMessages();
					messages.addMessage(badField, "Invalid Format");
					data.setWebMessages(messages);
					super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, data);
				}
			}
			
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e) {
			super.sendForbidden(response);
		} catch (ResourceNotFoundException e) {
			super.sendNotAllowed(response);
		} catch ( RecordNotFoundException e ) {
			super.sendNotFound(response);
		} catch ( Exception e ) {
			AppUtils.logException(e);
			AppUtils.rollbackQuiet(conn);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}
	}


	private void doGetWork(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		Connection conn = null;
		UserResponse userResponse = new UserResponse();
		WebMessages messages = new WebMessages();
		try {
			conn = AppUtils.getDBCPConn();
			AnsiURL url = new AnsiURL(request, REALM, new String[] {FILTER_IS_LIST,FILTER_IS_MANAGER});	
			String sortField = request.getParameter("sortBy");
			logger.log(Level.DEBUG, "Sorting by: " + sortField);
			if ( ! StringUtils.isBlank(sortField)) {
				if ( ! ArrayUtils.contains(UserResponse.VALID_SORT_FIELDS, sortField) ) {
					sortField = null;
				}
			}
			logger.log(Level.DEBUG, "Still sorting by: " + sortField);
	
			if( url.getId() == null && StringUtils.isBlank(url.getCommand())) {
				throw new ResourceNotFoundException();
			} else if (url.getId() != null) {
				userResponse = new UserResponse(conn, url.getId());
			} else if ( ! StringUtils.isBlank(url.getCommand())) {
				UserResponse.UserListType listType = UserResponse.UserListType.valueOf(url.getCommand().toUpperCase());
				userResponse = new UserResponse(conn, listType);
			} else {
				// according to the URI parsing, this shouldn't happen, but it gives me warm fuzzies
				throw new ResourceNotFoundException();
			}
			if ( ! StringUtils.isBlank(sortField)) {
				userResponse.sort(sortField);
			}
			logger.log(Level.DEBUG, "user servlet 55");
			messages.addMessage(WebMessages.GLOBAL_MESSAGE, "Success");
			userResponse.setWebMessages(messages);
			super.sendResponse(conn, response, ResponseCode.SUCCESS, userResponse);
		} catch(RecordNotFoundException e) {
			logger.log(Level.DEBUG, "user servlet 60");
			super.sendNotFound(response);
		} catch(ResourceNotFoundException e) {
			logger.log(Level.DEBUG, "user servlet 63");
			super.sendNotFound(response);
		} catch ( Exception e) {
			AppUtils.logException(e);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}
	
	}


	private void addUser(Connection conn, HttpServletRequest request, HttpServletResponse response,
			User user, AnsiUserRequest userRequest, SessionUser sessionUser) throws Exception {
		WebMessages webMessages = new WebMessages();
		ResponseCode responseCode = null;
		UserResponse userResponse = new UserResponse();
		
		try {
			webMessages = userRequest.validateAdd(conn);
			if ( webMessages.isEmpty() ) {
				populateUser(user, userRequest, sessionUser);
				user.setAddedBy(sessionUser.getUserId());
				user.setPassword("New Pass");
				Integer userId = user.insertWithKey(conn);
				conn.commit();
				updatePassword(conn, userId, userRequest.getPassword());
				conn.commit();
				webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "Success");
				responseCode = ResponseCode.SUCCESS;
				userResponse = new UserResponse(conn, userId);
			} else {
				responseCode = ResponseCode.EDIT_FAILURE;
			}
		} catch (Exception e) {
			AppUtils.logException(e);
			conn.rollback();
			responseCode = ResponseCode.SYSTEM_FAILURE;
			webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "System Failure");
		}
		
		userResponse.setWebMessages(webMessages);
		super.sendResponse(conn, response, responseCode, userResponse);
	}


	
	private void updateUser(Connection conn, HttpServletRequest request, HttpServletResponse response,
			User user, AnsiUserRequest userRequest, SessionUser sessionUser) throws Exception {
		WebMessages webMessages = new WebMessages();
		ResponseCode responseCode = null;
		UserResponse userResponse = new UserResponse();
		
		try {
			webMessages = userRequest.validateUpdate(conn);
			if ( webMessages.isEmpty() ) {
				populateUser(user, userRequest, sessionUser);
				User key = new User();
				key.setUserId(userRequest.getUserId());
				user.update(conn, key);
				conn.commit();

				// we update subscriptions after committing the user change so the user shows up
				// in the list of group members for whom we need to check subscriptions.
				SubscriptionUtils.cureReportSubscriptions(conn, userRequest.getPermissionGroupId());
				conn.commit();

				if ( ! StringUtils.isBlank(userRequest.getPassword())) {
					updatePassword(conn, userRequest.getUserId(), userRequest.getPassword());
					conn.commit();
				}
				webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "Success");
				responseCode = ResponseCode.SUCCESS;
				userResponse = new UserResponse(conn, key.getUserId());
			} else {
				responseCode = ResponseCode.EDIT_FAILURE;
			}
		} catch (Exception e) {
			AppUtils.logException(e);
			conn.rollback();
			responseCode = ResponseCode.SYSTEM_FAILURE;
			webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "System Failure");
		}
		
		userResponse.setWebMessages(webMessages);
		super.sendResponse(conn, response, responseCode, userResponse);
	}


	private void populateUser(User user, AnsiUserRequest userRequest, SessionUser sessionUser) {
		user.setAddress1(userRequest.getAddress1());
		user.setAddress2(userRequest.getAddress2());
		user.setCity(userRequest.getCity());
		user.setEmail(userRequest.getEmail());
		user.setFirstName(userRequest.getFirstName());
		user.setLastName(userRequest.getLastName());
//		if ( ! StringUtils.isBlank(userRequest.getPassword())) {
//			String password = userRequest.getUserId() == null ? "New Pass" : AppUtils.encryptPassword(userRequest.getPassword(), userRequest.getUserId());user.setPassword("New Pass");
//			user.setPassword(password);
//		}
		user.setPermissionGroupId(userRequest.getPermissionGroupId());
		user.setPhone(userRequest.getPhone());
		user.setState(userRequest.getState());		
		user.setStatus(statusMap.get(userRequest.getStatus()));
		user.setSuperUser(User.SUPER_USER_IS_NO);
		user.setTitle(userRequest.getTitle());
		user.setUpdatedBy(sessionUser.getUserId());
		if ( userRequest.getUserId() != null ) {
			user.setUserId(userRequest.getUserId());
		}
		user.setZip(userRequest.getZip());
		user.setMinimumHourlyPay(userRequest.getMinimumHourlyPay());
	}

	


	private void updatePassword(Connection conn, Integer userId, String password) throws Exception {
		String encryptedPassword = AppUtils.encryptPassword(password, userId);
		PreparedStatement ps = conn.prepareStatement("update ansi_user set password=? where user_id=?");
		ps.setString(1, encryptedPassword);
		ps.setInt(2,  userId);
		ps.executeUpdate();
	}

}
