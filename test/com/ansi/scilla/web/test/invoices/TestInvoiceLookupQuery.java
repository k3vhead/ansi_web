package com.ansi.scilla.web.test.invoices;

import java.sql.Connection;
import java.util.List;

import com.ansi.scilla.web.common.struts.SessionDivision;
import com.ansi.scilla.web.invoice.query.InvoiceLookupQuery;
import com.ansi.scilla.web.test.common.AbstractTester;

public class TestInvoiceLookupQuery extends AbstractTester {

	private final Integer userId = 5;
	
	public static void main(String[] args) {
		new TestInvoiceLookupQuery().run();

	}

	@Override
	protected void go(Connection conn) throws Exception {
		List<SessionDivision> divisionList = super.makeDivisionList(conn);
//		String searchTerm = null;
//		if(request.getParameter("search[value]") != null){
//			searchTerm = request.getParameter("search[value]");
//		}
		InvoiceLookupQuery lookupQuery = new InvoiceLookupQuery(userId, divisionList);
		lookupQuery.addBaseFilter(userId);
//		if ( searchTerm != null ) {
//			lookupQuery.setSearchTerm(searchTerm);
//		}	
		lookupQuery.selectCount(conn);
		lookupQuery.select(conn, 0, 10);
		lookupQuery.countAll(conn);
	}

}
