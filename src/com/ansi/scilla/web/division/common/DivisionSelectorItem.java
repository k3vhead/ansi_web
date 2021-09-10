package com.ansi.scilla.web.division.common;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.db.Division;

public class DivisionSelectorItem extends ApplicationObject {
	private static final long serialVersionUID = 1L;
	private Integer divisionId;
	private String div;
	private Boolean active;
	public DivisionSelectorItem(ResultSet rs) throws SQLException {
		super();
		this.divisionId = rs.getInt("division_id");
		this.div = rs.getString("div");
		this.active = rs.getInt("division_status") == Division.STATUS_IS_ACTIVE;
	}
	public Integer getDivisionId() {
		return divisionId;
	}
	public String getDiv() {
		return div;
	}
	public Boolean getActive() {
		return active;
	}
	public String makeHtml() {
		return "<option value=\"" + this.divisionId + "\">" + this.div + "</option>";
	}
	
}