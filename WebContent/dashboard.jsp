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
    	<div style="float:right; text-align:right; width:30%;">
    		<span style="font-weight:bold;">Hello <c:out value="${com_ansi_scilla_session_data.user.firstName}" /></span>
    	</div>
		<h1>ANSI Scheduling Dashboard</h1>
		<br />
		<table>
			<tr>
				<td>Lookup</td>
				<td>Reports</td>
				<td></td>
				<td></td>
				<td></td>
				<td></td>
			</tr>
			<tr>
				<td><a href="#">Addresses</a></td>
				<td></td>
			</tr>
			<tr>
				<td>
					<ansi:hasPermission permissionRequired="PAYMENT">
					<a href="#">Payments</a>
					</ansi:hasPermission>
				</td>
				<td></td>
				<td></td>
				<td></td>
				<td></td>
				<td></td>
				<td></td>
			</tr>
			<tr>
				<td>
					<ansi:hasPermission permissionRequired="QUOTE">
						<a href="#">Quotes</a>
					</ansi:hasPermission>
				</td>
				<td></td>
				<td></td>
				<td></td>
				<td></td>
				<td></td>
				<td></td>
			</tr>
			<tr>
				<td><ansi:hasPermission permissionRequired="JOB">
					<a href="#">Jobs</a>
					</ansi:hasPermission></td>
				<td></td>
				<td></td>
				<td></td>
				<td></td>
				<td></td>
				<td></td>
			</tr>
			<tr>
				<td><ansi:hasPermission permissionRequired="TICKET">
					<a href="#">Tickets</a>
					</ansi:hasPermission></td>
				<td></td>
				<td></td>
				<td></td>
				<td></td>
				<td></td>
				<td></td>
			</tr>
			<tr>
				<td><ansi:hasPermission permissionRequired="INVOICE">
					<a href="#">Invoices</a>
					</ansi:hasPermission></td>
				<td></td>
				<td></td>
				<td></td>
				<td></td>
				<td></td>
				<td></td>
			</tr>
		</table>
					
					
					<div  style="clear:both; width:100%;">
					<h1><a href="http://www.quotes-day.com/">Quote of the Day</a> for Wednesday December 14, 2016</h1><br /><h2>Positive Quote of the Day</h2><p><em>The winds of grace are always blowing;<br />all we need to do is raise our sails.<br />- <a href=http://www.quotes-day.com/by/anonymous/>Anonymous</a></em></p><h2>Funny Quote of the Day</h2><p><em>I got a perfect build for clothes.<br />I'm a twenty-eight dwarf.<br />- <a href=http://www.quotes-day.com/by/love-and-death/>Love and Death</a></em></p><h2>Love Quote of the Day</h2><p><em>As we grow old, the beauty steals inward.<br />- <a href=http://www.quotes-day.com/by/ralph-waldo-emerson/>Ralph Waldo Emerson</a></em></p><p>Also see <a href="http://www.lifesayingsquotes.com/proverbs.php">Proverbs</a></p>

					</div>
					
					
				        				
		
		
		
		
					    		<ul>
			    			<ansi:hasPermission permissionRequired="SYSADMIN">
			    				<ansi:hasWrite>
			        			<li>
			        				<a href="#">Settings</a>
			        				<ul class="sub_menu">
			        			 		<li><a href="#">Message Maintenance</a></li>
			        			 		<li><html:link action="codeMaintenance">Code Maintenance</html:link></li>
			        			 		<li><a href="#">User Admin</a></li>
			        			 		<li><a href="#">Permission Groups</a></li> 
										<li><html:link action="DivisionAdmin">Division Admin</html:link></li>
			        				</ul>
			        			</li>
			        			</ansi:hasWrite>
		        			</ansi:hasPermission>
		        			<li>
		        				<a href="#">My ANSI</a>
		        				<ul class="sub_menu">
		        			 		<li><a href="#">My Account</a></li>
		        			 		<li><html:link action="logoff">Logoff</html:link></li>
		        				</ul>
		        			</li>
							<li><html:link action="dashboard">Dashboard</html:link></li>
		        			<li><a href="#">Reports</a>
		        				<ul class="sub_menu">
									<li><a href="#">Addresses</a></li>
									<ansi:hasPermission permissionRequired="QUOTE">
									<li><a href="#">Quotes</a></li>
									</ansi:hasPermission>
									<ansi:hasPermission permissionRequired="JOB">
									<li><a href="#">Jobs</a></li>
									</ansi:hasPermission>
									<ansi:hasPermission permissionRequired="TICKET">
									<li><a href="#">Tickets</a></li>
									</ansi:hasPermission>
									<ansi:hasPermission permissionRequired="INVOICE">
									<li><a href="#">Invoices</a></li>
									</ansi:hasPermission>
									<ansi:hasPermission permissionRequired="PAYMENT">
									<li><a href="#">Payments</a></li>
									</ansi:hasPermission>
		        				</ul>
		        			</li>
		        			<li>
		        				<a href="#">QuickLinks</a>
		        				<ul class="sub_menu">
									<li>
										<a href="#">Addresses</a>
										<ul>
											<li><a href="#">Lookup</a></li>
											<li><a href="#">Maintenance</a></li>
											<li><a href="#">New</a></li>
										</ul>
									</li>
									<ansi:hasPermission permissionRequired="QUOTE">
									<li>
										<a href="#">Quotes</a>
										<ul>
											<li><a href="#">Lookup</a></li>
											<li><a href="#">Maintenance</a></li>
											<ansi:hasWrite>
											<li><a href="#">New</a></li>
											<li><a href="#">Templates</a></li>
											</ansi:hasWrite>
										</ul>
									</li>
									</ansi:hasPermission>
									<ansi:hasPermission permissionRequired="JOB">
									<li>
										<a href="#">Jobs</a>
										<ul>
											<li><a href="#">Lookup</a></li>
											<ansi:hasWrite>
											<li><a href="#">Maintenance</a></li>
											<li><a href="#">New</a></li>
											</ansi:hasWrite>
										</ul>
									</li>
									</ansi:hasPermission>
									<ansi:hasPermission permissionRequired="TICKET">
									<li>
										<a href="#">Tickets</a>
										<ul>
											<li><a href="#">Lookup</a></li>
											<ansi:hasWrite>
											<li><a href="#">Maintenance</a></li>
											<li><a href="#">New</a></li>
											</ansi:hasWrite>											
										</ul>
									</li>
									</ansi:hasPermission>
									<ansi:hasPermission permissionRequired="INVOICE">
									<li>
										<a href="#">Invoices</a>
										<ul>
											<li><a href="#">Lookup</a></li>
											<ansi:hasWrite>
											<li><a href="#">Maintenance</a></li>
											<li><a href="#">New</a></li>
											<li><a href="#">Templates</a></li>
											</ansi:hasWrite>
										</ul>
									</li>
									</ansi:hasPermission>
									<ansi:hasPermission permissionRequired="PAYMENT">
									<li>
										<a href="#">Payments</a>
										<ul>
											<li><a href="#">Lookup</a></li>
											<ansi:hasWrite>
											<li><a href="#">Maintenance</a></li>
											<li><a href="#">New</a></li>
											</ansi:hasWrite>
										</ul>
									</li>
									</ansi:hasPermission>
		        				</ul>
		        			</li>
		        		</ul>
		
		
		
		
		
		
		
		
		
		
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

