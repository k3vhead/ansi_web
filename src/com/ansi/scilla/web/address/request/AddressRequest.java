package com.ansi.scilla.web.address.request;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.PropertyUtils;

import com.ansi.scilla.web.common.request.AbstractRequest;
import com.ansi.scilla.web.common.request.RequiredForAdd;
import com.ansi.scilla.web.common.request.RequiredForUpdate;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.thewebthing.commons.lang.JsonException;


	public class AddressRequest extends AbstractRequest{

	
		private static final long serialVersionUID = 1L;
		
		public static final String NAME = "name";
		public static final String ADDRESS1 = "address1";
		public static final String ADDRESS2 = "address2";
		public static final String CITY = "city";
		public static final String ZIP = "zip";
		public static final String COUNTY = "county";
		public static final String INVOICE_BATCH_DEFAULT = "invoiceBatchDefault";
		public static final String INVOICE_OUR_VENDOR_NBR_DEFAULT = "invoiceOurVendorNbrDefault";
		public static final String STATE = "state";
		public static final String INVOICE_STYLE_DEFAULT = "invoiceStyleDefault";
		public static final String INVOICE_GROUPING_DEFAULT = "invoiceGroupingDefault";
		public static final String INVOICE_TERMS_DEFAULT = "invoiceTermsDefault";
		public static final String JOBSITE_BILLTO_ADDRESS_DEFAULT = "jobsiteBilltoAddressDefault";
		public static final String JOBSITE_JOB_CONTACT_DEFAULT = "jobsiteJobContactDefault";
		public static final String JOBSITE_SITE_CONTACT_DEFAULT = "jobsiteSiteContactDefault";
		public static final String JOBSITE_FLOORS_DEFAULT = "jobsiteFloorsDefault";
		public static final String JOBSITE_BUILDING_TYPE_DEFAULT = "jobsiteBuildingTypeDefault";
		public static final String BILLTO_ACCOUNT_TYPE_DEFAULT = "billtoAccountTypeDefault";
		public static final String BILLTO_CONTRACT_CONTACT_DEFAULT = "billtoContractContactDefault";
		public static final String BILLTO_BILLING_CONTACT_DEFAULT = "billtoBillingContactDefault";
		public static final String BILLTO_TAX_EXEMPT = "billtoTaxExempt";
		public static final String BILLTO_TAX_EXEMPT_REASON = "billtoTaxExemptReason";
		
		private String address1;
		private String address2;
		private Integer addressId;
		private String city;
		private String county;
		private String name;
		private String state;
		private String status;
		private String zip;
		private String invoiceStyleDefault;
		private String invoiceGroupingDefault;
		private Integer invoiceBatchDefault;
		private String invoiceTermsDefault;
		private String invoiceOurVendorNbrDefault;
		private Integer jobsiteBilltoAddressDefault;
		private Integer jobsiteJobContactDefault;
		private Integer jobsiteSiteContactDefault;
		private Integer jobsiteFloorsDefault;
		private String jobsiteBuildingTypeDefault;
		private String billtoAccountTypeDefault;
		private Integer billtoContractContactDefault;
		private Integer billtoBillingContactDefault;
		private Integer billtoTaxExempt;
		private String billtoTaxExemptReason;
		
		public AddressRequest() {
			super();
		}
		
		public AddressRequest(String jsonString) throws JsonException, IllegalAccessException, InvocationTargetException, JsonParseException, JsonMappingException, IOException, InstantiationException, NoSuchMethodException {
			this();
			AddressRequest req = new AddressRequest();
			AppUtils.json2object(jsonString, req);
			PropertyUtils.copyProperties(this, req);
		}
		

		public void setAddress1(String address1) {
			this.address1 = address1;
		}

		@RequiredForAdd
		@RequiredForUpdate
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

		@RequiredForAdd
		@RequiredForUpdate
		public String getCity() {
			return this.city;
		}

		public void setCounty(String county) {
			this.county = county;
		}

		@RequiredForAdd
		@RequiredForUpdate
		public String getCounty() {
			return this.county;
		}

		public void setName(String name) {
			this.name = name;
		}

		@RequiredForAdd
		@RequiredForUpdate
		public String getName() {
			return this.name;
		}

		public void setState(String state) {
			this.state = state;
		}

		@RequiredForAdd
		@RequiredForUpdate
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

		@RequiredForAdd
		@RequiredForUpdate
		public String getZip() {
			return this.zip;
		}
		@JsonProperty(INVOICE_STYLE_DEFAULT)
		public String getInvoiceStyleDefault() {
			return invoiceStyleDefault;
		}
		@JsonProperty(INVOICE_STYLE_DEFAULT)
		public void setInvoiceStyleDefault(String invoiceStyleDefault) {
			this.invoiceStyleDefault = invoiceStyleDefault;
		}
		@JsonProperty(INVOICE_GROUPING_DEFAULT)
		public String getInvoiceGroupingDefault() {
			return invoiceGroupingDefault;
		}
		@JsonProperty(INVOICE_GROUPING_DEFAULT)
		public void setInvoiceGroupingDefault(String invoiceGroupingDefault) {
			this.invoiceGroupingDefault = invoiceGroupingDefault;
		}
		@JsonProperty(INVOICE_BATCH_DEFAULT)
		public Integer getInvoiceBatchDefault() {
			return invoiceBatchDefault;
		}
		@JsonProperty(INVOICE_BATCH_DEFAULT)
		public void setInvoiceBatchDefault(Integer invoiceBatchDefault) {
			this.invoiceBatchDefault = invoiceBatchDefault;
		}
		@JsonProperty(INVOICE_TERMS_DEFAULT)
		public String getInvoiceTermsDefault() {
			return invoiceTermsDefault;
		}
		@JsonProperty(INVOICE_TERMS_DEFAULT)
		public void setInvoiceTermsDefault(String invoiceTermsDefault) {
			this.invoiceTermsDefault = invoiceTermsDefault;
		}
		@JsonProperty(INVOICE_OUR_VENDOR_NBR_DEFAULT)
		public String getInvoiceOurVendorNbrDefault() {
			return invoiceOurVendorNbrDefault;
		}
		@JsonProperty(INVOICE_OUR_VENDOR_NBR_DEFAULT)
		public void setInvoiceOurVendorNbrDefault(String invoiceOurVendorNbrDefault) {
			this.invoiceOurVendorNbrDefault = invoiceOurVendorNbrDefault;
		}

		@JsonProperty(JOBSITE_BILLTO_ADDRESS_DEFAULT)
		public Integer getJobsiteBilltoAddressDefault() {
			return jobsiteBilltoAddressDefault;
		}

		@JsonProperty(JOBSITE_BILLTO_ADDRESS_DEFAULT)
		public void setJobsiteBilltoAddressDefault(Integer jobsiteBilltoAddressDefault) {
			this.jobsiteBilltoAddressDefault = jobsiteBilltoAddressDefault;
		}

		@JsonProperty(JOBSITE_JOB_CONTACT_DEFAULT)
		public Integer getJobsiteJobContactDefault() {
			return jobsiteJobContactDefault;
		}

		@JsonProperty(JOBSITE_JOB_CONTACT_DEFAULT)
		public void setJobsiteJobContactDefault(Integer jobsiteJobContactDefault) {
			this.jobsiteJobContactDefault = jobsiteJobContactDefault;
		}

		@JsonProperty(JOBSITE_SITE_CONTACT_DEFAULT)
		public Integer getJobsiteSiteContactDefault() {
			return jobsiteSiteContactDefault;
		}

		@JsonProperty(JOBSITE_SITE_CONTACT_DEFAULT)
		public void setJobsiteSiteContactDefault(Integer jobsiteSiteContactDefault) {
			this.jobsiteSiteContactDefault = jobsiteSiteContactDefault;
		}

		@JsonProperty(JOBSITE_FLOORS_DEFAULT)
		public Integer getJobsiteFloorsDefault() {
			return jobsiteFloorsDefault;
		}

		@JsonProperty(JOBSITE_FLOORS_DEFAULT)
		public void setJobsiteFloorsDefault(Integer jobsiteFloorsDefault) {
			this.jobsiteFloorsDefault = jobsiteFloorsDefault;
		}

		@JsonProperty(JOBSITE_BUILDING_TYPE_DEFAULT)
		public String getJobsiteBuildingTypeDefault() {
			return jobsiteBuildingTypeDefault;
		}

		@JsonProperty(JOBSITE_BUILDING_TYPE_DEFAULT)
		public void setJobsiteBuildingTypeDefault(String jobsiteBuildingTypeDefault) {
			this.jobsiteBuildingTypeDefault = jobsiteBuildingTypeDefault;
		}

		@JsonProperty(BILLTO_ACCOUNT_TYPE_DEFAULT)
		public String getBilltoAccountTypeDefault() {
			return billtoAccountTypeDefault;
		}

		@JsonProperty(BILLTO_ACCOUNT_TYPE_DEFAULT)
		public void setBilltoAccountTypeDefault(String billtoAccountTypeDefault) {
			this.billtoAccountTypeDefault = billtoAccountTypeDefault;
		}

		@JsonProperty(BILLTO_CONTRACT_CONTACT_DEFAULT)
		public Integer getBilltoContractContactDefault() {
			return billtoContractContactDefault;
		}

		@JsonProperty(BILLTO_CONTRACT_CONTACT_DEFAULT)
		public void setBilltoContractContactDefault(Integer billtoContractContactDefault) {
			this.billtoContractContactDefault = billtoContractContactDefault;
		}

		@JsonProperty(BILLTO_BILLING_CONTACT_DEFAULT)
		public Integer getBilltoBillingContactDefault() {
			return billtoBillingContactDefault;
		}

		@JsonProperty(BILLTO_BILLING_CONTACT_DEFAULT)
		public void setBilltoBillingContactDefault(Integer billtoBillingContactDefault) {
			this.billtoBillingContactDefault = billtoBillingContactDefault;
		}

		@JsonProperty(BILLTO_TAX_EXEMPT)
		public Integer getBilltoTaxExempt() {
			return billtoTaxExempt;
		}

		@JsonProperty(BILLTO_TAX_EXEMPT)
		public void setBilltoTaxExempt(Integer billtoTaxExempt) {
			this.billtoTaxExempt = billtoTaxExempt;
		}

		@JsonProperty(BILLTO_TAX_EXEMPT_REASON)
		public String getBilltoTaxExemptReason() {
			return billtoTaxExemptReason;
		}

		@JsonProperty(BILLTO_TAX_EXEMPT_REASON)
		public void setBilltoTaxExemptReason(String billtoTaxExemptReason) {
			this.billtoTaxExemptReason = billtoTaxExemptReason;
		}

		
		

	
	
}
