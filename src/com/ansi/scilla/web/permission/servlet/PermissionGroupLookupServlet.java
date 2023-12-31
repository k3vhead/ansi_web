package com.ansi.scilla.web.permission.servlet;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.common.utils.Permission;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.permission.query.PermissionGroupLookupSearch;
import com.ansi.scilla.web.permission.query.PermissionGroupSearchResult;
import com.ansi.scilla.web.permission.response.PermissionGroupLookupJsonResponse;

public class PermissionGroupLookupServlet extends AbstractServlet {

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
				"permission_group_id",
				"name",
				"description",
				"permission_group_status",
				"user_count",
				};
		String sStart = request.getParameter("start");
		String sAmount = request.getParameter("length");
		String sDraw = request.getParameter("draw");
		String sCol = request.getParameter("order[0][column]");
		String sdir = request.getParameter("order[0][dir]");


		Connection conn = null;
		try {
			conn = AppUtils.getDBCPConn();
			SessionData sessionData = AppUtils.validateSession(request, Permission.SYSADMIN_READ);
			SessionUser user = sessionData.getUser();
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
			
			
			PermissionGroupLookupSearch lookup = new PermissionGroupLookupSearch(user.getUserId());
			lookup.setSearchTerm(term);
			lookup.setSortBy(colName);
			lookup.setSortIsAscending(dir.equals("asc"));
			List<PermissionGroupSearchResult> itemList = lookup.select(conn, start, amount);
			logger.log(Level.DEBUG, "Records: " + itemList.size());
			Integer filteredCount = lookup.selectCount(conn);
			Integer totalCount = lookup.selectCountAll(conn);
			
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("application/json");

			PermissionGroupLookupJsonResponse jsonResponse = new PermissionGroupLookupJsonResponse();
			jsonResponse.setRecordsFiltered(filteredCount);
			jsonResponse.setRecordsTotal(totalCount);
			jsonResponse.setData(itemList);
			jsonResponse.setDraw(draw);

			String json = AppUtils.object2json(jsonResponse);

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
