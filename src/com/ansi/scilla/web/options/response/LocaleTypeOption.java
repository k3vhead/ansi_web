package com.ansi.scilla.web.options.response;

import com.ansi.scilla.common.utils.LocaleType;;

public class LocaleTypeOption extends WebOption  {
	private static final long serialVersionUID = 1L;
	private String code;
	private String name;

	public LocaleTypeOption(LocaleType expenseType) {
		super();
		this.code = expenseType.name();
		this.display = expenseType.name();
		this.name = expenseType.name();
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	

}
