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

	private TaxRateResponseItem taxRate;

	public TaxRateResponse() {
		super();
	}

	public TaxRateResponse(TaxRate taxRate, WebMessages webMessages) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		super(webMessages);
		this.taxRate = new TaxRateResponseItem(taxRate);
		
	}

	public TaxRateResponseItem getTaxRate() {
		return taxRate;
	}

	public void setTaxRate(TaxRateResponseItem taxRate) {
		this.taxRate = taxRate;
	}

	
	
}
