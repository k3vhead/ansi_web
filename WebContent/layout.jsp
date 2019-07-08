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
<%@ page import="java.util.Calendar,
				java.util.TimeZone" %>

<!DOCTYPE html>
<html>
    <head>
	    <meta charset="utf-8">
    	<meta http-equiv="X-UA-Compatible" content="IE=edge">
    	<meta http-equiv="Cache-Control" content="no-cache, no-store, must-revalidate" />
		<meta http-equiv="Pragma" content="no-cache" />
		<meta http-equiv="Expires" content="0" />
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
    	<link rel="stylesheet" href="http://code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css" />
    	
    	<%-- 
    	<script src="js/bootstrap-3.3.7-dist/js/bootstrap.min.js"></script>
        --%>
         
		<%-- <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.4.2/css/all.css" integrity="sha384-/rXc/GQVaYpyDdyxK+ecHPVYJSN9bmVFBvjA/9eOB+pb3F2w2N6fc5qB9Ew5yIns" crossorigin="anonymous">  --%>
		<link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.8.1/css/all.css" integrity="sha384-50oBUHEmvpQ+1lW4y57PTFmhCaXp0ML5d60M1M7uH2+nqUivzIebhndOJK28anvf" crossorigin="anonymous">        
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
			    			<webthing:menu menuName="SETTINGS" />
			    			<webthing:menu menuName="MY_ANSI" />
		    			</ul>
						<div style="padding-top:4px; padding-left:4px;"><c:out value="${com_ansi_scilla_session_data.user.firstName}" /> <c:out value="${com_ansi_scilla_session_data.user.lastName}" /></div>		    		</div>
						<div style="float:left; width:65%">	    		    		
							<ul class="dropdown"> 
								<webthing:menu menuName="DASHBOARD" />
								<webthing:menu menuName="LOOKUPS" />
			        			<webthing:menu menuName="REPORTS"><webthing:reportMenu /></webthing:menu>
			        			<webthing:menu menuName="QUICK_LINKS" />
							</ul>
						</div>  <!--  menus left --> 
				</div> <!-- id=headerNav -->
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
		    			(<webthing:buildDate />| <span id="taskList" class="trailerLink">Deployed </span>)
		    		</div>
	    		</div>
	    		<div style="float:left; width:45%;">
		    		<div class="trailerText">
		    			&copy; 2016-<%= Calendar.getInstance(TimeZone.getTimeZone("America/Chicago")).get(Calendar.YEAR) %> American National Skyline, All Rights Reserved   
		    		</div>
	    		</div>	  
	    		<div class="spacer">&nbsp;</div>  		
	    	</div>
	    	
    	</div>
    	<div id="taskListModal">
    		... loading ...
    	</div>
    </body>
</html>
