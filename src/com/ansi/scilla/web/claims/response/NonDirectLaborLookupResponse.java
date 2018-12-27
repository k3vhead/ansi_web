package com.ansi.scilla.web.claims.response;

import java.util.List;

import com.ansi.scilla.web.claims.query.NonDirectLaborSearchResult;

public class NonDirectLaborLookupResponse {

	private int recordsTotal;
	private int recordsFiltered;
	private int draw;
	private String columns;

	private List<NonDirectLaborSearchResult> data;

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

	public List<NonDirectLaborSearchResult> getData() {
		return data;
	}

	

	public void setData(List<NonDirectLaborSearchResult> data) {
		this.data = data;
	}
}
