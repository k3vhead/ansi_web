package com.ansi.scilla.web.struts;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.RequestProcessor;

public class ScillaRequestProcessor extends RequestProcessor {

	@Override
	public void process(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		super.process(request, response);
	}

	@Override
	protected ActionForm processActionForm(HttpServletRequest request,
			HttpServletResponse response, ActionMapping mapping) {
		return super.processActionForm(request, response, mapping);
	}

	@Override
	protected boolean processValidate(HttpServletRequest request,
			HttpServletResponse response, ActionForm actionForm, ActionMapping mapping)
			throws IOException, ServletException {
		return super.processValidate(request, response, actionForm, mapping);
	}

}
