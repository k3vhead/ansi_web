package com.ansi.scilla.web.response.ticketTable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.web.response.quoteTable.QuoteTableReturnItem;

public class TicketTableJsonResponse {

    int recordsTotal;

    int recordsFiltered;

    int draw;

    String columns;

    List<TicketTableReturnItem> data;

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

    public void setData(List<TicketTableReturnItem> data) {
        this.data = data;
    }

}
