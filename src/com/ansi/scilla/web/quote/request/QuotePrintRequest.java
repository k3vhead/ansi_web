package com.ansi.scilla.web.quote.request;

import java.util.Date;

import com.ansi.scilla.web.common.request.AbstractRequest;
import com.ansi.scilla.web.common.request.RequiredForAdd;
import com.fasterxml.jackson.annotation.JsonFormat;

public class QuotePrintRequest extends AbstractRequest {

	private static final long serialVersionUID = 1L;

	private Date quoteDate;

	@RequiredForAdd
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public Date getQuoteDate() {
		return quoteDate;
	}

	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public void setQuoteDate(Date quoteDate) {
		this.quoteDate = quoteDate;
	}
	
	
}
