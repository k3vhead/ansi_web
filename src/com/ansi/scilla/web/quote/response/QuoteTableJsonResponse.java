package com.ansi.scilla.web.quote.response;

import java.util.List;

public class QuoteTableJsonResponse {

    int recordsTotal;

    int recordsFiltered;

    int draw;

    String columns;

    List<QuoteTableReturnItem> data;

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

    public List<QuoteTableReturnItem> getData() {
        return data;
    }

    public void setData(List<QuoteTableReturnItem> data) {
        this.data = data;
    }

}
