package com.ansi.scilla.web.code.servlet;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections4.Transformer;
import org.apache.logging.log4j.Level;

import com.ansi.scilla.web.code.query.CodeLookupQuery;
import com.ansi.scilla.web.common.query.LookupQuery;
import com.ansi.scilla.web.common.servlet.AbstractLookupServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionDivision;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.common.utils.AnsiURL;
import com.ansi.scilla.web.common.utils.Permission;
import com.ansi.scilla.web.exceptions.ResourceNotFoundException;

public class CodeLookupServlet extends AbstractLookupServlet {

	private static final long serialVersionUID = 1L;
	
	public static final String REALM = "codeLookup";
	
	public static final String CAN_DELETE = "can_delete";
	
	public CodeLookupServlet() {
		super(Permission.SYSADMIN_READ);
		cols = new String[] { 
				CodeLookupQuery.TABLE_NAME,
				CodeLookupQuery.FIELD_NAME,
				CodeLookupQuery.VALUE,
				CodeLookupQuery.DISPLAY_VALUE,
				CodeLookupQuery.SEQ,
				CodeLookupQuery.DESCRIPTION,
				CodeLookupQuery.CODE_STATUS,
				CAN_DELETE,
				};
//		super.itemTransformer = new ItemTransformer();
	}


	


	@Override
	public LookupQuery makeQuery(Connection conn, HttpServletRequest request) {
		HttpSession session = request.getSession();
		SessionData sessionData = (SessionData)session.getAttribute(SessionData.KEY);
		
		SessionUser user = sessionData.getUser();
		List<SessionDivision> divisionList = sessionData.getDivisionList();
		try {
			Statement statement = conn.createStatement();
			super.itemTransformer = new ItemTransformer(statement);

			AnsiURL url = new AnsiURL(request, REALM, (String[])null);
			String searchTerm = null;
			if(request.getParameter("search[value]") != null){
				searchTerm = request.getParameter("search[value]");
			}
			CodeLookupQuery lookupQuery = new CodeLookupQuery(user.getUserId(), divisionList);
			if ( searchTerm != null ) {
				lookupQuery.setSearchTerm(searchTerm);
			}

			
			return lookupQuery;
			
		} catch (ResourceNotFoundException e) { 
			throw new RuntimeException(e);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}




	public class ItemTransformer implements Transformer<HashMap<String, Object>,HashMap<String, Object>> {
		private Statement statement;

		public ItemTransformer(Statement statement) {
			super();
			this.statement = statement;
		}

		@Override
		public HashMap<String, Object> transform(HashMap<String, Object> arg0) {
			try {
				String sql = "select count(*) as record_count from " + (String)arg0.get("table_name") + "\n"
						+ " where " + (String)arg0.get("field_name") +"='" + (String)arg0.get("value") + "'";
//				logger.log(Level.DEBUG, sql);
				ResultSet rs = statement.executeQuery(sql);
				Boolean canDelete = false;
				if ( rs.next() ) {
					canDelete = rs.getInt("record_count") == 0;
				}
				rs.close();
				arg0.put(CAN_DELETE, canDelete);
				return arg0;
			} catch ( SQLException e ) {
				logger.log(Level.DEBUG, e);
				arg0.put(CAN_DELETE, true);
				return arg0;
			} catch ( Exception e) {
				throw new RuntimeException(e);	
			}
		}

	}


	
	
}
