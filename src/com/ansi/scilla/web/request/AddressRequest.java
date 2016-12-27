package com.ansi.scilla.web.request;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;

import org.apache.commons.beanutils.BeanUtils;

import com.ansi.scilla.web.common.AppUtils;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.thewebthing.commons.lang.JsonException;


	public class AddressRequest extends AbstractRequest{

	
		private static final long serialVersionUID = 1L;
		
		private Integer addedBy;
		private Date addedDate;
		private String address1;
		private String address2;
		private Integer addressId;
		private String city;
		private String county;
		private String name;
		private String state;
		private String status;
		private Integer updatedBy;
		private Date updatedDate;
		private String zip;

		
		public AddressRequest() {
			super();
		}
		
		public AddressRequest(String jsonString) throws JsonException, IllegalAccessException, InvocationTargetException, JsonParseException, JsonMappingException, IOException {
			this();
			AddressRequest req = (AddressRequest) AppUtils.json2object(jsonString, AddressRequest.class);
			BeanUtils.copyProperties(this, req);
		}
		
		@RequiredForAdd
		@RequiredForUpdate
		public Integer getAddedBy() {
			return this.addedBy;
		}
		
		
		public void setAddedBy(Integer addedBy) {
			this.addedBy = addedBy;
		}

		@RequiredForAdd
		@RequiredForUpdate
		public Date getAddedDate() {
			return this.addedDate;
		}
		
		public void setAddedDate(Date addedDate) {
			this.addedDate = addedDate;
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

		@RequiredForAdd
		@RequiredForUpdate
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

		@RequiredForAdd
		@RequiredForUpdate
		public String getStatus() {
			return this.status;
		}


		public void setUpdatedBy(Integer updatedBy) {
			this.updatedBy = updatedBy;
		}

		@RequiredForAdd
		@RequiredForUpdate
		public Integer getUpdatedBy() {
			return this.updatedBy;
		}


		public void setUpdatedDate(Date updatedDate) {
			this.updatedDate = updatedDate;
		}

		@RequiredForAdd
		@RequiredForUpdate
		public Date getUpdatedDate() {
			return this.updatedDate;
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
