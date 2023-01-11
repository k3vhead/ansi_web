package com.ansi.scilla.web.bcr.request;

import java.sql.Connection;
import java.util.Calendar;
import java.util.Date;

import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.web.common.request.AbstractRequest;
import com.ansi.scilla.web.common.request.RequestValidator;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.fasterxml.jackson.annotation.JsonFormat;

public class BcrTitleRequest extends AbstractRequest {

	private static final long serialVersionUID = 1L;
	
	public static final String DIVISION_ID = "divisionId";
	public static final String WORK_DATE = "workDate";
	
	private Integer divisionId;
	private Calendar workDate;

	public Integer getDivisionId() {
		return divisionId;
	}

	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}

	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd", timezone="America/Chicago")
	public Calendar getWorkDate() {
		return workDate;
	}

	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd", timezone="America/Chicago")
	public void setWorkDate(Calendar workDate) {
		this.workDate = workDate;
	}



	
	
	
	public WebMessages validate(Connection conn, SessionUser sessionUser) throws Exception {
		WebMessages webMessages = new WebMessages();
		RequestValidator.validateId(conn, webMessages, Division.TABLE, Division.DIVISION_ID, DIVISION_ID, this.divisionId, true);
		RequestValidator.validateDate(webMessages, WORK_DATE, this.workDate, true, (Date)null, (Date)null);
		if ( webMessages.isEmpty() ) {
			RequestValidator.validateDivisionUser(conn, webMessages, this.divisionId, DIVISION_ID, sessionUser.getUserId(), DIVISION_ID, true);
		}
		return webMessages;
	}

}
