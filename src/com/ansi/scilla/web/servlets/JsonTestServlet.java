package com.ansi.scilla.web.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ansi.scilla.web.common.AppUtils;

public class JsonTestServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		try {
			String jsonString = super.makeJsonString(request);
			TestJsonStuff stuff = (TestJsonStuff)AppUtils.json2object(jsonString, TestJsonStuff.class);
			System.out.println(jsonString);
			System.out.println(stuff);
		} catch ( Exception e) {
			throw new ServletException(e);
		}
		super.doPost(request, response);
	}

	
}
