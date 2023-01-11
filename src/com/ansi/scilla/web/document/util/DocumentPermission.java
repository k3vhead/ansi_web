package com.ansi.scilla.web.document.util;

import java.util.HashMap;
import java.util.Map;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.document.DocumentType;
import com.ansi.scilla.common.utils.Permission;

/**
 * This is a wrapper for the DocumentType enum that assigns a permission for accessing a particular 
 * document type. The default value is "no permission required", signified by returning a null value.
 * 
 * @author dclewis
 *
 */
public final class DocumentPermission extends ApplicationObject {

	private static final long serialVersionUID = 1L;
	
	private Map<DocumentType, Permission> permissionMap;
	private static DocumentPermission instance;
		
	
	private DocumentPermission() {
		super();
		this.permissionMap = new HashMap<DocumentType, Permission>();
		for ( DocumentType docType : DocumentType.values() ) {
			this.permissionMap.put(docType, (Permission)null);
		}
		this.permissionMap.put(DocumentType.SIGNED_CONTRACT, Permission.QUOTE_READ);
		this.permissionMap.put(DocumentType.TAX_EXEMPT, Permission.ADDRESS_READ);
	}
	
	private Permission getPermission(DocumentType documentType ) {
		return this.permissionMap.get(documentType);
	}
	
	public static Permission get(DocumentType documentType) {
		if ( instance == null ) {
			instance = new DocumentPermission();
		}
		return instance.getPermission(documentType);
	}

}
