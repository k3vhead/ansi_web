package com.ansi.scilla.web.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.sql.Connection;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ansi.scilla.web.common.ResponseCode;
import com.ansi.scilla.web.response.AnsiResponse;
import com.ansi.scilla.web.response.MessageResponse;
import com.thewebthing.commons.lang.JsonUtils;


public class AbstractServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		super.doDelete(request, response);
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		super.doGet(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		super.doPost(request, response);
	}

	protected void sendNotFound(HttpServletResponse response) {
		response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		response.setContentType("application/json");
	}
	
	protected void sendResponse(Connection conn, HttpServletResponse response, ResponseCode responseCode, MessageResponse data) throws Exception {
		AnsiResponse ansiResponse = new AnsiResponse(conn, responseCode, data);
		response.setStatus(responseCode.statusCode());
		response.setContentType("application/json");
		
		ServletOutputStream o = response.getOutputStream();
		OutputStreamWriter writer = new OutputStreamWriter(o);
		String jsonString = JsonUtils.object2JSON(ansiResponse);
		writer.write(jsonString);
		writer.flush();
		writer.close();
	}

	public String makeJsonString(HttpServletRequest request) throws UnsupportedEncodingException, IOException {
		Writer writer = new StringWriter();
		 
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } finally {
        	request.getInputStream().close();
        }
        return writer.toString();        
	}

	protected HashMap<String, String> makeParamMap(String queryString) {
		HashMap<String, String> paramMap = new HashMap<String, String>();
		String[] pairs = queryString.split("\\&");
		for ( String pair : pairs ) {
			String[] pieces = pair.split("=");
			if ( pieces.length == 2 ) {
				paramMap.put(pieces[0], pieces[1]);
			}
		}
		return paramMap;
	}
	
	
	
}
