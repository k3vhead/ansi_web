package com.ansi.scilla.web.common.response;

import java.io.Serializable;
import java.util.HashMap;

public class CrudResponse extends MessageResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private HashMap<String, Object> item;

	public CrudResponse() {
		super();
	}

	public CrudResponse(HashMap<String, Object> item) {
		this();
		this.item = item;
	}

	public HashMap<String, Object> getItem() {
		return item;
	}

	public void setItem(HashMap<String, Object> item) {
		this.item = item;
	}
	
	
	
	
}
