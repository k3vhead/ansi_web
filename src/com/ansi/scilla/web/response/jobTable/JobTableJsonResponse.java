package com.ansi.scilla.web.response.jobTable;

import java.util.List;

public class JobTableJsonResponse {

    int recordsTotal;

    int recordsFiltered;

    int draw;

    String columns;

    List<JobTableReturnItem> data;

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

    public List<JobTableReturnItem> getData() {
        return data;
    }

    public void setData(List<JobTableReturnItem> data) {
        this.data = data;
    }

}
