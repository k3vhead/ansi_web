$( document ).ready(function() {
	;JOBUTILS = {
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

		}
	}
	
	
	
	;JOBPANEL = {
		init: function($namespace, $divisionList) {
			console.debug("Doing the jobpanel init:" + $namespace);
			JOBPANEL.setDivisionList($namespace, $divisionList);
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
			init: function($namespace, $buildingTypeList) {
				console.debug("Doing the jobactivation init:" + $namespace);
				
				
		        //var $buildingTypeList = [];
				//$.each($data.data.codeList, function(index, value) {
				//	if ( value.fieldName == 'building_type') {
				//		$buildingTypeList.push(value);
				//	}
				//});
				JOBACTIVATION.setBuildingType($namespace, $buildingTypeList, null);

				
		
				$("#" + $namespace + "_jobActivationForm select[name='" + $namespace + "_automanual']").selectmenu();
				$("#" + $namespace + "_jobActivationForm select[name='" + $namespace + "_buildingType']").selectmenu();
				$("#" + $namespace + "_jobActivationForm input[name='" + $namespace + "_nbrFloors']").spinner({
							spin: function( event, ui ) {
								if ( ui.value < 1 ) {
									$( this ).spinner( "value", 1 );
									return false;
								}
							}
						});
				},
				
				
				setBuildingType: function ($namespace, $optionList, $selectedValue) {
					var selectorName = "#" + $namespace + "_jobActivationForm select[name='" + $namespace + "_buildingType']";
					var $select = $(selectorName);
					if($select.prop) {
						var options = $select.prop('options');
					} else {
						var options = $select.attr('options');
					}
					$('option', $select).remove();

					options[options.length] = new Option("","");
					$.each($optionList, function(index, type) {
						options[options.length] = new Option(type.displayValue, type.value);
					});
					if ( $selectedValue != null ) {
						$select.val($selectedValue);
					}
					$select.selectmenu();
				}
				
				
			}
		
	

	;JOBINVOICE = {
			init: function($namespace, $invoiceStyleList, $invoiceGroupList, $invoiceTermList) {
				console.debug("Doing the jobinvoice init:" + $namespace);
				
				JOBINVOICE.setInvoiceStyle($namespace, $invoiceStyleList);
				JOBINVOICE.setInvoiceGrouping($namespace, $invoiceGroupList);
				JOBINVOICE.setInvoiceTerms($namespace, $invoiceTermList);
				
				
				var $selectorName = "input[name='" + $namespace + "_invoiceExpire']";
				$($selectorName).datepicker({
	                prevText:'&lt;&lt;',
	                nextText: '&gt;&gt;',
	                showButtonPanel:true
	            });

				
			},
			setInvoiceStyle: function($namespace, $optionList, $selectedValue) {
				var selectorName = "#" + $namespace + "_jobInvoiceForm select[name='" + $namespace + "_invoiceStyle']";
				$selectorName = "select[name='" + $namespace + "_invoiceStyle']";
				console.debug($selectorName);
				console.debug($optionList);
				ansi_utils.setOptionList($selectorName, $optionList, $selectedValue)
			},
			setInvoiceGrouping: function($namespace, $optionList, $selectedValue) {
				var selectorName = "#" + $namespace + "_jobInvoiceForm select[name='" + $namespace + "_invoiceGrouping']";
				var $selectorName = "select[name='" + $namespace + "_invoiceGrouping']";
				ansi_utils.setOptionList($selectorName, $optionList, $selectedValue)
			},
			setInvoiceTerms: function($namespace, $optionList, $selectedValue) {
				var selectorName = "#" + $namespace + "_jobInvoiceForm select[name='" + $namespace + "_invoiceTerms']";
				var $selectorName = "select[name='" + $namespace + "_invoiceTerms']";
				ansi_utils.setOptionList($selectorName, $optionList, $selectedValue)
			}

	}
});



