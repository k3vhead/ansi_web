<%@ page contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<%@ taglib uri="WEB-INF/c.tld" prefix="c" %>
<%@ taglib uri="WEB-INF/sql.tld" prefix="sql" %>
<%@ taglib uri="WEB-INF/fmt.tld" prefix="fmt" %>
<%@ taglib uri="WEB-INF/struts-html.tld"  prefix="html"  %>
<%@ taglib uri="WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="WEB-INF/struts-bean.tld"  prefix="bean"  %>
<%@ taglib uri="WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib tagdir="/WEB-INF/tags/webthing" prefix="webthing" %>
<%@ taglib uri="WEB-INF/theTagThing.tld" prefix="ansi" %>

<% 
	// if we go through the servlet (from quote screen) parameters are set in the request attribute
	// if we go through a jsp include (from the job screen) parameters are set in a request parameter
	// figure out which one we've got, and use it
	String attrPanel = (String)request.getAttribute("panelname");
	String parmPanel = request.getParameter("panelname");	
	String panelname = attrPanel == null ? parmPanel : attrPanel;
	String namespace = (String)request.getAttribute("namespace");
	
	String attrPage = (String)request.getAttribute("page");
	String parmPage = request.getParameter("page");	
	String pageName = parmPage == null ? attrPage : parmPage;
	

%>
	
	<webthing:jobActivateCancel page="<%= pageName %>" namespace="<%= namespace %>" />


		