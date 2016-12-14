package com.ansi.scilla.web.response.division;

import java.io.Serializable;

import com.ansi.scilla.common.db.Code;
import com.ansi.scilla.web.response.MessageResponse;

/**
 * Used to return a single code to the client
 * 
 * @author dclewis
 *
 */
public class DivisionResponse extends MessageResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private Code code;

	public DivisionResponse() {
		super();
	}

	public DivisionResponse(Code code) {
		super();
		this.code = code;
	}

	public Code getCode() {
		return code;
	}

	public void setCode(Code code) {
		this.code = code;
	}

	
	
}
