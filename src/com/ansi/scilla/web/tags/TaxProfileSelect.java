package com.ansi.scilla.web.tags;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.ansi.scilla.web.common.utils.AppUtils;


public class TaxProfileSelect extends OptionTag {

	private static final long serialVersionUID = 1L;

	@Override
	protected List<Option> makeOptionList() throws Exception {
		List<Option> options = new ArrayList<Option>();
		Connection conn = null;
		try {
			conn = AppUtils.getDBCPConn();
			Statement s = conn.createStatement();
			ResultSet rs = s.executeQuery("select ptp.profile_id, ptp.profile_desc\n"
					+ "from payroll_tax_profile ptp\n"
					+ "order by ptp.profile_desc");
			while ( rs.next() ) {
				options.add(new Option(String.valueOf(rs.getInt("profile_id")), rs.getString("profile_desc")));
			}
			rs.close();
		} catch ( Exception e) {
			AppUtils.logException(e);
			throw e;
		} finally {
			AppUtils.closeQuiet(conn);
		}
		return options;
	}

	
}
