package com.ansi.scilla.web.ticket.response;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.PropertyUtils;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.db.Ticket;
import com.ansi.scilla.common.jobticket.TicketStatus;
import com.ansi.scilla.common.jsonFormat.AnsiCurrencyFormatter;
import com.ansi.scilla.common.jsonFormat.AnsiDateFormatter;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * 
 * @author ggroce
 *
 */

public class TicketRecord extends ApplicationObject {
	private static final long serialVersionUID = 1L;
	private BigDecimal actDl;
	private BigDecimal actDlPct;
	private BigDecimal actPricePerCleaning;
	private Integer billSheet;
	private Integer customerSignature;
	private Integer mgrApproval;
	private Date processDate;
	private String processNotes;
	private Date startDate;
	private String status;
	private Integer ticketId;
	private List<NextAllowedStatusItem> nextAllowedStatusList;
	
	
	public TicketRecord() {
		super();
	}

	public TicketRecord(Ticket ticket) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		this();
		System.out.println("ticket:" + ticket);
		BeanUtilsBean beanUtilsBean = BeanUtilsBean.getInstance();
		beanUtilsBean.getConvertUtils().register(
		    new org.apache.commons.beanutils.converters.BigDecimalConverter(null),
		BigDecimal.class);
		PropertyUtils.copyProperties(this, ticket);
		this.nextAllowedStatusList = getNextAllowedStatusList(ticket.getStatus());	
	}

	public List<NextAllowedStatusItem> getNextAllowedStatusList( String status ) {
		String statusId = status.substring(0,1);
		TicketStatus ticketStatus = TicketStatus.lookup(statusId);
		System.out.println("ticketStatus:" + ticketStatus);
		List<NextAllowedStatusItem> nextOptions = new ArrayList<NextAllowedStatusItem>();
		if (ticketStatus != null){
			List<TicketStatus> nextValues = ticketStatus.nextValues();
			for ( TicketStatus option : nextValues) {
				nextOptions.add(new NextAllowedStatusItem(option));
			}
		}
		return nextOptions;
	}
	
	public class NextAllowedStatusItem extends ApplicationObject {
		private static final long serialVersionUID = 1L;
		private String code;
		private String display;
		
		public NextAllowedStatusItem(TicketStatus status) {
			super();
			this.code = status.code();
			this.display = status.display();
		}

		public String getcode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getDisplay() {
			return display;
		}

		public void setDisplay(String display) {
			this.display = display;
		}

	}

	public void setActDl(BigDecimal actDl) {
		this.actDl = actDl;
	}
	public BigDecimal getActDl() {
		return this.actDl;
	}

	public void setActDlPct(BigDecimal actDlPct) {
		this.actDlPct = actDlPct;
	}
	public BigDecimal getActDlPct() {
		return this.actDlPct;
	}

	public void setActPricePerCleaning(BigDecimal actPricePerCleaning) {
		this.actPricePerCleaning = actPricePerCleaning;
	}
	@JsonSerialize(using=AnsiCurrencyFormatter.class)
	public BigDecimal getActPricePerCleaning() {
		return this.actPricePerCleaning;
	}

	public void setBillSheet(Integer billSheet) {
		this.billSheet = billSheet;
	}
	public Integer getBillSheet() {
		return this.billSheet;
	}

	public void setCustomerSignature(Integer customerSignature) {
		this.customerSignature = customerSignature;
	}
	public Integer getCustomerSignature() {
		return this.customerSignature;
	}

	public void setMgrApproval(Integer mgrApproval) {
		this.mgrApproval = mgrApproval;
	}
	public Integer getMgrApproval() {
		return this.mgrApproval;
	}

	
	public void setProcessDate(Date processDate) {
		this.processDate = processDate;
	}
	@JsonSerialize(using=AnsiDateFormatter.class)
	public Date getProcessDate() {
		return this.processDate;
	}

	public void setProcessNotes(String processNotes) {
		this.processNotes = processNotes;
	}
	public String getProcessNotes() {
		return this.processNotes;
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

	public void setNextAllowedStatusList(List<NextAllowedStatusItem> nextAllowedStatusList) {
		this.nextAllowedStatusList = nextAllowedStatusList;
	}
	public List<NextAllowedStatusItem> getNextAllowedStatusList() {
		return this.nextAllowedStatusList;
	}

}
