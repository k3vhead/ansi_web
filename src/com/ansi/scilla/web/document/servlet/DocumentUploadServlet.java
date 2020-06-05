package com.ansi.scilla.web.document.servlet;

import java.io.File;
import java.io.IOException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.rowset.serial.SerialBlob;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;

import com.ansi.scilla.common.db.Document;
import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.response.SuccessMessage;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.utils.AnsiURL;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.Permission;
import com.ansi.scilla.web.document.request.DocumentUploadRequest;
import com.ansi.scilla.web.document.response.DocumentUploadResponse;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;

public class DocumentUploadServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;

	public static final String REALM = "documentUpload";





	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		Connection conn = null;
		try {
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);
			SessionData sessionData = AppUtils.validateSession(request, Permission.DOCUMENTS_WRITE);

			if ( !ServletFileUpload.isMultipartContent(request) ) {
				logger.log(Level.DEBUG, "not multipart");
				request.setAttribute("ansi_upload_message","Not a MultiPart");
				getServletContext().getRequestDispatcher("/documentLookup.html").forward(request, response);
				return;
			}

			DiskFileItemFactory factory = new DiskFileItemFactory();
			factory.setRepository(new File("/tmp"));
			ServletFileUpload upload = new ServletFileUpload(factory);

			@SuppressWarnings("unchecked")
			List<FileItem> formItems = upload.parseRequest(request);
			if ( formItems != null && formItems.size() > 0 ) {
				logger.log(Level.DEBUG, "formItems");
				Document document = makeDocument(formItems);

				if ( document.getDocumentId() == null ) {
					doUpload(conn, request, response, sessionData, document);
				} else {
					doUpdate(conn, request, response, sessionData, document);
				}
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
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Connection conn = null;
		try {
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);
			AppUtils.validateSession(request, Permission.DOCUMENTS_WRITE);
			AnsiURL url = new AnsiURL(request, REALM, (String[])null);
			Integer documentId = url.getId();
			
			if ( documentId == null ) {
				super.sendNotFound(response);
			} else {
				String sql = "delete from document where document_id=?";
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setInt(1, Integer.valueOf(documentId));
				ps.executeUpdate();
				conn.commit();
				DocumentUploadResponse data = new DocumentUploadResponse();
				data.setWebMessages(new SuccessMessage());
				super.sendResponse(conn, response, ResponseCode.SUCCESS, data);
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








	private void doUpload(Connection conn, HttpServletRequest request, HttpServletResponse response, SessionData sessionData, Document document) throws TimeoutException, NotAllowedException, ExpiredLoginException, Exception {
		logger.log(Level.DEBUG, "doing upload");
		try {
			Date today = new Date();
			document.setAddedBy(sessionData.getUser().getUserId());
			document.setUpdatedBy(sessionData.getUser().getUserId());
			document.setAddedDate(today);
			document.setUpdatedDate(today);
			document.insertWithKey(conn);
			conn.commit();

			request.setAttribute("ansi_upload_message","Success");
			getServletContext().getRequestDispatcher("/documentLookup.html").forward(request, response);
		} catch ( Exception e) {
			conn.rollback();
			AppUtils.logException(e);
			request.setAttribute("ansi_upload_message","System Error: " + e.getMessage() + ". Contact Support");
			getServletContext().getRequestDispatcher("/documentLookup.html").forward(request, response);
		}
	}



	private void doUpdate(Connection conn, HttpServletRequest request, HttpServletResponse response, SessionData sessionData, Document document) throws ServletException, IOException, SQLException {
		try {

			logger.log(Level.DEBUG, "formItems");
			Date today = new Date();
			String sql = "update document set " + Document.DESCRIPTION + "=?, " +
					Document.DOCUMENT_DATE + "=?, " + 
					Document.EXPIRATION_DATE + "=?, " +
					Document.UPDATED_DATE + "=?, " +
					Document.UPDATED_BY + "=? " +
					"where  " + Document.DOCUMENT_ID + "=?";
			logger.log(Level.DEBUG, sql);
			PreparedStatement ps = conn.prepareStatement(sql);
			int n = 1;
			ps.setString(n, document.getDescription());
			n++;
			ps.setDate(n, new java.sql.Date(document.getDocumentDate().getTime()));
			n++;
			if ( document.getExpirationDate() == null ) {
				ps.setNull(n, Types.DATE);
			} else {
				ps.setDate(n, new java.sql.Date(document.getExpirationDate().getTime()));
			}
			n++;
			ps.setDate(n, new java.sql.Date(today.getTime()));
			n++;
			ps.setInt(n, sessionData.getUser().getUserId());
			n++;
			ps.setInt(n, document.getDocumentId());
			ps.executeUpdate();
			conn.commit();

			request.setAttribute("ansi_upload_message","Success");
			getServletContext().getRequestDispatcher("/documentLookup.html").forward(request, response);
		} catch ( Exception e) {
			conn.rollback();
			AppUtils.logException(e);
			request.setAttribute("ansi_upload_message","System Error: " + e.getMessage() + ". Contact Support");
			getServletContext().getRequestDispatcher("/documentLookup.html").forward(request, response);
		}		
	}





	private Document makeDocument(List<FileItem> formItems) throws ParseException, Exception {
		Document document = new Document();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		for ( FileItem item : formItems ) {
			String fieldName = item.getFieldName();
			if ( item.isFormField() ) {
				String value = item.getString();
				if ( fieldName.equalsIgnoreCase(DocumentUploadRequest.DOCUMENT_ID)) {
					if ( ! StringUtils.isBlank(value) ) {  //id is only for updating documents
						document.setDocumentId(Integer.valueOf(value));
					}
				} else if ( fieldName.equalsIgnoreCase(DocumentUploadRequest.DESCRIPTION)) {
					document.setDescription(value);
				} else if (fieldName.equalsIgnoreCase(DocumentUploadRequest.DOCUMENT_DATE)) {
					Date documentDate = sdf.parse(value);
					document.setDocumentDate(documentDate);
				} else if (fieldName.equalsIgnoreCase(DocumentUploadRequest.DOCUMENT_TYPE)) {
					document.setXrefType(value);
				} else if (fieldName.equalsIgnoreCase(DocumentUploadRequest.EXPIRATION_DATE)) {
					if ( ! StringUtils.isBlank(value)) {
						Date expirationDate = sdf.parse(value);
						document.setExpirationDate(expirationDate);
					}
				} else if (fieldName.equalsIgnoreCase(DocumentUploadRequest.XREF_ID)) {
					if ( ! StringUtils.isBlank(value) ) {  //xref is only for new documents
						document.setXrefId(Integer.valueOf(value));
					}
				} else if (fieldName.equalsIgnoreCase(DocumentUploadRequest.XREF_NAME)) {
					// ignore this one -- it's just there to make th autocomplete happy				
				} else {
					throw new Exception("Invalid upload field " + fieldName);
				}
			} else {
				if ( fieldName.equalsIgnoreCase(DocumentUploadRequest.FILE_SELECT)) {
					byte[] bytes = IOUtils.toByteArray(item.getInputStream());
					Blob content = new SerialBlob(bytes);
					document.setContent(content);
				} else {
					throw new Exception("Invalid upload field " + fieldName);
				}
			}
		}
		return document;
	}




}
