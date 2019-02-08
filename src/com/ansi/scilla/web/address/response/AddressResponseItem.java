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
	private Integer jobsiteBilltoAddressDefault;
	private String jobsiteBillToName;
	private Integer jobsiteJobContactDefault;
	private String jobsiteJobContactName;
	private Integer jobsiteSiteContactDefault;
	private String jobsiteSiteContactName;
	private Integer jobsiteFloorsDefault;
	private String jobsiteBuildingTypeDefault;
	private String billtoAccountTypeDefault;
	private Integer billtoContractContactDefault;
	private Integer billtoBillingContactDefault;
	private Integer billtoTaxExempt;
	private String billtoTaxExemptReason;

	
	public AddressResponseItem() {
		super();
	}
	
	/**
	 * Create an item to be included in the AddressListResponse, based on an address record. 
	 * Deprecated because the response needs to have display values for jobsite/billing/invoice default address
	 * and contact id's. Use AddressResponseQuery to completely populate this record.
	 * 
	 * @param address
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	@Deprecated
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
	public Integer getJobsiteBilltoAddressDefault() {
		return jobsiteBilltoAddressDefault;
	}
	public void setJobsiteBilltoAddressDefault(Integer jobsiteBilltoAddressDefault) {
		this.jobsiteBilltoAddressDefault = jobsiteBilltoAddressDefault;
	}
	public Integer getJobsiteJobContactDefault() {
		return jobsiteJobContactDefault;
	}
	public void setJobsiteJobContactDefault(Integer jobsiteJobContactDefault) {
		this.jobsiteJobContactDefault = jobsiteJobContactDefault;
	}
	public Integer getJobsiteSiteContactDefault() {
		return jobsiteSiteContactDefault;
	}
	public void setJobsiteSiteContactDefault(Integer jobsiteSiteContactDefault) {
		this.jobsiteSiteContactDefault = jobsiteSiteContactDefault;
	}
	public Integer getJobsiteFloorsDefault() {
		return jobsiteFloorsDefault;
	}
	public void setJobsiteFloorsDefault(Integer jobsiteFloorsDefault) {
		this.jobsiteFloorsDefault = jobsiteFloorsDefault;
	}
	public String getJobsiteBuildingTypeDefault() {
		return jobsiteBuildingTypeDefault;
	}
	public void setJobsiteBuildingTypeDefault(String jobsiteBuildingTypeDefault) {
		this.jobsiteBuildingTypeDefault = jobsiteBuildingTypeDefault;
	}
	public String getBilltoAccountTypeDefault() {
		return billtoAccountTypeDefault;
	}
	public void setBilltoAccountTypeDefault(String billtoAccountTypeDefault) {
		this.billtoAccountTypeDefault = billtoAccountTypeDefault;
	}
	public Integer getBilltoContractContactDefault() {
		return billtoContractContactDefault;
	}
	public void setBilltoContractContactDefault(Integer billtoContractContactDefault) {
		this.billtoContractContactDefault = billtoContractContactDefault;
	}
	public Integer getBilltoBillingContactDefault() {
		return billtoBillingContactDefault;
	}
	public void setBilltoBillingContactDefault(Integer billtoBillingContactDefault) {
		this.billtoBillingContactDefault = billtoBillingContactDefault;
	}
	public Integer getBilltoTaxExempt() {
		return billtoTaxExempt;
	}
	public void setBilltoTaxExempt(Integer billtoTaxExempt) {
		this.billtoTaxExempt = billtoTaxExempt;
	}
	public String getBilltoTaxExemptReason() {
		return billtoTaxExemptReason;
	}
	public void setBilltoTaxExemptReason(String billtoTaxExemptReason) {
		this.billtoTaxExemptReason = billtoTaxExemptReason;
	}

	public String getJobsiteBillToName() {
		return jobsiteBillToName;
	}

	public void setJobsiteBillToName(String jobsiteBillToName) {
		this.jobsiteBillToName = jobsiteBillToName;
	}

	public String getJobsiteJobContactName() {
		return jobsiteJobContactName;
	}

	public void setJobsiteJobContactName(String jobsiteJobContactName) {
		this.jobsiteJobContactName = jobsiteJobContactName;
	}

	public String getJobsiteSiteContactName() {
		return jobsiteSiteContactName;
	}

	public void setJobsiteSiteContactName(String jobsiteSiteContactName) {
		this.jobsiteSiteContactName = jobsiteSiteContactName;
	}

	@Override
	public int compareTo(AddressResponseItem o) {
		int ret = this.getAddressId().compareTo(o.getAddressId());

		return ret;
	}

	@Override
	public AddressResponseItem clone() throws CloneNotSupportedException {
		AddressResponseItem clone = new AddressResponseItem();
		
		clone.setAddress1(this.address1);
		clone.setAddress2(address2);
		clone.setAddressId(addressId);
		clone.setCity(city);
		clone.setCounty(county);
		clone.setName(name);
		clone.setState(state);
		clone.setStatus(status);
		clone.setZip(zip);
		clone.setCountryCode(countryCode);
		clone.setInvoiceStyleDefault(invoiceStyleDefault);
		clone.setInvoiceGroupingDefault(invoiceGroupingDefault);
		clone.setInvoiceBatchDefault(invoiceBatchDefault);
		clone.setInvoiceTermsDefault(invoiceTermsDefault);
		clone.setOurVendorNbrDefault(ourVendorNbrDefault);
		clone.setJobsiteBilltoAddressDefault(jobsiteBilltoAddressDefault);
		clone.setJobsiteBillToName(jobsiteBillToName);
		clone.setJobsiteJobContactDefault(jobsiteJobContactDefault);
		clone.setJobsiteJobContactName(jobsiteJobContactName);
		clone.setJobsiteSiteContactDefault(jobsiteSiteContactDefault);
		clone.setJobsiteSiteContactName(jobsiteSiteContactName);
		clone.setJobsiteFloorsDefault(jobsiteFloorsDefault);
		clone.setJobsiteBuildingTypeDefault(jobsiteBuildingTypeDefault);
		clone.setBilltoAccountTypeDefault(billtoAccountTypeDefault);
		clone.setBilltoContractContactDefault(billtoContractContactDefault);
		clone.setBilltoBillingContactDefault(billtoBillingContactDefault);
		clone.setBilltoTaxExempt(billtoTaxExempt);
		clone.setBilltoTaxExemptReason(billtoTaxExemptReason);
		
		return clone;
	}



	
}
