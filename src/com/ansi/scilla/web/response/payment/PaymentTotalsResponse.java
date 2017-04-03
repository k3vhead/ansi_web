package com.ansi.scilla.web.response.payment;

import java.util.List;

public class PaymentTotalsResponse {

    int recordsTotal;

    int recordsFiltered;

    int draw;

    String columns;

    List<PaymentTotalsResponseItem> data;

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

    public List<PaymentTotalsResponseItem> getData() {
        return data;
    }

    public void setData(List<PaymentTotalsResponseItem> data) {
        this.data = data;
    }

}
