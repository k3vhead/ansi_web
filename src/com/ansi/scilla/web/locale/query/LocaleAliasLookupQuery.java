package com.ansi.scilla.web.locale.query;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

import com.ansi.scilla.common.queries.SelectType;
import com.ansi.scilla.web.common.query.LookupQuery;
import com.ansi.scilla.web.common.struts.SessionDivision;

public class LocaleAliasLookupQuery extends LookupQuery {

	private static final long serialVersionUID = 1L;
	
	public static final String LOCALE_ALIAS_ID = "locale_alias.locale_alias_id";
	public static final String LOCALE_NAME = "locale_alias.locale_name";
	
	private static final String sqlSelectClause = "select locale_alias.locale_alias_id, locale_alias.locale_name ";
	private static final String sqlFromClause = "from locale_alias ";
	private static final String sqlWhereClause = "where locale_alias.locale_id=?";
			
	 
	
	public LocaleAliasLookupQuery(Integer userId, List<SessionDivision> divisionList, Integer localeId) {
		super(sqlSelectClause, sqlFromClause, sqlWhereClause);
		this.logger = LogManager.getLogger(LocaleAliasLookupQuery.class);
		this.userId = userId;	
		super.setBaseFilterValue(Arrays.asList( new Object[] {localeId}));
	}

	

	@Override
	protected String makeOrderBy(SelectType selectType) {
		String orderBy = "";
		if ( selectType.equals(SelectType.DATA)) {
			if ( StringUtils.isBlank(sortBy)) {
				orderBy =  " order by locale_alias.locale_name";
			} else {
//				List<String> sortList = Arrays.asList(StringUtils.split(sortBy, ","));
				String sortDir = sortIsAscending ? orderBy + " asc " : orderBy + " desc ";
//				String sortBy = StringUtils.join(sortList, sortDir + ", ");
				orderBy = " order by " + sortBy + " " + sortDir;
			}
		}
		logger.log(Level.DEBUG, "Alias SortBy: " + sortBy);
		logger.log(Level.DEBUG, "Alias OrderBy: " + orderBy);
		return "\n" + orderBy;
	}


	

	

	@Override
	protected String makeWhereClause(String queryTerm) {
		String whereClause = sqlWhereClause;
		
		if ( ! StringUtils.isBlank(queryTerm) ) {
			StringBuffer searchFieldBuffer = new StringBuffer();
			searchFieldBuffer.append("\n lower(locale_alias.locale_name) like '%" + queryTerm.toLowerCase() + "%'");
			whereClause = StringUtils.isBlank(sqlWhereClause) ? searchFieldBuffer.toString() : sqlWhereClause + " and (" + searchFieldBuffer.toString() + ")";
		}
		
		return whereClause;
	}


	
	

}
