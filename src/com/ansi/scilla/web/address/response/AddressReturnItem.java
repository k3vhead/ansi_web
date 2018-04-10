package com.ansi.scilla.web.address.response;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.ansi.scilla.common.ApplicationObject;

public class AddressReturnItem extends ApplicationObject {
		private static final long serialVersionUID = 1L;
		private Integer address_id;
		private String name;
		private String address_status;
		private String address1;
		private String address2;
		private String city;
		private String county;
		private String state;
		private String zip;
		private String countryCode;
		private String DT_RowId;
		private int count;
		private String invoiceStyleDefault;
		private String invoiceGroupingDefault;
		private int invoiceBatchDefault;
		private String invoiceTermsDefault;
		private String ourVendorNbrDefault;
		
		
		public AddressReturnItem(ResultSet rs) throws SQLException {
			super();
			this.address_id = rs.getInt("address_id");
			this.name = rs.getString("name");
			this.address_status = rs.getString("address_status");
			this.address1 = rs.getString("address1");
			this.address2 = rs.getString("address2");
			this.county = rs.getString("county");
			this.city = rs.getString("city");
			this.state = rs.getString("state");
			this.zip = rs.getString("zip");
			this.countryCode = rs.getString("country_code");
			this.DT_RowId = rs.getInt("address_id")+"";
			this.count = rs.getInt("count");
			
			this.invoiceStyleDefault = rs.getString("invoice_style_default");
			this.invoiceGroupingDefault = rs.getString("invoice_grouping_default");
			this.invoiceBatchDefault = rs.getInt("invoice_batch_default");
			this.invoiceTermsDefault = rs.getString("invoice_terms_default");
			this.ourVendorNbrDefault = rs.getString("our_vendor_nbr_default");

		}

		public Integer getAddressId() {
			return address_id;
		}

		public void setAddressId(Integer address_id) {
			this.address_id = address_id;
		}
		
		public String getDT_RowId() {
			return DT_RowId;
		}

		public void setDT_RowId(String DT_RowId) {
			this.DT_RowId = DT_RowId;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getStatus() {
			return address_status;
		}

		public void setStatus(String address_status) {
			this.address_status = address_status;
		}

		public String getAddress1() {
			return address1;
		}

		public void setAddress1(String address1) {
			this.address1 = address1;
		}

		public String getAddress2() {
			return address2;
		}

		public void setAddress2(String address2) {
			this.address2 = address2;
		}

		public String getCity() {
			return city;
		}

		public void setCity(String city) {
			this.city = city;
		}
		
		public String getCounty() {
			return county;
		}

		public void setCounty(String county) {
			this.county = county;
		}

		public String getState() {
			return state;
		}

		public void setState(String state) {
			this.state = state;
		}
		
		public int getCount() {
			return count;
		}

		public void setCount(int count) {
			this.count = count;
		}
		
		public String getZip() {
			return zip;
		}

		public void setZip(String zip) {
			this.zip = zip;
		}
		public String getCountryCode() {
			return countryCode;
		}

		public void setCountryCode(String countryCode) {
			this.countryCode = countryCode;
		}

		public Integer getAddress_id() {
			return address_id;
		}

		public void setAddress_id(Integer address_id) {
			this.address_id = address_id;
		}

		public String getAddress_status() {
			return address_status;
		}

		public void setAddress_status(String address_status) {
			this.address_status = address_status;
		}

		public String getInvoiceStyleDefault() {
			return invoiceStyleDefault;
		}

		public void setInvoiceStyleDefault(String invoiceStyleDefault) {
			this.invoiceStyleDefault = invoiceStyleDefault;
		}

		public String getInvoiceGroupingDefault() {
			return invoiceGroupingDefault;
		}

		public void setInvoiceGroupingDefault(String invoiceGroupingDefault) {
			this.invoiceGroupingDefault = invoiceGroupingDefault;
		}

		public int getInvoiceBatchDefault() {
			return invoiceBatchDefault;
		}

		public void setInvoiceBatchDefault(int invoiceBatchDefault) {
			this.invoiceBatchDefault = invoiceBatchDefault;
		}

		public String getInvoiceTermsDefault() {
			return invoiceTermsDefault;
		}

		public void setInvoiceTermsDefault(String invoiceTermsDefault) {
			this.invoiceTermsDefault = invoiceTermsDefault;
		}

		public String getOurVendorNbrDefault() {
			return ourVendorNbrDefault;
		}

		public void setOurVendorNbrDefault(String ourVendorNbrDefault) {
			this.ourVendorNbrDefault = ourVendorNbrDefault;
		}

	}