package com.ansi.scilla.web.qotd.response;

import java.io.Serializable;

import com.ansi.scilla.web.common.response.MessageResponse;
import com.ansi.scilla.web.common.response.WebMessages;

/**
 * Used to return the MOTD to the client
 * 
 * @author dclewis
 *
 */
public class MotdResponse extends MessageResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private String motd;
	private String source;

	public MotdResponse() {
		super();
	}

	public MotdResponse(String motd, String source) {
		super(new WebMessages());
		this.motd = motd;
		this.source = source;
	}

	public String getMotd() {
		return motd;
	}

	public void setMotd(String motd) {
		this.motd = motd;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}
	
}
