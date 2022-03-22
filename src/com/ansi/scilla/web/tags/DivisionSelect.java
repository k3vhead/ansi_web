package com.ansi.scilla.web.tags;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.ansi.scilla.web.common.utils.AppUtils;


public class DivisionSelect extends OptionTag {

	private static final long serialVersionUID = 1L;

	@Override
	protected List<Option> makeOptionList() throws Exception {
		List<Option> options = new ArrayList<Option>();
		Connection conn = null;
		try {
			conn = AppUtils.getDBCPConn();
			Statement s = conn.createStatement();
			ResultSet rs = s.executeQuery("select division_id, concat(division_nbr,'-',division_code) as div \n"
					+ "from division\n"
					+ "order by division_nbr");
			while ( rs.next() ) {
				options.add(new Option(String.valueOf(rs.getInt("division_id")), rs.getString("div")));
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
