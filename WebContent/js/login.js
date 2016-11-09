$(function() {
    $("#goButton").click(function($event) {
    	$.ajax({

    	     type: 'POST',
    	     url: 'component.html',
    	     data: $('#componentForm :input').serialize(),
    	     success: function($data) {
    	          $("#myProjectComponents").html($data);
    	     },
    	     dataType: 'json'
    	});
    });
});