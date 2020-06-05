package com.ansi.scilla.web.quote.response;

import java.sql.ResultSet;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.web.common.servlet.AbstractAutoCompleteItem;

public class QuoteAutoCompleteItem extends AbstractAutoCompleteItem {

	private static final long serialVersionUID = 1L;
	
	public static final String QUOTE_ID = "quote_id";
	public static final String QUOTE_REVISION = "quote_revision";
	public static final String JOB_SITE_NAME = "job_site_name";
	public static final String JOB_SITE_ADDRESS = "job_site_address";
	public static final String BILL_TO_NAME = "bill_to_name";
	public static final String BILL_TO_ADDRESS = "bill_to_address";
	
	
	public QuoteAutoCompleteItem(ResultSet rs) throws Exception {
		super(rs);
	}

	@Override
	protected void make(ResultSet rs) throws Exception {
		this.id = rs.getInt(QUOTE_ID);
		this.value = rs.getString(QUOTE_REVISION);
		
		List<String> displayList = Arrays.asList(new String[] {
			String.valueOf(rs.getInt(QUOTE_ID)),
			rs.getString(QUOTE_REVISION),
			rs.getString(JOB_SITE_NAME),
			rs.getString(JOB_SITE_ADDRESS),
			rs.getString(BILL_TO_NAME),
			rs.getString(BILL_TO_ADDRESS)
		});
		
		this.label = StringUtils.join(displayList,"|");
	}

	

	
}
