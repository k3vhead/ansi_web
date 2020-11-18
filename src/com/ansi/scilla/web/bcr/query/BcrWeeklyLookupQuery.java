package com.ansi.scilla.web.bcr.query;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.web.common.struts.SessionDivision;

public class BcrWeeklyLookupQuery extends AbstractBcrLookupQuery {
	private static final long serialVersionUID = 1L;

	private String workWeek;
	private Integer divisionId;
	
	public BcrWeeklyLookupQuery(Integer userId, List<SessionDivision> divisionList, Integer divisionId, String workWeek) {
		super(sqlSelectClause, sqlFromClause, makeBaseWhereClause());
		this.logger = LogManager.getLogger(this.getClass());
		this.userId = userId;	
		this.divisionId = divisionId;
		this.workWeek = workWeek;
		super.setBaseFilterValue(Arrays.asList( new Object[] {divisionId, workWeek}));
	}

	public BcrWeeklyLookupQuery(Integer userId, List<SessionDivision> divisionList, Integer divisionId, String workWeek, String searchTerm) {
		this(userId, divisionList, divisionId, workWeek);
		this.searchTerm = searchTerm;
	}

	private static String makeBaseWhereClause() {
		Logger myLogger = LogManager.getLogger(BcrWeeklyLookupQuery.class);
		String whereClause = baseWhereClause + " and concat(year(work_date),'-',right('00'+CAST(isnull(datepart(wk,work_date),0) as VARCHAR),2))=?";
		myLogger.log(Level.DEBUG, whereClause);
		return whereClause;
	}

	public String getWorkWeek() {
		return workWeek;
	}

	public void setWorkWeek(String workWeek) {
		this.workWeek = workWeek;
	}

	public Integer getDivisionId() {
		return divisionId;
	}

	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}

	
	@Override
	protected String makeWhereClause(String queryTerm) {
		String whereClause = makeBaseWhereClause();
		String joiner = StringUtils.isBlank(whereClause) ? " where " : " and ";
		
		if ( ! StringUtils.isBlank(queryTerm) ) {
			whereClause =  whereClause + joiner + " (\n"
					+ " " + TICKET_ID + " like '%" + queryTerm.toLowerCase() +  "%'" +
					"\n OR lower( " + NAME + " ) ) like '%" + queryTerm.toLowerCase() + "%'" +
					")" ;
		}
		
		return whereClause;
	}

}
