package com.ansi.scilla.web.user.response;

import java.util.ArrayList;
import java.util.List;

import com.ansi.scilla.common.queries.UserLookupItem;

public class UserLookupJsonResponse {

	private int recordsTotal;
	private int recordsFiltered;
	private int draw;
	private String columns;

	private List<UserLookupReturnItem> data;

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

	public List<UserLookupReturnItem> getData() {
		return data;
	}

	public void makeData(List<UserLookupItem> itemList) throws Exception {
		this.data = new ArrayList<UserLookupReturnItem>();
		for ( UserLookupItem item : itemList) {
			this.data.add(new UserLookupReturnItem(item));
		}
	}

	public void setData(List<UserLookupReturnItem> data) {
		this.data = data;
	}
}
