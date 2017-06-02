package com.ansi.scilla.web.response.options;

import com.ansi.scilla.common.payment.PaymentMethod;

public class PaymentMethodOption extends WebOption  {
	private static final long serialVersionUID = 1L;
	private String abbrev;

	public PaymentMethodOption(PaymentMethod paymentMethod) {
		this.abbrev = paymentMethod.toString();
		this.display = paymentMethod.display();
	}

	public String getAbbrev() {
		return abbrev;
	}

	public void setAbbrev(String abbrev) {
		this.abbrev = abbrev;
	}



	
}
