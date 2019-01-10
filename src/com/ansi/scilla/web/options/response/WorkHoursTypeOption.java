package com.ansi.scilla.web.options.response;

import com.ansi.scilla.common.claims.WorkHoursType;

public class WorkHoursTypeOption extends WebOption  {
	private static final long serialVersionUID = 1L;
	private String code;
	private String name;

	public WorkHoursTypeOption(WorkHoursType workHoursType) {
		super();
		this.code = workHoursType.name();
		this.display = workHoursType.getDescription() + " (" + workHoursType.name() + ")";
		this.name = workHoursType.name();
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
