package com.ansi.scilla.web.document.response;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.ansi.scilla.common.db.Document;
import com.ansi.scilla.web.common.response.MessageResponse;
import com.fasterxml.jackson.annotation.JsonFormat;

public class DocumentUploadResponse extends MessageResponse {

	private static final long serialVersionUID = 1L;
	
	public static final String DOCUMENT_ID = "documentId";
	public static final String DESCRIPTION = "description";
	public static final String DOCUMENT_DATE = "documentDate";
	public static final String EXPIRATION_DATE = "expirationDate";
	

	private Integer documentId;
	private String description;
	private Date documentDate;
	private Date expirationDate;
	
	
	public DocumentUploadResponse() {
		super();
	}
	
	public DocumentUploadResponse(Integer documentId, ResultSet rs) throws SQLException {
		this();
		this.documentId = documentId;
		this.description = rs.getString(Document.DESCRIPTION);
		this.documentDate = new Date(rs.getDate(Document.DOCUMENT_DATE).getTime());
		java.sql.Date expireDate = rs.getDate(Document.EXPIRATION_DATE);
		if ( expireDate != null ) {
			this.expirationDate = new Date(expireDate.getTime());
		}		
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
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd", timezone="America/Chicago")
	public Date getDocumentDate() {
		return documentDate;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd", timezone="America/Chicago")
	public void setDocumentDate(Date documentDate) {
		this.documentDate = documentDate;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd", timezone="America/Chicago")
	public Date getExpirationDate() {
		return expirationDate;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd", timezone="America/Chicago")
	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}
	
	
	

}
