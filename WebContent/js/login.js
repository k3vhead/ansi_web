$(function() {
    $("#goButton").click(function($event) {
    	$event.preventDefault();
    	$userid = $("input[name='userid']").val();
    	$password = $("input[name='password']").val();
    	$outbound = JSON.stringify({'userid':$userid, 'password':$password});
    	var jqxhr = $.ajax({
    	     type: 'POST',
    	     url: 'login',
    	     data: $outbound,
    	     success: function($data) {
    	    	 location.href="dashboard.html";
    	     },
    	     statusCode: {
    	    	403: function($data) {
    	    		$("#loginMsg").html($data.responseJSON.responseHeader.responseMessage);
    	    		$("#loginMsg").fadeIn('fast');
    	    		$("#loginMsg").fadeOut(6000);
    	    	} 
    	     },
    	     dataType: 'json'
    	});
    });

    $("input[name='userid']").on('focus',function(e) {
    	$("#validUser").addClass("fa");
    	$("#validUser").addClass("fa-ban");
		$("#validUser").addClass("inputIsInvalid");
    });
    
    $("input[name='password']").on('focus',function(e) {
    	$("#validPass").addClass("fa");
    	$("#validPass").addClass("fa-ban");
		$("#validPass").addClass("inputIsInvalid");
    });
    
    $("input[name='userid']").on('input',function(e) {
    	userid = $("input[name='userid']").val();
    	var re = /.+\@.+\..+/;
    	if ( re.test(userid) ) {
    		$("#validUser").removeClass("fa-ban");
    		$("#validUser").removeClass("inputIsInvalid");
    		$("#validUser").addClass("fa-check-square-o");
    		$("#validUser").addClass("inputIsValid");
    	} else {
    		$("#validUser").removeClass("fa-check-square-o");
    		$("#validUser").removeClass("inputIsValid");
    		$("#validUser").addClass("fa-ban");
    		$("#validUser").addClass("inputIsInvalid");
    	}
    	
    });
    $("input[name='password']").on('input',function(e) {
    	password = $("input[name='password']").val();
    	var re = /.+/;
    	if ( re.test(password) ) {
    		$("#validPass").removeClass("fa-ban");
    		$("#validPass").removeClass("inputIsInvalid");
    		$("#validPass").addClass("fa-check-square-o");
    		$("#validPass").addClass("inputIsValid");
    	} else {
    		$("#validPass").removeClass("fa-check-square-o");
    		$("#validPass").removeClass("inputIsValid");
    		$("#validPass").addClass("fa-ban");
    		$("#validPass").addClass("inputIsInvalid");
    	}
    	
    });
});