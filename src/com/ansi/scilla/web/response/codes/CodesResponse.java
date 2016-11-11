package com.ansi.scilla.web.response.codes;

import java.io.Serializable;

import com.ansi.scilla.common.db.Codes;
import com.ansi.scilla.web.response.MessageResponse;

public class CodesResponse implements MessageResponse, Serializable {

	private static final long serialVersionUID = 1L;

	private Codes codes;

	public CodesResponse() {
		super();
	}

	public CodesResponse(Codes codes) {
		super();
		this.codes = codes;
	}

	public Codes getCodes() {
		return codes;
	}

	public void setCodes(Codes codes) {
		this.codes = codes;
	}

	
	
}
