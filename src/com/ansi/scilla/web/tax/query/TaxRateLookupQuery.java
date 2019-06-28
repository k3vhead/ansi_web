package com.ansi.scilla.web.tax.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

import com.ansi.scilla.common.queries.SelectType;
import com.ansi.scilla.common.utils.WhereFieldLikeTransformer;
import com.ansi.scilla.web.common.query.LookupQuery;

public class TaxRateLookupQuery extends LookupQuery {	
	private static final long serialVersionUID = 1L;

	public static final String LOCALE_ID = "locale.locale_id";
	public static final String STATE_NAME = "locale.state_name";
	public static final String NAME = "locale.name";	
	public static final String LOCALE_TYPE_ID = "locale.locale_type_id";	
	public static final String EFFECTIVE_DATE = "locale_tax_rate.effective_date";	
	public static final String RATE_VALUE = "locale_tax_rate.rate_value";	
	public static final String TYPE_ID = "locale_tax_rate.type_id";
	public static final String TYPE_NAME = "type_name";
			
			
	public static final String sqlSelectClause = 
			"select\n" + 
			"	locale.locale_id, locale.state_name, locale.name, locale.locale_type_id,\n" + 
			"	locale_tax_rate.effective_date, locale_tax_rate.rate_value,\n" + 
			"	tax_rate_type.type_id, tax_rate_type.type_name\n"; 
			 
	public static final String sqlFromClause = 
			"from locale \n" + 
					"left outer join locale_tax_rate on locale_tax_rate.locale_id=locale.locale_id\n" + 
					"inner join tax_rate_type on tax_rate_type.type_id=locale_tax_rate.type_id\n";
	
	public static final String sqlWhereClause = " ";


	public TaxRateLookupQuery() {
		super(sqlSelectClause, sqlFromClause, sqlWhereClause);
		this.logger = LogManager.getLogger(this.getClass());
	}


	@Override
	protected String makeOrderBy(SelectType selectType) {
		String orderBy = "";
		if ( selectType.equals(SelectType.DATA)) {
			if ( StringUtils.isBlank(sortBy)) {
				orderBy = "order by " + STATE_NAME + " asc, " + NAME + " asc, " + EFFECTIVE_DATE + " desc";
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
					STATE_NAME,
					NAME,
					TYPE_NAME
			});
			Collection<String> numericFields = Arrays.asList(new String[] {
					RATE_VALUE,
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

}
