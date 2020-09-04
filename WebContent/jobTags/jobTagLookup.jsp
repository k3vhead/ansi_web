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
        <bean:message key="page.label.jobTag" /> <bean:message key="menu.label.lookup" />
    </tiles:put>
    
    
    <tiles:put name="headextra" type="string">
       	<link rel="stylesheet" href="css/lookup.css" />
    	<link rel="stylesheet" href="css/ticket.css" />
    	<script type="text/javascript" src="js/ansi_utils.js"></script>
    	<script type="text/javascript" src="js/addressUtils.js"></script>
    	<script type="text/javascript" src="js/lookup.js"></script> 
    
        <style type="text/css">
        	#table-container {
        		width:100%;
        	}
			#filter-container {
        		width:402px;
        		float:right;
        	}			
        	#jobtag-crud-modal {
        		display:none;
        	}
        	#jobtag-delete-modal {
        		display:none;
        		text-align:center;
        	}
			.dataTables_wrapper {
				padding-top:10px;
			}	
			.form-label {
				font-weight:bold;
			}		
        </style>
        
        <script type="text/javascript">    
        
        $(document).ready(function(){
        	;JOBTAGLOOKUP = {
        		dataTable : null,
        		jobTagTypesList : [],
        		jobTagStatusList : [],
        		
        		init : function() {
        			JOBTAGLOOKUP.makeTable();
        			JOBTAGLOOKUP.makeClickers();   
        			ANSI_UTILS.getOptionList("JOBTAG_TYPE,JOBTAG_STATUS", JOBTAGLOOKUP.makeOptionList);
        			JOBTAGLOOKUP.makeModal();
        		},
        		
        		
        		
        		deleteTag : function() {
        			var $tagId = $("#jobtag-delete-modal").attr("data-id");
        			var $url = "jobtag/jobTag/" + $tagId;
            		ANSI_UTILS.doServerCall("delete", $url, null, JOBTAGLOOKUP.deleteTagSuccess, JOBTAGLOOKUP.deleteTagFailure);
        		},
        		
        		
        		deleteTagFailure : function($data) {
        			$("#globalMsg").html("System Error: Invalid job tag. Reload page and try again").show();        		
        			$("#jobtag-delete-modal").dialog("close");
        		},
        		
        		
        		
        		deleteTagSuccess : function($data) {
        			$("#globalMsg").html("Success!").show().fadeOut(5000);
            		$('#jobtag-lookup-table').DataTable().ajax.reload();
            		$("#jobtag-delete-modal").dialog("close");
        		},
        		
        		
        		
        		
        		
        		doFunctionBinding : function() {
        			$(".jobtag-edit-action-link").click( function($event) {
        				var $tagId = $(this).attr("data-id");
        				ANSI_UTILS.doServerCall("get", "jobtag/jobTag/"+$tagId, null, JOBTAGLOOKUP.getTagSuccess, JOBTAGLOOKUP.getTagFailure);
        			});
        			$(".jobtag-delete-action-link").click( function($event) {
        				var $tagId = $(this).attr("data-id");
        				$("#jobtag-delete-modal").attr("data-id", $tagId);
        				$("#jobtag-delete-modal").dialog("open");
        			});
    			},
    			

    			
    			
    			getTagFailure : function($data) {
    				$("#globalMsg").html("System Error: Invalid job tag. Reload page and try again").show();
    			},
    			
    			
    			
    			getTagSuccess : function($data) {
    				console.log("getTagSuccess");
    				JOBTAGLOOKUP.resetModal();
    				$("#tagId").html($data.data.item.tagId)
    				$("#jobtag-crud-modal input[name='tagId']").val($data.data.item.tagId);
    				$("#jobtag-crud-modal input[name='name']").val($data.data.item.name);
    				$("#jobtag-crud-modal input[name='description']").val($data.data.item.description);
    				$("#jobtag-crud-modal select[name='tagType']").val($data.data.item.tagType);
    				$("#jobtag-crud-modal select[name='status']").val($data.data.item.status);
    				$("#jobtag-crud-modal").dialog("open");
    			},
    			
    			
    			
    			
    			makeClickers : function() {
    				$('.ScrollTop').click(function() {
    					$('html, body').animate({scrollTop: 0}, 800);
    					return false;
    	       	    });
    				
    				
    				$("#new-jobtag-button").click(function() {
    					console.log("new-jobtag-button.click()")
    					JOBTAGLOOKUP.resetModal();
    					$("#jobtag-crud-modal").dialog("open");
    				});
    			},
        		
        		
        		
    			makeOptionList : function($data) {
    				console.log("makeOptionList");
    				JOBTAGLOOKUP.jobTagTypesList = $data.jobTagType;
    				JOBTAGLOOKUP.jobTagStatusList = $data.jobTagStatus;
    			},
        		
        		
    			
    			makeModal : function() {
    				$( "#jobtag-crud-modal" ).dialog({
						title:'Job Tag',
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
								id: "jobtag-cancel-button",
								click: function($event) {
									$( "#jobtag-crud-modal" ).dialog("close");
								}
							},
							{
								id: "jobtag-save-button",
								click: function($event) {
									JOBTAGLOOKUP.saveTag();
								}
							},
							
						]
					});	
					$("#jobtag-save-button").button('option', 'label', 'Save');
					$("#jobtag-cancel-button").button('option', 'label', 'Cancel');
					
					
					
					
    				$( "#jobtag-delete-modal" ).dialog({
						title:'Confirm',
						autoOpen: false,
						height: 200,
						width: 300,
						modal: true,
						closeOnEscape:true,
						//open: function(event, ui) {
						//	$(".ui-dialog-titlebar-close", ui.dialog | ui).hide();
						//},
						buttons: [
							{
								id: "delete-cancel-button",
								click: function($event) {
									$( "#jobtag-delete-modal" ).dialog("close");
								}
							},
							{
								id: "delete-save-button",
								click: function($event) {
									JOBTAGLOOKUP.deleteTag();
								}
							},
							
						]
					});	
					$("#delete-save-button").button('option', 'label', 'Yes');
					$("#delete-cancel-button").button('option', 'label', 'No');
    			},
    			
    			
    			
    			
        		
        		makeTable : function(){
            		JOBTAGLOOKUP.dataTable = $('#jobtag-lookup-table').DataTable( {
            			"aaSorting":		[[1,'asc'], [2,'asc']],
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
            	        "pageLength":		50,
            	        lengthMenu: [
            	        	[ 10, 50, 100, 500, 1000 ],
            	            [ '10 rows', '50 rows', '100 rows', '500 rows', '1000 rows' ]
            	        ],
            	        buttons: [
            	        	'pageLength','copy', 'csv', 'excel', {extend: 'pdfHtml5', orientation: 'landscape'}, 'print',{extend: 'colvis',	label: function () {doFunctionBinding();}}
            	        ],
            	        
            	        "columnDefs": [
             	            { "orderable": false, "targets": -1 },
            	            { className: "dt-left", "targets": [1,2,3] },
            	            { className: "dt-center", "targets": [0,4,5,6] },
            	            { className: "dt-right", "targets": []}
            	         ],
            	        "paging": true,
    			        "ajax": {
    			        	"url": "jobtag/jobTagLookup",
    			        	"type": "GET",
    			        	"data": {}
    			        	},
    			        columns: [
    			            { width:"5%", title: "ID", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {	
    			            	if(row.tag_id != null){return row.tag_id;}
    			            } },
    			            { width:"12%", title: "Type", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {	
    			            	if(row.type_display != null){return row.type_display;}
    			            } },
    			            { width:"25%", title: "Name", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            	if(row.name != null){return row.name;}
    			            } },
    			            { width:"25%", title: "Description", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            	if(row.description != null){return row.description;}
    			            } },
    			            { width:"12%", title: "Status", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            	if(row.tag_status != null){return row.tag_status;}
    			            } },
    			            { width:"12%", title: "Job Count", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            	if(row.job_count != null){return (row.job_count+"");}
    			            } },
    			            { width:"10%", title: "<bean:message key="field.label.action" />",  data: function ( row, type, set ) {
    			            	var $editLink = "";
    			            	var $deleteLink = "";
    			            	
    			            	var $editLink = '<a href="#" class="jobtag-edit-action-link" data-id="'+row.tag_id+'"><webthing:edit>Edit</webthing:edit></a>';
    			            	if ( row.can_delete ) {
    			            		$deleteLink = '<a href="#" class="jobtag-delete-action-link" data-id="'+row.tag_id+'"><webthing:delete>Delete</webthing:delete></a>';
    			            	}
    			            	return '<ansi:hasPermission permissionRequired="JOBTAG_WRITE">' + $editLink + $deleteLink + '</ansi:hasPermission>';
    			            } }],
    			            "initComplete": function(settings, json) {
    			            	console.log("initComplete");
    			            	//console.log(json);
    			            	//doFunctionBinding();
    			            	var myTable = this;
    			            	LOOKUPUTILS.makeFilters(myTable, "#filter-container", "#jobtag-lookup-table", JOBTAGLOOKUP.makeTable);
    			            },
    			            "drawCallback": function( settings ) {    			            	
    			            	console.log("drawCallback");
    			            	//$("#searching-modal").dialog("close");
    			            	JOBTAGLOOKUP.doFunctionBinding();
    			            }
    			    } );
            	},
            	
            	
            	
            	resetModal : function() {            		
            		var $select = $("#jobtag-crud-modal select[name='tagType']");            		
					$('option', $select).remove();
					$select.append(new Option("",""));
					$.each(JOBTAGLOOKUP.jobTagTypesList, function($index, $value) {
						$select.append(new Option($value.display, $value.NAME));
					});
					
            		var $select = $("#jobtag-crud-modal select[name='status']");            		
					$('option', $select).remove();
					$select.append(new Option("",""));
					$.each(JOBTAGLOOKUP.jobTagStatusList, function($index, $value) {
						$select.append(new Option($value.display, $value.NAME));
					});

					$("#tagId").html("");
					$("#jobtag-crud-modal input").val("");
            		$("#jobtag-crud-modal select").val("");
            		$("#jobtag-crud-modal .err").html("");
            	},
            	
            	
            	
            	saveTag : function() {
            		console.log("saveTag");
            		var $tagId = $("#jobtag-crud-modal input[name='tagId']").val();
            		var $outbound = {
            				"tagId":$tagId,
		    				"name":$("#jobtag-crud-modal input[name='name']").val(),
		    				"description":$("#jobtag-crud-modal input[name='description']").val(),
		    				"tagType":$("#jobtag-crud-modal select[name='tagType']").val(),
		    				"status":$("#jobtag-crud-modal select[name='status']").val(),
	            		}
            		var $url = "jobtag/jobTag";
					if ( $tagId != null && $tagId != "" ) {
						$url = $url + "/" + $tagId;
					}            		
            		ANSI_UTILS.doServerCall("post", $url, JSON.stringify($outbound), JOBTAGLOOKUP.saveTagSuccess, JOBTAGLOOKUP.saveTagFailure);
            		
            	},
            	
            	
            	
            	saveTagFailure : function($data) {
            		console.log("saveTagFailure");            		
            		$.each($data.data.webMessages, function($index, $value) {
            			var $selector = "#jobtag-crud-modal ." + $index;
            			$($selector).html($value[0]);
            		});
            	},
            	
            	
            	saveTagSuccess : function($data) {
            		console.log("saveTagSuccess");
            		$("#globalMsg").html("Success!").show().fadeOut(5000);
            		$('#jobtag-lookup-table').DataTable().ajax.reload();
            		$("#jobtag-crud-modal").dialog("close");
            	},
            	
        	};
        	
        	JOBTAGLOOKUP.init();
        	
        	
        });
        		
        </script>        
    </tiles:put>
    
   <tiles:put name="content" type="string">
    	<h1><bean:message key="page.label.jobTag" /> <bean:message key="menu.label.lookup" /></h1> 

    	
    	  	
	 	<webthing:lookupFilter filterContainer="filter-container" />
		<div id="table-container">
		 	<table id="jobtag-lookup-table" class="display" cellspacing="0" style="table-layout: fixed; font-size:9pt;min-width:1300px; max-width:1300px;width:1300px;">
		        <thead></thead>
		        <tbody></tbody>
		        <tfoot></tfoot>
		    </table>
		    <ansi:hasPermission permissionRequired="JOBTAG_WRITE">
		    <input type="button" class="prettyWideButton" id="new-jobtag-button" value="New" />
			</ansi:hasPermission>	    
	    </div>
	    
	    <webthing:scrolltop />
	

		<ansi:hasPermission permissionRequired='JOBTAG_WRITE'>
			<div id="jobtag-crud-modal">
				<div id="modal-message" class="err"></div>			
				<form id="crudForm">
					<input type="hidden" name="tagId" class="userField" />
					<table>
						<tr>
							<td><span class="formLabel">Tag Id:</span></td>
							<td><span class="displayField userId" id="tagId"></span></td>
							<td><span class="err errField tagId"></span></td>
						</tr>
						<tr>
							<td><span class="formLabel">Type:</span></td>
							<td><select class="userField" name="tagType"></select></td>
							<td><span class="err errField tagType"></span></td>
						</tr>
						<tr>
							<td><span class="formLabel">Name:</span></td>
							<td><input type="text" class="userField" name="name"/></td>
							<td><span class="err errField name"></span></td>
						</tr>
						<tr>
							<td><span class="formLabel">Description:</span></td>
							<td><input type="text" class="userField" name="description"/></td>
							<td><span class="err errField description"></span></td>
						</tr>
						<tr>
							<td><span class="formLabel">Status:</span></td>
							<td><select class="userField" name="status"></select></td>
							<td><span class="err errField status"></span></td>
						</tr>					
					</table>
				</form>
			</div>
			
			<div id="jobtag-delete-modal">
				Delete this job tag?
			</div>
		</ansi:hasPermission>
		
    </tiles:put>
		
</tiles:insert>

