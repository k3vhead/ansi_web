package com.ansi.scilla.web.user.response;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.web.common.response.MessageResponse;
import com.ansi.scilla.web.common.utils.Menu;
import com.ansi.scilla.web.report.common.ReportType;

public class DashboardFavoriteResponse extends MessageResponse {

	private static final long serialVersionUID = 1L;

	private List<DashboardFavoriteItem> favoriteList;

	public DashboardFavoriteResponse(Menu menu, Boolean selected) {
		super();
		this.favoriteList = Arrays.asList(new DashboardFavoriteItem[] {new DashboardFavoriteItem(menu, selected)} );
	}
	
	public DashboardFavoriteResponse(ReportType reportType, Boolean selected) {
		super();
		this.favoriteList = Arrays.asList(new DashboardFavoriteItem[] {new DashboardFavoriteItem(reportType, selected)} );
	}
	
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
		if ( parent.equals(Menu.REPORTS)) {
			for ( ReportType reportType : ReportType.values() ) {
				if ( permissionList.contains(reportType.getPermission().name())) {
					favoriteList.add(new DashboardFavoriteItem(reportType, dashboardFavoriteList.contains(reportType.getLink())));
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
		private String menu;
		private String displayText;
		private String link;
		private Boolean selected;

		public DashboardFavoriteItem(Menu menu, Boolean selected) {
			super();
			this.menu = menu.name();
			this.displayText = menu.getDisplayText();
			this.link = menu.getLink();
			this.selected = selected;
		}
		public DashboardFavoriteItem(ReportType reportType, boolean selected)  {
			super();
			
			try {
				String reportClassName = reportType.reportClassName();
				Class<?> reportClass = Class.forName(reportClassName);
				java.lang.reflect.Field field = reportClass.getDeclaredField("REPORT_TITLE");
				String title = (String)field.get(null);
				
				this.menu = reportType.name();
				this.displayText = title;
				this.link = "report.html?id=" + reportType.name();
				this.selected = selected;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		public String getMenu() {
			return menu;
		}
		public void setMenu(String menu) {
			this.menu = menu;
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
