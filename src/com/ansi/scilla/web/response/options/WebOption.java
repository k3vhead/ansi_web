package com.ansi.scilla.web.response.options;

import com.ansi.scilla.common.ApplicationObject;

public abstract class WebOption extends ApplicationObject implements Comparable<WebOption> {

	private static final long serialVersionUID = 1L;

	protected String display;
	
	public String getDisplay() {
		return display;
	}

	public void setDisplay(String display) {
		this.display = display;
	}

	@Override
	public int compareTo(WebOption o) {
		return getDisplay().compareTo(o.getDisplay());
	}
}
