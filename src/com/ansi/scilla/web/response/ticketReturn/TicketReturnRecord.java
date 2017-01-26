package com.ansi.scilla.web.response.ticketReturn;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.BeanUtilsBean;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.db.Ticket;
import com.ansi.scilla.common.jobticket.TicketStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.thewebthing.commons.lang.BeanUtils;

/**
 * 
 * @author ggroce
 *
 */

public class TicketReturnRecord extends ApplicationObject {
	private static final long serialVersionUID = 1L;
	private BigDecimal actDl;
	private BigDecimal actDlPct;
	private BigDecimal actPricePerCleaning;
	private Integer billSheet;
	private Integer customerSignature;
	private Integer mgrApproval;
	private Date processDate;
	private String processNotes;
	private String status;
	private Integer ticketId;
	private List<NextAllowedStatusItem> nextAllowedStatusList;
	
	
	public TicketReturnRecord() {
		super();
	}

	public TicketReturnRecord(Ticket ticket) throws IllegalAccessException, InvocationTargetException {
		this();
		System.out.println("ticket:" + ticket);
		BeanUtilsBean beanUtilsBean = BeanUtilsBean.getInstance();
		beanUtilsBean.getConvertUtils().register(
		    new org.apache.commons.beanutils.converters.BigDecimalConverter(null),
		BigDecimal.class);
		BeanUtils.copyProperties(this, ticket);
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

	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy")
	public void setProcessDate(Date processDate) {
		this.processDate = processDate;
	}
	public Date getProcessDate() {
		return this.processDate;
	}

	public void setProcessNotes(String processNotes) {
		this.processNotes = processNotes;
	}
	public String getProcessNotes() {
		return this.processNotes;
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