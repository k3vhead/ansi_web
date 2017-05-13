$( document ).ready(function() {
	;ADDRESS_UTILS = {

		getAddress : function($addressId, $tagName) {
			var $url = 'address/' + $addressId;

			var jqxhr = $.ajax({
				type: 'GET',
				url: $url,
				statusCode: {
					200: function($data) {
						ADDRESS_UTILS.clearAddress($tagName);
						var $address = $data.data.addressList[0];  // we're only getting one address
						ADDRESS_UTILS.populateAddress($tagName, $address);
					},
					403: function($data) {
						$("#globalMsg").html("Session Timeout. Log in and try again");
					},
					404: function($data) {
						$("#globalMsg").html("System Error 404: Contact Support");
					},
					405: function($data) {
						$("#globalMsg").html("System Error 405: Contact Support");
					},
					500: function($data) {
						$("#globalMsg").html("System Error 500: Contact Support");
					} 
				},
				dataType: 'json'
			});
		},
		
		
		clearAddress : function($tagName) {
			$selector = $tagName + " .ansi-address-value";
			$($selector).html("");
		},
		
		
		populateAddress : function($tagName, $address) {
			$.each($address, function($fieldName, $value) {									
				$selector = $tagName + " .ansi-address-" + $fieldName;
				if ( $($selector).length > 0 ) {
					$($selector).html($value);
				}
			});
		}
			
	}
});