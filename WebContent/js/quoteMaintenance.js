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
					$currentRow++;
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
					
				}
				
				var $quoteData = $quoteDetail.quote;
				console.log($quoteDetail);
				ADDRESSPANEL.init("jobSite", JOB_DATA.countryList);
				ADDRESSPANEL.init("billTo", JOB_DATA.countryList);
				

				QUOTEUTILS.setSelectMenu("accountType",ANSI_UTILS.getCodes("quote","account_type"));
				QUOTEUTILS.setSelectMenu("leadType",ANSI_UTILS.getCodes("quote","lead_type"));
				
				$divisionList = ANSI_UTILS.getDivisionList();
				$buildingTypeList = ANSI_UTILS.makeBuildingTypeList();
				

				QUOTEUTILS.setDivisionList($divisionList);
				
				if($quoteDetail != null){
					//console.log($quoteData);
					if($quoteDetail.billTo != null){
						ADDRESSPANEL.setAddress("billTo",$quoteDetail.billTo);
					}
					if($quoteDetail.jobSite != null){
						ADDRESSPANEL.setAddress("jobSite",$quoteDetail.jobSite);
					}
						
						$("select[name='division']").val($quoteData.getDivisionCode);
						$("select[name='division").selectmenu("refresh");
						
					if($quoteData.proposalDate != ''){
						$("input[name='proposalDate']").val($quoteData.proposalDate);
					}
					
					var $manager = $quoteData.managerFirstName + " " + $quoteData.managerLastName + " (" + $quoteData.managerEmail + ")";
					$("input[name='manager']").val($manager);
					
					var $jobs = QUOTEUTILS.getJobs($quoteId);
					//console.log($jobs);
					var modalText = "";
					$.each($jobs.reverse(), function($index, $job) {
						//console.log($currentRow);
						//addAJob($currentRow);
						
						JOB_UTILS.panelLoadQuote($currentRow, $job.jobId, $index);
						$(".addressTable").remove();
						
						modalText += "<webthing:jobActivateCancel page='QUOTE' namespace='"+$currentRow+"_activateModal' />";
						$currentRow++;
					});
					
					$("#modalSpan").html(modalText);
					QUOTEUTILS.bindAndFormat();
				}
				
				$("#quoteTable").show();
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
			
			addAJob: function(){
				var $namespace = "jobpanel" + $currentRow.toString();
				$currentRow++;
				var jqxhr1 = $.ajax({
					type: 'GET',
					url: 'quotePanel.html',
					data: {"panelname":$namespace,"page":"QUOTE"},
					success: function($data) {
						//console.log($data);
						
						$('#jobPanelHolder > tbody:last-child').append($data);
						
						$(".addressTable").remove();
						var $jobDetail = null;			
						var $quoteDetail = null;
						var $lastRun = null;
						var $nextDue = null;
						var $lastCreated = null;
						JOBPANEL.init($currentRow.toString()+"_jobPanel", JOB_DATA.divisionList, "activateModal", $jobDetail);
						JOBPROPOSAL.init($namespace+"_jobProposal", JOB_DATA.jobFrequencyList, $jobDetail);
						JOBACTIVATION.init($namespace+"_jobActivation", JOB_DATA.buildingTypeList, $jobDetail);
						JOBDATES.init($namespace+"_jobDates", $quoteDetail, $jobDetail);
						JOBSCHEDULE.init($namespace+"_jobSchedule", $jobDetail, $lastRun, $nextDue, $lastCreated)
						JOBINVOICE.init($namespace+"_jobInvoice", JOB_DATA.invoiceStyleList, JOB_DATA.invoiceGroupingList, JOB_DATA.invoiceTermList, $jobDetail);
						JOBAUDIT.init($namespace+"_jobAudit", $jobDetail);
						bindAndFormat();
						$currentRow++;
						
						
						
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
				console.log($quoteId);
				var $returnValue = null;
				if ( $quoteId != null ) {
					var $url = "getJobs?quoteId=" + $quoteId;
					//console.log($url);
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
				$.each($data.codeList, function($index, $val) {
					$select.append(new Option($val.displayValue));
				});

				$select.selectmenu();
			},
			bindAndFormat:function(){
				$.each($('input'), function () {
			        $(this).css("height","20px");
			        $(this).css("max-height", "20px");
			    });
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



