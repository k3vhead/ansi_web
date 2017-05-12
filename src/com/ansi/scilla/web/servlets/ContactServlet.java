package com.ansi.scilla.web.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.common.db.Contact;
import com.ansi.scilla.common.db.PermissionLevel;
import com.ansi.scilla.web.common.AnsiURL;
import com.ansi.scilla.web.common.AppUtils;
import com.ansi.scilla.web.common.MessageKey;
import com.ansi.scilla.web.common.Permission;
import com.ansi.scilla.web.common.ResponseCode;
import com.ansi.scilla.web.common.WebMessages;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.request.ContactRequest;
import com.ansi.scilla.web.response.contact.ContactListResponse;
import com.ansi.scilla.web.response.contact.ContactResponse;
import com.ansi.scilla.web.response.ticket.TicketReturnResponse;
import com.ansi.scilla.web.struts.SessionData;
import com.ansi.scilla.web.struts.SessionUser;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
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
		AnsiURL url = null;
		try {
			conn = AppUtils.getDBCPConn();
//			String jsonString = super.makeJsonString(request);
//			ContactRequest contactRequest = (ContactRequest)AppUtils.json2object(jsonString, ContactRequest.class);
			url = new AnsiURL(request, "contact", (String[])null);
			AppUtils.validateSession(request, Permission.SYSADMIN, PermissionLevel.PERMISSION_LEVEL_IS_WRITE);
			conn.setAutoCommit(false);
			Contact key = new Contact();
			key.setContactId(url.getId());
			key.delete(conn);
			conn.commit();
			ContactResponse data = new ContactResponse();
			WebMessages webMessages = new WebMessages();
			data.setWebMessages(webMessages);
			super.sendResponse(conn, response, ResponseCode.SUCCESS, data);
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e) {
			super.sendForbidden(response);
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
		AnsiURL url = null;
		Connection conn = null;
		WebMessages webMessages = new WebMessages();
		try {
			conn = AppUtils.getDBCPConn();
			AppUtils.validateSession(request, Permission.JOB, PermissionLevel.PERMISSION_LEVEL_IS_READ);
			url = new AnsiURL(request, "contact",(String[])null);
			ContactListResponse contactListResponse = makeSingleListResponse(conn, url.getId());
			webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "Success");
			contactListResponse.setWebMessages(webMessages);
			super.sendResponse(conn, response, ResponseCode.SUCCESS, contactListResponse);				
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e) {
			super.sendForbidden(response);
		} catch ( RecordNotFoundException e ) {
			super.sendNotFound(response);						
		} catch ( Exception e) {
			AppUtils.logException(e);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}
	}


	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		
		
		AnsiURL url = null;
		Connection conn = null;
		try {
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);
			
			SessionData sessionData = AppUtils.validateSession(request, Permission.SYSADMIN, PermissionLevel.PERMISSION_LEVEL_IS_WRITE);
			SessionUser sessionUser = sessionData.getUser();

			try {
				String jsonString = super.makeJsonString(request);
				ContactRequest contactRequest = (ContactRequest)AppUtils.json2object(jsonString, ContactRequest.class);
				url = new AnsiURL(request,"contact", new String[] {ACTION_IS_ADD});

				if ( url.getId() != null ) {
					// THis is an update
					processUpdate(conn, response, url.getId(), contactRequest, sessionUser);
				} else if ( url.getCommand().equalsIgnoreCase(ACTION_IS_ADD)) {
					// this is an add
					processAdd(conn, response, contactRequest, sessionUser);
				} else {
					// this is messed up
					super.sendNotFound(response);
				}
			} catch ( InvalidFormatException e ) {
				String badField = super.findBadField(e.toString());
				ContactResponse data = new ContactResponse();
				WebMessages messages = new WebMessages();
				messages.addMessage(badField, "Invalid Format");
				data.setWebMessages(messages);
				super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, data);
			} catch (TimeoutException | NotAllowedException | ExpiredLoginException e) {
				super.sendForbidden(response);
			} catch ( RecordNotFoundException e ) {
				super.sendNotFound(response);
			}
		} catch ( Exception e ) {
			AppUtils.logException(e);
			AppUtils.rollbackQuiet(conn);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}
	}			
			
	protected void processAdd(Connection conn, HttpServletResponse response, ContactRequest contactRequest, SessionUser sessionUser) throws Exception {
		ResponseCode responseCode = null;
		Contact contact = new Contact();
		
		WebMessages webMessages = validateAdd(conn, contactRequest);
		if (webMessages.isEmpty()) {			
			contact = doAdd(conn, contactRequest, sessionUser);
			String message = AppUtils.getMessageText(conn, MessageKey.SUCCESS, "Success!");		
			webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, message);
			responseCode = ResponseCode.SUCCESS;
		} else {
			responseCode = ResponseCode.EDIT_FAILURE;
		}
		ContactResponse contactResponse = new ContactResponse(contact, webMessages);
		super.sendResponse(conn, response, responseCode, contactResponse);
	}




	protected void processUpdate(Connection conn, HttpServletResponse response, Integer contactId, ContactRequest contactRequest, SessionUser sessionUser) throws RecordNotFoundException, Exception {
		Contact contact = new Contact();
		ContactResponse data = new ContactResponse();
		contact.setContactId(contactId);
		contact.selectOne(conn);  // this throws RecordNotFound, which is propagated up the line into a 404 return 
		WebMessages webMessages = validateUpdate(conn, contact, contactRequest);
		
		if (webMessages.isEmpty()) {
			contact = doUpdate(conn, contactId, contact, contactRequest, sessionUser);
			conn.commit();
			webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "Update successful!");
			data.setContact(contact);
			data.setWebMessages(webMessages);
			super.sendResponse(conn, response, ResponseCode.SUCCESS, data);
		} else {
			data.setWebMessages(webMessages);
			super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, data);
		}
	}

	protected WebMessages validateAdd(Connection conn, ContactRequest contactRequest) throws Exception {
		WebMessages webMessages = new WebMessages();
		List<String> missingFields = super.validateRequiredAddFields(contactRequest);
		if ( missingFields.isEmpty() ) {
			List<String> badFormatFieldList = super.validateFormat(contactRequest);
			if ( badFormatFieldList.isEmpty() ) {
				if ( contactRequest.isValidPreferredContact(conn)) {
					validatePreferredContact(contactRequest, webMessages);
				} else {
					webMessages.addMessage(ContactRequest.PREFERRED_CONTACT, "Invalid value");
				}
				boolean isDupe = isDuplicateContact(conn, contactRequest);
				if ( isDupe ) {
					webMessages.addMessage(ContactRequest.LAST_NAME, "Duplicate Contact");
				}
			} else {
				for ( String field : badFormatFieldList ) {
					webMessages.addMessage(field, "Invalid Format");
				}
			}
		} else {
			// we have required fields that are not populated
			String messageText = AppUtils.getMessageText(conn, MessageKey.MISSING_DATA, "Required Entry");
			for ( String field : missingFields ) {
				webMessages.addMessage(field, messageText);
			}
		}
		return webMessages;
	}

	protected WebMessages validateUpdate(Connection conn, Contact contact, ContactRequest contactRequest) throws RecordNotFoundException, Exception {
		WebMessages webMessages = new WebMessages();
		List<String> missingFields = super.validateRequiredUpdateFields(contactRequest);
		if ( missingFields.isEmpty() ) {
			List<String> badFormatFieldList = super.validateFormat(contactRequest);
			if ( badFormatFieldList.isEmpty() ) {
				if ( contactRequest.isValidPreferredContact(conn)) {
					validatePreferredContact(contactRequest, webMessages);
				} else {
					webMessages.addMessage(ContactRequest.PREFERRED_CONTACT, "Invalid value");
				}
			} else {
				for ( String field : badFormatFieldList ) {
					webMessages.addMessage(field, "Invalid Format");
				}
			}
		} else {
			// we have required fields that are not populated
			String messageText = AppUtils.getMessageText(conn, MessageKey.MISSING_DATA, "Required Entry");
			for ( String field : missingFields ) {
				webMessages.addMessage(field, messageText);
			}
		}
		return webMessages;
	}

	protected Contact doAdd(Connection conn, ContactRequest contactRequest, SessionUser sessionUser) throws Exception {
		Contact contact = new Contact();
		contact.setAddedBy(sessionUser.getUserId());
		contact.setBusinessPhone(contactRequest.getBusinessPhone());
//		contact.setContactId(contactRequest.getContactId());//created on add
		contact.setEmail(contactRequest.getEmail());
		contact.setFax(contactRequest.getFax());
		contact.setFirstName(contactRequest.getFirstName());
		contact.setLastName(contactRequest.getLastName());
		contact.setMobilePhone(contactRequest.getMobilePhone());
		contact.setPreferredContact(contactRequest.getPreferredContact());
		contact.setUpdatedBy(sessionUser.getUserId());
		Integer contactId = contact.insertWithKey(conn);
		contact.setContactId(contactId);
		conn.commit();
		return contact;
	}

	protected Contact doUpdate(Connection conn, Integer contactId, Contact contact, ContactRequest contactRequest, SessionUser sessionUser) throws Exception {
			contact.setBusinessPhone(contactRequest.getBusinessPhone());
			contact.setFax(contactRequest.getFax());
			contact.setEmail(contactRequest.getEmail());
			contact.setFirstName(contactRequest.getFirstName());
			contact.setLastName(contactRequest.getLastName());
			contact.setMobilePhone(contactRequest.getMobilePhone());
			contact.setPreferredContact(contactRequest.getPreferredContact());
			contact.setUpdatedBy(sessionUser.getUserId());
	//		contact.setUpdatedDate(today); this gets magically updated for us
			
			Contact key = new Contact();
			key.setContactId(contactId);
			contact.update(conn, key);	
			return contact;
		}

	protected void validatePreferredContact(ContactRequest contactRequest, WebMessages webMessages) {
		if ( contactRequest.getPreferredContact().equals(Contact.BUSINESS_PHONE) && StringUtils.isBlank(contactRequest.getBusinessPhone())){
			webMessages.addMessage(Contact.BUSINESS_PHONE, "Missing data");
		} else if ( contactRequest.getPreferredContact().equals(Contact.MOBILE_PHONE) && StringUtils.isBlank(contactRequest.getMobilePhone())){
			webMessages.addMessage(Contact.MOBILE_PHONE, "Missing data");
		} else if ( contactRequest.getPreferredContact().equals(Contact.FAX) && StringUtils.isBlank(contactRequest.getFax())){
			webMessages.addMessage(Contact.FAX, "Missing data");
		} else if ( contactRequest.getPreferredContact().equals(Contact.EMAIL) && StringUtils.isBlank(contactRequest.getEmail())){
			webMessages.addMessage(Contact.EMAIL, "Missing data");
		}
		
	}

	protected boolean isDuplicateContact(Connection conn, ContactRequest contactRequest) throws Exception {
		boolean isDuplicate = false;
		Contact potential = new Contact();
		BeanUtils.copyProperties(potential, contactRequest);
		
		Contact key = new Contact();
		key.setLastName(contactRequest.getLastName());
		key.setFirstName(contactRequest.getFirstName());
		List<Contact> contactList = Contact.cast(key.selectSome(conn));
		for ( Contact actual : contactList) {
			if ( potential.equals(actual)) {
				isDuplicate = true;
			}
		}
		return isDuplicate;
	}

	protected ContactListResponse makeSingleListResponse(Connection conn, Integer contactId) throws Exception {
		ContactListResponse contactListResponse = new ContactListResponse();		
		Contact contact = new Contact();
		contact.setContactId(contactId);
		contact.selectOne(conn);
		contactListResponse.setContactList(Arrays.asList(new Contact[] {contact} ));
		return contactListResponse;
	}

	
}
