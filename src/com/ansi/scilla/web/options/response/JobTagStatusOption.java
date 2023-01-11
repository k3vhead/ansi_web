package com.ansi.scilla.web.options.response;

import com.ansi.scilla.common.jobticket.JobTagStatus;;

public class JobTagStatusOption extends WebOption  {
	private static final long serialVersionUID = 1L;
	private String code;
	private String name;

	public JobTagStatusOption(JobTagStatus tagStatus) {
		super();
		this.code = tagStatus.name();
		this.display = tagStatus.name();
		this.name = tagStatus.name();
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	

}
