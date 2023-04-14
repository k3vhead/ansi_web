package com.ansi.scilla.web.address.query;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;

import com.ansi.scilla.common.db.Address;
import com.ansi.scilla.common.queries.SelectType;
import com.ansi.scilla.web.common.query.LookupQuery;
import com.ansi.scilla.web.common.struts.SessionDivision;

public class AddressLookupQuery extends LookupQuery {
	private static final long serialVersionUID = 1L;

	private static final String sqlSelectClause = 
			"select a.address_id, a.name, a.address_status, a.address1, a.address2, a.city, a.county, a.state, a.zip, \n" + 
			"	a.country_code, (a3.jobCount + a3.billCount) as count, \n" + 
			"	a.invoice_style_default, a.invoice_grouping_default, \n" + 
			"	a.invoice_batch_default, a.invoice_terms_default, a.our_vendor_nbr_default, count(document.document_id) as document_count\n";
	
	private static final String sqlFromClause =
			" from address a\n" + 
			" left outer join document on document.xref_id=a.address_id and document.xref_type='TAX_EXEMPT'\n" + 
			" left join (select a2.address_id, count(q1.job_site_address_id) as jobCount, count(q1.bill_to_address_id) as billCount from address a2\n" + 
			" inner join quote q1 on (a2.address_id = q1.job_site_address_id or a2.address_id = q1.bill_to_address_id) group by a2.address_id ) a3 on a.address_id = a3.address_id\n"; 
			

	private static final String groupByClause = "group by a.address_id, a.name, a.address_status, a.address1, a.address2, a.city, a.county, a.state, a.zip, \n" + 
			"	a.country_code, (a3.jobCount + a3.billCount), \n" + 
			"	a.invoice_style_default, a.invoice_grouping_default, \n" + 
			"	a.invoice_batch_default, a.invoice_terms_default, a.our_vendor_nbr_default";
	
	private final String[] searchableFields = new String[] {
			"a.address_id",
			"a.name",
			"a.address1",
			"a.address2",
			"a.city",
			"a.county",
			"a.state",
			"a.zip",
			"a.country_code",	
	};
	
	public AddressLookupQuery(Integer userId, List<SessionDivision> divisionList) throws Exception {
		super(sqlSelectClause, sqlFromClause, "");	
		super.setGroupByClause(groupByClause);
		this.logger = LogManager.getLogger(this.getClass());
		this.userId = userId;		
	}

	public AddressLookupQuery(Integer userId, List<SessionDivision> divisionList, String searchTerm) throws Exception {
		this(userId, divisionList);
		this.searchTerm = searchTerm;
	}
	
	
	@Override
	protected String makeOrderBy(SelectType selectType) {
		String orderBy = "";
		if ( selectType.equals(SelectType.DATA)) {
			if ( StringUtils.isBlank(sortBy)) {				
				orderBy = " order by " + Address.NAME + " asc";
			} else {
				String sortDir = sortIsAscending ? orderBy + " asc " : orderBy + " desc ";
				orderBy = " order by " + sortBy + " " + sortDir;
			}
		}

		return "\n" + orderBy;
	}

	@Override
	protected String makeWhereClause(String queryTerm) {
		String filterPhrase = "";
		if ( ! StringUtils.isBlank(queryTerm) ) {
			List<String> likeList = new ArrayList<String>();
			for ( String field : this.searchableFields ) {
				likeList.add("lower(" + field + ") like '%" + queryTerm.toLowerCase() + "%'");
			}
			filterPhrase = "(\n" + StringUtils.join(likeList, "\n or ") + ")";
		}
		
		String baseWhereClause = super.getBaseWhereClause();
		String whereClause = "";
		if ( StringUtils.isBlank(baseWhereClause) ) {
			if ( StringUtils.isBlank(filterPhrase) ) {
				whereClause = "";
			} else {
				whereClause = "where " + filterPhrase;
			}
		} else {
			if ( StringUtils.isBlank(filterPhrase) ) {
				whereClause = baseWhereClause;
			} else {
				whereClause = baseWhereClause + " and " + filterPhrase;
			}
		}
		return whereClause;
	}

	

}
