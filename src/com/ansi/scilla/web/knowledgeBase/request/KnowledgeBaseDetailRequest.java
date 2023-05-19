package com.ansi.scilla.web.knowledgeBase.request;

import java.sql.Connection;
import java.util.regex.Pattern;

import com.ansi.scilla.web.common.request.AbstractRequest;
import com.ansi.scilla.web.common.request.RequestValidator;
import com.ansi.scilla.web.common.response.WebMessages;

public class KnowledgeBaseDetailRequest extends AbstractRequest {

	private static final long serialVersionUID = 1L;

	public static final String LANGUAGE_CODE = "languageCode";
	public static final String KB_TITLE = "kbTitle";
	public static final String KB_TAG_NAME = "kbTagName";
	public static final String KB_CONTENT = "kbContent";
	public static final String KB_STATUS = "kbStatus";
		
	private String languageCode;
	private String kbTitle;
	private String kbTagName;
	private String kbContent;
	private Integer kbStatus;
	
	
	public String getLanguageCode() {
		return languageCode;
	}

	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}

	public String getKbTitle() {
		return kbTitle;
	}

	public void setKbTitle(String kbTitle) {
		this.kbTitle = kbTitle;
	}

	public String getKbTagName() {
		return kbTagName;
	}

	public void setKbTagName(String kbTagName) {
		this.kbTagName = kbTagName;
	}

	public String getKbContent() {
		return kbContent;
	}

	public void setKbContent(String kbContent) {
		this.kbContent = kbContent;
	}

	public Integer getKbStatus() {
		return kbStatus;
	}

	public void setKbStatus(Integer kbStatus) {
		this.kbStatus = kbStatus;
	}

	public WebMessages validate(Connection conn) throws Exception {
		WebMessages webMessages = new WebMessages();
		
		RequestValidator.validateString(webMessages, KB_TITLE, this.kbTitle, 128, true, (String)null);
		RequestValidator.validateStringFormat(webMessages, LANGUAGE_CODE, this.languageCode, Pattern.compile("^([A-Za-z]{2,3})(-?[A-Za-z]{2,3})?"), true, (String)null);		
		RequestValidator.validateString(webMessages, KB_CONTENT, this.kbContent, 2048, true, (String)null);
		RequestValidator.validateKnowledgeBaseTag(webMessages, KB_TAG_NAME, this.kbTagName, true);
		RequestValidator.validateInteger(webMessages, KB_STATUS, this.kbStatus, 0, 1, true);
		
		return webMessages;
	}
	
	
	
	
}
