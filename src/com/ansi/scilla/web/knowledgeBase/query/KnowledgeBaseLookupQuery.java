package com.ansi.scilla.web.knowledgeBase.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

import com.ansi.scilla.common.db.KnowledgeBase;
import com.ansi.scilla.common.queries.SelectType;
import com.ansi.scilla.common.utils.WhereFieldLikeTransformer;
import com.ansi.scilla.web.common.query.LookupQuery;
import com.ansi.scilla.web.common.struts.SessionDivision;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.ColumnFilter;

public class KnowledgeBaseLookupQuery extends LookupQuery {

	private static final long serialVersionUID = 1L;

	public static final String KB_KEY = AppUtils.makeTableName(KnowledgeBase.class) + "." + KnowledgeBase.KB_KEY;
	public static final String KB_LANGUAGE = AppUtils.makeTableName(KnowledgeBase.class) + "." + KnowledgeBase.KB_LANGUAGE;
	public static final String KB_TITLE = AppUtils.makeTableName(KnowledgeBase.class) + "." + KnowledgeBase.KB_TITLE;
	public static final String KB_CONTENT = AppUtils.makeTableName(KnowledgeBase.class) + "." + KnowledgeBase.KB_CONTENT;
	public static final String KB_STATUS = AppUtils.makeTableName(KnowledgeBase.class) + "." + KnowledgeBase.KB_STATUS;
	
	
	public KnowledgeBaseLookupQuery(Integer userId, List<SessionDivision> divisionList) {
		super(makeBaseSelect(), makeBaseFrom(), makeBaseWhere());
		this.logger = LogManager.getLogger(this.getClass());
		this.userId = userId;
	}
	
		
	private static String makeBaseSelect() {
		String[] fieldNames = new String[] {
				KB_KEY,
				KB_LANGUAGE,
				KB_TITLE,
				KB_CONTENT,
				KB_STATUS
		};
		return "select " + StringUtils.join(fieldNames, ",") + "\n";
	}


	private static String makeBaseFrom() {
		return "from " + AppUtils.makeTableName(KnowledgeBase.class) + "\n";
	}


	private static String makeBaseWhere() {
		 return "";
	}


	@Override
	protected String makeOrderBy(SelectType selectType) {
		String orderBy = "";
		if ( selectType.equals(SelectType.DATA)) {
			if ( StringUtils.isBlank(sortBy)) {
				orderBy = " order by " + KB_KEY + ", " + KB_LANGUAGE;
			} else {
				String sortDir = sortIsAscending ? orderBy + " asc " : orderBy + " desc ";
				orderBy = " order by " + sortBy + " " + sortDir;
			}
		}

		return "\n" + orderBy;
	}

	@Override
	protected String makeWhereClause(String queryTerm) {
		logger.log(Level.DEBUG, "makeWhereClause");
		String whereClause = makeBaseWhere();
		String joiner = StringUtils.isBlank(whereClause) ? " where " : " and ";
		
		
		if (! StringUtils.isBlank(queryTerm)) {
			String searchTerm = queryTerm.toLowerCase();
			Collection<String> whereFields= Arrays.asList(new String[] {
					KB_KEY,
					KB_LANGUAGE,
					KB_TITLE,
					KB_CONTENT,
			});
			
			Collection<?> whereClauseList = CollectionUtils.collect(whereFields, new WhereFieldLikeTransformer(searchTerm));
			whereClause = whereClause + joiner +  "(" + StringUtils.join(whereClauseList, " \n\tOR ") + ")";
		}

		List<ColumnFilter> filterList = new ArrayList<ColumnFilter>();		
		super.setConstraintList(filterList);

		return whereClause;
	}

	

	
	
}
