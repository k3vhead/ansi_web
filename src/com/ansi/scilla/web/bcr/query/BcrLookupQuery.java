package com.ansi.scilla.web.bcr.query;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.web.common.struts.SessionDivision;

/**
 * Returns data to populate "all tickets" panel in BCR page
 * 
 * @author dclewis
 *
 */
public class BcrLookupQuery extends AbstractBcrLookupQuery {

	private static final long serialVersionUID = 1L;
		

	/**
	 * 
	 * @param userId
	 * @param divisionList
	 * @param divisionId
	 * @param workYear
	 * @param workWeek comma-delimited list of work weeks: eg "45,45,46,47"
	 */
	public BcrLookupQuery(Integer userId, List<SessionDivision> divisionList, Integer divisionId, Integer workYear, String workWeek) {
		super(userId, divisionId, workYear, workWeek, sqlSelectClause, makeFilteredFromClause(divisionList), makeBaseWhereClause(workWeek));
	}

	public BcrLookupQuery(Integer userId, List<SessionDivision> divisionList, Integer divisionId, Integer workYear, String workWeek, String searchTerm) {
		this(userId, divisionList, divisionId, workYear, workWeek);
		this.searchTerm = searchTerm;
	}

	private static String makeBaseWhereClause(String workWeek) {
		Logger myLogger = LogManager.getLogger(BcrLookupQuery.class);
		String whereClause = baseWhereClause.replaceAll("\\$CLAIMWEEKFILTER\\$", workWeek);
		myLogger.log(Level.DEBUG, whereClause);
		return whereClause;
	}

	

	@Override
	protected String makeWhereClause(String queryTerm) {
		String whereClause = makeBaseWhereClause(workWeek);
		String joiner = " and ";
		
		if ( ! StringUtils.isBlank(queryTerm) ) {
			whereClause =  whereClause + joiner + " (\n"
					+ " " + TICKET_ID + " like '%" + queryTerm.toLowerCase() +  "%'" +
					"\n OR lower( " + NAME + " ) ) like '%" + queryTerm.toLowerCase() + "%'" +
					")" ;
		}
		
		return whereClause;
	}
	
	

}
