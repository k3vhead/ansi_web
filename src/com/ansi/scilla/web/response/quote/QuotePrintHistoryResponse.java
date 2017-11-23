package com.ansi.scilla.web.response.quote;

import java.util.List;

import com.ansi.scilla.common.queries.QuotePrintHistoryItem;
import com.ansi.scilla.web.address.response.AddressDetail;
import com.ansi.scilla.web.response.MessageResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

public class QuotePrintHistoryResponse extends MessageResponse {

	private static final long serialVersionUID = 1L;

	private AddressDetail jobSite;
	private List<QuotePrintHistoryItem> quotePrintHistoryItemList;
	
	public QuotePrintHistoryResponse() {
		super();
	}

	public QuotePrintHistoryResponse(AddressDetail jobSite, List<QuotePrintHistoryItem> quotePrintHistoryItemList) {
		this();
		this.jobSite = jobSite;
		this.quotePrintHistoryItemList = quotePrintHistoryItemList;
	}

	public AddressDetail getJobSite() {
		return jobSite;
	}

	public void setJobSite(AddressDetail jobSite) {
		this.jobSite = jobSite;
	}

	@JsonProperty("history")
	public List<QuotePrintHistoryItem> getQuotePrintHistoryItemList() {
		return quotePrintHistoryItemList;
	}
	@JsonProperty("history")
	public void setQuotePrintHistoryItemList(List<QuotePrintHistoryItem> quotePrintHistoryItemList) {
		this.quotePrintHistoryItemList = quotePrintHistoryItemList;
	}
	
}
