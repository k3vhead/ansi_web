package com.ansi.scilla.web.tax.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

import com.ansi.scilla.common.queries.SelectType;
import com.ansi.scilla.common.utils.WhereFieldLikeTransformer;
import com.ansi.scilla.web.common.query.LookupQuery;
import com.ansi.scilla.web.common.utils.ColumnFilter;
import com.ansi.scilla.web.common.utils.ColumnFilter.ComparisonType;

public class LocaleTaxRateLookupQuery extends LookupQuery {	
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
			"	parent.name as parent_name, parent.state_name as parent_state, parent.locale_type_id as parent_type_id,\n" +
			"	tax_rate_type.type_id, tax_rate_type.type_name\n"; 
			 
	public static final String sqlFromClause = 
			"from locale \n" + 
					"left outer join locale parent on parent.locale_id=locale.parent_locale_id\n" +
					"left outer join locale_tax_rate on locale_tax_rate.locale_id=locale.locale_id\n" + 
					"inner join tax_rate_type on tax_rate_type.type_id=locale_tax_rate.type_id\n";
	
	public static final String sqlWhereClause = " ";

	private Integer localeFilter;
	
	public LocaleTaxRateLookupQuery() {
		super(sqlSelectClause, sqlFromClause, sqlWhereClause);
		this.logger = LogManager.getLogger(this.getClass());
	}

	

	public Integer getLocaleFilter() {
		return localeFilter;
	}
	public void setLocaleFilter(Integer locageFilter) {
		this.localeFilter = locageFilter;
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
		
		List<ColumnFilter> filterList = new ArrayList<ColumnFilter>();	
		if ( this.localeFilter != null ) {
			filterList.add(new ColumnFilter(LOCALE_ID, this.localeFilter, ComparisonType.EQUAL_NUMBER));
		}
		super.setConstraintList(filterList);
		
		return whereClause;
	}

	
	public class ItemTransformer implements Transformer<HashMap<String, Object>, HashMap<String,Object>> {

		@Override
		public HashMap<String, Object> transform(HashMap<String, Object> arg0) {
			String parent = null;
			if ( arg0.containsKey("parent_name")) {
				String parentName = (String)arg0.get("parent_name");
				if ( ! StringUtils.isEmpty(parentName)) {
					parent = parentName + ", " + arg0.get("parent_state") + " (" + arg0.get("parent_type_id") + ")"; 
				}
			}
			arg0.put("parent", parent);
			return arg0;
		}
		
	}
}
