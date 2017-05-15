package com.ansi.scilla.web.actionForm;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

public class MessageForm extends AbstractActionForm {

	private static final long serialVersionUID = 1L;

	public static final String MESSAGE = "message";
	
	private String message;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		// TODO Auto-generated method stub
		super.reset(mapping, request);
		this.message = null;
	}
	

	@Override
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
		return new ActionErrors();
	}
	
}
