package com.ansi.scilla.web.quote.response;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;

import org.apache.commons.beanutils.PropertyUtils;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.db.Quote;
import com.fasterxml.jackson.annotation.JsonFormat;

public class QuoteDetail extends ApplicationObject {
	private static final long serialVersionUID = 1L;
	
	private Date proposalDate;
	
	public QuoteDetail() {
		super();
	}
	public QuoteDetail(Quote quote) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		this();
		PropertyUtils.copyProperties(this, quote);
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public Date getProposalDate() {
		return proposalDate;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public void setProposalDate(Date proposalDate) {
		this.proposalDate = proposalDate;
	}		
}
