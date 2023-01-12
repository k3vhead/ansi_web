package com.ansi.scilla.web.job.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Level;

import com.ansi.scilla.common.db.JobTag;
import com.ansi.scilla.web.common.request.RequestValidator;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.servlet.AbstractCrudServlet;
import com.ansi.scilla.web.common.servlet.PermittedAction;
import com.ansi.scilla.web.common.utils.FieldMap;
import com.ansi.scilla.web.common.utils.JsonFieldFormat;
import com.ansi.scilla.common.utils.Permission;

public class JobTagServlet extends AbstractCrudServlet {

	private static final long serialVersionUID = 1L;
	
	private static final String REALM = "jobTag";
	
	private static final String TAG_ID = "tagId";
	private static final String TAG_TYPE = "tagType";
	private static final String ABBREV = "abbrev";
	private static final String LONG_CODE = "longCode";
	private static final String DESCRIPTION = "description";
	private static final String STATUS = "status";
	
	private static List<FieldMap> fieldMap;

	static {
		fieldMap = new ArrayList<FieldMap>();
		fieldMap.add(new FieldMap(TAG_ID, JobTag.TAG_ID, JsonFieldFormat.INTEGER, false));
		fieldMap.add(new FieldMap(TAG_TYPE, JobTag.TAG_TYPE, JsonFieldFormat.STRING, true));
		fieldMap.add(new FieldMap(ABBREV, JobTag.ABBREV, JsonFieldFormat.STRING, true));
		fieldMap.add(new FieldMap(LONG_CODE, JobTag.LONG_CODE, JsonFieldFormat.STRING, true));
		fieldMap.add(new FieldMap(DESCRIPTION, JobTag.DESCRIPTION, JsonFieldFormat.STRING, true));
		fieldMap.add(new FieldMap(STATUS, JobTag.STATUS, JsonFieldFormat.STRING, true));
	}
	
//	private static final String selectSql = "select tag_type, name, 'some sort of desc' as description, status from job_tag";
	
	public JobTagServlet() {
		super();
//		super.setDisplaySql(selectSql);
		super.setPermittedActionList(Arrays.asList(new PermittedAction[] { PermittedAction.ADD, PermittedAction.GET, PermittedAction.DELETE, PermittedAction.UPDATE }));
	}
	

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		logger.log(Level.DEBUG, "doGet");
		processGet(request, response, 
				Permission.QUOTE_READ, 
				REALM, 
				new String[] { ACTION_IS_GET, ACTION_IS_LIST },
				new JobTag(), 
				fieldMap);
	}


	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		logger.log(Level.DEBUG, "doPost");
		processPost(request, response, 
				Permission.JOBTAG_WRITE, 
				REALM, 
				new String[] { ACTION_IS_ADD, ACTION_IS_EDIT },
				new JobTag(), 
				fieldMap);
	}


	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		super.processDelete(
				request, 
				response, 
				Permission.JOBTAG_WRITE, 
				REALM, 
				new String[] {ACTION_IS_DELETE}, 
				new JobTag(), 
				fieldMap);
	}


	@Override
	protected WebMessages validateAdd(Connection conn, HashMap<String, Object> addRequest) throws Exception {
		WebMessages webMessages = new WebMessages();
		RequestValidator.validateString(webMessages, DESCRIPTION, (String)addRequest.get(DESCRIPTION), 128, true, null);
		RequestValidator.validateTagType(webMessages, TAG_TYPE, (String)addRequest.get(TAG_TYPE), true);
		RequestValidator.validateTagStatus(webMessages, STATUS, (String)addRequest.get(STATUS), true);
		RequestValidator.validateString(webMessages, ABBREV, (String)addRequest.get(ABBREV), 45, true, null);
		RequestValidator.validateString(webMessages, LONG_CODE, (String)addRequest.get(LONG_CODE), 45, true, null);
		if ( webMessages.isEmpty()) {
			// name must be unique within type
			RequestValidator.validateTagAbbrev(conn, webMessages, ABBREV, (String)addRequest.get(TAG_TYPE),  (String)addRequest.get(ABBREV), true);
			RequestValidator.validateTagCode(conn, webMessages, LONG_CODE, (String)addRequest.get(TAG_TYPE),  (String)addRequest.get(LONG_CODE), true);
		}		
		return webMessages;
	}

	@Override
	protected WebMessages validateUpdate(Connection conn, HashMap<String, Object> updateRequest) throws Exception {
		WebMessages webMessages = new WebMessages();
		/* We don't need this line because the id is validated in the Abstract servlet. It returns a 404 if the id is invalid */
//		RequestValidator.validateId(conn, webMessages, JobTag.TABLE, JobTag.TAG_ID, TAG_ID, id, true);
		RequestValidator.validateString(webMessages, DESCRIPTION, (String)updateRequest.get(DESCRIPTION), 128, true, null);
		RequestValidator.validateTagType(webMessages, TAG_TYPE, (String)updateRequest.get(TAG_TYPE), true);
		RequestValidator.validateTagStatus(webMessages, STATUS, (String)updateRequest.get(STATUS), true);
		RequestValidator.validateTagAbbrev(conn, webMessages, ABBREV, (String)updateRequest.get(TAG_TYPE),  (String)updateRequest.get(ABBREV), true);
		RequestValidator.validateTagCode(conn, webMessages, LONG_CODE, (String)updateRequest.get(TAG_TYPE),  (String)updateRequest.get(LONG_CODE), true);
		return webMessages;
	}

}
