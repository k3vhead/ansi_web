package com.ansi.scilla.web.division.query;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.common.queries.SelectType;
import com.ansi.scilla.common.utils.WhereFieldLikeTransformer;
import com.ansi.scilla.web.common.query.LookupQuery;
import com.ansi.scilla.web.common.struts.SessionDivision;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.SessionDivisionTransformer;

public class DivisionCloseQuery extends LookupQuery {

	private static final long serialVersionUID = 1L;

	public static final String DIVISION_ID = "division.division_id";
	public static final String DIV = "concat(division_nbr,'-',division_code)";
	public static final String DIVISION_NBR = "division.division_nbr";
	public static final String DIVISION_DESCRIPTION = "division.description";
	public static final String ACT_CLOSE_DATE = "division.act_close_date";
	public static final String NEXT_CLOSE_DATE = "	isnull(" + 
			"		(select top(1) ansi_date from ansi_calendar where ansi_date > (select act_close_date from division d where d.division_id=division.division_id) and date_type='DIVISION_CLOSE' order by ansi_date asc )," + 
			"		(select top(1) ansi_date from ansi_calendar where ansi_date < sysdatetime() and date_type='DIVISION_CLOSE' order by ansi_date desc )" + 
			"	) as next_close_date";
	public static final String LAST_CLOSE_DATE = "select top(1) ansi_date from ansi_calendar where ansi_date < (select act_close_date from division d where d.division_id=division.division_id) and date_type='DIVISION_CLOSE' order by ansi_date desc";

	
	public static final String sqlSelect = 
			"select division.division_id, \n" + 
			"	concat(division_nbr,'-',division_code) as div, \n" + 
			"	division.division_nbr, \n" + 
			"	division.description, \n" + 
			"	division.act_close_date, \n" + 
			"	isnull(\n" + 
			"		(select top(1) ansi_date from ansi_calendar where ansi_date > (select act_close_date from division d where d.division_id=division.division_id) and date_type='DIVISION_CLOSE' order by ansi_date asc ),\n" + 
			"		(select top(1) ansi_date from ansi_calendar where ansi_date < sysdatetime() and date_type='DIVISION_CLOSE' order by ansi_date desc )\n" + 
			"	) as next_close_date,\n" + 
			"	(select top(1) ansi_date from ansi_calendar where ansi_date < (select act_close_date from division d where d.division_id=division.division_id) and date_type='DIVISION_CLOSE' order by ansi_date desc) as last_close_date";
	
	public static final String sqlFromClause = 
			"\n from division ";
	
	public static final String sqlWhereClause = "";
	
	
	public DivisionCloseQuery(Integer userId, List<SessionDivision> divisionList) {
		super(sqlSelect, makeFromClause(sqlFromClause, divisionList, ""), sqlWhereClause);
		this.logger = LogManager.getLogger(this.getClass());
		this.userId = userId;
	}
	

	@Override
	protected String makeOrderBy(SelectType selectType) {
		String orderBy = "";
		if ( selectType.equals(SelectType.DATA)) {
			if ( StringUtils.isBlank(sortBy)) {
				orderBy = "order by concat(division_nbr,'-',division_code) asc";
			} else {
//				List<String> sortList = Arrays.asList(StringUtils.split(sortBy, ","));
				String sortDir = sortIsAscending ? orderBy + " asc " : orderBy + " desc ";
//				String sortBy = StringUtils.join(sortList, sortDir + ", ");
				orderBy = " order by " + sortBy + " " + sortDir;
			}
		}

		return "\n" + orderBy;
	}

	@Override
	protected String makeWhereClause(String queryTerm) {
		logger.log(Level.DEBUG, "makeWhereClause");
		String whereClause = sqlWhereClause;
		String joiner = StringUtils.isBlank(sqlWhereClause) ? " where " : " and ";
		
		
		if (! StringUtils.isBlank(queryTerm)) {
			String searchTerm = queryTerm.toLowerCase();
			Collection<String> stringFields= Arrays.asList(new String[] {
					DIVISION_DESCRIPTION,
					ACT_CLOSE_DATE,
					NEXT_CLOSE_DATE,
					LAST_CLOSE_DATE
			});
			Collection<String> numericFields = Arrays.asList(new String[] {
					DIVISION_NBR,
					DIVISION_ID
			});
			
			List<String> whereFields = new ArrayList<String>();
			whereFields.addAll(stringFields);
			if ( StringUtils.isNumeric(searchTerm)) {
				whereFields.addAll(numericFields);
			}

			Collection<?> whereClauseList = CollectionUtils.collect(whereFields, new WhereFieldLikeTransformer(searchTerm));
			whereClause = whereClause + joiner +  "(" + StringUtils.join(whereClauseList, " \n\tOR ") + ")";
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
		try {
			conn = AppUtils.getDevConn();
			List<SessionDivision> sdList = new ArrayList<SessionDivision>();
			List<Division> divisionList = Division.cast(new Division().selectAll(conn));
			for ( Division d : divisionList) { sdList.add(new SessionDivision(d)); }
			DivisionCloseQuery x = new DivisionCloseQuery(5, sdList);
//			x.setSearchTerm("Chicago");
			System.out.println("Countall: " + x.countAll(conn));
			System.out.println("Count some: " + x.selectCount(conn));
			ResultSet rs = x.select(conn,  0, 100);
			while ( rs.next() ) { 
				System.out.println(rs.getString("div"));
			}
			rs.close();
		} catch ( Exception e) {
			e.printStackTrace();
		} finally {
			AppUtils.closeQuiet(conn);
		}
	}
	
	
}
