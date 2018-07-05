package com.ansi.scilla.web.test;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import com.ansi.scilla.common.utils.AppUtils;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.quote.common.BuildingTypeValidator;

public class TestFieldValidator {

	public static void main(String[] args) {
		try {
			new TestFieldValidator().go();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void go() throws Exception {
		Connection conn = null;
		try {
			conn = AppUtils.getDevConn();
			conn.setAutoCommit(false);
			WebMessages webMessages = new WebMessages();
			BuildingTypeValidator x = new BuildingTypeValidator();
			
			x.validate(conn, "buildingType", "SPRTCX", webMessages);
			
			for ( Map.Entry<String, List<String>> entry : webMessages.entrySet() ) {
				System.out.println(entry.getKey());
				for ( String value : entry.getValue() ) {
					System.out.println("\t" + value);
				}
			}
		} catch ( Exception e ) {
			conn.rollback();
			throw e;
		} finally {
			conn.close();
		}
		
	}

}
