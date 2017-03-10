$( document ).ready(function() {
	;JOB_DATA = {}
	
	;JOB_UTILS = {
		pageInit:function($jobId) {
			
			$optionData = ANSI_UTILS.getOptions('JOB_FREQUENCY,JOB_STATUS,INVOICE_TERM,INVOICE_GROUPING,INVOICE_STYLE');
			JOB_DATA.jobFrequencyList = $optionData.jobFrequency;
			JOB_DATA.jobStatusList = $optionData.jobStatus;
			JOB_DATA.invoiceTermList = $optionData.invoiceTerm;
			JOB_DATA.invoiceGroupingList = $optionData.invoiceGrouping;
			JOB_DATA.invoiceStyleList = $optionData.invoiceStyle;

			JOB_DATA.divisionList = ANSI_UTILS.getDivisionList();
			JOB_DATA.buildingTypeList = ANSI_UTILS.makeBuildingTypeList();
						
			JOB_DATA.jobId = $jobId;
			JOB_UTILS.panelLoad($jobId);
			
			
		},
			
		panelLoad:function($jobId) {			
			var $jobDetail = null;			
			var $quoteDetail = null;
			var $lastRun = null;
			var $nextDue = null;
			var $lastCreated = null;
			
			if ( $jobId != '' ) {
				$jobData = JOB_UTILS.getJobDetail($jobId);
				$jobDetail = $jobData.job;
				$quoteDetail = $jobData.quote;
				$lastRun = $jobData.lastRun;
				$nextDue = $jobData.nextDue;
				$lastCreated = $jobData.lastCreated;
			}
			
			var jqxhr1 = $.ajax({
				type: 'GET',
				url: 'quotePanel.html',
				data: {"namespace":'row0',"page":'JOB'},
				success: function($data) {
					$('#jobPanelHolder > tbody:last-child').replaceWith($data);
					JOBPANEL.init("row0_jobPanel", JOB_DATA.divisionList, "activateModal", $jobDetail);
					JOBPROPOSAL.init("row0_jobProposal", JOB_DATA.jobFrequencyList, $jobDetail);
					JOBACTIVATION.init("row0_jobActivation", JOB_DATA.buildingTypeList, $jobDetail);
					JOBDATES.init("row0_jobDates", $quoteDetail, $jobDetail);
					JOBSCHEDULE.init("row0_jobSchedule", $jobDetail, $lastRun, $nextDue, $lastCreated)
					JOBINVOICE.init("row0_jobInvoice", JOB_DATA.invoiceStyleList, JOB_DATA.invoiceGroupingList, JOB_DATA.invoiceTermList, $jobDetail);
					JOBAUDIT.init("row0_jobAudit", $jobDetail);
				},
				statusCode: {
					403: function($data) {
						$("#useridMsg").html($data.responseJSON.responseHeader.responseMessage);
					} 
				},
				dataType: 'html'
			});
		},
		
		panelLoadQuote:function($namespace, $jobId, $index) {
			if($index == 0){
				$optionData = ANSI_UTILS.getOptions('JOB_FREQUENCY,JOB_STATUS,INVOICE_TERM,INVOICE_GROUPING,INVOICE_STYLE');
				
				JOB_DATA.jobFrequencyList = $optionData.jobFrequency;
				JOB_DATA.jobStatusList = $optionData.jobStatus;
				JOB_DATA.invoiceTermList = $optionData.invoiceTerm;
				JOB_DATA.invoiceGroupingList = $optionData.invoiceGrouping;
				JOB_DATA.invoiceStyleList = $optionData.invoiceStyle;

				JOB_DATA.divisionList = ANSI_UTILS.getDivisionList();
				JOB_DATA.buildingTypeList = ANSI_UTILS.makeBuildingTypeList();
			}
			
			
			var $jobDetail = null;			
			var $quoteDetail = null;
			var $lastRun = null;
			var $nextDue = null;
			var $lastCreated = null;
			
			if ( $jobId != '' ) {
				$jobData = JOB_UTILS.getJobDetail($jobId);				
				$jobDetail = $jobData.job;
				$quoteDetail = $jobData.quote;
				$lastRun = $jobData.lastRun;
				$nextDue = $jobData.nextDue;
				$lastCreated = $jobData.lastCreated;
			}
			
			var jqxhr1 = $.ajax({
				type: 'GET',
				url: 'quotePanel.html',
				data: {"namespace":$namespace,"page":'QUOTE'},
				success: function($data) {
					$("#loadingJobsDiv").hide();
					$('#jobPanelHolder > tbody:last-child').append($data);
					JOBPANEL.init($namespace+"_jobPanel", JOB_DATA.divisionList, "activateModal", $jobDetail);
					JOBPROPOSAL.init($namespace+"_jobProposal", JOB_DATA.jobFrequencyList, $jobDetail);
					JOBACTIVATION.init($namespace+"_jobActivation", JOB_DATA.buildingTypeList, $jobDetail);
					JOBDATES.init($namespace+"_jobDates", $quoteDetail, $jobDetail);
					JOBSCHEDULE.init($namespace+"_jobSchedule", $jobDetail, $lastRun, $nextDue, $lastCreated)
					JOBINVOICE.init($namespace+"_jobInvoice", JOB_DATA.invoiceStyleList, JOB_DATA.invoiceGroupingList, JOB_DATA.invoiceTermList, $jobDetail);
					JOBAUDIT.init($namespace+"_jobAudit", $jobDetail);
					//$(".addressTable").remove();
				},
				statusCode: {
					403: function($data) {
						$("#useridMsg").html($data.responseJSON.responseHeader.responseMessage);
					} 
				},
				dataType: 'html'
			});
		},
		
		
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

		},
		
		fadeMessage:function($namespace, $id, $duration) {
			var $selectorName = "#" + $namespace + "_" + $id;
			if ( $duration == null ) {
				$duration=6000;
			}
			$($selectorName).fadeOut($duration);
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
		init: function($namespace, $divisionList, $modalNamespace, $jobDetail) {
			if ( $divisionList != null ) {
				var $divisionLookup = {}
				$.each($divisionList, function($index, $division) {
					$divisionLookup[$division.divisionId]=$division.divisionCode;
				});
				if($("#" + $namespace + "_divisionId").is("select")){
					JOBPANEL.setDivisionList($namespace, $divisionList);
				}
			}
			JOBPANEL.initActivateModal($namespace, $modalNamespace);
			JOBPANEL.initCancelModal($namespace, $modalNamespace);
			JOBPANEL.initScheduleModal($namespace, $modalNamespace);
			
			//make the date selectors work in the modal window
			var $selector= '.' + $modalNamespace + "_datefield";
			$($selector).datepicker({
                prevText:'&lt;&lt;',
                nextText: '&gt;&gt;',
                showButtonPanel:true
            });
			
			
			if ( $jobDetail != null ) {
				ANSI_UTILS.setFieldValue($namespace, "jobId", $jobDetail.jobId);
				if($("#" + $namespace + "_jobStatus").is("span")){
					ANSI_UTILS.setTextValue($namespace, "jobStatus", $jobDetail.status);
				} else {
					$("#" + $namespace + "_jobStatus").val($jobDetail.status);
				}
				if($("#" + $namespace + "_divisionId").is("span")){
				  ANSI_UTILS.setTextValue($namespace, "divisionId", $divisionLookup[$jobDetail.divisionId]);
				} else {
					$("#" + $namespace + "_divisionId").val($divisionLookup[$jobDetail.divisionId]);
					$("#" + $namespace + "_divisionId").selectmenu("refresh");
				}
				ANSI_UTILS.setTextValue($namespace, "quoteId", $jobDetail.jobId);
				
				var $activateJobButtonSelector = "#" + $namespace + "_activateJobButton";
				if ( $jobDetail.canActivate == true ) {							
					$($activateJobButtonSelector).show();
				} else {
					$($activateJobButtonSelector).hide();
				}
				
				var $cancelJobButtonSelector = "#" + $namespace + "_cancelJobButton";
				$($cancelJobButtonSelector).attr('data-jobid', $jobDetail.jobId);
				if ( $jobDetail.canCancel == true ) {		
					$($cancelJobButtonSelector).show();
				} else {
					$($cancelJobButtonSelector).hide();
				}
			}

		},

		initActivateModal: function($namespace, $modalNamespace) {
			var $activateJobButtonSelector = "#" + $namespace + "_activateJobButton";
			var $activateJobFormDialogSelector = "#" + $modalNamespace + "_activateJobForm";
			var $goButtonId = $namespace + "_activateFormButton";
			var $closeButtonId = $namespace + "_activateFormCloseButton";
			                                       
			
			$($activateJobButtonSelector).click(function() {
				//$("#updateOrAdd").val("add");
        		//clearAddForm();				
        		$('#'+$goButtonId).button('option', 'label', 'Activate Job');
        		$('#'+$closeButtonId).button('option', 'label', 'Close');
        	    $($activateJobFormDialogSelector).dialog( "open" );
			});
			
			
			// set up the activate job modal window
			$( $activateJobFormDialogSelector ).dialog({
	      	      autoOpen: false,
	      	      height: 300,
	      	      width: 500,
	      	      modal: true,
	      	      buttons: [
	      	    	  {
	      	    			id: $goButtonId,
	      	        		click: function() {
	      	        			JOBPANEL.activateJob($namespace, $modalNamespace);
	      	        		}
	      	      		},
	      	      		{
	        	    		id: $closeButtonId,
	        	        	click: function() {
	        	        		$( $activateJobFormDialogSelector ).dialog( "close" );
	        	        	}
	        	      	}
	      	      
	      	      ],
	      	      close: function() {
	      	    	  $( $activateJobFormDialogSelector ).dialog( "close" );
	      	        //allFields.removeClass( "ui-state-error" );
	      	      }
	      	    });
	
		},
	
		
		
		initCancelModal: function($namespace, $modalNamespace) {
			var $cancelJobButtonSelector = "#" + $namespace + "_cancelJobButton";
			var $cancelJobFormDialogSelector = "#" + $modalNamespace + "_cancelJobForm";
			var $goButtonId = $namespace + "_cancelFormButton";
			var $closeButtonId = $namespace + "_cancelFormCloseButton";
			var $cancelFieldSelector = "." + $modalNamespace + "_cancelField"
			var $cancelMessageSelector = "." + $modalNamespace + "_cancelMessage";
			
			$($cancelFieldSelector).focus(function() {
				$($cancelMessageSelector).html("");
			});
			
			$($cancelJobButtonSelector).click(function() {
				//$("#updateOrAdd").val("add");
        		//clearAddForm();				
        		$('#'+$goButtonId).button('option', 'label', 'Cancel Job');
        		$('#'+$closeButtonId).button('option', 'label', 'Close');
        	    $($cancelJobFormDialogSelector).dialog( "open" );
			});
			
			
			// set up the cancel job modal window
			$( $cancelJobFormDialogSelector ).dialog({
	      	      autoOpen: false,
	      	      height: 300,
	      	      width: 500,
	      	      modal: true,
	      	      buttons: [
	      	    	  {
	      	    			id: $goButtonId,
	      	        		click: function() {
	      	        			JOBPANEL.cancelJob($namespace, $modalNamespace);
	      	        		}
	      	      		},
	      	      		{
	        	    		id: $closeButtonId,
	        	        	click: function() {
	        	        		$( $cancelJobFormDialogSelector ).dialog( "close" );
	        	        	}
	        	      	}
	      	      
	      	      ],
	      	      close: function() {
	      	    	  $( $cancelJobFormDialogSelector ).dialog( "close" );
	      	        //allFields.removeClass( "ui-state-error" );
	      	      }
	      	    });
	
		},
		
		initScheduleModal: function($namespace, $modalNamespace) {
			var $scheduleJobButtonSelector = "#" + $namespace + "_scheduleJobButton";
			var $scheduleJobFormDialogSelector = "#" + $modalNamespace + "_scheduleJobForm";
			var $goButtonId = $namespace + "_scheduleFormButton";
			var $closeButtonId = $namespace + "_scheduleFormCloseButton";
			                                       
			
			$($scheduleJobButtonSelector).click(function() {
				//$("#updateOrAdd").val("add");
        		//clearAddForm();				
        		$('#'+$goButtonId).button('option', 'label', 'Schedue Job');
        		$('#'+$closeButtonId).button('option', 'label', 'Close');
        	    $($scheduleJobFormDialogSelector).dialog( "open" );
			});
			
			
			// set up the schedule job modal window
			$( $scheduleJobFormDialogSelector ).dialog({
	      	      autoOpen: false,
	      	      height: 300,
	      	      width: 500,
	      	      modal: true,
	      	      buttons: [
	      	    	  {
	      	    			id: $goButtonId,
	      	        		click: function() {
	      	        			JOBPANEL.scheduleJob($namespace, $modalNamespace);
	      	        		}
	      	      		},
	      	      		{
	        	    		id: $closeButtonId,
	        	        	click: function() {
	        	        		$( $scheduleJobFormDialogSelector ).dialog( "close" );
	        	        	}
	        	      	}
	      	      
	      	      ],
	      	      close: function() {
	      	    	  $( $scheduleJobFormDialogSelector ).dialog( "close" );
	      	        //allFields.removeClass( "ui-state-error" );
	      	      }
	      	    });
	
		},
	
		
		
		setDivisionList: function($namespace, $divisionList) {
			selectorName = "#"+$namespace+"_divisionId";
			console.log(selectorName);
			var $select = $(selectorName);
			//$('option', $select).remove();

			$select.append(new Option("",""));
			$.each($divisionList, function(index, val) {
			    $select.append(new Option(val.divisionCode, val.divisionId));
			});
			
			$select.selectmenu();
		},
		
		cancelJob: function($namespace, $modalNamespace) {
			//$event.preventDefault();
			//var $jobid = $event.currentTarget.attributes['data-jobid'].value;
			$jobId = ANSI_UTILS.getFieldValue($namespace, "jobId");
			var $date = ANSI_UTILS.getFieldValue($modalNamespace, "cancelDate");
			var $reason = ANSI_UTILS.getFieldValue($modalNamespace, "cancelReason");
			var $url = "job/" + $jobId
			var $outbound = {'action':'CANCEL_JOB','cancelDate':$date,'cancelReason':$reason};
			
			var jqxhr3 = $.ajax({
				type: 'POST',
				url: $url,
				data: JSON.stringify($outbound),
				statusCode: {
					200: function($data) {
						if ( $data.responseHeader.responseCode == 'EDIT_FAILURE') {
							var $cancelJobFormDialogSelector = "#" + $modalNamespace + "_cancelJobForm";
							var $cancelFieldSelector = "." + $modalNamespace + "_cancelField"
							var $cancelMessageSelector = "." + $modalNamespace + "_cancelMessage";							
							$($cancelMessageSelector).html("");
							$($cancelFieldSelector).val("");
							$.each($data.data.webMessages, function(index, $value){
								var $message = "";
								$.each($value, function(idx2, $msg){
									$message = $message + " " + $msg;
								});
								var $msgSelector = index + "_msg";
								ANSI_UTILS.setTextValue($modalNamespace, $msgSelector, $message);
							});
						}
						if ( $data.responseHeader.responseCode == 'SUCCESS') {
							var $cancelJobFormDialogSelector = "#" + $modalNamespace + "_cancelJobForm";
							var $cancelFieldSelector = "." + $modalNamespace + "_cancelField"
							var $cancelMessageSelector = "." + $modalNamespace + "_cancelMessage";							
							$( $cancelJobFormDialogSelector ).dialog( "close" );
							$($cancelMessageSelector).html("");
							$($cancelFieldSelector).val("");
							ANSI_UTILS.setTextValue($namespace, "panelMessage", "Update Successful");
							JOB_UTILS.fadeMessage($namespace, "panelMessage")
							JOB_UTILS.panelLoad($jobId);
						}
					},				
					403: function($data) {
						ANSI_UTILS.setTextValue($namespace, "panelMessage", "Function Not Permitted");
						JOB_UTILS.fadeMessage($namespace, "panelMessage")
					}, 
					404: function($data) {
						ANSI_UTILS.setTextValue($namespace, "panelMessage", "Resource Not Available");
						JOB_UTILS.fadeMessage($namespace, "panelMessage")
					}, 
					500: function($data) {
						ANSI_UTILS.setTextValue($namespace, "panelMessage", "System Error -- Contact Support");
					} 
				},
				dataType: 'json'
			});
		},
		
		activateJob: function($namespace, $modalNamespace) {
			$jobId = ANSI_UTILS.getFieldValue($namespace, "jobId");
			var $startDate = ANSI_UTILS.getFieldValue($modalNamespace, "startDate");
			var $activationDate = ANSI_UTILS.getFieldValue($modalNamespace, "activationDate");
			var $url = "job/" + $jobId
			var $outbound = {'action':'ACTIVATE_JOB','startDate':$startDate,'activationDate':$activationDate};
			
			var jqxhr3 = $.ajax({
				type: 'POST',
				url: $url,
				data: JSON.stringify($outbound),
				statusCode: {
					200: function($data) {
						if ( $data.responseHeader.responseCode == 'EDIT_FAILURE') {
							var $activateJobFormDialogSelector = "#" + $modalNamespace + "_activateJobForm";
							var $activateFieldSelector = "." + $modalNamespace + "_activateField"
							var $activateMessageSelector = "." + $modalNamespace + "_activateMessage";							
							$($activateMessageSelector).html("");
							$($activateFieldSelector).val("");
							$.each($data.data.webMessages, function(index, $value){
								var $message = "";
								$.each($value, function(idx2, $msg){
									$message = $message + " " + $msg;
								});
								var $msgSelector = index + "_msg";
								ANSI_UTILS.setTextValue($modalNamespace, $msgSelector, $message);
							});
						}
						if ( $data.responseHeader.responseCode == 'SUCCESS') {
							var $activateJobFormDialogSelector = "#" + $modalNamespace + "_activateJobForm";
							var $activateFieldSelector = "." + $modalNamespace + "_activateField"
							var $activateMessageSelector = "." + $modalNamespace + "_activateMessage";							
							$( $activateJobFormDialogSelector ).dialog( "close" );
							$($activateMessageSelector).html("");
							$($activateFieldSelector).val("");
							ANSI_UTILS.setTextValue($namespace, "panelMessage", "Update Successful");
							JOB_UTILS.fadeMessage($namespace, "panelMessage")
							JOB_UTILS.panelLoad($jobId);
						}
					},				
					403: function($data) {
						ANSI_UTILS.setTextValue($namespace, "panelMessage", "Function Not Permitted");
						JOB_UTILS.fadeMessage($namespace, "panelMessage")
					}, 
					404: function($data) {
						ANSI_UTILS.setTextValue($namespace, "panelMessage", "Resource Not Available");
						JOB_UTILS.fadeMessage($namespace, "panelMessage")
					}, 
					500: function($data) {
						ANSI_UTILS.setTextValue($namespace, "panelMessage", "System Error -- Contact Support");
					} 
				},
				dataType: 'json'
			});
		},
		
		scheduleJob: function($namespace, $modalNamespace) {
			$jobId = ANSI_UTILS.getFieldValue($namespace, "jobId");
			var $startDate = ANSI_UTILS.getFieldValue($modalNamespace, "scheduleStartDate");
			var $url = "job/" + $jobId
			var $outbound = {'action':'SCHEDULE_JOB','startDate':$startDate};
			
			var jqxhr3 = $.ajax({
				type: 'POST',
				url: $url,
				data: JSON.stringify($outbound),
				statusCode: {
					200: function($data) {
						if ( $data.responseHeader.responseCode == 'EDIT_FAILURE') {
							var $scheduleJobFormDialogSelector = "#" + $modalNamespace + "_scheduleJobForm";
							var $scheduleFieldSelector = "." + $modalNamespace + "_scheduleField"
							var $scheduleMessageSelector = "." + $modalNamespace + "_scheduleMessage";							
							$($scheduleMessageSelector).html("");
							$($scheduleFieldSelector).val("");
							$.each($data.data.webMessages, function(index, $value){
								var $message = "";
								$.each($value, function(idx2, $msg){
									$message = $message + " " + $msg;
								});
								var $msgSelector = index + "_msg";
								ANSI_UTILS.setTextValue($modalNamespace, $msgSelector, $message);
							});
						}
						if ( $data.responseHeader.responseCode == 'SUCCESS') {
							var $scheduleJobFormDialogSelector = "#" + $modalNamespace + "_scheduleJobForm";
							var $scheduleFieldSelector = "." + $modalNamespace + "_scheduleField"
							var $scheduleMessageSelector = "." + $modalNamespace + "_scheduleMessage";							
							$( $scheduleJobFormDialogSelector ).dialog( "close" );
							$($scheduleMessageSelector).html("");
							$($scheduleFieldSelector).val("");
							ANSI_UTILS.setTextValue($namespace, "panelMessage", "Update Successful");
							JOB_UTILS.fadeMessage($namespace, "panelMessage")
							JOB_UTILS.panelLoad($jobId);
						}
					},				
					403: function($data) {
						ANSI_UTILS.setTextValue($namespace, "panelMessage", "Function Not Permitted");
						JOB_UTILS.fadeMessage($namespace, "panelMessage")
					}, 
					404: function($data) {
						ANSI_UTILS.setTextValue($namespace, "panelMessage", "Resource Not Available");
						JOB_UTILS.fadeMessage($namespace, "panelMessage")
					}, 
					500: function($data) {
						ANSI_UTILS.setTextValue($namespace, "panelMessage", "System Error -- Contact Support");
					} 
				},
				dataType: 'json'
			});
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
		init: function($namespace, $jobDetail, $lastRun, $nextDue, $lastCreated) {
			if ( $lastRun != null && $lastRun.processDate != null ) {
				ANSI_UTILS.setTextValue($namespace, "lastRun", $lastRun.processDate + " (T" + $lastRun.ticketId + ")");
			}
			if ( $nextDue != null && $nextDue.startDate != null ) {
				ANSI_UTILS.setTextValue($namespace, "nextDue", $nextDue.startDate + " (T" + $nextDue.ticketId + ")");
			}
			if ( $lastCreated != null && $lastCreated.startDate != null) {
				ANSI_UTILS.setTextValue($namespace, "createdThru", $lastCreated.startDate + " (T" + $lastCreated.ticketId + ")");
			}
			if ($jobDetail != null && $jobDetail.repeatScheduleAnnually == 1) {
				ANSI_UTILS.setCheckbox($namespace, "annualRepeat", true);
			} else {
				ANSI_UTILS.setCheckbox($namespace, "annualRepeat", false);
			}
			
			var $ticketListSelector = "#" + $namespace + "_showTicketList";
			$($ticketListSelector).click(function($event){
				var $jobId = JOB_DATA.jobId;
				location.href="ticketLookup.html?jobId=" + $jobId;
			});
			
			
			var $repeatSelectorName = "#" + $namespace + "_" + "annualRepeat";
			$($repeatSelectorName).click(function($event) {
				var $jobId = JOB_DATA.jobId;
				var $isChecked = $($repeatSelectorName).prop('checked');
				var $outbound = {"action":"REPEAT_JOB", "annualRepeat":$isChecked};
				var $url = "job/" + $jobId;

				var jqxhr3 = $.ajax({
					type: 'POST',
					url: $url,
					data: JSON.stringify($outbound),
					statusCode: {
						200: function($data) {
							if ( $data.responseHeader.responseCode == 'EDIT_FAILURE') {
								$("#globalMsg").html("Edit Error").show().fadeOut(4000);
							}
							if ( $data.responseHeader.responseCode == 'SUCCESS') {
								$("#globalMsg").html("Update Successful").show().fadeOut(4000);
								JOB_UTILS.panelLoad($jobId);
							}
						},				
						403: function($data) {
							$("#globalMsg").html("Function Not Permitted");
						}, 
						404: function($data) {
							$("#globalMsg").html("Invalid Request");
						}, 
						500: function($data) {
							$("#globalMsg").html("System error; contact support");
						} 
					},
					dataType: 'json'
				});
			});
		}
	}
});



