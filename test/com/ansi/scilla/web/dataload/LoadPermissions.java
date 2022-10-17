package com.ansi.scilla.web.dataload;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import com.ansi.scilla.common.db.PermissionGroupLevel;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.common.utils.Permission;
import com.thewebthing.commons.db2.AbstractDBTable;

public class LoadPermissions {

	public static void main(String[] args) {
		try {
			new LoadPermissions().loadOptions();
//			new LoadPermissions().loadEquivalentValues();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Fills the permission_level table with all possible combinations of permission + level.
	 * This is for backwards compatibility. This table should be removed once we know that
	 * everything works as expected.
	 * @throws Exception
	 */
	private void loadOptions() throws Exception {
		Connection conn = null;
		try {
			conn = AppUtils.getDevConn();
			conn.setAutoCommit(false);
			
			PreparedStatement psPut = conn.prepareStatement("insert into permission_level (permission_name, level, added_by, added_date, updated_by, updated_date) values (?,?,6,GetDate(),6,GetDate())");
			PreparedStatement psGet = conn.prepareStatement("select * from permission_level where permission_name=? and level=?");
			
			for ( Permission permission : Permission.values() ) {
				String p = permission.name();
				for (int level = 0; level < 2; level++ ) {
					System.out.println("Checking " + p + " " + level);
					psGet.setString(1, p);
					psGet.setInt(2, level);
					ResultSet rs = psGet.executeQuery();
					if ( ! rs.next() ) {
						psPut.setString(1, p);
						psPut.setInt(2, level);
						System.out.println("\tPutting " + p + " " + level);
						psPut.executeUpdate();
					}
				}
			}
			
			conn.commit();
		} catch ( Exception e ) {
			conn.rollback();
			throw e;
		} finally {
			conn.close();
		}
	}
	
	/**
	 * For backwards compatibility, add "XX_READ" and "XX_WRITE" for permission groups that have
	 * permission XX + level 0, or permission XX + level 1 
	 */
	private void loadEquivalentValues() throws Exception {
		Connection conn = null;
		try {
			conn = AppUtils.getDevConn();
			conn.setAutoCommit(false);

			PreparedStatement psPut = conn.prepareStatement("insert into permission_group_level (permission_group_id, permission_name, permission_level, added_by, added_date, updated_by, updated_date) values (?,?,?,6,GetDate(),6,GetDate())");
			PreparedStatement psGet = conn.prepareStatement("select * from permission_group_level where permission_group_id=? and permission_name=? and permission_level=?");
			
			List<AbstractDBTable> pglList = new PermissionGroupLevel().selectAll(conn);
			String permission = null;
			for ( AbstractDBTable record : pglList ) {
				PermissionGroupLevel pgl = (PermissionGroupLevel)record;
				Integer id = pgl.getPermissionGroupId();
				String p = pgl.getPermissionName();
				Integer level = pgl.getPermissionLevel();
				
				System.out.println("Checking " + id + " " + p + " " + level);
				
				if ( level == 0 ) {
					permission = p + "_READ";
				} else if ( level == 1 ) {
					permission = p + "_WRITE";
				} else {
					throw new Exception("Weird level " + id + " " + p + " " + level);
				}
				
				psGet.setInt(1, id);
				psGet.setString(2, permission);
				psGet.setInt(3, level);
				ResultSet rs = psGet.executeQuery();
				if ( ! rs.next() ) {
					psPut.setInt(1, id);
					psPut.setString(2, permission);
					psPut.setInt(3, 1);
					System.out.println("\tPutting " + id + " " + permission + " " + level);
					psPut.executeUpdate();
				}

			}
		
			
			

			conn.commit();
		} catch ( Exception e ) {
			conn.rollback();
			throw e;
		} finally {
			conn.close();
		}
	}
}
