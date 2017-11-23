package com.ansi.scilla.web.contact.response;

import java.util.List;

import com.ansi.scilla.common.ApplicationObject;

public class ContactTableResponse extends ApplicationObject {

	private static final long serialVersionUID = 1L;

    int recordsTotal;
    int recordsFiltered;
    int draw;
    String columns;
	private List<ContactTableResponseItem> data;
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
	public List<ContactTableResponseItem> getData() {
		return data;
	}
	public void setData(List<ContactTableResponseItem> data) {
		this.data = data;
	}

	
	
}
