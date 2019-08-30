<%@ page contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<%@ taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%@ taglib uri="/WEB-INF/sql.tld" prefix="sql" %>
<%@ taglib uri="/WEB-INF/fmt.tld" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/struts-html.tld"  prefix="html"  %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld"  prefix="bean"  %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib tagdir="/WEB-INF/tags/webthing" prefix="webthing" %>
<%@ taglib uri="/WEB-INF/theTagThing.tld" prefix="ansi" %>




<tiles:insert page="../layout.jsp" flush="true">

    <tiles:put name="title" type="string">
        Tax Rate Lookup
    </tiles:put>
    
    
    <tiles:put name="headextra" type="string">
       	<link rel="stylesheet" href="css/lookup.css" />
    	<script type="text/javascript" src="js/ansi_utils.js"></script>
    	<script type="text/javascript" src="js/lookup.js"></script> 
    	<link rel="stylesheet" href="css/datepicker.css" type="text/css" />
    
        <style type="text/css">
			#displayTable {
				width:100%;
			}
			#addFormDiv {
				display:none;
				background-color:#FFFFFF;
				color:#000000;
				width:400px;
				padding:15px;
			}
			#filter-container {
        		width:402px;
        		float:right;
        	}
        	#new-taxrate-button {
        		display:none;
        	}
			.prettyWideButton {
				height:30px;
				min-height:30px;
			}
			.print-link {
				cursor:pointer;
			}
			.editJob {
				cursor:pointer;
				text-decoration:underline;
			}
			.formHdr {
				font-weight:bold;
			}
			.jobLink {
				color:#000000;
			}
			.overrideAction {
				cursor:pointer;
				text-decoration:none;
				color:#000000;
			}
			.dataTables_wrapper {
				padding-top:10px;
			}
			#ticket-modal {
				display:none;	
			}
			.ticket-clicker {
				color:#000000;
			}
			.type-swap {
				cursor:pointer;
			}
			.type-text {
				display:none;
			}
        </style>
        
        <script type="text/javascript">    
        
        $(document).ready(function(){
        	
        	TAXRATELOOKUP = {
                dataTable : null,
                localeFilter : "<c:out value="${ANSI_LOCALE_ID}" />",
                
       			init : function() {
       				$('.dateField').datepicker({
                        prevText:'&lt;&lt;',
                        nextText: '&gt;&gt;',
                        showButtonPanel:true
                    });
       				TAXRATELOOKUP.createTable();  
       				TAXRATELOOKUP.makeClickers();
       				TAXRATELOOKUP.markValid();  
       				TAXRATELOOKUP.makeModal();
       				if ( TAXRATELOOKUP.localeFilter != null && TAXRATELOOKUP.localeFilter != "" ) {
                		$("#new-taxrate-button").show();
                		
       				}
                }, 
                
                clearAddForm : function () {
    				$.each( $('#editPanel').find("input"), function(index, $inputField) {
    					$fieldName = $($inputField).attr('name');
    					if ( $($inputField).attr("type") == "text" ) {
    						$($inputField).val("");
    						TAXRATELOOKUP.markValid($inputField);
    					}
    				});
    				$('.err').html("");
    				$('#editPanel').data('rownum',null);
                },
                
                
                
                markValid : function ($inputField) {
                	$fieldName = $($inputField).attr('name');
                	$fieldGetter = "input[name='" + $fieldName + "']";
                	$fieldValue = $($fieldGetter).val();
                	$valid = '#' + $($inputField).data('valid');
    	            var re = /.+/;	            	 
                	if ( re.test($fieldValue) ) {
                		$($valid).removeClass("fa");
                		$($valid).removeClass("fa-ban");
                		$($valid).removeClass("inputIsInvalid");
                		$($valid).addClass("far");
                		$($valid).addClass("fa-check-square");
                		$($valid).addClass("inputIsValid");
                	} else {
                		$($valid).removeClass("far");
                		$($valid).removeClass("fa-check-square");
                		$($valid).removeClass("inputIsValid");
                		$($valid).addClass("fa");
                		$($valid).addClass("fa-ban");
                		$($valid).addClass("inputIsInvalid");
                	}
                },
                
                createTable : function(){
                	if (TAXRATELOOKUP.localeFilter == null || TAXRATELOOKUP.localeFilter == "" ) {
                		$url = "taxRateLookup";
                	} else {
                		$url = "taxRateLookup/" + TAXRATELOOKUP.localeFilter;
                	}
                	
            		var dataTable = $('#localeTaxRateTable').DataTable( {
            			"aaSorting":		[[0,'asc']],
            			"processing": 		true,
            	        "serverSide": 		true,
            	        "autoWidth": 		false,
            	        "deferRender": 		true,
            	        "scrollCollapse": 	true,
            	        "scrollX": 			true,
            	        rowId: 				'dt_RowId',
            	        dom: 				'Bfrtip',
            	        "searching": 		true,
            	        "searchDelay":		800,
            	        lengthMenu: [
            	        	[ 10, 50, 100, 500, 1000 ],
            	            [ '10 rows', '50 rows', '100 rows', '500 rows', '1000 rows' ]
            	        ],
            	        buttons: [
            	        	'pageLength','copy', 'csv', 'excel', {extend: 'pdfHtml5', orientation: 'landscape'}, 'print',{extend: 'colvis',	label: function () {doFunctionBinding();}}
            	        ],
            	        
            	        "columnDefs": [
             	            { "orderable": false, "targets": -1 },
            	            { className: "dt-head-left", "targets": [0,1] },
            	            { className: "dt-body-center", "targets": [2,3,4,5,6,7] },
            	            { className: "dt-right", "targets": []}
            	         ],
            	        "paging": true,
    			        "ajax": {
    			        	"url": $url,
    			        	"type": "GET",
    			        	"data": {}
    			        	},
    			        columns: [	//localeId, name, localeTypeId, typeName, stateName, effectiveDate, rateValue
    			            { width:"5%", title: "<bean:message key="field.label.localeId" />", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {	
    			            	if(row.locale_id != null){return (row.locale_id+"");}
    			            } },
    			            { width:"20%", title: "<bean:message key="field.label.name" />", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            	if(row.name != null){return (row.name+"");}
    			            } },
    			            { width:"8%", title: "<bean:message key="field.label.localeType" />", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            	if(row.locale_type_id != null){return (row.locale_type_id+"");}
    			            } },
    			            { width:"8%", title: "<bean:message key="field.label.typeName" />", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            	if(row.type_name != null){return (row.type_name+"");}
    			            } },
    			            { width:"5%", title: "<bean:message key="field.label.stateName" />" , "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {	
    			            	if(row.state_name != null){return (row.state_name+"");}
    			            } },
    			            { width:"10%", title: "Parent" , "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {	
    			            	if(row.parent_name != null){return (row.parent);}
    			            } },
    			            { width:"10%", title: "<bean:message key="field.label.effectiveDate" />" , "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {	
    			            	if(row.effective_date != null){return (row.effective_date+"");}
    			            } },
    			            { width:"8%", title: "<bean:message key="field.label.rateValue" />" , "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {	
    			            	if(row.display_rate != null){return (row.display_rate+"");}
    			            } },
    			            { width:"5%", title: "<bean:message key="field.label.action" />",  data: function ( row, type, set ) {	
    			            	//console.log(row);
    			            	var $actionData = "";
    			            	if ( row.locale_id != null ) {
    				            	var $editLink = '<ansi:hasPermission permissionRequired="TAX_WRITE"><a href="#" class="editAction" data-id="'+row.locale_id+'" data-effectivedate="'+row.effective_date+'" data-ratetypeid="'+row.type_id+'"><webthing:edit>Edit</webthing:edit></a></ansi:hasPermission>&nbsp;';
    				            	$actionData = $editLink;
    			            	}
    			            	return $actionData;
    			            } }],
    			            "initComplete": function(settings, json) {
    			            	//console.log(json);
    			            	//doFunctionBinding();
    			            	var myTable = this;
    			            	LOOKUPUTILS.makeFilters(myTable, "#filter-container", "#localeTaxRateTable", TAXRATELOOKUP.createTable);    			            	
    			            },
    			            "drawCallback": function( settings ) {
    			            	TAXRATELOOKUP.doFunctionBinding();
    			            }
    			    } );
            	},
            	
            	
            	doFunctionBinding : function() {
    				$( ".editAction" ).on( "click", function($clickevent) {
    					var $localeId = $clickevent.currentTarget.attributes['data-id'].value;
    					var $effectiveDate = $clickevent.currentTarget.attributes['data-effectivedate'].value;
    					var $rateTypeId = $clickevent.currentTarget.attributes['data-ratetypeid'].value;
    					TAXRATELOOKUP.showEdit($localeId, $effectiveDate, $rateTypeId);
    				});					
    				$(".print-link").on( "click", function($clickevent) {
    					doPrint($clickevent);
    				});
    				$(".tax-rate-clicker").on("click", function($clickevent) {
    					$clickevent.preventDefault();
    					var $localeId = $(this).attr("data-id");
//    					TICKETUTILS.doTicketViewModal("#ticket-modal",$ticketId);
    					$("#tax-rate-modal").dialog("open");
    				});

    			},
            	
            	
            	makeClickers : function() {
            		$('.ScrollTop').click(function() {
        				$('html, body').animate({scrollTop: 0}, 800);
        				return false;
               	    });
            		
            		
            		$('.type-swap').click(function() {
            			$(".type-text").toggle();
            			$(".type-select").toggle();
            			
        				$("#addTaxRateForm input[name='typeName']").val("");
        				$("#addTaxRateForm select[name='typeId']").val("");
            			
            		});
            		
            		
    				$("#new-taxrate-button").click(function($event) {
    					$('#goEdit').data("localeId",TAXRATELOOKUP.localeFilter);
    	        		$('#goEdit').button('option', 'label', 'Save');
    	        		$('#closeAddTaxRateForm').button('option', 'label', 'Close');
    	        		$("#addTaxRateForm ").dialog("option","title", "Add New Tax Rate").dialog("open");
    	        		TAXRATELOOKUP.showEdit(TAXRATELOOKUP.localeFilter, null, null);
    				});

            	},
            	
            	makeModal : function() {	
    				$("#addTaxRateForm" ).dialog({
    					autoOpen: false,
    					height: 400,
    					width: 600,
    					modal: true,
    					buttons: [
    						{
    							id: "closeAddTaxRateForm",
    							click: function() {
    								$("#addTaxRateForm").dialog( "close" );
    							}
    						},{
    							id: "goEdit",
    							click: function($event) {
    								TAXRATELOOKUP.updateTaxRate();
    							}
    						}	      	      
    					],
    					close: function() {
    						TAXRATELOOKUP.clearAddForm();
    						$("#addTaxRateForm").dialog( "close" );
    						//allFields.removeClass( "ui-state-error" );
    					}
    				});
    			},
    			
    			
    			
    			showEdit : function ($localeId, $effectiveDate, $rateTypeId) {
    				console.debug("localeId: " + $localeId);
    				$("#goEdit").data("localeId: " + $localeId);
    			    $('#goEdit').button('option', 'label', 'Save');
    			    $('#closeAddTaxRateForm').button('option', 'label', 'Close');
    			    
    				var $url = 'localeTaxRate/' + $localeId;
    				var jqxhr = $.ajax({
	    				type: 'GET',
	    				url: $url,
	    				data:{"effectiveDate":$effectiveDate, "rateTypeId":$rateTypeId},
	    				statusCode: {
	    					200: function($data) {
	    						var $taxRate = $data.data;
	    						$.each($taxRate, function($fieldName, $value) {									
	    							$selector = "#editPanel input[name=" + $fieldName + "]";
	    							if ( $($selector).length > 0 ) {
	    								$($selector).val($value);
	    							}
	    						}); 
	    						$select = $("select[name='typeId']");
	    	        			$('option', $select).remove();
	    	        			$select.append(new Option("",null));
	    	        			$.each($data.data.taxTypeList, function($index, $val) {
	    	        				$select.append(new Option($val.typeName, $val.typeId));
	    	        			});	
	    	        			
	    	        			
	    						$("#localeId").html($taxRate.localeId);
	    						$("#addTaxRateForm input[name='localeId']").val($taxRate.localeId);
	    						$("#localeName").html($taxRate.name);
	    						$("#localeTypeId").html($taxRate.localeTypeId);
	    						$("#stateName").html($taxRate.stateName);	
	    						$("#addTaxRateForm  input[name='effectiveDate']").val($taxRate.effectiveDate);
	    						$("#addTaxRateForm  input[name='keyEffectiveDate']").val($taxRate.effectiveDate);
	    						$("#addTaxRateForm  input[name='rateValue']").val($taxRate.rateValue);	
	    						$("#addTaxRateForm  select[name='typeId']").val($taxRate.typeId);
	    						$("#addTaxRateForm  input[name='keyRateTypeId']").val($taxRate.typeId);	
	    						$("#addTaxRateForm  input[name='typeName']").val("");
	    						$("#addTaxRateForm  .err").html("");
	    						$("#addTaxRateForm").dialog("option","title", "Edit Tax Rate").dialog("open");
	    					},
	    					403: function($data) {
	    						$("#addTaxRateForm").dialog("close");
	    						$("#globalMsg").html("Session Timeout. Log in and try again").show();
	    					},
	    					404: function($data) {
	    						$("#addTaxRateForm").dialog("close");
	    						$("#globalMsg").html("Invalid Request").show();
	    					},
	    					500: function($data) {
	    						$("#addTaxRateForm").dialog("close");
	    						$("#globalMsg").html("System Error; Contact Support").show();
	    					}
	    				},
	    				dataType: 'json'
    				});
    			},
    			
    				
    			updateTaxRate : function () {
    				console.debug("Updating Tax Rate");
    				var $localeId = $("#addTaxRateForm input[name='localeId']").val();
    				console.debug("localeId: " + $localeId);
    				
    				if ( $localeId == null || $localeId == '') {
    					$url = 'localeTaxRate';
    				} else {
    					$url = 'localeTaxRate/' + $localeId;
    				}
    				console.debug($url);
    						
    				var $outbound = {};
    				$outbound['localeId'] = $("#addTaxRateForm input[name='localeId']").val();
    				$outbound['keyEffectiveDate'] = $("#addTaxRateForm input[name='keyEffectiveDate']").val();
    				$outbound['keyRateTypeId'] = $("#addTaxRateForm input[name='keyRateTypeId']").val();
    				$outbound['typeName'] = $("#addTaxRateForm input[name='typeName']").val();
    				$outbound['effectiveDate'] = $("#addTaxRateForm input[name='effectiveDate']").val();
    				$outbound['rateValue'] = $("#addTaxRateForm input[name='rateValue']").val();
    				$outbound['typeId'] = $("#addTaxRateForm select[name='typeId']").val();
    				console.debug($outbound);
    				
    				var jqxhr = $.ajax({
    					type: 'POST',
    					url: $url,
    					data: JSON.stringify($outbound),
    					statusCode: {
    						200: function($data) {
    			    			if ( $data.responseHeader.responseCode == 'EDIT_FAILURE') {
    			    				$.each($data.data.webMessages, function (key, value) {
    			    					console.log(key);
    			    					if ( key == "GLOBAL_MESSAGE" ) {
    			    						$("#globalMsg").html(value[0]).show().fadeOut(6000);
    			    						$("#addTaxRateForm").dialog("close");
    			    					} else {
	    			    					var $selectorName = "#" + key + "Err";
	    			    					$($selectorName).show();
	    			    					$($selectorName).html(value[0]).fadeOut(6000);
    			    					}
    			    				});
    			    				
    			   				} else {	    				
    			    				$("#addTaxRateForm").dialog("close");
    			    				$('#localeTaxRateTable').DataTable().ajax.reload();		
    			    				TAXRATELOOKUP.clearAddForm();		    					
    			    				$("#globalMsg").html("Update Successful").show().fadeOut(6000);
    			    			}
    						},
    						403: function($data) {
    							$("#globalMsg").html("Session Timeout. Log in and try again");
    						},
    						404: function($data) {
    							$("#globalMsg").html("Invalid Selection").show().fadeOut(100000);
    						},
    						500: function($data) {
    							$("#globalMsg").html("System Error; Contact Support");
    						}
    					},
    					dataType: 'json'
    				});
    			},
    			
    			
    			
            	    
        	};
        
        	TAXRATELOOKUP.init();
        		
				
			
        });
        		
        </script>        
    </tiles:put>
    
   <tiles:put name="content" type="string">
    	<h1><bean:message key="page.label.taxrate" /> <bean:message key="menu.label.lookup" /></h1> 
    	<c:if test="${not empty ANSI_LOCALE_ID}">
    		<span class="orange"><bean:message key="field.label.localeFilter" />: <c:out value="${ANSI_LOCALE_DISPLAY}" /></span>
    			&nbsp;&nbsp;<html:link action="taxRateLookup" style="text-decoration:none;"><webthing:ban>Clear Filter</webthing:ban></html:link><br />
    	</c:if>
    	  	
    	  	
 	<webthing:lookupFilter filterContainer="filter-container" />

 	<table id="localeTaxRateTable" style="table-layout: fixed" class="display" cellspacing="0" style="font-size:9pt;max-width:1300px;width:1300px;">
        <thead>
        </thead>
        <tfoot>
        </tfoot>
    </table>
    <input type="button" value="New" class="prettyWideButton" id="new-taxrate-button" />
    
    <div id="addTaxRateForm">
	    <div class="modal-header">
	    <h5 class="modal-title" id="name"></h5>
	    </div>
	    <input type="hidden" name="keyEffectiveDate" />
	    <input type="hidden" name="keyRateTypeId" />
    	<table>
    		<tr>
    			<td><span class="formHdr">Locale ID</span></td>
    			<td><input type="hidden" name="localeId" /><span id="localeId"></span></td>
    			<td><span class="err" id="localeIdErr"></span></td>
    		</tr>
    		<tr>
    			<td><span class="formHdr">Locale Name</span></td>
    			<td><span id="localeName" /></td>
    			<td><span class="err" id="localeNameErr"></span></td>
    		</tr>
    		<tr>
    			<td><span class="formHdr">Locale Type</span></td>
    			<td><span id="localeTypeId"></span></td>
    			<td><span class="err" id="localeTypeIdErr"></span></td>
    		</tr>
    		<tr>
    			<td><span class="formHdr">State</span></td>
    			<td><span id="stateName" /></td>
    			<td><span class="err" id="stateNameErr"></span></td>
    		</tr>
    		<tr>
    			<td><span class="formHdr">Rate Type</span></td>
    			<td>
    				<select name="typeId" class="type-select"></select><webthing:addNew styleClass="type-select type-swap">New</webthing:addNew>
    				<input type="text" name="typeName" class="type-text" /><webthing:ban styleClass="type-text type-swap">Cancel</webthing:ban>
    			</td>
    			<td>
    				<span class="err" id="typeIdErr"></span>
    			</td>
    		</tr>
    		<tr>
    			<td><span class="formHdr">Effective Date</span></td>
    			<td><input type="text" name="effectiveDate" class="dateField" /></td>
    			<td><span class="err" id="effectiveDateErr"></span></td>
    		</tr>
    		<tr>
    			<td><span class="formHdr">Rate</span></td>
    			<td><input type="text" name="rateValue" /></td>
    			<td><span class="err" id="rateValueErr"></span></td>
    		</tr>
    	</table>
    </div>
    
	   
    
    <webthing:scrolltop />
    
    </tiles:put>
		
</tiles:insert>

