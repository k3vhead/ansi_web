package com.ansi.scilla.web.user.response;

import java.util.ArrayList;
import java.util.List;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.web.common.response.MessageResponse;
import com.ansi.scilla.web.common.utils.Menu;

public class DashboardFavoriteResponse extends MessageResponse {

	private static final long serialVersionUID = 1L;

	private List<DashboardFavoriteItem> favoriteList;

	
	public DashboardFavoriteResponse(Menu parent, List<String> dashboardFavoriteList, List<String> permissionList ) {
		super();
		favoriteList = new ArrayList<DashboardFavoriteItem>();
		
		for ( Menu menu : Menu.values() ) {
			if ( menu.getParent() != null && menu.getParent().equals(parent)) {
				if ( permissionList.contains(menu.getPermissionRequired().name())) {
					favoriteList.add(new DashboardFavoriteItem(menu, dashboardFavoriteList.contains(menu.getLink())));
				}
			}
		}
	}
	
	public List<DashboardFavoriteItem> getFavoriteList() {
		return favoriteList;
	}
	public void setFavoriteList(List<DashboardFavoriteItem> favoriteList) {
		this.favoriteList = favoriteList;
	}


	public class DashboardFavoriteItem extends ApplicationObject {
		private static final long serialVersionUID = 1L;
		private String displayText;
		private String link;
		private Boolean selected;

		public DashboardFavoriteItem(Menu menu, Boolean selected) {
			super();
			this.displayText = menu.getDisplayText();
			this.link = menu.getLink();
			this.selected = selected;
		}
		public String getDisplayText() {
			return displayText;
		}
		public void setDisplayText(String displayText) {
			this.displayText = displayText;
		}
		public String getLink() {
			return link;
		}
		public void setLink(String link) {
			this.link = link;
		}
		public Boolean getSelected() {
			return selected;
		}
		public void setSelected(Boolean selected) {
			this.selected = selected;
		}
		
	}
}
