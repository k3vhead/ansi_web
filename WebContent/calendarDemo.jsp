<%@ page contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@ taglib tagdir="/WEB-INF/tags/webthing" prefix="webthing" %>

<html>
<head>
	<title>Calendar Demo</title>
	<%--
	<link rel="stylesheet" href="font-awesome/css/font-awesome.min.css" />   
	<link href="https://fonts.googleapis.com/css?family=Roboto+Condensed" rel="stylesheet" />
	<link rel="stylesheet" href="http://code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css" />
		
	<script type="text/javascript" src="jQuery/jquery-3.1.1.min.js"></script>        
	<script type="text/javascript" src="jQuery/jquery-ui.min.js"></script>
	<script type="text/javascript" src="jQuery/jcookie.js"></script>
	 --%>
	
	<script type="text/javascript" src="jQuery/jquery-3.1.1.min.js"></script>        
       <script type="text/javascript" src="jQuery/jquery-ui.min.js"></script>
       <script type="text/javascript" src="jQuery/jcookie.js"></script>
       <script type="text/javascript" src="jQuery/jquery.bpopup.min.js"></script>
       <!-- <script type="text/javascript" src="jQuery/jquery.popmenu.js"></script> -->
   	<link rel="stylesheet" href="http://code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css" />
   	
   	
        
       <link rel="stylesheet" href="font-awesome/css/font-awesome.min.css" />   
       <link href="https://fonts.googleapis.com/css?family=Roboto+Condensed" rel="stylesheet" />
       
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


	<link rel="stylesheet" href="css/ansiCalendar.css" />
	
	<script type="text/javascript" src="js/ansiCalendar.js"></script>
        
    <script type="text/javascript">
    $(function() {       
		$(".ansi-date-show-calendar").click(function($event) {
			var $jobId = $("input[name='jobId']").val();
			ANSI_CALENDAR.go($jobId);			
		});

    });
    
    </script>
</head>
<body>
    	<h1>Calendar Demo</h1>
    	<div id="globalMsg"></div>
    	Job: <input type="text" name="jobId" />
    	<i class="fa fa-calendar-check-o fa-2x ansi-date-show-calendar" aria-hidden="true"></i>
		<webthing:ansiCalendar id="ansiDate" monthsToDisplay="12" triggerSize="2" label="Manual Job Schedule" />
		<a href="dashboard.html">Dashboard</a> 
</body>
</html>
		