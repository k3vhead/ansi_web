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
<%@ taglib uri="WEB-INF/theTagThing.tld" prefix="ansi" %>


<tiles:insert page="layout.jsp" flush="true">

    <tiles:put name="title" type="string">
        Quote Maintenance
    </tiles:put>
    
    
    <tiles:put name="headextra" type="string">
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
						QUOTEMAINTENANCE.makeButtons();
						if (QUOTEMAINTENANCE.quoteId != '' ) {
							QUOTEMAINTENANCE.getQuote(QUOTEMAINTENANCE.quoteId);	
						}
						$("#loadingDiv").hide();
						$("#quotePanel").fadeIn(1000);
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
					
					
					
		    		
					makeOptionLists : function(){
						$optionData = ANSI_UTILS.getOptions('JOB_STATUS,JOB_FREQUENCY,COUNTRY,INVOICE_GROUPING,INVOICE_STYLE,INVOICE_TERM');
						QUOTEMAINTENANCE.countryList = $optionData.country;
						QUOTEMAINTENANCE.invoiceGroupingList = $optionData.invoiceGrouping;
						QUOTEMAINTENANCE.invoiceStyleList = $optionData.invoiceTerm;
						QUOTEMAINTENANCE.invoiceTermList = $optionData.invoiceStyle;
						QUOTEMAINTENANCE.jobStatusList = $optionData.jobStatus;
						QUOTEMAINTENANCE.jobFrequencyList = $optionData.jobFrequencyList;
						QUOTEMAINTENANCE.divisionList = ANSI_UTILS.getDivisionList();
						QUOTEMAINTENANCE.makeManagerList();	
						QUOTEMAINTENANCE.makeCodeList("job","building_type", QUOTEMAINTENANCE.code2options, "#quoteDataContainer select[name='buildingType']");
						QUOTEMAINTENANCE.makeCodeList("quote","account_type", QUOTEMAINTENANCE.code2options, "#quoteDataContainer select[name='accountType']");
						QUOTEMAINTENANCE.makeCodeList("quote","lead_type", QUOTEMAINTENANCE.code2options, "#quoteDataContainer select[name='leadType']");
						QUOTEMAINTENANCE.populateDivisionList(QUOTEMAINTENANCE.divisionList);
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
		    		
		    		
		            populateAddressPanel : function($selectorId, $address) {
		            	console.debug("Populating address: " + $selectorId);	
		            },
		            
		            
		            populateDivisionList : function($optionList) {
		            	var $select = $("#quoteDataContainer select[name='divisionId']");
						$('option', $select).remove();

						$select.append(new Option("",""));
						$.each($optionList, function(index, val) {
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
		            
		            
		            populateQuotePanel : function($quote) {
		            	
		            },
		            
				};
				
				QUOTEMAINTENANCE.init();
        	});
         
        </script>
         
        
        
        <style type="text/css">
        	#addressPanel {
        		width:100%;
        	}
        	#quoteButtonContainer {
        		float:left;
        		width:30px;
        		text-align:left;
        	}
        	#quoteDataContainer {
        		float:right;
        		width:1250px;
        	}
			#quotePanel {
				display:none;
				border:solid 1px #000000;
				padding:8px;
				width:1300px;
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
    	<div id="loadingDiv"><webthing:thinking style="width:100%" /></div>
    	<div id="quotePanel">
    		<table id="quoteDataContainer">
	    		<colgroup>
	    			<!-- Manager -->
		        	<col style="width:8px;" />  <!-- req -->
		        	<col style="width:120px;" />  <!-- label -->
		        	<col style="width:200px;" />  <!-- input -->
		        	<col style="width:10px;" />  <!-- err -->
		        	<col style="width:32px;" />	<!-- spacer -->
		        	
		    		<!-- Division -->
		        	<col style="width:8px;" />  <!-- req -->
		        	<col style="width:120px;" />  <!-- label -->
		        	<col style="width:200px;" />  <!-- input -->
		        	<col style="width:10px;" />  <!-- err -->
		        	<col style="width:32px;" />	<!-- spacer -->
		        	
		        	<!-- Quote -->
		        	<col style="width:8px;" />  <!-- req -->
		        	<col style="width:120px;" />  <!-- label -->
		        	<col style="width:200px;" />  <!-- input -->
		        	<col style="width:10px;" />  <!-- err -->
		        	<col style="width:32px;" />	<!-- spacer -->
		        	
		        	<!--  rest of it -->
		        	<col style="width:140px" />  <!--  icons -->		        	
	    		</colgroup> 
    			<tr>
    				<td><span class="required">*</span></td>
    				<td><span class="formLabel">Manager:</span></td>
    				<td><select name="managerId"></select></td>
    				<td><span class="err" id="managerIdErr"></span></td>
    				<td>&nbsp;</td>
    				
    				<td><span class="required">*</span></td>
    				<td><span class="formLabel">Division:</span></td>
    				<td><select name="divisionId"></select></td>
    				<td><span class="err" id="divisionIdErr"></span></td>
    				<td>&nbsp;</td>
    				
    				<td><span class="required"></span></td>
    				<td colspan="3">
    					<span class="formLabel">Quote:</span> 
    					<span id="quoteNbrDisplay">12345</span><span id="revisionDisplay">A</span>
    				</td>
    				<td>&nbsp;</td>
    				
    				
    				<td rowspan="4" style="text-align:center;">
    					<span class="fa-stack fa-2x tooltip" style="color:#444444;">
							<i class="fa fa-print fa-stack-2x" id="printButton" aria=hidden="true"><span class="tooltiptext">Print</span></i>
						</span>
						<br />
						<span class="fa-stack fa-2x tooltip" id="viewPrintHistory" style="color:#444444;">
							<i class="fa fa-list-alt fa-stack-2x"><span class="tooltiptext">Print History<br />Print Count</span></i>
							<i class="fa fa-stack-1x"><span style="color:#FFFFFF; text-shadow:-1px -1px 0 #000,1px -1px 0 #000,-1px 1px 0 #000, 1px 1px 0 #000; font-weight:bold;" id="printCount">N/A</span></i>
						</span>
    				</td>
    			</tr>
    			
    			<tr>
    				<td><span class="required">*</span></td>
    				<td><span class="formLabel">Account Type:</span></td>
    				<td><select name="accountType"></select></td>
    				<td><span class="err" id="accountTypeErr"></span></td>
    				<td>&nbsp;</td>
    				
    				<td><span class="required">*</span></td>
    				<td><span class="formLabel">Invoice Terms:</span></td>
    				<td><select name="invoiceTerms"></select></td>
    				<td><span class="err" id="invoiceTermsErr"></span></td>   				
    				<td>&nbsp;</td>
    				
    				<td><span class="required"></span></td>
    				<td><span class="formLabel">Proposed Date:</span></td>
    				<td><span id="proposedDate">N/A</span></td>
    				<td><span class="err" id="proposedDateErr"></span></td>
    				<td>&nbsp;</td>
    			</tr>
    			
    			<tr>    				    				    				
    				<td><span class="required">*</span></td>
    				<td><span class="formLabel">Lead Type:</span></td>
    				<td><select name="leadType"></select></td>
    				<td><span class="err" id="leadTypeErr"></span></td>
    				<td>&nbsp;</td>

    				<td><span class="required">*</span></td>
    				<td><span class="formLabel">Invoice Style:</span></td>
    				<td><select name="invoiceStyle"></select></td>
    				<td><span class="err" id="invoiceStyleErr"></span></td>
    				<td>&nbsp;</td>
    				
    				<td><span class="required"></span></td>    				
    				<td><span class="formLabel">Signed By:</span></td>
    				<td><input type="text" name="signedBy" /></td>
    				<td><span class="err" id="signedByErr"></span></td>
    				<td>&nbsp;</td>
    			</tr>
    			
    			<tr>
    				<td><span class="required">*</span></td>
    				<td><span class="formLabel">Building Type:</span></td>
					<td><select name="buildingType"></select></td>
    				<td><span class="err" id="invoiceGroupingErr"></span></td>
    				<td>&nbsp;</td>
					
					<td><span class="required">*</span></td>
    				<td><span class="formLabel">Grouping:</span></td>
    				<td><select name="invoiceGrouping"></select></td>
    				<td><span class="err" id="invoiceGroupingErr"></span></td> 
    				<td>&nbsp;</td>
    				
    				<td><span class="required"></span></td>
    				<td><span class="formLabel">Batch:</span> <input type="checkbox" name="invoiceBatch" /></td>    					
    				<td><span class="formLabel">Tax Exempt:</span> <input type="checkbox" name="taxExempt" /></td>
    				<td><span class="err" id="invoiceTermsErr"></span></td>
    				<td>&nbsp;</td>
    			</tr>
    		</table>
    		<div id="quoteButtonContainer">
    			<webthing:edit styleClass="fa-2x quote-button">Edit</webthing:edit><br />
    			<webthing:copy styleClass="fa-2x quote-button">Copy</webthing:copy><br />
    			<webthing:view styleClass="fa-2x quote-button">Search</webthing:view><br />
    			<webthing:addNew styleClass="fa-2x quote-button">New</webthing:addNew><br />
    			<%--
    			<input type="button" class="quoteButton" id="buttonModifyQuote" value="Modify" /><br />
    			<input type="button" class="quoteButton" id="buttonCopyQuote" value="Copy" /><br />
    			<input type="button" class="quoteButton" id="buttonNewQuote" value="New" /><br />
    			 --%>
    		</div>
    		<div class="spacer">&nbsp;</div>
    	</div>
    	<div id="addressPanel"> 
    		<table style="width:100%;">
    			<tr>
    				<td>
    					<div id="billToLoading"></div>
    				</td>
    			</tr>
    		</table>
    	</div>
    </tiles:put>

</tiles:insert>

