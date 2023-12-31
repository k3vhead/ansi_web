$( document ).ready(function() {
	// this is the new & improved address utilities. Methods are set up to be called
	// with a callback function parameter to make asynchronous call work correctly.
	// The old version of thing ("ADDRESS_UTILS") is here for backwards compatability
	// and will go away at some point.
	;ADDRESSUTILS = {
		getAddress : function($addressId, $callback) {
			var $url = 'address/' + $addressId;

			var jqxhr = $.ajax({
				type: 'GET',
				url: $url,
				statusCode: {
					200: function($data) {
						$callback(200, $data);
					},
					403: function($data) {
						$callback(403, $data);
					},
					404: function($data) {
						$callback(404, $data);
					},
					405: function($data) {
						$callback(405, $data);
					},
					500: function($data) {
						$callback(500, $data);
					} 
				},
				dataType: 'json'
			});			
		},
			
	}
	
	
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
						ADDRESS_UTILS.populateDefaultJobsite($tagName, $address);
						ADDRESS_UTILS.populateDefaultInvoice($tagName, $address);
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
		
		
		clearDefaultInvoice : function($tagName) {
			$selector = $tagName + ". ansi-default-invoice-value";
			$($selector).html("");
		},
		
		
		populateAddress : function($tagName, $address) {
			$.each($address, function($fieldName, $value) {									
				$selector = $tagName + " .ansi-address-" + $fieldName;
				if ( $($selector).length > 0 ) {
					$($selector).html($value);
				}
			});
		},
		
		
		populateDefaultInvoice : function($tagName, $address) {
			$.each($address, function($fieldName, $value) {									
				$selector = $tagName + " .ansi-invoice-" + $fieldName;
				if ( $($selector).length > 0 ) {
					$($selector).html($value);
				}
			});
			// there's always one that doesn't fit:
			$selector = $tagName + " .ansi-invoice-invoiceBatchDefault";
			if ( $address.invoiceBatchDefault == 1 ) {
				$($selector).html("<webthing:checkmark>Batch Invoice</webthing:checkmark>");
			} else {
				$($selector).html("<webthing:ban>Not Batch</webthing:ban>");
			}
		},
			
		
		populateDefaultJobsite : function($tagName, $address) {
			$.each($address, function($fieldName, $value) {									
				$selector = $tagName + " .ansi-jobsite-" + $fieldName;
				if ( $($selector).length > 0 ) {
					$($selector).html($value);
				}
			});
		},
	}
});