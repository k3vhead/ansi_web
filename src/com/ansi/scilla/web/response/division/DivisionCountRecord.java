package com.ansi.scilla.web.response.division;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.queries.DivisionUserCount;
import com.thewebthing.commons.lang.BeanUtils;

public class DivisionCountRecord extends ApplicationObject {
	private static final long serialVersionUID = 1L;
	private BigDecimal defaultDirectLaborPct;
	private Integer divisionId;
	private String name;
	private Integer parentId;
	private Integer userCount;
	
	public DivisionCountRecord(DivisionUserCount divisionUserCount) throws IllegalAccessException, InvocationTargetException {
		super();
		BeanUtils.copyProperties(this, divisionUserCount.getDivision());
		this.userCount = divisionUserCount.getUserCount();			
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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