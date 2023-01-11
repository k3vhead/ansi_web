package com.ansi.scilla.web.bcr.response;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.utils.WorkYear;
import com.fasterxml.jackson.annotation.JsonFormat;

public class DisplayMonth extends ApplicationObject {
	
	private static final long serialVersionUID = 1L;
	private final SimpleDateFormat printFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss E");
	private final SimpleDateFormat monthFormat = new SimpleDateFormat("MMM");
	private String displayDate;
	private Calendar workDate;
	private Calendar firstOfMonth;
	private Calendar lastOfMonth;
	
	public DisplayMonth(WorkYear workYear, Calendar workDate) {
		super();
		this.displayDate = monthFormat.format(workDate.getTime()) + ", " + workDate.get(Calendar.YEAR);
		this.workDate = new GregorianCalendar(workDate.get(Calendar.YEAR),workDate.get(Calendar.MONTH),workDate.get(Calendar.DAY_OF_MONTH), 0, 0, 0);;
		this.firstOfMonth = workYear.getFirstOfMonth(workDate);
		this.lastOfMonth = workYear.getLastOfMonth(workDate);
	}

	
	public String getDisplayDate() {
		return displayDate;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd", timezone="America/Chicago")
	public Calendar getWorkDate() {
		return workDate;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public Calendar getFirstOfMonth() {
		return firstOfMonth;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public Calendar getLastOfMonth() {
		return lastOfMonth;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("<\n");
		buffer.append("\tDisplay: " + this.displayDate + "\n");
		buffer.append("\tfirstOfMonth: " + printFormat.format(this.firstOfMonth.getTime()) + "\n");
		buffer.append("\tlastOfMonth: " + printFormat.format(this.lastOfMonth.getTime()) + "\n");
		buffer.append(">");
		return buffer.toString();
	}
}
