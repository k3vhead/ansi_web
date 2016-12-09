package com.ansi.scilla.web.test;

import java.sql.Connection;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ansi.scilla.common.db.Code;
import com.ansi.scilla.common.db.User;
import com.ansi.scilla.web.common.AppUtils;
import com.ansi.scilla.web.common.WebMessages;
import com.ansi.scilla.web.request.CodeRequest;
import com.ansi.scilla.web.response.codes.CodeResponse;
import com.ansi.scilla.web.servlets.CodeServlet;
import com.ansi.scilla.web.struts.SessionUser;

public class TestCodes extends CodeServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String[] urlList = new String[] {
			"/ansi_web/code/list",
			"/ansi_web/code/list/",
			"/ansi_web/code/list?id=123",
			"/ansi_web/code/message",
			"/ansi_web/code/message/message/SUCCESS",
			"/ansi_web/code/message/message/SUCCESS?id=123&x=abc",
			"/ansi_web/code/message/message/SUCCESS/?id=123&x=abc",
			"/ansi_web/code",
			"/ansi_web/code/"
		};

	public static void main(String[] args) {
		TesterUtils.makeLoggers();
		try {
			new TestCodes().go();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	public void go() throws Exception {
		Connection conn = null;
		try {
			conn = AppUtils.getConn();
			conn.setAutoCommit(false);

//			new TestCodes().testUri();
//			new TestCodes().testUri2();
//			new TestCodes().testAdd(conn);
			new TestCodes().testUpdate(conn);
//			new TestCodes().testjson();
			
			conn.commit();
		} catch ( Exception e) {
			conn.rollback();
			throw e;
		} finally {
			conn.close();
		}
	}
	
	public void testjson() throws Exception {
		String json = "{\"tableName\":\"tt\",\"fieldName\":\"ff\",\"value\":\"vv\",\"display\":\"dd\",\"seq\":\"1\",\"description\":\"desc\",\"status\":\"1\"}";
		CodeRequest req = new CodeRequest(json);
		System.out.println(req);
	}
	public void testAdd(Connection conn) throws Exception {
		User user = new User();
		user.setUserId(5);
		user.selectOne(conn);
		SessionUser sessionUser = new SessionUser(user);
		CodeRequest codeRequest = new CodeRequest();
		codeRequest.setTableName("message");
		codeRequest.setFieldName("message");
		codeRequest.setValue("MISSING_DATA");
		codeRequest.setStatus(Code.STATUS_IS_ACTIVE);
		codeRequest.setSeq(1);
		codeRequest.setDisplayValue("Ooops -- something ain't where it oughta be");
		codeRequest.setDescription("Web message for missing input data");
		
		Code codeResponse = this.doAdd(conn, codeRequest, sessionUser);
		System.out.println(codeResponse);
	}


	public void testUpdate(Connection conn) throws Exception {
		User user = new User();
		user.setUserId(5);
		user.selectOne(conn);
		SessionUser sessionUser = new SessionUser(user);
		CodeRequest codeRequest = new CodeRequest();
		codeRequest.setTableName("aaa");
		codeRequest.setFieldName("bbb");
		codeRequest.setValue("ccc");
		codeRequest.setStatus(Code.STATUS_IS_ACTIVE);
		codeRequest.setSeq(2);
		codeRequest.setDisplayValue("Just a test entry " + new Date());
		codeRequest.setDescription("Stuff goes here");
		
		Code key = new Code();
		key.setTableName("aaa");
		key.setFieldName("bbb");
		key.setValue("ddd");
		Code code = this.doUpdate(conn, key, codeRequest, sessionUser);
		System.out.println(code);
	}

	public void testUri() {
		Pattern uriPattern = Pattern.compile("^(.*/)(.*)(\\.)(.*)$", Pattern.CASE_INSENSITIVE);
		Matcher matcher = uriPattern.matcher("/ansi_web/codes/getOne.json?id=123");
		
		if ( matcher.matches() ) {
			System.out.println( matcher.group(2));
		} else {
			System.out.println("Nope");
		}
	}
	
	public void testUri2() {
		for ( String url : urlList ) {
			System.out.println(url);
			int idx = url.indexOf("/code/");
			System.out.println("\t" + idx);
			String myString = url.substring(idx + "/code/".length());
			System.out.println("\t" + myString);
			
			int idx2 = myString.indexOf("?");
			if ( idx2 > -1 ) {
				String queryString = myString.substring(idx2 + 1);
				System.out.println("\tQS: " + queryString);
				myString = myString.substring(0, idx2);
				System.out.println("\tMine: " + myString);
			}
			
			String[] urlPieces = myString.split("/");
			System.out.println("\tcommand: " + urlPieces[0]);
			if ( urlPieces.length > 1 ) {
				System.out.println("\tTable: " + urlPieces[0]);
				System.out.println("\tField: " + urlPieces[1]);
				System.out.println("\tValue: " + urlPieces[2]);
				// ArrayIndexOutOfBoundsException
			}
		}
	}
}
