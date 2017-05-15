$( document ).ready(function() {
	;JOB_DATA = {}
	;QUOTE_DATA = {}
	;Page = null;
	var $jobContactId = null;
	var $siteContactId = null;
	var $contractContactId = null;
	var $billingContactId = null;
	;JOB_UTILS = {
		pageInit:function($jobId) {

			$optionData = ANSI_UTILS.getOptions('JOB_FREQUENCY,JOB_STATUS,INVOICE_TERM,INVOICE_GROUPING,INVOICE_STYLE,COUNTRY');
			//console.log($optionData);
			
			JOB_DATA.jobFrequencyList = $optionData.jobFrequency;
			JOB_DATA.jobStatusList = $optionData.jobStatus;
			JOB_DATA.invoiceTermList = $optionData.invoiceTerm;
			JOB_DATA.invoiceGroupingList = $optionData.invoiceGrouping;
			
			JOB_DATA.invoiceStyleList = $optionData.invoiceStyle;
			JOB_DATA.countryList = $optionData.country;
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
				console.log($jobDetail);
				JOB_DATA.jobDetail = $jobDetail;
				$quoteDetail = $jobData.quote;
				$lastRun = $jobData.lastRun;
				$nextDue = $jobData.nextDue;
				$lastCreated = $jobData.lastCreated;
			}
			
			var jqxhr1 = $.ajax({
				type: 'GET',
				url: 'quotePanel.html',
				data: {"namespace":'0',"page":'JOB'},
				success: function($data) {
					$('#jobPanelHolder > tbody:last-child').replaceWith($data);
					JOBPANEL.init("0_jobPanel", JOB_DATA.divisionList, "activateModal", $jobDetail);
					JOBPROPOSAL.init("0_jobProposal", JOB_DATA.jobFrequencyList, $jobDetail);
					JOBACTIVATION.init("0_jobActivation", JOB_DATA.buildingTypeList, $jobDetail);
					JOBDATES.init("0_jobDates", $quoteDetail, $jobDetail);
					JOBSCHEDULE.init("0_jobSchedule", $jobDetail, $lastRun, $nextDue, $lastCreated)
					JOBINVOICE.init("0_jobInvoice", JOB_DATA.invoiceStyleList, JOB_DATA.invoiceGroupingList, JOB_DATA.invoiceTermList, $jobDetail);
					JOBAUDIT.init("0_jobAudit", $jobDetail);
					ADDRESSPANEL.init("0_jobSite", JOB_DATA.countryList);
					ADDRESSPANEL.init("0_billTo", JOB_DATA.countryList);
					
					
					console.log("Quote ID: "+$jobDetail.quoteId);
					if ( $jobDetail.quoteId != '' ) {
						var $quoteDetails = QUOTEUTILS.getQuoteDetail($jobDetail.quoteId);
						var $quoteData = $quoteDetails.quote;
					}
					

					if($quoteData != null){
						console.log($quoteData);
						if($quoteDetails.billTo != null){
							ADDRESSPANEL.setAddress("0_billTo",$quoteDetails.billTo);
						}
						if($quoteDetails.jobSite != null){
							ADDRESSPANEL.setAddress("0_jobSite",$quoteDetails.jobSite);
						}
						
					
						
					}
					
				},
				statusCode: {
					403: function($data) {
						$("#globalMsg").html($data.responseJSON.responseHeader.responseMessage);
					} 
				},
				dataType: 'html'
			});
		},
		
		panelLoadQuote:function($namespace, $jobId, $index, $quoteData) {

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
			
			QUOTE_DATA.data = $quoteData;
			var $jobDetail = [];
			$jobDetail['jobContactId'] = null;
			$jobDetail['siteContact'] = null;
			$jobDetail['contractContactId'] = null;
			$jobDetail['billingContactId'] = null;
			
			var $quoteDetail = null;
			var $lastRun = null;
			var $nextDue = null;
			var $lastCreated = null;
			var $returnData = [];
			
			if ( $jobId != '' ) {
				$jobData = JOB_UTILS.getJobDetail($jobId);				
				$jobDetail = $jobData.job;
				$quoteDetail = $jobData.quote;
				$lastRun = $jobData.lastRun;
				$nextDue = $jobData.nextDue;
				$lastCreated = $jobData.lastCreated;
				console.log("Job Data");
				console.log($jobDetail);
				
				if($jobDetail != null){
					$returnData['jobContactId'] = $jobDetail.jobContactId;
					$returnData['siteContact'] = $jobDetail.siteContact;
					$returnData['contractContactId'] = $jobDetail.contractContactId;
					$returnData['billingContactId'] = $jobDetail.billingContactId;
				}
			}
			
			
		
			
			
			var jqxhr1 = $.ajax({
				type: 'GET',
				url: 'quotePanel.html',
				data: {"namespace":$namespace,"page":'QUOTE'},
				success: function($data) {
					$("#loadingJobsDiv").hide();
					$('#accordian').append("<h3 id="+$namespace+"_jobHeader><span id='"+$namespace+"_jobStatusHead'></span>&nbsp;&nbsp;Job: <span id='"+$namespace+"_jobIdHead'></span>&nbsp;&nbsp;Job #: <span id='"+$namespace+"_jobNumberHead'></span>&nbsp;&nbsp;PPC: <span id='"+$namespace+"_jobPPCHead'></span>&nbsp;&nbsp; Frequency: <span id='"+$namespace+"_jobFreqHead'></span></h3>");
					$('#accordian').append("<div id="+$namespace+"_jobDiv>"+$data+"</div>");
					JOBPANEL.init($namespace+"_jobPanel", JOB_DATA.divisionList, "activateModal", $jobDetail);
					JOBPROPOSAL.init($namespace+"_jobProposal", JOB_DATA.jobFrequencyList, $jobDetail);
					JOBACTIVATION.init($namespace+"_jobActivation", JOB_DATA.buildingTypeList, $jobDetail);
					$("#" + $namespace+"_jobActivation" + "_nbrFloors").spinner( "option", "disabled", false );
					JOBDATES.init($namespace+"_jobDates", $quoteDetail, $jobDetail);
					//console.log("Job Schedule NameSpace: "+$namespace+"_jobSchedule");
					JOBSCHEDULE.init($namespace+"_jobSchedule", $jobDetail, $lastRun, $nextDue, $lastCreated)
					JOBINVOICE.init($namespace+"_jobInvoice", JOB_DATA.invoiceStyleList, JOB_DATA.invoiceGroupingList, JOB_DATA.invoiceTermList, $jobDetail);
					JOBAUDIT.init($namespace+"_jobAudit", $jobDetail);
					if($jobDetail.jobId != null){
						$("#"+$namespace+"_jobIdHead").html($jobDetail.jobId);
					}
					if($jobDetail.status != null){
						$("#"+$namespace+"_jobStatusHead").html($jobDetail.status);					
					}
					if($jobDetail.jobNbr != null){
						$("#"+$namespace+"_jobNumberHead").html($jobDetail.jobNbr);	
					}
					if($jobDetail.pricePerCleaning != null){
						$("#"+$namespace+"_jobPPCHead").html($jobDetail.pricePerCleaning);	
					}
					if($jobDetail.jobFrequency != null){
						$("#"+$namespace+"_jobFreqHead").html($jobDetail.jobFrequency);
					}
					$("#"+$namespace+"_jobPanel_jobLink").attr("href", "jobMaintenance.html?id="+$jobId);
					$("#"+$namespace+"_jobPanel_jobLink").text($jobId);
					//$(".addressTable").remove();
					$.each($('input'), function () {
				        $(this).css("height","20px");
				        $(this).css("max-height", "20px");
				    });
					$('.jobSave').on('click', function($clickevent) {
						JOB_UTILS.addJob($(this).attr("rownum"),$jobDetail.quoteId, $namespace);
		            });
				},
				statusCode: {
					403: function($data) {
						$("#useridMsg").html($data.responseJSON.responseHeader.responseMessage);
					} 
				},
				dataType: 'html'
			});
			
			return $returnData;
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
							$("#globalMsg").html("System Error: Contact Support");
							$returnValue={};
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
		},
		
		addJob: function($rn,$quoteId, $namespace, $jobContactId1, $siteContactId1, $contractContactId1, $billingContactId1) {
			var $url = "job/add";
			
			if($jobContactId1 != null){
				$jobContactId = $jobContactId1;
			} if($siteContactId1 != null){
				$siteContactId = $siteContactId1;
			} if($contractContactId1 != null){
				$contractContactId = $contractContactId1;
			} if($billingContactId1 != null){
				$billingContactId = $billingContactId1;
			}
			var $outbound = {};
//			alert("job addJob - quoteId:" + $quoteId);
//			alert($globalQuoteId);
//    		$outbound["action"]	= 'ADD_JOB';
    		$pre = "#"+$rn;
    		
//			alert("job addJob - jobId:" + $($pre+"_jobPanel_jobId").val());
    		if ($($pre+"_jobPanel_jobId").val()) {
    			$url = "job/"+$($pre+"_jobPanel_jobId").val();
        		$outbound["action"]	= 'UPDATE_JOB';			
    		} else {
        		$outbound["action"]	= 'ADD_JOB';
    		}
    		$outbound["activationDate"]				= $($pre+"_jobDates_activationDate").html();
    		$outbound["billingContactId"]			= $billingContactId;
    		$outbound["billingNotes"]				= $($pre+"_jobActivation_billingNotes").val();
    		$outbound["budget"]						= $($pre+"_jobActivation_directLaborBudget").val();
    		$outbound["buildingType"]				= $($pre+"_jobActivation_buildingType").val();
    		$outbound["cancelDate"]					= $($pre+"_jobDates_cancelDate").html();
    		$outbound["cancelReason"]				= $($pre+"_jobDates_cancelReason").html();
    		$outbound["contractContactId"]			= $contractContactId;
    		$outbound["directLaborPct"]				= $($pre+"_jobActivation_directLaborPct").val();
    		$outbound["divisionId"]					= $($pre+"_jobPanel_divisionId").val();
    		$outbound["equipment"]					= $($pre+"_jobActivation_equipment").val();
    		$outbound["expirationDate"]				= $($pre+"_jobInvoice_invoiceExpire").val();
    		$outbound["expirationReason"]			= $($pre+"_jobInvoice_invoiceExpireReason").val();
    		$outbound["floors"]						= $($pre+"_jobActivation_nbrFloors").val();
    		$outbound["invoiceBatch"]				= $($pre+"_jobInvoice_invoiceBatch").val();
    		$outbound["invoiceGrouping"]			= $($pre+"_jobInvoice_invoiceGrouping").val();
    		$outbound["invoiceStyle"]				= $($pre+"_jobInvoice_invoiceStyle").val();
    		$outbound["invoiceTerms"]				= $($pre+"_jobInvoice_invoiceTerms").val();
    		$outbound["jobContactId"]				= $jobContactId;
    		$outbound["jobFrequency"]				= $($pre+"_jobProposal_jobFrequency").val();
    		$outbound["jobNbr"]						= $($pre+"_jobProposal_jobNbr").val();
    		$outbound["jobTypeId"]					= null;
    		$outbound["lastPriceChange"]			= null;
    		$outbound["lastReviewDate"]				= null;
    		$outbound["omNotes"]					= $($pre+"_jobActivation_omNotes").val();
    		$outbound["ourVendorNbr"]				= $($pre+"_jobInvoice_invoiceOurVendorNbr").val();
    		$outbound["paymentTerms"]				= null;
    		$outbound["poNumber"]					= $($pre+"_jobInvoice_invoicePO").val();
    		$outbound["pricePerCleaning"]			= $($pre+"_jobProposal_ppc").val();
    		$outbound["quoteId"]					= $quoteId;
    		$outbound["repeatScheduleAnnually"]		= $($pre+"_jobSchedule_annualRepeat").val();
    		$outbound["requestSpecialScheduling"]	= 0;
    		$outbound["serviceDescription"]			= $($pre+"_jobProposal_serviceDescription").val();
    		$outbound["siteContact"]				= $siteContactId;
    		$outbound["startDate"]					= $($pre+"_jobDates_startDate").html();
    		$outbound["status"]						= $($pre+"_jobPanel_jobStatus").val();
    		$outbound["taxExempt"]					= 1;
    		if(($($pre+"_jobInvoice_invoiceTaxExempt").val()) == "on") {
    			$outbound["taxExempt"] = 0;
    		} 
    		
    		$outbound["washerNotes"]				= $($pre+"_jobActivation_washerNotes").val();
 
    		console.log($outbound);
    		
    		
			var jqxhr3 = $.ajax({
				type: 'POST',
				url: $url,
				data: JSON.stringify($outbound),
				statusCode: {
					200: function($data) {
						$(".inputIsInvalid").removeClass("fa-ban");
						$(".inputIsInvalid").removeClass("inputIsInvalid");
						console.log("Return 200");
						console.log($data);
						if ( $data.responseHeader.responseCode == 'EDIT_FAILURE') {
							
							$.each($data.data.webMessages, function(key, messageList) {
								var identifier = "#"+key+"Err";
								$(identifier).addClass("fa-ban");
								$(identifier).addClass("inputIsInvalid");
							});
							
							alert("Edit Failure - missing data");
							console.log("Edit Fail");
						}
						if ( $data.responseHeader.responseCode == 'SUCCESS') {
							console.log("Success!");
							$($pre+"_jobPanel_jobId").val($data.data.job.jobId);
							$($pre+"_jobPanel_jobStatus").val($data.data.job.status);
							$userData = ANSI_UTILS.getUser($data.data.job.addedBy);
//************* FINISH AUDIT
							//JOBAUDIT.init($rn,$data.data.job);
							ANSI_UTILS.setTextValue($namespace, "panelMessage", "Update Successful");
							JOB_UTILS.fadeMessage($namespace, "panelMessage")
							//JOB_UTILS.panelLoad($jobId);
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
		update: function($outbound){
			console.log("Update Outbound: ");
    		console.log($outbound);

    		$url = "job/update/"+$("input[name=jobId]").val();
//			console.log($outbound);
			var jqxhr = $.ajax({
				type: 'POST',
				url: $url,
				data: JSON.stringify($outbound),
				success: function($data) {
					if ( $data.responseHeader.responseCode == 'SUCCESS') {

						console.log("Update Success");
						console.log($data);
							if ( 'GLOBAL_MESSAGE' in $data.data.webMessages ) {
								$("#globalMsg").html($data.data.webMessages['GLOBAL_MESSAGE'][0]).fadeIn(10).fadeOut(6000);
							}
						
					} else if ( $data.responseHeader.responseCode == 'EDIT_FAILURE') {
						
						console.log("Edit Failure");
						console.log($data);
						$.each($data.data.webMessages, function(key, messageList) {
							var identifier = "#" + key + "Err";
							msgHtml = "<ul>";
							$.each(messageList, function(index, message) {
								msgHtml = msgHtml + "<li>" + message + "</li>";
							});
							msgHtml = msgHtml + "</ul>";
							$(identifier).html(msgHtml);
						});		
					} else {
						console.log("Save Other");
					}
				},
				error: function($data) {
					console.log("Fail: ");
					console.log($data);
				},
				statusCode: {
					403: function($data) {
						$("#globalMsg").html($data.responseJSON.responseHeader.responseMessage);
					} 
				},
				dataType: 'json'
			});
		}
	}
	
	
	
	;JOBACTIVATION = {
			init: function($namespace, $buildingTypeList, $jobDetail) {
				//console.log("Job Activation Namespace: "+$namespace);
				var selectorName = "select[name='" + $namespace + "_automanual']";
				$(selectorName).selectmenu({ width : '75px', maxHeight: '400 !important', style: 'dropdown'});
				if ( $jobDetail == null ) {
					JOBACTIVATION.setBuildingType($namespace, QUOTEUTILS.setSelectMenu("buildingType",ANSI_UTILS.getCodes("job","building_type")), $jobDetail.buildingType);
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
				//$("#" + $namespace + "_nbrFloors").prop('disabled', true);
				//OBACTIVATION.setBuildingType("buildingType",ANSI_UTILS.getCodes("job","building_type"));
				//$("#" + $namespace + "_jobActivationForm select[name='" + $namespace + "_automanual']").selectmenu();
				//$("#" + $namespace + "_jobActivationForm select[name='" + $namespace + "_buildingType']").selectmenu();
				
					$("#" + $namespace + "_jobActivationForm input[name='" + $namespace + "_nbrFloors']").spinner({disabled: true,
						spin: function( event, ui ) {
							if ( ui.value < 0 ) {
								$( this ).spinner( {value: 0 });
								return false;
							}
						}
					});					
				
				
				$selectorName = "#" + $namespace + "_activationEdit";
				$($selectorName).click(function($event) {
					$event.preventDefault();

					
					if($("#" + $namespace + "_activationEdit").hasClass( "fa-pencil" )){
						$("#" + $namespace + "_automanual").selectmenu( "option", "disabled", false );
						$("#" + $namespace + "_buildingType").selectmenu( "option", "disabled", false );
						$("#" + $namespace + "_directLaborPct").prop('disabled', false);
						$("#" + $namespace + "_directLaborBudget").prop('disabled', false);
						$("#" + $namespace + "_nbrFloors").spinner( "option", "disabled", false );
						$("#" + $namespace + "_equipment").prop('disabled', false);
						$("#" + $namespace + "_washerNotes").prop('disabled', false);
						$("#" + $namespace + "_omNotes").prop('disabled', false);
						$("#" + $namespace + "_billingNotes").prop('disabled', false);
						
						$("#" + $namespace + "_activationEdit").removeClass('fa-pencil');
						$("#" + $namespace + "_activationEdit").addClass('fa-save');
					} else if($("#" + $namespace + "_activationEdit").hasClass( "fa-save" )){
						$("#" + $namespace + "_automanual").selectmenu( "option", "disabled", true );
						$("#" + $namespace + "_buildingType").selectmenu( "option", "disabled", true );
						$("#" + $namespace + "_directLaborPct").prop('disabled', true);
						$("#" + $namespace + "_directLaborBudget").prop('disabled', true);
						$("#" + $namespace + "_nbrFloors").spinner( "option", "disabled", true );
						$("#" + $namespace + "_equipment").prop('disabled', true);
						$("#" + $namespace + "_washerNotes").prop('disabled', true);
						$("#" + $namespace + "_omNotes").prop('disabled', true);
						$("#" + $namespace + "_billingNotes").prop('disabled', true);
						
						$outbound = {};
						

						$outbound["buildingType"]	= $("#" + $namespace + "_buildingType").val();
						$outbound["directLaborPct"]	= $("#" + $namespace + "_directLaborPct").val();
						$outbound["directLaborBudget"]	= $("#" + $namespace + "_directLaborBudget").val();
						$outbound["nbrFloors"]	= $("#" + $namespace + "_nbrFloors").val();
						$outbound["equipment"]	= $("#" + $namespace + "_equipment").val();
						$outbound["washerNotes"]	= $("#" + $namespace + "_washerNotes").val();
						$outbound["omNotes"]	= $("#" + $namespace + "_omNotes").val();
						$outbound["billingNotes"]	= $("#" + $namespace + "_billingNotes").val();
						
						JOB_UTILS.update($outbound);
		        	
						$("#" + $namespace + "_activationEdit").removeClass('fa-save');
						$("#" + $namespace + "_activationEdit").addClass('fa-pencil');
					}
				});
				
				},
				
				
				setBuildingType: function ($namespace, $data, $selectedValue) {

					var selectorName = "select[name='" + $namespace + "_buildingType']";
					var $select = $(selectorName);
					$('option', $select).remove();
					$select.append(new Option("",null));
					$.each($data, function($index, $val) {
						$select.append(new Option($val.displayValue,$val.value));
					});
					
					$select.selectmenu({ width : '175px', maxHeight: '400 !important', style: 'dropdown'});
					
					if($selectedValue != null) { 
						$select.val($selectedValue);
						$select.selectmenu("refresh");
					}
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
						$selectorName = "#" + $namespace + "_invoiceBatch_is_yes";
						$($selectorName).show();
						$selectorName = "#" + $namespace + "_invoiceBatch_is_no";
						$($selectorName).hide();
					} else {
						ANSI_UTILS.setCheckbox($namespace, "invoiceBatch", false);
						$selectorName = "#" + $namespace + "_invoiceBatch_is_yes";
						$($selectorName).hide();
						$selectorName = "#" + $namespace + "_invoiceBatch_is_no";
						$($selectorName).show();
					}
					if ( $jobDetail.taxExempt == 1) {
						ANSI_UTILS.setCheckbox($namespace, "invoiceTaxExempt", true);
						$selectorName = "#" + $namespace + "_invoiceTaxExempt_is_yes";
						$($selectorName).show();
						$selectorName = "#" + $namespace + "_invoiceTaxExempt_is_no";
						$($selectorName).hide();
					} else {
						ANSI_UTILS.setCheckbox($namespace, "invoiceTaxExempt", false);
						$selectorName = "#" + $namespace + "_invoiceTaxExempt_is_yes";
						$($selectorName).hide();
						$selectorName = "#" + $namespace + "_invoiceTaxExempt_is_no";
						$($selectorName).show();
					}
					ANSI_UTILS.setFieldValue($namespace, "invoicePO", $jobDetail.poNumber);
					ANSI_UTILS.setFieldValue($namespace, "invoiceOurVendorNbr", $jobDetail.ourVendorNbr);
					ANSI_UTILS.setFieldValue($namespace, "invoiceExpire", $jobDetail.expirationDate);
					ANSI_UTILS.setFieldValue($namespace, "invoiceExpireReason", $jobDetail.expirationReason);
					
					ANSI_UTILS.setTextValue($namespace, "invoicePO", $jobDetail.poNumber);
					ANSI_UTILS.setTextValue($namespace, "invoiceOurVendorNbr", $jobDetail.ourVendorNbr);
					ANSI_UTILS.setTextValue($namespace, "invoiceExpire", $jobDetail.expirationDate);
					ANSI_UTILS.setTextValue($namespace, "invoiceExpireReason", $jobDetail.expirationReason);
				}

				JOBINVOICE.setInvoiceStyle($namespace, JOB_DATA.invoiceStyleList, $jobDetail.invoiceStyle);
				JOBINVOICE.setInvoiceGrouping($namespace, JOB_DATA.invoiceGroupingList, $jobDetail.invoiceGrouping);
				JOBINVOICE.setInvoiceTerms($namespace, JOB_DATA.invoiceTermList, $jobDetail.invoiceTerms);
				
				
				var $selectorName = "input[name='" + $namespace + "_invoiceExpire']";
				$($selectorName).datepicker({
	                prevText:'&lt;&lt;',
	                nextText: '&gt;&gt;',
	                showButtonPanel:true
	            });

				var $goButtonId = $namespace + "_saveInvoiceButton";
				var $closeButtonId = $namespace + "_saveInvoiceCloseButton";
				// set up the activate job modal window
//				$( '#invoiceModal' ).dialog({
//		      	      autoOpen: false,
//		      	      height: 300,
//		      	      width: 500,
//		      	      modal: true,
//		      	      buttons: [
//		      	    	  {
//		      	    			id: $goButtonId,
//		      	        		click: function() {
//		      	        			//JOBPANEL.activateJob($namespace, $modalNamespace);
//		      	        			console.debug("gobutton clicked")
//		      	        		}
//		      	      		},
//		      	      		{
//		        	    		id: $closeButtonId,
//		        	        	click: function() {
//		        	        		$('#invoiceModal').dialog( "close" );
//		        	        	}
//		        	      	}
//		      	      
//		      	      ],
//		      	      close: function() {
//		      	    	  $( '#invoiceModal' ).dialog( "close" );
//		      	        //allFields.removeClass( "ui-state-error" );
//		      	      }
//		      	    });
//				
				$selectorName = "#" + $namespace + "_invoiceEdit";
				$($selectorName).click(function($event) {
					$event.preventDefault();

					if($("#" + $namespace + "_invoiceEdit").hasClass( "fa-pencil" )){
						$("#" + $namespace + "_invoiceStyle").selectmenu( "option", "disabled", false );
						$("#" + $namespace + "_invoiceBatch").prop('disabled', false);
						$("#" + $namespace + "_invoiceTaxExempt").prop('disabled', false);
						$("#" + $namespace + "_invoiceGrouping").selectmenu( "option", "disabled", false );
						$("#" + $namespace + "_invoiceTerms").selectmenu( "option", "disabled", false );
						$("#" + $namespace + "_invoicePO").prop('disabled', false);
						$("#" + $namespace + "_invoiceOurVendorNbr").prop('disabled', false);
						$("#" + $namespace + "_invoiceExpire").prop('disabled', false);
						$("#" + $namespace + "_invoiceExpireReason").prop('disabled', false);
						
						$("#" + $namespace + "_invoiceEdit").removeClass('fa-pencil');
						$("#" + $namespace + "_invoiceEdit").addClass('fa-save');
					} else if($("#" + $namespace + "_invoiceEdit").hasClass( "fa-save" )){
						$("#" + $namespace + "_invoiceStyle").selectmenu( "option", "disabled", true );
						$("#" + $namespace + "_invoiceBatch").prop('disabled', true);
						$("#" + $namespace + "_invoiceTaxExempt").prop('disabled', true);
						$("#" + $namespace + "_invoiceGrouping").selectmenu( "option", "disabled", true );
						$("#" + $namespace + "_invoiceTerms").selectmenu( "option", "disabled", true );
						$("#" + $namespace + "_invoicePO").prop('disabled', true);
						$("#" + $namespace + "_invoiceOurVendorNbr").prop('disabled', true);
						$("#" + $namespace + "_invoiceExpire").prop('disabled', true);
						$("#" + $namespace + "_invoiceExpireReason").prop('disabled', true);
						
						$outbound = {};
						
						$outbound["invoiceStyle"]	= $("#" + $namespace + "_invoiceStyle").val();
						$outbound["invoiceBatch"]	= $("#" + $namespace + "_invoiceBatch").val();
						$outbound["invoiceTaxExempt"]	= $("#" + $namespace + "_invoiceTaxExempt").val();
						$outbound["invoiceGrouping"]	= $("#" + $namespace + "_invoiceGrouping").val();
						$outbound["invoiceTerms"]	= $("#" + $namespace + "_invoiceTerms").val();
						$outbound["invoicePO"]	= $("#" + $namespace + "_invoicePO").val();
						$outbound["invoiceOurVendorNbr"]	= $("#" + $namespace + "_invoiceOurVendorNbr").val();
						$outbound["invoiceExpire"]	= $("#" + $namespace + "_invoiceExpire").val();
						$outbound["invoiceExpireReason"]	= $("#" + $namespace + "_invoiceExpireReason").val();
						
						
						JOB_UTILS.update($outbound);
						
						$("#" + $namespace + "_invoiceEdit").removeClass('fa-save');
						$("#" + $namespace + "_invoiceEdit").addClass('fa-pencil');
					}
				});
			},
			setInvoiceStyle: function($namespace, $optionList, $selectedValue) {
				var selectorName = "#" + $namespace + "_jobInvoiceForm select[name='" + $namespace + "_invoiceStyle']";

				$selectorName = "select[name='" + $namespace + "_invoiceStyle']";
				ANSI_UTILS.setOptionList($selectorName, $optionList, $selectedValue)

//				$.each($optionList, function(index, val) {
//					if ( val.abbrev == $selectedValue) {
//						ANSI_UTILS.setTextValue($namespace, "invoiceStyle", val.display);
//					}
//				});				
			},
			setInvoiceGrouping: function($namespace, $optionList, $selectedValue) {
//				var selectorName = "#" + $namespace + "_jobInvoiceForm select[name='" + $namespace + "_invoiceGrouping']";
				var $selectorName = "select[name='" + $namespace + "_invoiceGrouping']";
				ANSI_UTILS.setOptionList($selectorName, $optionList, $selectedValue)

//				$.each($optionList, function(index, val) {
//					if ( val.abbrev == $selectedValue) {
//						ANSI_UTILS.setTextValue($namespace, "invoiceGrouping", val.display);
//					}
//				});
			},
			setInvoiceTerms: function($namespace, $optionList, $selectedValue) {
//				var selectorName = "#" + $namespace + "_jobInvoiceForm select[name='" + $namespace + "_invoiceTerms']";
				var $selectorName = "select[name='" + $namespace + "_invoiceTerms']";
				ANSI_UTILS.setOptionList($selectorName, $optionList, $selectedValue)

//				$.each($optionList, function(index, val) {
//					if ( val.abbrev == $selectedValue) {
//						ANSI_UTILS.setTextValue($namespace, "invoiceTerms", val.display);
//					}
//				});
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
					$("#" + $namespace + "_divisionId").selectmenu({ width : '150px', maxHeight: '400 !important', style: 'dropdown'});
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
					$("#" + $namespace + "_divisionId").val($jobDetail.divisionId);
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

			var $select = $(selectorName);
			//$('option', $select).remove();

			$select.append(new Option("",""));
			$.each($divisionList, function(index, val) {
			    $select.append(new Option(val.divisionCode, val.divisionId));
			});
			
			//$select.selectmenu();
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
			
			$selectorName = "#" + $namespace + "_proposalEdit";
			$($selectorName).click(function($event) {
				$event.preventDefault();
				
				if($("#" + $namespace + "_proposalEdit").hasClass( "fa-pencil" )){
					$("#" + $namespace + "_jobFrequency").selectmenu( "option", "disabled", false );
					$("#" + $namespace + "_jobNbr").prop('disabled', false);
					$("#" + $namespace + "_ppc").prop('disabled', false);
					$("#" + $namespace + "_serviceDescription").prop('disabled', false);
					
					$("#" + $namespace + "_proposalEdit").removeClass('fa-pencil');
					$("#" + $namespace + "_proposalEdit").addClass('fa-save');
				} else if($("#" + $namespace + "_proposalEdit").hasClass( "fa-save" )){
					$("#" + $namespace + "_jobFrequency").selectmenu( "option", "disabled", true );
					$("#" + $namespace + "_jobNbr").prop('disabled', true);
					$("#" + $namespace + "_ppc").prop('disabled', true);
					$("#" + $namespace + "_serviceDescription").prop('disabled', true);
					
					$outbound = {};
					
					$outbound["jobFrequency"]	= $("#" + $namespace + "_jobFrequency").val();
					$outbound["jobNbr"]	= $("#" + $namespace + "_jobNbr").val();
					$outbound["ppc"]	= $("#" + $namespace + "_ppc").val();
					$outbound["serviceDescription"]	= $("#" + $namespace + "_serviceDescription").val();
					
					JOB_UTILS.update($outbound);
					
					$("#" + $namespace + "_proposalEdit").removeClass('fa-save');
					$("#" + $namespace + "_proposalEdit").addClass('fa-pencil');
				}
			});
			
		},
//		setJobFrequency: function($namespace, $optionList, $selectedValue) {
//			var selectorName = "#" + $namespace + "_jobDescriptionForm select[name='" + $namespace + "_jobFrequency']";
//			selectorName = "select[name='" + $namespace + "_jobFrequency']";
//			
//			var $select = $(selectorName);
//			$('option', $select).remove();
//
//			$select.append(new Option("",""));
//			$.each($optionList, function(index, val) {
//			    $select.append(new Option(val.display, val.abbrev));
//			});
//			
//			if ( $selectedValue != null ) {
//				$select.val($selectedValue);
//			}
//			//$select.selectmenu();
//		}
		setJobFrequency: function($namespace, $optionList, $selectedValue) {
//			var selectorName = "#" + $namespace + "_jobInvoiceForm select[name='" + $namespace + "_invoiceGrouping']";
			var $selectorName = "select[name='" + $namespace + "_jobFrequency']";
			ANSI_UTILS.setOptionList($selectorName, $optionList, $selectedValue)
		},

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
				var $jobId = $jobDetail.jobId;
				location.href="ticketLookup.html?jobId=" + $jobId;
			});
//			console.log("Job Scheduling JobDetail");
//			console.log($jobDetail);
			var $repeatSelectorName = "#" + $namespace + "_" + "annualRepeat";
			$($repeatSelectorName).click(function($event) {
				var $jobId = $jobDetail.jobId;
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



