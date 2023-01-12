package com.ansi.scilla.web.payroll.response;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import com.ansi.scilla.common.payroll.export.PayrollExportUtils;
import com.ansi.scilla.web.common.response.MessageResponse;

public class ExportPreviewResponse extends MessageResponse {

	private static final long serialVersionUID = 1L;

	private List<HashMap<String, Object>> regularHours;
	private List<HashMap<String, Object>> allHours;
	private List<HashMap<String, Object>> preview;
	
	public ExportPreviewResponse() {
		super();
	}
	
	public ExportPreviewResponse(Connection conn, String companyCode, Calendar weekEnding) throws SQLException {
		this();
		this.regularHours = rs2map( PayrollExportUtils.validateRegularHours(conn, weekEnding));
		this.allHours = rs2map( PayrollExportUtils.validateAllHours(conn, weekEnding) );
		this.preview = rs2map( PayrollExportUtils.makePreviewData(conn, companyCode, weekEnding) );
	}
	
	public List<HashMap<String, Object>> getRegularHours() {
		return regularHours;
	}
	public void setRegularHours(List<HashMap<String, Object>> regularHours) {
		this.regularHours = regularHours;
	}
	public List<HashMap<String, Object>> getAllHours() {
		return allHours;
	}
	public void setAllHours(List<HashMap<String, Object>> allHours) {
		this.allHours = allHours;
	}
	public List<HashMap<String, Object>> getPreview() {
		return preview;
	}
	public void setPreview(List<HashMap<String, Object>> preview) {
		this.preview = preview;
	}

	private List<HashMap<String, Object>> rs2map(ResultSet rs) throws SQLException {
		List<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();		
		ResultSetMetaData rsmd = rs.getMetaData();
		while ( rs.next() ) {
			HashMap<String, Object> row = new HashMap<String, Object>();
			for ( int i = 0; i < rsmd.getColumnCount(); i++ ) {
				int idx = i + 1;
				String fieldName = rsmd.getColumnName(idx);
				row.put(fieldName, rs.getObject(fieldName));
			}
			data.add(row);
		}
		rs.close();
		return data;
	}
	
	
}
