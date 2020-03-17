$(function() {  
	;DASHBOARD_UTILS = {
		motd : function() {
			var jqxhr = $.ajax({
				type: 'GET',
				url: 'motd',
				data: {},				
				statusCode: {
					200: function($data) {
						if ( $data.responseHeader.responseCode == 'SUCCESS') {
							$("#motd").html($data.data.motd);
						}
					},
					403: function($data) {
						$("#globalMsg").html($data.responseJSON.responseHeader.responseMessage);
					} 
				},
				dataType: 'json'
			});
			
			var now = new Date();
			if ( now.getHours() < 13 ) {
				$("#helloText").html("Good Morning");
			}
		},	
	}
	
	

});