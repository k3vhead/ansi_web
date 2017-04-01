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
        <!-- <link rel="SHORTCUT ICON" href="images/favicon.ico" /> -->
        <link rel="icon" type="image/png" href="favicon-16x16.png" sizes="16x16">
        <link rel="icon" type="image/png" href="favicon-32x32.png" sizes="32x32">
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
		<script type="text/javascript" src="js/menu.js"></script>
        
                     
        <link rel="stylesheet" href="css/layout.css" type="text/css" />
        <link rel="stylesheet" href="css/style.css" type="text/css" />
    	
        <link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/v/dt/dt-1.10.13/datatables.min.css"/>
 		<link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/buttons/1.2.4/css/buttons.dataTables.min.css"/>
 		

		<script type="text/javascript" src="https://cdn.datatables.net/v/dt/dt-1.10.13/datatables.min.js"></script>
		
		<script type="text/javascript" src="https://cdn.datatables.net/buttons/1.2.4/js/dataTables.buttons.min.js"></script>
 		
 		<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/jszip/2.5.0/jszip.min.js"></script>
 		<script type="text/javascript" src="https://cdn.rawgit.com/bpampuch/pdfmake/0.1.18/build/pdfmake.min.js"></script>
 		<script type="text/javascript" src="https://cdn.rawgit.com/bpampuch/pdfmake/0.1.18/build/vfs_fonts.js"></script>
 		<script type="text/javascript" src="https://cdn.datatables.net/buttons/1.2.4/js/buttons.html5.min.js"></script>
 		<script type="text/javascript"  src="https://cdn.datatables.net/buttons/1.2.4/js/buttons.print.min.js"></script>
       	<script type="text/javascript"  src="https://cdn.datatables.net/buttons/1.2.4/js/buttons.colVis.min.js"></script>
       
       
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
			        				<a href="#"><bean:message key="menu.label.settings" /></a>
			        				<ul class="sub_menu" style="z-index:1000">
			        			 		<li><a href="#"><bean:message key="menu.label.messages" /></a></li>
			        			 		<li><html:link action="codeMaintenance"><bean:message key="menu.label.codes" /></html:link></li>
			        			 		<li><html:link action="userAdmin"><bean:message key="menu.label.users" /></html:link></li>
			        			 		<li><html:link action="permissiongroupAdmin"><bean:message key="menu.label.permissions" /></html:link></li> 
										<li><html:link action="divisionAdmin"><bean:message key="menu.label.divisions" /></html:link></li>
										<li><html:link action="applicationpropertyAdmin"><bean:message key="menu.label.properties" /></html:link></li>
										<li><html:link action="printHistory"><bean:message key="menu.label.printhistory" /></html:link></li>
										<li><html:link action="usertitleMaintenance"><bean:message key="menu.label.titles" /></html:link></li>
										<li><html:link action="transactionhistoryView"><bean:message key="menu.label.transactionhistory" /></html:link></li>
			        				</ul>
			        			</li>
			        			</ansi:hasWrite>
		        			</ansi:hasPermission>
		        			<li>
		        				<a href="#">My ANSI</a>
		        				<ul class="sub_menu" style="z-index:1000">
		        			 		<li><html:link action="myAccount">My Account</html:link></li>
		        			 		<li><html:link action="logoff">Logoff</html:link></li>
		        				</ul>
		        			</li>
		        			<li>
			    		</ul>
			    		<div style="padding-top:4px; padding-left:4px;"><c:out value="${com_ansi_scilla_session_data.user.firstName}" /> <c:out value="${com_ansi_scilla_session_data.user.lastName}" /></div>
			    	</div>
	    	
					<div style="float:left; width:65%">	    		    		
						<ul class="dropdown">
							<li><html:link action="dashboard"><bean:message key="menu.label.dashboard" /></html:link></li>
		        			<li>
		        				<a href="#"><bean:message key="menu.label.lookup" /></a>
								<ul class="sub_menu" style="z-index:1000">
									<li><html:link action="addressMaintenance"><bean:message key="menu.label.addresses" /></html:link></li>
									<ansi:hasPermission permissionRequired="QUOTE">
									<li><html:link action="quoteLookup"><bean:message key="menu.label.quotes" /></html:link></li>
									</ansi:hasPermission>
									<ansi:hasPermission permissionRequired="JOB">
									<li><html:link action="jobLookup"><bean:message key="menu.label.jobs" /></html:link></li>
									</ansi:hasPermission>
									<ansi:hasPermission permissionRequired="TICKET">
									<li><html:link action="ticketLookup"><bean:message key="menu.label.tickets" /></html:link></li>
									</ansi:hasPermission>
									<ansi:hasPermission permissionRequired="INVOICE">
									<li><html:link action="invoiceLookup"><bean:message key="menu.label.invoices" /></html:link></li>
									</ansi:hasPermission>
									<ansi:hasPermission permissionRequired="PAYMENT">
									<li><html:link action="paymentLookup"><bean:message key="menu.label.payments" /></html:link></li>
									</ansi:hasPermission>
								</ul>
		        			</li>
		        			<li><a href="#"><bean:message key="menu.label.reports" /></a>
		        				<ul class="sub_menu" style="z-index:1000">
									<li><a href="#"><bean:message key="menu.label.addresses" /></a></li>
									<ansi:hasPermission permissionRequired="QUOTE">
									<li><a href="#"><bean:message key="menu.label.quotes" /></a></li>
									</ansi:hasPermission>
									<ansi:hasPermission permissionRequired="JOB">
									<li><a href="#"><bean:message key="menu.label.jobs" /></a></li>
									</ansi:hasPermission>
									<ansi:hasPermission permissionRequired="TICKET">
									<li><a href="#"><bean:message key="menu.label.tickets" /></a></li>
									</ansi:hasPermission>
									<ansi:hasPermission permissionRequired="INVOICE">
									<li><a href="#"><bean:message key="menu.label.invoices" /></a></li>
									</ansi:hasPermission>
									<ansi:hasPermission permissionRequired="PAYMENT">
									<li><a href="#"><bean:message key="menu.label.payments" /></a></li>
									</ansi:hasPermission>
		        				</ul>
		        			</li>
		        			<li>
		        				<a href="#"><bean:message key="menu.label.quick" /></a>
		        				<ul class="sub_menu" style="z-index:1000">
									<li>
										<a href="#"><bean:message key="menu.label.addresses" /></a>
										<ul>
											<li><html:link action="addressMaintenance"><bean:message key="menu.label.maintenance" /></html:link></li>
											<!-- <li><a href="#">New</a></li> -->
										</ul>
									</li>
									<li>
										<a href="#"><bean:message key="menu.label.contacts" /></a>
										<ul>
											<!-- <li><html:link action="addressLookup">Lookup</html:link></li> -->
											<li><html:link action="contactMaintenance"><bean:message key="menu.label.maintenance" /></html:link></li>
											<!-- <li><a href="#">New</a></li> -->
										</ul>
									</li>
									<ansi:hasPermission permissionRequired="QUOTE">
									<li>
										<a href="#"><bean:message key="menu.label.quotes" /></a>
										<ul>
											<li><html:link action="quoteLookup"><bean:message key="menu.label.lookup" /></html:link></li>
											<li><html:link action="quoteMaintenance"><bean:message key="menu.label.maintenance" /></html:link></li>
											<ansi:hasWrite>
											<li><a href="#"><bean:message key="menu.label.new" /></a></li>
											<li><a href="#"><bean:message key="menu.label.templates" /></a></li>
											</ansi:hasWrite>
										</ul>
									</li>
									</ansi:hasPermission>
									<ansi:hasPermission permissionRequired="JOB">
									<li>
										<a href="#"><bean:message key="menu.label.jobs" /></a>
										<ul>
											<li><html:link action="jobLookup"><bean:message key="menu.label.lookup" /></html:link></li>
											<ansi:hasWrite>
											<li><html:link action="jobMaintenance"><bean:message key="menu.label.maintenance" /></html:link></li>
											<li><a href="#"><bean:message key="menu.label.new" /></a></li>
											</ansi:hasWrite>
										</ul>
									</li>
									</ansi:hasPermission>
									<ansi:hasPermission permissionRequired="TICKET">
									<li>
										<a href="#"><bean:message key="menu.label.tickets" /></a>
										<ul>
											<li><html:link action="ticketLookup"><bean:message key="menu.label.lookup" /></html:link></li>
											<ansi:hasWrite>
											<li><html:link action="ticketReturn"><bean:message key="menu.label.maintenance" /></html:link></li>
											<li><a href="#"><bean:message key="menu.label.new" /></a></li>
											<li><html:link action="ticketView"><bean:message key="menu.label.drv30" /></html:link></li>
											</ansi:hasWrite>											
										</ul>
									</li>
									</ansi:hasPermission>
									<li>
										<a href="#"><bean:message key="menu.label.taxrates" /></a>
										<ul>
											<!-- <li><a href="#">Lookup</a></li> -->
											<li><html:link action="taxRateMaintenance"><bean:message key="menu.label.maintenance" /></html:link></li>
											<!-- <li><a href="#">New</a></li> -->
										</ul>
									</li>
									<ansi:hasPermission permissionRequired="INVOICE">
									<li>
										<a href="#"><bean:message key="menu.label.invoices" /></a>
										<ul>
											<li><html:link action="invoiceLookup"><bean:message key="menu.label.lookup" /></html:link></li>
											<ansi:hasWrite>
											<li><html:link action="invoiceGeneration">Generate</html:link></li>
											<li><html:link action="invoicePrint">Print</html:link></li>
											<%-- <li><a href="#"><bean:message key="menu.label.templates" /></a></li> --%>
											</ansi:hasWrite>
										</ul>
									</li>
									</ansi:hasPermission>
									<ansi:hasPermission permissionRequired="PAYMENT">
									<li>
										<a href="#"><bean:message key="menu.label.payments" /></a>
										<ul>
											<li><html:link action="paymentLookup"><bean:message key="menu.label.lookup" /></html:link></li>
											<ansi:hasWrite>
											<li><a href="#"><bean:message key="menu.label.maintenance" /></a></li>
											<li><a href="#"><bean:message key="menu.label.new" /></a></li>
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
