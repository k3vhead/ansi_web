package com.ansi.scilla.web.response.options;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.jobticket.JobFrequency;

public class JobFrequencyOption extends ApplicationObject {
	private static final long serialVersionUID = 1L;
	private String abbrev;
	private String display;

	public JobFrequencyOption(JobFrequency jobFrequency) {
		this.abbrev = jobFrequency.abbrev();
		this.display = jobFrequency.display();
	}

	public String getAbbrev() {
		return abbrev;
	}

	public void setAbbrev(String abbrev) {
		this.abbrev = abbrev;
	}

	public String getDisplay() {
		return display;
	}

	public void setDisplay(String display) {
		this.display = display;
	}

	
}
