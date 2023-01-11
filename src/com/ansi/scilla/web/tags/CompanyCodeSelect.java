package com.ansi.scilla.web.tags;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;

import com.ansi.scilla.web.common.utils.AppUtils;

public class CompanyCodeSelect extends OptionTag {

	private static final long serialVersionUID = 1L;	
	
		
	@Override
	protected List<Option> makeOptionList() throws Exception {
		Connection conn = null;
		try {
			conn = AppUtils.getDBCPConn();
			List<Option> optionList = new ArrayList<Option>();
			Statement s = conn.createStatement();
			ResultSet rs = s.executeQuery("select distinct company_code from payroll_employee order by company_code");
			while ( rs.next() ) {
				String companyCode = rs.getString("company_code");
				optionList.add(new Option(companyCode, companyCode));
			}
			rs.close();
			return optionList;
		} catch (Exception e) {
			e.printStackTrace();
			throw new JspException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}
	}
	
	
	
	
	
	
	
}
