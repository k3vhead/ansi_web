package com.ansi.scilla.web.payroll.response;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.ansi.scilla.web.common.response.MessageResponse;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class AliasResponse extends MessageResponse {

	private static final long serialVersionUID = 1L;

	private Integer employeeCode;
	private String employeeName;
	
	public AliasResponse() {
		super();
	}
	
	public AliasResponse(Connection conn, Integer employeeCode, String employeeName) throws RecordNotFoundException, SQLException {
		this();
		PreparedStatement ps = conn.prepareStatement("select employee_code, employee_name from employee_alias where employee_code=? and lower(employee_name)=?");
		ps.setInt(1, employeeCode);
		ps.setString(2, employeeName.toLowerCase());
		ResultSet rs = ps.executeQuery();
		if (rs.next() ) {
			this.employeeCode = rs.getInt("employee_code");
			this.employeeName = rs.getString("employee_name");
		} else {
			throw new RecordNotFoundException();
		}
	}
	
	
	public Integer getEmployeeCode() {
		return employeeCode;
	}
	public void setEmployeeCode(Integer employeeCode) {
		this.employeeCode = employeeCode;
	}
	public String getEmployeeName() {
		return employeeName;
	}
	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}
	
	
}
