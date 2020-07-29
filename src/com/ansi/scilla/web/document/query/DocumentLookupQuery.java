package com.ansi.scilla.web.document.query;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

import com.ansi.scilla.common.document.DocumentType;
import com.ansi.scilla.common.queries.SelectType;
import com.ansi.scilla.web.common.query.LookupQuery;
import com.ansi.scilla.web.common.struts.SessionDivision;

public class DocumentLookupQuery extends LookupQuery {

	public static final String DOCUMENT_ID = "document.document_id";
	public static final String DESCRIPTION = "document.description";
	public static final String DOCUMENT_DATE = "document.document_date";
	public static final String EXPIRATION_DATE = "document.expiration_date";
	public static final String XREF_TYPE = "document.xref_type";
	public static final String XREF_ID = "document.xref_id";
	public static final String XREF_DISPLAY = "xref_display";

	
	private static final long serialVersionUID = 1L;

	
	private static final String sqlSelectClause = 
			"select "
			+ "\n    document.document_id, "
			+ "\n    document.description, "
			+ "\n    document.document_date, "
			+ "\n    document.expiration_date,"
			+ "\n    document.xref_type, "
			+ "\n    document.xref_id";
			
		

	private static final String sqlFromClause = 
			"\nfrom document";

	private static final String baseWhereClause = "\n  ";

	
	
	
	public DocumentLookupQuery(Integer userId, List<SessionDivision> divisionList) {
		super(makeSqlSelect(), makeSqlFrom(), baseWhereClause);		
		this.logger = LogManager.getLogger(this.getClass());
		this.userId = userId;		
	}

	public DocumentLookupQuery(Integer userId, List<SessionDivision> divisionList, String searchTerm) {
		this(userId, divisionList);
		this.searchTerm = searchTerm;
	}

	public DocumentLookupQuery(Integer userId, List<SessionDivision> divisionList, String xrefType, Integer xrefId) {
		super(makeSqlSelect(), makeSqlFrom(), makeSqlWhere(xrefType, xrefId));
		this.logger = LogManager.getLogger(this.getClass());
		this.userId = userId;
	}
	
	private static String makeSqlWhere(String xrefType, Integer xrefId) {
		return "where document.xref_type='" + xrefType + "' and document.xref_id=" + xrefId;
	}

	private static String makeSqlSelect() {
		List<String> sqlSelect = new ArrayList<String>();
		for ( DocumentType type : DocumentType.values()) {
			sqlSelect.add("when xref_type = '"+type.name()+"' then " + type.xrefDisplay());			
		}
		return sqlSelectClause + ",\ncase\n" + StringUtils.join(sqlSelect, "\n") + "\nend as xref_display";
	}

	private static String makeSqlFrom() {
		List<String> sqlFrom = new ArrayList<String>();
		for ( DocumentType type : DocumentType.values()) {
			sqlFrom.add("left outer join "+type.xrefTable()+" on document.xref_type='"+type.name()+"' and "+type.xrefTable() + "." + type.xrefKey()+"=document.xref_id");			
		}
		return sqlFromClause + "\n" + StringUtils.join(sqlFrom, "\n");
	}

	protected String makeOrderBy(SelectType selectType) {
		String orderBy = "";
		if ( selectType.equals(SelectType.DATA)) {
			if ( StringUtils.isBlank(sortBy)) {
				orderBy = " order by " + DOCUMENT_DATE + " desc, " + DESCRIPTION + " asc ";
			} else {
//				List<String> sortList = Arrays.asList(StringUtils.split(sortBy, ","));
				String sortDir = sortIsAscending ? orderBy + " asc " : orderBy + " desc ";
//				String sortBy = StringUtils.join(sortList, sortDir + ", ");
				orderBy = " order by " + sortBy + " " + sortDir;
			}
		}

		return "\n" + orderBy;
	}
	
	
	
	
	/**
	 * Return a where clause with the passed in queryTerm embedded
	 * 
	 * @param queryTerm
	 * @return 
	 * @throws Exception
	 */
	protected String makeWhereClause(String queryTerm)  {
		String whereClause = super.getBaseWhereClause();
		logger.log(Level.DEBUG, whereClause);
		String joiner = StringUtils.isBlank(baseWhereClause) ? " where " : " and ";
		logger.log(Level.DEBUG, joiner);
		if (! StringUtils.isBlank(queryTerm)) {
				whereClause =  whereClause + joiner + " (\n"
						+ " document.document_id like '%" + queryTerm.toLowerCase() + "%'" +
						"\n OR document.description like '%" + queryTerm.toLowerCase() + "%'" +
//	(filter)				"\n OR document.xref_type like '%" + queryTerm.toLowerCase() + "%'" +
						"\n OR document.xref_id like '%" + queryTerm.toLowerCase() + "%'" +
//	(filter)				"\n OR document.document_date like '%" + queryTerm.toLowerCase() + "%'" +
//	(filter)				"\n OR document.expiration_date like '%" + queryTerm.toLowerCase() + "%'" +
						")" ;
		}
		logger.log(Level.DEBUG, whereClause);
		return whereClause;
	}
	
	
}
