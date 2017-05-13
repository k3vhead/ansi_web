package com.ansi.scilla.web.response.address;

import java.io.Serializable;
import java.sql.Connection;

import org.apache.commons.beanutils.PropertyUtils;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.db.Address;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class AddressDetail extends ApplicationObject implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String ADDRESS1 = "address1";
	public static final String ADDRESS2 = "address2";
	public static final String ADDRESS_ID = "address_id";
	public static final String CITY = "city";
	public static final String COUNTY = "county";
	public static final String NAME = "name";
	public static final String STATE = "state";
	public static final String STATUS = "address_status";
	public static final String ZIP = "zip";
	public static final String COUNTRY_CODE = "country_code";

	private String address1;
	private String address2;
	private Integer addressId;
	private String city;
	private String county;
	private String name;
	private String state;
	private String status;
	private String zip;
	private String countryCode;


	public AddressDetail() {
		super();
	}


	public AddressDetail(Address address) throws Exception {
		this();
		PropertyUtils.copyProperties(this, address);
	}

	public AddressDetail(Connection conn, Integer addressId) throws RecordNotFoundException, Exception {
		this();
		Address address = new Address();
		address.setAddressId(addressId);
		address.selectOne(conn);
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

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}


}
