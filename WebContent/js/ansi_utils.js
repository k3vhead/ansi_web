$( document ).ready(function() {
	;ansi_utils = {
		getOptions: function($optionList) {
			var $returnValue = null;
			var jqxhr1 = $.ajax({
				type: 'GET',
				url: 'options',
				data: $optionList,
				success: function($data) {
					$returnValue = $data.data;
				},
				statusCode: {
					403: function($data) {
						$("#useridMsg").html($data.responseJSON.responseHeader.responseMessage);
					} 
				},
				dataType: 'json',
		        async: false
			});
			return $returnValue;
		},
	
	
		// get a list of values from the codes table
		getCodes: function($tableName, $fieldName) {
			var $returnValue = null;
			var $url = "code/" + $tableName;
			if ( $fieldName != null ) {
				$url = $url + "/" + $fieldName;
			}
			var jqxhr2 = $.ajax({
				type: 'GET',
				url: $url,
				data: {},
				success: function($data) {
					$returnValue = $data.data;
				},
				statusCode: {
					403: function($data) {
						$("#useridMsg").html($data.responseJSON.responseHeader.responseMessage);
					} 
				},
				dataType: 'json',
				async:false
			});
			return $returnValue;
		},
		
		
		
		
		// get a list of divisions
		getDivisionList: function($tableName, $fieldName) {
			var $returnValue = null;
			var jqxhr3 = $.ajax({
				type: 'GET',
				url: 'division/list',
				data: {},
				success: function($data) {
					$returnValue = $data.data.divisionList;
				},
				statusCode: {
					403: function($data) {
						$("#useridMsg").html($data.responseJSON.responseHeader.responseMessage);
					} 
				},
				dataType: 'json',
				async:false
			});
			return $returnValue;
		},

		
	}	

});