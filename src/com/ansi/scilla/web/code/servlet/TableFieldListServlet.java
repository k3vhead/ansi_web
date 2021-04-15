package com.ansi.scilla.web.code.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ansi.scilla.common.db.PermissionLevel;
import com.ansi.scilla.web.code.response.TableFieldListResponse;
import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.Permission;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class TableFieldListServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Connection conn = null;
		try {
			conn = AppUtils.getDBCPConn();
//			conn = AppUtils.getConn();
			AppUtils.validateSession(request, Permission.SYSADMIN, PermissionLevel.PERMISSION_LEVEL_IS_READ);

			List<String> resultList = doGetWork(conn, request.getRequestURI());			
			TableFieldListResponse tableFieldListResponse = new TableFieldListResponse( resultList);
			super.sendResponse(conn, response, ResponseCode.SUCCESS, tableFieldListResponse);
		} catch ( TimeoutException | ExpiredLoginException | NotAllowedException e) {
			super.sendForbidden(response);
		} catch ( RecordNotFoundException e) {
			super.sendNotFound(response);
		} catch ( Exception e) {
			AppUtils.logException(e);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}
	}

	public List<String> doGetWork(Connection conn, String url) throws RecordNotFoundException, Exception {
		String command = "tableFieldList";
		String[] urlPieces = url.split("/");
		
		int base = -1;
		List<String> resultList = new ArrayList<String>();
		for ( int i = 0; i < urlPieces.length; i++ ) {
			if ( urlPieces[i].equals(command)) {
				base = i;
				i = urlPieces.length + 1;
			}
		}
		if ( base == -1 ) {
			throw new RecordNotFoundException();
		}
		if( urlPieces[base].equals(command)) {
			if ( urlPieces.length - base == 1 ) {
				resultList = makeTableList(conn);
			} else if ( urlPieces.length - base == 2 ) {
				resultList = makeFieldList(conn, urlPieces[base + 1]);
			} else {
				throw new RecordNotFoundException();
			}
		} else {
			throw new RecordNotFoundException();
		}
		
		return resultList;
	}

	protected List<String> makeTableList(Connection conn) throws SQLException {
		List<String> tableList = new ArrayList<String>();
		DatabaseMetaData dbmd = conn.getMetaData();
		String catalog = null;
		String schemaPattern = null;
		String tableNamePattern = null;
		String[] types = new String[] { "TABLE" };
		ResultSet rs = dbmd.getTables(catalog, schemaPattern, tableNamePattern, types);
		while ( rs.next() ) {
			tableList.add(rs.getString("TABLE_NAME"));
		}
		Collections.sort(tableList);
		return tableList;
	}

	protected List<String> makeFieldList(Connection conn, String tableName) throws RecordNotFoundException, SQLException {
		List<String> fieldList = new ArrayList<String>();
		DatabaseMetaData dbmd = conn.getMetaData();
		String catalog = null;
		String schemaPattern = null;
		String tableNamePattern = tableName;
		String columnNamePattern = null;
		
		boolean foundARecord = false;
		ResultSet rs = dbmd.getColumns(catalog, schemaPattern, tableNamePattern, columnNamePattern);
		while ( rs.next() ) {
			foundARecord = true;
			String typeName = rs.getString("TYPE_NAME");
			if ( typeName.equalsIgnoreCase("varchar") ) {
				fieldList.add(rs.getString("COLUMN_NAME"));
			}
		}
		rs.close();
		if ( ! foundARecord ) {
			throw new RecordNotFoundException();
		}
				
		ResultSet rs2 = dbmd.getImportedKeys(catalog, schemaPattern, "quote");
		while ( rs2.next() ) {
			String fkColumnName = rs2.getString("FKCOLUMN_NAME");
			if ( fieldList.contains(fkColumnName)) {
				fieldList.remove(fkColumnName);
			}
		}
		rs2.close();
		
		Collections.sort(fieldList);
		return fieldList;
	}

	
	

}
