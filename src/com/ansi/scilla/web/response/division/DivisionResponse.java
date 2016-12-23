package com.ansi.scilla.web.response.division;

import java.io.Serializable;

import com.ansi.scilla.web.response.MessageResponse;

/**
 * Used to return a single code to the client
 * 
 * @author dclewis
 *
 */
public class DivisionResponse extends MessageResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private DivisionCountRecord division;

	public DivisionResponse() {
		super();
	}

	public DivisionResponse(DivisionCountRecord division) {
		super();
		this.division = division;
	}

	public DivisionCountRecord getDivision() {
		return division;
	}

	
}
