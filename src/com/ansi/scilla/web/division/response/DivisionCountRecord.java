package com.ansi.scilla.web.division.response;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;

import org.apache.commons.beanutils.BeanUtils;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.common.queries.DivisionUserCount;

/**
 * 
 * @author dclewis
 *
 */

public class DivisionCountRecord extends ApplicationObject implements Comparable<DivisionCountRecord> {
	private static final long serialVersionUID = 1L;
	private BigDecimal defaultDirectLaborPct;
	private Integer divisionId;
	private String divisionCode;
	private Integer divisionNbr;
	private String description;
	private Integer parentId;
	private Integer userCount;
	private String status;
	private Integer maxRegHrsPerDay;
    private Integer maxRegHrsPerWeek;
    private BigDecimal overtimeRate;
    private Integer weekendIsOt;
    private Integer hourlyRateIsFixed;
	
	
	public DivisionCountRecord() {
		super();
	}

	public DivisionCountRecord(DivisionUserCount divisionUserCount) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		this();		
		BeanUtils.copyProperties(this, divisionUserCount.getDivision());
		if ( divisionUserCount.getDivision().getDivisionId() == null ) {
			this.divisionId = null;
		}
		this.userCount = divisionUserCount.getUserCount();	
		if ( divisionUserCount.getDivision().getStatus().equals(Division.STATUS_IS_ACTIVE)) {
			this.status = "Active";
		} else if (divisionUserCount.getDivision().getStatus().equals(Division.STATUS_IS_INACTIVE) ) {
			this.status = "Inactive";
		} else { 
			this.status = null;
		}
	}

	public Integer getDivisionNbr() {
		return divisionNbr;
	}

	public void setDivisionNbr(Integer divisionNbr) {
		this.divisionNbr = divisionNbr;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigDecimal getDefaultDirectLaborPct() {
		return defaultDirectLaborPct;
	}

	public void setDefaultDirectLaborPct(BigDecimal defaultDirectLaborPct) {
		this.defaultDirectLaborPct = defaultDirectLaborPct;
	}

	public Integer getDivisionId() {
		return divisionId;
	}

	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}

	public String getDivisionCode() {
		return divisionCode;
	}

	public void setDivisionCode(String divisionCode) {
		this.divisionCode = divisionCode;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public Integer getUserCount() {
		return userCount;
	}

	public void setUserCount(Integer userCount) {
		this.userCount = userCount;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getMaxRegHrsPerDay() {
		return maxRegHrsPerDay;
	}

	public void setMaxRegHrsPerDay(Integer maxRegHrsPerDay) {
		this.maxRegHrsPerDay = maxRegHrsPerDay;
	}

	public Integer getMaxRegHrsPerWeek() {
		return maxRegHrsPerWeek;
	}

	public void setMaxRegHrsPerWeek(Integer maxRegHrsPerWeek) {
		this.maxRegHrsPerWeek = maxRegHrsPerWeek;
	}

	public BigDecimal getOvertimeRate() {
		return overtimeRate;
	}

	public void setOvertimeRate(BigDecimal overtimeRate) {
		this.overtimeRate = overtimeRate;
	}

	public Integer getWeekendIsOt() {
		return weekendIsOt;
	}

	public void setWeekendIsOt(Integer weekendIsOt) {
		this.weekendIsOt = weekendIsOt;
	}

	public Integer getHourlyRateIsFixed() {
		return hourlyRateIsFixed;
	}

	public void setHourlyRateIsFixed(Integer hourlyRateIsFixed) {
		this.hourlyRateIsFixed = hourlyRateIsFixed;
	}

	@Override
	public int compareTo(DivisionCountRecord o) {
		String myDisplay = getDivisionNbr() + "-" + getDivisionCode();
		String yourDisplay = o.getDivisionNbr() + "-" + o.getDivisionCode();
		return myDisplay.compareTo(yourDisplay);
	}

}
