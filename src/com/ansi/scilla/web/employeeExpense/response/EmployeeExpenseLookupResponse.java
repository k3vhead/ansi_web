package com.ansi.scilla.web.employeeExpense.response;

import java.io.Serializable;
import java.util.List;

import com.ansi.scilla.web.common.response.MessageResponse;

public class EmployeeExpenseLookupResponse extends MessageResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int recordsTotal;
	private int recordsFiltered;
	private int draw;
	private String columns;

	private List<EmployeeExpenseResponseItem> data;

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

	public List<EmployeeExpenseResponseItem> getData() {
		return data;
	}

	

	public void setData(List<EmployeeExpenseResponseItem> data) {
		this.data = data;
	}

}
