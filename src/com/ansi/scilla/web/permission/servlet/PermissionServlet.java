package com.ansi.scilla.web.permission.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.common.db.PermissionGroup;
import com.ansi.scilla.common.db.PermissionGroupLevel;
import com.ansi.scilla.common.db.User;
import com.ansi.scilla.common.utils.QMarkTransformer;
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
import com.ansi.scilla.web.exceptions.ResourceNotFoundException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.permission.common.PermissionUtils;
import com.ansi.scilla.web.permission.request.PermissionRequest;
import com.ansi.scilla.web.permission.response.PermissionGroupResponse;
import com.ansi.scilla.web.permission.response.PermissionListResponse;
import com.ansi.scilla.web.report.common.BatchReports;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.thewebthing.commons.db2.RecordNotFoundException;
/**
  The url for GET will be one of:
  
 		GET permission/<permissionGroupId> returns:
 		{
  			"webMessages": null,
  			"permissionList": [
  				"QUOTE",
  				"QUOTE_READ",
  				"QUOTE_WRITE",
  				"INVOICE",
  				"INVOICE_READ",
  				]
  		}
 ***********************************************************************************
 		GET permission/list returns:
 		{
  			"webMessages": null,
 			"permmissionList": [{
 				"permissionName": "INVOICE",
 				"level": 1 }, {
 				"permissionName": "INVOICE",
 				"level": 0 }, {
				"permissionName": "JOB",
				"level": 1 }, {
				"permissionName": "JOB",
				"level": 0 }]
		}
 
 ***********************************************************************************
  
  The url for update will be a POST to:
  		/permission/<permissionGroupId#>,	(returns a list of permissions having the same groupId)
  
  		data to pass in will look like : 
 			{	"permissionGroupId": 123,
 				permissionName:	"QUOTE",
 				permissionLevel: -1
 			}
 */

public class PermissionServlet extends AbstractServlet {
	/**
	 * @author jwlewis
	 * @author kwagner;
	 */
	protected final Logger logger = LogManager.getLogger(PermissionServlet.class);
	
	private static final long serialVersionUID = 1L;



	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException { //  Note : modeled after recommended uri parsing pattern 2018-04-19 kjw
		AnsiURL url = null;
		Connection conn = null;
		WebMessages webMessages = new WebMessages();
		try {
			conn = AppUtils.getDBCPConn();
			AppUtils.validateSession(request, Permission.PERMISSIONS_READ);

			url = new AnsiURL(request, "permission", new String[] { ACTION_IS_LIST });	
			boolean isValidId = RequestValidator.validateId(conn, new WebMessages(), PermissionGroup.TABLE, PermissionGroup.PERMISSION_GROUP_ID, WebMessages.GLOBAL_MESSAGE, url.getId(), true);
			if ( isValidId ) {
				PermissionListResponse permissionListResponse = makePermissionListResponse(conn, url);
				webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "Success");										// add messages to the response
				
				permissionListResponse.setWebMessages(webMessages);
				
				super.sendResponse(conn, response, ResponseCode.SUCCESS, permissionListResponse);					// send the response
			} else {
				super.sendNotFound(response);
			}
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e) {							// these are thrown by session validation
			super.sendForbidden(response);
		} catch ( ResourceNotFoundException e) {		
			super.sendNotFound(response);
		} catch ( Exception e) {						// something bad happened
			AppUtils.logException(e);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);					// return the connection to the pool
		}
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {  // note : from contactServlet..
		AnsiURL url = null;
		Connection conn = null;
		try {
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);

			SessionData sessionData = AppUtils.validateSession(request, Permission.PERMISSIONS_WRITE);
			SessionUser sessionUser = sessionData.getUser();

			try {
				String jsonString = super.makeJsonString(request);								
				PermissionRequest permissionRequest = new PermissionRequest();

				if (!StringUtils.isBlank(jsonString)) {
					AppUtils.json2object(jsonString, permissionRequest);
				}
				
				url = new AnsiURL(request,"permission", (String []) null);

				boolean isValidId = RequestValidator.validateId(conn, new WebMessages(), PermissionGroup.TABLE, PermissionGroup.PERMISSION_GROUP_ID, WebMessages.GLOBAL_MESSAGE, url.getId(), true);
				if ( isValidId ) {
					WebMessages webMessages = permissionRequest.validate();
					if ( webMessages.isEmpty() ) {
						processUpdate(conn, response, url.getId(), permissionRequest, sessionUser);
					} else {
						sendEditError(conn, request, response, url.getId(), webMessages);
					}
				} else {
					super.sendNotFound(response);
				}
				

			} catch ( InvalidFormatException e ) {
				String badField = super.findBadField(e.toString());
				PermissionGroupResponse data = new PermissionGroupResponse();
				WebMessages messages = new WebMessages();
				messages.addMessage(badField, "Invalid Format");
				data.setWebMessages(messages);
				super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, data);
			} catch ( RecordNotFoundException e ) {
				super.sendNotFound(response);
			} catch (ResourceNotFoundException e_AnsiURL) {
				super.sendNotFound(response);
			}
		} catch (NotAllowedException | TimeoutException | ExpiredLoginException e_validateSession) {
			super.sendForbidden(response);
		} catch ( Exception e ) {   // SQLException and NamingException are subclasses of Exception 
			AppUtils.logException(e);
			AppUtils.rollbackQuiet(conn);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}
	}			
	
	
	
	/**
	 * 1. Remove all permissions in the functional area 
	 * 		a. get list of permissions in area
	 * 		b. delete permission_group_level records for permission_group_id where name in list
	 * 2. Add new permission
	 * 		a. insert permission_group_level record for permission_group_id
	 * 3. Get list of permissions for permission_group_id (just like when you log in)
	 * 4. Remove report invalid report subscriptions
	 * 		a. get list of users in permission group
	 * 		b. get list of subscriptions for those users
	 * 		c. remove subscriptions for reports that require a permission outside the list
	 * 5. Big sigh of relief if you haven't messed everybody up.
	 *    
	 * @param conn
	 * @param response
	 * @param permissionGroupId
	 * @param permissionRequest
	 * @param sessionUser
	 * @throws Exception 
	 */
	private void processUpdate(Connection conn, HttpServletResponse response, Integer permissionGroupId,
			PermissionRequest permissionRequest, SessionUser sessionUser) throws Exception {
		Permission functionalArea = Permission.valueOf(permissionRequest.getFunctionalArea());
		Permission newPermission = StringUtils.isBlank(permissionRequest.getPermission()) ? null : Permission.valueOf(permissionRequest.getPermission());
		
		deleteOldPermissions(conn, permissionGroupId, functionalArea);
		// if we're removing a permission and not replacing it
		if ( newPermission != null ) {
			addNewPermission(conn, permissionGroupId, newPermission, sessionUser);
		}
		
		/*
		 * get list of users in permissionGroupId, get list of subscriptions for each user,
		 * remove each sub not in line with the permissions of permissionGroupId
		 */
		List<Permission> permissionList = PermissionUtils.makeGroupList(conn, permissionGroupId);
		List<User> userList = getUserList(conn, permissionGroupId);
		
		List<BatchReports> reportList = new ArrayList<BatchReports>();
		
		for(Permission p : permissionList) {
			for(BatchReports br : BatchReports.values()) {
				if(br.permission().equals(p)) {
					reportList.add(br);
				}
			}
		}
		
		List<Object> subUserList = IteratorUtils.toList(CollectionUtils.collect(userList, new UserToId()).iterator());
		List<Object> subReportList = IteratorUtils.toList(CollectionUtils.collect(reportList, new ReportToName()).iterator());
		
		String sql = getSql(subUserList, subReportList);
		PreparedStatement ps = conn.prepareStatement(sql);
		
		int n = 1;
		for(int i = 0; i < userList.size(); i++) {
			ps.setInt(n, userList.get(i).getUserId());
			n++;
		}
		for(int i = 0; i < reportList.size(); i++) {
			ps.setString(n, reportList.get(i).name());
			n++;
		}
		
		ResultSet rs = ps.executeQuery();
		rs.close();
		conn.commit();
		
		PermissionListResponse data = new PermissionListResponse(conn, permissionGroupId);
		data.setWebMessages(new WebMessages());
		super.sendResponse(conn, response, ResponseCode.SUCCESS, data);
	}
	
	private List<User> getUserList(Connection conn, Integer permissionGroupId) throws Exception{
		User key = new User();
		key.setPermissionGroupId(permissionGroupId);
		List<User> userList = User.cast(key.selectSome(conn));
		return userList;
	}

	private String getSql(List<Object> subUserList, List<Object> subReportList) {
		String sql = "select from report_subscription where user_id in (" + QMarkTransformer.makeQMarkWhereClause(subUserList) + ") "
				+ "and report_id not in (" + QMarkTransformer.makeQMarkWhereClause(subReportList) + ")";
		return sql;
	}
	
	private void deleteOldPermissions(Connection conn, Integer permissionGroupId, Permission functionalArea) throws SQLException {
		List<Permission> permissionsInFunctionalArea = functionalArea.makeFunctionalAreaTree();
		List<Object> subNameList = IteratorUtils.toList(CollectionUtils.collect(permissionsInFunctionalArea, new PermissionToName()).iterator());
		String sql = "delete from permission_group_level where permission_group_id=? and permission_name in " + QMarkTransformer.makeQMarkWhereClause(subNameList);
		logger.log(Level.DEBUG, sql);
		PreparedStatement ps = conn.prepareStatement(sql);
		int n = 1;
		ps.setInt(n, permissionGroupId);
		n++;
		for ( Object o : subNameList ) {
			ps.setString(n, (String)o);
			n++;
		}
		ps.executeUpdate();
		
	}

	private void addNewPermission(Connection conn, Integer permissionGroupId, Permission newPermission, SessionUser sessionUser) throws Exception {
		Date now = new Date();
		PermissionGroupLevel level = new PermissionGroupLevel();
		level.setPermissionGroupId(permissionGroupId);
		level.setPermissionName(newPermission.name());
		level.setPermissionLevel(1);
		level.setAddedBy(sessionUser.getUserId());
		level.setAddedDate(now);
		level.setUpdatedBy(sessionUser.getUserId());
		level.setUpdatedDate(now);
		level.insertWithNoKey(conn);
		
	}

	private void sendEditError(Connection conn, HttpServletRequest request, HttpServletResponse response,
			Integer permissionGroupId, WebMessages webMessages) throws Exception {
		PermissionListResponse data = new PermissionListResponse(conn, permissionGroupId);
		data.setWebMessages(webMessages);
		super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, data);
		
	}

	
	protected PermissionListResponse makePermissionListResponse(Connection conn, AnsiURL url) throws Exception {
		
		PermissionListResponse permissionListResponse;
		
		if (url.getId() != null) {
			// if an ID was passed in, then get all permissions having that group ID.
			// note : this queries the permission_group_level table.
			permissionListResponse = new PermissionListResponse(conn, url.getId());			
		} else {
			// If no ID return LIST of all permissions
			// then return all permissions in all groups
			permissionListResponse = new PermissionListResponse();			
		}
		return permissionListResponse;
	}	
	
	
	
	
	public class PermissionToName implements Transformer<Permission, String> {

		@Override
		public String transform(Permission arg0) {			
			return arg0.name();
		}
		
	}
	
	public class UserToId implements Transformer<User, Integer> {

		@Override
		public Integer transform(User arg0) {			
			return arg0.getUserId();
		}
		
	}
	
	public class ReportToName implements Transformer<BatchReports, String> {

		@Override
		public String transform(BatchReports arg0) {			
			return arg0.name();
		}
		
	}
	
//	public static void main(String[] args ) {
//		Connection conn = null;
//		try {
//			conn = AppUtils.getDevConn();
//			List<User> usersList = new PermissionServlet().getUserList(conn, 1);
//			List<Permission> permissionList = PermissionUtils.makeGroupList(conn, 1);
//			List<BatchReports> reportList = Collections.emptyList();
//			
//			for(Permission p : permissionList) {
//				for(BatchReports br : BatchReports.values()) {
//					if(br.permission().equals(p)) {
//						reportList.add(br);
//					}
//				}
//			}
//			
//			for ( User user : usersList ) {
//				System.out.println(user.getUserId() + "\t" + user.getLastName() + "\t" + user.getFirstName());
//			}
//			for(Permission permission : permissionList) {
//				System.out.println(permission.name() + "\t" + permission.getDescription());
//			}
//			for(BatchReports br : reportList) {
//				System.out.println(br.abbreviation() + "\t" + br.name() + "\t" + br.description());
//			}
//			
//		} catch ( Exception e ) {
//			e.printStackTrace();
//		} finally {
//			AppUtils.closeQuiet(conn);
//		}
//	}
}
