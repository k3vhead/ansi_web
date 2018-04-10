package com.ansi.scilla.web.address.response;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.PropertyUtils;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.db.Address;


public class AddressResponseItem extends ApplicationObject implements Comparable<AddressResponseItem> {

	private static final long serialVersionUID = 1L;


	private String address1;
	private String address2;
	private Integer addressId;
	private String city;
	private String county;
	private String name;
	private String state;
	private String status;
//	private Integer updatedBy;
//	private Date updatedDate;
	private String zip;
	private String countryCode;
	private String invoiceStyleDefault;
	private String invoiceGroupingDefault;
	private Integer invoiceBatchDefault;
	private String invoiceTermsDefault;
	private String ourVendorNbrDefault;

	
	public AddressResponseItem(Address address) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		super();
		PropertyUtils.copyProperties(this, address);
	}
	

	public void setAddress1(String address1) {
		this.address1 = address1;
	}


	public String getAddress1() {
		return this.address1;
	}


	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	
	public String getAddress2() {
		return this.address2;
	}


	public void setAddressId(Integer addressId) {
		this.addressId = addressId;
	}


	public Integer getAddressId() {
		return this.addressId;
	}


	public void setCity(String city) {
		this.city = city;
	}


	public String getCity() {
		return this.city;
	}


	public void setCounty(String county) {
		this.county = county;
	}


	public String getCounty() {
		return this.county;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getName() {
		return this.name;
	}


	public void setState(String state) {
		this.state = state;
	}


	public String getState() {
		return this.state;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public String getStatus() {
		return this.status;
	}


	public void setZip(String zip) {
		this.zip = zip;
	}


	public String getZip() {
		return this.zip;
	}
	
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}


	public String getCountryCode() {
		return this.countryCode;
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


	public Integer getInvoiceBatchDefault() {
		return invoiceBatchDefault;
	}


	public void setInvoiceBatchDefault(Integer invoiceBatchDefault) {
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


	@Override
	public int compareTo(AddressResponseItem o) {
		int ret = this.getAddressId().compareTo(o.getAddressId());

		return ret;
	}



	
}
