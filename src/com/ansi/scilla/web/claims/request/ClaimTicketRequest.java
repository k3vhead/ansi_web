package com.ansi.scilla.web.claims.request;

import java.sql.Connection;
import java.util.List;

import com.ansi.scilla.web.claims.common.ClaimTicketItem;
import com.ansi.scilla.web.common.request.AbstractRequest;
import com.ansi.scilla.web.common.response.WebMessages;

public class ClaimTicketRequest extends AbstractRequest {

	private static final long serialVersionUID = 1L;

	private List<ClaimTicketItem> claims;

	public List<ClaimTicketItem> getClaims() {
		return claims;
	}

	public void setClaims(List<ClaimTicketItem> claims) {
		this.claims = claims;
	}
	
	public WebMessages validateAdd(Connection conn) throws Exception {
		WebMessages webMessages = new WebMessages();
		for ( int i = 0; i < claims.size(); i++ ) {
			WebMessages rowMessages = claims.get(i).validateAdd(conn, i);
			if ( ! rowMessages.isEmpty() ) {
				webMessages.addAllMessages(rowMessages);
			}
		}
		
		return webMessages;
	}
}
