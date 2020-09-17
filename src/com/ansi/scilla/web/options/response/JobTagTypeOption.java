package com.ansi.scilla.web.options.response;

import com.ansi.scilla.common.jobticket.JobTagType;;

public class JobTagTypeOption extends WebOption  {
	private static final long serialVersionUID = 1L;
	private String code;
	private String name;

	public JobTagTypeOption(JobTagType tagType) {
		super();
		this.code = tagType.name();
		this.display = tagType.display();
		this.name = tagType.name();
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
