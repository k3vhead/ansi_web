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

		
		

	
	
}
