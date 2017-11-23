package com.ansi.scilla.web.quote.response;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;

import com.ansi.scilla.common.db.Quote;
import com.ansi.scilla.web.common.WebMessages;
import com.ansi.scilla.web.common.response.MessageResponse;

/**
 * Used to return a single quote to the client
 * 
 * 
 *
 */
public class QuoteResponse extends MessageResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private Quote quote;
	private Date proposalDate;
	private QuoteDetail quoteDetail;
	
	public QuoteResponse() {
		super();
	}

	public QuoteResponse(Quote quote, WebMessages webMessages) throws IllegalAccessException, InvocationTargetException {
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
