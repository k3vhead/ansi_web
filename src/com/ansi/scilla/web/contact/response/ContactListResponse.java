package com.ansi.scilla.web.contact.response;

import java.io.Serializable;
import java.sql.Connection;
import java.util.List;

import com.ansi.scilla.common.db.Contact;
import com.ansi.scilla.web.common.response.MessageResponse;

/** 
 * Used to return a list of "contact" objects to the client
 * 
 * @author ggroce
 *
 */
public class ContactListResponse extends MessageResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<Contact> contactList;

	public ContactListResponse() {
		super();
	}
	/**
	 * create a list of all contact table records in the database, sorted by
	 * table, field, display value
	 * 
	 * @param conn
	 * @throws Exception
	 */
	public ContactListResponse(Connection conn) throws Exception {
		this.contactList = Contact.cast(new Contact().selectAll(conn));
/*		Collections.sort(contactList,

				new Comparator<Contact>() {

			public int compare(Contact o1, Contact o2) {

				int ret = o1.getTableName().compareTo(o2.getTableName());
				if ( ret == 0 ) {
					ret = o1.getFieldName().compareTo(o2.getFieldName());
				}
				if ( ret == 0 ) {
					ret = o1.getSeq().compareTo(o2.getSeq());
				}
				if ( ret == 0 ) {
					ret = o1.getDisplayValue().compareTo(o2.getDisplayValue());
				}
		return ret;

			}

		});
*/
	}
	public List<Contact> getContactList() {
		return contactList;
	}

	public void setContactList(List<Contact> contactList) {
		this.contactList = contactList;
	}
	
	
}
