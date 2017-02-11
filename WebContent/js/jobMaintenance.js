$( document ).ready(function() {
	;JOBUTILS = {
			
		getJob:function($jobId) {
			
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

		}
	}
	
	
	
	;JOBPANEL = {
		init: function($namespace, $divisionList, $defaultJobId) {
			JOBPANEL.setDivisionList($namespace, $divisionList);
			if ( $defaultJobId != null ) {
				JOBPANEL.setJobId($namespace, $defaultJobId)
			}
		},
		
		setDivisionList: function($namespace, $divisionList) {
			selectorName = "select[name='divisionId']";
			var $select = $(selectorName);
			$('option', $select).remove();

			$select.append(new Option("",""));
			$.each($divisionList, function(index, val) {
			    $select.append(new Option(val.divisionCode, val.divisionId));
			});
			
			$select.selectmenu();
		},
		
		setJobId: function($namespace, $jobId) {
			$selectorName = "#" + $namespace + "_jobId";
			$($selectorName).val($jobId);
			
		}
	}
	
	
	;JOBDESCRIPTION = {		
		init: function($namespace, $jobFrequencyList) {
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



