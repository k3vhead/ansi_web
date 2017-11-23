package com.ansi.scilla.web.report.request;

import com.ansi.scilla.web.request.DivStartEndDateRequest;
import com.ansi.scilla.web.request.RequiredForAdd;

public class CashReceiptsRegisterReportRequest extends DivStartEndDateRequest {

	private static final long serialVersionUID = 1L;

	private String crrType;

	@RequiredForAdd
	public String getCrrType() {
		return crrType;
	}

	public void setCrrType(String crrType) {
		this.crrType = crrType;
	}

	
	public enum CrrReportType {
		DETAIL,
		SUMMARY;
	}
}
