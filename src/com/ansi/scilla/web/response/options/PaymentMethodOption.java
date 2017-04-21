package com.ansi.scilla.web.response.options;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.payment.PaymentMethod;

public class PaymentMethodOption extends ApplicationObject {
	private static final long serialVersionUID = 1L;
	private String abbrev;
	private String display;

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

	public String getDisplay() {
		return display;
	}

	public void setDisplay(String display) {
		this.display = display;
	}

	
}
