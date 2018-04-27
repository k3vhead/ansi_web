package com.ansi.scilla.web.address;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Date;

import com.ansi.scilla.common.db.Address;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class AddressDisplayQuery {

	public static final String ADDRESS = "address";
	public static final String JOBCONTACT_NAME = "jobcontact_name";
	public static final String SITECONTACT_NAME = "sitecontact_name";
	public static final String BILLTO_NAME = "billto_name";
	
	private final String sql = "select address.*, " 
				+ "\n\t billto.name as billto_name,  "
				+ "\n\t concat(jobcontact.first_name, ' ', jobcontact.last_name) as jobcontact_name, " 
				+ "\n\t concat(sitecontact.first_name, ' ', sitecontact.last_name) as sitecontact_name "
				+ "\n from address  "
				+ "\n left outer join address billto on billto.address_id=address.jobsite_billto_address_default "
				+ "\n left outer join contact jobcontact on jobcontact.contact_id=address.jobsite_job_contact_default "
				+ "\n left outer join contact sitecontact on sitecontact.contact_id=address.jobsite_site_contact_default "
				+ "\n where address.address_id=?";
	
	private Address address;
	private String jobContactName;
	private String siteContactName;
	private String billToName;
	
	public AddressDisplayQuery(Connection conn, Integer addressId) throws RecordNotFoundException, Exception {
		super();
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setInt(1, addressId);
		ResultSet rs = ps.executeQuery();
		if ( rs.next() ) {
			makeAddressDisplay(rs);
		} else {
			throw new RecordNotFoundException();
		}
		rs.close();
	}
	
	private void makeAddressDisplay(ResultSet rs) throws Exception {
		ResultSetMetaData rsmd = rs.getMetaData();
//		******   you can't pass audit fields here *****
//		******   create a query that will do a list with an option filter on address id
//		******   create an item object
		Address address = (Address)new Address().rs2Object(rsmd, rs);  
		this.address = address;
		this.jobContactName = rs.getString(JOBCONTACT_NAME);
		this.siteContactName = rs.getString(SITECONTACT_NAME);
		this.billToName = rs.getString(BILLTO_NAME);
		
	}

	public Address getAddress() {
		return address;
	}
	public void setAddress(Address address) {
		this.address = address;
	}
	public String getJobContactName() {
		return jobContactName;
	}
	public void setJobContactName(String jobContactName) {
		this.jobContactName = jobContactName;
	}
	public String getSiteContactName() {
		return siteContactName;
	}
	public void setSiteContactName(String siteContactName) {
		this.siteContactName = siteContactName;
	}
	public String getBillToName() {
		return billToName;
	}
	public void setBillToName(String billToName) {
		this.billToName = billToName;
	}
	
	
}
