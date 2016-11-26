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



<tiles:insert page="layout.jsp" flush="true">

    <tiles:put name="title" type="string">
        Dashboard
    </tiles:put>
    
    
    <tiles:put name="headextra" type="string">
        <style type="text/css">

        </style>
        <script src="js/dashboard.js"></script>        
    </tiles:put>
    
    
    <tiles:put name="content" type="string">
		<h1>ANSI Scheduling Dashboard</h1>
		<br />
		<ansi:hasPermission permissionRequired="QUOTE">
			<a href="#">Quotes</a><br />
		</ansi:hasPermission>
		<ansi:hasPermission permissionRequired="JOB">
			<a href="#">Jobs</a><br />
		</ansi:hasPermission>
		<ansi:hasPermission permissionRequired="TICKET">
			<a href="#">Tickets</a><br />
		</ansi:hasPermission>
		<ansi:hasPermission permissionRequired="PAYMENT">
			<a href="#">Payments</a><br />
		</ansi:hasPermission>
		<ansi:hasPermission permissionRequired="INVOICE">
			<a href="#">Invoices</a><br />
		</ansi:hasPermission>
		<ansi:hasPermission permissionRequired="SYSADMIN">
			<a href="#">Permissions</a><br />
			<html:link action="codeMaintenance">Messages &amp; Codes</html:link><br />
		</ansi:hasPermission>
		<ansi:hasPermission permissionRequired="USER_ADMIN">
			<a href="#">User Admin</a><br />
		</ansi:hasPermission>
		<ansi:hasPermission permissionRequired="TECH_ADMIN">
			<a href="#">Tech Admin</a><br />
		</ansi:hasPermission>
		

		<h3>This is for demo purposes Only:</h3>		
		<br />
		<br />
		<ansi:hasPermission permissionRequired="INVOICE">
			Invoice Stuff goes here<br />
			<ansi:hasRead>
				Read only INVOICE<br />
			</ansi:hasRead>
			<ansi:hasWrite>
				Write INVOICE<br />
			</ansi:hasWrite>
		</ansi:hasPermission>
		<br />
		<ansi:hasPermission permissionRequired="SYSADMIN">
			Sysadmin Stuff goes here<br />
			<ansi:hasRead>
				Read only SYSADMIN<br />
			</ansi:hasRead>
			<ansi:hasWrite>
				Write SYSADMIN<br />
			</ansi:hasWrite>
		</ansi:hasPermission>
		<br />
		<br />
		<ansi:loggedIn>
			There is a valid login
		</ansi:loggedIn>
    </tiles:put>

</tiles:insert>

