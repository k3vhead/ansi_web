package com.ansi.scilla.web.address.response;

import java.io.Serializable;
import java.sql.Connection;
import java.util.Arrays;
import java.util.List;

import com.ansi.scilla.web.address.query.AddressResponseQuery;
import com.ansi.scilla.web.common.response.MessageResponse;

/** 
 * Used to return a list of "address" objects to the client
 * 
 * 
 *
 */
public class AddressListResponse extends MessageResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<AddressResponseItem> addressList;

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
		super();
		this.addressList = AddressResponseQuery.select(conn);
	}

	public AddressListResponse(Connection conn, String addressId) throws Exception {
		super();
		AddressResponseItem item = AddressResponseQuery.selectOne(conn, Integer.valueOf(addressId));
		this.addressList = Arrays.asList( new AddressResponseItem[] {item} );
	}

	public List<AddressResponseItem> getAddressList() {
		return addressList;
	}

	public void setAddressList(List<AddressResponseItem> addressList) {
		this.addressList = addressList;
	}
	
	
}
