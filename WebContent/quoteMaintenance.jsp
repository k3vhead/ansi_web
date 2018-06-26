<%@ page contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<%@ taglib uri="WEB-INF/c.tld" prefix="c" %>
<%@ taglib uri="WEB-INF/sql.tld" prefix="sql" %>
<%@ taglib uri="WEB-INF/fmt.tld" prefix="fmt" %>
<%@ taglib uri="WEB-INF/struts-html.tld"  prefix="html"  %>
<%@ taglib uri="WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="WEB-INF/struts-bean.tld"  prefix="bean"  %>
<%@ taglib uri="WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib tagdir="/WEB-INF/tags/webthing" prefix="webthing" %>
<%@ taglib tagdir="/WEB-INF/tags/quote" prefix="quote" %>
<%@ taglib uri="WEB-INF/theTagThing.tld" prefix="ansi" %>


<tiles:insert page="layout.jsp" flush="true">

    <tiles:put name="title" type="string">
        Quote Maintenance
    </tiles:put>
    
    
    <tiles:put name="headextra" type="string">
    	<link rel="stylesheet" href="css/sortable.css" type="text/css" />
        <script type="text/javascript" src="js/ansi_utils.js"></script>
        <script type="text/javascript" src="js/quotePrintHistory.js"></script>
        <script type="text/javascript" src="js/quotePrint.js"></script>
        <%--
        <script type="text/javascript" src="js/jobMaintenance.js"></script>
        <script type="text/javascript" src="js/quoteMaintenance.js"></script>        
         --%>
        <script type="text/javascript" src="js/addressUtils.js"></script>
        <script type="text/javascript">        
        
        	$(document).ready(function() {
				; QUOTEMAINTENANCE = {
					quoteId : '<c:out value="${ANSI_QUOTE_ID}" />',
					accountTypeList : null,
					countryList : null,
					buildingTypeList : null,
					divisionList : null,
					invoiceGroupingList : null,
					invoiceStyleList : null,
					invoiceTermList : null,
					jobStatusList : null,
					jobFrequencyList : null,
					leadTypeList : null,
					managerList : null,
					quote : null,
					
					progressbar : $("#progressbar"),
					progressLabel : $("#progress-label"),
					
					
					init : function() {
						QUOTEMAINTENANCE.makeProgressbar();
						QUOTEMAINTENANCE.init_modal();
						QUOTEMAINTENANCE.makeAutoComplete();
						QUOTEMAINTENANCE.makeOptionLists();
						QUOTEMAINTENANCE.makeButtons();
						QUOTEMAINTENANCE.makeOtherClickables();
						if (QUOTEMAINTENANCE.quoteId != '' ) {
							QUOTEMAINTENANCE.getQuote(QUOTEMAINTENANCE.quoteId);	
						}
						QUOTE_PRINT.init_modal("#printQuoteDiv");
						QUOTE_PRINT_HISTORY.init("#printHistoryDiv", "#viewPrintHistory");
						//$("#loading-container").hide();
						//$("#quotePanel").fadeIn(1000);
						//$("#address-container").fadeIn(1000);
						//$("#job-list-container").fadeIn(1000);						
					},
					
					
					
					doCopyQuote : function($quoteId) {
						console.log("Making a copy of " + $quoteId);	
						var $url = "quote/copy/" + $quoteId;
						var jqxhr = $.ajax({
							type: 'POST',
							url: $url,
							data: {},
							statusCode: {
								200: function($data) {
									location.href="quoteMaintenance.html?id=" + $data.data.quote.quoteId;
								},					
								403: function($data) {
									$("#globalMsg").html($data.responseJSON.responseHeader.responseMessage);
								},
								404: function($data) {
									$("#globalMsg").html("System Error Copy 404. Contact Support");
								},
								500: function($data) {
									$("#globalMsg").html("System Error Copy 500. Contact Support");
								}
							},
							dataType: 'json'
						});
					},
					
					
					
					doReviseQuote : function($quoteId) {
						console.log("Making a revise of " + $quoteId);	
						var $url = "quote/revise/" + $quoteId;
						var jqxhr = $.ajax({
							type: 'POST',
							url: $url,
							data: {},
							statusCode: {
								200: function($data) {
									location.href="quoteMaintenance.html?id=" + $data.data.quote.quoteId;
								},					
								403: function($data) {
									$("#globalMsg").html($data.responseJSON.responseHeader.responseMessage);
								},
								404: function($data) {
									$("#globalMsg").html("System Error Revise 404. Contact Support");
								},
								500: function($data) {
									$("#globalMsg").html("System Error Revise 500. Contact Support");
								}
							},
							dataType: 'json'
						});
					},
					
					
					
					doQuoteUpdate : function($quoteId, $data, $successCallback, $errCallback) {
						var $outbound = JSON.stringify($data);
						var $url = "quote/" + $quoteId;
						console.log($outbound);
						//$($modalName).dialog("close");
						//$("#globalMsg").html("Update faked").fadeOut(3000);
						
						var jqxhr3 = $.ajax({
							type: 'POST',
							url: $url,
							data: $outbound,
							statusCode: {
								200:function($data) {
									$successCallback($data);
								},
								403: function($data) {	
									$errCallback(403);									
								},
								404: function($data) {
									$errCallback(404);
								},
								500: function($data) {
									$errCallback(500);
								}
							},
							dataType: 'json'
						});
					},
					
					
					
					getDivisionList: function($callback) {
						var jqxhr3 = $.ajax({
							type: 'GET',
							url: 'division/list',
							data: {},
							statusCode: {
								200:function($data) {
									$callback($data.data);
									QUOTEMAINTENANCE.incrementProgress("Division List");
								},
								403: function($data) {								
									$("#globalMsg").html("Session Expired. Log In and try again").show();
								},
								404: function($data) {
									$("#globalMsg").html("System Error Division 404. Contact Support").show();
								},
								500: function($data) {
									$("#globalMsg").html("System Error Division 500. Contact Support").show();
								}
							},
							dataType: 'json',
							async:false
						});
					},
					
					
					getJobPanel : function($jobId) {
						var jqxhr1 = $.ajax({
		    				type: 'GET',
		    				url: "jobDisplay.html",
		    				data: null,			    				
		    				statusCode: {
		    					200: function($data) {
		    						QUOTEMAINTENANCE.getJob($jobId, $data);
		    					},			    				
		    					403: function($data) {
		    						$("#useridMsg").html($data.responseJSON.responseHeader.responseMessage);
		    					}, 
		    					404: function($data) {
		    						$("#globalMsg").html("System Error JobDisplay 404. Contact Support").show();
		    					}, 
		    					405: function($data) {
		    						$("#globalMsg").html("System Error JobDisplay 405. Contact Support").show();
		    					}, 
		    					500: function($data) {
		    						$("#globalMsg").html("System Error JobDisplay 500. Contact Support").show();
		    					}, 
		    				},
		    				dataType: 'html'
		    			});
					},
					
					
					
					
					getJob : function($jobId, $html) {
						var $url = "job/" + $jobId;
						var jqxhr1 = $.ajax({
		    				type: 'GET',
		    				url: $url,
		    				data: null,			    				
		    				statusCode: {
		    					200: function($data) {
		    						var $destination = "#job" + $jobId + " .job-data-row";
		    						$($destination).html($html);
		    						QUOTEMAINTENANCE.populateJobPanel($jobId, $destination, $data.data);		    						
		    					},			    				
		    					403: function($data) {
		    						$("#useridMsg").html($data.responseJSON.responseHeader.responseMessage);
		    					}, 
		    					404: function($data) {
		    						$("#globalMsg").html("System Error Option 404. Contact Support").show();
		    					}, 
		    					405: function($data) {
		    						$("#globalMsg").html("System Error Option 405. Contact Support").show();
		    					}, 
		    					500: function($data) {
		    						$("#globalMsg").html("System Error Option 500. Contact Support").show();
		    					}, 
		    				},
		    				dataType: 'json'
		    			});
					},
					
					
					getOptions: function($optionList, $callBack) {
		    			var $returnValue = null;
		    			var jqxhr1 = $.ajax({
		    				type: 'GET',
		    				url: 'options',
		    				data: $optionList,			    				
		    				statusCode: {
		    					200: function($data) {
		    						$callBack($data.data);		    						
		    					},			    				
		    					403: function($data) {
		    						$("#useridMsg").html($data.responseJSON.responseHeader.responseMessage);
		    					}, 
		    					404: function($data) {
		    						$("#globalMsg").html("System Error Option 404. Contact Support").show();
		    					}, 
		    					405: function($data) {
		    						$("#globalMsg").html("System Error Option 405. Contact Support").show();
		    					}, 
		    					500: function($data) {
		    						$("#globalMsg").html("System Error Option 500. Contact Support").show();
		    					}, 
		    				},
		    				dataType: 'json'
		    			});
		    		},
		            
		            
		            
		            getQuote : function($quoteId) {
						if ( $quoteId != null ) {
							var $url = "quote/" + $quoteId
							var jqxhr = $.ajax({
								type: 'GET',
								url: $url,
								data: {},
								statusCode: {
									200: function($data) {
										console.log("Got the quote");
										QUOTEMAINTENANCE.quote = $data.data.quoteList[0];
										QUOTEMAINTENANCE.populateQuotePanels($data.data);	
										if ( QUOTEMAINTENANCE.quote.canEdit == true ) {
											$(".edit-this-panel").show();
										}
									},					
									403: function($data) {
										$("#globalMsg").html("Session Expired. Log In and try again").show();
									},
									404: function($data) {
										$("#globalMsg").html("Invalid quote").show().fadeOut(4000);
									},
									500: function($data) {
										$("#globalMsg").html("System Error 500. Contact Support").show();
									}
								},
								dataType: 'json',
								async:false
							});
						}
					},
					

					
					getCodeList: function($tableName, $fieldName, $callback) {
						var $url = "code/" + $tableName;
						if ( $fieldName != null ) {
							$url = $url + "/" + $fieldName;
						}
						var jqxhr2 = $.ajax({
							type: 'GET',
							url: $url,
							data: {},							
							statusCode: {
								200: function($data) {
									$callback($data.data)
								},					
								403: function($data) {
									$("#globalMsg").html("Session Expired. Log In and try again").show();
								},
								404: function($data) {
									$("#globalMsg").html("Invalid quote").show().fadeOut(4000);
								},
								500: function($data) {
									$("#globalMsg").html("System Error 500. Contact Support").show();
								}
							},							
							dataType: 'json'
						});
					},
					
					
					
					incrementProgress : function($label) {
		            	var val = QUOTEMAINTENANCE.progressbar.progressbar("value") || 0;
						QUOTEMAINTENANCE.progressbar.progressbar("value", val+1);
						QUOTEMAINTENANCE.progressLabel.text( $label );
						console.log($label + ": " + QUOTEMAINTENANCE.progressbar.progressbar("value"));
					},
					
		    		
					
					
					init_modal : function() {						
						$( "#contact-edit-modal" ).dialog({
							title:'Edit Contact',
							autoOpen: false,
							height: 225,
							width: 500,
							modal: true,
							closeOnEscape:true,
							//open: function(event, ui) {
							//	$(".ui-dialog-titlebar-close", ui.dialog | ui).hide();
							//},
							buttons: [
								{
									id: "contact-cancel-button",
									click: function($event) {
										$( "#contact-edit-modal" ).dialog("close");
									}
								},
								{
									id: "contact-save-button",
									click: function($event) {
										QUOTEMAINTENANCE.saveContact();
									}
								}
							]
						});	
						$("#contact-save-button").button('option', 'label', 'Save');
						$("#contact-cancel-button").button('option', 'label', 'Cancel');
						
						
						
						
						
						
						$( "#address-edit-modal" ).dialog({
							title:'Edit Address',
							autoOpen: false,
							height: 300,
							width: 500,
							modal: true,
							closeOnEscape:true,
							//open: function(event, ui) {
							//	$(".ui-dialog-titlebar-close", ui.dialog | ui).hide();
							//},
							buttons: [
								{
									id: "address-cancel-button",
									click: function($event) {
										$( "#address-edit-modal" ).dialog("close");
									}
								},
								{
									id: "address-save-button",
									click: function($event) {
										QUOTEMAINTENANCE.saveAddress();
									}
								}
							]
						});	
						$("#address-save-button").button('option', 'label', 'Save');
						$("#address-cancel-button").button('option', 'label', 'Cancel');
					},
					
					
					
					
					makeAutoComplete : function() {
						QUOTEMAINTENANCE.makeAutoCompleteAddress();
						QUOTEMAINTENANCE.makeAutoCompleteContact();
					},
					
					
					
					
					makeAutoCompleteAddress : function() {
						var $selector = $("#address-edit-modal input[name='address-name']");						
				    	
						var $addressAutoComplete = $( $selector ).autocomplete({
							source: "addressTypeAhead?",
							select: function( event, ui ) {
								$( $selector ).val(ui.item.id);								
								console.log(ui.item);	
								ADDRESSUTILS.getAddress(ui.item.id, QUOTEMAINTENANCE.populateAddressModal);
	   				      	},
							response: function(event, ui) {
								if (ui.content.length === 0) {
									$("#address-edit-modal .none-found").show();
						        } else {
									$("#address-edit-display").hide();								
						        	$("#address-edit-modal .none-found").hide();
						        }
							}
						}); 	
					},
					
					
					
					makeAutoCompleteContact : function() {
						$selector = $("#contact-edit-modal input[name='contact-name']");
						$( $selector ).autocomplete({
							source:"contactTypeAhead?term=",
						    select: function( event, ui ) {
								console.log(ui.item);
								$("#contact-edit-modal").data("id",ui.item.id);
						    	
						    	var $contactSelector = "#contact-edit-display";
						    	$($contactSelector + " .ansi-contact-name").html(ui.item.value);
						    	var $preferred = ui.item.preferredContactValue.split(":");
						    	$($contactSelector + " .ansi-contact-number").html($preferred[1]);
						    	
						    	$($contactSelector + " .ansi-contact-method-is-business-phone").hide();
								$($contactSelector + " .ansi-contact-method-is-mobile-phone").hide();
								$($contactSelector + " .ansi-contact-method-is-fax").hide();
								$($contactSelector + " .ansi-contact-method-is-email").hide();
								if ( $preferred[0] == "business_phone") { $($contactSelector + " .ansi-contact-method-is-business-phone").show(); }
								if ( $preferred[0] == "mobile_phone") { $($contactSelector + " .ansi-contact-method-is-mobile-phone").show(); }
								if ( $preferred[0] == "fax") { $($contactSelector + " .ansi-contact-method-is-fax").show(); }
								if ( $preferred[0] == "email") { $($contactSelector + " .ansi-contact-method-is-email").show(); }
								$($contactSelector).show();
							},
						    response: function(event, ui) {
								if (ui.content.length === 0) {
									$("#contact-edit-modal .none-found").show();
						        } else {
									$("#contact-edit-display").hide();								
						        	$("#contact-edit-modal .none-found").hide();
						        }
						    }
						});
					},
					
					
					
					makeButtons : function() {
						$('.dateField').datepicker({
			                prevText:'&lt;&lt;',
			                nextText: '&gt;&gt;',
			                showButtonPanel:true
			            });
					},
					
					
					makeJobExpansion : function() {
						$(".jobTitleRow").click(function($event) {
							var $jobId = $(this).data("jobid");
							$openSelector = "#job" + $jobId + " .jobTitleRow .job-data-open";
							$closedSelector = "#job" + $jobId + " .jobTitleRow .job-data-closed";
							$tableSelector = "#job" + $jobId + " .job-data-row";
							$($tableSelector).toggle();
							$($closedSelector).toggle();
							$($openSelector).toggle();
							
							$detailSelector = "#job" + $jobId + " .job-data-row .job-detail-display";
							if ( ! $($detailSelector).length ) {
								QUOTEMAINTENANCE.getJobPanel($jobId);								
							}
							
						});
					},
					
					
					
					makeJobSort : function() {
						$("#jobList").sortable({
							stop:function($event, $ui) {
								var $jobId = $ui.item.attr("data-jobid");
								var $selector = "#job" + $jobId + " .jobTitleRow";
								$($selector).click();
							}
						});	
					},
					
					
					
					makeManagerList: function(){
	    				var $url = "user/manager";
	    				var jqxhr = $.ajax({
	    					type: 'GET',
	    					url: $url,
	    					data: {"sortBy":"firstName"},    // you can do firstName,lastName or email
	    					statusCode: {
	    						200: function($data) {
	    							QUOTEMAINTENANCE.managerList = $data.data.userList;
	    							QUOTEMAINTENANCE.populateManagerList($data.data.userList);
	    							QUOTEMAINTENANCE.incrementProgress("Manager List");
	    						},					
	    						403: function($data) {
	    							$("#globalMsg").html("Session Expired. Log In and try again").show();
	    						},
	    						404: function($data) {
	    							$("#globalMsg").html("System Error 404/Manager List. Contact Support").show();
	    						},
	    						500: function($data) {
	    							$("#globalMsg").html("System Error 500. Contact Support").show();
	    						}
	    					},
	    					dataType: 'json'
	    				});		    			
		    		},
		    		
		    		
		    		
		    		makeOptionLists : function(){
						QUOTEMAINTENANCE.getOptions('JOB_STATUS,JOB_FREQUENCY,COUNTRY,INVOICE_GROUPING,INVOICE_STYLE,INVOICE_TERM', QUOTEMAINTENANCE.populateOptions);
						QUOTEMAINTENANCE.incrementProgress("Job Status List");
						QUOTEMAINTENANCE.incrementProgress("Job Frequency List");
						
						
						QUOTEMAINTENANCE.getDivisionList(QUOTEMAINTENANCE.populateDivisionList);
						
						
						QUOTEMAINTENANCE.getCodeList("job", "building_type", QUOTEMAINTENANCE.populateBuildingType);
						QUOTEMAINTENANCE.incrementProgress("Building Type List");
						
						QUOTEMAINTENANCE.getCodeList("quote","account_type", QUOTEMAINTENANCE.populateAccountType); 
						QUOTEMAINTENANCE.incrementProgress("Account Type List");
						
						QUOTEMAINTENANCE.getCodeList("quote","lead_type", QUOTEMAINTENANCE.populateLeadType); 
						QUOTEMAINTENANCE.incrementProgress("Lead Type List");
						
						QUOTEMAINTENANCE.makeManagerList();	
		            },
		            
		            
		            
		            
		            
		            makeOtherClickables : function() {
						QUOTEMAINTENANCE.makeJobExpansion();

		    			$("#address-panel-hider").click(function($event) {
		    				$("#address-panel-open").toggle();
		    				$("#address-panel-closed").toggle();
		    				$("#addressPanel").toggle();
		    			});
		    			
		    			$("#quote-panel-hider").click(function($event) {
		    				$("#quote-panel-open").toggle();
		    				$("#quote-panel-closed").toggle();
		    				$("#quotePanel").toggle();
		    			});
		    			
		    			$("#copy-button").click(function($event) {
		    				var $quoteId = $(this).attr("data-quoteid");
		    				QUOTEMAINTENANCE.doCopyQuote($quoteId);
		    			});
		    			
		    			$("#revise-button").click(function($event) {
		    				var $quoteId = $(this).attr("data-quoteid");
		    				QUOTEMAINTENANCE.doReviseQuote($quoteId);
		    			});
		    			
		    			$("#print-button").click(function($event) {
		    				var $quoteId = $(this).attr("data-quoteid");
		    				var $quoteNumber = $(this).attr("data-quotenumber");
		    				//QUOTEMAINTENANCE.doReviseQuote($quoteId);
		    				console.log("Printing " + $quoteId);
		    				QUOTE_PRINT.showQuotePrint("#printQuoteDiv", $quoteId, $quoteNumber);
		    			});
		    			
		    			$("#edit-this-address .edit-address").click(function($event) {
		    				var $type = $(this).data("type");
		    				console.log("Editing addr: " + $type);
		    				var $title = "Edit Address";
		    				if ( $type == "jobsite") { $title = "Job Site"; }
		    				if ( $type == "billto") { $title = "Bill To"; }		    				
		    				$("#address-edit-modal input[name='address-name']").val("");
		    				$("#address-edit-modal").dialog("option","title",$title);
		    				$("#address-edit-modal .none-found").hide();
		    				$("#address-edit-display").hide();
		    				$("#address-edit-modal").data("type",$type);
				        	$("#address-edit-modal").data("id","");
		    				$("#address-edit-modal").dialog("open");
		    			});
		    			
		    			$("#edit-this-address .edit-contact").click(function($event) {
		    				var $type = $(this).data("type");
		    				var $title = "Edit Contact";
		    				if ( $type == "job") { $title = "Job Contact"; }
		    				if ( $type == "site") { $title = "Site Contact"; }
		    				if ( $type == "contract") { $title = "Contract Contact"; }
		    				if ( $type == "billing") { $title = "Billing Contact"; }
		    				$("#contact-edit-modal input[name='contact-name']").val("");
		    				$("#contact-edit-modal").dialog("option","title",$title);
		    				$("#contact-edit-modal .none-found").hide();
		    				$("#contact-edit-display").hide();
		    				$("#contact-edit-modal").data("type",$type);
				        	$("#contact-edit-modal").data("id","");
		    				$("#contact-edit-modal").dialog("open");
		    			});
		    		},
		    		
		    		
		    		
		    		makeProgressbar : function() {
		    			//var progressbar = $("#progressbar");
		    			//var progressLabel = $("#progress-label");
		    			QUOTEMAINTENANCE.progressbar.progressbar({
		    				value: false,
		    				change: function() {
		    					//progressLabel.text( progressbar.progressbar("value") + "%" );
		    					//console.log("progress value: " + QUOTEMAINTENANCE.progressbar.progressbar("value"));
		    				},
		    				complete: function() {
		    					console.log("Progress complete");
		    					QUOTEMAINTENANCE.progressLabel.text("Complete");
		    					//$("#progressbar").hide();
		    					QUOTEMAINTENANCE.showQuote();
		    				},
		    				max: 11
		    			});
		    		},
		    		
		    		
		    		
		    		
		    		populateAccountType : function($data) {
						QUOTEMAINTENANCE.accountTypeList = $data.codeList;
						$selectorName = "#quoteDataContainer select[name='accountType']";
						var $select = $($selectorName);
						$('option', $select).remove();

						$select.append(new Option("",""));
						$.each($data.codeList, function(index, val) {
						    $select.append(new Option(val.displayValue, val.value));
						});
					},
					
					
					
					
					populateAddressModal : function($statusCode, $data) {
						console.log("Populating address modal: " + $statusCode);
						console.log($data);
						if ( $statusCode == 200 ) {
							var $addressSelector = "#address-edit-display";
							$("#address-edit-modal").data("id",$data.data.addressList[0].addressId); //ui.item.id);	
							QUOTEMAINTENANCE.populateAddressPanel("#address-edit-display", $data.data.addressList[0]);
							$($addressSelector).show();
						}
						if ( $statusCode == 403 ) {
							$("#globalMsg").html("Session expired. Log in and try again").show();
							$("#address-edit-modal").dialog("close");
						}
						if ( $statusCode == 404 ) {
							$("#globalMsg").html("Address Error 404. Contact Support").show();
							$("#address-edit-modal").dialog("close");
						}
						if ( $statusCode == 500 ) {
							$("#globalMsg").html("Address Error 500. Contact Support").show();
							$("#address-edit-modal").dialog("close");
						}
					},
					
					
					populateAddressPanel : function($selectorId, $address) {
		            	$($selectorId + " .ansi-address-name").html($address.name);
		            	$($selectorId + " .ansi-address-address1").html($address.address1);
            			$($selectorId + " .ansi-address-address2").html($address.address2);
        				$($selectorId + " .ansi-address-city").html($address.city);
      					$($selectorId + " .ansi-address-state").html($address.state);
      					$($selectorId + " .ansi-address-zip").html($address.zip);
						$($selectorId + " .ansi-address-county").html($address.county);
 						$($selectorId + " .ansi-address-countryCode").html($address.countryCode);
		            },
		            
		            
		            
		            
		            populateBuildingType : function($data) {
						QUOTEMAINTENANCE.buildingTypeList = $data.codeList;
						$selectorName = "#quoteDataContainer select[name='buildingType']";
						var $select = $($selectorName);
						$('option', $select).remove();

						$select.append(new Option("",""));
						$.each($data.codeList, function(index, val) {
						    $select.append(new Option(val.displayValue, val.value));
						});
					},
					
					
					
					populateContactPanel : function($selector, $data) {
						$($selector + " .ansi-contact-name").html($data.firstName + " " + $data.lastName);						
						$($selector + " .ansi-contact-number").html($data.method);
						$($selector + " .ansi-contact-method-is-business-phone").hide();
						$($selector + " .ansi-contact-method-is-mobile-phone").hide();
						$($selector + " .ansi-contact-method-is-fax").hide();
						$($selector + " .ansi-contact-method-is-email").hide();
						if ( $data.preferredContact == "business_phone") { $($selector + " .ansi-contact-method-is-business-phone").show(); }
						if ( $data.preferredContact == "mobile_phone") { $($selector + " .ansi-contact-method-is-mobile-phone").show(); }
						if ( $data.preferredContact == "fax") { $($selector + " .ansi-contact-method-is-fax").show(); }
						if ( $data.preferredContact == "email") { $($selector + " .ansi-contact-method-is-email").show(); }
					},
					
					
					
					populateDivisionList : function($data) {
		            	QUOTEMAINTENANCE.divisionList = $data.divisionList
		            	
		            	var $select = $("#quoteDataContainer select[name='divisionId']");
						$('option', $select).remove();

						$select.append(new Option("",""));
						$.each(QUOTEMAINTENANCE.divisionList, function(index, val) {
						    $select.append(new Option(val.divisionNbr + "-" + val.divisionCode, val.divisionId));
						});
		            },
		            
		            
		            
		            populateJobHeader : function($jobHeaderList) {
		            	$.each($jobHeaderList, function($index, $value) {
		            		//$("#jobList").append('<li data-jobid="' + $value.jobId + '">' + $value.jobNbr + '</li>');
		            		$jobListItem = $("<li>");
		            		$jobListItem.attr("data-jobid", $value.jobId);
		            		$jobListItem.attr("id","job" + $value.jobId)

		            		
		            		$jobHeader = $("<div>");
		            		$jobHeader.attr("data-jobid", $value.jobId);
		            		$jobHeader.attr("class","jobTitleRow");
		            		
		            		
		            		$jobHeader.append('<span class="job-data-closed"><i class="fas fa-caret-right"></i></span>');
		            		$jobHeader.append('<span class="job-data-open"><i class="fas fa-caret-down"></i></span>');
		            		$jobHeader.append('&nbsp;');
		            		
		            		$jobDiv = $("<div>");
		            		$jobDiv.attr("class","job-header-job-div");
		            		$jobDiv.append('<span class="formLabel">Job: </span>');
		            		$jobDiv.append('<span>' + $value.jobNbr + '</span>');
		            		$jobHeader.append($jobDiv);
		            		
		            		$descDiv = $("<div>");
		            		$descDiv.attr("class","job-header-job-div");
		            		$descDiv.append($value.abbrDescription);
		            		$jobHeader.append($descDiv);
		            		
		            		$divDiv = $("<div>");
		            		$divDiv.attr("class","job-header-job-div");
		            		$divDiv.append('<span class="formLabel">Division: </span>');
		            		$divDiv.append('<span>' + $value.divisionNbr + '-' + $value.divisionCode + '</span>');
		            		$jobHeader.append($divDiv);
		            		
		            		$statusDiv = $("<div>");
		            		$statusDiv.attr("class","job-header-job-div");
		            		$statusDiv.append('<span class="formLabel">Status: </span>');
		            		$statusDiv.append('<span>' + $value.jobStatus +'</span>');
		            		$jobHeader.append($statusDiv);
		            		
		            		$freqDiv = $("<div>");
		            		$freqDiv.attr("class","job-header-job-div");
		            		$freqDiv.append('<span class="formLabel">Freq: </span>');
		            		$freqDiv.append('<span>' + $value.jobFrequency +'</span>');
		            		$jobHeader.append($freqDiv);
		            		
		            		$ppcDiv = $("<div>");
		            		$ppcDiv.attr("class","job-header-job-div");
		            		$ppcDiv.append('<span class="formLabel">PPC: </span>');
		            		$ppcDiv.append('<span>$' + $value.pricePerCleaning +'</span>');
		            		$jobHeader.append($ppcDiv);
		            		
		            		$detailDiv = $("#job-loading-pattern .job-data-row").clone()
		            		$detailDiv.attr("data-jobid", $value.jobId);
		            		
		            		$jobListItem.append($jobHeader);
		            		$jobListItem.append($detailDiv);
		            		$("#jobList").append($jobListItem);
		            	});	
		            },
		            
		            
		            populateJobPanel : function($jobId, $destination, $data) {		            	
		            	$($destination + " .jobProposalDisplayPanel .job-proposal-job-nbr").html($data.job.jobNbr);
		            	$($destination + " .jobProposalDisplayPanel .job-proposal-ppc").html("$" + $data.job.pricePerCleaning);
		            	$($destination + " .jobProposalDisplayPanel .job-proposal-freq").html($data.job.jobFrequency);
		            	$($destination + " .jobProposalDisplayPanel .job-proposal-desc").html($data.job.serviceDescription);
		            	
		            	$($destination + " .jobActivationDisplayPanel .job-activation-dl-pct").html($data.job.directLaborPct);
		            	$($destination + " .jobActivationDisplayPanel .job-activation-dl-budget").html($data.job.budget);
		            	$($destination + " .jobActivationDisplayPanel .job-activation-floors").html($data.job.floors);
		            	if ( $data.job.requestSpecialScheduling == 1 ) {
		            		$($destination + " .jobActivationDisplayPanel .job-activation-schedule").html("Manual");
		            	} else {
		            		$($destination + " .jobActivationDisplayPanel .job-activation-schedule").html("Auto");
		            	}
		            	$($destination + " .jobActivationDisplayPanel .job-activation-equipment").html($data.job.equipment);
		            	$($destination + " .jobActivationDisplayPanel .job-activation-washer-notes").html($data.job.washerNotes);
		            	$($destination + " .jobActivationDisplayPanel .job-activation-om-notes").html($data.job.omNotes);
		            	$($destination + " .jobActivationDisplayPanel .job-activation-billing-notes").html($data.job.billingNotes);

		            	$($destination + " .jobDatesDisplayPanel .job-dates-proposed-date").html($data.quote.proposalDate);
		            	$($destination + " .jobDatesDisplayPanel .job-dates-activation-date").html($data.job.activationDate);
		            	$($destination + " .jobDatesDisplayPanel .job-dates-start-date").html($data.job.startDate);
		            	$($destination + " .jobDatesDisplayPanel .job-dates-cancel-date").html($data.job.cancelDate);
		            	$($destination + " .jobDatesDisplayPanel .job-dates-cancel-reason").html($data.job.cancelReason);
		            			            	
		            	$($destination + " .jobInvoiceDisplayPanel .job-invoice-purchase-order").html($data.job.poNumber);
		            	$($destination + " .jobInvoiceDisplayPanel .job-invoice-vendor-nbr").html($data.job.ourVendorNbr);
		            	$($destination + " .jobInvoiceDisplayPanel .job-invoice-expire-date").html($data.job.expirationDate);
		            	$($destination + " .jobInvoiceDisplayPanel .job-invoice-expire-reason").html($data.job.expirationReason);
		            	
		            	$($destination + " .jobScheduleDisplayPanel .job-schedule-last-run").html($data.lastRun.startDate);		            	
		            	if ( $data.job.repeatScheduleAnnually == 1 ) {
		            		$($destination + " input[name='repeatedAnnually']").prop("checked", true);
		            	} else {
		            		$($destination + " input[name='repeatedAnnually']").prop("checked", false);
		            	}
		            	$($destination + " .jobScheduleDisplayPanel .job-schedule-next-due").html($data.nextDue.startDate);
		            	$($destination + " .jobScheduleDisplayPanel .job-schedule-created-thru").html($data.lastCreated.startDate);
		            	$($destination + " .jobScheduleDisplayPanel .job-schedule-ticket-list").attr("href", "ticketLookup.html?jobId="+$jobId);
		            	
		            	
		            	$($destination + " .jobAuditDisplayPanel .job-audit-created-by").html($data.job.addedFirstName + " " + $data.job.addedLastName);	
		            	$($destination + " .jobAuditDisplayPanel .job-audit-created-date").html($data.job.addedDate);	
		            	$($destination + " .jobAuditDisplayPanel .job-audit-updated-by").html($data.job.updatedFirstName + " " + $data.job.updatedLastName);	
		            	$($destination + " .jobAuditDisplayPanel .job-audit-updated-date").html($data.job.updatedDate);	
		            },
		            
		            
		            
		            populateLeadType : function($data) {
						QUOTEMAINTENANCE.leadTypeList = $data.codeList;
						$selectorName = "#quoteDataContainer select[name='leadType']";
						var $select = $($selectorName);
						$('option', $select).remove();

						$select.append(new Option("",""));
						$.each($data.codeList, function(index, val) {
						    $select.append(new Option(val.displayValue, val.value));
						});
					},
					
					
					
					
					populateManagerList : function($optionList) {
		            	var $select = $("#quoteDataContainer select[name='managerId']");
						$('option', $select).remove();

						$select.append(new Option("",""));
						$.each($optionList, function(index, val) {
						    $select.append(new Option(val.firstName + " " + val.lastName, val.userId));
						});
		            },
		            
		            
		            populateOptions : function($optionData) {
		            	QUOTEMAINTENANCE.countryList = $optionData.country;
		            	QUOTEMAINTENANCE.invoiceGroupingList = $optionData.invoiceGrouping;
						QUOTEMAINTENANCE.invoiceStyleList = $optionData.invoiceStyle;
						QUOTEMAINTENANCE.invoiceTermList = $optionData.invoiceTerm;
						QUOTEMAINTENANCE.jobStatusList = $optionData.jobStatus;
						QUOTEMAINTENANCE.jobFrequencyList = $optionData.jobFrequency;
						
						QUOTEMAINTENANCE.populateOptionSelects();
		            },
		            
		            
		            
		            populateOptionSelects : function() {
		            	QUOTEMAINTENANCE.incrementProgress("Country List");
						
						
						var $select = $("#quoteDataContainer select[name='invoiceTerms']");
						$('option', $select).remove();
						$select.append(new Option("",""));
						$.each(QUOTEMAINTENANCE.invoiceTermList, function(index, val) {
						    $select.append(new Option(val.display, val.abbrev));
						});
						QUOTEMAINTENANCE.incrementProgress("Invoice Terms");
						
						var $select = $("#quoteDataContainer select[name='invoiceStyle']");
						$('option', $select).remove();
						$select.append(new Option("",""));
						$.each(QUOTEMAINTENANCE.invoiceStyleList, function(index, val) {
						    $select.append(new Option(val.display, val.abbrev));
						});
						QUOTEMAINTENANCE.incrementProgress("Invoice Style List");
						
						
						var $select = $("#quoteDataContainer select[name='invoiceGrouping']");
						$('option', $select).remove();
						$select.append(new Option("",""));
						$.each(QUOTEMAINTENANCE.invoiceGroupingList, function(index, val) {
						    $select.append(new Option(val.display, val.abbrev));
						});
						QUOTEMAINTENANCE.incrementProgress("Invoice Grouping List");
		            },
		            
		            
		            populateQuotePanel : function($quote) {
		            	$("#printHistoryDiv").attr("data-quoteid", $quote.quoteId);	//this is so the gethistory method has an id to work with
		            
		            	$("#quoteDataContainer input[name='quoteId']").val($quote.quoteId);
		            	$("#quoteDataContainer select[name='managerId']").val($quote.managerId);
		            	$("#quoteDataContainer select[name='divisionId']").val($quote.divisionId);
		            	$("#quoteDataContainer .quoteNbrDisplay").html($quote.quoteNumber);
		            	$("#quoteDataContainer .revisionDisplay").html($quote.revision);
		            	
		            	$("#quoteDataContainer select[name='accountType']").val($quote.accountType);
		            	$("#quoteDataContainer select[name='invoiceTerms']").val($quote.invoiceTerms);
		            	$("#quoteDataContainer .proposedDate").html($quote.proposalDate);
		            	$("#quoteDataContainer select[name='leadType']").val($quote.leadType);
		            	$("#quoteDataContainer select[name='invoiceStyle']").val($quote.invoiceStyle);
		            	// ***** $("#quoteDataContainer input[name='signedBy']").val($quote.divisionId);
		            	$("#quoteDataContainer input[name='signedByContactId']").val($quote.signedByContactId);
		            	$("#quoteDataContainer select[name='buildingType']").val($quote.buildingType);
		            	$("#quoteDataContainer select[name='invoiceGrouping']").val($quote.invoiceGrouping);
		            	
		            	var $invoiceBatch = $quote.invoiceBatch == 1;
		            	$("#quoteDataContainer input[name='invoiceBatch']").prop("checked", $invoiceBatch);
		            	var $taxExempt = $quote.taxExempt == 1;
		            	$("#quoteDataContainer input[name='taxExempt']").prop("checked", $taxExempt);
		            	$("#quoteDataContainer input[name='taxExemptReason']").val($quote.taxExemptReason);
		            	
		            	$("#quoteDataContainer .printCount").html($quote.printCount);
		            	<%--
		            	"address": null,
						"billToAddressId": 52568,
						"copiedFromQuoteId": null,
						"jobSiteAddressId": 52567,
						"status": null,
						"templateId": 0,
						"managerFirstName": "Cameron",
						"managerLastName": "McDaniel",
						"managerEmail": "clm@ansi.com",
						"divisionCode": "IN03",
						--%>
		            },
		            
		            
		            
		            populateQuotePanels : function($data) {
	        			var $canPopulate = true;
	        			
	        			if ( QUOTEMAINTENANCE.accountTypeList == null ) { $canPopulate=false; }
	        			if ( QUOTEMAINTENANCE.countryList == null ) { $canPopulate=false; }
       					if ( QUOTEMAINTENANCE.buildingTypeList == null ) { $canPopulate=false; }
    					if ( QUOTEMAINTENANCE.divisionList == null ) { $canPopulate=false; }
    					if ( QUOTEMAINTENANCE.invoiceGroupingList == null ) { $canPopulate=false; }
    					if ( QUOTEMAINTENANCE.invoiceStyleList == null ) { $canPopulate=false; }
    					if ( QUOTEMAINTENANCE.invoiceTermList == null ) { $canPopulate=false; }
    					if ( QUOTEMAINTENANCE.jobStatusList == null ) { $canPopulate=false; }
    					if ( QUOTEMAINTENANCE.jobFrequencyList == null ) { $canPopulate=false; }
  						if ( QUOTEMAINTENANCE.leadTypeList == null ) { $canPopulate=false; }
						if ( QUOTEMAINTENANCE.managerList == null ) { $canPopulate=false; }
						if ( QUOTEMAINTENANCE.quoteId != null && QUOTEMAINTENANCE.quoteId != "" && QUOTEMAINTENANCE.quote == null ) { $canPopulate = false; }
						
						
	        			if ( $canPopulate == true ) {
	        				$(".action-button").attr("data-quoteid",$data.quoteList[0].quote.quoteId); //This is so copy/revise buttons know what to copy/revise
							$(".action-button").attr("data-quotenumber",$data.quoteList[0].quote.quoteNumber + $data.quoteList[0].quote.revision);
							QUOTEMAINTENANCE.populateQuotePanel($data.quoteList[0].quote);
							QUOTEMAINTENANCE.populateAddressPanel( "#address-bill-to", $data.quoteList[0].billTo);
							QUOTEMAINTENANCE.populateAddressPanel( "#address-job-site", $data.quoteList[0].jobSite);
							QUOTEMAINTENANCE.populateContactPanel( "#job-contact", $data.quoteList[0].jobContact.jobContact);
							QUOTEMAINTENANCE.populateContactPanel( "#site-contact", $data.quoteList[0].jobContact.siteContact);
							QUOTEMAINTENANCE.populateContactPanel( "#billing-contact", $data.quoteList[0].jobContact.billingContact);
							QUOTEMAINTENANCE.populateContactPanel( "#contract-contact", $data.quoteList[0].jobContact.contractContact);
							QUOTEMAINTENANCE.populateJobHeader($data.quoteList[0].jobHeaderList)
							QUOTEMAINTENANCE.makeJobExpansion();
	        			} else {
	        				setTimeout(function() {
	            				QUOTEMAINTENANCE.populateQuotePanels($data);
	            			},1000);
	        			}
					},
					
					
					
					saveAddress : function() {						
						var $addressType = $("#address-edit-modal").data("type");
						var $addressId = $("#address-edit-modal").data("id");
						console.log("Saving an address: " + $addressType + " " + $addressId);

						// this maps the address type we use in the jquery to the field in the quote update request object
						var $addressLabels = {
								"jobsite":"jobSiteAddressId",
								"billto":"billToAddressId"
						}
					
						var $quoteId = QUOTEMAINTENANCE.quote.quote.quoteId;
						var $addressLabel = $addressLabels[$addressType];
						var $outbound = {};
						$outbound["quoteId"]=$quoteId;
						$outbound[$addressLabel]=$addressId;
						QUOTEMAINTENANCE.doQuoteUpdate($quoteId, $outbound, QUOTEMAINTENANCE.saveAddressSuccess, QUOTEMAINTENANCE.saveAddressErr);
					},
					
					
					
					saveAddressErr : function($statusCode) {
						var $messages = {
								403:"Session Expired. Log in and try again",
								404:"System Error Address 404. Contact Support",
								500:"System Error Address 500. Contact Support"
						}
						$("#address-edit-modal").dialog("close");
						$("#globalMsg").html( $messages[$statusCode] );
					},
					
					
					
					saveAddressSuccess : function($data) {
						console.log($data);
						var $type = $("#address-edit-modal").data("type");
						if ( $data.responseHeader.responseCode == 'EDIT_FAILURE') {							
							if ( $type == 'jobsite' ) {
								$message = $data.data.webMessages.jobSiteAddressId[0]; 
							} else if ( $type == 'billto' ) {
								$message = $data.data.webMessages.billToAddressId[0];
							} else {
								$message = "Unexpected Response. Contact Support: " + $type;
							}
							$("#address-edit-modal .errMsg").html($message).show().fadeOut(3000);
						} else {
							QUOTEMAINTENANCE.populateAddressPanel( "#address-bill-to", $data.data.quote.billTo);
							QUOTEMAINTENANCE.populateAddressPanel( "#address-job-site", $data.data.quote.jobSite);
							$("#globalMsg").html("Update Successful").fadeOut(3000);
							$("#address-edit-modal").dialog("close");
						}
					}, 
						
						
					saveContact : function() {
						var $contactLabels = {
								"contract":"contractContactId",
								"billing":"billingContactId",
								"job":"jobContactId",
								"site":"siteContact"
						}
					
						console.log(QUOTEMAINTENANCE.quote.quote);
						var $quoteId = QUOTEMAINTENANCE.quote.quote.quoteId;
						var $contactType = $("#contact-edit-modal").data("type");
						var $contactId = $("#contact-edit-modal").data("id");						
						var $contactLabel = $contactLabels[$contactType];
						var $outbound = {};
						$outbound["quoteId"]=$quoteId;
						$outbound[$contactLabel]=$contactId;
						QUOTEMAINTENANCE.doQuoteUpdate($quoteId, $outbound, QUOTEMAINTENANCE.saveContactSuccess, QUOTEMAINTENANCE.saveContactErr);
					},
					
					
					saveContactErr : function($statusCode) {
						var $messages = {
								403:"Session Expired. Log in and try again",
								404:"System Error Quote 404. Contact Support",
								500:"System Error Quote 500. Contact Support"
						}
						$("#contact-edit-modal").dialog("close");
						$("#globalMsg").html( $messages[$statusCode] );
					},
					
					
					saveContactSuccess : function($data) {
						console.log($data);
						var $type = $("#contact-edit-modal").data("type");
						if ( $data.responseHeader.responseCode == 'EDIT_FAILURE') {							
							if ( $type == 'job' ) {
								$message = $data.data.webMessages.jobContactId[0]; 
							} else if ( $type == 'site' ) {
								$message = $data.data.webMessages.siteContact[0]; 
							} else if ( $type == 'contract' ) {
								$message = $data.data.webMessages.contractContactId[0]; 
							} else if ( $type == 'billing' ) {
								$message = $data.data.webMessages.billingContactId[0]; 
							} else {
								$message = "Unexpected Response. Contact Support: " + $type;
							}
							$("#contact-edit-modal .errMsg").html($message).show().fadeOut(3000);
						} else {
							QUOTEMAINTENANCE.populateContactPanel( "#job-contact", $data.data.quote.jobContact.jobContact);
							QUOTEMAINTENANCE.populateContactPanel( "#site-contact", $data.data.quote.jobContact.siteContact);
							QUOTEMAINTENANCE.populateContactPanel( "#billing-contact", $data.data.quote.jobContact.billingContact);
							QUOTEMAINTENANCE.populateContactPanel( "#contract-contact", $data.data.quote.jobContact.contractContact);
							$("#globalMsg").html("Update Successful").fadeOut(3000);
							$("#contact-edit-modal").dialog("close");
						}
						
					},
					
					
					showQuote : function() {
		            	$("#loading-container").hide();
						$("#quote-container").fadeIn(1000);
						$("#address-container").fadeIn(1000);
						$("#job-list-container").fadeIn(1000);
						$("#quoteButtonContainer").fadeIn(1000);
		            }
				};
				
				QUOTEMAINTENANCE.init();
				
				
        	});
         
        </script>
         
        
        
        <style type="text/css">   
        	#address-container {
        		display:none;
        	}     	
        	#addressPanel {
        		width:100%;
        	}
        	#address-panel-closed {
        		display:none;
        	}
        	#address-panel-open {
        	}
        	#contact-edit-modal .none-found {
        		display:none;
        	}
        	#job-loading-pattern {
        		display:none;
        	}
        	#job-list-container {
        		display:none;
        	}
        	#jobList li {
        		margin-top:2px;
        	}
			#jobList .job-data-row {
				display:none;
			}
			#jobList .job-data-open {
				display:none;
			}
			#printHistoryDiv {
                display:none;
            }
			#printQuoteDiv {
                display:none;
            }     
			#progressbar .ui-progressbar-value {
				background-color:#C9C9C9;
			}       
            #progress-label {
				position: absolute;
				left:50%;
				top:4px;
				font-weight:bold;
				text-shadow: 1px 1px 0 #fff;
			}
        	#quoteButtonContainer {
        		display:none;
        		float:right;
        		text-align:right;
        	}
        	#quote-container {
        		display:none;
        	}
        	#quote-panel-closed {
        		display:none;
        	}
        	#quote-panel-open {
        	}
			#quotePanel {
				border:solid 1px #000000;
				padding:8px;
				width:1300px;
			}
            #viewPrintHistory {
                cursor:pointer;
            }
        	.action-button {
        		cursor:pointer;
        	}
        	.ansi-address-container {
        		width:90%;
        	}
        	.ansi-address-form-label-container {
        		width:125px;
        	}
        	.ansi-contact-container {
        		width:90%;
        	}
        	.ansi-contact-method-is-business-phone { display:none; } 
			.ansi-contact-method-is-mobile-phone { display:none; }
			.ansi-contact-method-is-fax { display:none; }
			.ansi-contact-method-is-email { display:none; }
			.edit-modal {
        		display:none;
        	}
			
			.edit-this-panel {
				display:none;
			}
        	.job-data-row {
        		display:none;
        	}
			.jobTitleRow {
				background-color:#404040; 
				cursor:pointer; 
				padding-left:4px;
				color:#FFFFFF;
			}
			.job-data-closed {
				color:#FFFFFF;
			}
			.job-data-open {
				color:#FFFFFF;
			}
			.job-header-job-div {
				display:inline; 
				margin-right:10px;
			}
			.panel-button-container {
				float:right; 
				margin-right:8px;
				width:6%; 
				background-color:#e5e5e5; 
				border:solid 1px #404040; 
				text-align:center;
			}
			.quote-button {
				margin-top:2px;
				margin-bottom:2px;
				padding-top:2px;
				padding-bottom:2px;
			}
			.spacer {
				font-size:1px;
				clear:both;
				width:100%;
			}
			.ui-progressbar {
				position:relative;
			}
        </style>
    </tiles:put>
    
    
    <tiles:put name="content" type="string">
    	<h1>Quote Maintenance</h1>
    	<%--
    	<div id="loading-container"><webthing:thinking style="width:100%" /></div>
    	<div style="width:1200px;">
    		<div id="progressbar"><div id="progress-label">Loading...</div></div>
    	</div>
    	--%>
    	<div id="loading-container">
    		<div id="progressbar"><div id="progress-label">Loading...</div></div>
    	</div>
    	<div style="width:1300px;">	    	
    		<div id="quoteButtonContainer" style="width:30px;">
    			<%-- <ansi:hasPermission permissionRequired="QUOTE_CREATE"><webthing:edit styleClass="fa-2x quote-button">Edit</webthing:edit></ansi:hasPermission>--%>
 			    <ansi:hasPermission permissionRequired="QUOTE_CREATE"><webthing:revise styleClass="fa-2x quote-button action-button" styleId="revise-button">Revise</webthing:revise></ansi:hasPermission>
    			<ansi:hasPermission permissionRequired="QUOTE_CREATE"><webthing:copy styleClass="fa-2x quote-button action-button" styleId="copy-button">Copy</webthing:copy></ansi:hasPermission>
    			<ansi:hasPermission permissionRequired="QUOTE_READ"><a href="quoteLookup.html" style="text-decoration:none; color:#404040;"><webthing:view styleClass="fa-2x quote-button">Lookup</webthing:view></a></ansi:hasPermission>
    			<ansi:hasPermission permissionRequired="QUOTE_CREATE"><webthing:addNew styleClass="fa-2x quote-button">New</webthing:addNew></ansi:hasPermission>
    			<ansi:hasPermission permissionRequired="QUOTE_READ"><webthing:print styleClass="orange fa-2x quote-button action-button" styleId="print-button">Preview</webthing:print></ansi:hasPermission>    			
    			<ansi:hasPermission permissionRequired="QUOTE_PROPOSE"><webthing:print styleClass="green fa-2x quote-button action-button" styleId="print-button">Propose</webthing:print></ansi:hasPermission>    			
    			<%--
    			<input type="button" class="quoteButton" id="buttonModifyQuote" value="Modify" /><br />
    			<input type="button" class="quoteButton" id="buttonCopyQuote" value="Copy" /><br />
    			<input type="button" class="quoteButton" id="buttonNewQuote" value="New" /><br />
    			 --%>
	    	</div>
	    	<div id="address-container">
		    	<div style="color:#FFFFFF; background-color:#404040; cursor:pointer; width:1269px; margin-bottom:1px;">
		    		<div class="panel-button-container">
		    			<%-- <webthing:edit styleId="edit-this-address" styleClass="edit-this-panel">Edit</webthing:edit> --%>
		    			<quote:editAddressMenu styleId="edit-this-address" styleClass="edit-this-panel" />
		    		</div>
		    		<div id="address-panel-hider">
		    		Addresses
	    			<div style="float:left; padding-left:4px;">
	    				<span id="address-panel-closed"><i class="fas fa-caret-right" style="color:#FFFFFF;"></i>&nbsp;</span>
	    				<span id="address-panel-open"><i class="fas fa-caret-down" style="color:#FFFFFF;"></i>&nbsp;</span>
	    			</div>
	    			</div>
	    		</div>
		    	<div id="addressPanel" style="width:1269px; float:left;">
		    		<div id="addressContainerBillTo" style="float:right; width:50%; border:solid 1px #404040;">
		    			<quote:addressDisplayPanel label="Bill To" id="address-bill-to" />
		    			<div id="billToContactContainer" style="width:80%;">
		    				<quote:addressContact label="Contract Contact" id="contract-contact" />
		    				<quote:addressContact label="Billing Contact" id="billing-contact" />
		    			</div>
		    		</div>
		    		<div id="addressContainerJobSite" style="float:left; width:49%; border:solid 1px #404040;">
		    			<quote:addressDisplayPanel label="Job Site" id="address-job-site" />
		    			<div id="jobSiteContactContainer" style="width:80%;">
		    				<quote:addressContact label="Job Contact" id="job-contact" />
		    				<quote:addressContact label="Site Contact" id="site-contact" />
		    			</div>
		    		</div>
		    		<div class="spacer">&nbsp;</div>
		    	</div>
	    	</div>  <!-- Address container -->
	    	<div id="quote-container" style="width:1260px; clear:left; margin-top:12px;">
		    	<div id="quote-panel-hider" style="color:#FFFFFF; background-color:#404040; cursor:pointer; width:1269px; margin-bottom:1px;">
		    		Quote
		    		<div class="panel-button-container">
		    			<webthing:edit styleId="edit-this-quote" styleClass="edit-this-panel">Edit</webthing:edit>
		    		</div>
	    			<div style="float:left; padding-left:4px;">
	    				<span id="quote-panel-closed"><i class="fas fa-caret-right" style="color:#FFFFFF;"></i>&nbsp;</span>
	    				<span id="quote-panel-open"><i class="fas fa-caret-down" style="color:#FFFFFF;"></i>&nbsp;</span>
	    			</div>
	    		</div>
		    	<div id="quotePanel" style="width:1251px; clear:left;">
		    		<jsp:include page="quoteMaintenance/quoteDataContainer.jsp">
		    			<jsp:param name="action" value="view" />
		    		</jsp:include>
		    		<div class="spacer">&nbsp;</div>
		    	</div> 
	    	</div>  <!--  quote container -->
	    	<div id="job-list-container" style="width:1260px; clear:both; margin-top:12px;">
	    		<ul id="jobList" class="sortable" style="width:100%;">	    			
	    		</ul>
	    	</div>
	    </div>
	    <div class="spacer">&nbsp;</div>
	    
	    
	    <div id="job-loading-pattern">
	    	<div class="job-data-row"><webthing:thinking style="width:100%" /></div>
	    </div>
	    
	    
		<webthing:quotePrint modalName="printQuoteDiv" />
		
	    <webthing:quotePrintHistory modalName="printHistoryDiv" />
	    
	    
	    <ansi:hasPermission permissionRequired="QUOTE_CREATE">
		    <div id="contact-edit-modal" class="edit-modal">		    	
		    	<span class="formLabel">Name:</span> <input type="text" name="contact-name" />
		    	<span class="errMsg err"></span>
		    	<br /><hr /><br />
		    	<quote:addressContact id="contact-edit-display" label="Name" />
		    	<div class="none-found">
		    		<span class="err">No Matching Contacts</span>
		    	</div>
		    </div>
	    </ansi:hasPermission>
	    
	    
	    <ansi:hasPermission permissionRequired="QUOTE_CREATE">
		    <div id="address-edit-modal" class="edit-modal">		    	
		    	<span class="formLabel">Address:</span> <input type="text" name="address-name" />
		    	<span class="errMsg err"></span>
		    	<br /><hr /><br />
		    	<quote:addressDisplayPanel id="address-edit-display" label="Name" />
		    	<div class="none-found">
		    		<span class="err">No Matching Addresses</span>
		    	</div>
		    </div>
	    </ansi:hasPermission>
	    
	    
	    
	    <ansi:hasPermission permissionRequired="QUOTE_CREATE">
	    <script type="text/javascript">
	    $(document).ready(function() {
 			QUOTEMAINTENANCE.makeJobSort();
		});
	    </script>
		</ansi:hasPermission>
	    
    </tiles:put>

</tiles:insert>

