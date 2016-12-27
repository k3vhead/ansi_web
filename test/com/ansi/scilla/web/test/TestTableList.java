package com.ansi.scilla.web.test;

import java.sql.Connection;
import java.util.List;

import com.ansi.scilla.web.common.AppUtils;
import com.ansi.scilla.web.servlets.TableFieldListServlet;

public class TestTableList {

	public static void main(String[] args) {
		TesterUtils.makeLoggers();
		try {
//			new TestTableList().go();
			new TestTableList().go2();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void go() throws Exception {
		Connection conn = null;
		try {
			conn = AppUtils.getConn();
			List<String> tables = new TableFieldListServlet().doGetWork(conn, "/tableFieldList/quote");
			for ( String table: tables ) {
				System.out.println(table);
			}
		} finally {
			conn.close();
		}
	}

	public void go2() throws Exception {
		String json = TesterUtils.getJson("http://127.0.0.1:8080/ansi_web/tableFieldList/xxx");
		System.out.println(json);
	}
}
