<%@ page contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@ taglib tagdir="/WEB-INF/tags/webthing" prefix="webthing" %>

<html>
<head>
	<title>Calendar Demo</title>
	<link rel="stylesheet" href="http://code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css" />
	<link rel="stylesheet" href="css/ansiCalendar.css" />
		<%-- <script type="text/javascript" src="jQuery/jquery.js"></script> --%>
		
		<script type="text/javascript" src="jQuery/jquery-3.1.1.min.js"></script>        
        <script type="text/javascript" src="jQuery/jquery-ui.min.js"></script>
        <script type="text/javascript" src="jQuery/jcookie.js"></script>
        <script type="text/javascript" src="js/ansiCalendar.js"></script>
        
</head>
<body>
    	<h1>Calendar Demo</h1>
    	begin
		<webthing:ansiCalendar id="ansiDate" monthsToDisplay="12" /> 
		end
		<webthing:ansiCalendar id="myDate" monthsToDisplay="6" /> 
		
</body>
</html>
		