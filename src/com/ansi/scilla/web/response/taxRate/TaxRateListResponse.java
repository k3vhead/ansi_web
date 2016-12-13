package com.ansi.scilla.web.response.taxRate;

import java.io.Serializable;
import java.sql.Connection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.ansi.scilla.common.db.TaxRate;
import com.ansi.scilla.web.response.MessageResponse;

/** 
 * Used to return a list of "taxRate" objects to the client
 * 
 * @author gagroce
 *
 */
public class TaxRateListResponse extends MessageResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<TaxRate> taxRateList;

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
		this.taxRateList = TaxRate.cast(new TaxRate().selectAll(conn));
		Collections.sort(taxRateList,

				new Comparator<TaxRate>() {

			public int compare(TaxRate o1, TaxRate o2) {

				int ret = o1.getTableName().compareTo(o2.getTableName());
				if ( ret == 0 ) {
					ret = o1.getFieldName().compareTo(o2.getFieldName());
				}
				if ( ret == 0 ) {
					ret = o1.getSeq().compareTo(o2.getSeq());
				}
				if ( ret == 0 ) {
					ret = o1.getDisplayValue().compareTo(o2.getDisplayValue());
				}
				return ret;

			}

		});
	}
	public List<TaxRate> getTaxRateList() {
		return taxRateList;
	}

	public void setTaxRateList(List<TaxRate> taxRateList) {
		this.taxRateList = taxRateList;
	}
	
	
}
