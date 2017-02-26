package com.ansi.scilla.web.response.options;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.account.AccountType;

public class AccountTypeOption extends ApplicationObject {
	private static final long serialVersionUID = 1L;
	private String display;

	public AccountTypeOption(AccountType accountType) {
		this.display = accountType.display();
	}

	public String getDisplay() {
		return display;
	}

	public void setDisplay(String display) {
		this.display = display;
	}

	
}
