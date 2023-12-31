package com.ansi.scilla.web.address.response;

import java.io.Serializable;

import com.ansi.scilla.common.db.Address;
import com.ansi.scilla.web.common.response.MessageResponse;
import com.ansi.scilla.web.common.response.WebMessages;

/**
 * Used to return a single address to the client
 * 
 *
 */
public class AddressResponse extends MessageResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private Address address;
	


	public AddressResponse() {
		super();
	}

	public AddressResponse(Address address, WebMessages webMessages) {
		super(webMessages);
		this.address = address;
		
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}



	
}
