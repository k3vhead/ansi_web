package com.ansi.scilla.web.options.response;

import com.ansi.scilla.common.document.DocumentType;;

public class DocumentTypeOption extends WebOption  {
	private static final long serialVersionUID = 1L;
	private String code;
	private String name;

	public DocumentTypeOption(DocumentType documentType) {
		super();
		this.code = documentType.name();
		this.display = documentType.description();
		this.name = documentType.name();
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	

}
