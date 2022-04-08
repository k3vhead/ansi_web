package com.ansi.scilla.web.common.servlet;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.web.common.query.LookupQuery;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.ColumnFilter;
import com.ansi.scilla.web.common.utils.Permission;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.InvalidParameterException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;

public abstract class AbstractLookupServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;
	
	protected Logger logger;
	protected Permission permission;
	protected Transformer<HashMap<String, Object>,HashMap<String, Object>> itemTransformer;
	
	protected int amount = 10;
	protected int start = 0;
	protected int draw = 0;
	protected int col = 0;
	protected String dir = "asc";
	protected String[] cols;
	

	
	public AbstractLookupServlet(Permission permission) {
		super();
		this.logger = LogManager.getLogger(this.getClass());
		this.permission = permission;
	}
	
	public Permission getPermission() {
		return permission;
	}
	public void setPermission(Permission permission) {
		this.permission = permission;
	}

	public Transformer<HashMap<String, Object>, HashMap<String, Object>> getItemTransformer() {
		return itemTransformer;
	}

	/**
	 * A class that implements org.apache.commons.collections4.Transformer (https://commons.apache.org/proper/commons-collections/javadocs/api-4.3/index.html)
	 * If populated, the "transform()" method will be called for each record in the results of the query. Use this method
	 * to add/update fields in the results that can derived from queried content. (For example, the description of an enum value)

	 * @param itemTransformer
	 */
	public void setItemTransformer(Transformer<HashMap<String, Object>, HashMap<String, Object>> itemTransformer) {
		this.itemTransformer = itemTransformer;
	}


	public int getAmount() {
		return amount;
	}

	/**
	 * Number of rows to return (default is 10)
	 * @param amount
	 */
	public void setAmount(int amount) {
		this.amount = amount;
	}


	public int getStart() {
		return start;
	}

	/**
	 * Row number to begin results (default is 0)
	 * @param start
	 */
	public void setStart(int start) {
		this.start = start;
	}


	public int getDraw() {
		return draw;
	}


	public void setDraw(int draw) {
		this.draw = draw;
	}

	/**
	 * Column number to sort by
	 * @return
	 */
	public int getCol() {
		return col;
	}


	public void setCol(int col) {
		this.col = col;
	}

	public String getDir() {
		return dir;
	}

	/**
	 * Direction to sort (asc or desc; default is asc)
	 * @param dir
	 */
	public void setDir(String dir) {
		this.dir = dir;
	}


	public String[] getCols() {
		return cols;
	}

	/**
	 * Columns being displayed in the JSP
	 * @param cols
	 */
	public void setCols(String[] cols) {
		this.cols = cols;
	}


	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		super.sendNotAllowed(response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			if ( permission == null ) {
				AppUtils.validateSession(request);
			} else {
				AppUtils.validateSession(request, permission);
			}
			processGet(request, response);
		} catch (TimeoutException  | NotAllowedException | ExpiredLoginException e) {
			super.sendForbidden(response);
		} catch ( InvalidParameterException e ) {
			super.sendNotFound(response);
		}
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		try {
			if ( permission == null ) {
				AppUtils.validateSession(request);
			} else {
				AppUtils.validateSession(request, permission);
			}
			processGet(request, response);
		} catch (TimeoutException  | NotAllowedException | ExpiredLoginException e) {
			super.sendForbidden(response);
		} catch (InvalidParameterException e) {
			super.sendNotFound(response);
		}
	}
	
	
	
	private void processGet(HttpServletRequest request, HttpServletResponse response) throws InvalidParameterException, ServletException {
		String sStart = request.getParameter("start");
		String sAmount = request.getParameter("length");
		String sDraw = request.getParameter("draw");
		String sCol = request.getParameter("order[0][column]");
		String sdir = request.getParameter("order[0][dir]");		
	
	
		Connection conn = null;
		try {
			conn = AppUtils.getDBCPConn();
			String term = "";
	
			if(request.getParameter("search[value]") != null){
				term = request.getParameter("search[value]");
			}
			
			if (sStart != null) {
				start = Integer.parseInt(sStart);
				start = start < 0 ? 0 : start;
			}
			if (sAmount != null) {				
				amount = Integer.parseInt(sAmount);
				// -1 indicates "get all rows"
				if ( amount > 0 ) {
					if (amount < 10 ) {
						amount = 10;
					} else if (amount > 1000) {
						amount = 1000;
					}
				}
			}
			if (sDraw != null) {
				draw = Integer.parseInt(sDraw);
			}
			if (sCol != null) {
				col = Integer.parseInt(sCol);
				if (col < 0 || col > this.cols.length) {
					col = 0;
				}
			}
			if (sdir != null) {
				if (sdir.equals("asc")) {
					dir = "asc";
				} else if (sdir.equals("desc")) {
					dir = "desc";
				}
			}

			String colName = cols[col];
	
			logger.log(Level.DEBUG, "sCol: " + sCol + "\tCol: " + col + "\tCols[col]: " + cols[col]);
			logger.log(Level.DEBUG, "Start: " + start + "\tAmount: " + amount + "\tTerm: " + term);
			
			
			LookupQuery lookup = makeQuery(conn, request);
			lookup.setSearchTerm(term);
			if ( sCol != null ) {
				lookup.setSortBy(colName);
				lookup.setSortIsAscending(dir.equals("asc"));
			}
			
			List<ColumnFilter> columnFilterList = makeColumnFilter(request);
			for ( ColumnFilter filter : columnFilterList ) {
				lookup.addColumnFilter(filter); // add instead of "set" because other filters may already exist
			}
	
			Integer filteredCount = lookup.selectCount(conn);
			Integer totalCount = lookup.countAll(conn);
			
			List<HashMap<String, Object>> dataList = new ArrayList<HashMap<String, Object>>();
			ResultSet rs = lookup.select(conn, start, amount);
			ResultSetMetaData rsmd = rs.getMetaData();
			while ( rs.next() ) {
				dataList.add(makeDataItem(rs, rsmd));
			}
			if ( itemTransformer != null ) {
				CollectionUtils.transform(dataList, itemTransformer);
			}
			
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("application/json");
	
			HashMap<String, Object> jsonResponse = new HashMap<String, Object>();			
			jsonResponse.put("recordsTotal", totalCount);
			jsonResponse.put("recordsFiltered", filteredCount);
			jsonResponse.put("draw", draw);
			jsonResponse.put("columns",null);
			jsonResponse.put("data", dataList);
	
			String json = AppUtils.object2json(jsonResponse);
	
			ServletOutputStream o = response.getOutputStream();
			OutputStreamWriter writer = new OutputStreamWriter(o);
			writer.write(json);
			writer.flush();
			writer.close();
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e) {
			super.sendForbidden(response);
		} catch ( Exception e ) {
			AppUtils.logException(e);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}
	}

	private HashMap<String, Object> makeDataItem(ResultSet rs, ResultSetMetaData rsmd) throws SQLException {
		HashMap<String, Object> dataItem = new HashMap<String, Object>();
		for ( int i = 0; i < rsmd.getColumnCount(); i++ ) {
			int idx = i + 1;
			String column = rsmd.getColumnName(idx);
			Object value = rs.getObject(idx);
			
			dataItem.put(column, value);
		}
		return dataItem;
	}

	
	
	private List<ColumnFilter> makeColumnFilter(HttpServletRequest request) {
		List<ColumnFilter> columnFilterList = new ArrayList<ColumnFilter>();
		for (int i = 0; i < this.cols.length; i++ ) {
			String parmName = "columns[" + i + "][search][value]";
			String parmValue = request.getParameter(parmName);
			if ( ! StringUtils.isBlank(parmValue)) {
				columnFilterList.add(new ColumnFilter(cols[i],parmValue));
			}
		}
		return columnFilterList;
	}

	public abstract LookupQuery makeQuery(Connection conn, HttpServletRequest request) throws InvalidParameterException;
	
	
}