$( document ).ready(function() {

	
	
	;QUOTEUTILS = {
			
			getQuoteDetail:function($quoteId) {		
				var $returnValue = null;
				if ( $quoteId != null ) {
					var $url = "quote/" + $quoteId
					var jqxhr = $.ajax({
						type: 'GET',
						url: $url,
						data: {},
						statusCode: {
							200: function($data) {
								$returnValue = $data.data;
							},					
							403: function($data) {
								$("#useridMsg").html($data.responseJSON.responseHeader.responseMessage);
							},
							404: function($data) {
								$returnValue = {};
							},
							500: function($data) {
								
							}
						},
						dataType: 'json',
						async:false
					});
				}
				return $returnValue;

			}
		}
		
	
	;QUOTEAUDIT = {
		init: function($namespace, $quoteDetail) {
			if ( $quoteDetail != null ) {
//				var $createdBy = $jobDetail.addedFirstName + " " + $jobDetail.addedLastName + " (" + $jobDetail.addedEmail + ")";
//				var $updatedBy = $jobDetail.updatedFirstName + " " + $jobDetail.updatedLastName + " (" + $jobDetail.updatedEmail + ")";				
//				ANSI_UTILS.setTextValue($namespace, "createdBy", $createdBy);
//				ANSI_UTILS.setTextValue($namespace, "lastChangeBy", $updatedBy);
//				ANSI_UTILS.setTextValue($namespace, "lastChangeDate", $jobDetail.updatedDate);
			}
		}
	}
	
});



