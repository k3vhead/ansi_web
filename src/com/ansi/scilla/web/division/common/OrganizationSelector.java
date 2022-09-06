package com.ansi.scilla.web.division.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.organization.OrganizationType;


/**
 * Use this object to collect the information necessary to populate a division group (ie, Company, Region, etc) selector drop-down in the UI.
 * 
 * @author dclewis
 *
 */
public abstract class OrganizationSelector extends ApplicationObject {

	private static final long serialVersionUID = 1L;
	
	private List<DivisionGroupSelectorItem> groupList;
	
	protected OrganizationSelector(Connection conn, OrganizationType type) throws SQLException {
		super();
		PreparedStatement s = conn.prepareStatement("select group_id, company_code, name, group_status\n" + 
				"from division_group\n" + 
				"where group_type=?\n" + 
				"order by company_code");
		s.setString(1, type.name());
		ResultSet rs = s.executeQuery();
		this.groupList = new ArrayList<DivisionGroupSelectorItem>();
		while ( rs.next() ) { 
			groupList.add(new DivisionGroupSelectorItem(rs));
		}
		rs.close();
	}

	public List<DivisionGroupSelectorItem> getGroupList() {
		return groupList;
	}


	public String makeHtml(boolean activeOnly) {
		List<String> options = new ArrayList<String>();
		for ( DivisionGroupSelectorItem item : groupList ) {
			if ( activeOnly == false || item.getActive() == true ) {
				options.add(item.makeHtml());
			}
		}
		return StringUtils.join(options, "\n");
	}
	
	
}
