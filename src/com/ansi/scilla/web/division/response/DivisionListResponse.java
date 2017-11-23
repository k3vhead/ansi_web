package com.ansi.scilla.web.division.response;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.common.queries.DivisionUserCount;
import com.ansi.scilla.web.common.response.MessageResponse;

/** 
 * Used to return a list of "code" objects to the client
 * 
 * @author dclewis
 *
 */
public class DivisionListResponse extends MessageResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<DivisionCountRecord> divisionList;

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
		List<DivisionUserCount> divisionCountList = DivisionUserCount.select(conn, new String[] {Division.DIVISION_CODE});
		this.divisionList = new ArrayList<DivisionCountRecord>();
		for ( DivisionUserCount record : divisionCountList ) {
			this.divisionList.add(new DivisionCountRecord(record));
		}
		Collections.sort(this.divisionList);
	}
	
	public DivisionListResponse(Connection conn, Integer divisionId) throws Exception {
		DivisionUserCount divisionUserCount = DivisionUserCount.select(conn, divisionId);
		DivisionCountRecord record = new DivisionCountRecord(divisionUserCount);
		this.divisionList = Arrays.asList(new DivisionCountRecord[] { record });
	}

	public List<DivisionCountRecord> getDivisionList() {
		return divisionList;
	}

	public void setDivisionList(List<DivisionCountRecord> divisionList) {
		this.divisionList = divisionList;
	}
	
	
	
}
