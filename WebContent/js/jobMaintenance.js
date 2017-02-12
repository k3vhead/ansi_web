$( document ).ready(function() {
	;JOBUTILS = {
			
		getJobDetail:function($jobId) {			
			var $returnValue = null;
			if ( $jobId != null ) {
				var $url = "job/" + $jobId
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
	
	
	
	;JOBACTIVATION = {
			init: function($namespace, $buildingTypeList, $jobDetail) {
				
				if ( $jobDetail == null ) {
					JOBACTIVATION.setBuildingType($namespace, $buildingTypeList, null);
				} else {
					JOBACTIVATION.setBuildingType($namespace, $buildingTypeList, $jobDetail.buildingType);
					ANSI_UTILS.setFieldValue($namespace, "directLaborPct", $jobDetail.directLaborPct);
					ANSI_UTILS.setFieldValue($namespace, "directLaborBudget", $jobDetail.budget);
					ANSI_UTILS.setFieldValue($namespace, "nbrFloors", $jobDetail.floors);
					ANSI_UTILS.setFieldValue($namespace, "equipment", $jobDetail.equipment);
					ANSI_UTILS.setFieldValue($namespace, "washerNotes", $jobDetail.washerNOtes);
					ANSI_UTILS.setFieldValue($namespace, "omNotes", $jobDetail.omNotes);
					ANSI_UTILS.setFieldValue($namespace, "billingNotes", $jobDetail.billingNotes);
				}
				

				$("#" + $namespace + "_jobActivationForm select[name='" + $namespace + "_automanual']").selectmenu();
				$("#" + $namespace + "_jobActivationForm select[name='" + $namespace + "_buildingType']").selectmenu();
				$("#" + $namespace + "_jobActivationForm input[name='" + $namespace + "_nbrFloors']").spinner({
							spin: function( event, ui ) {
								if ( ui.value < 0 ) {
									$( this ).spinner( "value", 0 );
									return false;
								}
							}
						});
				},
				
				
				setBuildingType: function ($namespace, $optionList, $selectedValue) {
					var selectorName = "#" + $namespace + "_jobActivationForm select[name='" + $namespace + "_buildingType']";
					var $select = $(selectorName);
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
		
	
	;JOBAUDIT = {
		init: function($namespace, $jobDetail) {
			if ( $jobDetail != null ) {
				var $createdBy = $jobDetail.addedFirstName + " " + $jobDetail.addedLastName + " (" + $jobDetail.addedEmail + ")";
				var $updatedBy = $jobDetail.updatedFirstName + " " + $jobDetail.updatedLastName + " (" + $jobDetail.updatedEmail + ")";				
				ANSI_UTILS.setTextValue($namespace, "createdBy", $createdBy);
				ANSI_UTILS.setTextValue($namespace, "lastChangeBy", $updatedBy);
				ANSI_UTILS.setTextValue($namespace, "lastChangeDate", $jobDetail.updatedDate);
			}
		}
	}
	
	
	
	;JOBDATES = {
		init: function($namespace, $quoteDetail, $jobDetail) {
			if ( $jobDetail != null ) {
				ANSI_UTILS.setTextValue($namespace, "proposalDate", $quoteDetail.proposalDate);
				ANSI_UTILS.setTextValue($namespace, "activationDate", $jobDetail.activationDate);
				ANSI_UTILS.setTextValue($namespace, "startDate", $jobDetail.startDate);
				ANSI_UTILS.setTextValue($namespace, "cancelDate", $jobDetail.cancelDate);
				ANSI_UTILS.setTextValue($namespace, "cancelReason", $jobDetail.cancelReason);
			}
		}
	}

	
	
	;JOBINVOICE = {
			init: function($namespace, $invoiceStyleList, $invoiceGroupList, $invoiceTermList, $jobDetail) {
				if ( $jobDetail ) {
					if ( $jobDetail.invoiceBatch == 1) {
						ANSI_UTILS.setCheckbox($namespace, "invoiceBatch", true);
					} else {
						ANSI_UTILS.setCheckbox($namespace, "invoiceBatch", false);
					}
					if ( $jobDetail.taxExempt == 1) {
						ANSI_UTILS.setCheckbox($namespace, "invoiceTaxExempt", true);
					} else {
						ANSI_UTILS.setCheckbox($namespace, "invoiceTaxExempt", false);
					}
					ANSI_UTILS.setFieldValue($namespace, "invoicePO", $jobDetail.poNumber);
					ANSI_UTILS.setFieldValue($namespace, "invoiceOurVendorNbr", $jobDetail.ourVendorNbr);
					ANSI_UTILS.setFieldValue($namespace, "invoiceExpire", $jobDetail.expirationDate);
					ANSI_UTILS.setFieldValue($namespace, "invoiceExpireReason", $jobDetail.expirationReason);
				}
				JOBINVOICE.setInvoiceStyle($namespace, $invoiceStyleList, $jobDetail.invoiceStyle);
				JOBINVOICE.setInvoiceGrouping($namespace, $invoiceGroupList, $jobDetail.invoiceGrouping);
				JOBINVOICE.setInvoiceTerms($namespace, $invoiceTermList, $jobDetail.invoiceTerms);
				
				
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
				ANSI_UTILS.setOptionList($selectorName, $optionList, $selectedValue)
			},
			setInvoiceGrouping: function($namespace, $optionList, $selectedValue) {
				var selectorName = "#" + $namespace + "_jobInvoiceForm select[name='" + $namespace + "_invoiceGrouping']";
				var $selectorName = "select[name='" + $namespace + "_invoiceGrouping']";
				ANSI_UTILS.setOptionList($selectorName, $optionList, $selectedValue)
			},
			setInvoiceTerms: function($namespace, $optionList, $selectedValue) {
				var selectorName = "#" + $namespace + "_jobInvoiceForm select[name='" + $namespace + "_invoiceTerms']";
				var $selectorName = "select[name='" + $namespace + "_invoiceTerms']";
				ANSI_UTILS.setOptionList($selectorName, $optionList, $selectedValue)
			}

	}

	
	
	;JOBPANEL = {
		init: function($namespace, $divisionList, $jobDetail) {
			var $divisionLookup = {}
			$.each($divisionList, function($index, $division) {
				$divisionLookup[$division.divisionId]=$division.divisionCode;
			});
			JOBPANEL.setDivisionList($namespace, $divisionList);
			if ( $jobDetail != null ) {
				ANSI_UTILS.setFieldValue($namespace, "jobId", $jobDetail.jobId);
				ANSI_UTILS.setTextValue($namespace, "jobStatus", $jobDetail.status);
				ANSI_UTILS.setTextValue($namespace, "divisionId", $divisionLookup[$jobDetail.divisionId]);
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
		}
	}
	
	
	;JOBPROPOSAL = {		
		init: function($namespace, $jobFrequencyList, $jobDetail) {
			if ( $jobDetail != null ) {
				ANSI_UTILS.setFieldValue($namespace, "jobNbr", $jobDetail.jobNbr);
				ANSI_UTILS.setFieldValue($namespace, "ppc", $jobDetail.pricePerCleaning);
				ANSI_UTILS.setFieldValue($namespace, "serviceDescription", $jobDetail.serviceDescription);
				JOBPROPOSAL.setJobFrequency($namespace, $jobFrequencyList, $jobDetail.jobFrequency);
			} else {
				JOBPROPOSAL.setJobFrequency($namespace, $jobFrequencyList);
			}
			
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



	;JOBSCHEDULE = {
		init: function($namespace, $lastRun, $nextDue, $lastCreated) {
			if ( $lastRun != null ) {
				ANSI_UTILS.setTextValue($namespace, "lastRun", $lastRun.startDate);
				ANSI_UTILS.setTextValue($namespace, "lastTicket", $lastRun.ticketId);
			}
			if ( $nextDue != null ) {
				ANSI_UTILS.setTextValue($namespace, "nextDue", $nextDue.processDate + " (" + $nextDue.ticketId + ")");
			}
			if ( $lastCreated != null) {
				ANSI_UTILS.setTextValue($namespace, "createdThru", $lastCreated.processDate);
			}
		}
	}
});



