package com.ansi.scilla.web.test.documents;

import java.sql.Connection;

import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.document.query.DocumentLookupQuery;

public class TestDocumentLookupQuery {

	public void go() throws Exception {
		Connection conn = null;
		try {
			conn = AppUtils.getDevConn();
			conn.setAutoCommit(false);
			DocumentLookupQuery query = new DocumentLookupQuery(5, null);
			System.out.println(query.getSql());
		} catch ( Exception e ) {
			conn.rollback();
			throw e;
		}
	}
	
	public static void main(String[] args) {
		try {
			new TestDocumentLookupQuery().go();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
