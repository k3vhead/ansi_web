$( document ).ready(function() {

	var $currentRow = 0;
	;JOB_DATA = {}
	
	;QUOTEUTILS = {
			pageInit:function($quoteId) {
			
				$optionData = ANSI_UTILS.getOptions('JOB_FREQUENCY,JOB_STATUS,INVOICE_TERM,INVOICE_GROUPING,INVOICE_STYLE,COUNTRY');
				JOB_DATA.jobFrequencyList = $optionData.jobFrequency;
				JOB_DATA.jobStatusList = $optionData.jobStatus;
				JOB_DATA.invoiceTermList = $optionData.invoiceTerm;
				JOB_DATA.invoiceGroupingList = $optionData.invoiceGrouping;
				JOB_DATA.invoiceStyleList = $optionData.invoiceStyle;

				JOB_DATA.divisionList = ANSI_UTILS.getDivisionList();
				JOB_DATA.buildingTypeList = ANSI_UTILS.makeBuildingTypeList();
				JOB_DATA.countryList = $optionData.country;

				
				$("#addJobRow").click(function(){
					QUOTEUTILS.addAJob();
				});
				
				QUOTEUTILS.panelLoad($quoteId);
			},
				
			panelLoad:function($quoteId) {
				var $quoteDetail = null;			
				var $jobDetail = null;
				var $lastRun = null;
				var $nextDue = null;
				var $lastCreated = null;
				if ( $quoteId != '' ) {
					var $quoteDetail = QUOTEUTILS.getQuoteDetail($quoteId);
					var $quoteData = $quoteDetail.quote;
				}
				
				
				console.log($quoteDetail);
				ADDRESSPANEL.init("jobSite", JOB_DATA.countryList);
				ADDRESSPANEL.init("billTo", JOB_DATA.countryList);
				
				console.log(ANSI_UTILS.getCodes("quote","account_type"));
				QUOTEUTILS.setSelectMenu("accountType",ANSI_UTILS.getCodes("quote","account_type"));
				QUOTEUTILS.setSelectMenu("leadType",ANSI_UTILS.getCodes("quote","lead_type"));
				
				$divisionList = ANSI_UTILS.getDivisionList();
				$buildingTypeList = ANSI_UTILS.makeBuildingTypeList();
				

				QUOTEUTILS.setDivisionList($divisionList);
				
				if($quoteData != null){
					//console.log($quoteData);
					if($quoteDetail.billTo != null){
						ADDRESSPANEL.setAddress("billTo",$quoteDetail.billTo);
					}
					if($quoteDetail.jobSite != null){
						ADDRESSPANEL.setAddress("jobSite",$quoteDetail.jobSite);
					}
						console.log("DivisionCode: "+ $quoteData.divisionId);
						$("select[name='division']").val($quoteData.divisionId);
						$("select[name='division").selectmenu("refresh");
						
					//console.log("Account Type: "+ $quoteData.accountType);
					//console.log(($quoteData.accountType).length < 128);
					if($quoteData.accountType.length < 128){
						$("select[name='accountType']").val($quoteData.accountType);
						$("select[name='accountType").selectmenu("refresh");
					} else {
						$("select[name='leadType']").val(null);
						$("select[name='leadType").selectmenu("refresh");
					}
					
					if($quoteData.leadType.length < 128){
						$("select[name='leadType']").val($quoteData.leadType);
						$("select[name='leadType").selectmenu("refresh");
					} else {
						$("select[name='leadType']").val(null);
						$("select[name='leadType").selectmenu("refresh");
					}
					
					if($quoteData.proposalDate != ''){
						$("input[name='proposalDate']").val($quoteData.proposalDate);
					}
					
					var $manager = $quoteData.managerFirstName + " " + $quoteData.managerLastName + " (" + $quoteData.managerEmail + ")";
					$("input[name='manager']").val($manager);
					
					var $jobs = QUOTEUTILS.getJobs($quoteId);
					//console.log($jobs);
					if($jobs.length == 0){
						$("#loadingJobsDiv").hide();
					} else {
						$("#loadingJobsDiv").show();
					}
					var modalText = "";
					$.each($jobs, function($index, $job) {
						//console.log($currentRow);
						//addAJob($currentRow);
						console.log("Index:"+$index);
						JOB_UTILS.panelLoadQuote($currentRow, $job.jobId, $index);
						//$(".addressTable").remove();
						console.log("#"+$currentRow+"_jobPanel_jobLink");
						
						//console.log(QUOTEUTILS.getModal($currentRow));
						$currentRow++;
					});
					
					//$("#modalSpan").html(modalText);
					QUOTEUTILS.bindAndFormat();
				} else {
					$("#loadingJobsDiv").hide();
				}
				$("#loadingDiv").hide();
				$("#quoteTable").show();
				
				QUOTEUTILS.buttonsInit();
				QUOTEUTILS.bindAndFormat();
			},
			getQuoteDetail:function($quoteId) {
				var $returnValue = null;
				if ( $quoteId != null ) {
					var $url = "quote/" + $quoteId
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
				return $returnValue.quoteList[0];

			},
			
			setDivisionList:function($divisionList) {
				
				var $select = $("select[name='division']");
				$select.selectmenu({ width : '150px', maxHeight: '400 !important', style: 'dropdown'});
				
				$('option', $select).remove();
				$.each($divisionList, function($index, $division) {
					$select.append(new Option($division.divisionCode, $division.divisionId));
				});

				$select.selectmenu();
			},
			buttonsInit:function() {
				
				$("#quoteSaveButton").button().on( "click", function() {
					QUOTEUTILS.save();
	            });
				$("#quoteCancelButton").button().on( "click", function() {
//					QUOTEUTILS.cancel();
	            });
				$("#quoteExitButton").button().on( "click", function() {
					QUOTEUTILS.save();
//					QUOTEUTILS.exit();
	            });
				$("input[name=newQuoteButton]").button().on( "click", function(event) {
					event.preventDefault();
					window.location.replace("quoteMaintenance.html");
	            });
				$("input[name=copyQuoteButton]").button().on( "click", function(event) {
					event.preventDefault();
					console.log("Copy Quote");
	            });
				$("input[name=modifyQuoteButton]").button().on( "click", function(event) {
					event.preventDefault();
					console.log("Modify Quote");
	            });
				
			},
			save: function(){
				$outbound = {};
//        		$outbound["manager"]		=	$("input[name="+$addressPanelNamespace+"_name]").val();
//        		$outbound["lead_type"]		=	$("input[name="+$addressPanelNamespace+"_status]").val();
//        		$outbound["account_type"]	=	$("input[name="+$addressPanelNamespace+"_address1]").val();
//        		$outbound["proposal_date"]	=	$("input[name="+$addressPanelNamespace+"_address2]").val();
//        		$outbound["division_id"]		=	$("input[name="+$addressPanelNamespace+"_city]").val();
//        		
				
//				$aNames = {0:"jobSite",1:"billTo"};
//				$.each($aNames, function($index, $addressPanelNamespace) {
//					$aOutbound = {};
//					$aOutbound[$addressPanelNamespace+"name"]		=	$("input[name="+$addressPanelNamespace+"_name]").val();
//					$aOutbound[$addressPanelNamespace+"status"]		=	$("input[name="+$addressPanelNamespace+"_status]").val();
//					$aOutbound[$addressPanelNamespace+"address1"]	=	$("input[name="+$addressPanelNamespace+"_address1]").val();
//					$aOutbound[$addressPanelNamespace+"address2"]	=	$("input[name="+$addressPanelNamespace+"_address2]").val();
//					$aOutbound[$addressPanelNamespace+"city"]		=	$("input[name="+$addressPanelNamespace+"_city]").val();
//					$aOutbound[$addressPanelNamespace+"county"]		=	$("input[name="+$addressPanelNamespace+"_county]").val();
//					$aOutbound[$addressPanelNamespace+"country_code"]	=	$("input[name="+$addressPanelNamespace+"country] option:selected").val();
//	        		$aOutbound[$addressPanelNamespace+"state"]		=	$("input[name="+$addressPanelNamespace+"state] option:selected").val();
//	        		$aOutbound[$addressPanelNamespace+"zip"]		=	$("input[name="+$addressPanelNamespace+"_zip]").val();
//	        		QUOTEUTILS.saveAddress($aOutbound);
//				});
				
				
			},
			saveAddress: function($outbound){
				$url = "address/add";
				var jqxhr = $.ajax({
					type: 'POST',
					url: $url,
					data: JSON.stringify($outbound),
					success: function($data) {
						if ( $data.responseHeader.responseCode == 'SUCCESS') {
								if ( 'GLOBAL_MESSAGE' in $data.data.webMessages ) {
									$("#globalMsg").html($data.data.webMessages['GLOBAL_MESSAGE'][0]).fadeIn(10).fadeOut(6000);
								}
						} else if ( $data.responseHeader.responseCode == 'EDIT_FAILURE') {
							$.each($data.data.webMessages, function(key, messageList) {
								var identifier = "#" + key + "Err";
								msgHtml = "<ul>";
								$.each(messageList, function(index, message) {
									msgHtml = msgHtml + "<li>" + message + "</li>";
								});
								msgHtml = msgHtml + "</ul>";
								$(identifier).html(msgHtml);
							});		
							if ( 'GLOBAL_MESSAGE' in $data.data.webMessages ) {
								$("#addFormMsg").html($data.data.webMessages['GLOBAL_MESSAGE'][0]);
							}
						} else {
						}
					},
					error: function($data) {
						alert("fail");
						//console.log($data);
					},
					statusCode: {
						403: function($data) {
							$("#globalMsg").html($data.responseJSON.responseHeader.responseMessage);
						} 
					},
					dataType: 'json'
				});
			},
			addAJob: function(){
				var $namespace = $currentRow.toString() + "_jobPanel";
				var $row = $currentRow.toString();
				$currentRow++;
				var jqxhr1 = $.ajax({
					type: 'GET',
					url: 'quotePanel.html',
					data: {"panelname":$row,"page":"QUOTE"},
					success: function($data) {

						$('#jobPanelHolder > tbody:last-child').append($data);
						console.log($namespace);
						var $jobDetail = [{invoiceStyle: null, activationDate: null, startDate: null, cancelDate: null, cancelReason: null}];		

						var $quoteDetail = [{proposalDate: null}];
						var $lastRun = null;
						var $nextDue = null;
						var $lastCreated = null;
						//console.log(JOB_DATA);
						
						JOBPANEL.init($row+"_jobPanel", JOB_DATA.divisionList, "activateModal", $jobDetail);
						JOBPROPOSAL.init($row+"_jobProposal", JOB_DATA.jobFrequencyList, $jobDetail);
						JOBACTIVATION.init($row+"_jobActivation", JOB_DATA.buildingTypeList, $jobDetail);
						JOBDATES.init($row+"_jobDates", $quoteDetail, $jobDetail);
						JOBSCHEDULE.init($row+"_jobSchedule", $jobDetail, $lastRun, $nextDue, $lastCreated)
						JOBINVOICE.init($row+"_jobInvoice", JOB_DATA.invoiceStyleList, JOB_DATA.invoiceGroupingList, JOB_DATA.invoiceTermList, $jobDetail);
						JOBAUDIT.init($row+"_jobAudit", $jobDetail);
						QUOTEUTILS.bindAndFormat();
	
					},
					statusCode: {
						403: function($data) {
							$("#useridMsg").html($data.responseJSON.responseHeader.responseMessage);
						} 
					},
					dataType: 'html'
				});
			},
			
			getJobs: function($quoteId) {
				var $returnValue = null;
				if ( $quoteId != null ) {
					var $url = "getJobs?quoteId=" + $quoteId;
					var jqxhr = $.ajax({
						type: 'GET',
						url: $url,
						data: {},
						statusCode: {
							200: function($data) {
								$returnValue = $data;
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
			setSelectMenu: function($selector,$data){
				var $select = $("select[name='"+$selector+"']");
				$select.selectmenu({ width : '150px', maxHeight: '400 !important', style: 'dropdown'});
				
				$('option', $select).remove();
				$select.append(new Option("",null));
				$.each($data.codeList, function($index, $val) {
					$select.append(new Option($val.displayValue,$val.value));
				});

				$select.selectmenu();
			},
			bindAndFormat:function(){
				$.each($('input'), function () {
			        $(this).css("height","20px");
			        $(this).css("max-height", "20px");
			    });
			},
			
			getModal: function($namespace){
				var $returnValue = null;
				var jqxhr1 = $.ajax({
					type: 'GET',
					url: 'modalGet.html',
					data: {"namespace":$namespace,"page":'QUOTE'},
					success: function($data) {
						$returnValue = $data;
						//$(".addressTable").remove();
					},
					statusCode: {
						403: function($data) {
							$("#useridMsg").html($data.responseJSON.responseHeader.responseMessage);
						} 
					},
					dataType: 'html'
				});
				return $returnValue;
			}
			
		}
		
	
	;QUOTEAUDIT = {
		init: function($namespace, $quoteDetail) {
			if ( $quoteDetail != null ) {
//				var $createdBy = $jobDetail.addedFirstName + " " + $jobDetail.addedLastName + " (" + $jobDetail.addedEmail + ")";
//				var $updatedBy = $jobDetail.updatedFirstName + " " + $jobDetail.updatedLastName + " (" + $jobDetail.updatedEmail + ")";				
//				ANSI_UTILS.setTextValue($namespace, "createdBy", $createdBy);
//				ANSI_UTILS.setTextValue($namespace, "lastChangeBy", $updatedBy);
//				ANSI_UTILS.setTextValue($namespace, "lastChangeDate", $jobDetail.updatedDate);
			}
		}
	}
	
});



