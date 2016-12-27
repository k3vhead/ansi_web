package com.ansi.scilla.web.response.address;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;

import org.apache.commons.beanutils.BeanUtils;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.db.Address;


public class AddressResponseRecord extends ApplicationObject implements Comparable<AddressResponseRecord> {

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

	
	public AddressResponseRecord(Address address) throws IllegalAccessException, InvocationTargetException {
		super();
		BeanUtils.copyProperties(this, address);
	}
	
	public Integer getAddedBy() {
		return this.addedBy;
	}
	
	
	public void setAddedBy(Integer addedBy) {
		this.addedBy = addedBy;
	}


	public Date getAddedDate() {
		return this.addedDate;
	}
	
	public void setAddedDate(Date addedDate) {
		this.addedDate = addedDate;
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


	public void setUpdatedBy(Integer updatedBy) {
		this.updatedBy = updatedBy;
	}


	public Integer getUpdatedBy() {
		return this.updatedBy;
	}


	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}


	public Date getUpdatedDate() {
		return this.updatedDate;
	}


	public void setZip(String zip) {
		this.zip = zip;
	}


	public String getZip() {
		return this.zip;
	}

	@Override
	public int compareTo(AddressResponseRecord o) {
		int ret = this.getAddressId().compareTo(o.getAddressId());
		if ( ret == 0 ) {
			ret = this.name.compareTo(o.getName());
		}
		if ( ret == 0 ) {
			ret = this.status.compareTo(o.getStatus());
		}
		return 0;
	}



	
}
