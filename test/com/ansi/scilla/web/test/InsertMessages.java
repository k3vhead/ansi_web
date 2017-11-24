package com.ansi.scilla.web.test;

import java.sql.Connection;
import java.util.Date;

import com.ansi.scilla.common.db.ApplicationProperties;
import com.ansi.scilla.common.utils.AppUtils;
import com.ansi.scilla.web.common.response.MessageKey;

public class InsertMessages {

	public static void main(String[] args) throws Exception {
		new InsertMessages().go();
	}
	
	public void go() throws Exception {
		Connection conn = null;
		try {
			conn = AppUtils.getDevConn();
			conn.setAutoCommit(false);
			
			Date today = new Date();
			ApplicationProperties ap = new ApplicationProperties();
			ap.setAddedBy(5);
			ap.setAddedDate(today);
			ap.setUpdatedBy(5);
			ap.setUpdatedDate(today);
			
			ap.setPropertyId(MessageKey.DUPLICATE_ENTRY.toString());
			ap.setValueString("Duplicate Entry");
			ap.insertWithNoKey(conn);
			
			ap.setPropertyId(MessageKey.INSERT_FAILED.toString());
			ap.setValueString("Add Failed");
			ap.insertWithNoKey(conn);
			
			ap.setPropertyId(MessageKey.MISSING_DATA.toString());
			ap.setValueString("Required Data is Missing");
			ap.insertWithNoKey(conn);
			
			ap.setPropertyId(MessageKey.SUCCESS.toString());
			ap.setValueString("Success!");
			ap.insertWithNoKey(conn);
			
			ap.setPropertyId(MessageKey.UPDATE_FAILED.toString());
			ap.setValueString("Update failed");
			ap.insertWithNoKey(conn);

			conn.commit();
		} catch ( Exception e ) {
			conn.rollback();
			throw e;
		} finally {
			conn.close();
		}
	}

}
