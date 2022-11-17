package com.ansi.scilla.web.document.servlet;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections4.Transformer;

import com.ansi.scilla.common.document.DocumentType;
import com.ansi.scilla.web.common.query.LookupQuery;
import com.ansi.scilla.web.common.servlet.AbstractLookupServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionDivision;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.common.utils.AnsiURL;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.common.utils.Permission;
import com.ansi.scilla.web.document.query.DocumentLookupQuery;
import com.ansi.scilla.web.exceptions.ResourceNotFoundException;

public class DocumentLookupServlet extends AbstractLookupServlet {

	private static final long serialVersionUID = 1L;
	
	public static final String REALM = "documentLookup";
	
	public static final String DOCUMENT_ID = "document_id";
	public static final String DESCRIPTION = "description";
	public static final String DOCUMENT_DATE = "document_date";
	public static final String EXPIRATION_DATE = "expiration_date";
	public static final String XREF_TYPE = "xref_type";
	public static final String XREF_ID = "xref_id";
	public static final String XREF_DISPLAY = "case\n" + 
			"when xref_type = 'SIGNED_CONTRACT' then concat(quote.quote_number,quote.revision)\n" + 
			"when xref_type = 'TAX_EXEMPT' then address.name\n" + 
			"end";
	public static final String XREF_TYPE_DISPLAY = "xref_type_display";

	public DocumentLookupServlet(Permission documentRead) {
		super(Permission.DOCUMENT_READ);
		cols = new String[] { 
				DOCUMENT_ID,
				XREF_TYPE,
				DESCRIPTION,
				DOCUMENT_DATE,
				EXPIRATION_DATE,				
				XREF_DISPLAY,
				};
		super.itemTransformer = new ItemTransformer();
	}


	


	@Override
	public LookupQuery makeQuery(Connection conn, HttpServletRequest request) {
		HttpSession session = request.getSession();
		SessionData sessionData = (SessionData)session.getAttribute(SessionData.KEY);
		
		
		
		SessionUser user = sessionData.getUser();
		List<SessionDivision> divisionList = sessionData.getDivisionList();
		
		try {
			
			AnsiURL url = new AnsiURL(request, REALM, (String[])null);
			String searchTerm = null;
			if(request.getParameter("search[value]") != null){
				searchTerm = request.getParameter("search[value]");
			}
			DocumentLookupQuery lookupQuery = new DocumentLookupQuery(user.getUserId(), divisionList);
			if ( searchTerm != null ) {
				lookupQuery.setSearchTerm(searchTerm);
			}
//			if ( url.getId() != null ) {
//				lookupQuery.setTicketFilter(url.getId());
//			}
			return lookupQuery;
		} catch (ResourceNotFoundException e) {
			throw new RuntimeException(e);
		}
	}




	public class ItemTransformer implements Transformer<HashMap<String, Object>, HashMap<String, Object>> {

		private final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		
		@Override
		public HashMap<String, Object> transform(HashMap<String, Object> arg0) {
			java.sql.Date documentDate = (java.sql.Date)arg0.get(DOCUMENT_DATE);
			if ( documentDate != null ) {
				String documentDateDisplay = sdf.format(documentDate);
				arg0.put(DOCUMENT_DATE, documentDateDisplay);
			}
			
			java.sql.Date expirationDate = (java.sql.Date)arg0.get(EXPIRATION_DATE);
			if ( expirationDate != null ) {
				String expirationDateDisplay = sdf.format(expirationDate);
				arg0.put(EXPIRATION_DATE, expirationDateDisplay);
			}
			
			DocumentType documentType = DocumentType.valueOf((String)arg0.get(XREF_TYPE));
			arg0.put(XREF_TYPE_DISPLAY, documentType.description());
			return arg0;
		}
		
	}


	
	
}
