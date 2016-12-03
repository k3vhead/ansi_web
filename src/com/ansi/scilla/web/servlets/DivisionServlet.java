package com.ansi.scilla.web.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.common.db.Code;
import com.ansi.scilla.web.common.AppUtils;
import com.ansi.scilla.web.common.ResponseCode;
import com.ansi.scilla.web.request.CodeRequest;
import com.ansi.scilla.web.response.codes.CodeListResponse;
import com.ansi.scilla.web.response.codes.CodeResponse;

/**
 * The url for delete will be of the form /code/<table>/<field>/<value>
 * 
 * The url for get will be one of:
 * 		/code    (retrieves everything)
 * 		/code/<table>      (filters code table by tablename)
 * 		/code/<table>/<field>	(filters code table tablename and field
 * 		/code/<table>/<field>/<value>	(retrieves a single record)
 * 
 * The url for adding a new record will be a POST to:
 * 		/code/new   with parameters in the JSON
 * 
 * The url for update will be a POST to:
 * 		/code/<table>/<field>/<value> with parameters in the JSON
 * 
 * 
 * @author dclewis
 *
 */
public class DivisionServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doDelete(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		Connection conn = null;
		try {
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);
			
			String jsonString = super.makeJsonString(request);
			CodeRequest codeRequest = new CodeRequest(jsonString);
			System.out.println(codeRequest);
			Code code = new Code();
			code.setTableName(codeRequest.getTableName());
			code.setFieldName(codeRequest.getFieldName());
			code.setValue(codeRequest.getValue());
			code.delete(conn);
			
			CodeResponse codeResponse = new CodeResponse();
			super.sendResponse(conn, response, ResponseCode.SUCCESS, codeResponse);
			
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
		String url = request.getRequestURI();
		int idx = url.indexOf("/code/");
		if ( idx > -1 ) {
			System.out.println("Url:" + url);
			String queryString = request.getQueryString();
			System.out.println("Query String: " + queryString);
			
			// we're in the right place
			Connection conn = null;
			try {
				conn = AppUtils.getDBCPConn();
				
				// Figure out what we've got:				
				String myString = url.substring(idx + "/code/".length());
				
				String[] urlPieces = myString.split("/");
				String command = urlPieces[0];
				
				if ( StringUtils.isBlank(command)) {
					super.sendNotFound(response);
				} else {
					if ( command.equals("list")) {
						// we're getting all the codes in the database
						CodeListResponse codesListResponse = makeCodesListResponse(conn);
						super.sendResponse(conn, response, ResponseCode.SUCCESS, codesListResponse);
					} else {
						CodeListResponse codesListResponse = makeFilteredListResponse(conn, urlPieces);
						super.sendResponse(conn, response, ResponseCode.SUCCESS, codesListResponse);
					}
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

	private CodeListResponse makeFilteredListResponse(Connection conn, String[] urlPieces) throws Exception {
		String tableName = null;
		String fieldName = null;
		String value = null;
		try {
			tableName = urlPieces[0];
			fieldName = urlPieces[1];
			value = urlPieces[2];
		} catch (ArrayIndexOutOfBoundsException e) {
			// this is OK, just means we ran out of filters
		}
		Code code = new Code();
		if ( ! StringUtils.isBlank(tableName)) {
			code.setTableName(tableName);
		}
		if ( ! StringUtils.isBlank(fieldName)) {
			code.setFieldName(fieldName);
		}
		if ( ! StringUtils.isBlank(value)) {
			code.setValue(value);
		}
		List<Code> codeList = Code.cast(code.selectSome(conn));
		Collections.sort(codeList,
				new Comparator<Code>() {
			public int compare(Code o1, Code o2) {
				int ret = o1.getTableName().compareTo(o2.getTableName());
				if ( ret == 0 ) {
					ret = o1.getFieldName().compareTo(o2.getFieldName());
				}
				if ( ret == 0 ) {
					ret = o1.getValue().compareTo(o2.getValue());
				}
				return ret;
			}
		});
		CodeListResponse codeListResponse = new CodeListResponse();
		codeListResponse.setCodeList(codeList);
		return codeListResponse;
	}

	
}
