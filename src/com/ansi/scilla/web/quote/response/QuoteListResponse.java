package com.ansi.scilla.web.quote.response;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.common.db.Quote;
import com.ansi.scilla.web.common.response.MessageResponse;
import com.ansi.scilla.web.common.utils.UserPermission;
import com.thewebthing.commons.db2.RecordNotFoundException;

/** 
 * Used to return a list of "quote" objects to the client
 * 
 * 
 *
 */
public class QuoteListResponse extends MessageResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<QuoteResponseItem> quoteList;
	private Logger logger;

	public QuoteListResponse() {
		super();
		this.logger = LogManager.getLogger(this.getClass());
	}
	/**
	 * create a list of all code table records in the database, sorted by
	 * table, field, display value
	 * 
	 * @param conn
	 * @throws Exception
	 */
	public QuoteListResponse(Connection conn, List<UserPermission> permissionList) throws RecordNotFoundException, Exception {
		this();
		logger.log(Level.DEBUG, "QuoteListResponse constructor 1");
		List<Quote> quoteList = Quote.cast(new Quote().selectAll(conn));
		this.quoteList = new ArrayList<QuoteResponseItem>();
		for ( Quote quote : quoteList ) {
			this.quoteList.add(new QuoteResponseItem(conn, quote, permissionList));
		}
		//Collections.sort(this.quoteList);
	}

	public QuoteListResponse(Connection conn, String quoteNumber, String revision, List<UserPermission> permissionList) throws RecordNotFoundException, Exception {
		this();
		logger.log(Level.DEBUG, "QuoteListResponse constructor 2");
		Quote key = new Quote();
		key.setQuoteNumber(Integer.parseInt(quoteNumber));
		key.setRevision(revision);		
		
		List<Quote> quoteList = Quote.cast(key.selectSome(conn));
		this.quoteList = new ArrayList<QuoteResponseItem>();
		for ( Quote quote : quoteList ) {
			this.quoteList.add(new QuoteResponseItem(conn, quote, permissionList));
		}
		//Collections.sort(this.quoteList);
	}

	public QuoteListResponse(Connection conn, String quoteId, List<UserPermission> permissionList) throws RecordNotFoundException, Exception {
		this();
		logger.log(Level.DEBUG, "QuoteListResponse constructor 3");
		Quote key = new Quote();
		key.setQuoteId(Integer.parseInt(quoteId));
		logger.log(Level.DEBUG, "Getting quote " + quoteId);
		List<Quote> quoteList = Quote.cast(key.selectSome(conn));
		this.quoteList = new ArrayList<QuoteResponseItem>();
		for ( Quote quote : quoteList ) {
			this.quoteList.add(new QuoteResponseItem(conn, quote, permissionList));
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
