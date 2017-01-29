$( document ).ready(function() {
	;JOBDESCRIPTION = {		
		init: function($namespace, $jobFrequencyList) {
			console.debug("Doing the jobdesc init:" + $namespace);
			JOBDESCRIPTION.setJobFrequency($namespace, $jobFrequencyList);
		},
		setJobFrequency: function($namespace, $optionList, $selectedValue) {
			var selectorName = "#" + $namespace + "_jobDescriptionForm select[name='" + $namespace + "_jobFrequency']";
			selectorName = "select[name='" + $namespace + "_jobFrequency']";
			
			var $select = $(selectorName);
			$('option', $select).remove();

			$select.append(new Option("",""));
			$.each($optionList, function(index, val) {
			    $select.append(new Option(val.display, val.abbrev));
			});
			
			if ( $selectedValue != null ) {
				$select.val($selectedValue);
			}
			$select.selectmenu();
		}

	}

	
	
	;JOBACTIVATION = {
		init: function($namespace) {
			console.debug("Doing the jobactivation init:" + $namespace);
			
			
	        //var $buildingTypeList = [];
			//$.each($data.data.codeList, function(index, value) {
			//	if ( value.fieldName == 'building_type') {
			//		$buildingTypeList.push(value);
			//	}
			//});
			//JOBACTIVATION.setBuildingType($buildingTypeList);

			
			
			
			
		}
	}
	

	
	
	;JOBPANEL = {
			init: function($namespace, $divisionList) {
				console.debug("Doing the jobactivation init:" + $namespace);
				JOBPANEL.setDivisionList($namespace, $divisionList);

				
		        //var $buildingTypeList = [];
				//$.each($data.data.codeList, function(index, value) {
				//	if ( value.fieldName == 'building_type') {
				//		$buildingTypeList.push(value);
				//	}
				//});
				//JOBACTIVATION.setBuildingType($buildingTypeList);

				
				
				
				
			},
			setDivisionList: function($namespace, $divisionList) {
				selectorName = "select[name='division']";
				var $select = $(selectorName);
				$('option', $select).remove();
	
				$select.append(new Option("",""));
				$.each($divisionList, function(index, val) {
				    $select.append(new Option(val.divisionCode, val.divisionId));
				});
				
				$select.selectmenu();
			}

		}

	

	
	
	
});