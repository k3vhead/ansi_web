package com.ansi.scilla.web.division.response;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.beanutils.PropertyUtils;

import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.web.common.response.MessageResponse;

/**
 * Used to return a single code to the client
 * 
 * @author dclewis
 *
 */
public class DivisionResponse extends MessageResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private DivisionCountRecord division;

	public DivisionResponse() {
		super();
	}

	public DivisionResponse(DivisionCountRecord division) {
		super();
		this.division = division;
	}

	public DivisionResponse(Connection conn, Division division) throws IllegalAccessException, InvocationTargetException, SQLException, NoSuchMethodException {
		this();
		this.division = new DivisionCountRecord();
		this.division.setDefaultDirectLaborPct(division.getDefaultDirectLaborPct());
		this.division.setDescription(division.getDescription());
		this.division.setDivisionCode(division.getDivisionCode());
		this.division.setDivisionId(division.getDivisionId());
		this.division.setDivisionNbr(division.getDivisionNbr());
		this.division.setHourlyRateIsFixed(division.getHourlyRateIsFixed());
		this.division.setMaxRegHrsPerDay(division.getMaxRegHrsPerDay());
		this.division.setMaxRegHrsPerWeek(division.getMaxRegHrsPerWeek());
		this.division.setOvertimeRate(division.getOvertimeRate());
		this.division.setParentId(division.getParentId());
//		this.division.setStatus(status);
//		this.division.setUserCount(userCount);
		this.division.setWeekendIsOt(division.getWeekendIsOt());
		Integer userCount = this.makeUserCount(conn, division.getDivisionId());
		this.division.setUserCount(userCount);
		if ( division.getStatus().equals(Division.STATUS_IS_ACTIVE)) {
			this.division.setStatus("Active");
		} else if (division.getStatus().equals(Division.STATUS_IS_INACTIVE) ) {
			this.division.setStatus("Inactive");
		} else { 
			this.division.setStatus(null);
		}
	}

	private Integer makeUserCount(Connection conn, Integer divisionId) throws SQLException {
		String sql = "select count(*) as user_count from division_user where division_id = ?";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setInt(1, divisionId);
		ResultSet rs = ps.executeQuery();
		Integer userCount = 0;
		if(rs.next()){
			userCount=rs.getInt("user_count");
		}
		rs.close();
		return userCount;
	}

	public DivisionCountRecord getDivision() {
		return division;
	}
	
	
}
