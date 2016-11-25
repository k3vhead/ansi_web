$(function() {
    $("#goButton").click(function($event) {
    	$event.preventDefault();
    	$userid = $("input[name='userid']").val();
    	$password = $("input[name='password']").val();
    	$outbound = JSON.stringify({'userid':$userid, 'password':$password});
    	console.debug($outbound);
    	var jqxhr = $.ajax({
    	     type: 'POST',
    	     url: 'login',
    	     data: $outbound,
    	     success: function($data) {
    	    	 location.href="dashboard.html";
    	     },
    	     statusCode: {
    	    	403: function($data) {
    	    		$("#useridMsg").html($data.responseJSON.responseHeader.responseMessage);
    	    	} 
    	     },
    	     dataType: 'json'
    	});
    });
});