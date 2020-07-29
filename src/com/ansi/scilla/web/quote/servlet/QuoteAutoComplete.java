package com.ansi.scilla.web.quote.servlet;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;

import com.ansi.scilla.web.common.servlet.AbstractAutoCompleteItem;
import com.ansi.scilla.web.common.servlet.AbstractAutoCompleteServlet;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.common.utils.Permission;
import com.ansi.scilla.web.quote.response.QuoteAutoCompleteItem;

public class QuoteAutoComplete extends AbstractAutoCompleteServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public QuoteAutoComplete() {
		super(Permission.QUOTE_READ);
	}

	@Override
	protected List<AbstractAutoCompleteItem> makeResultList(Connection conn, SessionUser user, HashMap<String, String> parameterMap)
			throws Exception {
		String term = parameterMap.get("term").toLowerCase();
		String sql1 = "select quote.quote_id, concat(quote.quote_number, quote.revision) as quote_revision,\n" + 
				"	job_site.name as job_site_name, job_site.address1 as job_site_address, bill_to.name as bill_to_name, bill_to.address1 as bill_to_address\n" + 
				"from quote \n" + 
				"inner join address as job_site on job_site.address_id=quote.job_site_address_id\n" + 
				"inner join address as bill_to on bill_to.address_id=quote.bill_to_address_id\n" +
				"where ";
		String sql2 = " quote.quote_id=" + term + " OR ";
		String sql3 = " lower(concat(quote.quote_number, quote.revision)) like '%" + term + "%'" +
				" OR lower(job_site.name) like '%" + term + "%'" +
				" OR lower(job_site.address1) like '%" + term + "%'" +
				" OR lower(bill_to.name) like '%" + term + "%'" +
				" OR lower(bill_to.address1) like '%" + term + "%'" +
				" ORDER BY concat(quote.quote_number, quote.revision) " +
				" OFFSET 0 ROWS" +
				" FETCH NEXT 500 ROWS ONLY";
		String sql = StringUtils.isNumeric(term) ? sql1 + sql2 + sql3 : sql1 + sql3;
		logger.log(Level.DEBUG, sql);
		
		Statement s = conn.createStatement();
		ResultSet rs = s.executeQuery(sql);
		
		List<AbstractAutoCompleteItem> resultList = new ArrayList<AbstractAutoCompleteItem>();
		while ( rs.next() ) {			
			resultList.add(new QuoteAutoCompleteItem(rs));
		}
		rs.close();

		return resultList;
	}

}
