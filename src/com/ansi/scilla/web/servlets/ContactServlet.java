package com.ansi.scilla.web.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.common.db.Contact;
import com.ansi.scilla.common.exceptions.DuplicateEntryException;
import com.ansi.scilla.web.common.AppUtils;
import com.ansi.scilla.web.common.MessageKey;
import com.ansi.scilla.web.common.ResponseCode;
import com.ansi.scilla.web.common.WebMessages;
import com.ansi.scilla.web.request.ContactRequest;
import com.ansi.scilla.web.response.contact.ContactListResponse;
import com.ansi.scilla.web.response.contact.ContactResponse;
import com.ansi.scilla.web.struts.SessionUser;
import com.thewebthing.commons.db2.RecordNotFoundException;

/**
 * The url for delete will be of the form /contact/<contactId>
 * 
 * The url for get will be one of:
 * 		/contact/list  			(retrieves everything)
 * 		/contact/<contactId>	(retrieves a single record)
 * 
 * The url for adding a new record will be a POST to:
 * 		/contact/add   with parameters in the JSON
 * 
 * The url for update will be a POST to:
 * 		/contact/<contactId> with parameters in the JSON
 * 
 * 
 * @author gagroce
 *
 */
public class ContactServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doDelete(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		Connection conn = null;
		try {
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);
			
			String url = request.getRequestURI();
			System.out.println("ContactServlet: doDelete() Url:" + url);
			int idx = url.indexOf("/contact/");
			String myString = url.substring(idx + "/contact/".length());				
			String[] urlPieces = myString.split("/");
			String command = urlPieces[0];
			System.out.println("ContactServlet: doDelete() contactId:" + command);

			if ( urlPieces.length == 1 ) {   //  /<contactId> = 1 pieces
				Contact key = new Contact();
				if ( StringUtils.isNumeric(urlPieces[0])) { //Looks like a contactId
					System.out.println("ContactServlet: DoDelete: Trying to delete"+command);
					key.setContactId(Integer.valueOf(urlPieces[0]));
					key.delete(conn);
					
					ContactResponse contactResponse = new ContactResponse();
					super.sendResponse(conn, response, ResponseCode.SUCCESS, contactResponse);
					
					conn.commit();
				} else {
					throw new RecordNotFoundException();
				}
			} else {
				throw new RecordNotFoundException();
			}

/*			String jsonString = super.makeJsonString(request); //get request, change to Json
			ContactRequest contactRequest = new ContactRequest(jsonString); //put Json into contactReques
			System.out.println(contactRequest);//print request
			Contact contact = new Contact();
			contact.setContactId(contactRequest.getContactId());
			contact.delete(conn);
			
			ContactResponse contactResponse = new ContactResponse();
			super.sendResponse(conn, response, ResponseCode.SUCCESS, contactResponse);
			
			conn.commit();*/
		} catch ( Exception e) {
			AppUtils.logException(e);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}
	}

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String url = request.getRequestURI();
		int idx = url.indexOf("/contact/");
		if ( idx > -1 ) {
			System.out.println("Url:" + url);
			String queryString = request.getQueryString();
			System.out.println("Query String: " + queryString);
			
			// we're in the right place
			Connection conn = null;
			try {
				conn = AppUtils.getDBCPConn();
				
				// Figure out what we've got:				
				String myString = url.substring(idx + "/contact/".length());
				
				String[] urlPieces = myString.split("/");
				String command = urlPieces[0];
				
				if ( StringUtils.isBlank(command)) {
//					super.sendNotFound(response);
					throw new RecordNotFoundException();
				} else {
					if ( command.equals("list")) {
						// we're getting all the contacts in the database
						ContactListResponse contactListResponse = makeContactListResponse(conn);
						super.sendResponse(conn, response, ResponseCode.SUCCESS, contactListResponse);
					} else {
						// we're getting a single contact by contactId returned in a single entry list
						ContactListResponse contactListResponse = makeSingleListResponse(conn, urlPieces);
						super.sendResponse(conn, response, ResponseCode.SUCCESS, contactListResponse);
					}
				}
			} catch ( RecordNotFoundException e ) {
				System.out.println("ContactServlet: doGet() RecordNotFoundException 404");
				super.sendNotFound(response);						
			} catch ( Exception e) {
				AppUtils.logException(e);
				throw new ServletException(e);
			} finally {
				AppUtils.closeQuiet(conn);
			}
			
		} else {
			super.sendNotFound(response);
		}
	}


	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		SessionUser sessionUser = AppUtils.getSessionUser(request);
		String url = request.getRequestURI();
//		String queryString = request.getQueryString();
		
		Connection conn = null;
		try {
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);

			// figure out if this is an "add" or an "update"
			int idx = url.indexOf("/contact/");
			String myString = url.substring(idx + "/contact/".length());				
			String[] urlPieces = myString.split("/");
			String command = urlPieces[0];

			String jsonString = super.makeJsonString(request);
			System.out.println(jsonString);
			ContactRequest contactRequest = new ContactRequest(jsonString);
			
			Contact contact = null;
			ResponseCode responseCode = null;
			if ( command.equals(ACTION_IS_ADD) ) {
				WebMessages webMessages = validateAdd(conn, contactRequest);
				if (webMessages.isEmpty()) {
					try {
						contact = doAdd(conn, contactRequest, sessionUser);
						String message = AppUtils.getMessageText(conn, MessageKey.SUCCESS, "Success!");
						responseCode = ResponseCode.SUCCESS;
						webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, message);
					} catch ( DuplicateEntryException e ) {
						String messageText = AppUtils.getMessageText(conn, MessageKey.DUPLICATE_ENTRY, "Record already Exists");
						webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, messageText);
						responseCode = ResponseCode.EDIT_FAILURE;
					} catch ( Exception e ) {
						responseCode = ResponseCode.SYSTEM_FAILURE;
						AppUtils.logException(e);
						String messageText = AppUtils.getMessageText(conn, MessageKey.INSERT_FAILED, "Insert Failed");
						webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, messageText);
					}
				} else {
					responseCode = ResponseCode.EDIT_FAILURE;
				}
				ContactResponse contactResponse = new ContactResponse(contact, webMessages);
				super.sendResponse(conn, response, responseCode, contactResponse);
				
			} else if ( urlPieces.length == 1 ) {   //  /<contactId> = 1 pieces
				System.out.println("Doing Update Stuff");				
				WebMessages webMessages = validateAdd(conn, contactRequest);
				if (webMessages.isEmpty()) {
					System.out.println("passed validation");
					try {
						Contact key = new Contact();
						if ( StringUtils.isNumeric(urlPieces[0]) ) {//looks like a contactId
							System.out.println("Trying to do update");
							key.setContactId(Integer.valueOf(urlPieces[0]));
							contact = doUpdate(conn, key, contactRequest, sessionUser);
							String message = AppUtils.getMessageText(conn, MessageKey.SUCCESS, "Success!");
							responseCode = ResponseCode.SUCCESS;
							webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, message);
						} else { //non-integer contactId, probably a bad thing
							throw new RecordNotFoundException();
						}
					} catch ( RecordNotFoundException e ) {
						System.out.println("Doing 404");
						super.sendNotFound(response);						
					} catch ( Exception e) {
						System.out.println("Doing SysFailure");
						responseCode = ResponseCode.SYSTEM_FAILURE;
						AppUtils.logException(e);
						String messageText = AppUtils.getMessageText(conn, MessageKey.INSERT_FAILED, "Insert Failed");
						webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, messageText);
					}
				} else {
					System.out.println("Doing Edit Fail");
					responseCode = ResponseCode.EDIT_FAILURE;
				}
				ContactResponse contactResponse = new ContactResponse(contact, webMessages);
				super.sendResponse(conn, response, responseCode, contactResponse);
			} else {
				super.sendNotFound(response);
			}
			
			conn.commit();
		} catch ( RecordNotFoundException e ) {
			System.out.println("ContactServlet: doDelete() RecordNotFoundException 404");
			super.sendNotFound(response);						
		} catch ( Exception e ) {
			AppUtils.logException(e);
			AppUtils.rollbackQuiet(conn);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}
		
	}


	protected Contact doAdd(Connection conn, ContactRequest contactRequest, SessionUser sessionUser) throws Exception {
		Date today = new Date();
		Contact contact = new Contact();
		contact.setAddedBy(sessionUser.getUserId());
		contact.setAddedDate(today);
		if ( ! StringUtils.isBlank(contactRequest.getBusinessPhone())) {
			contact.setBusinessPhone(contactRequest.getBusinessPhone());
		}
//		contact.setContactId(contactRequest.getContactId());//created on add
		if ( ! StringUtils.isBlank(contactRequest.getFax())) {
			contact.setFax(contactRequest.getFax());
		}
		if ( ! StringUtils.isBlank(contactRequest.getFirstName())) {
			contact.setFirstName(contactRequest.getFirstName());
		}
		if ( ! StringUtils.isBlank(contactRequest.getLastName())) {
			contact.setLastName(contactRequest.getLastName());
		}
		if ( ! StringUtils.isBlank(contactRequest.getMobilePhone())) {
			contact.setMobilePhone(contactRequest.getMobilePhone());
		}
		if ( ! StringUtils.isBlank(contactRequest.getPreferredContact())) {
			contact.setPreferredContact(contactRequest.getPreferredContact());
		}
		contact.setUpdatedBy(sessionUser.getUserId());
		contact.setUpdatedDate(today);
		try {
			Integer contactId = contact.insertWithKey(conn);
			contact.setContactId(contactId);
		} catch ( SQLException e) {
			if ( e.getMessage().contains("duplicate key")) {
				throw new DuplicateEntryException();
			} else {
				AppUtils.logException(e);
				throw e;
			}
		} 
		return contact;
	}


	protected Contact doUpdate(Connection conn, Contact key, ContactRequest contactRequest, SessionUser sessionUser) throws Exception {
		System.out.println("This is the key:");
		System.out.println(key);
		System.out.println("************");
		Date today = new Date();
		Contact contact = new Contact();
		if ( ! StringUtils.isBlank(contactRequest.getBusinessPhone())) {
			contact.setBusinessPhone(contactRequest.getBusinessPhone());
		}
//		contact.setContactId(contactRequest.getContactId());//key is in the url
		if ( ! StringUtils.isBlank(contactRequest.getFax())) {
			contact.setFax(contactRequest.getFax());
		}
		if ( ! StringUtils.isBlank(contactRequest.getFirstName())) {
			contact.setFirstName(contactRequest.getFirstName());
		}
		if ( ! StringUtils.isBlank(contactRequest.getLastName())) {
			contact.setLastName(contactRequest.getLastName());
		}
		if ( ! StringUtils.isBlank(contactRequest.getMobilePhone())) {
			contact.setMobilePhone(contactRequest.getMobilePhone());
		}
		if ( ! StringUtils.isBlank(contactRequest.getPreferredContact())) {
			contact.setPreferredContact(contactRequest.getPreferredContact());
		}
		contact.setUpdatedBy(sessionUser.getUserId());
		contact.setUpdatedDate(today);
		// if we update something that isn't there, a RecordNotFoundException gets thrown
		// that exception get propagated and turned into a 404
		contact.update(conn, key);		
		return contact;
	}

	private ContactListResponse makeContactListResponse(Connection conn) throws Exception {
		ContactListResponse contactsListResponse = new ContactListResponse(conn);
		return contactsListResponse;
	}

	private ContactListResponse makeSingleListResponse(Connection conn, String[] urlPieces) throws Exception {
		ContactListResponse contactListResponse = new ContactListResponse(conn);
		
		if (StringUtils.isNumeric(urlPieces[0])){
			Contact contact = new Contact();
			contact.setContactId(Integer.valueOf(urlPieces[0]));
			System.out.println("Getting Contact for contactId: " + urlPieces[0]);
			contact.selectOne(conn);
			contactListResponse.setContactList(Arrays.asList(new Contact[] {contact} ));
		} else {
			throw new RecordNotFoundException();
		}
		return contactListResponse;
	}

/*	private ContactListResponse makeFilteredListResponse(Connection conn, String[] urlPieces) throws Exception {
		String tableName = null;
		String fieldName = null;
		String value = null;
		try {
			tableName = urlPieces[0];
			fieldName = urlPieces[1];
			value = urlPieces[2];
		} catch (ArrayIndexOutOfBoundsException e) {
			// this is OK, just means we ran out of filters
		}
		Contact contact = new Contact();
		if ( ! StringUtils.isBlank(tableName)) {
			contact.setTableName(tableName);
		}
		if ( ! StringUtils.isBlank(fieldName)) {
			contact.setFieldName(fieldName);
		}
		if ( ! StringUtils.isBlank(value)) {
			contact.setValue(value);
		}
		List<Contact> contactList = Contact.cast(contact.selectSome(conn));
		Collections.sort(contactList,
				new Comparator<Contact>() {
			public int compare(Contact o1, Contact o2) {
				int ret = o1.getTableName().compareTo(o2.getTableName());
				if ( ret == 0 ) {
					ret = o1.getFieldName().compareTo(o2.getFieldName());
				}
				if ( ret == 0 ) {
					ret = o1.getValue().compareTo(o2.getValue());
				}
				return ret;
			}
		});
		ContactListResponse contactListResponse = new ContactListResponse();
		contactListResponse.setContactList(contactList);
		return contactListResponse;
	}
*/

	
	protected WebMessages validateAdd(Connection conn, ContactRequest contactRequest) throws Exception {
		WebMessages webMessages = new WebMessages();
		List<String> missingFields = super.validateRequiredAddFields(contactRequest);
		if ( ! missingFields.isEmpty() ) {
			String messageText = AppUtils.getMessageText(conn, MessageKey.MISSING_DATA, "Required Entry");
			for ( String field : missingFields ) {
				webMessages.addMessage(field, messageText);
			}
		}
		return webMessages;
	}

	protected WebMessages validateUpdate(Connection conn, Contact key, ContactRequest contactRequest) throws RecordNotFoundException, Exception {
		WebMessages webMessages = new WebMessages();
		List<String> missingFields = super.validateRequiredUpdateFields(contactRequest);
		if ( ! missingFields.isEmpty() ) {
			String messageText = AppUtils.getMessageText(conn, MessageKey.MISSING_DATA, "Required Entry");
			for ( String field : missingFields ) {
				webMessages.addMessage(field, messageText);
			}
		}
		// if we "select" the key, and it isn't found, a "RecordNotFoundException" is thrown.
		// That exception will propagate up the tree until it turns into a 404 message sent to the client
		Contact testKey = (Contact)key.clone(); 
		testKey.selectOne(conn);
		return webMessages;
	}

	
}
