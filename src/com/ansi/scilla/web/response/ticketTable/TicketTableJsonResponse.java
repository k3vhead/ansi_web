package com.ansi.scilla.web.response.ticketTable;

import java.util.ArrayList;
import java.util.List;

import com.ansi.scilla.common.queries.TicketLookupSearchItem;

public class TicketTableJsonResponse {

	private int recordsTotal;
	private int recordsFiltered;
	private int draw;
	private String columns;

	private List<TicketTableReturnItem> data;

	public int getRecordsTotal() {
		return recordsTotal;
	}

	public void setRecordsTotal(int recordsTotal) {
		this.recordsTotal = recordsTotal;
	}

	public int getRecordsFiltered() {
		return recordsFiltered;
	}

	public void setRecordsFiltered(int recordsFiltered) {
		this.recordsFiltered = recordsFiltered;
	}

	public int getDraw() {
		return draw;
	}

	public void setDraw(int draw) {
		this.draw = draw;
	}

	public String getColumns() {
		return columns;
	}

	public void setColumns(String columns) {
		this.columns = columns;
	}

	public List<TicketTableReturnItem> getData() {
		return data;
	}

	public void makeData(List<TicketLookupSearchItem> itemList) throws Exception {
		this.data = new ArrayList<TicketTableReturnItem>();
		for ( TicketLookupSearchItem item : itemList) {
			this.data.add(new TicketTableReturnItem(item));
		}
	}

	public void setData(List<TicketTableReturnItem> data) {
		this.data = data;
	}
}
