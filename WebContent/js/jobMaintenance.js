$( document ).ready(function() {
	var $divisionLookup = {};
	;JOB_DATA = {}
	;QUOTE_DATA = {}
	;Page = null;
	$jobClicked = 0;
	;JOB_UTILS = {
		pageInit:function($jobId) {

			ANSI_UTILS.getOptionList('COUNTRY,INVOICE_GROUPING,INVOICE_STYLE,INVOICE_TERM',JOB_UTILS.populateOptionList);
			//console.log($optionData);
			
			
		},
		
		
		populateOptionList : function ($optionData) {	
			
			JOB_DATA.jobFrequencyList = $optionData.jobFrequency;
			JOB_DATA.jobStatusList = $optionData.jobStatus;
			JOB_DATA.invoiceTermList = $optionData.invoiceTerm;
			JOB_DATA.invoiceGroupingList = $optionData.invoiceGrouping;
			
			JOB_DATA.invoiceStyleList = $optionData.invoiceStyle;
			JOB_DATA.countryList = $optionData.country;
			JOB_DATA.divisionList = ANSI_UTILS.getDivisionList();
			JOB_DATA.buildingTypeList = ANSI_UTILS.makeBuildingTypeList();
			
			
			JOB_DATA.jobId = $jobId;
			
			JOB_UTILS.panelLoad("0",$jobId);
	   	},
	   	
	   	
		panelLoad:function($namespace,$jobId) {			
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
				
				if($jobDetail != null){
					console.log("setting Ids " + $jobDetail.jobContactId);
					QUOTEUTILS.setjobContactId($jobDetail.jobContactId);
					QUOTEUTILS.setsiteContactId($jobDetail.siteContact);
					QUOTEUTILS.setcontractContactId($jobDetail.contractContactId);
					QUOTEUTILS.setbillingContactId($jobDetail.billingContactId);
					console.log("Check Ids " + QUOTEUTILS.getjobContactId());
				}
			}
			
			var jqxhr1 = $.ajax({
				type: 'GET',
				url: 'quotePanel.html',
				data: {"namespace":'0',"page":'JOB'},
				success: function($data) {
					$('#jobPanelHolder > tbody:last-child').replaceWith($data);
					//JOBPANEL.init($namespace+"_jobPanel", JOB_DATA.divisionList, "activateModal", $jobDetail);
//					JOBPROPOSAL.init($namespace+"_jobProposal", JOB_DATA.jobFrequencyList, $jobDetail);
//					JOBACTIVATION.init($namespace+"_jobActivation", JOB_DATA.buildingTypeList, $jobDetail);
//					JOBDATES.init($namespace+"_jobDates", $quoteDetail, $jobDetail);
//					JOBSCHEDULE.init($namespace+"_jobSchedule", $jobDetail, $lastRun, $nextDue, $lastCreated)
//					JOBINVOICE.init($namespace+"_jobInvoice", JOB_DATA.invoiceStyleList, JOB_DATA.invoiceGroupingList, JOB_DATA.invoiceTermList, $jobDetail);
//					JOBAUDIT.init($namespace+"_jobAudit", $jobDetail);
					ADDRESSPANEL.init($namespace+"_jobSite", JOB_DATA.countryList);
					ADDRESSPANEL.init($namespace+"_billTo", JOB_DATA.countryList);
					
					
					
					if ( $jobDetail.quoteId != '' ) {
						var $quoteDetails = QUOTEUTILS.getQuoteDetail($jobDetail.quoteId);
						var $quoteData = $quoteDetails.quote;
						
						//console.log($quoteData);
						if($quoteDetails.billTo != null){
							//ADDRESSPANEL.setAddress("billTo",$quoteDetail.billTo);
							//ADDRESS_UTILS.getAddress($quoteDetail.billTo.addressId, "#billTo");
							$billToId = $quoteDetails.billTo.addressId;
							ADDRESS_UTILS.populateAddress("#billTo", $quoteDetails.billTo);
							 $( "input[name='billTo_name']" ).val($quoteDetails.billTo.name);
						}
						
						if($quoteDetails.jobSite != null){
							//ADDRESSPANEL.setAddress("jobSite",$quoteDetail.jobSite);
							//ADDRESS_UTILS.getAddress($quoteDetail.jobSite.addressId, "#jobSite");
							$jobSiteId = $quoteDetails.jobSite.addressId;
							ADDRESS_UTILS.populateAddress("#jobSite", $quoteDetails.jobSite);
							$( "input[name='jobSite_name']" ).val($quoteDetails.jobSite.name);
						}
					}
					
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
					if($jobDetail.serviceDescription != null){
						$("#"+$namespace+"_jobDescHead").html($jobDetail.serviceDescription.substring(0, 50));
					}
					

					$("#"+$namespace+"_jobPanel_jobLink").attr("href", "jobMaintenance.html?id="+$jobId);
					$("#"+$namespace+"_jobPanel_jobLink").text($jobId);
					
					console.log("Quote ID For Link: "+$jobDetail.quoteId);
					$("#"+$namespace+"_jobPanel_quoteLink").attr("href", "quoteMaintenance.html?id="+$jobDetail.quoteId);
					$("#"+$namespace+"_jobPanel_quoteLink").text($jobDetail.quoteId);
					//$(".addressTable").remove();
					
//					if($jobDetail.status != "N"){
//						$("#"+$namespace+"_jobProposal_proposalEdit").hide();
//					}

					
					$pageURL = window.location.href;
					if($pageURL.indexOf("job")>0){
						$("#"+$namespace+"_jobPanel_jobLinkSpan").hide();
					}
					if($pageURL.indexOf("quote")>0){
						$("#"+$namespace+"_jobPanel_quoteLinkSpan").hide();
					}

					
					if($jobDetail.canReschedule){
						$("#"+$namespace+"_jobPanel_scheduleJobButton").show();
					} else {
						$("#"+$namespace+"_jobPanel_scheduleJobButton").hide();
					}
					
					if($jobDetail.canActivate){
						$("#"+$namespace+"_jobPanel_activateJobButton").show();
					} else {
						$("#"+$namespace+"_jobPanel_activateJobButton").hide();
					}
					
					if($jobDetail.canCancel){
						$("#"+$namespace+"_jobPanel_cancelJobButton").show();
					} else {
						$("#"+$namespace+"_jobPanel_cancelJobButton").hide();
					}
					
					if($jobDetail.canDelete){
						$("#"+$namespace+"_jobPanel_deleteJobButton").show();
					} else {
						$("#"+$namespace+"_jobPanel_deleteJobButton").hide();
					}
					
					
					

					if($quoteData != null){
//						console.log($quoteData);
//						if($quoteDetails.billTo != null){
//							ADDRESSPANEL.setAddress("0_billTo",$quoteDetails.billTo);
//						}
//						if($quoteDetails.jobSite != null){
//							ADDRESSPANEL.setAddress("0_jobSite",$quoteDetails.jobSite);
//						}
						$jobContacts = JOB_UTILS.panelLoadQuote(0, $jobId, 0, $quoteData);
						QUOTEUTILS.addressActions();
						if($jobContacts.jobContactId != null){
							$jobContactId = $jobContacts.jobContactId;
							QUOTEUTILS.setjobContactId($jobContactId);
							var data = QUOTEUTILS.getContact($jobContactId);
					    	$("input[name='jobSite_jobContactName']").val(data.value);
					    	$("span[name='jobSite_jobContactInfo']").html(QUOTEUTILS.processContact(data));
						}
						if($jobContacts.siteContact != null){
							$siteContactId = $jobContacts.siteContact;
							QUOTEUTILS.setsiteContactId($siteContactId);
							var data = QUOTEUTILS.getContact($siteContactId);
							$("input[name='jobSite_siteContactName']").val(data.value);
					    	$("span[name='jobSite_siteContactInfo']").html(QUOTEUTILS.processContact(data));
						}
						if($jobContacts.contractContactId != null){
							$contractContactId = $jobContacts.contractContactId;
							QUOTEUTILS.setcontractContactId($contractContactId);
							var data = QUOTEUTILS.getContact($contractContactId);
							$("input[name='billTo_contractContactName']").val(data.value);
					    	$("span[name='billTo_contractContactInfo']").html(QUOTEUTILS.processContact(data));
						}
						if($jobContacts.billingContactId != null){
							$billingContactId = $jobContacts.billingContactId;
							QUOTEUTILS.setbillingContactId($billingContactId);
							console.log("Just set $billingContactId: "+ $billingContactId);
							var data = QUOTEUTILS.getContact($billingContactId);
							//console.log("check")
							$("input[name='billTo_billingContactName']").val(data.value);
					    	$("span[name='billTo_billingContactInfo']").html(QUOTEUTILS.processContact(data));
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
		
		populatePanelLoadQuoteList : function ($optionData) {

			JOB_DATA.jobFrequencyList = $optionData.jobFrequency;
			JOB_DATA.jobStatusList = $optionData.jobStatus;
			JOB_DATA.invoiceTermList = $optionData.invoiceTerm;
			JOB_DATA.invoiceGroupingList = $optionData.invoiceGrouping;
			JOB_DATA.invoiceStyleList = $optionData.invoiceStyle;

			JOB_DATA.divisionList = ANSI_UTILS.getDivisionList();
			JOB_DATA.buildingTypeList = ANSI_UTILS.makeBuildingTypeList();
			
		},
		
		panelLoadQuote:function($namespace, $jobId, $index, $quoteData) {
			var $page = "JOB";
			
			if($jobId == null){
				$page = "QUOTE";
			}
			if($index == 0){
				ANSI_UTILS.getOptionList('JOB_FREQUENCY,JOB_STATUS,INVOICE_TERM,INVOICE_GROUPING,INVOICE_STYLE',JOB_UTILS.populatePanelLoadQuoteList);
				
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
				//$page = 'JOB';
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
					$jobContactId = $jobDetail.jobContactId;
					$siteContactId = $jobDetail.siteContact;
					$contractContactId = $jobDetail.contractContactId;
					$billingContactId = $jobDetail.billingContactId;
				}
			}
			
			
		
			
			
			var jqxhr1 = $.ajax({
				type: 'GET',
				url: 'quotePanel.html',
				data: {"namespace":$namespace,"page":$page},
				success: function($data) {
					$("#loadingJobsDiv").hide();
					$('#accordian').append("<h3 id="+$namespace+"_jobHeader><span id='"+$namespace+"_jobStatusHead'></span>&nbsp;&nbsp;Job: <span id='"+$namespace+"_jobIdHead'></span>&nbsp;&nbsp;Job #: <span id='"+$namespace+"_jobNumberHead'></span>&nbsp;&nbsp;PPC: <span id='"+$namespace+"_jobPPCHead'></span>&nbsp;&nbsp; Frequency: <span id='"+$namespace+"_jobFreqHead'></span>&nbsp;&nbsp; Desc: <span id='"+$namespace+"_jobDescHead'></span><i id='"+$namespace+"_jobSaveHead' class='fa fa-floppy-o saveIcon grey' aria-hidden='true'></i></h3>");
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
					$("#" + $namespace + "_jobActivation_nbrFloors").spinner( "option", "disabled", true );
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
					if($jobDetail.serviceDescription != null){
						$("#"+$namespace+"_jobDescHead").html($jobDetail.serviceDescription.substring(0, 50));
					}
					
					
					$("#"+$namespace+"_jobPanel_jobLink").attr("href", "jobMaintenance.html?id="+$jobId);
					$("#"+$namespace+"_jobPanel_jobLink").text($jobId);
					

					$("#"+$namespace+"_jobPanel_quoteLink").attr("href", "quoteMaintenance.html?id="+$jobDetail.quoteId);
					$("#"+$namespace+"_jobPanel_quoteLink").text($jobDetail.quoteId);

					$pageURL = window.location.href;
					if($pageURL.indexOf("job")>0){
						$("#"+$namespace+"_jobPanel_jobLinkSpan").hide();
					}
					if($pageURL.indexOf("quote")>0){
						$("#"+$namespace+"_jobPanel_quoteLinkSpan").hide();
					}
					
					//$(".addressTable").remove();

					
					if($jobDetail.canReschedule){
						$("#"+$namespace+"_jobPanel_scheduleJobButton").show();
					} else {
						$("#"+$namespace+"_jobPanel_scheduleJobButton").hide();
					}
					
					if($jobDetail.canActivate){
						$("#"+$namespace+"_jobPanel_activateJobButton").show();
					} else {
						$("#"+$namespace+"_jobPanel_activateJobButton").hide();
					}
					
					if($jobDetail.canCancel){
						$("#"+$namespace+"_jobPanel_cancelJobButton").show();
					} else {
						$("#"+$namespace+"_jobPanel_cancelJobButton").hide();
					}
					
					if($jobDetail.canDelete){
						$("#"+$namespace+"_jobPanel_deleteJobButton").show();
					} else {
						$("#"+$namespace+"_jobPanel_deleteJobButton").hide();
					}
					
//					if($jobDetail.status != "N"){
//						$("#"+$namespace+"_jobProposal_proposalEdit").hide();
//					}
					
					$.each($('input'), function () {
				        $(this).css("height","20px");
				        $(this).css("max-height", "20px");
				    });
					//$('.jobSave').on('click', function($clickevent) {
						//console.log($namespace);
						//JOB_UTILS.addJob($(this).attr("rownum"),$jobDetail.quoteId, $namespace);
		           // });
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
		deleteJob: function($namespace,$rn){
			var $outbound = {};
			var $pre = "#"+$rn;
			$outbound["action"]	= 'DELETE_JOB';
			var $url = "job/"+$("#"+$rn+"_jobPanel_jobId").val();
			console.log($url);
			var jqxhr3 = $.ajax({
				type: 'POST',
				url: $url,
				data: JSON.stringify($outbound),
				statusCode: {
					200: function($data) {
						$(".inputIsInvalid").removeClass("fa-ban");
						$(".inputIsInvalid").removeClass("inputIsInvalid");
						console.log("Return 200: "+$rn);
						console.log($data);
						
						if ( $data.responseHeader.responseCode == 'EDIT_FAILURE') {
							
							
							$("#"+$rn+"_jobSaveHead").removeClass("grey");
							$("#"+$rn+"_jobSaveHead").removeClass("error");
							$("#"+$rn+"_jobSaveHead").removeClass("green");
							$("#"+$rn+"_jobSaveHead").addClass("error");
							
							$errorMessage ="<p>Job Delete Failed - Contact Support</p>";

							$( "#error-message" ).dialog( "option", "title", "Job Delete Error" );
							$( "#error-message" ).html($errorMessage);
							$( "#error-message" ).dialog("open");
							//alert("Edit Failure - missing data");
							console.log("Edit Fail");
						}
						if ( $data.responseHeader.responseCode == 'SUCCESS') {
							$("#"+$rn+"_jobHeader").remove();
							$("#"+$rn+"_jobDiv").remove();
						}
					},				
					403: function($data) {
						ANSI_UTILS.setTextValue($namespace, "panelMessage", "Function Not Permitted");
						JOB_UTILS.fadeMessage($namespace, "panelMessage")
						$($pre+"_jobSaveHead").removeClass("grey");
						$($pre+"_jobSaveHead").removeClass("error");
						$($pre+"_jobSaveHead").removeClass("green");
						$($pre+"_jobSaveHead").addClass("error");
					}, 
					404: function($data) {
						ANSI_UTILS.setTextValue($namespace, "panelMessage", "Resource Not Available");
						JOB_UTILS.fadeMessage($namespace, "panelMessage")
						$($pre+"_jobSaveHead").removeClass("grey");
						$($pre+"_jobSaveHead").removeClass("error");
						$($pre+"_jobSaveHead").removeClass("green");
						$($pre+"_jobSaveHead").addClass("error");
					}, 
					500: function($data) {
						ANSI_UTILS.setTextValue($namespace, "panelMessage", "System Error -- Contact Support");
						//JOB_UTILS.fadeMessage($namespace, "panelMessage")
						$($pre+"_jobSaveHead").removeClass("grey");
						$($pre+"_jobSaveHead").removeClass("error");
						$($pre+"_jobSaveHead").removeClass("green");
						$($pre+"_jobSaveHead").addClass("error");
					} 
				},
				dataType: 'json'
			});
		},
		changeFieldState: function($namespace,$disabledBoolean){

			//$("#" + $namespace + "_jobActivation_automanual").selectmenu( "option", "disabled", $disabledBoolean );
			$("#" + $namespace + "_jobActivation_automanual").prop('disabled', $disabledBoolean);
			//$("#" + $namespace + "_jobActivation_buildingType").selectmenu( "option", "disabled", $disabledBoolean );
			$("#" + $namespace + "_jobActivation_directLaborPct").prop('disabled', $disabledBoolean);
			$("#" + $namespace + "_jobActivation_directLaborBudget").prop('disabled', $disabledBoolean);
			$("#" + $namespace + "_jobActivation_nbrFloors").spinner( "option", "disabled", $disabledBoolean );
			$("#" + $namespace + "_jobActivation_equipment").prop('disabled', $disabledBoolean);
			$("#" + $namespace + "_jobActivation_washerNotes").prop('disabled', $disabledBoolean);
			$("#" + $namespace + "_jobActivation_omNotes").prop('disabled', $disabledBoolean);
			$("#" + $namespace + "_jobActivation_billingNotes").prop('disabled', $disabledBoolean);
			
			//$("#" + $namespace + "_jobInvoice_invoiceStyle").selectmenu( "option", "disabled", $disabledBoolean );
			$("#" + $namespace + "_jobInvoice_invoiceStyle").prop( "disabled", $disabledBoolean );
			$("#" + $namespace + "_jobInvoice_invoiceBatch").prop('disabled', $disabledBoolean);
			$("#" + $namespace + "_jobInvoice_invoiceTaxExempt").prop('disabled', true);
			//$("#" + $namespace + "_jobInvoice_invoiceGrouping").selectmenu( "option", "disabled", $disabledBoolean );
			$("#" + $namespace + "_jobInvoice_invoiceGrouping").prop( "disabled", $disabledBoolean );
			//$("#" + $namespace + "_jobInvoice_invoiceTerms").selectmenu( "option", "disabled", $disabledBoolean );
			$("#" + $namespace + "_jobInvoice_invoiceTerms").prop( "disabled", $disabledBoolean );
			$("#" + $namespace + "_jobInvoice_invoicePO").prop('disabled', $disabledBoolean);
			$("#" + $namespace + "_jobInvoice_invoiceOurVendorNbr").prop('disabled', $disabledBoolean);
			$("#" + $namespace + "_jobInvoice_invoiceExpire").prop('disabled', $disabledBoolean);
			$("#" + $namespace + "_jobInvoice_invoiceExpireReason").prop('disabled', $disabledBoolean);
			
//			$("#" + $namespace + "_jobProposal_jobFrequency").selectmenu( "option", "disabled", $disabledBoolean );
			$("#" + $namespace + "_jobProposal_jobFrequency").prop( "disabled", $disabledBoolean );
			$("#" + $namespace + "_jobProposal_jobNbr").prop('disabled', $disabledBoolean);
			$("#" + $namespace + "_jobProposal_ppc").prop('disabled', $disabledBoolean);
			$("#" + $namespace + "_jobProposal_serviceDescription").prop('disabled', $disabledBoolean);
		},
		fadeMessage:function($namespace, $id, $duration) {
			var $selectorName = "#" + $namespace + "_" + $id;
			if ( $duration == null ) {
				$duration=6000;
			}
			$($selectorName).fadeOut($duration);
		},
		
		addJob: function($rn,$quoteId, $namespace,$exit) {
			var $url = "job/add";
						
//			console.log("$jobContactId: "+$jobContactId);
//			console.log("$siteContactId: "+$siteContactId);
//			console.log("$contractContactId: "+$contractContactId);
//			console.log("$billingContactId: "+$billingContactId);
			
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
    		
    		console.log("Action: "+$outbound["action"]+" Row: "+$rn);
    		$outbound["activationDate"]				= $($pre+"_jobDates_activationDate").html();
    		$outbound["billingContactId"]			= QUOTEUTILS.getbillingContactId();
    		$outbound["billingNotes"]				= $($pre+"_jobActivation_billingNotes").val();
    		$outbound["budget"]						= $($pre+"_jobActivation_directLaborBudget").val();
//    		$outbound["buildingType"]				= $($pre+"_jobActivation_buildingType").val();//0_jobActivation_buildingType
    		$outbound["buildingType"]				= $("#buildingType").val();
    		$outbound["cancelDate"]					= $($pre+"_jobDates_cancelDate").html();
    		$outbound["cancelReason"]				= $($pre+"_jobDates_cancelReason").html();
    		$outbound["contractContactId"]			= QUOTEUTILS.getcontractContactId();
    		$outbound["directLaborPct"]				= $($pre+"_jobActivation_directLaborPct").val();
    		$outbound["divisionId"]					= QUOTEUTILS.getDivision();
    		$outbound["equipment"]					= $($pre+"_jobActivation_equipment").val();
    		$outbound["expirationDate"]				= $($pre+"_jobInvoice_invoiceExpire").val();
    		$outbound["expirationReason"]			= $($pre+"_jobInvoice_invoiceExpireReason").val();
    		$outbound["floors"]						= $($pre+"_jobActivation_nbrFloors").val();
    		$outbound["invoiceBatch"]				= 0;
    		if($($pre+"_jobInvoice_invoiceBatch").prop("checked")){
    			$outbound["invoiceBatch"]				= 1;
    		}
    		

    		$outbound["invoiceGrouping"]			= $($pre+"_jobInvoice_invoiceGrouping").val();
    		$outbound["invoiceStyle"]				= $($pre+"_jobInvoice_invoiceStyle").val();
    		$outbound["invoiceTerms"]				= $($pre+"_jobInvoice_invoiceTerms").val();
    		$outbound["jobContactId"]				= QUOTEUTILS.getjobContactId();
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
    		$outbound["quoteId"]					= QUOTEUTILS.getQuoteId();
    		    		
    		$outbound["repeatScheduleAnnually"]		= 0;
    		if($($pre+"_jobSchedule_annualRepeat").prop("checked")){
    			$outbound["repeatScheduleAnnually"]				= 1;
    		}
    		
    		$outbound["requestSpecialScheduling"]	= null;
    		if($($pre+"_jobActivation_automanual").val() == "manual"){
    			$outbound["requestSpecialScheduling"]	= 1;
    		} else if($($pre+"_jobActivation_automanual").val() == "auto") {
    			$outbound["requestSpecialScheduling"]	= 0;
    		}
    		
    		$outbound["serviceDescription"]			= $($pre+"_jobProposal_serviceDescription").val();
    		$outbound["siteContact"]				= QUOTEUTILS.getsiteContact();
    		$outbound["startDate"]					= $($pre+"_jobDates_startDate").html();
    		
    		if ($($pre+"_jobPanel_jobId").val() == false) {
    			$outbound["status"]						= $($pre+"_jobPanel_jobStatus").val();
    		}
    		
    		$outbound["taxExempt"]					= 0;
    		
    		if($($pre+"_jobInvoice_invoiceTaxExempt").prop("checked")){
    			$outbound["taxExempt"]				= 1;
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
						console.log("Return 200: "+$rn);
						console.log($data);
						
						if ( $data.responseHeader.responseCode == 'EDIT_FAILURE') {
							
							
							$("#"+$rn+"_jobSaveHead").removeClass("grey");
							$("#"+$rn+"_jobSaveHead").removeClass("error");
							$("#"+$rn+"_jobSaveHead").removeClass("green");
							$("#"+$rn+"_jobSaveHead").addClass("error");
							
							$errorMessage ="<p>";
							$.each($data.data.webMessages, function(key, messageList) {
								var identifier = "#"+$rn+"_jobDiv ."+key+"Err";
								$errorMessage += "<b>"+key+"</b>: "+messageList[0]+"<br/>";
								$(identifier).addClass("fa-ban");
								$(identifier).addClass("inputIsInvalid");
							});
							$errorMessage +="</p>";
							$( "#error-message" ).dialog( "option", "title", "Job Save Error" );
							$( "#error-message" ).html($errorMessage);
							$( "#error-message" ).dialog("open");
							//alert("Edit Failure - missing data");
							console.log("Edit Fail");
							return false;
						}
						if ( $data.responseHeader.responseCode == 'SUCCESS') {
							console.log("Success!");
							console.log($data.data.job);
							
							$("#"+$rn+"_jobSaveHead").removeClass("grey");
							$("#"+$rn+"_jobSaveHead").removeClass("error");
							$("#"+$rn+"_jobSaveHead").removeClass("green");
							$("#"+$rn+"_jobSaveHead").addClass("green");
							
							$("#"+$rn+"_jobPanel_jobId").val($data.data.job.jobId);
							$("#"+$rn+"_jobPanel_jobStatus").val($data.data.job.status);
							$userData = ANSI_UTILS.getUser($data.data.job.addedBy);
							if($data.data.job.jobId != null){
								$("#"+$rn+"_jobIdHead").html($data.data.job.jobId);
							}
							if($data.data.job.status != null){
								$("#"+$rn+"_jobStatusHead").html($data.data.job.status);					
							}
							if($data.data.job.jobNbr != null){
								$("#"+$rn+"_jobNumberHead").html($data.data.job.jobNbr);	
							}
							if($data.data.job.pricePerCleaning != null){
								$("#"+$rn+"_jobPPCHead").html($data.data.job.pricePerCleaning);	
							}
							if($data.data.job.jobFrequency != null){
								$("#"+$rn+"_jobFreqHead").html($data.data.job.jobFrequency);
							}
							if($data.data.job.serviceDescription != null){
								$("#"+$rn+"_jobDescHead").html($data.data.job.serviceDescription.substring(0, 50));
							}
							$("#"+$rn+"_jobPanel_divisionId").text(QUOTEUTILS.getDivision());
							
							if($data.data.job.canReschedule){
								$("#"+$rn+"_jobPanel_scheduleJobButton").show();
							} else {
								$("#"+$rn+"_jobPanel_scheduleJobButton").hide();
							}
							
							if($data.data.job.canActivate){
								$("#"+$rn+"_jobPanel_activateJobButton").show();
							} else {
								$("#"+$rn+"_jobPanel_activateJobButton").hide();
							}
							
							if($data.data.job.canCancel){
								$("#"+$rn+"_jobPanel_cancelJobButton").show();
							} else {
								$("#"+$rn+"_jobPanel_cancelJobButton").hide();
							}
							
							if($data.data.job.canDelete){
								$("#"+$namespace+"_jobPanel_deleteJobButton").show();
							} else {
								$("#"+$namespace+"_jobPanel_deleteJobButton").hide();
							}
							
							$("#"+$rn+"_jobPanel_jobLink").attr("href", "jobMaintenance.html?id="+$data.data.job.jobId);
							$("#"+$rn+"_jobPanel_jobLink").text($data.data.job.jobId);
							$("#"+$rn+"_jobSchedule_showTicketList").show();
							
							JOB_UTILS.changeFieldState($namespace,true);
							
//************* FINISH AUDIT
							//$addedBy = ANSI_UTILS.getUser($data.data.job.addedBy);
							//$updatedBy = ANSI_UTILS.getUser($data.data.job.updatedBy);
							JOBAUDIT.init($rn+"_jobAudit", $data.data.job);
							//JOBAUDIT.init($rn,$data.data.job);
							ANSI_UTILS.setTextValue($namespace, "panelMessage", "Update Successful");
							JOB_UTILS.fadeMessage($namespace, "panelMessage")
							QUOTEUTILS.incrementSave();
							if($exit){
								if(QUOTEUTILS.getSaveCounter() == QUOTEUTILS.getCurrentRow()){
									location.href="quoteLookup.html";
								}
							}
							return true;
							//JOB_UTILS.panelLoad($jobId);
						}
					},				
					403: function($data) {
						ANSI_UTILS.setTextValue($namespace, "panelMessage", "Function Not Permitted");
						JOB_UTILS.fadeMessage($namespace, "panelMessage")
						$($pre+"_jobSaveHead").removeClass("grey");
						$($pre+"_jobSaveHead").removeClass("error");
						$($pre+"_jobSaveHead").removeClass("green");
						$($pre+"_jobSaveHead").addClass("error");
					}, 
					404: function($data) {
						ANSI_UTILS.setTextValue($namespace, "panelMessage", "Resource Not Available");
						JOB_UTILS.fadeMessage($namespace, "panelMessage")
						$($pre+"_jobSaveHead").removeClass("grey");
						$($pre+"_jobSaveHead").removeClass("error");
						$($pre+"_jobSaveHead").removeClass("green");
						$($pre+"_jobSaveHead").addClass("error");
					}, 
					500: function($data) {
						ANSI_UTILS.setTextValue($namespace, "panelMessage", "System Error -- Contact Support");
						//JOB_UTILS.fadeMessage($namespace, "panelMessage")
						$($pre+"_jobSaveHead").removeClass("grey");
						$($pre+"_jobSaveHead").removeClass("error");
						$($pre+"_jobSaveHead").removeClass("green");
						$($pre+"_jobSaveHead").addClass("error");
					} 
				},
				dataType: 'json'
			});
		},
		update: function($id,$outbound){
			console.log("Update Outbound: ");
    		console.log($outbound);

    		$url = "job/"+$id;
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
				console.log("Job Activation Config Namespace: "+$namespace);
				
				if ( $jobDetail == null ) {
					JOBACTIVATION.setBuildingType($namespace, QUOTEUTILS.setSelectMenu("buildingType",ANSI_UTILS.getCodes("job","building_type")), $jobDetail.buildingType);
				} else {
					JOBACTIVATION.setBuildingType($namespace, $buildingTypeList, $jobDetail.buildingType);
					if($jobDetail.directLaborPct < 1 && $jobDetail.directLaborPct != 0){
						$jobDetail.directLaborPct = $jobDetail.directLaborPct*100;
					}
					ANSI_UTILS.setFieldValue($namespace, "directLaborPct", $jobDetail.directLaborPct);
					ANSI_UTILS.setFieldValue($namespace, "directLaborBudget", $jobDetail.budget);
					ANSI_UTILS.setFieldValue($namespace, "nbrFloors", $jobDetail.floors);
					ANSI_UTILS.setFieldValue($namespace, "equipment", $jobDetail.equipment);
					ANSI_UTILS.setFieldValue($namespace, "washerNotes", $jobDetail.washerNotes);
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
				
					var $select = $("select[name='"+$namespace+"_automanual']");
					$select.append(new Option("",""));
					$select.append(new Option("Auto","auto"));
					$select.append(new Option("Manual","manual"));
					//$select.selectmenu({ width : '75px', maxHeight: '400 !important', style: 'dropdown'});
					
					if($jobDetail.requestSpecialScheduling == 0){
						$select.val("auto");
					} else if($jobDetail.requestSpecialScheduling == 1){
						$select.val("manual");
					}
					//$select.selectmenu("refresh");
					
					$selectorName = "#" + $namespace + "_activationEdit";
					$($selectorName).click(function($event) {
					$event.preventDefault();
					$id = $("#"+$namespace.substring(0,$namespace.indexOf("_"))+"_jobPanel_jobId").val();
					console.log("#"+$namespace.substring(0,$namespace.indexOf("_"))+"_jobPanel_jobId");
					if($("#" + $namespace + "_activationEdit").hasClass( "fa-pencil-alt" )){
						console.log("Clicked: Pencil");
//						$("#" + $namespace + "_automanual").selectmenu( "option", "disabled", false );
						$("#" + $namespace + "_automanual").prop( "disabled", false );
//						$("#" + $namespace + "_buildingType").selectmenu( "option", "disabled", false );
						$("#" + $namespace + "_directLaborPct").prop('disabled', false);
						$("#" + $namespace + "_directLaborBudget").prop('disabled', false);
						$("#" + $namespace + "_nbrFloors").spinner( "option", "disabled", false );
						$("#" + $namespace + "_equipment").prop('disabled', false);
						$("#" + $namespace + "_washerNotes").prop('disabled', false);
						$("#" + $namespace + "_omNotes").prop('disabled', false);
						$("#" + $namespace + "_billingNotes").prop('disabled', false);
						
						$("#" + $namespace + "tooltiptext").text("Save");
						
						$("#" + $namespace + "_activationEdit").removeClass('fa-pencil-alt');
						$("#" + $namespace + "_activationEdit").addClass('fa-save');
					} else if($("#" + $namespace + "_activationEdit").hasClass( "fa-save" )){
						console.log("Clicked: Save");
						//$("#" + $namespace + "_automanual").selectmenu( "option", "disabled", true );
						$("#" + $namespace + "_automanual").prop( "disabled", true );
//						$("#" + $namespace + "_buildingType").selectmenu( "option", "disabled", true );
						$("#" + $namespace + "_directLaborPct").prop('disabled', true);
						$("#" + $namespace + "_directLaborBudget").prop('disabled', true);
						$("#" + $namespace + "_nbrFloors").spinner( "option", "disabled", true );
						$("#" + $namespace + "_equipment").prop('disabled', true);
						$("#" + $namespace + "_washerNotes").prop('disabled', true);
						$("#" + $namespace + "_omNotes").prop('disabled', true);
						$("#" + $namespace + "_billingNotes").prop('disabled', true);
						
						$outbound = {};
						
						
						JOB_UTILS.addJob($namespace.substring(0,$namespace.indexOf("_")),$id,$namespace,false);
						
						$("#" + $namespace + "tooltiptext").text("Edit");
		        	
						$("#" + $namespace + "_activationEdit").removeClass('fa-save');
						$("#" + $namespace + "_activationEdit").addClass('fa-pencil-alt');
					}
				});
				
				},
				
				
				setBuildingType: function ($namespace, $optionList, $selectedValue) {

//					var selectorName = "select[name='" + $namespace + "_buildingType']";
					var selectorName = "select[name='buildingType']";
					var $select = $(selectorName);
					
					$('option', $select).remove();

					$select.append(new Option("",""));
					$.each($optionList, function(index, val) {
					    $select.append(new Option(val.displayValue, val.value));
					});
					
					if ( $selectedValue != null ) {
						$select.val($selectedValue);
					}
					//$select.selectmenu({ width : '175px', maxHeight: '400 !important', style: 'dropdown'});


				}
				
				
			}
	
	
	
	
	
	;JOBAUDIT = {
		init: function($namespace, $jobDetail) {
			if ( $jobDetail != null ) {
				var $createdBy = $jobDetail.addedFirstName + " " + $jobDetail.addedLastName + " (" + $jobDetail.addedEmail + ")";
				var $updatedBy = $jobDetail.updatedFirstName + " " + $jobDetail.updatedLastName + " (" + $jobDetail.updatedEmail + ")";				
				ANSI_UTILS.setTextValue($namespace, "createdBy", $createdBy);
				ANSI_UTILS.setTextValue($namespace, "createdDate", $jobDetail.addedDate);
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
					$id = $("#"+$namespace.substring(0,$namespace.indexOf("_"))+"_jobPanel_jobId").val();
					if($("#" + $namespace + "_invoiceEdit").hasClass( "fa-pencil-alt" )){
						//$("#" + $namespace + "_invoiceStyle").selectmenu( "option", "disabled", false );
						$("#" + $namespace + "_invoiceStyle").prop( "disabled", false );
						$("#" + $namespace + "_invoiceBatch").prop('disabled', false);
						$("#" + $namespace + "_invoiceTaxExempt").prop('disabled', false);
						//$("#" + $namespace + "_invoiceGrouping").selectmenu( "option", "disabled", false );
						$("#" + $namespace + "_invoiceGrouping").prop( "disabled", false );
						//$("#" + $namespace + "_invoiceTerms").selectmenu( "option", "disabled", false );
						$("#" + $namespace + "_invoiceTerms").prop( "disabled", false );
						$("#" + $namespace + "_invoicePO").prop('disabled', false);
						$("#" + $namespace + "_invoiceOurVendorNbr").prop('disabled', false);
						$("#" + $namespace + "_invoiceExpire").prop('disabled', false);
						$("#" + $namespace + "_invoiceExpireReason").prop('disabled', false);
						
						$("#" + $namespace + "tooltiptext").text("Save");
						
						$("#" + $namespace + "_invoiceEdit").removeClass('fa-pencil-alt');
						$("#" + $namespace + "_invoiceEdit").addClass('fa-save');
					} else if($("#" + $namespace + "_invoiceEdit").hasClass( "fa-save" )){
						//$("#" + $namespace + "_invoiceStyle").selectmenu( "option", "disabled", true );
						$("#" + $namespace + "_invoiceStyle").prop( "disabled", true );
						$("#" + $namespace + "_invoiceBatch").prop('disabled', true);
						$("#" + $namespace + "_invoiceTaxExempt").prop('disabled', true);
						//$("#" + $namespace + "_invoiceGrouping").selectmenu( "option", "disabled", true );
						$("#" + $namespace + "_invoiceGrouping").prop( "disabled", true );
						//$("#" + $namespace + "_invoiceTerms").selectmenu( "option", "disabled", true );
						$("#" + $namespace + "_invoiceTerms").prop( "disabled", true );
						$("#" + $namespace + "_invoicePO").prop('disabled', true);
						$("#" + $namespace + "_invoiceOurVendorNbr").prop('disabled', true);
						$("#" + $namespace + "_invoiceExpire").prop('disabled', true);
						$("#" + $namespace + "_invoiceExpireReason").prop('disabled', true);
						
						$outbound = {};
						$outbound["action"]	= 'UPDATE_JOB';
						$outbound["invoiceStyle"]	= $("#" + $namespace + "_invoiceStyle").val();
						$outbound["invoiceBatch"]	= $("#" + $namespace + "_invoiceBatch").val();
						$outbound["invoiceTaxExempt"]	= $("#" + $namespace + "_invoiceTaxExempt").val();
						$outbound["invoiceGrouping"]	= $("#" + $namespace + "_invoiceGrouping").val();
						$outbound["invoiceTerms"]	= $("#" + $namespace + "_invoiceTerms").val();
						$outbound["invoicePO"]	= $("#" + $namespace + "_invoicePO").val();
						$outbound["invoiceOurVendorNbr"]	= $("#" + $namespace + "_invoiceOurVendorNbr").val();
						$outbound["invoiceExpire"]	= $("#" + $namespace + "_invoiceExpire").val();
						$outbound["invoiceExpireReason"]	= $("#" + $namespace + "_invoiceExpireReason").val();
						
						
						JOB_UTILS.addJob($namespace.substring(0,$namespace.indexOf("_")),$id,$namespace,false);
						
						$("#" + $namespace + "tooltiptext").text("Edit");
						
						$("#" + $namespace + "_invoiceEdit").removeClass('fa-save');
						$("#" + $namespace + "_invoiceEdit").addClass('fa-pencil-alt');
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
				
				$.each($divisionList, function($index, $division) {
					$divisionLookup[$division.divisionId]=$division.divisionCode;
				});
				if($("#" + $namespace + "_divisionId").is("select")){
					JOBPANEL.setDivisionList($namespace, $divisionList);
					//$("#" + $namespace + "_divisionId").selectmenu({ width : '150px', maxHeight: '400 !important', style: 'dropdown'});
				}
			}
			JOBPANEL.initActivateModal($namespace, $modalNamespace);
			JOBPANEL.initCancelModal($namespace, $modalNamespace);
			JOBPANEL.initScheduleModal($namespace, $modalNamespace);
			JOBPANEL.initDelete($namespace);
			
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
				  $("#" + $namespace + "_divisionId").text($divisionLookup[QUOTEUTILS.getDivision()]);
				  //QUOTEUTILS.setDivision($jobDetail.divisionId);
				} else {
					$("#" + $namespace + "_divisionId").val($jobDetail.divisionId);
					//$("#" + $namespace + "_divisionId").selectmenu("refresh");
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
				
				$id = this.id;
        	    $num = $id.substring(0,$id.indexOf("_"));
        	    console.log($num);
        	    $jobClicked = $num;
        		$("#activateFormButton").button('option', 'label', 'Activate Job');
        		$("#activateFormCloseButton").button('option', 'label', 'Close');

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
	      	    			id: "activateFormButton",
	      	        		click: function() {
	      	        			JOBPANEL.activateJob($jobClicked+"_jobPanel", $modalNamespace);
	      	        		}
	      	      		},
	      	      		{
	        	    		id: "activateFormCloseButton",
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
			var $goButtonId = "cancelFormButton";
			var $closeButtonId = "cancelFormCloseButton";
			var $cancelFieldSelector = "." + $modalNamespace + "_cancelField"
			var $cancelMessageSelector = "." + $modalNamespace + "_cancelMessage";
			
			$($cancelFieldSelector).focus(function() {
				$($cancelMessageSelector).html("");
			});
			
			$($cancelJobButtonSelector).click(function() {
				//$("#updateOrAdd").val("add");
        		//clearAddForm();				
				$id = this.id;
        	    $num = $id.substring(0,$id.indexOf("_"));
        	    console.log($num);
        	    $jobClicked = $num;
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
	      	        			JOBPANEL.cancelJob($jobClicked+"_jobPanel", $modalNamespace, $jobClicked);
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
			var $goButtonId = "scheduleFormButton";
			var $closeButtonId = "scheduleFormCloseButton";
			                                       
			
			$($scheduleJobButtonSelector).click(function() {
				//$("#updateOrAdd").val("add");
        		//clearAddForm();				
				$id = this.id;
        	    $num = $id.substring(0,$id.indexOf("_"));
        	    console.log($num);
        	    $jobClicked = $num;
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
	      	        			JOBPANEL.scheduleJob($jobClicked+"_jobPanel", $modalNamespace);
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
		initDelete: function($namespace){
			var $deleteJobButtonSelector = "#" + $namespace + "_deleteJobButton";
		
			$($deleteJobButtonSelector).click(function() {
				//$("#updateOrAdd").val("add");
        		//clearAddForm();				
				$id = this.id;
        	    $num = $id.substring(0,$id.indexOf("_"));
        	    console.log($num);
        	    $jobClicked = $num;
        	    $errorMessage ="</p>Are you sure you want to delete job "+$("#"+$jobClicked+"_jobPanel_jobId").val()+"?</p>";
				$( "#delete-message" ).dialog( "option", "title", "Delete Job?" );
				$( "#delete-message" ).html($errorMessage);
        	    $( "#delete-message" ).dialog( "open" );
			});
			
			$( "#delete-message" ).dialog({
			      modal: true,
			      autoOpen: false,
			      width: 400,
			      buttons: {
			        Cancel: function() {
			          $( this ).dialog( "close" );
			        },
			        Delete: function() {
			        	$( this ).dialog( "close" );
			        	JOB_UTILS.deleteJob($namespace,$jobClicked);
			        }
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
		
		cancelJob: function($namespace, $modalNamespace, $rn) {
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
							JOB_UTILS.panelLoad($rn,$jobId);
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
							location.reload();
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
							JOB_UTILS.panelLoad($jobClicked, $jobId);
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
				$id = $("#"+$namespace.substring(0,$namespace.indexOf("_"))+"_jobPanel_jobId").val();
				if($("#" + $namespace + "_proposalEdit").hasClass( "fa-pencil-alt" )){
					//$("#" + $namespace + "_jobFrequency").selectmenu( "option", "disabled", false );
					$("#" + $namespace + "_jobFrequency").prop( "disabled", false );
					$("#" + $namespace + "_jobNbr").prop('disabled', false);
					$("#" + $namespace + "_ppc").prop('disabled', false);
					$("#" + $namespace + "_serviceDescription").prop('disabled', false);
					
					$("#" + $namespace + "tooltiptext").text("Save");
					
					$("#" + $namespace + "_proposalEdit").removeClass('fa-pencil-alt');
					$("#" + $namespace + "_proposalEdit").addClass('fa-save');
				} else if($("#" + $namespace + "_proposalEdit").hasClass( "fa-save" )){
					//$("#" + $namespace + "_jobFrequency").selectmenu( "option", "disabled", true );
					$("#" + $namespace + "_jobFrequency").prop( "disabled", true );
					$("#" + $namespace + "_jobNbr").prop('disabled', true);
					$("#" + $namespace + "_ppc").prop('disabled', true);
					$("#" + $namespace + "_serviceDescription").prop('disabled', true);
					
					$outbound = {};
					$outbound["action"]	= 'UPDATE_JOB';
					$outbound["jobFrequency"]	= $("#" + $namespace + "_jobFrequency").val();
					$outbound["jobNbr"]	= $("#" + $namespace + "_jobNbr").val();
					$outbound["ppc"]	= $("#" + $namespace + "_ppc").val();
					$outbound["serviceDescription"]	= $("#" + $namespace + "_serviceDescription").val();
					
					JOB_UTILS.addJob($namespace.substring(0,$namespace.indexOf("_")),$id,$namespace,false);
					
					$("#" + $namespace + "tooltiptext").text("Edit");
					
					$("#" + $namespace + "_proposalEdit").removeClass('fa-save');
					$("#" + $namespace + "_proposalEdit").addClass('fa-pencil-alt');
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
			//$($ticketListSelector).click(function($event){
			//	var $jobId = $jobDetail.jobId;
			//	location.href="ticketLookup.html?jobId=" + $jobId;
			//});
			$($ticketListSelector).mousedown(function($event){
				var $jobId = $jobDetail.jobId;
				var $ticketUrl = "ticketLookup.html?jobId=" + $jobId;
				switch($event.which) {
				case 1:
					location.href=$ticketUrl;
					break;
				case 2:
					location.href=$ticketUrl;
					break;
				case 3:
					var win = window.open($ticketUrl, '_blank');
					win.focus();
					break;
				default:
					location.href=$ticketUrl;
				}
			});
			
			
			
			
//			console.log("Job Scheduling JobDetail");
//			console.log($jobDetail);
			var $repeatSelectorName = "#" + $namespace + "_" + "annualRepeat";
			$($repeatSelectorName).click(function($event) {
				$id = this.id;
        	    $num = $id.substring(0,$id.indexOf("_"));
        	    console.log($num);
        	    $jobClicked = $num;
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
								JOB_UTILS.panelLoad($jobClicked,$jobId);
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



