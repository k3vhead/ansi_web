package com.ansi.scilla.web.tags;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.jsp.JspException;

import com.ansi.scilla.common.db.Locale;
import com.ansi.scilla.common.utils.LocaleType;
import com.ansi.scilla.web.common.utils.AppUtils;

public class LocaleStateSelect extends OptionTag {

	private static final long serialVersionUID = 1L;	
	
	
	@Override
	protected List<Option> makeOptionList() throws Exception {
		Connection conn = null;
		try {
			conn = AppUtils.getDBCPConn();
			List<Option> optionList = new ArrayList<Option>();
			
			Locale locale = new Locale();
			List<Locale> localeList = new ArrayList<Locale>();
			locale.setLocaleTypeId(LocaleType.STATE.name());
			localeList = Locale.cast(locale.selectSome(conn));
			
			Collections.sort(localeList, new Comparator<Locale>() {
				public int compare(Locale o1, Locale o2) {
					return o1.getName().compareTo(o2.getName());
				}
			});
			for ( Locale l : localeList ) {
				optionList.add(new Option(String.valueOf(l.getStateName()), l.getName()));
			}
			
			return optionList;
		} catch (Exception e) {
			e.printStackTrace();
			throw new JspException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}
	}
	
	
	
	
	
	
	
}
