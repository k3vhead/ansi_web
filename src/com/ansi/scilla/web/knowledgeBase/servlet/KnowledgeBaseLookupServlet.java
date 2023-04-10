package com.ansi.scilla.web.knowledgeBase.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections4.Transformer;
import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.common.db.KnowledgeBase;
import com.ansi.scilla.web.common.query.LookupQuery;
import com.ansi.scilla.web.common.servlet.AbstractLookupServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionDivision;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.knowledgeBase.query.KnowledgeBaseLookupQuery;

public class KnowledgeBaseLookupServlet extends AbstractLookupServlet {

	private static final long serialVersionUID = 1L;
	
	public static final String REALM = "lookup";
	
	public static final String ABBREV_CONTENT = "abbreviatedContent";
	
	
	public KnowledgeBaseLookupServlet() {
		super(null); // anybody can do this
		cols = new String[] { 
				KnowledgeBaseLookupQuery.KB_KEY,
				KnowledgeBaseLookupQuery.KB_LANGUAGE,
				KnowledgeBaseLookupQuery.KB_STATUS,
				KnowledgeBaseLookupQuery.KB_TITLE,
				KnowledgeBaseLookupQuery.KB_CONTENT,
			};
		super.itemTransformer = new ItemTransformer();
	}


	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		super.doPost(request, response);
	}


	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		super.doGet(request, response);
	}


	@Override
	public LookupQuery makeQuery(Connection conn, HttpServletRequest request) {
		HttpSession session = request.getSession();
		SessionData sessionData = (SessionData)session.getAttribute(SessionData.KEY);
		
		SessionUser user = sessionData.getUser();
		List<SessionDivision> divisionList = sessionData.getDivisionList();
		String searchTerm = null;
		if(request.getParameter("search[value]") != null){
			searchTerm = request.getParameter("search[value]");
		}
		KnowledgeBaseLookupQuery lookupQuery = new KnowledgeBaseLookupQuery(user.getUserId(), divisionList);
		if ( searchTerm != null ) {
			lookupQuery.setSearchTerm(searchTerm);
		}
			return lookupQuery;
			
	}
	
	
	public class ItemTransformer implements Transformer<HashMap<String, Object>, HashMap<String,Object>> {

		@Override
		public HashMap<String, Object> transform(HashMap<String, Object> arg0) {
			String abbreviatedContent = null;
			if ( arg0.containsKey(KnowledgeBase.KB_CONTENT)) {
				String content = (String)arg0.get(KnowledgeBase.KB_CONTENT);
				if ( ! StringUtils.isEmpty(content)) {
					abbreviatedContent = StringUtils.abbreviate(AppUtils.stripHtml(content), 125); 
				}
			}
			arg0.put(ABBREV_CONTENT, abbreviatedContent);
			return arg0;
		}
		
	}
}
