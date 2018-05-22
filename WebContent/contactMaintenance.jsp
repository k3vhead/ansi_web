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
        <bean:message key="page.label.contact" /> <bean:message key="menu.label.lookup" />
    </tiles:put>
    
    
    <tiles:put name="headextra" type="string">
        <style type="text/css">
			#displayTable {
				width:90%;
			}
			#addFormDiv {
				display:none;
				background-color:#FFFFFF;
				color:#000000;
				width:400px;
				padding:15px;
			}
			.prettyWideButton {
				height:30px;
				min-height:30px;
			}
			select	{
				width:80px !important;
				max-width:80px !important;
			}
			#editPanel {
				display:none;
			}
			.formHdr {
				font-weight:bold;				
			}
			.editAction {
				cursor:pointer;
			}
			.swap-name {
				cursor:pointer;
			}
        </style>
        
        <script type="text/javascript">
        
        $(document).ready(function(){
        	var $ansiModal = '<c:out value="${ANSI_MODAL}" />';
        	
			$('.ScrollTop').click(function() {
				$('html, body').animate({scrollTop: 0}, 800);
				return false;
      		});
			
			$('.swap-name').click(function() {
				var $tempName = $("#editPanel input[name='firstName']").val();
				$("#editPanel input[name='firstName']").val(  $("#editPanel input[name='lastName']").val()  );
				$("#editPanel input[name='lastName']").val($tempName);
			});
			
			
			$(".showNew").click(function($event) {
				$('#goEdit').data("contactId",null);
        		$('#goEdit').button('option', 'label', 'Save');
        		$('#closeEditPanel').button('option', 'label', 'Close');
        		
				$("#editPanel input[name='businessPhone']").val("");
				$("#editPanel input[name='fax']").val("");
				$("#editPanel input[name='firstName']").val("");
				$("#editPanel input[name='lastName']").val("");
				$("#editPanel input[name='mobilePhone']").val("");
				$("#editPanel input[name='preferredContact']").val("email");
				$("#editPanel input[name='email']").val("");			        		
        		$("#editPanel .err").html("");
        		$("#editPanel").dialog("open");
			});

			
			console.debug("Getting preferred cont options");
			var $outbound = {};
			$outbound['sortBy'] = 'display';
			var jqxhr = $.ajax({
				type: 'GET',
				url: "code/contact/preferred_contact",
				data:$outbound,
				statusCode: {
					200: function($data) {
						//console.log($data);
						$select="#editPanel select[name='preferredContact']"
						$('option', $select).remove();
						$.each($data.data.codeList, function($index, $code) {
                            $($select).append(new Option($code.displayValue, $code.value));
                    	});
					},
					403: function($data) {
						$("#globalMsg").html("Session Timeout. Log in and try again");
					},
					404: function($data) {
						$("#globalMsg").html("System Error while retrieving preferred contact types");
					},
					500: function($data) {
						$("#globalMsg").html("System Error; Contact Support");
					}
				},
				dataType: 'json'
			});
            	       	
        	var dataTable = null;
        	
        	function createTable(){
        		var dataTable = $('#contactTable').DataTable( {
        			"processing": 		true,
        	        "serverSide": 		true,
        	        "autoWidth": 		false,
        	        "deferRender": 		true,
        	        "scrollCollapse": 	true,
        	        "scrollX": 			true,
        	        rowId: 				'dt_RowId',
        	        dom: 				'Bfrtip',
        	        "searching": 		true,
        	        lengthMenu: [
        	        	[ 10, 50, 100, 500, 1000 ],
        	            [ '10 rows', '50 rows', '100 rows', '500 rows', '1000 rows' ]
        	        ],
        	        buttons: [
        	        	'pageLength','copy', 'csv', 'excel', {extend: 'pdfHtml5', orientation: 'landscape'}, 'print',{extend: 'colvis',	label: function () {doFunctionBinding();}}
        	        ],
        	        "columnDefs": [
//         	            { "orderable": false, "targets": -1 },  // Need to re-add this when we add the action column back in
        	            { className: "dt-left", "targets": [0,1,2,3,4,5] },
        	            { className: "dt-center", "targets": [6] }
//        	            { className: "dt-right", "targets": [5,6,7,8,9]}
        	         ],
        	        "paging": true,
			        "ajax": {
			        	"url": "contactLookup",
			        	"type": "GET"
			        	},
			        columns: [
			        	
			            { title: "<bean:message key="field.label.lastName" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {	
			            	if(row.lastName != null){return (row.lastName+"");}
			            } },
			            { title: "<bean:message key="field.label.firstName" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.firstName != null){return (row.firstName+"");}
			            } },
			            { title: "<bean:message key="field.label.businessPhone" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.businessPhone != null){
			            		if ( row.preferredContact=='business_phone') {
			            			value = '<span style="font-weight:bold;">' + row.businessPhone + '</span>';
			            		} else {
			            			value = row.businessPhone + "";
			            		}			            		
			            		return (value);
			            	}
			            } },
			            { title: "<bean:message key="field.label.email" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.businessPhone != null){
			            		if ( row.preferredContact=='email') {
			            			value = '<span style="font-weight:bold;">' + row.email + '</span>';
			            		} else {
			            			value = row.email + "";
			            		}			            		
			            		return (value);
			            	}
			            } },
			            { title: "<bean:message key="field.label.fax" />" , "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {	
		            		if ( row.preferredContact=='fax') {
		            			value = '<span style="font-weight:bold;">' + row.fax + '</span>';
		            		} else {
		            			value = row.fax + "";
		            		}			            		
		            		return (value);
			            } },
			            { title: "<bean:message key="field.label.mobilePhone" />" , "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {	
		            		if ( row.preferredContact=='mobile_phone') {
		            			value = '<span style="font-weight:bold;">' + row.mobilePhone + '</span>';
		            		} else {
		            			value = row.mobilePhone + "";
		            		}			            		
		            		return (value);
			            } },
			            { title: "<bean:message key="field.label.action" />",  data: function ( row, type, set ) {	
			            	{
			            		return '<span class="tooltip"><i class="editAction fas fa-pencil-alt" data-id="'+ row.contactId + '" /><span class="tooltiptext">Edit</span></span>';}
			            } }
			            ],
			            "initComplete": function(settings, json) {
			            	//console.log(json);
			            	doFunctionBinding();
			            },
			            "drawCallback": function( settings ) {
			            	doFunctionBinding();
			            }
			    } );
        	}
        	        	
        	init();
        			
            
            function init(){
					$.each($('input'), function () {
				        $(this).css("height","20px");
				        $(this).css("max-height", "20px");
				    });
					
					createTable();
            }; 
				
			function doFunctionBinding() {
				$( ".editAction" ).on( "click", function($clickevent) {
					 showEdit($clickevent);
				});
			}
			
			
			
			$("#editPanel" ).dialog({
				title:'Edit Contact',
				autoOpen: false,
				height: 400,
				width: 600,
				modal: true,
				buttons: [
					{
						id: "closeEditPanel",
						click: function() {
							$("#editPanel").dialog( "close" );
						}
					},{
						id: "goEdit",
						click: function($event) {
							updateContact();
						}
					}	      	      
				],
				close: function() {
					$("#editPanel").dialog( "close" );
					//allFields.removeClass( "ui-state-error" );
				}
			});
			
			
			function updateContact() {
				console.debug("Updating contact");
				var $contactId = $("#goEdit").data("contactId");
				console.debug("contactId: " + $contactId);
				
				if ( $contactId == null || $contactId == '') {
					$url = 'contact/add';
				} else {
					$url = 'contact/' + $contactId;
				}
				console.debug($url);
					
				var $outbound = {};
				$outbound['contactId'] = $contactId;
				$outbound['businessPhone'] = $("#editPanel input[name='businessPhone']").val();
				$outbound['fax'] = $("#editPanel input[name='fax']").val();
				$outbound['firstName'] = $("#editPanel input[name='firstName']").val();
				$outbound['lastName'] = $("#editPanel input[name='lastName']").val();
				$outbound['mobilePhone'] = $("#editPanel input[name='mobilePhone']").val();
				$outbound['preferredContact'] = $("#editPanel select[name='preferredContact'] option:selected").val();
				$outbound['email'] = $("#editPanel input[name='email']").val();			        		
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
				        		$("#editPanel").dialog("close");
		    					$("#globalMsg").html("Update Successful").show().fadeOut(10000);
		    					$('#contactTable').DataTable().ajax.reload();
		    				}
						},
						403: function($data) {
							$("#globalMsg").html("Session Timeout. Log in and try again");
						},
						404: function($data) {
							$("#globalMsg").html("Invalid Contact").show().fadeOut(100000);
						},
						500: function($data) {
							$("#globalMsg").html("System Error; Contact Support");
						}
					},
					dataType: 'json'
				});
			}
			
			
			
			
			
				
			function showEdit($clickevent) {
				var $contactId = $clickevent.currentTarget.attributes['data-id'].value;
				console.debug("contactId: " + $contactId);
				$("#goEdit").data("contactId", $contactId);
        		$('#goEdit').button('option', 'label', 'Save');
        		$('#closeEditPanel').button('option', 'label', 'Close');
        		
        		
				var $url = 'contact/' + $contactId;
				var jqxhr = $.ajax({
					type: 'GET',
					url: $url,
					statusCode: {
						200: function($data) {
							//console.log($data);
							var $contact = $data.data.contactList[0];
							$("#editPanel input[name='businessPhone']").val($contact.businessPhone);
							$("#editPanel input[name='fax']").val($contact.fax);
							$("#editPanel input[name='firstName']").val($contact.firstName);
							$("#editPanel input[name='lastName']").val($contact.lastName);
							$("#editPanel input[name='mobilePhone']").val($contact.mobilePhone);
							$("#editPanel input[name='preferredContact']").val($contact.preferredContact);
							$("#editPanel input[name='email']").val($contact.email);			        		
			        		$("#editPanel .err").html("");
			        		$("#editPanel").dialog("open");
						},
						403: function($data) {
							$("#globalMsg").html("Session Timeout. Log in and try again");
						},
						404: function($data) {
							$("#globalMsg").html("Invalid Contact");
						},
						500: function($data) {
							$("#globalMsg").html("System Error; Contact Support");
						}
					},
					dataType: 'json'
				});
			}
			
			if ( $ansiModal != '' ) {
				$(".showNew").click();
			}
			
        });
        </script>        
    </tiles:put>
    
   <tiles:put name="content" type="string">
    	<h1><bean:message key="page.label.contact" /> <bean:message key="menu.label.lookup" /></h1>
    	
    	    <input type="button" class="prettyWideButton showNew" value="New" />
 	<table id="contactTable" style="table-layout: fixed" class="display" cellspacing="0" style="font-size:9pt;max-width:800px;width:800px;">
       	<colgroup>
        	<col style="width:20%;" />
        	<col style="width:20%;" />
        	<col style="width:12%" />
        	<col style="width:12%;" />
        	<col style="width:12%;" />
        	<col style="width:12%;" />
        	<col style="width:12%;" />
   		</colgroup>
        <thead>
            <tr>
                <th><bean:message key="field.label.lastName" /></th>
    			<th><bean:message key="field.label.firstName" /></th>
    			<th><bean:message key="field.label.businessPhone" /></th>
    			<th><bean:message key="field.label.email" /></th>
    			<th><bean:message key="field.label.fax" /></th>
    			<th><bean:message key="field.label.mobilePhone" /></th>
    			<th><bean:message key="field.label.action" /></th>
            </tr>
        </thead>
        <tfoot>
            <tr>
                <th><bean:message key="field.label.lastName" /></th>
    			<th><bean:message key="field.label.firstName" /></th>
    			<th><bean:message key="field.label.businessPhone" /></th>
    			<th><bean:message key="field.label.email" /></th>
    			<th><bean:message key="field.label.fax" /></th>
    			<th><bean:message key="field.label.mobilePhone" /></th>
    			<th><bean:message key="field.label.action" /></th>
            </tr>
        </tfoot>
    </table>
    <input type="button" class="prettyWideButton showNew" value="New" />
    
    <p align="center">
    	<br>
    	<a href="#" title="Scroll to Top" class="ScrollTop"><bean:message key="field.label.scrollToTop" /></a>
    	</br>
    </p>
    
    
    <div id="editPanel">
    	<table>
    		<tr>
    			<td><span class="formHdr"><bean:message key="field.label.firstName" /></span></td>
    			<td><input type="text" name="firstName" /> <i class="fa fa-level-down swap-name" aria-hidden="true"></i></td>
    			<td><span class="err" id="firstNameErr"></span></td>
    		</tr>
    		<tr>
    			<td><span class="formHdr"><bean:message key="field.label.lastName" /></span></td>
    			<td></i><input type="text" name="lastName" /> <i class="fa fa-level-up swap-name" aria-hidden="true"></td>
    			<td><span class="err" id="lastNameErr"></span></td>
    		</tr>
    		<tr>
    			<td><span class="formHdr"><bean:message key="field.label.businessPhone" /></span></td>
    			<td><input type="text" name="businessPhone" /></td>
    			<td><span class="err" id="businessPhoneErr"></span></td>
    		</tr>
    		<tr>
    			<td><span class="formHdr"><bean:message key="field.label.email" /></span></td>
    			<td><input type="text" name="email" /></td>
    			<td><span class="err" id="emailErr"></span></td>
    		</tr>
    		<tr>
    			<td><span class="formHdr"><bean:message key="field.label.fax" /></span></td>
    			<td><input type="text" name="fax" /></td>
    			<td><span class="err" id="faxErr"></span></td>
    		</tr>
    		<tr>
    			<td><span class="formHdr"><bean:message key="field.label.mobilePhone" /></span></td>
    			<td><input type="text" name="mobilePhone" /></td>
    			<td><span class="err" id="mobilePhoneErr"></span></td>
    		</tr>
    		<tr>
    			<td><span class="formHdr">Preferred Contact</span></td>
    			<td><select name="preferredContact"></select></td>
    			<td><span class="err" id="preferredContactErr"></span></td>
    		</tr>    		
    	</table>
    </div>
    </tiles:put>
		
</tiles:insert>

