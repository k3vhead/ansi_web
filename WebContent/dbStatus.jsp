<%@ page contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@ taglib tagdir="/WEB-INF/tags/webthing" prefix="webthing" %>

<%@ page import="java.sql.Connection,
					javax.naming.Context,
					javax.naming.InitialContext,
					javax.naming.NamingException,
					javax.servlet.http.HttpServletRequest,
					javax.servlet.http.HttpSession,
					org.apache.tomcat.dbcp.dbcp2.BasicDataSource" %>
<%
Context ctx = new InitialContext();
BasicDataSource ds = (BasicDataSource)ctx.lookup("java:comp/env/jdbc/ansi");
ds.setLogAbandoned(true);
ds.setTestOnBorrow(true);

String url = ds.getUrl();
Integer initalSize = ds.getInitialSize();
Long maxLifeTime = ds.getMaxConnLifetimeMillis();
Integer maxIdle = ds.getMaxIdle();
Integer maxTotal = ds.getMaxTotal();
Integer numActive = ds.getNumActive();
Integer numIdle = ds.getNumIdle();

%>
<html>
<head>
	<title>DB Status</title>
	<style type="text/css">
		.formHdr { font-weight:bold; }
	</style>
</head>
<body>
    	<h1>ANSI DB Status</h1>
    	<table>
    		<tr><td class="formHdr">URL: </td><td><%= url %></td></tr>
			<tr><td class="formHdr">Initial Size: </td><td><%= initalSize %></td></tr>
			<tr><td class="formHdr">Max Lifetime: </td><td><%= maxLifeTime %></td></tr>
			<tr><td class="formHdr">Max Idle: </td><td><%= maxIdle %></td></tr>
			<tr><td class="formHdr">Max Total: </td><td><%= maxTotal %></td></tr>
			<tr><td class="formHdr">Num Active: </td><td><%= numActive %></td></tr>
			<tr><td class="formHdr">Num Idle: </td><td><%= numIdle %></td></tr>
    	</table>
    	
</body>
</html>
		