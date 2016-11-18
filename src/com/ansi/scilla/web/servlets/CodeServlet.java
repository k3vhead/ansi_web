package com.ansi.scilla.web.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ansi.scilla.common.db.Code;
import com.ansi.scilla.web.common.AppUtils;
import com.ansi.scilla.web.common.ResponseCode;
import com.ansi.scilla.web.request.CodeRequest;
import com.ansi.scilla.web.response.codes.CodeListResponse;
import com.ansi.scilla.web.response.codes.CodeResponse;

public class CodeServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doDelete(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		Connection conn = null;
		try {
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);
			
			String jsonString = super.makeJsonString(request);
			CodeRequest codesRequest = new CodeRequest(jsonString);
			System.out.println(codesRequest);
			Code codes = new Code();
			codes.setTableName(codesRequest.getTableName());
			codes.setFieldName(codesRequest.getFieldName());
			codes.setValue(codesRequest.getValue());
			System.out.println(codes);
			codes.delete(conn);
			
			CodeResponse codesResponse = new CodeResponse();
			super.sendResponse(conn, response, ResponseCode.SUCCESS, codesResponse);
			
			conn.commit();
		} catch ( Exception e) {
			AppUtils.logException(e);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}
	}

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		Pattern uriPattern = Pattern.compile("^(.*/)(.*)(\\.)(.*)$", Pattern.CASE_INSENSITIVE);
		Matcher matcher = uriPattern.matcher(request.getRequestURI());
		if ( matcher.matches() ) {
//			String format = matcher.group(4);  // for now this is always json, so ignore it
			String command = matcher.group(2);
			
			Connection conn = null;
			try {
				conn = AppUtils.getDBCPConn();
				if ( command.equals("getList")) {
					CodeListResponse codesListResponse = makeCodesListResponse(conn);
					super.sendResponse(conn, response, ResponseCode.SUCCESS, codesListResponse);
				} else if ( command.equals("get")) {
					HashMap<String, String> params = super.makeParamMap(request.getQueryString());
					CodeResponse codesResponse = makeCodeResponse(conn, params);
					super.sendResponse(conn, response, ResponseCode.SUCCESS, codesResponse);
				} else {
					super.sendNotFound(response);
				}
			} catch ( Exception e) {
				AppUtils.logException(e);
				throw new ServletException(e);
			} finally {
				AppUtils.closeQuiet(conn);
			}
			
		} else {
			super.sendNotFound(response);
		}
	}


	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		throw new ServletException("Not Yet Coded");
	}

	private CodeListResponse makeCodesListResponse(Connection conn) throws Exception {
		CodeListResponse codesListResponse = new CodeListResponse(conn);
		return codesListResponse;
	}

	private CodeResponse makeCodeResponse(Connection conn, HashMap<String, String> params) {
		return new CodeResponse();
	}
	
}
