package com.ansi.scilla.web.specialOverride.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ansi.scilla.web.common.servlet.AbstractServlet;

/**
 * Exists solely to override the visibility in the do* methods
 * @author dclewis
 *
 */
public abstract class AbstractOverrideServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;

	@Override
	public abstract void doDelete(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException;

	@Override
	public abstract void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException;

	@Override
	public abstract void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException;

	
}
