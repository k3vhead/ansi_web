package com.ansi.scilla.web.employeeExpense.response;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.ansi.scilla.web.common.response.MessageResponse;

public class EmployeeExpenseListResponse extends MessageResponse implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private List<EmployeeExpenseResponseItem> itemList = new ArrayList<EmployeeExpenseResponseItem>();

	public List<EmployeeExpenseResponseItem> getItemList() {
		return itemList;
	}

	public void setItemList(List<EmployeeExpenseResponseItem> itemList) {
		this.itemList = itemList;
	}

}
