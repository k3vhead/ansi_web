package com.ansi.scilla.web.test;

import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.ansi.scilla.web.common.struts.SessionDivision;
import com.thewebthing.commons.lang.StringUtils;

public class TesterUtils {
	
	/**
	 * This is only here for backward compatability. It does nothing.
	 * @deprecated replace with LogManager.getLogger(logname);
	 */
	public static void makeLoggers() {
		
	}
	
	public static String postJson(String url, String jsonString ) throws Exception {
		CloseableHttpClient httpclient = HttpClients.createDefault();
//		String url = "http://192.168.100.199/addressNV/addressNV.wsgi/checkAddress";
		HttpPost httpPost = new HttpPost(url);
		String pageContent = null;
		System.out.println("Input\n" + jsonString);
		StringEntity params = new StringEntity(jsonString);
		httpPost.addHeader("content-type", "application/x-www-form-urlencoded");
		httpPost.setEntity(params);
		CloseableHttpResponse response = httpclient.execute(httpPost);
		try {
			System.out.println(response.getStatusLine());
			HttpEntity entity = response.getEntity();
			StringWriter writer = new StringWriter();
			IOUtils.copy(entity.getContent(), writer, "UTF-8");
			pageContent = writer.toString();
			EntityUtils.consume(entity);
		} finally {
			response.close();
		}
		return pageContent;
	}
	
	public static String doDelete(String url, String jsonString ) throws Exception {
		CloseableHttpClient httpclient = HttpClients.createDefault();
//		String url = "http://192.168.100.199/addressNV/addressNV.wsgi/checkAddress";
		HttpDelete httpDelete = new HttpDelete(url);
		String pageContent = null;
		System.out.println("Input\n" + jsonString);
		httpDelete.addHeader("content-type", "application/x-www-form-urlencoded");
		if ( ! StringUtils.isBlank(jsonString)) {
			StringEntity params = new StringEntity(jsonString);
//			httpDelete.setEntity(params);
		}
		CloseableHttpResponse response = httpclient.execute(httpDelete);
		try {
			System.out.println(response.getStatusLine());
			HttpEntity entity = response.getEntity();
			StringWriter writer = new StringWriter();
			IOUtils.copy(entity.getContent(), writer, "UTF-8");
			pageContent = writer.toString();
			EntityUtils.consume(entity);
		} finally {
			response.close();
		}
		return pageContent;
	}
	
	public static String getJson(String url) throws Exception {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(url);
		String pageContent = null;
		CloseableHttpResponse response = httpclient.execute(httpGet);
		try {
			System.out.println(response.getStatusLine());
			HttpEntity entity = response.getEntity();
			StringWriter writer = new StringWriter();
			IOUtils.copy(entity.getContent(), writer, "UTF-8");
			pageContent = writer.toString();
			EntityUtils.consume(entity);
		} finally {
			response.close();
		}
		return pageContent;
	}
	
	public static List<SessionDivision> makeSessionDivisionList(Connection conn, Integer userId) throws SQLException {
		List<SessionDivision> sessionDivisionList = new ArrayList<SessionDivision>();
		String sql = "select division.division_id, division.division_nbr, division.division_code \n" + 
				"from division \n" + 
				"inner join division_user on division_user.division_id =division.division_id and division_user.user_id=?\n" + 
				"order by division_id";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setInt(1,  userId);
		ResultSet rs = ps.executeQuery();
		while ( rs.next() ) {
			SessionDivision sd = new SessionDivision();
			sd.setDivisionCode(rs.getString("division_code"));
			sd.setDivisionId(rs.getInt("division_id"));
			sd.setDivisionNbr(rs.getInt("division_nbr"));
			sessionDivisionList.add(sd);
		}
		rs.close();
		return sessionDivisionList;
		
	}
}
