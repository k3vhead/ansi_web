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
        Nexus Taxed Lookup
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
			#localeDivisionModal {
				display:none;	
			}
			.ticket-clicker {
				color:#000000;
			}
        </style>
        
        <script type="text/javascript">    
        
        $(document).ready(function(){
        	
        	LOCALEDIVISIONLOOKUP = {
                dataTable : null,

       			init : function() {
       				LOCALEDIVISIONLOOKUP.createTable();  
       				LOCALEDIVISIONLOOKUP.clearAddForm();
       				LOCALEDIVISIONLOOKUP.makeClickers();
       				LOCALEDIVISIONLOOKUP.populateDivisionSelect();
       				LOCALEDIVISIONLOOKUP.makeLocaleTypeList();
       				LOCALEDIVISIONLOOKUP.markValid();  
       				LOCALEDIVISIONLOOKUP.makeEditPanel();
       				LOCALEDIVISIONLOOKUP.showNew();
       				
       				$('.dateField').datepicker({
                        prevText:'&lt;&lt;',
                        nextText: '&gt;&gt;',
                        showButtonPanel:true
                    });
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
                
                clearAddForm : function () {
    				$.each( $('#localeDivisionModal').find("input"), function(index, $inputField) {
    					$fieldName = $($inputField).attr('name');
    					if ( $($inputField).attr("type") == "text" ) {
    						$($inputField).val("");
    						LOCALEDIVISIONLOOKUP.markValid($inputField);
    					}
    				});
    				$('.err').html("");
    				$('#localeDivisionModal').data('rownum',null);
                },
                
                createTable : function(){
            		var dataTable = $('#localeDivisionTable').DataTable( {
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
            	            { className: "dt-head-left", "targets": [0,1,2] },
            	            { className: "dt-body-center", "targets": [3,4,5,7] },
            	            { className: "dt-right", "targets": [6]}
            	         ],
            	        "paging": true,
    			        "ajax": {
    			        	"url": "localeDivisionLookup",
    			        	"type": "GET",
    			        	"data": {}
    			        	},
    			        columns: [	//divisionId, localeId, effectiveStartDate, effectiveStopDate, addressId
    			            { width:"10%", title: "<bean:message key="field.label.divisionNbr" />", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {	
    			            	if(row.division_display != null){return (row.division_display+"");}
    			            } },
    			            { width:"15%", title: "<bean:message key="field.label.name" />", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            	if(row.name != null){return (row.name+"");}
    			            } },
//    			            { width:"10%", title: "<bean:message key="field.label.localeType" />", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
//    			            	if(row.locale_type_id != null){return (row.locale_type_id+"");}
//    			            } },
    			            { width:"8%", title: "<bean:message key="field.label.stateName" />", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            	if(row.state_name != null){return (row.state_name+"");}
    			            } },
    			            { width:"10%", title: "<bean:message key="field.label.startDate" />", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            	if(row.effective_start_date != null){return (row.effective_start_date+"");}
    			            } },
    			            { width:"10%", title: "<bean:message key="field.label.stopDate" />", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            	if(row.effective_stop_date != null){return (row.effective_stop_date+"");}
    			            } },
    			            { width:"20%", title: "<bean:message key="field.label.address" />" , "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {	
    			            	if(row.address1 != null){return (row.address1+"\n");}
    			            	if(row.address2 != null){return (row.address2+"\n");}
    			            	if(row.city != null){return (row.city+" ");}
    			            	if(row.state != null){return (row.state+", ");}
    			            	if(row.zip != null){return (row.zip+"");}
    			            } },
    			            
    			            { width:"5%", title: "<bean:message key="field.label.action" />",  data: function ( row, type, set ) {	
    			            	//console.log(row);
    			            	var $actionData = "";
    			            	if ( row.locale_id != null ) {
    				            	var $editLink = '<ansi:hasPermission permissionRequired="TAX_WRITE"><a href="localeReturn.html?id='+row.locale_id+'" class="editAction" data-id="'+row.locale_id+'"><webthing:edit>Edit</webthing:edit></a></ansi:hasPermission>&nbsp;';
    				            	
//    		            			var $ticketData = 'data-id="' + row.locale_id + '"';
//    			            		$printLink = '<ansi:hasPermission permissionRequired="TAX_READ"><i class="print-link fa fa-print" aria-hidden="true" ' + $localeData + '></i></ansi:hasPermission>'
//    			            		var $claimLink = '';
    			            		
    				            	$actionData = $editLink; //+ $printLink;
    			            	}
    			            	return $actionData;
    			            } }],
    			            "initComplete": function(settings, json) {
    			            	//console.log(json);
    			            	//doFunctionBinding();
    			            	var myTable = this;
    			            	LOOKUPUTILS.makeFilters(myTable, "#filter-container", "#localeDivisionTable", LOCALEDIVISIONLOOKUP.createTable);
    			            },
    			            "drawCallback": function( settings ) {
    			            	LOCALEDIVISIONLOOKUP.doFunctionBinding();
    			            }
    			    } );
            	},
            	
            	
            	doFunctionBinding : function() {
    				$( ".editAction" ).on( "click", function($clickevent) {
    					 doEdit($clickevent);
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
    			
    			showNew : function () {
					$(".showNew").click(function($event) {
						$('#goEdit').data("localeId",null);
		        		$('#goEdit').button('option', 'label', 'Save');
		        		$('#closeLocaleDivisionModal').button('option', 'label', 'Close');
		        		
						$("#localeDivisionModal  select[name='divisionId']").val("");
						$("#localeDivisionModal  input[name='localeName']").val("");
//						$("#localeDivisionModal  input[name='localeType']").val("");
						$("#localeDivisionModal  select[name='stateName']").val("");
						$("#localeDivisionModal  input[name='effectiveStartDate']").val("");	
						$("#localeDivisionModal  input[name='effectiveStopDate']").val("");
						$("#localeDivisionModal  input[name='addressId']").val("");
		        		$("#localeDivisionModal  .err").html("");
		        		$("#localeDivisionModal ").dialog("option","title", "Add New Locale/Division").dialog("open");
					});
				},
    			
    			makeEditPanel : function() {	
    				$("#localeDivisionModal").dialog({
    					autoOpen: false,
    					height: 400,
    					width: 600,
    					modal: true,
    					buttons: [
    						{
    							id: "closeLocaleDivisionModal",
    							click: function() {
    								$("#localeDivisionModal").dialog( "close" );
    							}
    						},{
    							id: "goEdit",
    							click: function($event) {
    								LOCALEDIVISIONLOOKUP.updateLocaleDivision();
    							}
    						}	      	      
    					],
    					close: function() {
    						LOCALEDIVISIONLOOKUP.clearAddForm();
    						$("#localeDivisionModal").dialog( "close" );
    						//allFields.removeClass( "ui-state-error" );
    					}
    				});
    			},
    			
    			updateLocaleDivision : function () {
    				console.debug("Updating Tax Rate");
    				var $localeId = $("#localeDivisionModal input[name='localeId']").val();
    				console.debug("localeId: " + $localeId);
    				
    				if ( $localeId == null || $localeId == '') {
    					$url = 'localeDivision';
    				} else {
    					$url = 'localeDivision/' + $localeId;
    				}
    				console.debug($url);
    						
    				var $outbound = {};
    				$outbound['divisionId'] = $("#addTaxRateForm select[name='divisionId']").val();
    				$outbound['localeName'] = $("#addTaxRateForm input[name='localeName']").val();
    				$outbound['stateName'] = $("#addTaxRateForm select[name='stateName']").val();
    				$outbound['effectiveStartDate'] = $("#addTaxRateForm input[name='effectiveStartDate']").val();
    				$outbound['effectiveStopDate'] = $("#addTaxRateForm input[name='effectiveStopDate']").val();
    				$outbound['addressId'] = $("#addTaxRateForm input[name='address1']").val();
    				
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
    			    				$("#localeDivisionModal").dialog("close");
    			    				$('#localeDivisionTable').DataTable().ajax.reload();		
    			    				LOCALEDIVISIONLOOKUP.clearAddForm();		    					
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
            	
    			populateDivisionSelect:function() {
                	$data = ANSI_UTILS.getDivisionList();
                	console.log("populateDivSelect");
                	var $select = $("#localeDivisionModal select[name='divisionId']");
        			$('option', $select).remove();
        			$select.append(new Option("",null));
        			$.each($data, function($index, $val) {
        				var $display = $val.divisionNbr + "-" + $val.divisionCode;
        				$select.append(new Option($display, $val.divisionId));
        			});	
                },
                
				makeLocaleTypeList: function (){ 
	    			var jqxhr = $.ajax({
	    				type: 'GET',
	    				url: "options?LOCALE_TYPE",
	    				success: function($data) {
	    					var $select = $("#localeDivisionModal select[name='localeTypeId']")
	    					$('option', $select).remove();
	    					$select.append(new Option("",""));
	    					$.each($data.data.localeType, function(index, val){
	    						$select.append(new Option(val.display, val.name));
	    					});
	    				},
	    				statusCode: {
	    					403: function($data) {
	    						$("#globalMsg").html($data.responseJSON.responseHeader.responseMessage);
	    					}
	    				},
	    				dataType: 'json'
	    			});
	    		},	  
            	
            	makeClickers : function() {
            		$('.ScrollTop').click(function() {
        				$('html, body').animate({scrollTop: 0}, 800);
        				return false;
               	    });
            	},
            	    
        	};
        
        	LOCALEDIVISIONLOOKUP.init();
        	
			
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
    	<h1><bean:message key="page.label.localedivision" /> <bean:message key="menu.label.lookup" /></h1> 
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

 	<table id="localeDivisionTable" style="table-layout: fixed" class="display" cellspacing="0" style="font-size:9pt;max-width:1300px;width:1300px;">
        <thead>
        </thead>
        <tfoot>
        </tfoot>
    </table>
    
     <div id="localeDivisionModal">
	    <div class="modal-header">
	    	<h5 class="modal-title" id="name"></h5>
	    </div>
    	<table class="ui-front">
    		<tr>
    			<td><span class="formHdr">Division</span></td>
    			<td>
    				<select name="divisionId" />
    				
    			</td>
    			<td><span class="err" id="divisionIdErr"></span></td>
    		</tr>
    		<tr>
    			<td><span class="formHdr">Locale Name</span></td>
    			<td><input type="text" name="localeName" /></td>
    			<td><span class="err" id="localeNameErr"></span></td>
    		</tr>
    		<tr>
    			<td><span class="formHdr">State</span></td>
    			<td>
    				<select name="stateName" ><webthing:states /></select>
    			</td>
    			<td><span class="err" id="stateNameErr"></span></td>
    		</tr>
    		
    		<tr>
    			<td><span class="formHdr">Effective Start Date</span></td>
    			<td><input type="text" name="effectiveStartDate" class="dateField" /></td>
    			<td><span class="err" id="effectiveStartDateErr"></span></td>
    		</tr>
    		<tr>
    			<td><span class="formHdr">Effective Stop Date</span></td>
    			<td><input type="text" name="effectiveStopDate" class="dateField" /></td>
    			<td><span class="err" id="effectiveStopDateErr"></span></td>
    		</tr>
    		<tr>
    			<td><span class="formHdr">Address</span></td>
    			<td><input type="text" name="addressId" /></td>
    			<td><span class="err" id="addressIdErr"></span></td>
    		</tr>
    	</table>
    </div>
    <input type="button" class="prettyWideButton showNew" value="New" />
    <webthing:scrolltop />


    </tiles:put>
		
</tiles:insert>

