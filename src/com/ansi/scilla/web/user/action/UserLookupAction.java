package com.ansi.scilla.web.user.action;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.ansi.scilla.common.db.PermissionGroup;
import com.ansi.scilla.web.common.action.SessionPageDisplayAction;
import com.ansi.scilla.web.common.actionForm.IdForm;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.thewebthing.commons.db2.RecordNotFoundException;


public class UserLookupAction extends SessionPageDisplayAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		IdForm form = (IdForm)actionForm;

		if ( form != null) {
			if ( ! StringUtils.isBlank(form.getId())) {
				Integer permissionGroupId = Integer.valueOf(form.getId());
				String groupName = getGroupName(permissionGroupId);
				request.setAttribute("ANSI_PERMISSION_GROUP_ID", form.getId());
				request.setAttribute("ANSI_PERMISSION_GROUP_NAME", groupName);
			}
		}
		return super.execute(mapping, actionForm, request, response);
	}

	private String getGroupName(Integer permissionGroupId) throws Exception {
		String groupName = "";
		Connection conn = null;
		try {
			conn = AppUtils.getDBCPConn();
			PermissionGroup permissionGroup = new PermissionGroup();
			permissionGroup.setPermissionGroupId(permissionGroupId);
			permissionGroup.selectOne(conn);
			groupName = permissionGroup.getName();
		} catch ( RecordNotFoundException e ) {
			groupName = "Invalid Permission Group";
		} finally {
			conn.close();
		}
		return groupName;
	}

}
