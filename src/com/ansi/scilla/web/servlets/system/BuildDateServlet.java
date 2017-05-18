package com.ansi.scilla.web.servlets.system;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ResourceBundle;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;

public class BuildDateServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public static final String KEY_BUILDDATE = "builddate";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
		ResourceBundle webBundle = ResourceBundle.getBundle("resources.build_web");
		String webBuildDate = webBundle.containsKey(KEY_BUILDDATE) ? webBundle.getString(KEY_BUILDDATE) : "not available";
		ResourceBundle commonBundle = ResourceBundle.getBundle("resources.build_common");
		String commonBuildDate = commonBundle.containsKey(KEY_BUILDDATE) ? webBundle.getString(KEY_BUILDDATE) : "not available";

		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("text/html");
		
		ServletOutputStream o = response.getOutputStream();
		OutputStreamWriter writer = new OutputStreamWriter(o);
		String jsonString = "{\"webBuildDate\":\"" + webBuildDate + ",\"commonBuildDate\":\"" + commonBuildDate + "\"}";
		writer.write(jsonString);
		writer.flush();
		writer.close();
	}

	public static void main(String[] args) {
		
	}
	
	public class MySerializer extends JsonSerializer {

		@Override
		public void serialize(Object arg0, JsonGenerator arg1, SerializerProvider arg2)
				throws IOException, JsonProcessingException {
			
			
		}
		
	}
}
