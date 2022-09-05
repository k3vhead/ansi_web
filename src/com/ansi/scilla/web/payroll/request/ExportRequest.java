package com.ansi.scilla.web.payroll.request;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import com.ansi.scilla.web.common.request.AbstractRequest;
import com.ansi.scilla.web.common.request.RequestValidator;
import com.ansi.scilla.web.common.response.WebMessages;
import com.fasterxml.jackson.annotation.JsonFormat;

public class ExportRequest extends AbstractRequest {

	private static final long serialVersionUID = 1L;
	
	public static final String COMPANY_CODE = "companyCode";
	public static final String WEEK_ENDING = "weekEnding";

	private String companyCode;
	private Calendar weekEnding;
	
	public ExportRequest() {
		super();
	}
	
	public ExportRequest(String companyCode, Calendar weekEnding) {
		this();
		this.companyCode = companyCode;
		this.weekEnding = weekEnding;
	}
	
	public ExportRequest(String companyCode, String weekEnding) throws ParseException {
		this();
		this.companyCode = companyCode;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if ( ! StringUtils.isBlank(weekEnding) ) {
			Date weekEndingDate = sdf.parse(weekEnding);
			this.weekEnding = DateUtils.toCalendar(weekEndingDate);
		}
	}
	
	public String getCompanyCode() {
		return companyCode;
	}
	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}	
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd", timezone="America/Chicago")
	public Calendar getWeekEnding() {
		return weekEnding;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd", timezone="America/Chicago")
	public void setWeekEnding(Calendar weekEnding) {
		this.weekEnding = weekEnding;
	}
	
	public WebMessages validate(Connection conn) throws SQLException {
		WebMessages webMessages = new WebMessages();
		RequestValidator.validateCompanyCode(conn, webMessages, COMPANY_CODE, this.companyCode, true, "Company Code");
		RequestValidator.validateDay(webMessages, WEEK_ENDING, this.weekEnding, true, null, null, Calendar.FRIDAY);
		return webMessages;
	}
}
