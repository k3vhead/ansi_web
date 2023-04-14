package com.ansi.scilla.web.test.address;

import java.sql.Connection;
import java.util.List;

import com.ansi.scilla.web.address.query.AddressLookupQuery;
import com.ansi.scilla.web.common.query.LookupQuery;
import com.ansi.scilla.web.common.struts.SessionDivision;
import com.ansi.scilla.web.test.common.AbstractTester;

public class TestAddressQuery extends AbstractTester {

	private final Integer userId = 5;
	
	public static void main(String[] args) {
		try {
			new TestAddressQuery().run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void go(Connection conn) throws Exception {
		conn.setAutoCommit(false);
		List<SessionDivision> divisionList = super.makeDivisionList(conn);
		LookupQuery lookupQuery = new AddressLookupQuery(userId, divisionList);
		lookupQuery.setSearchTerm("");
		System.out.println("Count: " + lookupQuery.selectCount(conn));
		System.out.println("*****************************");
		System.out.println("*****************************");
		System.out.println("CountAll: " + lookupQuery.countAll(conn));
		
	}

}
