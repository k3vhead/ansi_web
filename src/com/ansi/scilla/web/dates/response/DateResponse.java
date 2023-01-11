package com.ansi.scilla.web.dates.response;

import java.io.Serializable;
import java.sql.Connection;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.lang3.time.DateUtils;

import com.ansi.scilla.common.exceptions.AnsiCalendarException;
import com.ansi.scilla.common.utils.WorkYear;
import com.ansi.scilla.web.common.response.MessageResponse;
import com.fasterxml.jackson.annotation.JsonFormat;

public class DateResponse extends MessageResponse implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Calendar workDate;
	private Calendar firstOfWeek;
	private Calendar lastOfWeek;
	private Calendar firstOfMonth;
	private Calendar lastOfMonth;
	private Calendar firstOfYear;
	private Calendar lastOfYear;
	private Integer workMonth;
	private Integer weekOfMonth;
	private Integer weekOfYear;
	private Calendar fiscalYearStart;
	
	public DateResponse() {
		super();
	}
	
	public DateResponse(Connection conn, Date workDate) throws AnsiCalendarException {
		this();		
		make(conn, DateUtils.toCalendar(workDate));
	}
	
	public DateResponse(Connection conn, Calendar workDate) throws AnsiCalendarException {
		this();		
		make(conn, workDate);
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public Calendar getWorkYear() {
		return workDate;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public Calendar getFirstOfWeek() {
		return firstOfWeek;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public Calendar getLastOfWeek() {
		return lastOfWeek;
	}

	public Integer getWorkMonth() {
		return workMonth;
	}

	public Integer getWeekOfMonth() {
		return weekOfMonth;
	}

	public Integer getWeekOfYear() {
		return weekOfYear;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public Calendar getFiscalYearStart() {
		return fiscalYearStart;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public Calendar getFirstOfMonth() {
		return firstOfMonth;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public Calendar getLastOfMonth() {
		return lastOfMonth;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public Calendar getFirstOfYear() {
		return firstOfYear;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public Calendar getLastOfYear() {
		return lastOfYear;
	}

	private void make(Connection conn, Calendar workDate) throws AnsiCalendarException {
		this.workDate = workDate;
		WorkYear w = new WorkYear(workDate.get(Calendar.YEAR));		
		this.firstOfWeek = w.getFirstOfWeek(workDate);
		this.lastOfWeek = w.getLastOfWeek(workDate);
		this.workMonth = w.getWorkMonth(workDate);
		this.weekOfMonth = w.getWeekOfMonth(workDate);
		this.weekOfYear = w.getWeekOfYear(workDate);
		this.firstOfMonth = w.getFirstOfMonth(workDate);
		this.lastOfMonth = w.getLastOfMonth(workDate);
		this.firstOfYear = w.getFirstOfYear();
		this.lastOfYear = w.getLastOfYear();
		// this attribute should be added once the automated reports development branch and job tags development branch
		// are merged (into dev branch?)
		//this.fiscalYearStart = AppUtils.getFiscalYearStart(conn, workDate);
		Calendar fiscalStart = new GregorianCalendar(workDate.get(Calendar.YEAR), Calendar.OCTOBER, 1);
		Integer year = fiscalStart.after(workDate) ? fiscalStart.get(Calendar.YEAR) - 1 : fiscalStart.get(Calendar.YEAR); 
		this.fiscalYearStart = new GregorianCalendar(year, Calendar.OCTOBER, 1);
	}


	
}

