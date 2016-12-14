package com.ansi.scilla.web.response.division;

import java.io.Serializable;
import java.sql.Connection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.web.response.MessageResponse;

/** 
 * Used to return a list of "code" objects to the client
 * 
 * @author dclewis
 *
 */
public class DivisionListResponse implements MessageResponse, Serializable {

	private static final long serialVersionUID = 1L;

	private List<Division> divisionList;

	public DivisionListResponse() {
		super();
	}
	/**
	 * create a list of all code table records in the database, sorted by
	 * table, field, display value
	 * 
	 * @param conn
	 * @throws Exception
	 */
	public DivisionListResponse(Connection conn) throws Exception {
		this.divisionList = Division.cast(new Division().selectAll(conn));
		Collections.sort(divisionList,

				new Comparator<Division>() {

			public int compare(Division o1, Division o2) {

				int ret = o1.getName().compareTo(o2.getName());
				if ( ret == 0 ) {
					ret = o1.getName().compareTo(o2.getName());
				}
				
				return ret;

			}

		});
	}
	public List<Division> getDivisionList() {
		return divisionList;
	}

	public void setDivisionList(List<Division> divisionList) {
		this.divisionList = divisionList;
	}
	
	
}
