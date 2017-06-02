package com.ansi.scilla.web.response.options;

import com.ansi.scilla.common.account.AccountType;

public class AccountTypeOption extends WebOption  {
	private static final long serialVersionUID = 1L;

	public AccountTypeOption(AccountType accountType) {
		this.display = accountType.display();
	}


	
}
