package com.ansi.scilla.web.response.code;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ansi.scilla.common.db.Code;
import com.ansi.scilla.web.response.MessageResponse;

/** 
 * Used to return a list of "code" objects to the client
 * 
 * @author dclewis
 *
 */
public class CodeListResponse extends MessageResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<CodeResponseRecord> codeList;

	public CodeListResponse() {
		super();
	}
	/**
	 * create a list of all code table records in the database, sorted by
	 * table, field, display value
	 * 
	 * @param conn
	 * @throws Exception
	 */
	public CodeListResponse(Connection conn) throws Exception {
		List<Code> codeList = Code.cast(new Code().selectAll(conn));
		this.codeList = new ArrayList<CodeResponseRecord>();
		for ( Code code : codeList ) {
			this.codeList.add(new CodeResponseRecord(code));
		}
		Collections.sort(this.codeList);
	}

	public CodeListResponse(Connection conn, String tableName, String fieldName, String value) throws Exception {
		Code key = new Code();
		key.setTableName(tableName);
		key.setFieldName(fieldName);
		key.setValue(value);
		List<Code> codeList = Code.cast(key.selectSome(conn));
		this.codeList = new ArrayList<CodeResponseRecord>();
		for ( Code code : codeList ) {
			this.codeList.add(new CodeResponseRecord(code));
		}
		Collections.sort(this.codeList);
	}

	public List<CodeResponseRecord> getCodeList() {
		return codeList;
	}

	public void setCodeList(List<CodeResponseRecord> codeList) {
		this.codeList = codeList;
	}
	
	
}
