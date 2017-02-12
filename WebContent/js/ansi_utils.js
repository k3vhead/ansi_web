$( document ).ready(function() {
	;ANSI_UTILS = {
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

		
		
		
		makeBuildingTypeList:function() {							
			var $returnValue = null;
			var jqxhr3 = $.ajax({
				type: 'GET',
				url: 'code/job/building_type',
				data: {},
				success: function($data) {
					$returnValue = $data.data.codeList;
				},
				statusCode: {
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
			return $returnValue;

		},

		
		// Set the values in an html select tag
		setOptionList: function($selectorName, $optionList, $selectedValue) {
			
			var $select = $($selectorName);
			$('option', $select).remove();

			$select.append(new Option("",""));
			$.each($optionList, function(index, val) {
			    $select.append(new Option(val.display, val.abbrev));
			});
			
			if ( $selectedValue != null ) {
				$select.val($selectedValue);
			}
			$select.selectmenu();
		},
		
		setCheckbox: function($namespace, $field, $value) {
			$selectorName = "#" + $namespace + "_" + $field;
			$($selectorName).prop('checked', $value);
		},
				
		setFieldValue: function($namespace, $field, $value) {
			$selectorName = "#" + $namespace + "_" + $field;
			$($selectorName).val($value);
			
		},
		
		setTextValue: function($namespace, $field, $value) {
			$selectorName = "#" + $namespace + "_" + $field;
			$($selectorName).html($value);
		},
		
		setSelectValue: function($namespace, $field, $value) {
			$selectorName = "#" + $namespace + "_" + $field;
			$($selectorName).val($value);
//			$($selectorName).selectmenu('refresh');
		}
		
		
	}	

});