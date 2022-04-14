package com.ansi.scilla.web.tags;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;

import com.ansi.scilla.common.db.Locale;
import com.ansi.scilla.common.utils.LocaleType;
import com.ansi.scilla.web.common.utils.AppUtils;

public class LocaleSelect extends OptionTag {

	private static final long serialVersionUID = 1L;	
	
	private String type;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	

	
	@Override
	protected List<Option> makeOptionList() throws Exception {
		Connection conn = null;
		try {
			conn = AppUtils.getDBCPConn();
			LocaleType localeType = LocaleType.valueOf(this.type);
			List<Option> optionList = new ArrayList<Option>();
			
			Locale locale = new Locale();
			List<Locale> localeList = new ArrayList<Locale>();
			if ( localeType == null ) {
				localeList = Locale.cast(locale.selectAll(conn));
			} else {
				locale.setLocaleTypeId(localeType.name());
				localeList = Locale.cast(locale.selectSome(conn));
			}
			
			for ( Locale l : localeList ) {
				optionList.add(new Option(String.valueOf(l.getLocaleId()), l.getName()));
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
