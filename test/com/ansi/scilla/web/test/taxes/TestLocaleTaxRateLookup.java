package com.ansi.scilla.web.test.taxes;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;

import com.ansi.scilla.web.common.struts.SessionDivision;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.tax.query.LocaleTaxRateLookupQuery;
import com.ansi.scilla.web.test.common.AbstractTester;

public class TestLocaleTaxRateLookup extends AbstractTester {

	
	public void go() throws Exception {
		Connection conn = null;
		
		try {
			conn = AppUtils.getDevConn();
			String searchTerm = null;
			Integer localeId = 1;
			
			List<SessionDivision> divisionList = super.makeDivisionList(conn);
			LocaleTaxRateLookupQuery lookupQuery = new LocaleTaxRateLookupQuery();
			if ( searchTerm != null ) {
				lookupQuery.setSearchTerm(searchTerm);
			}
			if (localeId != null) {
				lookupQuery.setLocaleFilter(localeId);
			}
			ResultSet rs = lookupQuery.select(conn, 0, 10);
			while ( rs.next() ) {
				System.out.println(rs.getInt(LocaleTaxRateLookupQuery.LOCALE_ID));
			}
			rs.close();
		} finally {
			conn.close();
		}
	}
	public static void main(String[] args) {
		try {
			new TestLocaleTaxRateLookup().go();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

