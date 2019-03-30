package com.ansi.scilla.web.common.struts;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.db.Division;

public class SessionDivision extends ApplicationObject implements Comparable<SessionDivision> {

	private static final long serialVersionUID = 1L;

	private Integer divisionId;
	private Integer divisionNbr;
	private String divisionCode;
	public SessionDivision() {
		super();
	}
	public SessionDivision(Division division) {
		this();
		this.divisionId = division.getDivisionId();
		this.divisionNbr = division.getDivisionNbr();
		this.divisionCode = division.getDivisionCode();
	}
	
	public Integer getDivisionId() {
		return divisionId;
	}
	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}
	public Integer getDivisionNbr() {
		return divisionNbr;
	}
	public void setDivisionNbr(Integer divisionNbr) {
		this.divisionNbr = divisionNbr;
	}
	public String getDivisionCode() {
		return divisionCode;
	}
	public void setDivisionCode(String divisionCode) {
		this.divisionCode = divisionCode;
	}
	
	public String getDivisionDisplay() {
		return this.getDivisionNbr() + "-" + this.getDivisionCode();
	}
	@Override
	public int compareTo(SessionDivision o) {
		return this.getDivisionDisplay().compareTo(o.getDivisionDisplay());
	}
	
	
}
