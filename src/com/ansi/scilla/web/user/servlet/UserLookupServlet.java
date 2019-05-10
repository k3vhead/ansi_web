package com.ansi.scilla.web.user.servlet;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.common.db.PermissionLevel;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.Permission;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.user.query.UserLookup;
import com.ansi.scilla.web.user.query.UserLookupItem;
import com.ansi.scilla.web.user.response.UserLookupJsonResponse;

public class UserLookupServlet extends AbstractServlet {

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
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Logger logger = LogManager.getLogger(this.getClass());
		int amount = 10;
		int start = 0;
		int draw = 0;
		int col = 0;
		String dir = "asc";
		String[] cols = { 
				"ansi_user.user_id",
				"ansi_user.last_name",
				"ansi_user.first_name",
				"ansi_user.email",
				"ansi_user.phone",
				"ansi_user.city",
				"permission_group.name",
				"ansi_user.user_status",
				};
//		String[] cols = { 
//				"ansi_user.user_id",
//				"ansi_user.last_name",
//				"ansi_user.first_name",
//				"ansi_user.email",
//				"ansi_user.phone",
//				"ansi_user.state",
//				"ansi_user.city",
//				"permission_group.name as permission_group_name",
//				"ansi_user.user_status",
//				"ansi_user.title",
//				};
		String sStart = request.getParameter("start");
		String sAmount = request.getParameter("length");
		String sDraw = request.getParameter("draw");
		String sCol = request.getParameter("order[0][column]");
		String sdir = request.getParameter("order[0][dir]");
		
		String permissionGroupParm = request.getParameter("permissionGroupId");
		Integer permissionGroupId = StringUtils.isBlank(permissionGroupParm) ? null : Integer.valueOf(permissionGroupParm);

		logger.log(Level.DEBUG, "PermissionGroupId: " + permissionGroupId);
		Connection conn = null;
		try {
			conn = AppUtils.getDBCPConn();
			AppUtils.validateSession(request, Permission.SYSADMIN);
//			SessionUser user = sessionData.getUser();
			String term = "";

			if(request.getParameter("search[value]") != null){
				term = request.getParameter("search[value]");
			}
			
			if (sStart != null) {
				start = Integer.parseInt(sStart);
				start = start < 0 ? 0 : start;
			}
			if (sAmount != null) {
				amount = Integer.parseInt(sAmount);
				if (amount < 10 ) {
					amount = 10;
				} else if (amount > 1000) {
					amount = 1000;
				}
			}
			if (sDraw != null) {
				draw = Integer.parseInt(sDraw);
			}
			if (sCol != null) {
				col = Integer.parseInt(sCol);
				if (col < 0 || col > 15) {
					col = 0;
				}
			}
			if (sdir != null) {
				if (!sdir.equals("asc")) {
					dir = "desc";
				}
			}

			String colName = cols[col];


			logger.log(Level.DEBUG, "Start: " + start + "\tAmount: " + amount + "\tTerm: " + term);
			
			
			UserLookup userLookup = new UserLookup(permissionGroupId, start, amount);
			userLookup.setSearchTerm(term);
			userLookup.setSortBy(colName);
			userLookup.setSortIsAscending(dir.equals("asc"));
			List<UserLookupItem> itemList = userLookup.select(conn);
			logger.log(Level.DEBUG, "Records: " + itemList.size());
			Integer filteredCount = userLookup.selectCount(conn);
			Integer totalCount = userLookup.countAll(conn);
			
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("application/json");

			UserLookupJsonResponse userLookupJsonResponse = new UserLookupJsonResponse();
			userLookupJsonResponse.setRecordsFiltered(filteredCount);
			userLookupJsonResponse.setRecordsTotal(totalCount);
			userLookupJsonResponse.makeData(itemList);
			userLookupJsonResponse.setDraw(draw);

			String json = AppUtils.object2json(userLookupJsonResponse);

			ServletOutputStream o = response.getOutputStream();
			OutputStreamWriter writer = new OutputStreamWriter(o);
			writer.write(json);
			writer.flush();
			writer.close();
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e) {
			super.sendForbidden(response);
		} catch ( Exception e ) {
			AppUtils.logException(e);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}
	}
}
