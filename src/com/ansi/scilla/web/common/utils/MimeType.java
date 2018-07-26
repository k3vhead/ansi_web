package com.ansi.scilla.web.common.utils;

public enum MimeType {
	EXCEL("application/vnd.ms-excel"),
	HTML("text/html"),
	TEXT("text/plain"),
	PDF("application/pdf");
	
	private final String contentType;
	
	MimeType(String contentType) {
		this.contentType = contentType;
	}
	
	public String contentType() {
		return contentType;
	}
}
