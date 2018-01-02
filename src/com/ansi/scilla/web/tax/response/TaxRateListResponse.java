package com.ansi.scilla.web.tax.response;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ansi.scilla.common.db.TaxRate;
import com.ansi.scilla.web.common.response.MessageResponse;
import com.thewebthing.commons.db2.RecordNotFoundException;

/** 
 * Used to return a list of "taxRate" objects to the client
 * 
 * @author gagroce
 *
 */
public class TaxRateListResponse extends MessageResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<TaxRateResponseItem> taxRateList;

	public TaxRateListResponse() {
		super();
	}
	/**
	 * create a list of all taxRate table records in the database
	 * 
	 * @param conn
	 * @throws Exception
	 */
	public TaxRateListResponse(Connection conn) throws Exception {
		this.taxRateList = new ArrayList<TaxRateResponseItem>();
		List<TaxRate> dbTaxRateList = TaxRate.cast(new TaxRate().selectAll(conn));
		for ( TaxRate record : dbTaxRateList ) {
			this.taxRateList.add(new TaxRateResponseItem(record));
			}
	}

	public TaxRateListResponse(Connection conn, Integer taxRateId) throws RecordNotFoundException, Exception {
		this.taxRateList = new ArrayList<TaxRateResponseItem>();
		TaxRate taxRate = new TaxRate();
		taxRate.setTaxRateId(taxRateId);	
		taxRate.selectOne(conn);
		this.taxRateList = Arrays.asList(new TaxRateResponseItem[] { new TaxRateResponseItem(taxRate) });
	}

	public List<TaxRateResponseItem> getTaxRateList() {
		return taxRateList;
	}

	public void setTaxRateList(List<TaxRateResponseItem> taxRateList) {
		this.taxRateList = taxRateList;
	}
	
	
}
