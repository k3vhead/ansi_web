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
        New Quote
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
				; NEWQUOTE = {
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

					// pieces of the quote that are required
					jobSiteAddress : null,
					billToAddress : null,
					jobsiteJobContact : null,
					jobsiteSiteContact : null,
					billtoContractContact : null,
					billtoBillingContact : null,
					divisionId : null,
					managerId : null,
					leadType : null,
					
					// pieces of the quote that are not required
					accountType : null,
					buildingType : null,
					invoiceTerms : null,
					invoiceStyle : null,
					invoiceGrouping : null,
					invoiceBatch : null,
					taxExempt : null,
					taxExemptReason : null,
				
					
					
					joblist : {},
					
					progressbar : $("#progressbar"),
					progressLabel : $("#progress-label"),
					
					
					init : function() {
						console.log("init");
						NEWQUOTE.makeProgressbar();
						NEWQUOTE.init_modal();
						NEWQUOTE.makeAutoComplete();
						NEWQUOTE.makeOptionLists();
						NEWQUOTE.makeButtons();
						NEWQUOTE.makeOtherClickables();
						if (NEWQUOTE.quoteId != '' ) {
							NEWQUOTE.getQuote(NEWQUOTE.quoteId);	
						}
						QUOTE_PRINT.init_modal("#printQuoteDiv");
						//QUOTE_PRINT_HISTORY.init("#printHistoryDiv", "#viewPrintHistory");
						//$("#loading-container").hide();
						//$("#quotePanel").fadeIn(1000);
						//$("#address-container").fadeIn(1000);
						//$("#job-list-container").fadeIn(1000);						
					},
					
					
					

					
					
					
					doQuoteUpdate : function($data, $successCallback, $errCallback) {
						console.log("doQuoteUpdate");
						var $outbound = JSON.stringify($data);
						var $url = "quote/new";
						console.log($outbound);
						
						
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
					
					
					
					
	    			
	    			
	    			
	    			
					getDivisionList : function($callback) {
						console.log("getDivisionList");
						var jqxhr3 = $.ajax({
							type: 'GET',
							url: 'division/list',
							data: {},
							statusCode: {
								200:function($data) {
									$callback($data.data);
									NEWQUOTE.incrementProgress("Division List");
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
					
					
					
					
					
					getOptions : function($optionList, $callBack) {
						console.log("getOptions");
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
		            	console.log("getQuote");
						if ( $quoteId != null ) {
							var $url = "quote/" + $quoteId
							var jqxhr = $.ajax({
								type: 'GET',
								url: $url,
								data: {},
								statusCode: {
									200: function($data) {
										console.log("Got the quote");
										NEWQUOTE.quote = $data.data.quote;
										NEWQUOTE.populateQuotePanels($data.data);	
										if ( NEWQUOTE.quote.canEdit == true ) {
											$("#edit-this-address").show();
											$(".quote-button-container").show();
											$("#edit-this-quote").show();
										} 
										if ( NEWQUOTE.quote.canAddJob == true ) {
											$("#new-job-button").show();
										} else {
											$("#new-job-button").hide();
										}
										if ( NEWQUOTE.quote.canPropose == true ) {
											$("#propose-button").show();
										} else {
											$("#propose-button").hide();
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
						console.log("getCodeList");
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
						console.log("incrementProgress");
		            	var val = NEWQUOTE.progressbar.progressbar("value") || 0;
						NEWQUOTE.progressbar.progressbar("value", val+1);
						NEWQUOTE.progressLabel.text( $label );
						console.log($label + ": " + NEWQUOTE.progressbar.progressbar("value"));
					},
					
		    		
					
					
					init_modal : function() {
						console.log("init_modal");
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
										NEWQUOTE.saveContact();
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
										location.href="quoteLookup.html";
									}
								},
								{
									id: "address-save-button",
									click: function($event) {
										NEWQUOTE.saveAddress();
									}
								}
							]
						});	
						$("#address-save-button").button('option', 'label', 'Save');
						$("#address-cancel-button").button('option', 'label', 'Cancel');

						
					},
					
					
					
					
					
					
					
					
					
					makeAutoComplete : function() {
						console.log("makeAutoComplete");
						NEWQUOTE.makeAutoCompleteAddress();
						NEWQUOTE.makeAutoCompleteContact();
						NEWQUOTE.makeAutoCompleteSignedBy();
					},
					
					
					
					
					makeAutoCompleteAddress : function() {
						console.log("makeAutoCompleteAddress");
						var $selector = $("#address-edit-modal input[name='address-name']");						
				    	
						var $addressAutoComplete = $( $selector ).autocomplete({
							source: "addressTypeAhead?",
							select: function( event, ui ) {
								$( $selector ).val(ui.item.id);								
								console.log(ui.item);	
								ADDRESSUTILS.getAddress(ui.item.id, NEWQUOTE.populateAddressModal);
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
						console.log("makeAutoCompleteContact");
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
					
					
					
					makeAutoCompleteSignedBy : function() {
						$selector = $("#quotePanel input[name='signedBy']");
						$( $selector ).autocomplete({
							source:"contactTypeAhead?term=",
						    select: function( event, ui ) {
								console.log(ui.item);
								//$("#contact-edit-modal").data("id",ui.item.id);
						    	$("#quotePanel input[name='signedBy']").attr("id", ui.item.id);
						    	//var $contactSelector = "#contact-edit-display";
						    	//$($contactSelector + " .ansi-contact-name").html(ui.item.value);
						    	//var $preferred = ui.item.preferredContactValue.split(":");
						    	//$($contactSelector + " .ansi-contact-number").html($preferred[1]);
						    	
						    	//$($contactSelector + " .ansi-contact-method-is-business-phone").hide();
								//$($contactSelector + " .ansi-contact-method-is-mobile-phone").hide();
								//$($contactSelector + " .ansi-contact-method-is-fax").hide();
								//$($contactSelector + " .ansi-contact-method-is-email").hide();
								//if ( $preferred[0] == "business_phone") { $($contactSelector + " .ansi-contact-method-is-business-phone").show(); }
								//if ( $preferred[0] == "mobile_phone") { $($contactSelector + " .ansi-contact-method-is-mobile-phone").show(); }
								//if ( $preferred[0] == "fax") { $($contactSelector + " .ansi-contact-method-is-fax").show(); }
								//if ( $preferred[0] == "email") { $($contactSelector + " .ansi-contact-method-is-email").show(); }
								//$($contactSelector).show();
							},
						    response: function(event, ui) {
								if (ui.content.length === 0) {
									alert("No matches");
						        } else {
									//$("#contact-edit-display").hide();								
						        	//$("#contact-edit-modal .none-found").hide();
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
					
					
					
					
					
					
					
					
					
					makeManagerList: function(){
	    				var $url = "user/manager";
	    				var jqxhr = $.ajax({
	    					type: 'GET',
	    					url: $url,
	    					data: {"sortBy":"firstName"},    // you can do firstName,lastName or email
	    					statusCode: {
	    						200: function($data) {
	    							NEWQUOTE.managerList = $data.data.userList;
	    							NEWQUOTE.populateManagerList($data.data.userList);
	    							NEWQUOTE.incrementProgress("Manager List");
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
						NEWQUOTE.getOptions('JOB_STATUS,JOB_FREQUENCY,COUNTRY,INVOICE_GROUPING,INVOICE_STYLE,INVOICE_TERM', NEWQUOTE.populateOptions);
						NEWQUOTE.incrementProgress("Job Status List");
						NEWQUOTE.incrementProgress("Job Frequency List");
						
						
						NEWQUOTE.getDivisionList(NEWQUOTE.populateDivisionList);
						
						
						NEWQUOTE.getCodeList("job", "building_type", NEWQUOTE.populateBuildingType);
						NEWQUOTE.incrementProgress("Building Type List");
						
						NEWQUOTE.getCodeList("quote","account_type", NEWQUOTE.populateAccountType); 
						NEWQUOTE.incrementProgress("Account Type List");
						
						NEWQUOTE.getCodeList("quote","lead_type", NEWQUOTE.populateLeadType); 
						NEWQUOTE.incrementProgress("Lead Type List");
						
						NEWQUOTE.makeManagerList();	
		            },
		            
		            
		            
		            
		            
		            makeOtherClickables : function() {
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
		    			
		    			$("#edit-this-quote").click(function($event) {
		    				console.log("Editing a quote");
		    				$("#quotePanel input").prop("disabled", false);
		    				$("#quotePanel select").prop("disabled", false);
		    				$("#edit-this-quote").hide();
		    				$("#quote-container .quote-button-container .save-quote").show();
		    				$("#quote-container .quote-button-container .cancel-edit").show();
		    			});
		    			
		    			
		    			$("#quote-container .quote-button-container .cancel-edit").click(function($event) {
		    				console.log("Cancel Editing a quote");
		    				NEWQUOTE.populateQuotePanel();
		    				$("#quotePanel input").prop("disabled", true);
		    				$("#quotePanel select").prop("disabled", true);
		    				$("#quotePanel select").removeClass("edit-err");
						    $("#quotePanel input").removeClass("edit-err");
		    				$("#edit-this-quote").show();
		    				$("#quote-container .quote-button-container .save-quote").hide();
		    				$("#quote-container .quote-button-container .cancel-edit").hide();
		    			});
		    			
		    			
		    			$("#quote-container .quote-button-container .save-quote").click(function($event) {
		    				console.log("Saving quote header");
		    				NEWQUOTE.saveQuoteHeader();
		    			});
		    			
		    			
		    			$("#save-quote-button").click(function($event) {
		    				NEWQUOTE.saveTheNewQuote();
		    			});
		    		},
		    		
		    		
		    		
		    		makeProgressbar : function() {
		    			//var progressbar = $("#progressbar");
		    			//var progressLabel = $("#progress-label");
		    			NEWQUOTE.progressbar.progressbar({
		    				value: false,
		    				change: function() {
		    					//progressLabel.text( progressbar.progressbar("value") + "%" );
		    					//console.log("progress value: " + NEWQUOTE.progressbar.progressbar("value"));
		    				},
		    				complete: function() {
		    					console.log("Progress complete");
		    					NEWQUOTE.progressLabel.text("Complete");
		    					//$("#progressbar").hide();
		    					NEWQUOTE.showQuote();
		    				},
		    				max: 11
		    			});
		    		},
		    		
		    		
		    		
		    		
		    		populateAccountType : function($data) {
						NEWQUOTE.accountTypeList = $data.codeList;
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
							NEWQUOTE.populateAddressPanel("#address-edit-display", $data.data.addressList[0]);
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
						console.log("populateAddressPanel");
						console.log($selectorId);
						console.log($address);
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
						NEWQUOTE.buildingTypeList = $data.codeList;
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
					
					
					
					populateDefaultQuoteHeader : function() {
						console.log("populateDefaultQuoteHeader");
						if ( NEWQUOTE.jobSiteAddress.invoiceStyleDefault != null ) {
							NEWQUOTE.invoiceStyle = NEWQUOTE.jobSiteAddress.invoiceStyleDefault;
							$("#quoteDataContainer select[name='invoiceStyle']").val(NEWQUOTE.jobSiteAddress.invoiceStyleDefault);
						}	
						if ( NEWQUOTE.jobSiteAddress.invoiceGroupingDefault != null ) {
							NEWQUOTE.invoiceGrouping = NEWQUOTE.jobSiteAddress.invoiceGroupingDefault
							$("#quoteDataContainer select[name='invoiceGrouping']").val(NEWQUOTE.jobSiteAddress.invoiceGroupingDefault);
						}
						if ( NEWQUOTE.jobSiteAddress.invoiceBatchDefault != null ) {
							NEWQUOTE.invoiceBatch = NEWQUOTE.jobSiteAddress.invoiceBatchDefault;
							var $invoiceBatch = NEWQUOTE.jobSiteAddress.invoiceBatchDefault == 1;
							$("#quoteDataContainer input[name='invoiceBatch']").prop("checked", $invoiceBatch);
						}
						if ( NEWQUOTE.jobSiteAddress.invoiceTermsDefault != null ) {
							NEWQUOTE.invoiceTerms = NEWQUOTE.jobSiteAddress.invoiceTermsDefault;
							$("#quoteDataContainer select[name='invoiceTerms']").val(NEWQUOTE.jobSiteAddress.invoiceTermsDefault);
						}
						if ( NEWQUOTE.jobSiteAddress.jobsiteBuildingTypeDefault != null ) {
							NEWQUOTE.buildingType = NEWQUOTE.jobSiteAddress.jobsiteBuildingTypeDefault;
							$("#quoteDataContainer select[name='buildingType']").val(NEWQUOTE.jobSiteAddress.jobsiteBuildingTypeDefault);
						}
						if ( NEWQUOTE.jobSiteAddress.billtoAccountTypeDefault != null ) {
							NEWQUOTE.accountType = NEWQUOTE.jobSiteAddress.billtoAccountTypeDefault;
							$("#quoteDataContainer select[name='accountType']").val(NEWQUOTE.jobSiteAddress.billtoAccountTypeDefault);
						}
						if ( NEWQUOTE.jobSiteAddress.billToTaxExempt != null ) {
							NEWQUOTE.taxExempt = NEWQUOTE.jobSiteAddress.billToTaxExempt;
							var $taxExempt = NEWQUOTE.jobSiteAddress.billToTaxExempt == 1;
			            	$("#quoteDataContainer input[name='taxExempt']").prop("checked", $taxExempt);
			            }
						if ( NEWQUOTE.jobSiteAddress.billToTaxExemptReason != null ) {
							NEWQUOTE.taxExemptReason = NEWQUOTE.jobSiteAddress.billToTaxExemptReason;
							$("#quoteDataContainer input[name='taxExemptReason']").val(NEWQUOTE.jobSiteAddress.billToTaxExemptReason);
						}
		            	
					},
					
					
					
					populateDivisionList : function($data) {
		            	NEWQUOTE.divisionList = $data.divisionList
		            	
		            	var $select = $("#quoteDataContainer select[name='divisionId']");
						$('option', $select).remove();

						$select.append(new Option("",""));
						$.each(NEWQUOTE.divisionList, function(index, val) {
						    $select.append(new Option(val.divisionNbr + "-" + val.divisionCode, val.divisionId));
						});
		            },
		            
		            
		            
		            populateLeadType : function($data) {
						NEWQUOTE.leadTypeList = $data.codeList;
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
		            	NEWQUOTE.countryList = $optionData.country;
		            	NEWQUOTE.invoiceGroupingList = $optionData.invoiceGrouping;
						NEWQUOTE.invoiceStyleList = $optionData.invoiceStyle;
						NEWQUOTE.invoiceTermList = $optionData.invoiceTerm;
						NEWQUOTE.jobStatusList = $optionData.jobStatus;
						NEWQUOTE.jobFrequencyList = $optionData.jobFrequency;
						
						NEWQUOTE.populateOptionSelects();
		            },
		            
		            
		            
		            populateOptionSelects : function() {
		            	NEWQUOTE.incrementProgress("Country List");
						
						
						var $select = $("#quoteDataContainer select[name='invoiceTerms']");
						$('option', $select).remove();
						$select.append(new Option("",""));
						$.each(NEWQUOTE.invoiceTermList, function(index, val) {
						    $select.append(new Option(val.display, val.abbrev));
						});
						NEWQUOTE.incrementProgress("Invoice Terms");
						
						var $select = $("#quoteDataContainer select[name='invoiceStyle']");
						$('option', $select).remove();
						$select.append(new Option("",""));
						$.each(NEWQUOTE.invoiceStyleList, function(index, val) {
						    $select.append(new Option(val.display, val.abbrev));
						});
						NEWQUOTE.incrementProgress("Invoice Style List");
						
						
						var $select = $("#quoteDataContainer select[name='invoiceGrouping']");
						$('option', $select).remove();
						$select.append(new Option("",""));
						$.each(NEWQUOTE.invoiceGroupingList, function(index, val) {
						    $select.append(new Option(val.display, val.abbrev));
						});
						NEWQUOTE.incrementProgress("Invoice Grouping List");
		            },
		            
		            
		            populateQuotePanel : function() {
		            	console.log("populating quote panel");
		            
		            	//$("#printHistoryDiv").attr("data-quoteid", $quote.quote.quoteId);	//this is so the gethistory method has an id to work with
		            
		            	//$("#quoteDataContainer input[name='quoteId']").val($quote.quote.quoteId);
		            	$("#quoteDataContainer select[name='managerId']").val(NEWQUOTE.managerId);
		            	$("#quoteDataContainer select[name='divisionId']").val(NEWQUOTE.divisionId);
		            	//$("#quoteDataContainer .quoteNbrDisplay").html($quote.quote.quoteNumber);
		            	//$("#quoteDataContainer .revisionDisplay").html($quote.quote.revision);
		            	
		            	//if ( $quote.quote.copiedFromQuoteId != null ) {
		            	//	var $copyLink = '(Previous: <a href="quoteMaintenance.html?id=' + $quote.quote.copiedFromQuoteId + '" style="color:#404040">'+$quote.quote.copiedFromQuoteNbrRev+'</a>)'
		            	//	$("#quoteDataContainer .quoteCopyDisplay").html($copyLink);
		            	//}
		            	
		            	$("#quoteDataContainer select[name='accountType']").val(NEWQUOTE.accountType);
		            	$("#quoteDataContainer select[name='invoiceTerms']").val(NEWQUOTE.invoiceTerms);
		            	$("#quoteDataContainer .proposedDate").html(NEWQUOTE.proposalDate);
		            	$("#quoteDataContainer select[name='leadType']").val(NEWQUOTE.leadType);
		            	$("#quoteDataContainer select[name='invoiceStyle']").val(NEWQUOTE.invoiceStyle);
		            	//if ( $quote.signedBy != null ) {
		            	//	$("#quoteDataContainer input[name='signedBy']").val($quote.signedBy.firstName + " " + $quote.signedBy.lastName);
		            	//	$("#quoteDataContainer input[name='signedBy']").attr("id", $quote.signedBy.contactid);
		            	//}		            	
		            	//$("#quoteDataContainer input[name='signedByContactId']").val($quote.quote.signedByContactId);
		            	$("#quoteDataContainer select[name='buildingType']").val(NEWQUOTE.buildingType);
		            	$("#quoteDataContainer select[name='invoiceGrouping']").val(NEWQUOTE.invoiceGrouping);
		            	
		            	var $invoiceBatch = NEWQUOTE.invoiceBatch == 1;
		            	$("#quoteDataContainer input[name='invoiceBatch']").prop("checked", $invoiceBatch);
		            	var $taxExempt = NEWQUOTE.taxExempt == 1;
		            	$("#quoteDataContainer input[name='taxExempt']").prop("checked", $taxExempt);
		            	$("#quoteDataContainer input[name='taxExemptReason']").val(NEWQUOTE.taxExemptReason);
		            	
		            	//$("#quoteDataContainer .printCount").html($quote.quote.printCount);		            	
		            },
		            
		            
		            
		            populateQuotePanels : function($data) {
	        			var $canPopulate = true;
	        			
	        			if ( NEWQUOTE.accountTypeList == null ) { $canPopulate=false; }
	        			if ( NEWQUOTE.countryList == null ) { $canPopulate=false; }
       					if ( NEWQUOTE.buildingTypeList == null ) { $canPopulate=false; }
    					if ( NEWQUOTE.divisionList == null ) { $canPopulate=false; }
    					if ( NEWQUOTE.invoiceGroupingList == null ) { $canPopulate=false; }
    					if ( NEWQUOTE.invoiceStyleList == null ) { $canPopulate=false; }
    					if ( NEWQUOTE.invoiceTermList == null ) { $canPopulate=false; }
    					if ( NEWQUOTE.jobStatusList == null ) { $canPopulate=false; }
    					if ( NEWQUOTE.jobFrequencyList == null ) { $canPopulate=false; }
  						if ( NEWQUOTE.leadTypeList == null ) { $canPopulate=false; }
						if ( NEWQUOTE.managerList == null ) { $canPopulate=false; }
						if ( NEWQUOTE.quoteId != null && NEWQUOTE.quoteId != "" && NEWQUOTE.quote == null ) { $canPopulate = false; }
						
						
	        			if ( $canPopulate == true ) {
	        				$(".action-button").attr("data-quoteid",$data.quote.quote.quoteId); //This is so copy/revise buttons know what to copy/revise
							$(".action-button").attr("data-quotenumber",$data.quote.quote.quoteNumber + $data.quote.quote.revision);
							NEWQUOTE.populateQuotePanel($data.quote);
							NEWQUOTE.populateAddressPanel( "#address-bill-to", $data.quote.billTo);
							NEWQUOTE.populateAddressPanel( "#address-job-site", $data.quote.jobSite);
							NEWQUOTE.populateContactPanel( "#job-contact", $data.quote.jobContact.jobContact);
							NEWQUOTE.populateContactPanel( "#site-contact", $data.quote.jobContact.siteContact);
							NEWQUOTE.populateContactPanel( "#billing-contact", $data.quote.jobContact.billingContact);
							NEWQUOTE.populateContactPanel( "#contract-contact", $data.quote.jobContact.contractContact);
							NEWQUOTE.populateJobHeader($data.quote.jobHeaderList)
							NEWQUOTE.makeJobExpansion();
							
							// when the page loads the first time, check for a job id
							// if it is there, expand that job
							// then delete the input parameter so it doesn't get accidentally referenced later
							if ( NEWQUOTE.jobId != null ) {
								$.each( $("#jobList li .job-hider"), function($index, $value) {
									var $jobid = $($value).attr("data-jobid");
									if ( $jobid == NEWQUOTE.jobId ) {
										$($value).click();
									}
								});
								NEWQUOTE.jobId = null;
							}
	        			} else {
	        				setTimeout(function() {
	            				NEWQUOTE.populateQuotePanels($data);
	            			},1000);
	        			}
					},
					
					
					
					saveAddress : function() {	
						console.log("saveAddress");
						var $addressType = $("#address-edit-modal").data("type");
						var $addressId = $("#address-edit-modal").data("id");
						console.log("Saving an address: " + $addressType + " " + $addressId);

						// this maps the address type we use in the jquery to the field in the quote update request object
						var $addressLabels = {
								"jobsite":"jobSiteAddressId",
								"billto":"billToAddressId"
						}
						console.log("NEWQUOTE.quote");
						console.log(NEWQUOTE.quote);
						var $addressLabel = $addressLabels[$addressType];
						var $outbound = {};
						$outbound[$addressLabel]=$addressId;
						$outbound['action'] = 'validate';
						NEWQUOTE.doQuoteUpdate($outbound, NEWQUOTE.saveAddressSuccess, NEWQUOTE.saveAddressErr);
					},
					
					
					
					saveAddressErr : function($statusCode) {
						$("#save-quote-button").hide(2500);
						var $messages = {
								403:"Session Expired. Log in and try again",
								404:"System Error Address 404. Contact Support",
								500:"System Error Address 500. Contact Support"
						}
						$("#address-edit-modal").dialog("close");
						$("#globalMsg").html( $messages[$statusCode] );
					},
					
					
					
					saveAddressSuccess : function($data) {
						console.log("saveAddressSuccess");
						console.log($data);
						var $type = $("#address-edit-modal").data("type");
						if ( $data.responseHeader.responseCode == 'EDIT_FAILURE') {	
							$("#save-quote-button").hide(2500);
							if ( $type == 'jobsite' ) {
								$message = $data.data.webMessages.jobSiteAddressId[0]; 
							} else if ( $type == 'billto' ) {
								$message = $data.data.webMessages.billToAddressId[0];
							} else {
								$message = "Unexpected Response. Contact Support: " + $type;
							}
							$("#address-edit-modal .errMsg").html($message).show().fadeOut(3000);
						} else {
							console.log("Saving quote stuff from address update");
							console.log("We've got a '" + $type + "' address");
							if ( $type == 'jobsite' ) {
								// job site stuff
								NEWQUOTE.jobSiteAddress = $data.data.jobSiteAddress;								
								NEWQUOTE.populateAddressPanel( "#address-job-site", NEWQUOTE.jobSiteAddress);
								if ( $data.data.jobsiteSiteContact != null ) {
									NEWQUOTE.jobsiteSiteContact = $data.data.jobsiteSiteContact
									NEWQUOTE.populateContactPanel( "#site-contact", NEWQUOTE.jobsiteSiteContact);
								}
								if ( $data.data.jobsiteJobContact != null ) {
									NEWQUOTE.jobsiteJobContact = $data.data.jobsiteJobContact
									NEWQUOTE.populateContactPanel( "#job-contact", NEWQUOTE.jobsiteJobContact);
								}
								// billto stuff
								NEWQUOTE.billToAddress = $data.data.billToAddress
								NEWQUOTE.populateAddressPanel( "#address-bill-to", NEWQUOTE.billToAddress );
								if ( $data.data.billtoContractContact != null ) {
									NEWQUOTE.billtoContractContact = $data.data.billtoContractContact
									NEWQUOTE.populateContactPanel( "#contract-contact", NEWQUOTE.billtoContractContact);
								}
								if ( $data.data.billtoBillingContact != null ) {
									NEWQUOTE.billtoBillingContact = $data.data.billtoBillingContact
									NEWQUOTE.populateContactPanel( "#billing-contact", NEWQUOTE.billtoBillingContact);
								}
								NEWQUOTE.populateDefaultQuoteHeader();
							} else if ( $type == 'billto' ){
								// billto stuff
								NEWQUOTE.billToAddress = $data.data.billToAddress
								NEWQUOTE.populateAddressPanel( "#address-bill-to", NEWQUOTE.billToAddress );
								if ( $data.data.billtoContractContact != null ) {
									NEWQUOTE.billtoContractContact = $data.data.billtoContractContact
									NEWQUOTE.populateContactPanel( "#contract-contact", NEWQUOTE.billtoContractContact);
								}
								if ( $data.data.billtoBillingContact != null ) {
									NEWQUOTE.billtoBillingContact = $data.data.billtoBillingContact
									NEWQUOTE.populateContactPanel( "#billing-contact", NEWQUOTE.billtoBillingContact);
								}
							} else {
								$("#globalMsg").html( "Unknown address type " + $type + ". Contact support" );
							}
							$("#address-edit-modal").dialog("close");
							$("#edit-this-address").show();
							$(".quote-button-container").show();
							$("#edit-this-quote").show();
							
							NEWQUOTE.showNextModal();
						}
					}, 
						
						
					saveContact : function() {
						var $contactLabels = {
								"contract":"contractContactId",
								"billing":"billingContactId",
								"job":"jobContactId",
								"site":"siteContact"
						}
					
						var $contactType = $("#contact-edit-modal").data("type");
						var $contactId = $("#contact-edit-modal").data("id");						
						var $contactLabel = $contactLabels[$contactType];
						var $outbound = {};
						$outbound[$contactLabel]=$contactId;
						$outbound['action'] = 'validate';
						NEWQUOTE.doQuoteUpdate($outbound, NEWQUOTE.saveContactSuccess, NEWQUOTE.saveContactErr);
					},
					
					
					saveContactErr : function($statusCode) {
						$("#save-quote-button").hide(2500);
						var $messages = {
								403:"Session Expired. Log in and try again",
								404:"System Error Contact 404. Contact Support",
								500:"System Error Contact 500. Contact Support"
						}
						$("#contact-edit-modal").dialog("close");
						$("#globalMsg").html( $messages[$statusCode] );
					},
					
					
					saveContactSuccess : function($data) {
						console.log("save contact success");
						console.log($data);
						var $type = $("#contact-edit-modal").data("type");
						if ( $data.responseHeader.responseCode == 'EDIT_FAILURE') {	
							$("#save-quote-button").hide(2500);
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
							console.log("Saving quote stuff from contact update");
							var $message = "Update Successful";
							if ( $type == 'job' ) {
								NEWQUOTE.jobsiteJobContact = $data.data.contact;
								NEWQUOTE.populateContactPanel( "#job-contact", NEWQUOTE.jobsiteJobContact);
							} else if ( $type == 'site' ) {
								NEWQUOTE.jobsiteSiteContact = $data.data.contact;
								NEWQUOTE.populateContactPanel( "#site-contact", NEWQUOTE.jobsiteSiteContact);
							} else if ( $type == 'contract' ) {
								NEWQUOTE.billtoContractContact = $data.data.contact; 
								NEWQUOTE.populateContactPanel( "#billing-contact", NEWQUOTE.billtoContractContact);
							} else if ( $type == 'billing' ) {
								NEWQUOTE.billtoBillingContact = $data.data.contact; 
								NEWQUOTE.populateContactPanel( "#contract-contact", NEWQUOTE.billtoBillingContact);
							} else {
								$message = "Unexpected Response. Contact Support: " + $type;
							}
							$("#globalMsg").html($message).fadeOut(3000);
							$("#contact-edit-modal").dialog("close");	
							NEWQUOTE.showNextModal();
						}
						
					},
					
					
					
					

					
					saveQuoteHeader : function() {
	    				console.log("saveQuoteHeader");
	    				var $outbound = {};
	    				$.each( $("#quotePanel input"), function($index, $value) {
	    					if ( $value.name == 'signedBy') {
	    						$outbound['signedByContactId'] = $("#quotePanel input[name='signedBy']").attr("id");
	    					} else {
		    					$selector = "#quotePanel input[name='" + $value.name + "']";
		    					$outbound[$value.name] = $($selector).val();
	    					}
	    				});
	    				$.each( $("#quotePanel select"), function($index, $value) {
	    					$selector = "#quotePanel select[name='" + $value.name + "']";
	    					$outbound[$value.name] = $($selector).val();
	    				});
	    				$outbound['taxExempt'] = $("#quotePanel input[name='taxExempt']").prop("checked");
	    				$outbound['invoiceBatch'] = $("#quotePanel input[name='invoiceBatch']").prop("checked");
	    				
	    				console.log($outbound);
	    				$outbound['action'] = 'validate';
	    				NEWQUOTE.doQuoteUpdate($outbound, NEWQUOTE.saveQuoteHeaderSuccess, NEWQUOTE.saveQuoteHeaderErr);
					},
					
					
					
					saveQuoteHeaderErr : function($statusCode) {
						console.log("saveQuoteHeaderErr " + $statusCode)
						$("#save-quote-button").hide(2500);
						var $messages = {
								403:"Session Expired. Log in and try again",
								404:"System Error Quote 404. Contact Support",
								500:"System Error Quote 500. Contact Support"
						}
						$("#globalMsg").html( $messages[$statusCode] );
						$("#globalMsg").show();
						$("#quotePanel input").prop("disabled", true);
	    				$("#quotePanel select").prop("disabled", true);
	    				$("#quotePanel select").removeClass("edit-err");
					    $("#quotePanel input").removeClass("edit-err");
	    				$("#edit-this-quote").show();
	    				$("#quote-container .quote-button-container .save-quote").hide();
	    				$("#quote-container .quote-button-container .cancel-edit").hide();
					},
					
					
					saveQuoteHeaderSuccess : function($data) {
						if ( $data.responseHeader.responseCode == 'EDIT_FAILURE') {
							$("#save-quote-button").hide(2500);
							console.log($data);
							$.each($data.data.webMessages, function(index, val) {							    
							    // it's too much work to figure out if the field is input or select, so do both
							    $selector = "#quotePanel select[name='"+ index +"']";							    
							    $($selector).addClass("edit-err");
							    $selector = "#quotePanel input[name='"+ index +"']";							    
							    $($selector).addClass("edit-err");
							});
							alert("Invalid input. Correct the indicated fields and resubmit");
						} else {
							console.log("Update header success:");

							
							$.each( $("#quotePanel input"), function($index, $value) {
		    					if ( $value.name == 'signedBy') {
		    						// skip this one; we're not using it
		    					} else {
			    					$selector = "#quotePanel input[name='" + $value.name + "']";
			    					NEWQUOTE[$value.name] = $($selector).val();
		    					}
		    				});
		    				$.each( $("#quotePanel select"), function($index, $value) {
		    					$selector = "#quotePanel select[name='" + $value.name + "']";
		    					NEWQUOTE[$value.name] = $($selector).val();
		    				});
							
							
							
							$("#globalMsg").html("Update Successful").fadeOut(3000);
							$("#quotePanel input").prop("disabled", true);
		    				$("#quotePanel select").prop("disabled", true);
						    $("#quotePanel select").removeClass("edit-err");
						    $("#quotePanel input").removeClass("edit-err");
		    				$("#edit-this-quote").show();
		    				$("#quote-container .quote-button-container .save-quote").hide();
		    				$("#quote-container .quote-button-container .cancel-edit").hide();
		    				NEWQUOTE.showNextModal();
						}
						
						
					},
					
					
					
					
					
					saveTheNewQuote : function() {
						console.log("saveTheNewQuote")
						var $outbound = {}
						$outbound['action'] = 'save';
						
						// pieces of the quote that are required
						$.each(["jobSiteAddress","billToAddress","jobsiteJobContact","jobsiteSiteContact","billtoContractContact","billtoBillingContact","divisionId","managerId","leadType"], function($index, $value) {
							$outbound[$value] = NEWQUOTE[$value];
						});
						
						
						// pieces of the quote that are not required
						$.each(["accountType","buildingType","invoiceTerms","invoiceStyle","invoiceGrouping","invoiceBatch","taxExempt","taxExemptReason"], function($index, $value) {
							$outbound[$value] = NEWQUOTE[$value];
						});
						
						NEWQUOTE.doQuoteUpdate($outbound, NEWQUOTE.saveTheQuoteSuccess, NEWQUOTE.saveTheQuoteErr);
					},
					
					
					
					saveTheQuoteErr : function($data) {
						console.log("saveTheQuoteErr");
						$("#globalMsg").html("not coded yet -- hang tight for a few").show();
					},
					
					
					
					saveTheQuoteSuccess : function($data) {
						console.log("saveTheQuoteSuccess");
						$("#globalMsg").html("not coded yet -- hang tight for a few").show();
					},
					
					
					
					
					showNextModal : function() {
						if ( NEWQUOTE.jobSiteAddress == null) {
							$("#save-quote-button").hide(2500);
							var $type = "jobsite"
		    				$title = "Job Site";		    				
		    				$("#address-edit-modal input[name='address-name']").val("");
		    				$("#address-edit-modal").dialog("option","title",$title);
		    				$("#address-edit-modal .none-found").hide();
		    				$("#address-edit-display").hide();
		    				$("#address-edit-modal").data("type",$type);
				        	$("#address-edit-modal").data("id","");
		    				$("#address-edit-modal").dialog("open");							
						} else if ( NEWQUOTE.billToAddress == null) {
							$("#save-quote-button").hide(2500);
							var $type = "billto"
		    				$title = "Bill To";		    				
		    				$("#address-edit-modal input[name='address-name']").val("");
		    				$("#address-edit-modal").dialog("option","title",$title);
		    				$("#address-edit-modal .none-found").hide();
		    				$("#address-edit-display").hide();
		    				$("#address-edit-modal").data("type",$type);
				        	$("#address-edit-modal").data("id","");
		    				$("#address-edit-modal").dialog("open");
						} else if ( NEWQUOTE.jobsiteJobContact == null) {
							$("#save-quote-button").hide(2500);
		    				$("#contact-edit-modal input[name='contact-name']").val("");
		    				$("#contact-edit-modal").dialog("option","title","Job Contact");
		    				$("#contact-edit-modal .none-found").hide();
		    				$("#contact-edit-display").hide();
		    				$("#contact-edit-modal").data("type","job");
				        	$("#contact-edit-modal").data("id","");
		    				$("#contact-edit-modal").dialog("open");
						} else if ( NEWQUOTE.jobsiteSiteContact == null) {
							$("#save-quote-button").hide(2500);
		    				$("#contact-edit-modal input[name='contact-name']").val("");
		    				$("#contact-edit-modal").dialog("option","title","Site Contact");
		    				$("#contact-edit-modal .none-found").hide();
		    				$("#contact-edit-display").hide();
		    				$("#contact-edit-modal").data("type","site");
				        	$("#contact-edit-modal").data("id","");
		    				$("#contact-edit-modal").dialog("open");
						} else if ( NEWQUOTE.billtoContractContact == null) {
							$("#save-quote-button").hide(2500);
		    				$("#contact-edit-modal input[name='contact-name']").val("");
		    				$("#contact-edit-modal").dialog("option","title","Contract Contact");
		    				$("#contact-edit-modal .none-found").hide();
		    				$("#contact-edit-display").hide();
		    				$("#contact-edit-modal").data("type","contract");
				        	$("#contact-edit-modal").data("id","");
		    				$("#contact-edit-modal").dialog("open");
						} else if ( NEWQUOTE.billtoBillingContact == null) {
							$("#save-quote-button").hide(2500);
		    				$("#contact-edit-modal input[name='contact-name']").val("");
		    				$("#contact-edit-modal").dialog("option","title","Billing Contact");
		    				$("#contact-edit-modal .none-found").hide();
		    				$("#contact-edit-display").hide();
		    				$("#contact-edit-modal").data("type","billing");
				        	$("#contact-edit-modal").data("id","");
		    				$("#contact-edit-modal").dialog("open");
						} else if ( NEWQUOTE.divisionId == null ) {
							$("#save-quote-button").hide(2500);
							$("#edit-this-quote").click();
						} else if ( NEWQUOTE.managerId == null ) {
							$("#save-quote-button").hide(2500);
							$("#edit-this-quote").click();
						} else if ( NEWQUOTE.leadType == null ) {
							$("#save-quote-button").hide(2500);
							$("#edit-this-quote").click();
						} else {
							$("#save-quote-button").fadeIn(2500);
						}
						
					},
					
					
					
					showQuote : function() {
		            	$("#loading-container").hide();
						$("#quote-container").fadeIn(1000);
						$("#address-container").fadeIn(1000);
						$("#job-list-container").fadeIn(1000);
						$("#quoteButtonContainer").fadeIn(1000);
						
						// after init is done -- show bill-to modal
						var $type = "jobsite"
	    				$title = "Job Site";		    				
	    				$("#address-edit-modal input[name='address-name']").val("");
	    				$("#address-edit-modal").dialog("option","title",$title);
	    				$("#address-edit-modal .none-found").hide();
	    				$("#address-edit-display").hide();
	    				$("#address-edit-modal").data("type",$type);
			        	$("#address-edit-modal").data("id","");
	    				$("#address-edit-modal").dialog("open");
		            }
				};
				
				NEWQUOTE.init();
				
				
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
        	#edit-this-quote {
        		cursor:pointer;
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
			
			
			#quote-container .quote-button-container .save-quote {
				display:none;
				cursor:pointer;
			}
	    	#quote-container .quote-button-container .cancel-edit {
				display:none;
				cursor:pointer;
			}		
	    	
	    	#save-quote-button {
	    		display:none;
	    	}
	    			
	    			

        	.action-button {
        		cursor:pointer;
        	}
        	.address-button-container {
				float:right; 
				margin-right:8px;
				width:6%; 
				background-color:#e5e5e5; 
				border:solid 1px #404040; 
				text-align:center;
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
			.edit-modal { display:none; }
			.edit-this-panel { display:none; }
			.edit-err { 
				background-color:#FF0000; 
				opacity:0.20;
			}
			.panel-button-container {
				float:right; 
				margin-right:8px;
				width:6%; 
				background-color:#e5e5e5; 
				border:solid 1px #404040; 
				text-align:center;
				display:none;
			}
			.quote-button {
				margin-top:2px;
				margin-bottom:2px;
				padding-top:2px;
				padding-bottom:2px;
			}
			.quote-button-container {
				float:right; 
				margin-right:8px;
				width:6%; 
				background-color:#e5e5e5; 
				border:solid 1px #404040; 
				text-align:center;
				display:none;
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
    	<h1>New Quote</h1>
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
    			<ansi:hasPermission permissionRequired="QUOTE_READ"><a href="quoteLookup.html" style="text-decoration:none; color:#404040;"><webthing:view styleClass="fa-2x quote-button">Lookup</webthing:view></a></ansi:hasPermission>
    			<ansi:hasPermission permissionRequired="QUOTE_CREATE">
    				<a href="newQuote.html"><webthing:addNew styleClass="fa-2x quote-button action-button" styleId="new-quote-button">New Quote</webthing:addNew></a>
    				<webthing:save styleClass="fa-2x quote-button action-button" styleId="save-quote-button">Save</webthing:save>
    			</ansi:hasPermission>
	    	</div>
	    	<div id="address-container">
		    	<div style="color:#FFFFFF; background-color:#404040; cursor:pointer; width:1269px; margin-bottom:1px;">
		    		<div class="address-button-container">
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
	    		<div class="quote-button-container">
	    			<webthing:edit styleId="edit-this-quote" styleClass="edit-this-panel">Edit</webthing:edit>
	    			<webthing:save styleClass="save-quote">Save</webthing:save>
	    			<webthing:ban styleClass="cancel-edit">Cancel</webthing:ban>	    			
	    		</div>
		    	<div id="quote-panel-hider" style="color:#FFFFFF; background-color:#404040; cursor:pointer; width:1269px; margin-bottom:1px;">
		    		Quote
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
	    	
	    	
	    </div>
	    <div class="spacer">&nbsp;</div>
	    
	    
	    
	    
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
	    
	    
	    <jsp:include page="quoteModals.jsp" />
	    
	    
	    
	    
	    
    </tiles:put>

</tiles:insert>

