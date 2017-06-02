package com.ansi.scilla.web.response.options;

import com.ansi.scilla.common.jobticket.JobFrequency;

public class JobFrequencyOption extends WebOption  {
	private static final long serialVersionUID = 1L;
	private String abbrev;

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


	
}
