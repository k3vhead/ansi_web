package com.ansi.scilla.web.demo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import com.ansi.scilla.common.db.Code;
import com.ansi.scilla.common.utils.AppUtils;
import com.thewebthing.commons.db2.AbstractDBTable;

/**
 * An attempt to demonstrate most of the ways SQL can be executed in java code:
 * Raw sql execution:
 * 1. Normal sql select, with no parameters, and looping through a result set
 * 2. Normal sql select with bind variables, looping through a result set
 * 3. Insert with bind variables
 * 
 * ansi_common/DB usage:
 * 1. SelectAll
 * 2. SelectSome
 * 3. Insert
 * 4. Delete
 * 5. Delete again to show "RecordNotFound" exception
 * 
 * Things that we're not mentioning:
 * 1. ResultSetMetaData - data about the data, including field names and types.
 * 
 * 
 * @author dclewis
 *
 */
public class BindVariables {

	private void hardWay() throws Exception {
		Connection conn = null;
		try {
			conn = AppUtils.getDevConn();
			conn.setAutoCommit(false);
			
			
			doSelect1(conn);
			System.out.println("***********************");
			doSelectBind1(conn);
			System.out.println("***********************");
			doInsert1(conn);
			conn.commit();
		} catch ( Exception e ) {
			conn.rollback();
			throw e;
		} finally {
			conn.close();
		}
		
	}
	
	
	private void doSelect1(Connection conn) throws SQLException {
		Statement s = conn.createStatement();
		ResultSet rs = s.executeQuery("select * from code");
		while ( rs.next() ) {
			String tableName = rs.getString("table_name");
			String fieldName = rs.getString("field_name");
			String value = rs.getString("value");
			Integer seq = rs.getInt("seq");
			System.out.println(tableName + "\t" + fieldName + "\t" + value + "\t" + seq);
		}
		rs.close();
		
	}

	/**
	 * Using bind variables:
	 * 1.  Parameter count starts at 1, not zero
	 * 2.  Re-use the PreparedStatement with different variables
	 * 
	 * @param conn
	 * @throws SQLException
	 */
	private void doSelectBind1(Connection conn) throws SQLException {
		PreparedStatement ps = conn.prepareStatement("select * from code where table_name=?");
		ps.setString(1,  "call_log");
		ResultSet rs = ps.executeQuery();
		while ( rs.next() ) {
			String tableName = rs.getString("table_name");
			String fieldName = rs.getString("field_name");
			String value = rs.getString("value");
			Integer seq = rs.getInt("seq");
			System.out.println(tableName + "\t" + fieldName + "\t" + value + "\t" + seq);
		}
		rs.close();
		
		System.out.println("++++++++++++++++++++++++");
		
		ps.setString(1,  "contact");
		rs = ps.executeQuery();
		while ( rs.next() ) {
			String tableName = rs.getString("table_name");
			String fieldName = rs.getString("field_name");
			String value = rs.getString("value");
			Integer seq = rs.getInt("seq");
			System.out.println(tableName + "\t" + fieldName + "\t" + value + "\t" + seq);
		}
		rs.close();
	}


	/**
	 * 1. java.sql.Date vs. java.util.Date
	 * 
	 * @param conn
	 * @throws SQLException
	 */
	private void doInsert1(Connection conn) throws SQLException {
		String sql = "insert into code ("
				+ "table_name,field_name,value,display_value,seq,description,code_status,added_by,added_date,updated_by,updated_date) "
				+ "values (?,?,?,?,?,?,?,?,?,?,?)";
		java.sql.Date today = new java.sql.Date( (new java.util.Date()).getTime());
		PreparedStatement ps = conn.prepareStatement(sql);
		
		ps.setString(1, "test_table");
		ps.setString(2, "test_field");
		ps.setString(3, "test_value");
		ps.setString(4, "Test Value");
		ps.setInt(5, 1);
		ps.setString(6, "A demonstration record for training");
		ps.setInt(7, 0);
		ps.setInt(8, 5);
		ps.setDate(9, today);
		ps.setInt(10, 5);
		ps.setDate(11, today);
		
		ps.executeUpdate();
	}

	
	
	public void easyWay() throws Exception {
		Connection conn = null;
		try {
			conn = AppUtils.getDevConn();
			conn.setAutoCommit(false);
			
			
			doSelectAll(conn);
			System.out.println("***********************");
			doSelectSome(conn);
			System.out.println("***********************");
			doInsert2(conn);
			System.out.println("***********************");
			doUpdate2(conn);
			conn.commit();
		} catch ( Exception e ) {
			conn.rollback();
			throw e;
		} finally {
			conn.close();
		}
	}
	
	public void easyDelete() throws Exception {
		Connection conn = null;
		try {
			conn = AppUtils.getDevConn();
			conn.setAutoCommit(false);
						
			doDelete2(conn);
			conn.commit();
		} catch ( Exception e ) {
			conn.rollback();
			throw e;
		} finally {
			conn.close();
		}
	}

	private void doSelectAll(Connection conn) throws Exception {
		Code code = new Code();
		List<AbstractDBTable> recordList = code.selectAll(conn);
		List<Code> codeList = Code.cast(recordList);
		for ( Code record : codeList ) {
			System.out.println(record.getTableName() + "\t" + record.getFieldName() + "\t" + record.getValue() + "\t" + record.getSeq());
		}
		
		//  --- or ---
		List<Code> codeList2 = Code.cast((new Code()).selectAll(conn));
	}


	private void doSelectSome(Connection conn) throws Exception {
		Code code = new Code();
		code.setTableName("call_log");
		List<Code> codeList = Code.cast(code.selectSome(conn));
		for ( Code record : codeList ) {
			System.out.println(record.getTableName() + "\t" + record.getFieldName() + "\t" + record.getValue() + "\t" + record.getSeq());
		}
	}


	private void doInsert2(Connection conn) throws Exception {
		java.util.Date today = new java.util.Date();

		Code code = new Code();
		code.setTableName("test_table");
		code.setFieldName("test_field");
		code.setValue("test_value2");
		code.setDisplayValue("Another demonstration record for training");
		code.setStatus(0);
		code.setSeq(1);
		code.setAddedBy(5);
		code.setAddedDate(today);
		code.setUpdatedBy(5);
		code.setUpdatedDate(today);
		code.insertWithNoKey(conn);
	}

	private void doUpdate2(Connection conn) throws Exception {
		Code code = new Code();
		code.setTableName("test_table");
		code.setFieldName("test_field");
		code.setValue("test_value2");
		code.selectOne(conn);
		
		System.out.println("Before:");
		System.out.println(code);
		
		Code key = new Code();
		key.setTableName("test_table");
		key.setFieldName("test_field");
		key.setValue("test_value2");
		
		code.setDisplayValue("XXXXXXXXXXXXXXXXXXXXXX");
		code.setUpdatedBy(5);
		code.setUpdatedDate(new java.util.Date());
		
		code.update(conn, key);
	}
	
	
	private void doDelete2(Connection conn) throws Exception {
		Code code = new Code();
		code.setTableName("test_table");
		code.setFieldName("test_field");
		code.setValue("test_value2");
		code.delete(conn);
	}

	public static void main(String[] args) {
		try {
			new BindVariables().hardWay();
//			new BindVariables().easyWay();
//			new BindVariables().easyDelete();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
