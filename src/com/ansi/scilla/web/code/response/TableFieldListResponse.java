package com.ansi.scilla.web.code.response;

import java.util.List;

import com.ansi.scilla.web.response.MessageResponse;

public class TableFieldListResponse extends MessageResponse {

	private static final long serialVersionUID = 1L;

	private List<String> nameList;
	
	public TableFieldListResponse() {
		super();
	}

	public TableFieldListResponse(List<String> nameList) {
		this();
		this.nameList = nameList;
	}

	public List<String> getNameList() {
		return nameList;
	}

	public void setNameList(List<String> nameList) {
		this.nameList = nameList;
	}
	
	
}
