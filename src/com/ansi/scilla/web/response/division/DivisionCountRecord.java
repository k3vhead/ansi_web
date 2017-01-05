package com.ansi.scilla.web.response.division;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.queries.DivisionUserCount;
import com.thewebthing.commons.lang.BeanUtils;

/**
 * 
 * @author dclewis
 *
 */

public class DivisionCountRecord extends ApplicationObject {
	private static final long serialVersionUID = 1L;
	private BigDecimal defaultDirectLaborPct;
	private Integer divisionId;
	private String divisionCode;
	private Integer divisionNbr;
	private String description;
	
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

	private Integer parentId;
	private Integer userCount;
	
	public DivisionCountRecord(DivisionUserCount divisionUserCount) throws IllegalAccessException, InvocationTargetException {
		this();
		BeanUtils.copyProperties(this, divisionUserCount.getDivision());
		this.userCount = divisionUserCount.getUserCount();			
	}

	public DivisionCountRecord() {
		super();
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

}