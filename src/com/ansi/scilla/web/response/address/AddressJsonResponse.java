package com.ansi.scilla.web.response.address;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.web.response.address.AddressReturnItem;

public class AddressJsonResponse {

    int iTotalRecords;

    int iTotalDisplayRecords;

    String sEcho;

    String sColumns;

    List<AddressReturnItem> aaData;

    public int getiTotalRecords() {
    return iTotalRecords;
    }

    public void setiTotalRecords(int iTotalRecords) {
    this.iTotalRecords = iTotalRecords;
    }

    public int getiTotalDisplayRecords() {
    return iTotalDisplayRecords;
    }

    public void setiTotalDisplayRecords(int iTotalDisplayRecords) {
    this.iTotalDisplayRecords = iTotalDisplayRecords;
    }

    public String getsEcho() {
    return sEcho;
    }

    public void setsEcho(String sEcho) {
    this.sEcho = sEcho;
    }

    public String getsColumns() {
    return sColumns;
    }

    public void setsColumns(String sColumns) {
    this.sColumns = sColumns;
    }

    public List<AddressReturnItem> getAaData() {
        return aaData;
    }

    public void setAaData(List<AddressReturnItem> aaData) {
        this.aaData = aaData;
    }

}
