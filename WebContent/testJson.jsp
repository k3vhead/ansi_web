<html>
<head>
        <script type="text/javascript" src="jQuery/jquery-3.1.1.min.js"></script>        
        <script type="text/javascript" src="jQuery/jquery-ui.min.js"></script>
        <script type="text/javascript" src="jQuery/jcookie.js"></script>
        <script type="text/javascript" src="jQuery/jquery.bpopup.min.js"></script>
        <script type="text/javascript">        

        $( document ).ready(function() {
        	$outbound = {'intOne':123,'intTwo':'','intThree':null}
			var jqxhr = $.ajax({
   				type: 'POST',
   				url: "testJson",
   				data: JSON.stringify($outbound),
   				statusCode: {
	   				200: function($data) {
	   					console.debug("200");
	   				},
   					403: function($data) {
   						console.debug("403");
	   				},
	   				404: function($data) {
	   					console.debug("404");
	   				},
	   				500: function($data) {
	   					console.debug("500");
        	    	} 
	   			},
	   			dataType: 'json'
	   		});       
        	
		});
        </script>
</head>
<body>
</body>
</html>