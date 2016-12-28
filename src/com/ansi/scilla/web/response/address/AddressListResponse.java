package com.ansi.scilla.web.response.address;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ansi.scilla.common.db.Address;
import com.ansi.scilla.web.response.MessageResponse;

/** 
 * Used to return a list of "address" objects to the client
 * 
 * 
 *
 */
public class AddressListResponse extends MessageResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<AddressResponseRecord> addressList;

	public AddressListResponse() {
		super();
	}
	/**
	 * create a list of all code table records in the database, sorted by
	 * table, field, display value
	 * 
	 * @param conn
	 * @throws Exception
	 */
	public AddressListResponse(Connection conn) throws Exception {
		List<Address> addressList = Address.cast(new Address().selectAll(conn));
		this.addressList = new ArrayList<AddressResponseRecord>();
		for ( Address address : addressList ) {
			this.addressList.add(new AddressResponseRecord(address));
		}
		Collections.sort(this.addressList);
	}

	public AddressListResponse(Connection conn, Integer addressId, String name, String status) throws Exception {
		Address key = new Address();
		key.setAddressId(addressId);
		key.setName(name);
		key.setStatus(status);
		List<Address> addressList = Address.cast(key.selectSome(conn));
		this.addressList = new ArrayList<AddressResponseRecord>();
		for ( Address address : addressList ) {
			this.addressList.add(new AddressResponseRecord(address));
		}
		Collections.sort(this.addressList);
	}

	public List<AddressResponseRecord> getCodeList() {
		return addressList;
	}

	public void setAddressList(List<AddressResponseRecord> addressList) {
		this.addressList = addressList;
	}
	
	
}