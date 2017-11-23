package com.ansi.scilla.web.invoice.response;

import java.util.List;

public class InvoiceLookupResponse {

    int recordsTotal;

    int recordsFiltered;

    int draw;

    String columns;

    List<InvoiceLookupResponseItem> data;

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

    public List<InvoiceLookupResponseItem> getData() {
        return data;
    }

    public void setData(List<InvoiceLookupResponseItem> data) {
        this.data = data;
    }

}
