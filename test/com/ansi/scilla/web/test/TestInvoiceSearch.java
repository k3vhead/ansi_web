package com.ansi.scilla.web.test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ansi.scilla.common.queries.InvoiceSearch;
import com.ansi.scilla.common.utils.AppUtils;
import com.ansi.scilla.web.invoice.response.InvoiceLookupResponseItem;


public class TestInvoiceSearch {

	public static void main(String[] args) {
		try {
			String term = "Harry Carry";
//			new TestInvoiceSearch().testSearch(term);
			new TestInvoiceSearch().testLookup(10, 0, term, null, false, "invoice_id", "asc");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	
	private void testLookup(Integer amount, Integer start, String term, String filterDivisionId, Boolean filterPPC, String colName, String dir) throws Exception {
		Connection conn = null;
		Date startDate = new Date();
		try {
			conn = AppUtils.getDevConn();
			conn.setAutoCommit(false);

//			testMakeFilteredCount(conn, amount, start, term, filterDivisionId, filterPPC, colName, dir);
			Integer totalFiltered = InvoiceSearch.makeFilteredCount(conn, (String)null, "200000", false);		    


			conn.rollback();
		} catch ( Exception e) {
			conn.rollback();
			throw e;
		} finally {
			conn.close();
		}
		Date endDate = new Date();
		Long elapsed = endDate.getTime() - startDate.getTime();
		System.out.println("Elapsed: " + elapsed);
	}








	
	
	
	
	
	private void testMakeFilteredCount(Connection conn, Integer amount, Integer start, String term, String filterDivisionId, Boolean filterPPC, String colName, String dir) throws Exception {
		Integer totalFiltered = InvoiceSearch.makeFilteredCount(conn, term, filterDivisionId, filterPPC);		    
		List<InvoiceSearch> records = InvoiceSearch.makeFetchData(conn, amount, start, term, filterDivisionId, filterPPC, colName, dir);
		Integer totalUnfiltered = InvoiceSearch.makeUnfilteredCount(conn);
		
		System.out.println("TotalFiltered: " + totalFiltered);
		System.out.println(records.size() + " records");
		System.out.println("totalUnfiltered: " + totalUnfiltered);
		List<InvoiceLookupResponseItem> resultList = new ArrayList<InvoiceLookupResponseItem>();
		for ( InvoiceSearch invoiceSearch : records ) {
			resultList.add(new InvoiceLookupResponseItem(invoiceSearch));
		}
		
	}



	private void testSearch(String term) throws Exception {
		
		Connection conn = null;
		Date startDate = new Date();
		try {
			conn = AppUtils.getDevConn();
			conn.setAutoCommit(false);

			// Copied from InvoiceTypeAhead:
			String sql = InvoiceSearch.sql + "\n" + InvoiceSearch.generateWhereClause(conn, term);
			System.out.println("******");
			System.out.println("Invoice SQL:\n" + sql);
			System.out.println("******");
			
			Statement s = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);			
			ResultSet rs = s.executeQuery(sql);
//			while ( rs.next() ) {
//				resultList.add(new ReturnItem(rs));
				
//			}
			rs.last();
			System.out.println(rs.getRow() + " rows");
			rs.close();
			// end of copy
		} catch  ( Exception e ) {
			conn.rollback();
			throw e;
		} finally {
			conn.close();
		}
		Date endDate = new Date();
		Long elapsed = endDate.getTime() - startDate.getTime();
		System.out.println("Elapsed: " + elapsed);
	}

}
