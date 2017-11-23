package com.ansi.scilla.web.code.response;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.PropertyUtils;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.db.Code;

public class CodeResponseRecord extends ApplicationObject implements Comparable<CodeResponseRecord> {

	private static final long serialVersionUID = 1L;

	private String description;
	private String displayValue;
	private String fieldName;
	private Integer seq;
	private Integer status;
	private String tableName;
	private String value;
	
	public CodeResponseRecord(Code code) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		super();
		PropertyUtils.copyProperties(this, code);
	}
	
	public String getDescription() {		
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getDisplayValue() {
		return displayValue;
	}
	public void setDisplayValue(String displayValue) {
		this.displayValue = displayValue;
	}
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public Integer getSeq() {
		return seq;
	}
	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	@Override
	public int compareTo(CodeResponseRecord o) {
		int ret = this.getTableName().compareTo(o.getTableName());
		if ( ret == 0 ) {
			ret = this.fieldName.compareTo(o.getFieldName());
		}
		if ( ret == 0 ) {
			ret = this.seq.compareTo(o.getSeq());
		}
		return 0;
	}

	
}
