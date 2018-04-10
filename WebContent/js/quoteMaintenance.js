$( document ).ready(function() {

	var $currentRow = 0;
	var $globalQuoteId = 0;
	var $billToId = null;
	var $jobSiteId = null;
	var $jobContactId = null;
	var $siteContactId = null;
	var $contractContactId = null;
	var $divisionId = 0;
	var $billingContactId = null;
	var $saveCounter = 0;
	;JOB_DATA = {}
	;QUOTE_DATA = {}
		
	
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
					QUOTEUTILS.addAJob($globalQuoteId);
				});
				
				$( "#error-message" ).dialog({
				      modal: true,
				      autoOpen: false,
				      width: 400,
				      buttons: {
				        Ok: function() {
				          $( this ).dialog( "close" );
				        }
				      }
				 });
				
				
				QUOTEUTILS.panelLoad($quoteId);
			},
				
			panelLoad:function($quoteId) {
				var $quoteDetail = null;			
				var $jobDetail = null;
				var $lastRun = null;
				var $nextDue = null;
				var $lastCreated = null;
				
				$('.dateField').datepicker({
	                prevText:'&lt;&lt;',
	                nextText: '&gt;&gt;',
	                showButtonPanel:true
	            });
				
				QUOTE_PRINT.init_modal("#printQuoteDiv");
				
				if ( $quoteId != '' ) {
					var $quoteDetail = QUOTEUTILS.getQuoteDetail($quoteId);
					var $quoteData = $quoteDetail.quote;
					$globalQuoteId = $quoteId;
					QUOTE_DATA.data = $quoteData;
				} else {
					$("#printButton").hide();
				}
				
				
				console.log($quoteDetail);
				
				//ADDRESSPANEL.init("jobSite", JOB_DATA.countryList);
				//ADDRESSPANEL.init("billTo", JOB_DATA.countryList);
				
				//console.log(ANSI_UTILS.getCodes("quote","account_type"));
				QUOTEUTILS.setSelectMenu("manager",QUOTEUTILS.getUsers());
				// parms: table name, field name, select to be populated, key field, value field
				ANSI_UTILS.populateCodeSelect("quote", "account_type", "select[name='accountType']", "value", "displayValue")
				ANSI_UTILS.populateCodeSelect("quote", "lead_type", "select[name='leadType']", "value", "displayValue")
				
				$divisionList = ANSI_UTILS.getDivisionList();
				$buildingTypeList = ANSI_UTILS.makeBuildingTypeList();
				QUOTEUTILS.setDivisionList($divisionList);
				
			
				QUOTEUTILS.addressActions();
				
				
				if($quoteData != null){
					//console.log($quoteData);
					if($quoteDetail.billTo != null){
						//ADDRESSPANEL.setAddress("billTo",$quoteDetail.billTo);
						//ADDRESS_UTILS.getAddress($quoteDetail.billTo.addressId, "#billTo");
						$billToId = $quoteDetail.billTo.addressId;
						ADDRESS_UTILS.populateAddress("#billTo", $quoteDetail.billTo);
						 $( "input[name='billTo_name']" ).val($quoteDetail.billTo.name);
					}
					
					if($quoteDetail.jobSite != null){
						//ADDRESSPANEL.setAddress("jobSite",$quoteDetail.jobSite);
						//ADDRESS_UTILS.getAddress($quoteDetail.jobSite.addressId, "#jobSite");
						$jobSiteId = $quoteDetail.jobSite.addressId;
						ADDRESS_UTILS.populateAddress("#jobSite", $quoteDetail.jobSite);
						$( "input[name='jobSite_name']" ).val($quoteDetail.jobSite.name);
					}
					//console.log(" Quote Detail Contact Id:" + $quoteData.jobContactId);
					
					$signedByData = ADDRESSPANEL.getContact($quoteData.signedByContactId);
					//console.log("SignedBy");
					//console.log($signedByData);
					if($signedByData.length > 0){
						$("input[name='signedBy']").val($signedByData.lastName + ", "+$signedByData.firstName + "(" +$signedByData.contactId+")");
					}
					$("input[name='revision']").val($quoteData.revision);
					//	console.log("DivisionCode: "+ $quoteData.divisionId);
					
						$("select[name='division']").val($quoteData.divisionId);
						$divisionId = $quoteData.divisionId;
						//$("select[name='division").selectmenu("refresh");
						

					$("#printCount").html($quoteData.printCount);
					
					
					//console.log("Account Type: "+ $quoteData.accountType);
					//console.log(($quoteData.accountType).length < 128);
					if($quoteData.accountType.length < 128){
						$("select[name='accountType']").val($quoteData.accountType);
						//$("select[name='accountType").selectmenu("refresh");
					} else {
						$("select[name='leadType']").val(null);
						//$("select[name='leadType").selectmenu("refresh");
					}
					
					if($quoteData.leadType.length < 128){
						$("select[name='leadType']").val($quoteData.leadType);
						//$("select[name='leadType").selectmenu("refresh");
					} else {
						$("select[name='leadType']").val(null);
						//$("select[name='leadType").selectmenu("refresh");
					}
					
					if($quoteData.proposalDate != ''){
						$("input[name='proposalDate']").val($quoteData.proposalDate);
					}
					
					
					
					
					//var $manager = $quoteData.managerFirstName + " " + $quoteData.managerLastName + " (" + $quoteData.managerEmail + ")";
					//console.log("ManagerId: " + $quoteData.managerId);
					$("select[name='manager']").val($quoteData.managerId);
					//$("select[name='manager']").selectmenu("refresh");
					
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
						console.log("Loading Job: "+$index);
						$jobContacts = JOB_UTILS.panelLoadQuote($currentRow, $job.jobId, $index, $quoteData);
												
						console.log("Job Contacts:");
						console.log($jobContacts);
						
						if($index == 0){
							if($jobContacts.jobContactId != null){
								$jobContactId = $jobContacts.jobContactId;
								var data = QUOTEUTILS.getContact($jobContactId);
						    	$("input[name='jobSite_jobContactName']").val(data.value);
						    	$("span[name='jobSite_jobContactInfo']").html(QUOTEUTILS.processContact(data));
							}
							if($jobContacts.siteContact != null){
								$siteContactId = $jobContacts.siteContact;
								var data = QUOTEUTILS.getContact($siteContactId);
								$("input[name='jobSite_siteContactName']").val(data.value);
						    	$("span[name='jobSite_siteContactInfo']").html(QUOTEUTILS.processContact(data));
							}
							if($jobContacts.contractContactId != null){
								$contractContactId = $jobContacts.contractContactId;
								var data = QUOTEUTILS.getContact($contractContactId);
								$("input[name='billTo_contractContactName']").val(data.value);
						    	$("span[name='billTo_contractContactInfo']").html(QUOTEUTILS.processContact(data));
							}
							if($jobContacts.billingContactId != null){
								$billingContactId = $jobContacts.billingContactId;
								console.log("Just set $billingContactId: "+ $billingContactId);
								var data = QUOTEUTILS.getContact($billingContactId);
								$("input[name='billTo_billingContactName']").val(data.value);
						    	$("span[name='billTo_billingContactInfo']").html(QUOTEUTILS.processContact(data));
							}
						}
						

						
						//$(".addressTable").remove();
//						console.log("#"+$currentRow+"_jobPanel_jobLink");
						
						//console.log(QUOTEUTILS.getModal($currentRow));
						$currentRow++;
					});
					
					//$("#modalSpan").html(modalText);
					
					QUOTEUTILS.bindAndFormat();
					if($quoteData.proposalDate != null){
						//console.log("Disable Button!");
						$("#addJobRow").hide();
					}
				} else {
					QUOTEUTILS.addAJob($globalQuoteId,false);
					$("#loadingJobsDiv").hide();
				}
				
				
				
				QUOTEUTILS.buttonsInit();
				QUOTEUTILS.bindAndFormat();
				
				 setTimeout(function() {
					$("#loadingDiv").hide();
					$("#quoteTable").show();
					$("#accordian").accordion({
							collapsible: true
					});
			        }, 750);
				
			},
			processContact:function($ui){
				
		    	var spanText = "";
		    	
		    	if($ui.preferredContactValue != null){
		    	var res = ($ui.preferredContactValue).split(":");
		    	if(res[0] == "mobile_phone"){ //mobile-phone
		    		spanText = "<i class='fa fa-mobile' aria-hidden='true'></i>&nbsp;"+res[1];
		    	} else if(res[0] == "email"){
		    		spanText = "<i class='fa fa-envelope-o' aria-hidden='true'></i>&nbsp;<a href='mailto:"+res[1]+"' target='_top'>"+res[1]+"</a>";
		    	} else if(res[0] == "business_phone"){
		    		spanText = "<i class='fa fa-phone' aria-hidden='true'></i>&nbsp;"+res[1];
		    	} else if(res[0] == "fax"){
		    		spanText = "<i class='fa fa-fax' aria-hidden='true'></i>&nbsp;"+res[1];
		    	}
		    	
		    	//spanText += "&nbsp;"+res[1];
		    	}
		    	return spanText;
			},
			incrementSave: function(){
				$saveCounter++;
			},
			getSaveCounter: function(){
				return $saveCounter;
			},
			getCurrentRow: function(){
				return $currentRow;
			},
			getContact:function($id){
				var $url = "contactTypeAhead?id="+$id;
//				console.log("Contact Data: "+$id);
				$returnValue = null;
				var jqxhr = $.ajax({
					type: 'GET',
					url: $url,
					data: {},
					statusCode: {
						200: function($data) {
//							console.log("Contact Data: "+$id);
//							console.log($data[0]);
							$returnValue = $data[0];
						},					
						403: function($data) {
							$("#useridMsg").html($data.responseJSON.responseHeader.responseMessage);
						},
						404: function($data) {
							console.log("THis?");
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
			addressActions: function(){
				 var $jobSiteNameComplete = $( "input[name='jobSite_name']" ).autocomplete({
				     'source':"addressTypeAhead?",
				      select: function( event, ui ) {
				    	$jobSiteId = ui.item.id;
				    	ADDRESS_UTILS.clearAddress("#jobSite");
				    	ADDRESS_UTILS.getAddress($jobSiteId, "#jobSite");
				    	
				      },
		              response: function(event, ui) {
		            	  if (ui.content.length === 0) {
		            		  alert("No Matches")
		            	  }
		              }
				}).data('ui-autocomplete');
				 $jobSiteNameComplete._renderMenu = function( ul, items ) {
					var that = this;
					$.each( items, function( index, item ) {
						that._renderItemData( ul, item );
					});
					if ( items.length == 1 ) {
				    	$jobSiteId = items[0].id;
				    	ADDRESS_UTILS.clearAddress("#jobSite");
				    	ADDRESS_UTILS.getAddress($jobSiteId, "#jobSite");
					}
				}
				
				 $( "input[name='jobSite_name']" ).focusout(function() {
					 if( $( "input[name='jobSite_name']" ).val() == ""){
						 $jobSiteId = null;
					 }
				  });
				 
				
				var $billToNameComplete = $( "input[name='billTo_name']" ).autocomplete({
				     'source':"addressTypeAhead?",
				      select: function( event, ui ) {
				    	$billToId = ui.item.id;
				    	ADDRESS_UTILS.clearAddress("#billTo");
				    	ADDRESS_UTILS.getAddress($billToId, "#billTo");
				    	//label
				    	//preferredContactValue
				      },
		              response: function(event, ui) {
		            	  if (ui.content.length === 0) {
		            		  alert("No Matches")
		            	  }
		              }
				}).data('ui-autocomplete');
				$billToNameComplete._renderMenu = function( ul, items ) {
					var that = this;
					$.each( items, function( index, item ) {
						that._renderItemData( ul, item );
					});
					if ( items.length == 1 ) {
						$billToId = items[0].id;
				    	ADDRESS_UTILS.clearAddress("#billTo");
				    	ADDRESS_UTILS.getAddress($billToId, "#billTo");
					}
				}

				
				
				$( "input[name='billTo_name']" ).focusout(function() {
					 if( $( "input[name='billTo_name']" ).val() == ""){
						 $billToId = null;
					 }
				  });
				 
				
				$( "input[name='jobSite_jobContactName']" ).autocomplete({
				     'source':"contactTypeAhead?",
				      select: function( event, ui ) {
				    	$jobContactId = ui.item.id;
				    	$("input[name='jobSite_jobContactName']").val(ui.item.value);
				    	$("span[name='jobSite_jobContactInfo']").html(QUOTEUTILS.processContact(ui.item));
				      },
		              response: function(event, ui) {
		            	  if (ui.content.length === 0) {
		            		  alert("No Matches")
		            	  }
		              }
				});

				$( "input[name='jobSite_jobContactName']" ).focusout(function() {
					 if( $( "input[name='jobSite_jobContactName']" ).val() == ""){
						 $jobContactId = null;
					 }
				  });
				 
				$( "input[name='jobSite_siteContactName']" ).autocomplete({
				     'source':"contactTypeAhead?",
				      select: function( event, ui ) {
				    	$siteContactId = ui.item.id;
				    	$("input[name='jobSite_siteContactName']").val(ui.item.value);
				    	$("span[name='jobSite_siteContactInfo']").html(QUOTEUTILS.processContact(ui.item));
				      },
		              response: function(event, ui) {
		            	  if (ui.content.length === 0) {
		            		  alert("No Matches")
		            	  }
		              }
				 });
				
				$( "input[name='jobSite_siteContactName']" ).focusout(function() {
					 if( $( "input[name='jobSite_siteContactName']" ).val() == ""){
						 $siteContactId = null;
					 }
				  });
				 
				$( "input[name='billTo_contractContactName']" ).autocomplete({
				     'source':"contactTypeAhead?",
				      select: function( event, ui ) {
				    	$contractContactId = ui.item.id;
				    	
				    	$("input[name='billTo_contractContactName']").val(ui.item.value);
				    	$("span[name='billTo_contractContactInfo']").html(QUOTEUTILS.processContact(ui.item));
				    	
				    	if( $( "input[name='jobSite_jobContactName']" ).val() == ""){
				    		$jobContactId = ui.item.id;
					    	$("input[name='jobSite_jobContactName']").val(ui.item.value);
					    	$("span[name='jobSite_jobContactInfo']").html(QUOTEUTILS.processContact(ui.item));
				    	}
				    	if( $( "input[name='jobSite_siteContactName']" ).val() == ""){
				    		$siteContactId = ui.item.id;
					    	$("input[name='jobSite_siteContactName']").val(ui.item.value);
					    	$("span[name='jobSite_siteContactInfo']").html(QUOTEUTILS.processContact(ui.item));
				    	}
				    	if( $( "input[name='billTo_billingContactName']" ).val() == ""){
				    		$billingContactId = ui.item.id;
					    	$("input[name='billTo_billingContactName']").val(ui.item.value);
					    	$("span[name='billTo_billingContactInfo']").html(QUOTEUTILS.processContact(ui.item));
				    	}
				      },
		              response: function(event, ui) {
		            	  if (ui.content.length === 0) {
		            		  alert("No Matches")
		            	  }
		              }
				 });
				
				$( "input[name='billTo_contractContactName']" ).focusout(function() {
					 if( $( "input[name='billTo_contractContactName']" ).val() == ""){
						 $contractContactId = null;
					 }
				  });
				 
				 
				 $( "input[name='billTo_billingContactName']" ).autocomplete({
				     'source':"contactTypeAhead?",
				      select: function( event, ui ) {
				    	$billingContactId = ui.item.id;
				    	
				    	$("input[name='billTo_billingContactName']").val(ui.item.value);
				    	$("span[name='billTo_billingContactInfo']").html(QUOTEUTILS.processContact(ui.item));
				      },
		              response: function(event, ui) {
		            	  if (ui.content.length === 0) {
		            		  alert("No Matches")
		            	  }
		              }
				 });
				
				 $( "input[name='billTo_billingContactName']" ).focusout(function() {
					 if( $( "input[name='billTo_billingContactName']" ).val() == ""){
						 $billingContactId = null;
					 }
				  });
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
				//$select.selectmenu({ width : '175px', maxHeight: '400 !important', style: 'dropdown'});
				
				$('option', $select).remove();
				$.each($divisionList, function($index, $division) {
					var $displayValue = $division.divisionNbr + "-" + $division.divisionCode;
					$select.append(new Option($displayValue, $division.divisionId));
				});

				//$select.selectmenu();
			},
			buttonsInit:function() {
				
				$("#quoteSaveButton").button().on( "click", function() {
					var qN = $("input[name=quoteNumber]").val();
					if(qN.length == 0){
						QUOTEUTILS.save(false);
					} else {
						QUOTEUTILS.update(false);
					}
					
	            });
				$("#quoteCancelButton").button().on( "click", function() {
//					QUOTEUTILS.cancel();
	            });
				$("#quoteExitButton").button().on( "click", function() {
					var qN = $("input[name=quoteNumber]").val();
					$rtn = null;
					if(qN.length == 0){
						$rtn = QUOTEUTILS.save(true);
					} else {
						$rtn = QUOTEUTILS.update(true);
					}
					
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
				$("#printButton").on( "click", function(event) {
					event.preventDefault();
					//ADD PROPOSE ACTION
					QUOTE_PRINT.showQuotePrint("#printQuoteDiv", $globalQuoteId, $("input[name=quoteNumber]").val());
					console.log("Print Quote");
	            });
				
				 
				
			},
			save: function($exit){
				$outbound = {};
				
	        		$outbound["managerId"]		=	$("select[name=manager]").val();
	        		$outbound["leadType"]		=	$("select[name=leadType").val();
	        		$outbound["accountType"]	=	$("select[name=accountType").val();
	        		$outbound["divisionId"]	=	$("select[name=division]").val();
	        		QUOTEUTILS.setDivision($("select[name=division]").val());

	        		$outbound["jobSiteAddressId"]	=	$jobSiteId;
	        		$outbound["billToAddressId"]	=	$billToId;
				
	        		$outbound["templateId"]	=	0;
	        		
	        		$outbound["jobContactId"] = $jobContactId;
	        		$outbound["siteContact"] = $siteContactId;
	        		$outbound["contractContactId"] = $contractContactId;
	        		$outbound["billingContactId"] = $billingContactId;


        		console.log("Save Outbound: ");
        		console.log($outbound);
        		console.log("$currentRow");
				console.log($currentRow);
        		
        		$url = "quote/add";
//				console.log($outbound);
				var jqxhr = $.ajax({
					type: 'POST',
					url: $url,
					data: JSON.stringify($outbound),
					error: function($data) {
						console.log("Fail: ");
						console.log($data);
					},
					statusCode: {
						200: function($data) {
							$(".inputIsInvalid").removeClass("fa-ban");
							$(".inputIsInvalid").removeClass("inputIsInvalid");
		   					if ( $data.responseHeader.responseCode=='EDIT_FAILURE') {
		   						$("#QuoteSaveHead").removeClass("grey");
		   						$("#QuoteSaveHead").removeClass("error");
		   						$("#QuoteSaveHead").removeClass("green");
		   						$("#QuoteSaveHead").addClass("error");
								console.log("Edit Failure");
								console.log($data);
								
								$errorMessage ="<p>";
								$.each($data.data.webMessages, function(key, messageList) {
									var identifier = "#"+key+"Err";
									$errorMessage += "<b>"+key+"</b>: "+messageList[0]+"<br/>";
									$(identifier).addClass("fa-ban");
									$(identifier).addClass("inputIsInvalid");
								});	
								$errorMessage +="</p>";
								$( "#error-message" ).dialog( "option", "title", "Quote Save Error" );
								$( "#error-message" ).html($errorMessage);
								$( "#error-message" ).dialog("open");
								return false;
		   					} else if ( $data.responseHeader.responseCode == 'SUCCESS') {
//								$("input[name=quoteId]").val($data.data.quote.quoteId);//gag
								$globalQuoteId =	$data.data.quote.quoteId;//gag
								$("#printButton").show();
								$("#QuoteSaveHead").removeClass("grey");
		   						$("#QuoteSaveHead").removeClass("error");
		   						$("#QuoteSaveHead").removeClass("green");
		   						$("#QuoteSaveHead").addClass("green");
								$("input[name=quoteNumber]").val($data.data.quote.quoteNumber);
								$("input[name=revision]").val($data.data.quote.revision);

								console.log("Save Success");
								console.log($data);
								if ( 'GLOBAL_MESSAGE' in $data.data.webMessages ) {
									$("#globalMsg").html($data.data.webMessages['GLOBAL_MESSAGE'][0]).fadeIn(10).fadeOut(6000);
								}
									
								console.log("$currentRow");
								console.log($currentRow);
								$saveCounter = 0;
								for($i = 0;$i < $currentRow;$i++){
										JOB_UTILS.addJob($i,$globalQuoteId, $i, $exit);
								}
								
		   					} else {
		   						$("#globalMsg").html($data.responseHeader.responseMessage);

		   					}
		   				},
	   					403: function($data) {
		   					$("#globalMsg").html($data.responseJSON.responseHeader.responseMessage);
		   				},
		   				404: function($data) {		   					
		   					
		   				},
		   				405: function($data) {
		   					$("#globalMsg").html("System Error: Contact Support").fadeIn(4000);
		   				},
		   				500: function($data) {
	        	    		$("#globalMsg").html("System Error: Contact Support").fadeIn(4000);
	        	    	}
					},
					dataType: 'json'
				});
				
			},
			update: function($exit){
				$outbound = {};
				
		       		$outbound["managerId"]		=	$("select[name=manager]").val();
		       	 	$outbound["leadType"]		=	$("select[name=leadType").val();
	        		$outbound["accountType"]	=	$("select[name=accountType").val();
	        		$outbound["divisionId"]	=	$("select[name=division]").val();
	        		$outbound["jobSiteAddressId"]	=	$jobSiteId;
	        		$outbound["billToAddressId"]	=	$billToId;
	        		$outbound["templateId"]	=	0;
	        		$outbound["jobContactId"] = $jobContactId;
	        		$outbound["siteContact"] = $siteContactId;
	        		$outbound["contractContactId"] = $contractContactId;
	        		$outbound["billingContactId"] = $billingContactId;
	        		$outbound["quoteNumber"] = $("input[name='quoteNumber']").val();
	        		$outbound["revision"] = $("input[name='revision']").val();
	        		$outbound["quoteId"] = $globalQuoteId;
        		
        		console.log("$currentRow");
				console.log($currentRow);
					
        		console.log("Update Outbound: ");
        		console.log($outbound);

        		$url = "quote/"+$globalQuoteId+"/"+$("input[name=quoteNumber]").val()+"/"+$("input[name=revision]").val();
//        		$url = "quote/"+$("input[name=quoteId]").val()+"/"+$("input[name=quoteNumber]").val()+"/"+$("input[name=revision]").val();
        		console.log("URL: "+ $url);
        		var jqxhr = $.ajax({
					type: 'POST',
					url: $url,
					data: JSON.stringify($outbound),
					error: function($data) {
						console.log("Fail: ");
						console.log($data);
					},
					statusCode: {
						200: function($data) {
		   					if ( $data.responseHeader.responseCode=='EDIT_FAILURE') {
		   						//doQuoteEditFailure($data.data);
		   						$("#QuoteSaveHead").removeClass("grey");
		   						$("#QuoteSaveHead").removeClass("error");
		   						$("#QuoteSaveHead").removeClass("green");
		   						$("#QuoteSaveHead").addClass("error");
		   						alert("Edit Failure: Required Data is Missing");
								console.log("Edit Failure");
								console.log($data);
								$.each($data.data.webMessages, function(key, messageList) {
									var identifier = "#"+key+"Err";
									$(identifier).addClass("fa-ban");
									$(identifier).addClass("inputIsInvalid");
								});	
								return false;
		   					} else if ( $data.responseHeader.responseCode == 'SUCCESS') {
		   						console.log("Update Success");
								console.log($data);
								$("#QuoteSaveHead").removeClass("grey");
		   						$("#QuoteSaveHead").removeClass("error");
		   						$("#QuoteSaveHead").removeClass("green");
		   						$("#QuoteSaveHead").addClass("green");
									if ( 'GLOBAL_MESSAGE' in $data.data.webMessages ) {
										$("#globalMsg").html($data.data.webMessages['GLOBAL_MESSAGE'][0]).fadeIn(10).fadeOut(6000);
									}

					        		console.log("$currentRow");
									console.log($currentRow);

									$saveCounter = 0;
									for($i = 0;$i < $currentRow;$i++){
											JOB_UTILS.addJob($i,$globalQuoteId, $i, $exit);
									}
									
			
		   					} else {
		   						$("#globalMsg").html($data.responseHeader.responseMessage);

		   					}
		   				},
	   					403: function($data) {
		   					$("#globalMsg").html($data.responseJSON.responseHeader.responseMessage);
		   				},
		   				404: function($data) {		   					
		   					
		   				},
		   				405: function($data) {
		   					$("#globalMsg").html("System Error: Contact Support").fadeIn(4000);
		   				},
		   				500: function($data) {
	        	    		$("#globalMsg").html("System Error: Contact Support").fadeIn(4000);
	        	    	}
					},
					dataType: 'json'
				});
				
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
							alert("Edit Failure: Required Data is Missing");
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
			addAJob: function($quoteId,refresh=true){
				var $namespace = $currentRow.toString() + "_jobPanel";
				var $row = $currentRow.toString();
				$globalQuoteId = $quoteId;
//				alert("quote addAJob quoteId:" + $quoteId);
				$currentRow++;
				var jqxhr1 = $.ajax({
					type: 'GET',
					url: 'quotePanel.html',
					data: {"panelname":$row,"page":"QUOTE"},
					success: function($data) {

	
					},
					statusCode: {
						200: function($data) {

							$('#accordian').append("<h3 id="+$row+"_jobHeader><span id='"+$row+"_jobStatusHead'></span>&nbsp;&nbsp;Job: <span id='"+$row+"_jobIdHead'></span>&nbsp;&nbsp;Job #: <span id='"+$row+"_jobNumberHead'></span>&nbsp;&nbsp;PPC: <span id='"+$row+"_jobPPCHead'></span>&nbsp;&nbsp; Frequency: <span id='"+$row+"_jobFreqHead'></span>&nbsp;&nbsp; Desc: <span id='"+$row+"_jobDescHead'></span><i id='"+$row+"_jobSaveHead' class='fa fa-floppy-o saveIcon' aria-hidden='true'></i></h3>");
							$('#accordian').append("<div id="+$row+"_jobDiv>"+$data+"</div>");
		   					//	$('#jobPanelHolder > tbody:last-child').append($data);
//								console.log($namespace);
								var $jobDetail = [{invoiceStyle: null, activationDate: null, startDate: null, cancelDate: null, cancelReason: null}];		

								var $quoteDetail = [{proposalDate: null}];
								var $lastRun = null;
								var $nextDue = null;
								var $lastCreated = null;
								//console.log(JOB_DATA);
								
								JOBPANEL.init($row+"_jobPanel", JOB_DATA.divisionList, "activateModal", $jobDetail);
								JOBPROPOSAL.init($row+"_jobProposal", JOB_DATA.jobFrequencyList, $jobDetail);
								JOBACTIVATION.init($row+"_jobActivation", JOB_DATA.buildingTypeList, $jobDetail);
								$("#" + $row+"_jobActivation" + "_nbrFloors").spinner( "option", "disabled", false );
								JOBDATES.init($row+"_jobDates", $quoteDetail, $jobDetail);
								JOBSCHEDULE.init($row+"_jobSchedule", $jobDetail, $lastRun, $nextDue, $lastCreated)
								JOBINVOICE.init($row+"_jobInvoice", JOB_DATA.invoiceStyleList, JOB_DATA.invoiceGroupingList, JOB_DATA.invoiceTermList, $jobDetail);
								JOBAUDIT.init($row+"_jobAudit", $jobDetail);
//								$('.jobSave').on('click', function($clickevent) {
//									JOB_UTILS.addJob($(this).attr("rownum"),$globalQuoteId);
//					            });
								QUOTEUTILS.bindAndFormat();
								
								//console.log($globalQuoteId);
								
								var $maxJobNbr = 0;
								
								for($x=0;$x<=$row;$x++){
									if(parseInt($("#"+$x+'_jobProposal_jobNbr').val()) > $maxJobNbr){
										$maxJobNbr = parseInt($("#"+$x+'_jobProposal_jobNbr').val());
									}
								}
								$("#"+$row+'_jobProposal_jobNbr').val(($maxJobNbr+1));
								$('#'+$row+'_jobActivation_automanual').val("auto");
								//$('#'+$row+'_jobActivation_automanual').selectmenu("refresh");
								$("#"+$row+'_jobActivation_equipment').val('BASIC');
								$("#"+$row+'_jobSchedule_annualRepeat').prop('checked', true);
								
								$('#'+$row+'_jobInvoice_invoiceStyle').val("INVOICE");
								//$('#'+$row+'_jobInvoice_invoiceStyle').selectmenu("refresh");
								
								$('#'+$row+'_jobInvoice_invoiceGrouping').val("BY_JOB_SITE");
								//$('#'+$row+'_jobInvoice_invoiceGrouping').selectmenu("refresh");
								
								$('#'+$row+'_jobInvoice_invoiceTerms').val("DAY10");
								//$('#'+$row+'_jobInvoice_invoiceTerms').selectmenu("refresh");
								//console.log(QUOTE_DATA['data']);
								if(QUOTE_DATA['data'] != null && QUOTE_DATA['data'].divisionCode != null){
									$("#"+$row+'_jobPanel_divisionId').text(QUOTE_DATA['data'].divisionCode);
								}
								//$("#"+$row+'_jobPanel_divisionId').selectmenu("refresh");
								$pageURL = window.location.href;
								if($pageURL.indexOf("job")>0){
									$("#"+$row+"_jobPanel_jobLinkSpan").hide();
								}
								if($pageURL.indexOf("quote")>0){
									$("#"+$row+"_jobPanel_quoteLinkSpan").hide();
								}
								$("#"+$row+"_jobPanel_scheduleJobButton").hide();
								$("#"+$row+"_jobPanel_deleteJobButton").hide();
								$("#"+$row+"_jobSchedule_showTicketList").hide();
								
								setTimeout(function() {
									if(refresh){
										$("#accordian").accordion( "refresh" );
										var current = $("#accordian").accordion( "option", "active" );

										$("#accordian").accordion("option","active",current+1);
									}
							       }, 250);
								
		   		
		   				},
	   					403: function($data) {
	   						$("#paymentModal").dialog("close");
		   					$("#globalMsg").html($data.responseJSON.responseHeader.responseMessage);
		   				},
		   				404: function($data) {		   					
		   					$("#pmtSearchIdErr").html("Invalid Payment Id").show().fadeOut(10000);
		   				},
		   				405: function($data) {
		   					$("#paymentModal").dialog("close");
		   					$("#globalMsg").html("System Error: Contact Support").fadeIn(4000);
		   				},
		   				500: function($data) {
		   					$("#paymentModal").dialog("close");
	        	    		$("#globalMsg").html("System Error: Contact Support").fadeIn(4000);
	        	    	}
					},
					dataType: 'html'
				});
			},			
			getjobContactId: function(){
				return $jobContactId;	
			},
			getsiteContact: function(){
				return $siteContactId;
			},
			getcontractContactId: function(){
				return $contractContactId;
			},
			getbillingContactId: function(){
				return $billingContactId;
			},
			setjobContactId: function($jobContactId1){
				$jobContactId = $jobContactId1;	
			},
			setsiteContactId: function($siteContactId1){
				$siteContactId = $siteContactId1;
			},
			setcontractContactId: function($contractContactId1){
				$contractContactId = $contractContactId1;
			},
			setbillingContactId: function($billingContactId1){
				$billingContactId = $billingContactId1;
			},
			setDivision: function($divisionId1){
				$divisionId = $divisionId1;
			},
			getDivision: function(){
				return $divisionId;
			},
			getQuoteId: function(){
				return $globalQuoteId;
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
				//$select.selectmenu({ width : '175px', maxHeight: '400 !important', style: 'dropdown'});
				//console.log($data);
				$('option', $select).remove();
				$select.append(new Option("",""));
				$.each($data.codeList, function($index, $val) {
					$select.append(new Option($val.displayValue,$val.value));
				});

				//$select.selectmenu({ width : '175px', maxHeight: '400 !important', style: 'dropdown'});
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
			},
			
		getUsers: function(){
			var $returnValue = [];
			var $codeList = [];
				var $url = "user/manager";
				var jqxhr = $.ajax({
					type: 'GET',
					url: $url,
					data: {"sortBy":"firstName"},    // you can do firstName,lastName or email
					statusCode: {
						200: function($data) {
							//console.log($data.data.userList);
							var $i = 0;
							$.each($data.data.userList, function($index, $val) {
								//if($val.title === "Division Manager"){
									$codeList[$i] = {displayValue: $val.firstName + " " + $val.lastName + " (" + $val.email + ")", value: $val.userId};
									$i++;
								//}
							});
							
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
			//console.log($returnValue);
			$returnValue = {codeList: $codeList};
			return $returnValue;
		}, doQuoteEditFailure: function ($data) {
    		$.each($data.webMessages, function(field, errList) {
    			var $selector = "#" + field + "Err";
    			if ( errList.length == 1 ) {
    				$($selector).html(errList[0]).show().fadeOut(8000);
    			} else {
    				var $errString = "";
    				$.each(errList, function(index, err) {
    					$errString = $errString + "<li>" + err + "</li>";
    				});
    				$($selector).html("<ul>" + errString + "</ul>").show().fadeOut(8000);
    			}
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



