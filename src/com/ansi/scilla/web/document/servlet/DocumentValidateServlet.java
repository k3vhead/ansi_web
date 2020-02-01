package com.ansi.scilla.web.document.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;

import com.ansi.scilla.common.db.Document;
import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.utils.AnsiURL;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.Permission;
import com.ansi.scilla.web.document.request.DocumentUploadRequest;
import com.ansi.scilla.web.document.response.DocumentUploadResponse;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;

public class DocumentValidateServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;

	public static final String REALM = "validate";

	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Connection conn = null;
		try {
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);
			AppUtils.validateSession(request, Permission.DOCUMENTS_READ);
			
			AnsiURL url = new AnsiURL(request, REALM, (String[])null);
			if ( url.getId() == null ) {
				super.sendNotFound(response);
			} else {
				sendDocument(conn, response, url.getId());
			}
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e1) {
			super.sendForbidden(response);
		} catch ( Exception e) {
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
			conn.setAutoCommit(false);
			String jsonString = super.makeJsonString(request);
			DocumentUploadRequest documentRequest = new DocumentUploadRequest();			
			AppUtils.validateSession(request, Permission.DOCUMENTS_WRITE);
			
			AppUtils.json2object(jsonString, documentRequest);
			AnsiURL url = new AnsiURL(request, REALM, (String[])null);
			if (url.getId() == null ) {
				validateAdd(conn, request, response, documentRequest);
			} else {
				validateUpdate(conn, request, response, documentRequest, url.getId());
			}
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e1) {
			super.sendForbidden(response);
		} catch ( Exception e) {
			AppUtils.logException(e);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}
		
	}

	
	

	

	
	
	private void sendDocument(Connection conn, HttpServletResponse response, Integer id) throws Exception {
		List<String> fieldNames = Arrays.asList(new String[] {
				Document.DESCRIPTION,
				Document.DOCUMENT_DATE,
				Document.EXPIRATION_DATE,
				Document.XREF_TYPE,
				Document.XREF_ID
		});
		String sql = "select " + StringUtils.join(fieldNames, ",") + " from " + Document.TABLE + " where " + Document.DOCUMENT_ID + "=?";
		logger.log(Level.DEBUG, sql);
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setInt(1, id);
		ResultSet rs = ps.executeQuery();
		if ( rs.next() ) {
			DocumentUploadResponse data = new DocumentUploadResponse(id, rs);
			WebMessages webMessages = new WebMessages();
			webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "Success");
			data.setWebMessages(webMessages);
			super.sendResponse(conn, response, ResponseCode.SUCCESS, data);
		} else {
			super.sendNotFound(response);
		}
		rs.close();
	}








	private void validateAdd(Connection conn, HttpServletRequest request, HttpServletResponse response, DocumentUploadRequest documentRequest) throws Exception {
//		DocumentUploadRequest documentRequest = makeUploadRequest(request);
		
		
		WebMessages webMessages = documentRequest.validateUpload(conn);
		DocumentUploadResponse data = new DocumentUploadResponse();
		data.setWebMessages(webMessages);
	
		if (webMessages.isEmpty()) {
			webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "Success");
			super.sendResponse(conn, response, ResponseCode.SUCCESS, data);				
		} else {
			super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, data);
		}			
	}


	private void validateUpdate(Connection conn, HttpServletRequest request, HttpServletResponse response, DocumentUploadRequest documentRequest, Integer id) throws Exception {
//		DocumentUploadRequest documentRequest = makeUploadRequest(request);
		
		
		WebMessages webMessages = documentRequest.validateEdit(conn);
		DocumentUploadResponse data = new DocumentUploadResponse();
		data.setWebMessages(webMessages);
	
		
		if (webMessages.isEmpty()) {
//			data = makeResponse(conn, )
			webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "Success");
			super.sendResponse(conn, response, ResponseCode.SUCCESS, data);				
		} else {
			super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, data);
		}			
	}


//	private DocumentUploadRequest makeUploadRequest(HttpServletRequest request) throws ParseException {
//		DocumentUploadRequest documentRequest = new DocumentUploadRequest();
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//	
//		
//		documentRequest.setDescription(request.getParameter(DocumentUploadRequest.DESCRIPTION));		
//		if ( ! StringUtils.isBlank(request.getParameter(DocumentUploadRequest.DOCUMENT_DATE)) ) {
//			Date documentDate = sdf.parse(request.getParameter(DocumentUploadRequest.DOCUMENT_DATE));
//			documentRequest.setDocumentDate(documentDate);
//		}
//		if ( ! StringUtils.isBlank(request.getParameter(DocumentUploadRequest.EXPIRATION_DATE)) ) {
//			Date expirationDate = sdf.parse(request.getParameter(DocumentUploadRequest.EXPIRATION_DATE));
//			documentRequest.setExpirationDate(expirationDate);
//		}
//		documentRequest.setDocumentType(request.getParameter(DocumentUploadRequest.DOCUMENT_TYPE));
//		if ( ! StringUtils.isBlank(request.getParameter(DocumentUploadRequest.XREF_ID))) {
//			Integer xrefId = Integer.valueOf(request.getParameter(DocumentUploadRequest.XREF_ID));
//			documentRequest.setXrefId(xrefId);
//		}		
//		documentRequest.setFileSelect(request.getParameter(DocumentUploadRequest.FILE_SELECT));
//		return documentRequest;
//	}


//	private void doUpload(Connection conn, HttpServletRequest request, HttpServletResponse response, SessionData sessionData) throws TimeoutException, NotAllowedException, ExpiredLoginException, Exception {
//		logger.log(Level.DEBUG, "doing upload");
//		if ( !ServletFileUpload.isMultipartContent(request) ) {
//			logger.log(Level.DEBUG, "not multipart");
//			request.setAttribute("ansi_upload_message","Not a MultiPart");
//			getServletContext().getRequestDispatcher("/message.jsp").forward(request, response);
//			return;
//		}
//		
//		DiskFileItemFactory factory = new DiskFileItemFactory();
//		factory.setRepository(new File("/tmp"));
//		ServletFileUpload upload = new ServletFileUpload(factory);
//
//		try {
//			@SuppressWarnings("unchecked")
//			List<FileItem> formItems = upload.parseRequest(request);
//			if ( formItems != null && formItems.size() > 0 ) {
//				logger.log(Level.DEBUG, "formItems");
//				Date today = new Date();
//				Document document = makeDocument(formItems);
//				document.setAddedBy(sessionData.getUser().getUserId());
//				document.setUpdatedBy(sessionData.getUser().getUserId());
//				document.setAddedDate(today);
//				document.setUpdatedDate(today);
//				document.insertWithKey(conn);
//				conn.commit();
//			}
//			request.setAttribute("ansi_upload_message","Success");
//			getServletContext().getRequestDispatcher("/documentLookup.html").forward(request, response);
//		} catch ( Exception e) {
//			conn.rollback();
//			request.setAttribute("ansi_upload_message","System Error: " + e.getMessage() + ". Contact Support");
//			getServletContext().getRequestDispatcher("/documentLookup.html").forward(request, response);
//		}
//	}

//	private Document makeDocument(List<FileItem> formItems) throws ParseException, Exception {
//		Document document = new Document();
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//		for ( FileItem item : formItems ) {
//			String fieldName = item.getFieldName();
//			if ( item.isFormField() ) {
//				String value = item.getString();
//				if ( fieldName.equalsIgnoreCase(DocumentUploadRequest.DOCUMENT_ID)) {
//					// this is an add -- no doc id needed
//				} else if ( fieldName.equalsIgnoreCase(DocumentUploadRequest.DESCRIPTION)) {
//					document.setDescription(value);
//				} else if (fieldName.equalsIgnoreCase(DocumentUploadRequest.DOCUMENT_DATE)) {
//					Date documentDate = sdf.parse(value);
//					document.setDocumentDate(documentDate);
//				} else if (fieldName.equalsIgnoreCase(DocumentUploadRequest.DOCUMENT_TYPE)) {
//					document.setXrefType(value);
//				} else if (fieldName.equalsIgnoreCase(DocumentUploadRequest.EXPIRATION_DATE)) {
//					if ( ! StringUtils.isBlank(value)) {
//						Date expirationDate = sdf.parse(value);
//						document.setExpirationDate(expirationDate);
//					}
//				} else if (fieldName.equalsIgnoreCase(DocumentUploadRequest.XREF_ID)) {
//					document.setXrefId(Integer.valueOf(value));
//				} else if (fieldName.equalsIgnoreCase(DocumentUploadRequest.XREF_NAME)) {
//					// ignore this one -- it's just there to make th autocomplete happy				
//				} else {
//					throw new Exception("Invalid upload field " + fieldName);
//				}
//			} else {
//				if ( fieldName.equalsIgnoreCase(DocumentUploadRequest.FILE_SELECT)) {
//					byte[] bytes = IOUtils.toByteArray(item.getInputStream());
//					Blob content = new SerialBlob(bytes);
//					document.setContent(content);
//				} else {
//					throw new Exception("Invalid upload field " + fieldName);
//				}
//			}
//		}
//		return document;
//	}



	
}
