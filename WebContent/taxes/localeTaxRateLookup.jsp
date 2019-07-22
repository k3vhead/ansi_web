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
        	
			.prettyWideButton {
				height:30px;
				min-height:30px;
			}
			select	{
				width:80px !important;
				max-width:80px !important;
			}
			.print-link {
				cursor:pointer;
			}
			.editJob {
				cursor:pointer;
				text-decoration:underline;
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
        </style>
        
        <script type="text/javascript">    
        
        $(document).ready(function(){
        	
        	TAXRATELOOKUP = {
                dataTable : null,

       			init : function() {
       				TAXRATELOOKUP.createTable();  
       				TAXRATELOOKUP.makeClickers();
       				TAXRATELOOKUP.markValid();  
       				TAXRATELOOKUP.makeEditPanel(); 
       				TAXRATELOOKUP.makeClickers();
       				TAXRATELOOKUP.showNew();
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
    			        	"url": "taxRateLookup",
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
//    				            	var $editLink = '<ansi:hasPermission permissionRequired="TAX_WRITE"><a href="localeReturn.html?id='+row.locale_id+'" class="editAction" data-id="'+row.locale_id+'"><webthing:edit>Edit</webthing:edit></a></ansi:hasPermission>&nbsp;';
//    				            	
//    		            			var $ticketData = 'data-id="' + row.locale_id + '"';
//    			            		$printLink = '<ansi:hasPermission permissionRequired="TAX_READ"><i class="print-link fa fa-print" aria-hidden="true" ' + $localeData + '></i></ansi:hasPermission>'
//    			            		var $claimLink = '';
//    			            		
//    				            	$actionData = $editLink + $printLink;
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
    					TAXRATELOOKUP.doEdit($clickevent);
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
            	},
            	
            	makeEditPanel : function() {	
    				$("#addTaxRateForm" ).dialog({
    					autoOpen: false,
    					height: 300,
    					width: 500,
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
    			
    			doEdit : function($clickevent) {
    				var $rowid = $clickevent.currentTarget.attributes['data-id'].value;
    					var $url = 'localeTaxRateTable/' + $rowid;
    					//console.log("YOU PASSED ROW ID:" + $rowid);
    					var jqxhr = $.ajax({
    						type: 'GET',
    						url: $url,
    						success: function($data) {
    							//console.log($data);
    							//localeId, name, localeTypeId, typeName, stateName, effectiveDate, rateValue
    			        		$("#localeId").val(($data.data.codeList[0]).localeId);
    			        		$("#name").val(($data.data.codeList[0]).name);
    			        		$("#localeTypeId").val(($data.data.codeList[0]).localeTypeId);
    			        		$("#typeName").val(($data.data.codeList[0]).typeName);
    			        		$("#stateName").val(($data.data.codeList[0]).stateName);
    			        		$("#effectiveDate").val(($data.data.codeList[0]).effectiveDate);
    			        		$("#rateValue").val(($data.data.codeList[0]).rateValue);
    			        		
    			        		$("#updateOrAdd").val("update");
    			        		$("#addTaxRateForm").dialog( "open" );
    						},
    						statusCode: {
    							403: function($data) {
    								$("#globalMsg").html($data.responseJSON.responseHeader.responseMessage);
    							} 
    						},
    						dataType: 'json'
    					});
    				//console.log("Edit Button Clicked: " + $rowid);
    			},
    			
    			showEdit : function ($clickevent) {
                	
    			    //$name = $("#addLocaleForm").attr("data-name");
    				//var $name = $(this).attr("data-name");
    				var $localeId = $clickevent.currentTarget.attributes['data-id'].value;
    				console.debug("localeId: " + $localeId);
    				$("#goEdit").data("localeId: " + $localeId);
    			    $('#goEdit').button('option', 'label', 'Save');
    			    $('#closeAddTaxRateForm').button('option', 'label', 'Close');
    			    
    				var $url = 'taxRateLookup/' + $localeId;
    				var jqxhr = $.ajax({
    				type: 'GET',
    				url: $url,
    				statusCode: {
    					200: function($data) {
    					/*		var $permissionGroup = $data.data.permGroupItemList[0];
    							$.each($permissionGroup, function($fieldName, $value) {									
    								$selector = "#editPanel input[name=" + $fieldName + "]";
    								if ( $($selector).length > 0 ) {
    									$($selector).val($value);
    								}
    							}); */
    						$("#addTaxRateForm 	input[name='localeId']").val($permissionGroup.localeId);
    						$("#addTaxRateForm  input[name='name']").val($permissionGroup.name);
    						$("#addTaxRateForm  input[name='localeTypeId']").val($permissionGroup.localeTypeId);
    						$("#addTaxRateForm  input[name='typeName']").val($permissionGroup.typeName);	
    						$("#addTaxRateForm  input[name='stateName']").val($permissionGroup.stateName);	
    						$("#addTaxRateForm  input[name='effectiveDate']").val($permissionGroup.effectiveDate);
    						$("#addTaxRateForm  input[name='rateValue']").val($permissionGroup.rateValue);	
    						$("#addTaxRateForm  .err").html("");
    						$("#addTaxRateForm ").dialog("option","title", "Edit Locale").dialog("open");
    					},
    					403: function($data) {
    						$("#globalMsg").html("Session Timeout. Log in and try again");
    					},
    					404: function($data) {
    						$("#globalMsg").html("Invalid Request");
    					},
    					500: function($data) {
    						$("#globalMsg").html("System Error; Contact Support");
    					}
    				},
    				dataType: 'json'
    				});
    			},
    			
    			showNew : function () {
    				$(".showNew").click(function($event) {
    					$('#goEdit').data("localeId",null);
    	        		$('#goEdit').button('option', 'label', 'Save');
    	        		$('#closeAddTaxRateForm').button('option', 'label', 'Close');
    	        		
    	 //       		$("#editPanel display[name='']").val("");
    					$("#addTaxRateForm	input[name='localeId']").val("");
    					$("#addTaxRateForm  input[name='name']").val("");
    					$("#addTaxRateForm  input[name='localeTypeId']").val("");
    					$("#addTaxRateForm  input[name='typeName']").val("");	
    					$("#addTaxRateForm  input[name='stateName']").val("");	
    					$("#addTaxRateForm  input[name='effectiveDate']").val("");
    					$("#addTaxRateForm  input[name='rateValue']").val("");	
    	        		$("#addTaxRateForm  .err").html("");
    	        		$("#addTaxRateForm ").dialog("option","title", "Add New Tax Rate").dialog("open");
    				});
    			},
    				
    			updateTaxRate : function () {
    				console.debug("Updating Tax Rate");
    				var $permissionGroupId = $("#addTaxRateForm input[name='localeId']").val();
    				console.debug("localeId: " + $localeId);
    				
    				if ( $permissionGroupId == null || $permissionGroupId == '') {
    					$url = 'localeTaxRateTable/add';
    				} else {
    					$url = 'localeTaxRateTable/' + $localeId;
    				}
    				console.debug($url);
    						
    				var $outbound = {};
    				$outbound['localeId'] = $("#addLocaleForm input[name='localeId']").val();
    				$outbound['name'] = $("#addLocaleForm input[name='name']").val();
    				$outbound['localeTypeId'] = $("#addLocaleForm select[name='localeTypeId']").val();	
    				$outbound['typeName'] = $("#addLocaleForm input[name='typeName']").val();
    				$outbound['stateName'] = $("#addLocaleForm select[name='stateName']").val();
    				$outbound['effectiveDate'] = $("#addLocaleForm select[name='effectiveDate']").val();
    				$outbound['rateValue'] = $("#addLocaleForm select[name='rateValue']").val();
    				console.debug($outbound);
    				
    				var jqxhr = $.ajax({
    					type: 'POST',
    					url: $url,
    					data: JSON.stringify($outbound),
    					statusCode: {
    						200: function($data) {
    			    			if ( $data.responseHeader.responseCode == 'EDIT_FAILURE') {
    			    				$.each($data.data.webMessages, function (key, value) {
    			    					var $selectorName = "#" + key + "Err";
    			    					$($selectorName).show();
    			    					$($selectorName).html(value[0]).fadeOut(10000);
    			    				});
    			   				} else {	    				
    			    				$("#addTaxRateForm").dialog("close");
    			    				$('#localeTaxRateTable').DataTable().ajax.reload();		
    			    				TAXRATELOOKUP.clearAddForm();		    					
    			    				$("#globalMsg").html("Update Successful").show().fadeOut(10000);
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
        		
				
			function doPrint($clickevent) {
				var $localeId = $clickevent.currentTarget.attributes['data-id'].value;
				console.debug("ROWID: " + $localeId);
				var a = document.createElement('a');
                var linkText = document.createTextNode("Download");
                a.appendChild(linkText);
                a.title = "Download";
                a.href = "ticketPrint/" + $localeId;
                a.target = "_new";   // open in a new window
                document.body.appendChild(a);
                a.click();				
			}
        });
        		
        </script>        
    </tiles:put>
    
   <tiles:put name="content" type="string">
    	<h1><bean:message key="page.label.taxrate" /> <bean:message key="menu.label.lookup" /></h1> 
    	<c:if test="${not empty ANSI_JOB_ID}">
    		<span class="orange"><bean:message key="field.label.jobFilter" />: <c:out value="${ANSI_JOB_ID}" /></span><br />
    	</c:if>
    	<c:if test="${not empty ANSI_DIVISION_ID}">
    		<span class="orange"><bean:message key="field.label.divisionFilter" />: <c:out value="${ANSI_DIVISION_ID}" /></span><br />
    	</c:if>
    	<c:if test="${not empty ANSI_TICKET_LOOKUP_START_DATE}">
    		<span class="orange"><bean:message key="field.label.startDate" />: <c:out value="${ANSI_TICKET_LOOKUP_START_DATE}" /></span><br />
    	</c:if>
    	<c:if test="${not empty ANSI_TICKET_LOOKUP_STATUS}">
    		<span class="orange"><bean:message key="field.label.statusFilter" />: <c:out value="${ANSI_TICKET_LOOKUP_STATUS}" /></span><br />
    	</c:if>
    	  	
    	  	
 	<webthing:lookupFilter filterContainer="filter-container" />

 	<table id="localeTaxRateTable" style="table-layout: fixed" class="display" cellspacing="0" style="font-size:9pt;max-width:1300px;width:1300px;">
        <thead>
        </thead>
        <tfoot>
        </tfoot>
    </table>
    
    <div id="addTaxRateForm">
	    <div class="modal-header">
	    <h5 class="modal-title" id="name"></h5>
	    </div>
    	<table>
    		<tr>
    			<td><span class="formHdr">Locale ID</span></td>
    			<td><input type="text" name="localeId" style="border-style: hidden" readOnly/></td>
    		</tr>
    		<tr>
    			<td><span class="formHdr">Locale Name</span></td>
    			<td><input type="text" name="name" /></td>
    			<td><span class="err" id="nameErr"></span></td>
    		</tr>
    		<tr>
    			<td><span class="formHdr">Locale Type</span></td>
    			<td><input type="text" name="localeTypeId" /></td>
    			<td><span class="err" id="localeTypeIdErr"></span></td>
    		</tr>
    		<tr>
    			<td><span class="formHdr">Rate Type</span></td>
    			<td><input type="text" name="typeName" /></td>
    			<td><span class="err" id="typeNameErr"></span></td>
    		</tr>
    		<tr>
    			<td><span class="formHdr">State</span></td>
    			<td><input type="text" name="stateName" /></td>
    			<td><span class="err" id="stateNameErr"></span></td>
    		</tr>
    		<tr>
    			<td><span class="formHdr">Effective Date</span></td>
    			<td><input type="text" name="effectiveDate" /></td>
    			<td><span class="err" id="effectiveDateErr"></span></td>
    		</tr>
    		<tr>
    			<td><span class="formHdr">Rate</span></td>
    			<td><input type="text" name="rateValue" /></td>
    			<td><span class="err" id="rateValueErr"></span></td>
    		</tr>
    				
    	</table>
    </div>
    
	    
	    <input type="button" class="prettyWideButton showNew" value="New" />
    
    <webthing:scrolltop />
    
    </tiles:put>
		
</tiles:insert>

