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
<%@ taglib tagdir="/WEB-INF/tags/webthing" prefix="ansi" %>



<tiles:insert page="layout.jsp" flush="true">

    <tiles:put name="title" type="string">
        Login
    </tiles:put>
    
    
    <tiles:put name="headextra" type="string">
        <style type="text/css">

        </style>
        <script src="js/login.js"></script>        
    </tiles:put>
    
    
    <tiles:put name="content" type="string">
    	<form action="#" method="post">
			<table>
				<tr>
					<td>User Id:</td>
					<td><input type="text" name="userid" /></td>
					<td><div class="err" id="useridMsg"></div></td>
				</tr>
				<tr>
					<td>Password:</td>
					<td><input type="password" name="password" /></td>
					<td><div class="err" id="passwordMsg"></div></td>
				</tr>
				<tr>
					<td colspan="2" style="text-align:center;">
						<input type="button" value="Go" id="goButton" />
					</td>
				</tr>
			</table>
		</form>
    </tiles:put>

</tiles:insert>

