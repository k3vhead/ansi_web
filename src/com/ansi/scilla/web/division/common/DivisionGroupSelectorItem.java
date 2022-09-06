package com.ansi.scilla.web.division.common;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.db.DivisionGroup;

public class DivisionGroupSelectorItem extends ApplicationObject {
	private static final long serialVersionUID = 1L;
	private Integer groupId;
	private String companyCode;
	private String groupName;
	private Boolean active;
	public DivisionGroupSelectorItem(ResultSet rs) throws SQLException {
		super();
		this.groupId = rs.getInt("group_id");
		this.companyCode = rs.getString("company_code");
		this.groupName = rs.getString("name");
		this.active = rs.getInt("group_status") == DivisionGroup.STATUS_IS_ACTIVE;
	}
	public Integer getGroupId() {
		return groupId;
	}
	public String getCompanyCode() {
		return companyCode;
	}
	public String getGroupName() {
		return groupName;
	}
	
	public Boolean getActive() {
		return active;
	}
	public String makeHtml() {
		return "<option value=\"" + this.groupId + "\">" + this.groupName  + "</option>";
	}
	
	public String makeCompanyHtml() {
		return "<option value=\"" + this.companyCode + "\">" + this.companyCode  + "</option>";
	}
}
