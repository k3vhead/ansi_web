package com.ansi.scilla.web.request.quote;

import java.util.Date;

import com.ansi.scilla.web.request.AbstractRequest;
import com.ansi.scilla.web.request.RequiredForAdd;

public class QuotePrintRequest extends AbstractRequest {

	private static final long serialVersionUID = 1L;

	private Date quoteDate;

	@RequiredForAdd
	public Date getQuoteDate() {
		return quoteDate;
	}

	public void setQuoteDate(Date quoteDate) {
		this.quoteDate = quoteDate;
	}
	
	
}
