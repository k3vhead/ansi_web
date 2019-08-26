package com.ansi.scilla.web.locale.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.ansi.scilla.web.common.response.AbstractAutocompleteReturnItem;
import com.ansi.scilla.web.common.servlet.AbstractAutocompleteServlet;
import com.ansi.scilla.web.common.utils.AppUtils;


public class LocaleAutocompleteServlet extends AbstractAutocompleteServlet {

	private static final long serialVersionUID = 1L;

	public LocaleAutocompleteServlet() {
		super(null);
	}
	



	
	@Override
	protected String doSearch(Connection conn, String term, HashMap<String, String> searchKeyMap) throws SQLException, IOException {
		String sql = "select ansi_user.user_id, concat(ansi_user.first_name, ' ', ansi_user.last_name) as washer_name\n" + 
				" from permission_group_level\n" + 
				" inner join permission_group on permission_group.permission_group_id=permission_group_level.permission_group_id\n" + 
				" inner join ansi_user on ansi_user.permission_group_id=permission_group.permission_group_id\n" + 
				" where permission_group_level.permission_name='CAN_RUN_TICKETS'\n" + 
				" and lower(concat(ansi_user.first_name, ' ', ansi_user.last_name)) like '%" + term.toLowerCase() + "%'";
		Statement s = conn.createStatement();
		ResultSet rs = s.executeQuery(sql);
		List<ReturnItem> resultList = new ArrayList<ReturnItem>();
		while ( rs.next() ) {
			resultList.add(new ReturnItem(rs));
		}
		rs.close();
		
		String json = AppUtils.object2json(resultList);
		
		return json;
	}
	
	
	public class ReturnItem extends AbstractAutocompleteReturnItem {

		private static final long serialVersionUID = 1L;

		public ReturnItem(ResultSet rs) throws SQLException {
			super(rs);
		}

		@Override
		protected Integer formatId(ResultSet rs) throws SQLException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		protected String formatLabel(ResultSet rs) throws SQLException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		protected String formatName(ResultSet rs) throws SQLException {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
}
