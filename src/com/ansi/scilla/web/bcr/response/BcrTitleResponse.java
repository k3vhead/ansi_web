package com.ansi.scilla.web.bcr.response;

import java.sql.Connection;
import java.util.Calendar;
import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.common.AnsiTime;
import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.common.utils.WorkWeek;
import com.ansi.scilla.common.utils.WorkYear;
import com.ansi.scilla.web.bcr.common.BcrUtils;
import com.ansi.scilla.web.common.response.MessageResponse;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.fasterxml.jackson.annotation.JsonFormat;

public class BcrTitleResponse extends MessageResponse {

	private static final long serialVersionUID = 1L;
	private final String[] monthNames = new String[] {null, "January","February","March","April","May","June","July","August","September","October","November","December"};
	private final Logger logger = LogManager.getLogger(BcrTitleResponse.class);
	
	
	private Calendar dateCreated;
	private Calendar dateModified;
	private Integer workYear;
	private Integer workMonth;
	private String workMonthName;
	private Calendar firstOfMonth;
	private Calendar lastOfMonth;
	private Integer divisionId;
	private String div;
	private String managerFirstName;
	private String managerLastName;
	private Integer managerId;
	private List<WorkWeek> workCalendar;
	
	public BcrTitleResponse() {
		super();
	}
	
	public BcrTitleResponse(Connection conn, SessionUser sessionUser, Integer divisionId, Calendar selectedDate) throws Exception {
		this();
		Calendar today = Calendar.getInstance(new AnsiTime());
		WorkYear workYear = new WorkYear(selectedDate.get(Calendar.YEAR));
		Division division = new Division();
		division.setDivisionId(divisionId);
		division.selectOne(conn);
		this.dateCreated = today;
		this.dateModified = today;
		this.workYear = workYear.getYear();
		this.workMonth = workYear.getWorkMonth(selectedDate);
		logger.log(Level.DEBUG, "workMonth: " + workMonth);
		this.workMonthName = monthNames[workMonth];
		this.firstOfMonth = workYear.getFirstOfMonth(selectedDate);
		this.lastOfMonth = workYear.getLastOfMonth(selectedDate);
		this.divisionId = divisionId;
		this.div = division.getDivisionDisplay();
		this.managerFirstName = sessionUser.getFirstName();
		this.managerLastName = sessionUser.getLastName();
		this.managerId = sessionUser.getUserId();
		this.workCalendar = BcrUtils.makeWorkCalendar(firstOfMonth, lastOfMonth);
	}
	
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public Calendar getDateCreated() {
		return dateCreated;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public void setDateCreated(Calendar dateCreated) {
		this.dateCreated = dateCreated;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public Calendar getDateModified() {
		return dateModified;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public void setDateModified(Calendar dateModified) {
		this.dateModified = dateModified;
	}
	public Integer getWorkYear() {
		return workYear;
	}
	public void setWorkYear(Integer workYear) {
		this.workYear = workYear;
	}
	public Integer getWorkMonth() {
		return workMonth;
	}
	public void setWorkMonth(Integer workMonth) {
		this.workMonth = workMonth;
	}
	public String getWorkMonthName() {
		return workMonthName;
	}
	public void setWorkMonthName(String workMonthName) {
		this.workMonthName = workMonthName;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public Calendar getFirstOfMonth() {
		return firstOfMonth;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public void setFirstOfMonth(Calendar firstOfMonth) {
		this.firstOfMonth = firstOfMonth;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public Calendar getLastOfMonth() {
		return lastOfMonth;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public void setLastOfMonth(Calendar lastOfMonth) {
		this.lastOfMonth = lastOfMonth;
	}
	public Integer getDivisionId() {
		return divisionId;
	}
	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}
	public String getDiv() {
		return div;
	}
	public void setDiv(String div) {
		this.div = div;
	}
	public String getManagerFirstName() {
		return managerFirstName;
	}
	public void setManagerFirstName(String managerFirstName) {
		this.managerFirstName = managerFirstName;
	}
	public String getManagerLastName() {
		return managerLastName;
	}
	public void setManagerLastName(String managerLastName) {
		this.managerLastName = managerLastName;
	}

	public Integer getManagerId() {
		return managerId;
	}

	public void setManagerId(Integer managerId) {
		this.managerId = managerId;
	}

	public List<WorkWeek> getWorkCalendar() {
		return workCalendar;
	}

	public void setWorkCalendar(List<WorkWeek> workCalendar) {
		this.workCalendar = workCalendar;
	}

	
	
	
}
