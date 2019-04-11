package com.ansi.scilla.web.address.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.address.AddressUtils;
import com.ansi.scilla.common.address.Country;
import com.ansi.scilla.common.db.Address;
import com.ansi.scilla.common.db.Code;
import com.ansi.scilla.common.db.Contact;
import com.ansi.scilla.common.exceptions.DuplicateEntryException;
import com.ansi.scilla.web.address.request.AddressRequest;
import com.ansi.scilla.web.address.response.AddressListResponse;
import com.ansi.scilla.web.address.response.AddressResponse;
import com.ansi.scilla.web.common.request.RequestValidator;
import com.ansi.scilla.web.common.response.MessageKey;
import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.common.utils.AnsiURL;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.Permission;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class AddressServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;

	public static final String COMMAND_IS_ADD = "add";

	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String url = request.getRequestURI();

		Connection conn = null;
		try {
			ParsedUrl parsedUrl = new ParsedUrl(url);

			conn = AppUtils.getDBCPConn();
			AppUtils.validateSession(request, Permission.ADDRESS_WRITE);
			conn.setAutoCommit(false);

			Address address = new Address();
			if (parsedUrl.addressId != null) {
				address.setAddressId(Integer.parseInt(parsedUrl.addressId));
			}
			logger.log(Level.DEBUG, "Address ID: " + Integer.parseInt(parsedUrl.addressId));
			address.delete(conn);

			AddressResponse addressResponse = new AddressResponse();
			super.sendResponse(conn, response, ResponseCode.SUCCESS, addressResponse);

			conn.commit();
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e1) {
			super.sendForbidden(response);
		} catch (Exception e) {
			AppUtils.logException(e);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String url = request.getRequestURI();
		Connection conn = null;
		try {
			ParsedUrl parsedUrl = new ParsedUrl(url);
			conn = AppUtils.getDBCPConn();
			AppUtils.validateSession(request, Permission.ADDRESS_READ);

			if (parsedUrl.addressId.equals("list")) {
				// we're getting all the addresses in the database
				AddressListResponse addressListResponse = makeAddressListResponse(conn);
				super.sendResponse(conn, response, ResponseCode.SUCCESS, addressListResponse);
			} else if (parsedUrl.addressId.equals("add")) {
				doPost(request, response);
			} else {
				AddressListResponse addressListResponse = makeFilteredListResponse(conn, parsedUrl);
				super.sendResponse(conn, response, ResponseCode.SUCCESS, addressListResponse);
			}
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e1) {
			super.sendForbidden(response);
		} catch (RecordNotFoundException e) {
			super.sendNotFound(response);
		} catch (Exception e) {
			AppUtils.logException(e);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}

	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		Connection conn = null;
		try {
			conn = AppUtils.getDBCPConn();
			SessionData sessionData = AppUtils.validateSession(request, Permission.ADDRESS_WRITE);
			SessionUser sessionUser = sessionData.getUser();
			conn.setAutoCommit(false);

			String jsonString = super.makeJsonString(request);
			AnsiURL url = new AnsiURL(request, "address", new String[] { COMMAND_IS_ADD });

			logger.log(Level.DEBUG, jsonString);
			// AddressRequest addressRequest = new AddressRequest(jsonString);
			AddressRequest addressRequest = new AddressRequest();
			AppUtils.json2object(jsonString, addressRequest);
			Country country = AddressUtils.getCountryForState(addressRequest.getState());
			if ((!StringUtils.isBlank(url.getCommand())) && url.getCommand().equals(COMMAND_IS_ADD)) {
				processAddRequest(conn, response, addressRequest, country, sessionUser);
			} else if (url.getId() != null) {
				processUpdateRequest(conn, response, url.getId(), addressRequest, country, sessionUser);
			} else {
				// this should never happen
				throw new ServletException("Invalid system state");
			}

			conn.commit();
		} catch (InvalidFormatException e) {
			String badField = super.findBadField(e.toString());
			AddressResponse data = new AddressResponse();
			WebMessages messages = new WebMessages();
			messages.addMessage(badField, "Invalid Format");
			data.setWebMessages(messages);
			try {
				super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, data);
			} catch (Exception e2) {
				AppUtils.logException(e2);
				AppUtils.rollbackQuiet(conn);
				throw new ServletException(e2);
			}
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e1) {
			super.sendForbidden(response);
		} catch (Exception e) {
			AppUtils.logException(e);
			AppUtils.rollbackQuiet(conn);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}

	}

	private void processAddRequest(Connection conn, HttpServletResponse response, AddressRequest addressRequest,
			Country country, SessionUser sessionUser) throws Exception {
		Address address = new Address();
		ResponseCode responseCode = null;

		List<String> badFields = super.validateRequiredAddFields(addressRequest);
		WebMessages webMessages = makeWebMessages(conn, badFields);
		RequestValidator.validateAccountType(conn, webMessages, AddressRequest.BILLTO_ACCOUNT_TYPE_DEFAULT, addressRequest.getBilltoAccountTypeDefault(), false);
		RequestValidator.validateBoolean(webMessages, AddressRequest.BILLTO_TAX_EXEMPT, addressRequest.getBilltoTaxExempt(), false);

		// if all required fields are here, make sure optional fields are valid
		if (webMessages.isEmpty()) {
			validateOptionalFields(conn, addressRequest, webMessages);
		}
		if (webMessages.isEmpty()) {
			try {
				validateDuplicate(conn, addressRequest, country);
			} catch (DuplicateEntryException e) {
				String messageText = AppUtils.getMessageText(conn, MessageKey.DUPLICATE_ENTRY, "Record already Exists");
				webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, messageText);
				responseCode = ResponseCode.EDIT_FAILURE;
			}
		}

		if (webMessages.isEmpty()) {
			try {
				address = doAdd(conn, addressRequest, country, sessionUser);
				String message = AppUtils.getMessageText(conn, MessageKey.SUCCESS, "Success!");
				responseCode = ResponseCode.SUCCESS;
				webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, message);
			} catch (Exception e) {
				responseCode = ResponseCode.SYSTEM_FAILURE;
				AppUtils.logException(e);
				String messageText = AppUtils.getMessageText(conn, MessageKey.INSERT_FAILED, "Insert Failed");
				webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, messageText);
			}

		} else {
			responseCode = ResponseCode.EDIT_FAILURE;
		}
		AddressResponse addressResponse = new AddressResponse(address, webMessages);
		super.sendResponse(conn, response, responseCode, addressResponse);
	}

	protected Address doAdd(Connection conn, AddressRequest addressRequest, Country country, SessionUser sessionUser)
			throws Exception {
		Address address = new Address();

		request2addr(addressRequest, address, country, sessionUser);
		address.setAddedBy(sessionUser.getUserId());

		Integer addressId = address.insertWithKey(conn);
		address.setAddressId(addressId);
		return address;
	}

	private void processUpdateRequest(Connection conn, HttpServletResponse response, Integer addressId,
			AddressRequest addressRequest, Country country, SessionUser sessionUser) throws Exception {
		logger.log(Level.DEBUG, "Doing Update Stuff");
		ResponseCode responseCode = null;
		WebMessages webMessages = new WebMessages();
		Address address = new Address();
		address.setAddressId(addressId);

		try {
			address.selectOne(conn); // make sure we're working with a valid
										// address

			List<String> badFields = super.validateRequiredUpdateFields(addressRequest);
			webMessages = makeWebMessages(conn, badFields);

			// if all required fields are here, make sure optional fields are
			// valid
			if (webMessages.isEmpty()) {
				validateOptionalFields(conn, addressRequest, webMessages);
			}

			if (webMessages.isEmpty()) {
				logger.log(Level.DEBUG, "passed validation");
				address = doUpdate(conn, address, addressRequest, country, sessionUser);
				String message = AppUtils.getMessageText(conn, MessageKey.SUCCESS, "Success!");
				responseCode = ResponseCode.SUCCESS;
				webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, message);
				AddressResponse addressResponse = new AddressResponse(address, webMessages);
				super.sendResponse(conn, response, responseCode, addressResponse);
			} else {
				logger.log(Level.DEBUG, "Doing Edit Fail");
				responseCode = ResponseCode.EDIT_FAILURE;
				AddressResponse addressResponse = new AddressResponse(address, webMessages);
				super.sendResponse(conn, response, responseCode, addressResponse);
			}
		} catch (RecordNotFoundException e) {
			logger.log(Level.DEBUG, "Doing 404");
			super.sendNotFound(response);
		} catch (Exception e) {
			logger.log(Level.DEBUG, "Doing SysFailure");
			responseCode = ResponseCode.SYSTEM_FAILURE;
			AppUtils.logException(e);
			String messageText = AppUtils.getMessageText(conn, MessageKey.INSERT_FAILED, "Insert Failed");
			webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, messageText);
			AddressResponse addressResponse = new AddressResponse(address, webMessages);
			super.sendResponse(conn, response, responseCode, addressResponse);
		}
	}

	protected Address doUpdate(Connection conn, Address address, AddressRequest addressRequest, Country country,
			SessionUser sessionUser) throws Exception {

		request2addr(addressRequest, address, country, sessionUser);
		address.setUpdatedBy(sessionUser.getUserId());

		Address key = new Address();
		key.setAddressId(address.getAddressId());
		// if we update something that isn't there, a RecordNotFoundException
		// gets thrown
		// that exception get propagated and turned into a 404
		address.update(conn, key);
		return address;
	}

	/**
	 * Duplicate is defined as: same name, address1, address2, city, state, zip
	 * 
	 * @param conn
	 * @param addressRequest
	 * @param country
	 * @throws DuplicateEntryException
	 * @throws Exception
	 */
	private void validateDuplicate(Connection conn, AddressRequest addressRequest, Country country)
			throws DuplicateEntryException, Exception {
		Address address = new Address();

		address.setName(addressRequest.getName());
		address.setCountryCode(country.abbrev());
		if (!StringUtils.isBlank(addressRequest.getAddress1())) {
			address.setAddress1(addressRequest.getAddress1());
		}
		if (!StringUtils.isBlank(addressRequest.getAddress2())) {
			address.setAddress2(addressRequest.getAddress2());
		}
		if (!StringUtils.isBlank(addressRequest.getCity())) {
			address.setCity(addressRequest.getCity());
		}
		if (!StringUtils.isBlank(addressRequest.getState())) {
			address.setState(addressRequest.getState());
		}
		if (!StringUtils.isBlank(addressRequest.getZip())) {
			address.setZip(addressRequest.getZip());
		}

		try {
			address.selectOne(conn);
			throw new DuplicateEntryException();
		} catch (RecordNotFoundException e) {
			// this is good -- means no duplicates
		}
	}

	private void validateOptionalFields(Connection conn, AddressRequest addressRequest, WebMessages webMessages)
			throws Exception {
		logger.log(Level.DEBUG, "Checking optional fields");
		if (addressRequest.getBilltoTaxExempt() == Address.BILLTO_TAX_EXEMPT_IS_YES) {
			if (addressRequest.getBilltoTaxExemptReason() == null) {
				webMessages.addMessage(AddressRequest.BILLTO_TAX_EXEMPT_REASON, "Tax exempt reason is required");
			}
		}
		if (!isValidAddress(conn, addressRequest.getJobsiteBilltoAddressDefault())) {
			webMessages.addMessage(AddressRequest.JOBSITE_BILLTO_ADDRESS_DEFAULT, "Invalid Address");
		}
		if (!isValidContact(conn, addressRequest.getJobsiteJobContactDefault())) {
			webMessages.addMessage(AddressRequest.JOBSITE_JOB_CONTACT_DEFAULT, "Invalid Contact");
		}
		if (!isValidContact(conn, addressRequest.getJobsiteSiteContactDefault())) {
			webMessages.addMessage(AddressRequest.JOBSITE_SITE_CONTACT_DEFAULT, "Invalid Contact");
		}
		if (!isValidFloor(addressRequest.getJobsiteFloorsDefault())) {
			webMessages.addMessage(AddressRequest.JOBSITE_FLOORS_DEFAULT, "Invalid Value");
		}
		if (!isValidBuildingType(conn, addressRequest.getJobsiteBuildingTypeDefault())) {
			webMessages.addMessage(AddressRequest.JOBSITE_BUILDING_TYPE_DEFAULT, "Invalid Value");
		}

	}

	private boolean isValidAddress(Connection conn, Integer addressId) throws Exception {
		boolean isValid = true;
		if (addressId != null) {
			try {
				Address address = new Address();
				address.setAddressId(addressId);
				address.selectOne(conn);
			} catch (RecordNotFoundException e) {
				isValid = false;
			}
		}
		return isValid;
	}

	private boolean isValidContact(Connection conn, Integer contactId) throws Exception {
		boolean isValid = true;
		if (contactId != null) {
			try {
				Contact contact = new Contact();
				contact.setContactId(contactId);
				contact.selectOne(conn);
			} catch (RecordNotFoundException e) {
				isValid = false;
			}
		}
		return isValid;
	}

	private boolean isValidFloor(Integer floors) {
		boolean isValid = true;
		if (floors != null && (floors < 0 || floors > 999)) {
			isValid = false;
		}
		return isValid;
	}

	private boolean isValidBuildingType(Connection conn, String buildingType) throws Exception {
		boolean isValid = true;
		if (!StringUtils.isBlank(buildingType)) {
			try {
				Code code = new Code();
				code.setTableName("job");
				code.setFieldName("building_type");
				code.setValue(buildingType);
				code.selectOne(conn);
			} catch (RecordNotFoundException e) {
				isValid = false;
			}
		}
		return isValid;
	}

	/**
	 * Populate address record from request record (common to add / update)
	 * 
	 * @param addressRequest
	 * @param address
	 * @param country
	 * @param sessionUser
	 */
	private void request2addr(AddressRequest addressRequest, Address address, Country country,
			SessionUser sessionUser) {

		address.setAddress1(addressRequest.getAddress1());
		address.setAddress2(addressRequest.getAddress2());
		// address.setAddressId(addressRequest.getAddressId());
		address.setCity(addressRequest.getCity());
		address.setCounty(addressRequest.getCounty());
		address.setName(addressRequest.getName());
		address.setState(addressRequest.getState());
		address.setStatus(addressRequest.getStatus());
		address.setZip(addressRequest.getZip());
		address.setCountryCode(country.abbrev());
		address.setInvoiceStyleDefault(addressRequest.getInvoiceStyleDefault());
		address.setInvoiceGroupingDefault(addressRequest.getInvoiceGroupingDefault());
		if (addressRequest.getInvoiceBatchDefault() != null && addressRequest.getInvoiceBatchDefault() == 1) {
			address.setInvoiceBatchDefault(Address.INVOICE_BATCH_DEFAULT_IS_YES);
		} else {
			address.setInvoiceBatchDefault(Address.INVOICE_BATCH_DEFAULT_IS_NO);
		}
		address.setInvoiceTermsDefault(addressRequest.getInvoiceTermsDefault());
		address.setOurVendorNbrDefault(addressRequest.getInvoiceOurVendorNbrDefault());
		address.setJobsiteBilltoAddressDefault(addressRequest.getJobsiteBilltoAddressDefault());
		address.setJobsiteJobContactDefault(addressRequest.getJobsiteJobContactDefault());
		address.setJobsiteSiteContactDefault(addressRequest.getJobsiteSiteContactDefault());
		address.setJobsiteFloorsDefault(addressRequest.getJobsiteFloorsDefault());
		address.setJobsiteBuildingTypeDefault(addressRequest.getJobsiteBuildingTypeDefault());
		address.setBilltoAccountTypeDefault(addressRequest.getBilltoAccountTypeDefault());
		address.setBilltoContractContactDefault(addressRequest.getBilltoContractContactDefault());
		address.setBilltoBillingContactDefault(addressRequest.getBilltoBillingContactDefault());
		address.setBilltoTaxExempt(addressRequest.getBilltoTaxExempt());
		address.setBilltoTaxExemptReason(addressRequest.getBilltoTaxExemptReason());
		address.setUpdatedBy(sessionUser.getUserId());
	}

	private AddressListResponse makeAddressListResponse(Connection conn) throws Exception {
		AddressListResponse addressesListResponse = new AddressListResponse(conn);
		return addressesListResponse;
	}

	private AddressListResponse makeFilteredListResponse(Connection conn, ParsedUrl parsedUrl) throws Exception {
		AddressListResponse addressListResponse = new AddressListResponse(conn, parsedUrl.addressId);
		return addressListResponse;
	}

	protected WebMessages makeWebMessages(Connection conn, List<String> missingFields) throws Exception {
		WebMessages webMessages = new WebMessages();
		if (!missingFields.isEmpty()) {
			String messageText = AppUtils.getMessageText(conn, MessageKey.MISSING_DATA, "Required Entry");
			for (String field : missingFields) {
				webMessages.addMessage(field, messageText);
			}
		}
		return webMessages;
	}

	public class ParsedUrl extends ApplicationObject {
		public String addressId;

		private static final long serialVersionUID = 1L;

		public ParsedUrl(String url) throws RecordNotFoundException {
			int idx = url.indexOf("/address/");
			if (idx < 0) {
				throw new RecordNotFoundException();
			}
			String myString = url.substring(idx + "/address/".length());
			String[] urlPieces = myString.split("/");
			if (urlPieces.length >= 1) {
				this.addressId = (urlPieces[0]);
			}

		}
	}
}
