package com.ansi.scilla.web.address.request;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.PropertyUtils;

import com.ansi.scilla.web.common.AppUtils;
import com.ansi.scilla.web.common.request.AbstractRequest;
import com.ansi.scilla.web.common.request.RequiredForAdd;
import com.ansi.scilla.web.common.request.RequiredForUpdate;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.thewebthing.commons.lang.JsonException;


	public class AddressRequest extends AbstractRequest{

	
		private static final long serialVersionUID = 1L;
		
		private String address1;
		private String address2;
		private Integer addressId;
		private String city;
		private String county;
		private String name;
		private String state;
		private String status;
		private String zip;

		
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
		

	
	
}
