package com.ansi.scilla.web.common.query;

import java.sql.Connection;
import java.util.List;

public interface Query<T> {
	
	
	/**
	 * Returns data, based on stuff that has been entered into the object
	 * @param conn
	 * @param offset
	 * @param rowCount
	 * @return
	 */
	public List<T> select(Connection conn) throws Exception;
	
	/**
	 * Returns count of filtered list
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public Integer selectCount(Connection conn) throws Exception;
	
	/**
	 * Returns count of unfiltered list
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public Integer countAll(Connection conn) throws Exception;

}
