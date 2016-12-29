package com.ansi.scilla.web.response.quote;

import java.io.Serializable;

import com.ansi.scilla.common.db.Quote;
import com.ansi.scilla.web.common.WebMessages;
import com.ansi.scilla.web.response.MessageResponse;

/**
 * Used to return a single quote to the client
 * 
 * 
 *
 */
public class QuoteResponse extends MessageResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private Quote quote;

	public QuoteResponse() {
		super();
	}

	public QuoteResponse(Quote quote, WebMessages webMessages) {
		super(webMessages);
		this.quote = quote;
		
	}

	public Quote getQuote() {
		return quote;
	}

	public void setQuote(Quote quote) {
		this.quote = quote;
	}

	
	
}
