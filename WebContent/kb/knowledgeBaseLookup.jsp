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
        Knowledge Base
    </tiles:put>
    
    
    <tiles:put name="headextra" type="string">
       	<link rel="stylesheet" href="css/lookup.css" />
    	<script type="text/javascript" src="js/ansi_utils.js"></script>
    	<script type="text/javascript" src="js/lookup.js"></script>
    	<script type="text/javascript" src="js/knowledgebase.js"></script>  
    	<!--   <script type="text/javascript" src="js/tinymce/tinymce.min.js"></script> -->
    	
    
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
        	
			
			#confirm-modal {
        		display:none;
        	}
			.action-link {
				cursor:pointer;
			}
			.formHdr {
				font-weight:bold;
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
        	
        	KNOWLEDGEBASE_LOOKUP = {
                dataTable : null,

       			init : function() {
       				KNOWLEDGEBASE_LOOKUP.makeDataTable();  
       				KNOWLEDGEBASE_LOOKUP.makeClickers();
       				// temp remove tinymce
       				//tinymce.init({
       				//	selector: "#editForm textarea[name='kbContent']", //"#editForm textarea[name='kbContent']",
       				//	menubar: false,
       				//	plugins: "table, preview, anchor, link, image, code", //, hr, textcolor",
                    //    image_advtab:true,
                    //    image_list: "mediaList.js?type=IMAGE",
                    //    tools: "inserttable",
                    //    toolbar1 : "bold italic underline strikethrough | alignleft aligncenter alignright | table | bullist numlist outdent indent blockquote | undo redo | removeformat | subscript superscript",
                    //    toolbar2 : "cut copy paste | preview | link anchor | image | styleselect fontselect fontsizeselect | hr forecolor | code ",
       				//});
       				$("#editForm").hide();
                }, 
				
                
                
    			clearEditForm : function () {
    				$.each( $('#editForm').find("input"), function(index, $inputField) {
    					$fieldName = $($inputField).attr('name');
    					if ( $($inputField).attr("type") == "text" ) {
    						$($inputField).val("");
    					}
    				});
    				$("#editForm select").val("");
    				$("#editForm textarea").val("");
    				$('.err').html("");
                },

                
                doDelete : function() {
                	console.log("doDelete");
                	var $kbTagName = $("#confirm-modal input[name='kbTagName']").val();
                	var $languageCode = $("#confirm-modal input[name='languageCode']").val();
                	var $url = "knowledgeBase/" + $kbTagName + "/" + $languageCode;
                	console.log($url);
                	var $callbacks = {200:KNOWLEDGEBASE_LOOKUP.doDeleteSuccess};
                	var $passThruData = {};
                	ANSI_UTILS.makeServerCall("DELETE", $url, {}, $callbacks, $passThruData);
                },
                
                
                doDeleteSuccess : function($data, $passthru) {
                	console.log("doDeleteSuccess");
                	$("#confirm-modal").dialog("close");
                	$("#globalMsg").html("Success").show().fadeOut(3000); 
            		$('#kbTable').DataTable().ajax.reload();                	
                },
                
                
                doFunctionBinding : function() {
	            	$(".editAction").off("click");
	    			$(".editAction").on( "click", function($clickevent) {
	    				$clickevent.preventDefault()
						var $kbKey = $(this).attr("data-name");
						var $kbLanguage = $(this).attr("data-language");
						KNOWLEDGEBASE_LOOKUP.showEditModal($kbKey, $kbLanguage);
	    			});		
	    			$(".deleteAction").off("click");
	    			$(".deleteAction").on( "click", function($clickevent) {
	    				$clickevent.preventDefault()
						var $kbKey = $(this).attr("data-name");
						var $kbLanguage = $(this).attr("data-language");
						KNOWLEDGEBASE_LOOKUP.showConfirmModal($kbKey, $kbLanguage);
	    			});
	    		},
	    		
	    		
	    		
                doUpdate : function() {
                	console.log("doUpdate");
                	var $outbound = {};
                	$.each( $('#editForm').find("input[type='text']"), function(index, $inputField) {
    					$fieldName = $($inputField).attr('name');
						var $val = $($inputField).val();
						$outbound[$fieldName] = $val;
    				});
                	$.each( $('#editForm').find("select"), function(index, $inputField) {
    					$fieldName = $($inputField).attr('name');
						var $val = $($inputField).val();
						$outbound[$fieldName] = $val;
    				});
                	$.each( $('#editForm').find("textarea"), function(index, $inputField) {
    					$fieldName = $($inputField).attr('name');
						var $val = $($inputField).val();
						$outbound[$fieldName] = $val;
    				});
                	console.log( JSON.stringify($outbound));
                	
                	var $callbacks = {200:KNOWLEDGEBASE_LOOKUP.doUpdateSuccess};
                	var $passThruData = {};
                	ANSI_UTILS.makeServerCall("POST", "knowledgeBase", JSON.stringify($outbound), $callbacks, $passThruData);
                },
                
                
                
                
                doUpdateSuccess($data, $passthru) {
                	console.log("doUpdateSuccess");	
                	if ( $data.responseHeader.responseCode == 'SUCCESS' ) {
                		$('#editForm').dialog("close");
                		$("globalMsg").html("Success").show().fadeOut(3000); 
                		$('#kbTable').DataTable().ajax.reload();
                	} else if ( $data.responseHeader.responseCode == 'EDIT_FAILURE' ) {
                		$("#editForm .err").html("");
                		$.each( $data.data.webMessages, function($key, $value) {
                			var $field = "#editForm ." + $key + "Err";
                			console.log($field);
                			$($field).html($value[0]);
                		});
                	} else {
                		$("globalMsg").html("Invalid response code: " + $data.responseHeader.responseCode + ". Contact support").show();
                	}
                },
                
                
                
                
                
                getKB : function($key, $language) {
                	console.log("getKB");
                	var $url = "knowledgeBase/" + $key + "/" + $language;
                	var $callbacks = {
               			200:KNOWLEDGEBASE_LOOKUP.getKBSuccess,
               			404:KNOWLEDGEBASE_LOOKUP.getKBFail404
               		};
                	ANSI_UTILS.makeServerCall("GET", $url, {}, $callbacks, {});
                },
                
                
                
                getKBFail404 : function($data, $passthru) {
                	console.log("getKBFail");
					$("#globalMsg").html("Record Not found; Reload page and try again").show().fadeOut(3000);
                },
                
                
                
                getKBSuccess : function($data, $passthru) {
                	console.log("getKBSuccess");
                	$("#editForm select[name='kbTagName']").val($data.data.kbTagName);
                	$("#editForm input[name='languageCode']").val($data.data.languageCode);
                	$("#editForm input[name='kbTitle']").val($data.data.kbTitle);
                	$("#editForm select[name='kbStatus']").val($data.data.kbStatus);
                	$("#editForm textarea[name='kbContent']").html($data.data.kbContent);
                	$("#editForm").dialog("option","title", "Update Knowledge").dialog("open");
                },
                
                
                
                
                makeClickers : function() {
                	console.log("makeClickers");
	            	$('.ScrollTop').click(function() {
	        			$('html, body').animate({scrollTop: 0}, 800);
	        			return false;
	               	});
	            	
	            	$("#editForm input[name='editCancelButton']").click(function($clickevent) {
	            		$("#editForm").hide();	
	            		$('#kbTable_wrapper').show();
	            		$(".showEditButton").show();
	            	});
	            	
	            	$(".showEditButton").click(function() {
	            		KNOWLEDGEBASE_LOOKUP.showEditModal();
	            	});
	            	
                },
                
                
                xxxxx : function() {
	            	var $localeComplete = $( "#parentName" ).autocomplete({
	    				source: function(request,response) {
	    					term = $("#editForm input[name='parentName']").val();
	    					localeTypeId = $("#editForm select[name='localeTypeId']").val();
	    					stateName = $("#editForm select[name='stateName']").val();
	    					$.getJSON("localeAutocomplete", {"term":term, "localeTypeId":localeTypeId, "stateName":stateName}, response);
	    				},
	                    minLength: 2,
	                    select: function( event, ui ) {
	                    	$("#editForm input[name='parentId']").val(ui.item.id);
	                    },
	                    response: function(event, ui) {
	                        if (ui.content.length === 0) {
	                        	$("#globalMsg").html("No Matching Locale");
	                        	$("#editForm input[name='parentId']").val("");
	                        } else {
	                        	$("#globalMsg").html("");
	                        }
	                    }
	              	}).data('ui-autocomplete');	            	
	                
	    			$localeComplete._renderMenu = function( ul, items ) {
	    				var that = this;
	    				$.each( items, function( index, item ) {
	    					that._renderItemData( ul, item );
	    				});
	    				if ( items.length == 1 ) {
	    					$("#editForm input[name='parentId']").val(items[0].id);
	    					$("#parentName").autocomplete("close");
	    				}
	    			}
	            },
                
                
                
                makeDataTable : function(){
                	console.log("makeDataTable");
					var dataTable = $('#kbTable').DataTable( {
            			"aaSorting":		[[0,'asc'],[1,'asc']],
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
            	            { className: "dt-head-left", "targets": [0,1,2,3,4] },
            	            { className: "dt-body-center", "targets": [] },
            	            { className: "dt-right", "targets": []}
            	         ],
            	        "paging": true,
    			        "ajax": {
    			        	"url": "knowledgeBase/lookup",
    			        	"type": "GET",
    			        	"data": {}
    			        	},
    			        columns: [
    			            { width:"10%", title: "Key", "defaultContent": "<i>N/A</i>", searchable:true, data: "kb_key"},
    			            { width:"10%", title: "Language", "defaultContent": "<i>N/A</i>", searchable:true, data: "kb_language"},
    			            { width:"5%", title: "Status", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            	var $value = "";
    			            	if(row.kb_status != null){
    			            		if ( row.kb_status == 1 ) {
    			            			$value = '<webthing:checkmark>Active</webthing:checkmark>';
    			            		} else if ( row.kb_status == 0) {
    			            			$value = '<webthing:ban>Inactive</webthing:ban>';
    			            		} else {
    			            			$value = row.kb_status;
    			            		}
    			            	}
    			            	return $value;
    			            } },
    			            { width:"15%", title: "Title" , "defaultContent": "<i>N/A</i>", searchable:true, data: "kb_title"},
    			            { width:"45%", title: "Content" , "defaultContent": "<i>N/A</i>", searchable:true, data: "abbreviatedContent"},
    			            { width:"5%", title: "<bean:message key="field.label.action" />",  data: function ( row, type, set ) {	
    			            	//console.log(row);
    			            	var $actionData = "";
   				            	var $editLink = '<ansi:hasPermission permissionRequired="KNOWLEDGE_WRITE"><a href="#" class="editAction" data-language="'+row.kb_language+'" data-name="'+row.kb_key+'"><webthing:edit>Edit</webthing:edit></a></ansi:hasPermission>';
				            	var $deleteLink = '<ansi:hasPermission permissionRequired="KNOWLEDGE_WRITE"><a href="#" class="deleteAction" data-language="'+row.kb_language+'" data-name="'+row.kb_key+'"><webthing:delete>Delete</webthing:delete></a></ansi:hasPermission>';
    			            	return $editLink + $deleteLink;
    			            } }],
    			            "initComplete": function(settings, json) {
    			            	//console.log(json);
    			            	//doFunctionBinding();
    			            	var myTable = this;
    			            	LOOKUPUTILS.makeFilters(myTable, "#filter-container", "#kbTable", KNOWLEDGEBASE_LOOKUP.makeDataTable);
    			            },
    			            "drawCallback": function( settings ) {
    			            	KNOWLEDGEBASE_LOOKUP.doFunctionBinding();    			            	
    			            }
    			    } );
            	},
                
                
            	
            	showEditModalXXXX : function($key, $language) {
            		console.log("showEditModal");
            		$('#kbTable_wrapper').hide();
            		$(".showEditButton").hide();
					$("#editForm").show();
					if ( $key == null || $key == "" ) {
						if ( $language == null || $language == "" ) {
							// this is an add function
							KNOWLEDGEBASE_LOOKUP.clearEditForm();
							$("#editForm ").dialog("option","title", "Create Knowledge").dialog("open");
						} else {
							// language but no key
							$("#globalMsg").html("Invalid system state; reload and try again");
						}
					} else {
						if ( $key == null || $key == "" ) {
							// key but no language
							$("#globalMsg").html("Invalid system state; reload and try again");
						} else {
							// this is an edit function
							KNOWLEDGEBASE_LOOKUP.getKB($key, $language);
						}
					}
				},	
				
				
                
            	showEditModal : function($key, $language) {
            		console.log("showEditModal");
					if ( ! $("#editForm").hasClass('ui-dialog-content') ) {
						$("#editForm" ).dialog({
							autoOpen: false,
							height: 650,
							width: 900,
							modal: true,
							buttons: [
								{
									id: "editFormCancelButton",
									click: function() {
										$("#editForm").dialog( "close" );
									}
								},{
									id: "editFormGoButton",
									click: function($event) {
										KNOWLEDGEBASE_LOOKUP.doUpdate();
									}
								}	      	      
							],
							close: function() {
								$("#editForm").dialog( "close" );
							}
						});
						$("#editFormCancelButton").button('option', 'label', 'Cancel');  
	        			$("#editFormGoButton").button('option', 'label', 'Confirm');
					}
					
					if ( $key == null || $key == "" ) {
						if ( $language == null || $language == "" ) {
							// this is an add function
							KNOWLEDGEBASE_LOOKUP.clearEditForm();
							$("#editForm ").dialog("option","title", "Create Knowledge").dialog("open");
						} else {
							// language but no key
							$("#globalMsg").html("Invalid system state; reload and try again");
						}
					} else {
						if ( $key == null || $key == "" ) {
							// key but no language
							$("#globalMsg").html("Invalid system state; reload and try again");
						} else {
							// this is an edit function
							KNOWLEDGEBASE_LOOKUP.getKB($key, $language);
						}
					}
				},	
                
                
                
                showConfirmModal : function($key, $language) {
                	console.log("showConfirmModal");
                	console.log("showEditModal");
					if ( ! $("#confirm-modal").hasClass('ui-dialog-content') ) {
						$("#confirm-modal" ).dialog({
							autoOpen: false,
							title:"Confirm Delete",
							height: 200,
							width: 250,
							modal: true,
							buttons: [
								{
									id: "confirmCancelButton",
									click: function() {
										$("#confirm-modal").dialog( "close" );
									}
								},{
									id: "confirmGoButton",
									click: function($event) {
										KNOWLEDGEBASE_LOOKUP.doDelete();
									}
								}	      	      
							],
							close: function() {
								$("#confirm-modal").dialog( "close" );
							}
						});
						$("#confirmCancelButton").button('option', 'label', 'No');  
	        			$("#confirmGoButton").button('option', 'label', 'Yes');
					}
					$("#confirm-modal input[name='kbTagName']").val($key);
					$("#confirm-modal input[name='languageCode']").val($language);
					$("#confirm-modal").dialog("open");
                },
                
                
                
                
                
        	}
	        
	        	KNOWLEDGEBASE_LOOKUP.init();
	        });
	        
	        		
	        </script>        
    </tiles:put>
    
   <tiles:put name="content" type="string">
    	<h1>Knowledge Base</h1>
    	
    	
    	  	
    	  	
 	<webthing:lookupFilter filterContainer="filter-container" />

 	<table id="kbTable" style="table-layout: fixed" class="display" cellspacing="0" style="font-size:9pt;max-width:1300px;width:1300px;">
        <thead>
        </thead>
        <tfoot>
        </tfoot>
    </table>
    <ansi:hasPermission permissionRequired="KNOWLEDGE_WRITE">
    <input type="button" class="prettyWideButton showEditButton" value="New" />
    </ansi:hasPermission>
    
    <div id="editForm">
    	<input type="hidden" name="abbreviation" value="" />
    	<table class="ui-front">
    		<tr>
    			<td><span class="formHdr">ID</span></td>
    			<td>
    				<select name="kbTagName">
    					<option value=""></option>
    					<webthing:knowledgeBaseSelect />
    				</select>
				</td>
				<td><span class="err kbTagNameErr"></span></td>
    		</tr>
    		<tr>
    			<td><span class="formHdr">Language:</span></td>
    			<td><input type="text" name="languageCode" /><webthing:knowledgeBase key="KNOWLEDGEBASE_LANGUAGE">Help</webthing:knowledgeBase></td>
    			<td><span class="err languageCodeErr"></span></td>
    		</tr>
    		<tr>
    			<td><span class="formHdr">Title:</span></td>
    			<td><input type="text" name="kbTitle" /></td>
    			<td><span class="err kbTitleErr"></span></td>
    		</tr>
    		<tr>
    			<td><span class="formHdr">Status</span></td>
    			<td>
    				<select name="kbStatus">
    					<option value=""></option>
    					<option value="1">Active</option>
    					<option value="0">Inactive</option>
    				</select>    				
    			</td>
    			<td><span class="err kbStatusErr"></span></td>
    		</tr>
    		<tr>
    			<td style="vertical-align:top;"><span class="formHdr">Content</span></td>
    			<td colspan="2">
    				<div class="newsContent">
    				<textarea name="kbContent" rows="15" cols="50"></textarea>
    				</div>
    			</td>
   			</tr>
    		<tr>
    			<td>&nbsp;</td>	
    			<td colspan="2"><span class="err kbContentErr"></span></td>
    		</tr>
    	</table>
    	
    </div>
    
    <webthing:scrolltop />

	
	
	<div id="confirm-modal">			
		<h2>Are you sure?</h2>
		<input type="hidden" name="kbTagName" />
    	<input type="hidden" name="languageCode" />
	</div>

	<!--  
	This is what the knowledge base tag should look like. It will pull the record from the 
	db with key QUOTE_PAGE_HELP and display help in a modal	
	<webthing:knowledgeBase key="QUOTE_PAGE_HELP">Help - Quotes</webthing:knowledgeBase>
	 -->
    </tiles:put>
		
</tiles:insert>

