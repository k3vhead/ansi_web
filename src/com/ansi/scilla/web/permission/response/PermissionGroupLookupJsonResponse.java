package com.ansi.scilla.web.permission.response;

import java.util.List;

import com.ansi.scilla.web.permission.query.PermissionGroupSearchResult;

public class PermissionGroupLookupJsonResponse {

	private int recordsTotal;
	private int recordsFiltered;
	private int draw;
	private String columns;

	private List<PermissionGroupSearchResult> data;

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

	public List<PermissionGroupSearchResult> getData() {
		return data;
	}

	

	public void setData(List<PermissionGroupSearchResult> data) {
		this.data = data;
	}
}
