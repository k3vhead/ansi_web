package com.ansi.scilla.web.job.servlet;

import java.io.OutputStreamWriter;
import java.io.IOException;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.Permission;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.thewebthing.commons.lang.StringUtils;

/**
 * 
 * The url for get will be one of: /getJobs?quoteId=<searchTerm> (returns all
 * job records containing <quoteId>)
 * 
 * The url for delete will return methodNotAllowed
 * 
 * The url for post will return methodNotAllowed
 * 
 *
 */
public class JobByQuoteIdServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		super.sendNotAllowed(response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		super.sendNotAllowed(response);
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Connection conn = null;
		try {
			conn = AppUtils.getDBCPConn();
			AppUtils.validateSession(request, Permission.QUOTE_READ);
			String qs = request.getQueryString();

			String term = "";
			if (qs != null) {
				if (qs.indexOf("quoteId=") != -1) {
					term = StringUtils.trimToNull(URLDecoder.decode(qs.substring("quoteId=".length()), "UTF-8"));
				}
			}

			List<ReturnItem> resultList = new ArrayList<ReturnItem>();
			String sql = "select job_id" + " from job where quote_id = " + term + " order by job_nbr ASC";
			Statement s = conn.createStatement();
			ResultSet rs = s.executeQuery(sql);
			while (rs.next()) {
				resultList.add(new ReturnItem(rs));
			}
			rs.close();

			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("application/json");

			String json = AppUtils.object2json(resultList);
			ServletOutputStream o = response.getOutputStream();
			OutputStreamWriter writer = new OutputStreamWriter(o);
			writer.write(json);
			writer.flush();
			writer.close();
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e) {
			super.sendForbidden(response);
		} catch (Exception e) {
			AppUtils.logException(e);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}
	}

	public class ReturnItem extends ApplicationObject {
		private static final long serialVersionUID = 1L;
		private Integer job_id;

		public ReturnItem(ResultSet rs) throws SQLException {
			super();
			this.job_id = rs.getInt("job_id");

		}

		public Integer getJobId() {
			return job_id;
		}

		public void setJobId(Integer job_id) {
			this.job_id = job_id;
		}

	}
}
