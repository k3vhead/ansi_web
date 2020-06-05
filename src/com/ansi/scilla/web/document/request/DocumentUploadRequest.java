package com.ansi.scilla.web.document.request;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.common.document.DocumentType;
import com.ansi.scilla.web.common.request.AbstractRequest;
import com.ansi.scilla.web.common.request.RequestValidator;
import com.ansi.scilla.web.common.response.WebMessages;

public class DocumentUploadRequest extends AbstractRequest {

	private static final long serialVersionUID = 1L;
	
	public static final String DOCUMENT_ID = "documentId";
	public static final String DESCRIPTION = "description";
	public static final String DOCUMENT_DATE = "documentDate";
	public static final String EXPIRATION_DATE = "expirationDate";
	public static final String DOCUMENT_TYPE = "documentType";
	public static final String XREF_ID = "xrefId";
	public static final String FILE_SELECT = "fileSelect";
	public static final String XREF_NAME = "xrefName";

	private Integer documentId;
	private String description;
	private Date documentDate;
	private Date expirationDate;
	private String documentType;
	private Integer xrefId;
	private String fileSelect;
	private String xrefName;
	private Logger logger;
	
	
	public DocumentUploadRequest() {
		super();
		this.logger = LogManager.getLogger(this.getClass());

	}
	public Integer getDocumentId() {
		return documentId;
	}
	public void setDocumentId(Integer documentId) {
		this.documentId = documentId;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Date getDocumentDate() {
		return documentDate;
	}
	public void setDocumentDate(Date documentDate) {
		this.documentDate = documentDate;
	}
	public Date getExpirationDate() {
		return expirationDate;
	}
	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}
	public String getDocumentType() {
		return documentType;
	}
	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}
	public Integer getXrefId() {
		return xrefId;
	}
	public void setXrefId(Integer xrefId) {
		this.xrefId = xrefId;
	}
	public String getFileSelect() {
		return fileSelect;
	}
	public void setFileSelect(String fileSelect) {
		this.fileSelect = fileSelect;
	}	
	public String getXrefName() {
		return xrefName;
	}
	public void setXrefName(String xrefName) {
		this.xrefName = xrefName;
	}

	private WebMessages validateCommon(Connection conn) throws SQLException {
		WebMessages webMessages = new WebMessages();
		RequestValidator.validateString(webMessages, DESCRIPTION, this.description, 256, true);
		RequestValidator.validateDate(webMessages, DOCUMENT_DATE, documentDate, true, (Date)null, (Date)null);
		RequestValidator.validateDate(webMessages, EXPIRATION_DATE, expirationDate, false, (Date)null, (Date)null);
		return webMessages;
		
	}
	
	public WebMessages validateUpload(Connection conn) throws SQLException {
		WebMessages webMessages = validateCommon(conn);
		RequestValidator.validateString(webMessages, FILE_SELECT, this.fileSelect, true);
		RequestValidator.validateDocumentType(webMessages, DOCUMENT_TYPE, documentType, true);
		if (! webMessages.containsKey(DOCUMENT_TYPE) ) {
			logger.log(Level.DEBUG, "Validating xrefid");
			DocumentType documentType = DocumentType.valueOf(this.documentType);
			RequestValidator.validateDocumentTypeXref(conn, webMessages, XREF_ID, documentType, xrefId, true);
		}
		return webMessages;
	}
	
	
	public WebMessages validateEdit(Connection conn) throws Exception {
		WebMessages webMessages = validateCommon(conn);
		RequestValidator.validateDocumentId(conn, webMessages, DOCUMENT_ID, documentId, true);
		return webMessages;
	}
	
	
}
