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
        	#clockBox {
        		width:100%; 
        		text-align:center; 
        		padding-left:25px;
        	}
			#helloBox {
				width:100%; 
				clear:both; 
				text-align:center;
			}
			#motd {
				padding-top:25px;
				padding-bottom:25px;
			}
			
			
			#clock {
	        	position: relative;
	        	width: 300px;
	        	height: 300px;
	        	margin: 10px auto 0 auto;
	        	background: url(images/clock/clockface.jpg);
	        	background-repeat: no-repeat;
	        	list-style: none;
	        	}
	        
	        #sec, #min, #hour {
	        	position: absolute;
	        	width: 15px;
	        	height: 300px;
	        	top: 0px;
	        	left: 142px;
	        	}
	        
	        #sec {
	        	background: url(images/clock/sechand.png);
	        	z-index: 3;
	           	}
	           
	        #min {
	        	background: url(images/clock/minhand.png);
	        	z-index: 2;
	           	}
	           
	        #hour {
	        	background: url(images/clock/hourhand.png);
	        	z-index: 1;
	           	}
	           	
	        p {
	            text-align: center; 
	            padding: 10px 0 0 0;
	            }
        </style>
        <script type="text/javascript" src="js/dashboard.js"></script>
        <script type="text/javascript" src="js/clock.js"></script>   
    </tiles:put>
    
    
    <tiles:put name="content" type="string">
		<h1>ANSI Scheduling Dashboard</h1>
    	<div id="helloBox">
    		<h4><span id="helloText">Hello</span> <c:out value="${com_ansi_scilla_session_data.user.firstName}" /></h4>
    		<div id="clockBox">
	    		<ul id="clock">	
				   	<li id="sec"></li>
				   	<li id="hour"></li>
					<li id="min"></li>
				</ul>
			</div>
    		<div id="motd"></div>
    	</div>
	</tiles:put>

</tiles:insert>

