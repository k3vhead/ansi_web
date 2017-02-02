package com.ansi.scilla.web.response.ticket;

import java.math.BigDecimal;
import java.util.Date;

import com.ansi.scilla.common.ApplicationObject;

public class TicketDRVResponseItem extends ApplicationObject {

	private Integer ticketId;
	private String status;
	private String address1;
	private String name;
	private String city;
	private Date lastDone;
	private Date startDate;
	private Integer jobNum;
	private String frequency;
	private BigDecimal budget;
	private BigDecimal ppc;
	private String cod;
	
}
