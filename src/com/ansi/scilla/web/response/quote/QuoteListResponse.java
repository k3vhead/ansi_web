package com.ansi.scilla.web.response.quote;

import java.io.Serializable;
import java.sql.Connection;
import java.util.Collections;
import java.util.Comparator;
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

	private List<Quote> quoteList;

	public QuoteListResponse() {
		super();
	}
	/**
	 * create a list of all quote table records in the database, sorted by
	 * id, quote Number, revision number
	 * 
	 * @param conn
	 * @throws Exception
	 */
	public QuoteListResponse(Connection conn) throws Exception {
		this.quoteList = Quote.cast(new Quote().selectAll(conn));
		Collections.sort(quoteList,

				new Comparator<Quote>() {

			public int compare(Quote o1, Quote o2) {

				int ret = o1.getQuoteId().compareTo(o2.getQuoteId());
				if ( ret == 0 ) {
					ret = o1.getQuoteNumber().compareTo(o2.getQuoteNumber());
				}
				if ( ret == 0 ) {
					ret = o1.getRevisionNumber().compareTo(o2.getRevisionNumber());
				}
				if ( ret == 0 ) {
					ret = o1.getProposalDate().compareTo(o2.getProposalDate());
				}
				return ret;

			}

		});
	}
	public List<Quote> getQuoteList() {
		return quoteList;
	}

	public void setQuoteList(List<Quote> quoteList) {
		this.quoteList = quoteList;
	}
	
	
}
