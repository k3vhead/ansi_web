package com.ansi.scilla.web.common.utils;

import java.util.ArrayList;
import java.util.List;

import com.ansi.scilla.common.ApplicationObject;

public class MenuItem extends ApplicationObject {

	private static final long serialVersionUID = 1L;

	private Menu menu;
	private List<MenuItem> subMenu;
	
	public MenuItem(Menu menu) {
		super();
		this.menu = menu;
		this.subMenu = new ArrayList<MenuItem>();
		List<Menu> subMenu = Menu.makeSubMenu(menu);
		for ( Menu m : subMenu ) {
			this.subMenu.add(new MenuItem(m));
		}
	}
	public Menu getMenu() {
		return menu;
	}
	public void setMenu(Menu menu) {
		this.menu = menu;
	}
	public List<MenuItem> getSubMenu() {
		return subMenu;
	}
	public void setSubMenu(List<MenuItem> subMenu) {
		this.subMenu = subMenu;
	}
	
	
	
//	public static void main(String[] args ) {
//		MenuItem menuItem = new MenuItem(Menu.CLAIM_ENTRY);
//		for ( MenuItem x : menuItem.getSubMenu() ) {
//			System.out.println(x.getMenu().getDisplayText());
//		}
//	}
	
}
