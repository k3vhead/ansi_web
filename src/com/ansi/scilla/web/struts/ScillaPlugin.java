package com.ansi.scilla.web.struts;

import javax.servlet.ServletException;

import org.apache.struts.action.ActionServlet;
import org.apache.struts.action.PlugIn;
import org.apache.struts.config.ModuleConfig;

import com.ansi.scilla.web.common.utils.AppUtils;

public class ScillaPlugin implements PlugIn {

	@Override
	public void destroy() {
		// nothing to do here
	}

	@Override
	public void init(ActionServlet arg0, ModuleConfig arg1) throws ServletException {
		try {
			AppUtils.makeApplicationLogger();
			AppUtils.makeApacheLogger();
			AppUtils.makeWebthingLogger();
			AppUtils.makeTransactionLogger();
		} catch ( Exception e) {
			throw new ServletException(e);
		}

	}

}
