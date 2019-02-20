package com.ansi.scilla.web.quote.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.ansi.scilla.web.common.action.SessionPageDisplayAction;
import com.ansi.scilla.web.quote.actionForm.NewQuoteDisplayForm;

public class NewQuoteDisplayAction extends SessionPageDisplayAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		NewQuoteDisplayForm form = (NewQuoteDisplayForm)actionForm;
		if ( form != null ) {
			Logger logger = LogManager.getLogger(this.getClass());
			logger.log(Level.DEBUG, form);
			request.setAttribute("ANSI_QUOTE_ID", form.getQuoteId());
			request.setAttribute(NewQuoteDisplayForm.KEY, form);
		}
		return super.execute(mapping, actionForm, request, response);
	}

}
