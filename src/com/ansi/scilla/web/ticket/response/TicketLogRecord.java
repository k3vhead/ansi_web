package com.ansi.scilla.web.ticket.response;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.PropertyUtils;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.db.ViewTicketLog;
import com.ansi.scilla.common.jobticket.TicketStatus;
import com.ansi.scilla.common.jsonFormat.AnsiCurrencyFormatter;
import com.ansi.scilla.common.jsonFormat.AnsiDateFormatter;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * 
 * @author ggroce
 *
 */

public class TicketLogRecord extends ApplicationObject {
	private static final long serialVersionUID = 1L;
	private Date startDate;
	private String status;
	private Integer ticketId;
	
	
	public TicketLogRecord() {
		super();
	}

	public TicketLogRecord(ViewTicketLog viewTicketLog) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		this();
		System.out.println("viewTicketLog:" + viewTicketLog);
		BeanUtilsBean beanUtilsBean = BeanUtilsBean.getInstance();
		beanUtilsBean.getConvertUtils().register(
		    new org.apache.commons.beanutils.converters.BigDecimalConverter(null),
		BigDecimal.class);
		PropertyUtils.copyProperties(this, viewTicketLog);
	}


	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	@JsonSerialize(using=AnsiDateFormatter.class)
	public Date getStartDate() {
		return this.startDate;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	public String getStatus() {
		return this.status;
	}

	public void setTicketId(Integer ticketId) {
		this.ticketId = ticketId;
	}
	public Integer getTicketId() {
		return this.ticketId;
	}

}
