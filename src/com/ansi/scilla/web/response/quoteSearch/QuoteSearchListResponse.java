package com.ansi.scilla.web.response.quoteSearch;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ansi.scilla.common.db.Quote;
import com.ansi.scilla.common.queries.QuoteSearch;
import com.ansi.scilla.web.response.MessageResponse;

/** 
 * Used to return a list of "code" objects to the client
 * 
 * @author dclewis
 *
 */
public class QuoteSearchListResponse extends MessageResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<QuoteSearchRecord> quoteSearchList;

	public QuoteSearchListResponse() {
		super();
	}
	/**
	 * create a list of all code table records in the database, sorted by
	 * table, field, display value
	 * 
	 * @param conn
	 * @throws Exception
	 */
	public QuoteSearchListResponse(Connection conn) throws Exception {
		List<QuoteSearch> quoteSearchList = QuoteSearch.select(conn);
		this.quoteSearchList = new ArrayList<QuoteSearchRecord>();
		for ( QuoteSearch record : quoteSearchList ) {
			this.quoteSearchList.add(new QuoteSearchRecord(record));
		}
	}
	
	public QuoteSearchListResponse(Connection conn, Integer quoteId) throws Exception {
		QuoteSearch quoteSearch = QuoteSearch.select(conn, quoteId);
		QuoteSearchRecord record = new QuoteSearchRecord(quoteSearch);
		this.quoteSearchList = Arrays.asList(new QuoteSearchRecord[] { record });
	}

	public List<QuoteSearchRecord> getQuoteSearchList() {
		return quoteSearchList;
	}

	public void setQuoteSearchList(List<QuoteSearchRecord> quoteSearchList) {
		this.quoteSearchList = quoteSearchList;
	}
	
	
	
}
