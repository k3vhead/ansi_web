<%@ page contentType="text/html"%>

<%@ taglib uri="WEB-INF/c.tld" prefix="c"%>
<%@ taglib uri="WEB-INF/sql.tld" prefix="sql"%>
<%@ taglib uri="WEB-INF/fmt.tld" prefix="fmt" %>
<%@ taglib uri="WEB-INF/struts-html.tld"  prefix="html"  %>
<%@ taglib uri="WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="WEB-INF/struts-bean.tld"  prefix="bean"  %>
<%@ taglib uri="WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib tagdir="/WEB-INF/tags/webthing" prefix="webthing"%>

<%@ page errorPage="errorHandler.jsp" %>

<!DOCTYPE html>
<html>
    <head>
	    <meta charset="utf-8">
    	<meta http-equiv="X-UA-Compatible" content="IE=edge">
    	<meta name="viewport" content="width=device-width, initial-scale=1">
    	<link rel="stylesheet" type="text/css" href="js/bootstrap-3.3.7-dist/css/bootstrap.min.css">
    	<!-- Optional Bootstrap theme -->
    	<link rel="stylesheet" type="text/css" href="js/bootstrap-3.3.7-dist/css/bootstrap-theme.min.css" />
    	
        <title>ANSI Scheduling - <tiles:insert attribute="title" /></title>
        <link rel="SHORTCUT ICON" href="images/favicon.ico" />
        <script type="text/javascript" src="jQuery/jquery-3.1.1.min.js"></script>        
        <script type="text/javascript" src="jQuery/jquery-ui-1.8.7.custom.js"></script>
        <script type="text/javascript" src="jQuery/jcookie.js"></script>
        <script type="text/javascript" src="jQuery/jquery.bpopup-0.7.0.min.js"></script>
        <!-- <script type="text/javascript" src="jQuery/jquery.popmenu.js"></script> -->
    	
    	<script src="js/bootstrap-3.3.7-dist/js/bootstrap.min.js"></script>
        
        <link rel="stylesheet" href="css/layout.css" type="text/css" />
        <link rel="stylesheet" href="css/style.css" type="text/css" />
    	
        <script src="js/ansi.js"></script>
       
        <tiles:insert attribute="headextra" />
    </head>
    <body>
    	<div id="pageContainer">
    		
	    	<img src="images/ansi_logo-symbol.png"  />
	    	<img src="images/ansi_logo-words.png" /><br />
	    	<div class="navbar-header">
	    		<button type="button" data-target="#navbarCollapse" data-toggle="collapse" class="navbar-toggle">
	    			<span class="sr-only">Toggle Navigation</span>
	    			<span class="icon-bar"></span>
	    			<span class="icon-bar"></span>
	    			<span class="icon-bar"></span>	    			
	    		</button>
	    		<a href="#" class="navbar-brand">Brand</a>
	    	</div>
	    	<div id="navbarCollapse" class="collapse navbar-collapse menuBar">
	    		<ul class="nav navbar-nav">
	    			<li class="active"><a href="#">Home</a></li>
	    			<li><a href="#">Profile</a>
	    			<li class="dropdown">
	    				<a data-toggle="dropdown" class="dropdown-toggle" href="#">Messages <b class="caret"></b></a>
	    				<ul role="menu" class="dropdown-menu">
	    					<li><a href="#">Inbox</a></li>
	    					<li><a href="#">Drafts</a></li>
	    					<li><a href="#">Sent Messages</a></li>
	    					<li class="divider"></li>
	    					<li><a href="#">Inbox</a></li>
	    				</ul>
	    			</li>
	    		</ul>
	    		<ul class="nav navbar-nav navbar-right">
	    			<li><a href="logoff.html">Logoff</a></li>
	    		</ul>
	    	</div>
	    	
	    	
	    	

	    	<tiles:insert attribute="content" />
	    	
	    	
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