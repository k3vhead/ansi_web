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
        Login
    </tiles:put>
    
    
    <tiles:put name="headextra" type="string">
        <style type="text/css">
			.forgotPassLink {
				color:#000000;
				text-decoration:none;
				font-style:italic;
				font-size:12px;
				font-family:times roman,serif;
			}
			.bigText {
				font-size:35px;
			}
			#bottomSpace {
				clear:both; 
				width:100%;
				padding-top:50px;
			}
			#columnLeft {
				width:66%; 
				float:left; 
				margin-top:50px;
			}
			#columnRight {
				width:50%; 
				float:right;
			}
			#formDiv {
				border:solid 1px #000000; 
				padding:30px; 
				width:80%;
			}
        </style>
        <script src="js/login.js"></script>        
    </tiles:put>
    
    
    <tiles:put name="content" type="string">
   		<div id="columnLeft">
   			<div id="columnRight">
    			<div id="formDiv">
    				<h1 class="centered">
    					<span class="bigText">Welcome</span><br />
    					to ANSI Scheduling
    				</h1>
	    			<form action="#" method="post" id="loginForm">
						<table>
							<tr>
								<td colspan="2" class="centered">
									<div>&nbsp;<span class="err" id="loginMsg"></span></div>
								</td>
							</tr>
							<tr>
								<td class="formLabel"><span class="required">*</span> User Id: </td>
								<td><input type="text" name="userid" /></td>
								<td><i id="validUser" aria-hidden="true"></i></td>							
							</tr>
							<tr>
								<td class="formLabel"><span class="required">*</span> Password: </td>
								<td><input type="password" name="password" /></td>
								<td><i id="validPass" aria-hidden="true"></i></td>
							</tr>
							<tr>
								<td colspan="2" style="text-align:center;">
									<input type="submit" value="Login" id="goButton" class="prettyWideButton" />
								</td>
							</tr>
							<tr>
								<td colspan="2" style="text-align:right; vertica-align:top;">
									<a href="#" class="forgotPassLink">Forgot My Password</a>
								</td>
							</tr>
						</table>
					</form>
				</div>
   			</div>
   		</div>
		<div id="bottomSpace">
			&nbsp;
		</div>
    </tiles:put>

</tiles:insert>

