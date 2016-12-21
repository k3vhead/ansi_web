package com.ansi.scilla.web.response.division;

import java.io.Serializable;

import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.web.response.MessageResponse;

/**
 * Used to return a single code to the client
 * 
 * @author dclewis
 *
 */
public class DivisionResponse extends MessageResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private Division division;

	public DivisionResponse() {
		super();
	}

	public DivisionResponse(Division division) {
		super();
		this.division = division;
	}

	public Division getDivision() {
		return division;
	}

	public void setCode(Division division) {
		this.division = division;
	}

	
	
}
