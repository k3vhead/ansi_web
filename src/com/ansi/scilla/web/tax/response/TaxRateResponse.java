package com.ansi.scilla.web.tax.response;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;

import com.ansi.scilla.common.db.TaxRate;
import com.ansi.scilla.web.common.response.MessageResponse;
import com.ansi.scilla.web.common.response.WebMessages;

/**
 * Used to return a single taxRate to the client
 * 
 * @author gagroce
 *
 */
public class TaxRateResponse extends MessageResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private TaxRateResponseRecord taxRate;

	public TaxRateResponse() {
		super();
	}

	public TaxRateResponse(TaxRate taxRate, WebMessages webMessages) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		super(webMessages);
		this.taxRate = new TaxRateResponseRecord(taxRate);
		
	}

	public TaxRateResponseRecord getTaxRate() {
		return taxRate;
	}

	public void setTaxRate(TaxRateResponseRecord taxRate) {
		this.taxRate = taxRate;
	}

	
	
}