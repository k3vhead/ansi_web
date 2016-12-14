package com.ansi.scilla.web.response.taxRate;

import java.io.Serializable;

import com.ansi.scilla.common.db.TaxRate;
import com.ansi.scilla.web.common.WebMessages;
import com.ansi.scilla.web.response.MessageResponse;

/**
 * Used to return a single taxRate to the client
 * 
 * @author gagroce
 *
 */
public class TaxRateResponse extends MessageResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private TaxRate taxRate;

	public TaxRateResponse() {
		super();
	}

	public TaxRateResponse(TaxRate taxRate, WebMessages webMessages) {
		super(webMessages);
		this.taxRate = taxRate;
		
	}

	public TaxRate getTaxRate() {
		return taxRate;
	}

	public void setTaxRate(TaxRate taxRate) {
		this.taxRate = taxRate;
	}

	
	
}
