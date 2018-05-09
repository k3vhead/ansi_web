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
        <%--
        <script type="text/javascript" src="js/jobMaintenance.js"></script>
        <script type="text/javascript" src="js/quoteMaintenance.js"></script>
        <script type="text/javascript" src="js/quotePrint.js"></script>
        <script type="text/javascript" src="js/addressUtils.js"></script>
        <script type="text/javascript" src="js/quotePrintHistory.js"></script>
         --%>
        <script type="text/javascript">        
        
        	$(document).ready(function() {
				; QUOTEMAINTENANCE = {
					quoteId : '<c:out value="${ANSI_QUOTE_ID}" />',
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
					
					
					init : function() {
						QUOTEMAINTENANCE.makeOptionLists();
						QUOTEMAINTENANCE.populateOptionSelects();
						QUOTEMAINTENANCE.makeButtons();
						QUOTEMAINTENANCE.makeOtherClickables();
						if (QUOTEMAINTENANCE.quoteId != '' ) {
							QUOTEMAINTENANCE.getQuote(QUOTEMAINTENANCE.quoteId);	
						}
						$("#loading-container").hide();
						$("#quotePanel").fadeIn(1000);
						$("#address-container").fadeIn(1000);
						$("#job-list-container").fadeIn(1000);
						//console.debug("country");
						//console.debug(QUOTEMAINTENANCE.countryList);
						console.debug("buildingTypeList");
						console.debug(QUOTEMAINTENANCE.buildingTypeList);
						//console.debug("divisionList");
						//console.debug(QUOTEMAINTENANCE.divisionList);
						//console.debug("invoiceGroupingList");
						//console.debug(QUOTEMAINTENANCE.invoiceGroupingList);
						//console.debug("invoiceStyleList");
						//console.debug(QUOTEMAINTENANCE.invoiceStyleList);
						//console.debug("invoiceTermList");
						//console.debug(QUOTEMAINTENANCE.invoiceTermList);
						//console.debug("jobStatusList");
						//console.debug(QUOTEMAINTENANCE.jobStatusList);
						console.debug("jobFrequencyList");
						console.debug(QUOTEMAINTENANCE.jobFrequencyList);
						console.debug("leadTypeList");
						console.debug(QUOTEMAINTENANCE.leadTypeList);
						console.debug("managerList");
						console.debug(QUOTEMAINTENANCE.managerList);
					},
					
					
					code2options : function($selectorName, $optionList, $selectedValue) {						
						var $select = $($selectorName);
						$('option', $select).remove();

						$select.append(new Option("",""));
						$.each($optionList, function(index, val) {
						    $select.append(new Option(val.displayValue, val.value));
						});
						
						if ( $selectedValue != null ) {
							$select.val($selectedValue);
						}	
					},
					
					
					getDivisionList: function($callback) {
						var $returnValue = null;
						var jqxhr3 = $.ajax({
							type: 'GET',
							url: 'division/list',
							data: {},
							statusCode: {
								200:function($data) {
									$callback($data.data);
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
						return $returnValue;
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
										QUOTEMAINTENANCE.populateQuotePanel($data.data.quoteList[0].quote);
										QUOTEMAINTENANCE.populateAddressPanel( "#billToAddress", $data.data.quoteList[0].billTo);
										QUOTEMAINTENANCE.populateAddressPanel( "#jobSiteAddress", $data.data.quoteList[0].jobSite);
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
					
					
					makeButtons : function() {
						$('.dateField').datepicker({
			                prevText:'&lt;&lt;',
			                nextText: '&gt;&gt;',
			                showButtonPanel:true
			            });
					},
					
					
					makeCodeList: function($tableName, $fieldName, $function, $selectorName) {
						var $returnValue = null;
						var $url = "code/" + $tableName;
						if ( $fieldName != null ) {
							$url = $url + "/" + $fieldName;
						}
						var jqxhr2 = $.ajax({
							type: 'GET',
							url: $url,
							data: {},
							success: function($data) {
								return $data.data;
							},
							statusCode: {
								200: function($data) {
									$function($selectorName, $data.data.codeList, null)
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
					
					
					
		    		
					makeJobExpansion : function() {
						$(".jobTitleRow").click(function($event) {
							var $jobId = $(this).data("jobid");
							console.debug("Clicked: " + $jobId);
							var $tableSelector = "#job" + $jobId + " .job-data-row";
							var $closedSelector = "#job" + $jobId + " .job-data-closed";
							var $openSelector = "#job" + $jobId + " .job-data-open";
							$($tableSelector).toggle();
							$($closedSelector).toggle();
							$($openSelector).toggle();
						});
					},
					
					
					
					makeJobSort : function() {
						$("#jobList").sortable({
							stop:function($event, $ui) {
								console.debug($ui.item);
								console.debug($ui.item.attr("data-jobid"));
								var $jobId = $ui.item.attr("data-jobid");
								var $selector = "#job" + $jobId + " .jobTitleRow";
								console.debug($selector);
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
	    							QUOTEMAINTENANCE.populateManagerList($data.data.userList);
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
						QUOTEMAINTENANCE.getDivisionList(QUOTEMAINTENANCE.populateDivisionList);
						
						QUOTEMAINTENANCE.makeManagerList();	
						QUOTEMAINTENANCE.makeCodeList("job","building_type", QUOTEMAINTENANCE.code2options, "#quoteDataContainer select[name='buildingType']");
						QUOTEMAINTENANCE.makeCodeList("quote","account_type", QUOTEMAINTENANCE.code2options, "#quoteDataContainer select[name='accountType']");
						QUOTEMAINTENANCE.makeCodeList("quote","lead_type", QUOTEMAINTENANCE.code2options, "#quoteDataContainer select[name='leadType']");
		            },
		            
		            
		            
		            
		            
		            makeOtherClickables : function() {
						QUOTEMAINTENANCE.makeJobSort();
						QUOTEMAINTENANCE.makeJobExpansion();

		    			$("#address-panel-hider").click(function($event) {
		    				$("#address-panel-open").toggle();
		    				$("#address-panel-closed").toggle();
		    				$("#addressPanel").toggle();
		    			});
		    		},
		    		
		    		
		    		
		    		
		            populateAddressPanel : function($selectorId, $address) {
		            	console.debug("Populating address: " + $selectorId);	
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
						QUOTEMAINTENANCE.invoiceStyleList = $optionData.invoiceTerm;
						QUOTEMAINTENANCE.invoiceTermList = $optionData.invoiceStyle;
						QUOTEMAINTENANCE.jobStatusList = $optionData.jobStatus;
						QUOTEMAINTENANCE.jobFrequencyList = $optionData.jobFrequencyList;
		            },
		            
		            
		            
		            populateOptionSelects : function() {
						var $select = $("#quoteDataContainer select[name='invoiceTerms']");
						$('option', $select).remove();
						$select.append(new Option("",""));
						$.each(QUOTEMAINTENANCE.invoiceTermList, function(index, val) {
						    $select.append(new Option(val.display, val.abbrev));
						});
						
						var $select = $("#quoteDataContainer select[name='invoiceStyle']");
						$('option', $select).remove();
						$select.append(new Option("",""));
						$.each(QUOTEMAINTENANCE.invoiceStyleList, function(index, val) {
						    $select.append(new Option(val.display, val.abbrev));
						});
						
						var $select = $("#quoteDataContainer select[name='invoiceGrouping']");
						$('option', $select).remove();
						$select.append(new Option("",""));
						$.each(QUOTEMAINTENANCE.invoiceGroupingList, function(index, val) {
						    $select.append(new Option(val.display, val.abbrev));
						});
		            },
		            
		            
		            populateQuotePanel : function($quote) {
		            	
		            },
		            
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
        	#job-list-container {
        		display:none;
        	}
        	.ansi-address-container {
        		width:90%;
        	}
        	.ansi-contact-container {
        		width:90%;
        	}
        	#quoteButtonContainer {
        		float:right;
        		text-align:right;
        	}
			#quotePanel {
				display:none;
				border:solid 1px #000000;
				padding:8px;
				width:1300px;
			}
			#jobList .job-data-row {
				display:none;
			}
			#jobList .job-data-open {
				display:none;
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
        </style>
    </tiles:put>
    
    
    <tiles:put name="content" type="string">
    	<h1>Quote Maintenance</h1>
    	<div id="loading-container"><webthing:thinking style="width:100%" /></div>
    	<div style="width:1300px;">	    	
    		<div id="quoteButtonContainer" style="width:30px;">
    			<webthing:edit styleClass="fa-2x quote-button">Edit</webthing:edit>
    			<webthing:revise styleClass="fa-2x quote-button">Revise</webthing:revise>
    			<webthing:copy styleClass="fa-2x quote-button">Copy</webthing:copy>
    			<webthing:view styleClass="fa-2x quote-button">Search</webthing:view>
    			<webthing:addNew styleClass="fa-2x quote-button">New</webthing:addNew>
    			<webthing:print styleClass="fa-2x quote-button">Print</webthing:print>    			
    			<%--
    			<input type="button" class="quoteButton" id="buttonModifyQuote" value="Modify" /><br />
    			<input type="button" class="quoteButton" id="buttonCopyQuote" value="Copy" /><br />
    			<input type="button" class="quoteButton" id="buttonNewQuote" value="New" /><br />
    			 --%>
	    	</div>
	    	<div id="address-container">
		    	<div id="address-panel-hider" style="color:#FFFFFF; background-color:#404040; cursor:pointer; width:1269px; margin-bottom:1px;">
		    		Addresses
	    			<div style="float:left; padding-left:4px;">
	    				<span id="address-panel-closed"><i class="fas fa-caret-right" style="color:#FFFFFF;"></i></span>
	    				<span id="address-panel-open"><i class="fas fa-caret-down" style="color:#FFFFFF;"></i></span>
	    			</div>
	    		</div>
		    	<div id="addressPanel" style="width:1269px; float:left;">
		    		<div id="addressContainerBillTo" style="float:right; width:50%; border:solid 1px #404040;">
		    			<quote:addressDisplayPanel label="Bill To" id="addressBillTo" />
		    			<div id="billToContactContainer" style="width:80%;">
		    				<quote:addressContact label="Contract Contact" id="contractContact" />
		    				<quote:addressContact label="Billing Contact" id="billingContact" />
		    			</div>
		    		</div>
		    		<div id="addressContainerJobSite" style="float:left; width:49%; border:solid 1px #404040;">
		    			<quote:addressDisplayPanel label="Job Site" id="addressJobSite" />
		    			<div id="jobSiteContactContainer" style="width:80%;">
		    				<quote:addressContact label="Job Contact" id="jobContact" />
		    				<quote:addressContact label="Site Contact" id="siteContact" />
		    			</div>
		    		</div>
		    		<div class="spacer">&nbsp;</div>
		    	</div>
	    	</div>  <!-- Address container -->
	    	<div id="quotePanel" style="width:1251px; clear:left;">
	    		<jsp:include page="quoteMaintenance/quoteDataContainer.jsp">
	    			<jsp:param name="action" value="view" />
	    		</jsp:include>
	    		<div class="spacer">&nbsp;</div>
	    	</div> 
	    	<div id="job-list-container" style="width:1260px; clear:both; margin-top:12px;">
	    		<ul id="jobList" class="sortable" style="width:100%;">
	    			<li data-jobid="1">
	    				<jsp:include page="quoteMaintenance/jobDisplay.jsp">
	    					<jsp:param name="jobid" value="1" />
	    				</jsp:include>
	    			</li>
	    			<li data-jobid="2">
	    				<jsp:include page="quoteMaintenance/jobDisplay.jsp">
	    					<jsp:param name="jobid" value="2" />
	    				</jsp:include>
	    			</li>
	    			<li data-jobid="3">
	    				<jsp:include page="quoteMaintenance/jobDisplay.jsp">
	    					<jsp:param name="jobid" value="3" />
	    				</jsp:include>
	    			</li>
	    			<li data-jobid="4">
	    				<jsp:include page="quoteMaintenance/jobDisplay.jsp">
	    					<jsp:param name="jobid" value="4" />
	    				</jsp:include>
	    			</li>
	    		</ul>
	    	</div>
	    </div>   	
	    <div class="spacer">&nbsp;</div>
    </tiles:put>

</tiles:insert>

