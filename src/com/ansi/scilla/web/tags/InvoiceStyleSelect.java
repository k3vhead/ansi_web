package com.ansi.scilla.web.tags;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ansi.scilla.common.invoice.InvoiceStyle;

public class InvoiceStyleSelect extends OptionTag {

	private static final long serialVersionUID = 1L;	
	
	
	@Override
	protected List<Option> makeOptionList() throws Exception {
			List<Option> optionList = new ArrayList<Option>();
			
			for ( InvoiceStyle style : InvoiceStyle.values() ) {
				String display = style.active() ? style.display() : style.display() + " (Deprecated)";
				optionList.add( new Option(style.code(), display) );
			}
			
			Collections.sort(optionList);
			
			return optionList;
	}
	
	
	
	
	
	
	
}
