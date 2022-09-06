package com.ansi.scilla.web.permission.request;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.web.common.request.AbstractRequest;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.common.utils.Permission;

public class PermissionRequest extends AbstractRequest {

	private static final long serialVersionUID = 1L;

	public static final String FUNCTIONAL_AREA = "functionalArea";
	public static final String PERMISSION = "permission";
	
	private String functionalArea;
	private String permission;
	
	public String getFunctionalArea() {
		return functionalArea;
	}
	public void setFunctionalArea(String functionalArea) {
		this.functionalArea = functionalArea;
	}
	public String getPermission() {
		return permission;
	}
	public void setPermission(String permission) {
		this.permission = permission;
	}
	
	public WebMessages validate() {
		Logger logger = LogManager.getLogger(this.getClass());
		WebMessages webMessages = new WebMessages();
		
		if ( StringUtils.isBlank(this.functionalArea) ) {
			webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "Missing Functional Area");
		} else {
			try {
				Permission functionalArea = Permission.valueOf(this.functionalArea);
				if ( functionalArea.getParent() != null ) {
					webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "Invalid Functional Area");
				}
			} catch (IllegalArgumentException e) {
				webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "Invalid Functional Area");
			}
		}
		
		if ( StringUtils.isBlank(this.permission) ) {
			// this is OK -- we're just going to delete the permission
		} else {
			try {
				Permission permission = Permission.valueOf(this.permission);
				if ( permission.getParent() == null  ) {
					webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "Invalid Permission Level");
				} else {
					List<Permission> parentTree = permission.makeParentList();
					List<Permission> parentList = IteratorUtils.toList(CollectionUtils.select(parentTree, new FunctionalAreaPredicate()).iterator());
					// there should only be one functional area, so get the first item in the parent list
					if ( ! parentList.get(0).name().equals(this.functionalArea) ) {
						webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "Invalid Permission Hierarchy");
					}
				}
			} catch (IllegalArgumentException e) {
				webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "Invalid permission");
			}
		}
		
		return webMessages;
	}
	
	
	/**
	 * matches when permission is a functional area (ie. parent is null)
	 * @author dclewis
	 *
	 */
	public class FunctionalAreaPredicate implements Predicate<Permission> {

		@Override
		public boolean evaluate(Permission arg0) {
			return arg0.getParent() == null;
		}
		
	}
}
