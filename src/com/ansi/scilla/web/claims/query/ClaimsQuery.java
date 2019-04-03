package com.ansi.scilla.web.claims.query;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.web.common.query.LookupQuery;
import com.ansi.scilla.web.common.struts.SessionDivision;
import com.ansi.scilla.web.common.utils.SessionDivisionTransformer;

public abstract class ClaimsQuery extends LookupQuery {

	private static final long serialVersionUID = 1L;
	
	protected List<SessionDivision> divisionList;
	
	public ClaimsQuery(String sqlSelectClause, String sqlFromClause, String baseWhereClause) {
		super(sqlSelectClause, sqlFromClause, baseWhereClause);
	}

	protected static String makeFromClause(String sqlFromClause, List<SessionDivision> divisionList) {
		List<Integer> divisionIdList = new ArrayList<Integer>();
		divisionIdList = CollectionUtils.collect(divisionList.iterator(), new SessionDivisionTransformer(), divisionIdList);
		String divisionFilter = StringUtils.join(divisionIdList, ",");
		String whereClause = sqlFromClause.replaceAll("\\$DIVISION_USER_FILTER\\$", divisionFilter);
		return whereClause;
	}

	
	public List<SessionDivision> getDivisionList() {
		return divisionList;
	}

	public void setDivisionList(List<SessionDivision> divisionList) {
		this.divisionList = divisionList;
	}

	
}
