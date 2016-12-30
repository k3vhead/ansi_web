<%@ page contentType="text/html"%>

<%@ taglib uri="WEB-INF/c.tld" prefix="c"%>
<%@ taglib uri="WEB-INF/sql.tld" prefix="sql"%>
<%@ taglib uri="WEB-INF/fmt.tld" prefix="fmt" %>
<%@ taglib uri="WEB-INF/struts-html.tld"  prefix="html"  %>
<%@ taglib uri="WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="WEB-INF/struts-bean.tld"  prefix="bean"  %>
<%@ taglib uri="WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib tagdir="/WEB-INF/tags/webthing" prefix="webthing"%>
<%@ taglib uri="WEB-INF/theTagThing.tld" prefix="ansi" %>

<%@ page errorPage="errorHandler.jsp" %>

<!DOCTYPE html>
<html>
    <head>
	    <meta charset="utf-8">
    	<meta http-equiv="X-UA-Compatible" content="IE=edge">
    	<meta name="viewport" content="width=device-width, initial-scale=1">
    	<%-- 
    	<link rel="stylesheet" type="text/css" href="js/bootstrap-3.3.7-dist/css/bootstrap.min.css">
 	    --> 
    	<!-- Optional Bootstrap theme -->
    	<!-- 
    	<link rel="stylesheet" type="text/css" href="js/bootstrap-3.3.7-dist/css/bootstrap-theme.min.css" />
		--%>
		    	
        <title>ANSI Scheduling - <tiles:insert attribute="title" /></title>
        <link rel="SHORTCUT ICON" href="images/favicon.ico" />
        <script type="text/javascript" src="jQuery/jquery-3.1.1.min.js"></script>        
        <script type="text/javascript" src="jQuery/jquery-ui.min.js"></script>
        <script type="text/javascript" src="jQuery/jcookie.js"></script>
        <script type="text/javascript" src="jQuery/jquery.bpopup.min.js"></script>
        <!-- <script type="text/javascript" src="jQuery/jquery.popmenu.js"></script> -->
    	<link rel="stylesheet" href="//code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css" />
    	
    	<%-- 
    	<script src="js/bootstrap-3.3.7-dist/js/bootstrap.min.js"></script>
        --%>
         
        <link rel="stylesheet" href="font-awesome/css/font-awesome.min.css">   
        <link href="https://fonts.googleapis.com/css?family=Roboto+Condensed" rel="stylesheet">
        
        <link rel="stylesheet" href="css/menuStyle.css" type="text/css" media="screen, projection"/>
		<!--[if lte IE 7]>
        	<link rel="stylesheet" type="text/css" href="css/menuIe.css" media="screen" />
    	<![endif]-->
		<script type="text/javascript" language="javascript" src="js/menu.js"></script>
        
                     
        <link rel="stylesheet" href="css/layout.css" type="text/css" />
        <link rel="stylesheet" href="css/style.css" type="text/css" />
    	
        <script src="js/ansi.js"></script>
       
        <tiles:insert attribute="headextra" />
    </head>
    <body>
    	<div id="pageContainer">
    		
	    	<img src="images/ansi_logo-symbol.png"  />
	    	<img src="images/ansi_logo-words.png" /><br />
	    	
	    	<ansi:loggedIn>
				<div id="headerNav">
	    		
		    		<div  style="float:right; width:30%;">
			    		<ul class="dropdown">
			    			<ansi:hasPermission permissionRequired="SYSADMIN">
			    				<ansi:hasWrite>
			        			<li>
			        				<a href="#">Settings</a>
			        				<ul class="sub_menu">
			        			 		<li><a href="#">Message Maintenance</a></li>
			        			 		<li><html:link action="codeMaintenance">Code Maintenance</html:link></li>
			        			 		<li><html:link action="userAdmin">User Admin</html:link></li>
			        			 		<li><html:link action="permissiongroupAdmin">Permission Group Admin</html:link></li> 
										<li><html:link action="divisionAdmin">Division Admin</html:link></li>
										<li><html:link action="applicationpropertyAdmin">Application Property Admin</html:link></li>
										<li><html:link action="printHistory">Print History</html:link></li>
										<li><html:link action="usertitleMaintenance">User Title Maintenance</html:link></li>
										<li><html:link action="transactionhistoryView">Transaction History View</html:link></li>
			        				</ul>
			        			</li>
			        			</ansi:hasWrite>
		        			</ansi:hasPermission>
		        			<li>
		        				<a href="#">My ANSI</a>
		        				<ul class="sub_menu">
		        			 		<li><html:link action="myAccount">My Account</html:link></li>
		        			 		<li><html:link action="logoff">Logoff</html:link></li>
		        				</ul>
		        			</li>
			    		</ul>
			    	</div>
	    	
					<div style="float:left; width:65%">	    		    		
						<ul class="dropdown">
							<li><html:link action="dashboard">Dashboard</html:link></li>
		        			<li>
		        				<a href="#">Lookup</a>
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
											<li><html:link action="addressLookup">Lookup</html:link></li>
											<li><html:link action="addressMaintenance">Maintenance</html:link></li>
											<li><a href="#">New</a></li>
										</ul>
									</li>
									<li>
										<a href="#">Contacts</a>
										<ul>
											<!-- <li><html:link action="addressLookup">Lookup</html:link></li> -->
											<li><html:link action="contactMaintenance">Maintenance</html:link></li>
											<!-- <li><a href="#">New</a></li> -->
										</ul>
									</li>
									<ansi:hasPermission permissionRequired="QUOTE">
									<li>
										<a href="#">Quotes</a>
										<ul>
											<li><a href="#">Lookup</a></li>
											<li><html:link action="quoteMaintenance">Maintenance</html:link></li>
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
											<li><html:link action="jobMaintenance">Maintenance</html:link></li>
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
									<li>
										<a href="#">Tax Rates</a>
										<ul>
											<!-- <li><a href="#">Lookup</a></li> -->
											<li><html:link action="taxRateMaintenance">Maintenance</html:link></li>
											<!-- <li><a href="#">New</a></li> -->
										</ul>
									</li>
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
	        		</div>
        		</div>
        		<br />
	    	</ansi:loggedIn>
	    	<ansi:notLoggedIn>
	    		<div id="headerNav">
	    			&nbsp;
	    		</div>
	    	</ansi:notLoggedIn>
	    	
	    	
			<div class="mainContent">
				<div id="globalMsg" class="err">&nbsp;</div>
	    		<tiles:insert attribute="content" />
	    	</div>
	    	
	    	
	    	<div class="menuBar" style="width:100%; backgroud-color:#000000;">
	    		<div style="float:right; width:45%;">
		    		<div class="trailerText" style="float:right;">
		    			Powered by <a href="http://www.thewebthing.com" class="trailerLink" target="new">theWebThing</a>
		    		</div>
	    		</div>
	    		<div style="float:left; width:45%;">
		    		<div class="trailerText">
		    			&copy; 2016 American National Skyline, All Rights Reserved
		    		</div>
	    		</div>	  
	    		<div class="spacer">&nbsp;</div>  		
	    	</div>
	    	
    	</div>
    </body>
</html>
