package com.ansi.scilla.web.division.common;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.common.ApplicationObject;


/**
 * Use this object to collect the information necessary to populate a division selector drop-down in the UI.
 * 
 * @author dclewis
 *
 */
public class DivisionSelector extends ApplicationObject {

	private static final long serialVersionUID = 1L;
	
	private static DivisionSelector instance;
	
	private List<DivisionSelectorItem> divisionList;
	
	private DivisionSelector(Connection conn) throws SQLException {
		super();
		Statement s = conn.createStatement();
		ResultSet rs = s.executeQuery("select division_id, concat(division_nbr, '-', division_code) as div, division_status from division order by div" );
		this.divisionList = new ArrayList<DivisionSelectorItem>();
		while ( rs.next() ) { 
			divisionList.add(new DivisionSelectorItem(rs));
		}
		rs.close();
	}

	
	public List<DivisionSelectorItem> getDivisionList() {
		return divisionList;
	}
	
	public String makeHtml(boolean activeOnly) {
		List<String> options = new ArrayList<String>();
		for ( DivisionSelectorItem item : divisionList ) {
			if ( activeOnly == false || item.getActive() == true ) {
				options.add(item.makeHtml());
			}
		}
		return StringUtils.join(options, "\n");
	}

	
	public static DivisionSelector getInstance(Connection conn) throws SQLException {
		if ( instance == null ) {
			// this makes this object thread-safe, more efficiently than "public static synchronized DivisionSelector ..."
			synchronized (DivisionSelector.class) { 
				instance = new DivisionSelector(conn);
			}
		}
		return instance;
	}
	
	
}
