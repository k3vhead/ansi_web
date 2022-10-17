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
        <bean:message key="page.label.reportSubscription" /> Admin
    </tiles:put>
    
    
    <tiles:put name="headextra" type="string">
    	<link rel="stylesheet" href="css/lookup.css" />
    	<link rel="stylesheet" href="css/callNote.css" />
    	<script type="text/javascript" src="js/ansi_utils.js"></script>
    	<script type="text/javascript" src="js/lookup.js"></script> 
        <style type="text/css">        	
        	.label {
        		font-weight:bold;
        	}
        	#newSubscriberModal {
        		display:none;
        	}
        	#subscriptionLookupContainer {
        		margin-top:15px;
        	}
        
        
        
			#selection-container {
				width:88%;
				float:left;
        		border:solid 1px #000000;				
			}
			#selection-menu-container {
				display: hidden;
				width:11%;
				float:right;
			}
			.checkbox {
				float: center;
			}
			.activeRowCol {
				backround-color:#F9F9F9;
			}
        	.selection-container {
        		width:100%;
        		display:none;
        	}
        	#thinking {
        		width:100%;
        	}
        	.thinking {
        		width:100%;
        		display:none;
        	}
        	#delete-modal {
        		display:none;
        	}
        	th {
        		text-align:center;
        	}
        	#filter-container {
        		width:402px;
        		float:right;
        	}
        	#new-calendar-modal {
        		display:none;
        	}
        	.action-link {
        		cursor:pointer;
        	}
        	.form-label {
        		font-weight:bold;
        	}
        	.monthLabel {
        		font-weight:bold;
        		background-color:#000000;
        		color:#FFFFFF;
        	}
        	.pre-edit {
				background-color:#CC6600;				
			}
        </style>
        
        <script type="text/javascript">
        
        $(document).ready(function() {
        	;SUBSCRIPTION_ADMIN = {
				subscriptionTable : null,
        		
        		init : function() {
        			ANSI_UTILS.makeServerCall("GET", "divisionList", {}, {200:SUBSCRIPTION_ADMIN.makeColumns}, {});
					$("#reportSelector select[name='reportId']").change(function($event) {
						var $reportId = $("#reportSelector select[name='reportId']").val();
						if ( $reportId == null || $reportId == '' ) {
							$("#subscriptionLookup tbody").html("");	
						} else {
							SUBSCRIPTION_ADMIN.makeSubscriptionTable();
						}
					});      			
        		},
        		                
        		
        		
        		doNewSubscription : function() {
        			var $userId = $("#newSubscriberModal input[name='userId']").val();
        			var $divisionId = $("#newSubscriberModal select[name='newDivisionId']").val();
        			var $reportId = $("#newSubscriberModal input[name='reportId']").val();
        			console.log("doSubscription: " + $userId + " " + $divisionId + " " + $reportId);
    				$url = "reports/subscriptionAdmin/" + $reportId;
       				$type = "POST";        			
        			ANSI_UTILS.makeServerCall($type, $url, JSON.stringify({"userId":$userId,"divisionId":$divisionId}), {200:SUBSCRIPTION_ADMIN.doNewSubscriptionSuccess}, {});
        		},
        		
        		
        		doNewSubscriptionSuccess : function($data, $passthru) {
        			console.log("doSubscriptionSuccess");
        			if ( $data.responseHeader.responseCode == "SUCCESS" ) {
        				$("#subscriptionLookup").DataTable().ajax.reload();
        				$("#globalMsg").html("Success!").show().fadeOut(4000);
        				$("#newSubscriberModal").dialog("close");
        			} else if ($data.responseHeader.responseCode == "EDIT_FAILURE" ) {
        				if ( $data.data.webMessages.divisionId != null ) {
        					$("#newSubscriberModal .newDivisionIdErr").html($data.data.webMessages.divisionId[0]);
        				}
        				if ( $data.data.webMessages.userId != null ) {
        					$("#newSubscriberModal .newSubscriberErr").html($data.data.webMessages.userId[0]);
        				}
        			} else {
        				$("#globalMsg").html("Unexpected response code: " + $data.responseHeader.responseCode + ". Contact Support").show();
        				$("#newSubscriberModal").dialog("close");
        			}
        		},
        		
        		
        		
        		
        		doSubscription : function($userId, $divisionId, $isChecked) {
        			console.log("doSubscription: " + $userId + " " + $divisionId + " " + $isChecked);
    				$url = "reports/subscriptionAdmin/" + $("#reportSelector select[name='reportId']").val();
       				$type = "POST";
        			if ( $isChecked == false ) {
						$type = "DELETE";
        			}
        			ANSI_UTILS.makeServerCall($type, $url, JSON.stringify({"userId":$userId,"divisionId":$divisionId}), {200:SUBSCRIPTION_ADMIN.doSubscriptionSuccess}, {});
        		},
        		
        		
        		doSubscriptionSuccess : function($data, $passthru) {
        			console.log("doSubscriptionSuccess");
        			if ( $data.responseHeader.responseCode == "SUCCESS" ) {
        				$("#globalMsg").html("Success!").show().fadeOut(4000);
        			} else if ($data.responseHeader.responseCode == "EDIT_FAILURE" ) {
        				$("#globalMsg").html("Invalid system state. Reload this page and try again").show();
        			} else {
        				$("#globalMsg").html("Unexpected response code: " + $data.responseHeader.responseCode + ". Contact Support").show();
        			}
        		},
        		
        		
        		
        		makeColumns : function($data, $passthru) {
        			SUBSCRIPTION_ADMIN.columnList = []
        			SUBSCRIPTION_ADMIN.columnList.push( { title: "Last Name", width:"20%", searchable:true, "defaultContent": "<i>N/A</i>", data:'last_name' } );
        			SUBSCRIPTION_ADMIN.columnList.push( { title: "First Name", width:"25%", searchable:true, "defaultContent": "<i>N/A</i>", data:'first_name' } );
        			$.each( $data.data.divisionList, function($index, $division) {
        				var $column = {};
        				$column["title"] = $division.division_code;
        				$column["width"] = "5%";
        				$column["searchable"] = false;
        				$column["defaultContent"] = "";        				
        				var $func = function(row,type,set) {var $isChecked = row[$division.division_code] == $division.division_id; var $checked = ""; if ( $isChecked ) { $checked = "checked='checked'"; } return '<input type="checkbox" name="subscribe" data-userid='+row["user_id"]+' data-divisionid='+$division.division_id+' '+$checked +' />'; }
        				$column["data"] = $func; //$division.division_code; //SUBSCRIPTION_ADMIN.makeCheckbox();
        				SUBSCRIPTION_ADMIN.columnList.push($column);
        			});
        		},

        		
        		makeNewSubscriber : function() {
        			console.log("makeNewSubscriber");
        			
        			if ( ! $("#newSubscriberModal").hasClass("ui-dialog-content") ) {
       					$( "#newSubscriberModal" ).dialog({
               				title:'New Report Subscriber',
               				autoOpen: false,
               				height: 250,
               				width: 500,
               				modal: true,
               				closeOnEscape:true,
               				//open: function(event, ui) {
               				//	$(".ui-dialog-titlebar-close", ui.dialog | ui).hide();
               				//},
               				buttons: [
               					{
               						id:  "newSubscriberCancel",
               						click: function($event) {
             							$( "#newSubscriberModal" ).dialog("close");
               						}
               					},{
               						id:  "newSubscriberSave",
               						click: function($event) {
               							SUBSCRIPTION_ADMIN.doNewSubscription();
               						}
               					}
               				]
               			});	
               			$("#newSubscriberCancel").button('option', 'label', 'Cancel');  
               			$("#newSubscriberSave").button('option', 'label', 'Confirm');
               			
               			var $ansiDisplaySelector = $("#newSubscriberModal input[name='newSubscriber']");          			
               			$( $ansiDisplaySelector ).autocomplete({
    						'source':"ansiUserAutoComplete?",
    						position:{my:"left top", at:"left bottom",collision:"none"},
    						appendTo:"#node-crud-form",
    						select: function( event, ui ) {
    							$('#newSubscriberModal input[name="userId"]').val(ui.item.id);
    							if ( ui.item.value == null || ui.item.value.trim() == "" ) {
    								$($contactDisplaySelector).val(ui.item.label);
    							}
           			      	},
           			      	response: function(event, ui) {
    							if (ui.content.length === 0) {
    								alert("Nobody here by that name");
    								$('#newSubscriberModal input[name="userId"]').val("");
    							}
    						}
           			 	});
       				}
        			
        			$("#newSubscriberModal .err").html("");
        			$("#newSubscriberModal .reportId").html($("#reportSelector select[name='reportId'] option:selected").text());
        			$("#newSubscriberModal input[name='reportId']").val($("#reportSelector select[name='reportId']").val());
        			$("#newSubscriberModal input[name='userId']").val("");
        			$("#newSubscriberModal input[name='newSubscriber']").val("");
        			$("#newSubscriberModal").dialog("open");
        		},
        		
        		
        		
        		makeSubscriptionTable : function() {
        			var $reportId = $("#reportSelector select[name='reportId']").val();
        			console.log("makeSubscriptionTable: " + $reportId);
        			var $url = "reports/subscriptionAdmin/" + $reportId;
        			
        			$("#subscriptionLookup").DataTable( {        				
               			"aaSorting":		[[0,'asc']],
               			"processing": 		true,
               	        "serverSide": 		true,
               	        "autoWidth": 		false,
               	        "deferRender": 		true,
               	        "scrollCollapse": 	true,
               	        "scrollX": 			true,
               	        //"pageLength":		50,
            	        rowId: 				'dt_RowId',
               	        destroy : 			true,		// this lets us reinitialize the table
               	        dom: 				'Bfrtip',
               	        "searching": 		false,
               	        "searchDelay":		800,
               	        //lengthMenu: [
               	        //	[ 10, 50, 100, 500, 1000 ],
               	        //    [ '10 rows', '50 rows', '100 rows', '500 rows', '1000 rows' ]
               	        //],
               	        buttons: [
              	        	'pageLength',
              	        	'copy', 
              	       		'csv', 
              	       		'excel', 
              	       		{extend: 'pdfHtml5', orientation: 'landscape'}, 
              	       		'print',{extend: 'colvis',	label: function () {doFunctionBinding();$('#timesheetLookup').draw();}},
              	       		{
   	        	        		text:'Refresh',
   	        	        		action: function(e, dt, node, config) {
   	        	        			$("#subscriptionLookup").DataTable().ajax.reload();      	        			
   	        	        		}
   	        	        	},
              	       		{
   	        	        		text:'New',
   	        	        		action: function(e, dt, node, config) {
   	        	        			SUBSCRIPTION_ADMIN.makeNewSubscriber();
   	        	        		}
   	        	        	}
               	        ],
               	        "columnDefs": [
               	        	{ "orderable": true, "targets": -1 },                	            
               	        	{ className: "dt-left", "targets": []},
               	            { className: "dt-center", "targets": []},
               	            { className: "dt-right", "targets": []}
               	        ],
               	        "paging": false,
       			        "ajax": {
       			        	"url": $url,
       			        	"type": "GET",
       			        	"data": {},
       			        	"dataSrc":"data.subscriptions"
       			        },
       			        columns: SUBSCRIPTION_ADMIN.columnList,
       			        "initComplete": function(settings, json) {
       			        	var myTable = this;
       			           	//LOOKUPUTILS.makeFilters(myTable, "#filter-container", "#timesheetLookup", PROFILELOOKUP.makeLookup);
       			           	$("th").removeClass("dt-right");
       			           	$("th").removeClass("dt-left");
       			           	$("th").addClass("dt-center");        			            	
       		         	},
       			        "drawCallback": function( settings ) {
       			         	$("#subscriptionLookup input[type='checkbox']").off("click");
       			         	$("#subscriptionLookup input[type='checkbox']").click(function($event) {
       			         		var $userId = $(this).attr("data-userid");
       			         		var $divisionId = $(this).attr("data-divisionid");
       			         		var $isChecked = $(this).prop("checked");
       			         		SUBSCRIPTION_ADMIN.doSubscription($userId, $divisionId, $isChecked);
       			         	});
       			        }
       			    });
        		},
       		}

	        SUBSCRIPTION_ADMIN.init();
        	
        });
        </script>        
    </tiles:put>
    
   <tiles:put name="content" type="string">
    	<h1><bean:message key="page.label.reportSubscription" /> Admin</h1>
		
		<div id="reportSelector">
			<span class="label">Report: </span>
			<select name="reportId">
				<option value=""></option>
				<ansi:batchReports  />
			</select>
	    </div>
	    
	    <div id="subscriptionLookupContainer">
		    <table id="subscriptionLookup">
		    	<thead></thead>
		    	<tbody></tbody>
		    	<tfoot></tfoot>
		    </table>
    	</div>
    	
    	<webthing:scrolltop />
    	
    	<div id="newSubscriberModal">
    		<div class="label reportId" style="width:100%; text-align:center;"></div>
    		<br />
    		<table>
    			<tr>
    				<td><span class="label">Subscriber: </span></td>
    				<td><input type="text" name="newSubscriber" /></td>
    				<td><span class="err newSubscriberErr"></span></td>
    			</tr>
    			<tr>
    				<td><span class="label">Division: </span></td>
    				<td>
    					<select name="newDivisionId">
    						<option value=""></option>
    						<ansi:selectOrganization type="division" active="true" />    						
    					</select>
    				</td>
    				<td><span class="err newDivisionIdErr"></span></td>
    			</tr>
    		</table>
    		<input type="hidden" name="reportId" /><br />
    		<input type="hidden" name="userId" /><br />
    	</div>
    </tiles:put>
		
</tiles:insert>

