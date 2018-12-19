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
<%@ taglib tagdir="/WEB-INF/tags/quote" prefix="quote" %>
<%@ taglib uri="WEB-INF/theTagThing.tld" prefix="ansi" %>


<ansi:hasPermission permissionRequired="QUOTE_CREATE">
    <div id="address-edit-modal" class="edit-modal">		    	
    	<span class="formLabel">Address:</span> <input type="text" name="address-name" />
    	<span class="errMsg err"></span>
    	<br /><hr /><br />
    	<quote:addressDisplayPanel id="address-edit-display" label="Name" />
    	<div class="none-found">
    		<span class="err">No Matching Addresses</span>
    	</div>
    </div>
</ansi:hasPermission>
   
<ansi:hasPermission permissionRequired="QUOTE_CREATE">
   	<div id="job-edit-modal" class="edit-modal">
   		<div class="job-edit-panel proposal">
   			<jsp:include page="quoteMaintenance/jobProposalEditPanel.jsp" />
   		</div>
   		<div class="job-edit-panel activation">
   			<jsp:include page="quoteMaintenance/jobActivationEditPanel.jsp" />
   		</div>
   		<div class="job-edit-panel invoice">
   			<jsp:include page="quoteMaintenance/jobInvoiceEditPanel.jsp" />
   		</div>
   		<div class="job-edit-panel schedule">
   			<jsp:include page="quoteMaintenance/jobScheduleEditPanel.jsp" />
   		</div>
   	</div>
</ansi:hasPermission>