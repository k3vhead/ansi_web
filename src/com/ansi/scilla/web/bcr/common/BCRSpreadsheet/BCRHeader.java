package com.ansi.scilla.web.bcr.common.BCRSpreadsheet;

import java.lang.reflect.Field;

import org.apache.poi.xssf.usermodel.XSSFCellStyle;

import com.ansi.scilla.common.ApplicationObject;

/**
 * Represents one column of the spreadsheet tab. 
 * The Field is the BCRRow source for the data
 *
 */
public class BCRHeader extends ApplicationObject {
	private static final long serialVersionUID = 1L;
	public Field field;
	public String headerName;
	public Integer columnWidth;
	public XSSFCellStyle cellStyle;
	
	public BCRHeader(Field field, String headerName, Integer columnWidth, XSSFCellStyle cellStyle) {
		super();
		this.field = field;
		this.headerName = headerName;
		this.columnWidth = columnWidth;
		this.cellStyle = cellStyle;
	}
}
