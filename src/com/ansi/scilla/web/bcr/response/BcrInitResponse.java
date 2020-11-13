package com.ansi.scilla.web.bcr.response;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.utils.WorkYear;
import com.ansi.scilla.web.common.response.MessageResponse;
import com.ansi.scilla.web.division.response.DivisionCountRecord;
import com.fasterxml.jackson.annotation.JsonFormat;

public class BcrInitResponse extends MessageResponse {

	private static final long serialVersionUID = 1L;

	private List<DivisionCountRecord> divisionList;
	private List<DisplayMonth> displayMonthList;
	
	public BcrInitResponse() {
		super();
	}
	
	public BcrInitResponse(List<DivisionCountRecord> divisionList, Calendar workDay) {
		this();
		this.divisionList = divisionList;
		this.displayMonthList = makeDisplayYear(workDay);
	}

	public List<DivisionCountRecord> getDivisionList() {
		return divisionList;
	}

	public void setDivisionList(List<DivisionCountRecord> divisionList) {
		this.divisionList = divisionList;
	}

	public List<DisplayMonth> getDisplayMonthList() {
		return displayMonthList;
	}

	public void setDisplayMonthList(List<DisplayMonth> displayMonthList) {
		this.displayMonthList = displayMonthList;
	}

	private List<DisplayMonth> makeDisplayYear(Calendar workDay) {
		// if today is october, november, december, so we start october this year
		// if today is January - September, so we start last october last year
		// in either case, we end 3 months from today
		Integer startYear = workDay.get(Calendar.MONTH) > Calendar.SEPTEMBER ? workDay.get(Calendar.YEAR) : workDay.get(Calendar.YEAR) - 1;
		Calendar workDate = new GregorianCalendar(startYear, Calendar.OCTOBER, 15);
		Calendar endDate = new GregorianCalendar(workDay.get(Calendar.YEAR), workDay.get(Calendar.MONTH), workDay.get(Calendar.DAY_OF_MONTH),0,0,0);
		endDate.add(Calendar.MONTH, 4);

		Integer previousYear = workDate.get(Calendar.YEAR);
		WorkYear workYear = new WorkYear(workDate.get(Calendar.YEAR));
		List<DisplayMonth> displayYear = new ArrayList<DisplayMonth>();
		while ( workDate.before(endDate) ) {
			if ( workDate.get(Calendar.YEAR)!=previousYear ) {
				previousYear = workDate.get(Calendar.YEAR);
				workYear = new WorkYear(workDate.get(Calendar.YEAR));
			}
			displayYear.add(new DisplayMonth(workYear, workDate));
			workDate.add(Calendar.MONTH, 1);
		}
		return displayYear;
	}

	
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
}
