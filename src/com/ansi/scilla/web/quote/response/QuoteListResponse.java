package com.ansi.scilla.web.quote.response;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import com.ansi.scilla.common.db.Quote;
import com.ansi.scilla.web.common.response.MessageResponse;

/** 
 * Used to return a list of "quote" objects to the client
 * 
 * 
 *
 */
public class QuoteListResponse extends MessageResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<QuoteResponseItem> quoteList;

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
		this.quoteList = new ArrayList<QuoteResponseItem>();
		for ( Quote quote : quoteList ) {
			this.quoteList.add(new QuoteResponseItem(conn, quote));
		}
		//Collections.sort(this.quoteList);
	}

	public QuoteListResponse(Connection conn, String quoteNumber, String revision) throws Exception {
		Quote key = new Quote();
		key.setQuoteNumber(Integer.parseInt(quoteNumber));
		key.setRevision(revision);		
		
		List<Quote> quoteList = Quote.cast(key.selectSome(conn));
		this.quoteList = new ArrayList<QuoteResponseItem>();
		for ( Quote quote : quoteList ) {
			this.quoteList.add(new QuoteResponseItem(conn, quote));
		}
		//Collections.sort(this.quoteList);
	}

	public QuoteListResponse(Connection conn, String quoteId) throws Exception {
		Quote key = new Quote();
		key.setQuoteId(Integer.parseInt(quoteId));
		
		List<Quote> quoteList = Quote.cast(key.selectSome(conn));
		this.quoteList = new ArrayList<QuoteResponseItem>();
		for ( Quote quote : quoteList ) {
			this.quoteList.add(new QuoteResponseItem(conn, quote));
		}
		//Collections.sort(this.quoteList);
	}

	public List<QuoteResponseItem> getQuoteList() {
		return quoteList;
	}

	public void setQuoteList(List<QuoteResponseItem> quoteList) {
		this.quoteList = quoteList;
	}
	
	
}
