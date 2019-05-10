package com.ansi.scilla.web.common.utils;

public enum UserFavoriteType {
	REPORT("Report"),
	LOOKUP("Lookup"),
	;
	
	private final String display;
	private UserFavoriteType(String display) {
		this.display = display;
	}
	
	public String getDisplay() {
		return display;
	}
}
