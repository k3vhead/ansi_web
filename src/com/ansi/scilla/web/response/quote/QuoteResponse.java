package com.ansi.scilla.web.response.quote;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.db.Quote;
import com.ansi.scilla.common.db.User;
import com.ansi.scilla.common.jsonFormat.AnsiDateFormatter;
import com.ansi.scilla.web.common.WebMessages;
import com.ansi.scilla.web.response.MessageResponse;
import com.ansi.scilla.web.response.job.JobDetailResponse.QuoteDetail;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.thewebthing.commons.lang.BeanUtils;

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
		this.quoteDetail = new QuoteDetail(quote);
		
	}

	public Quote getQuote() {
		return quote;
	}

	public void setQuote(Quote quote) {
		this.quote = quote;
	}

	
	public class QuoteDetail extends ApplicationObject {
		private static final long serialVersionUID = 1L;
		
		private Date proposalDate;
		
		public QuoteDetail() {
			super();
		}
		public QuoteDetail(Quote quote) throws IllegalAccessException, InvocationTargetException {
			this();
			BeanUtils.copyProperties(this, quote);
		}
		@JsonSerialize(using=AnsiDateFormatter.class)
		public Date getProposalDate() {
			return proposalDate;
		}
		public void setProposalDate(Date proposalDate) {
			this.proposalDate = proposalDate;
		}		
	}
	
}
