package com.ansi.scilla.web.response.codes;

import java.io.Serializable;
import java.sql.Connection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.ansi.scilla.common.db.Codes;
import com.ansi.scilla.web.response.MessageResponse;

public class CodesListResponse implements MessageResponse, Serializable {

	private static final long serialVersionUID = 1L;

	private List<Codes> codesList;

	public CodesListResponse() {
		super();
	}
	public CodesListResponse(Connection conn) throws Exception {
		this.codesList = Codes.cast(new Codes().selectAll(conn));
		Collections.sort(codesList,

				new Comparator<Codes>() {

			public int compare(Codes o1, Codes o2) {

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
	public List<Codes> getCodesList() {
		return codesList;
	}

	public void setCodesList(List<Codes> codesList) {
		this.codesList = codesList;
	}
	
	
}
