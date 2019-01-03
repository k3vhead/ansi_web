package com.ansi.scilla.web.common.response;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class CrudListResponse extends MessageResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<HashMap<String, Object>> itemList;

	public CrudListResponse() {
		super();
	}
	
	public CrudListResponse(List<HashMap<String, Object>> itemList) {
		this();
		this.itemList = itemList;
	}

	public List<HashMap<String, Object>> getItemList() {
		return itemList;
	}

	public void setItemList(List<HashMap<String, Object>> itemList) {
		this.itemList = itemList;
	}
	
	
}
