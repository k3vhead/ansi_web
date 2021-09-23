package com.ansi.scilla.web.payroll.response;

import java.sql.Connection;

import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.web.common.response.MessageResponse;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class ExceptionReportResponse extends MessageResponse {

	private static final long serialVersionUID = 1L;

	private String div;
	private String divDescription;
	
	public ExceptionReportResponse() {
		super();
	}
	
	public ExceptionReportResponse(Connection conn, Integer divisionId) throws RecordNotFoundException, Exception {
		this();
		Division division = new Division();
		division.setDivisionId(divisionId);
		division.selectOne(conn);
		this.div = division.getDivisionDisplay();
		this.divDescription = division.getDescription();
	}

	public String getDiv() {
		return div;
	}

	public void setDiv(String div) {
		this.div = div;
	}

	public String getDivDescription() {
		return divDescription;
	}

	public void setDivDescription(String divDescription) {
		this.divDescription = divDescription;
	}
	
	
}
