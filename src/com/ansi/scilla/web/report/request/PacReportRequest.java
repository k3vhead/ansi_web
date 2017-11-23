package com.ansi.scilla.web.report.request;

import com.ansi.scilla.web.common.request.RequiredForAdd;

public class PacReportRequest extends DivStartEndDateRequest {

	private static final long serialVersionUID = 1L;

	private String pacType;

	@RequiredForAdd
	public String getPacType() {
		return pacType;
	}

	public void setPacType(String pacType) {
		this.pacType = pacType;
	}

	
	public enum PacReportType {
		P,
		A,
		C,
		SUMMARY;
	}
}
