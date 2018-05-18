<%@ tag description="" body-content="scriptless" %> <%@ attribute name="jobId" required="true" rtexprvalue="true" %><%@ attribute name="styleClass" required="false" rtexprvalue="true" %><%@ attribute name="styleId" required="false" rtexprvalue="true" %><%@ attribute name="style" required="false" rtexprvalue="true" %><%
	String jobId = (String)jspContext.getAttribute("jobId"); 
    String className = (String)jspContext.getAttribute("styleClass"); 
	String idName = (String)jspContext.getAttribute("styleId");
	String styleName = (String)jspContext.getAttribute("style");
	String jobString = "data-jobid=" + jobId;
	String classString = className == null ? "" : className;
	String idString = idName == null ? "" : "id=\"" + idName + "\"";
	String styleString = styleName == null ? "" : "style=\"cursor:pointer; " + styleName + "\"";
%><span class="fa fa-list-alt fa-2x tooltip <%= classString %>" <%= idString %> <%= styleString %> <%= jobString %> ari-hidden="true"><span class="tooltiptext"><jsp:doBody /></span></span>