package com.ansi.scilla.web.action;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.web.common.action.AbstractAction;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.BuildDate;

public class DeployedTaskAction extends AbstractAction {

	
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		
		try {
			String webResource="resources/completedTask.properties";
//			System.out.println(webResource);
			InputStream webStream = BuildDate.class.getClassLoader().getResourceAsStream(webResource);
			List<AsanaListItem> taskList = makeTaskList(webStream);
			webStream.close();
			request.setAttribute("DeployedTaskAction_task_list", taskList);
			
		} catch ( Exception e ) {
			AppUtils.logException(e);
			
		}

		
		return mapping.findForward(FORWARD_IS_VALID);
	}

	
	private List<AsanaListItem> makeTaskList(InputStream is) throws IOException {
		List<AsanaListItem> taskList = new ArrayList<AsanaListItem>();
		Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } finally {
            is.close();
        }
        String propertyString = writer.toString();
        String[] lines = propertyString.split("\n");
        for ( String line : lines ) {
        	taskList.add(new AsanaListItem(line));
        }
        
        return taskList;
	}

	public class AsanaListItem extends ApplicationObject {
		private static final long serialVersionUID = 1L;
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		public Date modifiedAt;
		public String name;
		public AsanaListItem(String line)  {
			super();
			String[] pieces = line.split(":");
			if ( pieces.length == 2 ) {
				try {
					this.modifiedAt = sdf.parse(pieces[0]);
				} catch (ParseException e) {
					// this isn't good, but we probably don't care that much
				}
				this.name = pieces[1];				
			} else if (pieces.length == 1) {
				this.name = pieces[0];
			} else {
				this.name = line;
			}
		}
		public Date getModifiedAt() {
			return modifiedAt;
		}
		public String getName() {
			return name;
		}
		
	}
	
}
