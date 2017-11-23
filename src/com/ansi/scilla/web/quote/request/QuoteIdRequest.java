package com.ansi.scilla.web.quote.request;

import com.ansi.scilla.web.common.request.AbstractRequest;
import com.ansi.scilla.web.common.request.RequiredForAdd;
import com.ansi.scilla.web.common.request.RequiredForUpdate;

public class QuoteIdRequest extends AbstractRequest {

	private static final long serialVersionUID = 1L;

	private Integer quoteId;
	
	public QuoteIdRequest() {
		super();
	}

	@RequiredForAdd
	@RequiredForUpdate
	public Integer getQuoteId() {
		return quoteId;
	}

	public void setQuoteId(Integer quoteId) {
		this.quoteId = quoteId;
	}
	
	
}
