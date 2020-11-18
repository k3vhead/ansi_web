package com.ansi.scilla.web.bcr.query;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;

import com.ansi.scilla.web.common.struts.SessionDivision;

public class BcrLookupQuery extends AbstractBcrLookupQuery {

	private static final long serialVersionUID = 1L;
	
	private Integer divisionId;

	public BcrLookupQuery(Integer userId, List<SessionDivision> divisionList, Integer divisionId) {
		super(sqlSelectClause, sqlFromClause, baseWhereClause);
		this.logger = LogManager.getLogger(this.getClass());
		this.userId = userId;	
		this.divisionId = divisionId;
		super.setBaseFilterValue(Arrays.asList( new Object[] {divisionId}));
	}

	public BcrLookupQuery(Integer userId, List<SessionDivision> divisionList, Integer divisionId, String searchTerm) {
		this(userId, divisionList, divisionId);
		this.searchTerm = searchTerm;
	}

	public Integer getDivisionId() {
		return divisionId;
	}

	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}


	
	@Override
	protected String makeWhereClause(String queryTerm) {
		String whereClause = baseWhereClause;
		String joiner = StringUtils.isBlank(baseWhereClause) ? " where " : " and ";
		
		if ( ! StringUtils.isBlank(queryTerm) ) {
			whereClause =  whereClause + joiner + " (\n"
					+ " " + TICKET_ID + " like '%" + queryTerm.toLowerCase() +  "%'" +
					"\n OR lower( " + NAME + " ) ) like '%" + queryTerm.toLowerCase() + "%'" +
					")" ;
		}
		
		return whereClause;
	}
	
	

}
