package com.ansi.scilla.web.calendar.query;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.common.queries.SelectType;
import com.ansi.scilla.web.common.query.LookupQuery;
import com.ansi.scilla.web.common.struts.SessionDivision;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.SessionDivisionTransformer;

public class CalendarLookupQuery extends LookupQuery {

	private static final long serialVersionUID = 1L;

	
	public static final String sqlSelect = 
			"select ansi_date, date_type \n"; 
			

	public static final String sqlFromClause = 
			"from ansi_calendar \n";
	
	public static final String sqlWhereClause = "";
	
	
	public CalendarLookupQuery(Integer userId, List<SessionDivision> divisionList) {
		super(sqlSelect, makeFromClause(sqlFromClause, divisionList, ""), sqlWhereClause);
		this.logger = LogManager.getLogger(this.getClass());
		this.userId = userId;
		Calendar now = Calendar.getInstance();
		this.searchTerm = String.valueOf(now.get(Calendar.YEAR));
//		this.divisionList = divisionList;
	}
	
	





	/** *********************** **/
	
	@Override
	protected String makeOrderBy(SelectType selectType) {
		String orderBy = "";
		if ( selectType.equals(SelectType.DATA)) {
			orderBy = "order by ansi_date asc";
		}

		return "\n" + orderBy;
	}

	@Override
	protected String makeWhereClause(String queryTerm) {
		logger.log(Level.DEBUG, "makeWhereClause");
		String whereClause = sqlWhereClause;
		if ( ! StringUtils.isBlank(queryTerm)) {
			whereClause = "where year(ansi_date)=?";
		}
		
		return whereClause;
	}

	
	
	private static String makeFromClause(String sqlfromclause, List<SessionDivision> divisionList, String divisionFilter) {
		List<Integer> divisionIdList = (List<Integer>) CollectionUtils.collect(divisionList, new SessionDivisionTransformer());
		String divisionString = StringUtils.join(divisionIdList, ",");
		String sql = StringUtils.replace(sqlFromClause, "$USERFILTER$", divisionString);
		System.out.println("adding " + divisionFilter + " to sql");
		sql = StringUtils.replace(sql, "$DIVISIONFILTER$", divisionFilter);
		
		return sql;
	}

	
	public static void main(String[] args) {
		Connection conn = null;
		Calendar startDate = new GregorianCalendar(2019, Calendar.APRIL, 15);
		try {
			conn = AppUtils.getDevConn();
			List<SessionDivision> sdList = new ArrayList<SessionDivision>();
			List<Division> divisionList = Division.cast(new Division().selectAll(conn));
			for ( Division d : divisionList) { sdList.add(new SessionDivision(d)); }
			CalendarLookupQuery x = new CalendarLookupQuery(5, sdList);
//			x.setSearchTerm("2019");
			System.out.println("Search Term: " + x.getSearchTerm());
			System.out.println("Countall: " + x.countAll(conn));
			System.out.println("Count some: " + x.selectCount(conn));
			ResultSet rs = x.select(conn,  0, 10);
			while ( rs.next() ) { 
				System.out.println(rs.getDate("ansi_date") + "\t" + rs.getString("date_type"));
			}
			rs.close();
		} catch ( Exception e) {
			e.printStackTrace();
		} finally {
			AppUtils.closeQuiet(conn);
		}
	}
	
	
}
