package com.ansi.scilla.web.response.code;

import java.io.Serializable;
import java.sql.Connection;
import java.util.Collections;
import java.util.Comparator;
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

	private List<Code> codeList;

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
		this.codeList = Code.cast(new Code().selectAll(conn));
		Collections.sort(codeList,

				new Comparator<Code>() {

			public int compare(Code o1, Code o2) {

				int ret = o1.getTableName().compareTo(o2.getTableName());
				if ( ret == 0 ) {
					ret = o1.getFieldName().compareTo(o2.getFieldName());
				}
				if ( ret == 0 ) {
					ret = o1.getSeq().compareTo(o2.getSeq());
				}
				if ( ret == 0 ) {
					ret = o1.getDisplayValue().compareTo(o2.getDisplayValue());
				}
				return ret;

			}

		});
	}
	public List<Code> getCodeList() {
		return codeList;
	}

	public void setCodeList(List<Code> codeList) {
		this.codeList = codeList;
	}
	
	
}
