package com.ansi.scilla.web.knowledgeBase.response;

import java.sql.Connection;

import com.ansi.scilla.common.db.KnowledgeBase;
import com.ansi.scilla.web.common.response.MessageResponse;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class KnowledgeBaseDetailResponse extends MessageResponse {

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
	
	public KnowledgeBaseDetailResponse() {
		super();
	}
	
	public KnowledgeBaseDetailResponse(Connection conn, String kbTagName, String languageCode) throws RecordNotFoundException, Exception {
		this();
		this.kbTagName = kbTagName;
		this.languageCode = languageCode;
		KnowledgeBase kb = new KnowledgeBase();
		kb.setKbKey(kbTagName);
		kb.setKbLanguage(languageCode);
		kb.selectOne(conn);
		this.kbTitle = kb.getKbTitle();
		this.kbContent = kb.getKbContent();
		this.kbStatus = kb.getKbStatus();
	}
	
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

	
	
	
	
	
}
