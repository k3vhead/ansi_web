package com.ansi.scilla.web.quote.response;

import java.io.Serializable;
import java.sql.Connection;
import java.util.List;

import com.ansi.scilla.common.db.Quote;
import com.ansi.scilla.web.common.response.MessageResponse;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.utils.UserPermission;

/**
 * Used to return a single quote to the client
 * 
 * 
 *
 */
public class QuoteResponse extends MessageResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private QuoteResponseItem quote;
	
	public QuoteResponse() {
		super();
	}
	
	public QuoteResponse(Connection conn, Quote quote, WebMessages webMessages, List<UserPermission>permissionList) throws Exception {
		super(webMessages);
		if ( quote != null ) {
			this.quote = new QuoteResponseItem(conn, quote, permissionList);
		}
	}

	public QuoteResponseItem getQuote() {
		return quote;
	}

	public void setQuote(QuoteResponseItem quote) {
		this.quote = quote;
	}
	


	

	
}
