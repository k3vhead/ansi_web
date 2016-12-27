package com.ansi.scilla.web.response.quote;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ansi.scilla.common.db.Quote;
import com.ansi.scilla.web.response.MessageResponse;

/** 
 * Used to return a list of "quote" objects to the client
 * 
 * 
 *
 */
public class QuoteListResponse extends MessageResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<QuoteResponseRecord> quoteList;

	public QuoteListResponse() {
		super();
	}
	/**
	 * create a list of all code table records in the database, sorted by
	 * table, field, display value
	 * 
	 * @param conn
	 * @throws Exception
	 */
	public QuoteListResponse(Connection conn) throws Exception {
		List<Quote> quoteList = Quote.cast(new Quote().selectAll(conn));
		this.quoteList = new ArrayList<QuoteResponseRecord>();
		for ( Quote quote : quoteList ) {
			this.quoteList.add(new QuoteResponseRecord(quote));
		}
		Collections.sort(this.quoteList);
	}

	public QuoteListResponse(Connection conn, Integer quoteId, Integer quoteNumber, Integer revisionNumber) throws Exception {
		Quote key = new Quote();
		key.setQuoteId(quoteId);
		key.setQuoteNumber(quoteNumber);
		key.setRevisionNumber(revisionNumber);
		List<Quote> quoteList = Quote.cast(key.selectSome(conn));
		this.quoteList = new ArrayList<QuoteResponseRecord>();
		for ( Quote quote : quoteList ) {
			this.quoteList.add(new QuoteResponseRecord(quote));
		}
		Collections.sort(this.quoteList);
	}

	public List<QuoteResponseRecord> getQuoteList() {
		return quoteList;
	}

	public void setQuoteList(List<QuoteResponseRecord> quoteList) {
		this.quoteList = quoteList;
	}
	
	
}
