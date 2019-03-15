package com.ansi.scilla.web.address.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.common.db.Address;
import com.ansi.scilla.web.address.response.AddressResponseItem;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class AddressResponseQuery {

	public static final String JOBCONTACT_NAME = "jobcontact_name";
	public static final String SITECONTACT_NAME = "sitecontact_name";
	public static final String BILLTO_NAME = "billto_name";
	public static final String CONTRACTCONTACT_NAME = "contractcontact_name";
	public static final String BILLINGCONTACT_NAME = "billingcontact_name";
	
	private final String sql = "select address.*, " 
				+ "\n\t billto.name as " + BILLTO_NAME + ", " 
				+ "\n\t concat(jobcontact.first_name, ' ', jobcontact.last_name) as " + JOBCONTACT_NAME + ", " 
				+ "\n\t concat(sitecontact.first_name, ' ', sitecontact.last_name) as " + SITECONTACT_NAME + ", "
				+ "\n\t concat(contractcontact.first_name, ' ', contractcontact.last_name) as " + CONTRACTCONTACT_NAME + ", "
				+ "\n\t concat(billingcontact.first_name, ' ', billingcontact.last_name) as " + BILLINGCONTACT_NAME
				+ "\n from address  "
				+ "\n left outer join address billto on billto.address_id=address.jobsite_billto_address_default "
				+ "\n left outer join contact jobcontact on jobcontact.contact_id=address.jobsite_job_contact_default "
				+ "\n left outer join contact sitecontact on sitecontact.contact_id=address.jobsite_site_contact_default "
				+ "\n left outer join contact contractcontact on contractcontact.contact_id=address.billto_contract_contact_default"
				+ "\n left outer join contact billingcontact on billingcontact.contact_id=address.billto_billing_contact_default";
	
	private final String idFilter = "\n where address.address_id=? ";
	private final String sortPhrase = "\n order by address.name ";
	
	private Integer addressId;
	private PreparedStatement preparedStatement;
	private Logger logger;
	
	
	private AddressResponseQuery(Connection conn, Integer addressId) throws RecordNotFoundException, Exception {
		super();
		logger = LogManager.getLogger(this.getClass());
		String sql = this.sql + this.idFilter;
		logger.log(Level.DEBUG, sql);
		preparedStatement = conn.prepareStatement(sql);
		preparedStatement.setInt(1, addressId);		
	}
	
	private AddressResponseQuery(Connection conn) throws Exception {
		super();
		logger = LogManager.getLogger(this.getClass());
		preparedStatement = conn.prepareStatement(sql + sortPhrase);		
	}

	public Integer getAddressId() {
		return addressId;
	}

	public void setAddressId(Integer addressId) {
		this.addressId = addressId;
	}
	
	private static AddressResponseItem makeAddressResponseItem(ResultSet rs) throws SQLException {
		Logger logger = LogManager.getLogger(AddressResponseQuery.class);
		AddressResponseItem item = new AddressResponseItem();
		item.setAddress1(rs.getString(Address.ADDRESS1));
		item.setAddress2(rs.getString(Address.ADDRESS2));
		item.setAddressId(rs.getInt(Address.ADDRESS_ID));
		item.setCity(rs.getString(Address.CITY));
		item.setCounty(rs.getString(Address.COUNTY));
		item.setName(rs.getString(Address.NAME));
		item.setState(rs.getString(Address.STATE));
		item.setStatus(rs.getString(Address.STATUS));
		item.setZip(rs.getString(Address.ZIP));
		item.setCountryCode(rs.getString(Address.COUNTRY_CODE));
		item.setInvoiceStyleDefault(rs.getString(Address.INVOICE_STYLE_DEFAULT));
		item.setInvoiceGroupingDefault(rs.getString(Address.INVOICE_GROUPING_DEFAULT));
		item.setInvoiceBatchDefault(makeIntResponse(rs, Address.INVOICE_BATCH_DEFAULT));
		item.setInvoiceTermsDefault(rs.getString(Address.INVOICE_TERMS_DEFAULT));
		item.setOurVendorNbrDefault(rs.getString(Address.OUR_VENDOR_NBR_DEFAULT));
		item.setJobsiteBilltoAddressDefault(makeIntResponse(rs, Address.JOBSITE_BILLTO_ADDRESS_DEFAULT));
		item.setJobsiteJobContactDefault(makeIntResponse(rs, Address.JOBSITE_JOB_CONTACT_DEFAULT));
		item.setJobsiteSiteContactDefault(makeIntResponse(rs, Address.JOBSITE_SITE_CONTACT_DEFAULT));
		item.setJobsiteFloorsDefault(makeIntResponse(rs, Address.JOBSITE_FLOORS_DEFAULT));
		item.setJobsiteBuildingTypeDefault(rs.getString(Address.JOBSITE_BUILDING_TYPE_DEFAULT));
		item.setBilltoAccountTypeDefault(rs.getString(Address.BILLTO_ACCOUNT_TYPE_DEFAULT));
		item.setBilltoContractContactDefault(makeIntResponse(rs, Address.BILLTO_CONTRACT_CONTACT_DEFAULT));
		item.setBilltoBillingContactDefault(makeIntResponse(rs, Address.BILLTO_BILLING_CONTACT_DEFAULT));
		item.setBilltoTaxExempt(makeIntResponse(rs, Address.BILLTO_TAX_EXEMPT));
		item.setBilltoTaxExemptReason(rs.getString(Address.BILLTO_TAX_EXEMPT_REASON));
		
		item.setJobsiteBillToName(rs.getString(BILLTO_NAME));
		item.setJobsiteJobContactName(rs.getString(JOBCONTACT_NAME));
		item.setJobsiteSiteContactName(rs.getString(SITECONTACT_NAME));
		item.setJobsiteContractContactName(rs.getString(CONTRACTCONTACT_NAME));
		item.setJobsiteBillingContactName(rs.getString(BILLINGCONTACT_NAME));
		
		logger.debug(item);
		return item;
	}

	private static Integer makeIntResponse(ResultSet rs, String columnName) throws SQLException {	
		Integer value = null;
		Object o = rs.getObject(columnName);		
		if ( o != null ) {
			value = (Integer)o;
		}
		return value;
	}

	public static AddressResponseItem selectOne(Connection conn, Integer addressId) throws RecordNotFoundException, Exception {
		AddressResponseItem item = null;
		AddressResponseQuery query = new AddressResponseQuery(conn, addressId);
		ResultSet rs = query.preparedStatement.executeQuery();
		if ( rs.next() ) {
			item = makeAddressResponseItem(rs);
		} else {
			throw new RecordNotFoundException(String.valueOf(addressId));
		}
		rs.close();
		return item;
	}
	
	public static List<AddressResponseItem> select(Connection conn) throws Exception {
		List<AddressResponseItem> itemList = new ArrayList<AddressResponseItem>();
		AddressResponseQuery query = new AddressResponseQuery(conn);
		ResultSet rs = query.preparedStatement.executeQuery();
		while ( rs.next() ) {
			itemList.add(makeAddressResponseItem(rs));
		}
		rs.close();
		return itemList;
	}
	
	
}
