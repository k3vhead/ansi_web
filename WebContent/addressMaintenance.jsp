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
        Address Maintenance
    </tiles:put>
    
    
    <tiles:put name="headextra" type="string">
        <style type="text/css">
			#confirmDelete {
				display:none;
				background-color:#FFFFFF;
				color:#000000;
				width:300px;
				text-align:center;
				padding:15px;
			}
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
			#delData {
				margin-top:15px;
				margin-bottom:15px;
			}
			#state-menu {
			  max-height: 300px;
			}
			.prettyWideButton {
				height:30px;
				min-height:30px;
			}

        </style>
        
        <script type="text/javascript">        
        
        	$(document).ready(function() {
        	var dataTable = null;
        	
        	function createTable(){
        		var dataTable = $('#addressTable').DataTable( {
        			"processing": true,
        	        "serverSide": true,
        	        "autoWidth": false,
        	        "deferRender": true,
        	        "scrollX": true,
        	        rowId: 'dt_RowId',
        	        dom: 'Bfrtip',
        	        "searching": true,
        	        lengthMenu: [
        	            [ 10, 25, 50, -1 ],
        	            [ '10 rows', '25 rows', '50 rows', 'Show all' ]
        	        ],
        	        buttons: [
        	        	'pageLength','copy', 'csv', 'excel', {extend: 'pdfHtml5', orientation: 'landscape'}, 'print','colvis'
        	        ],
        	        "paging": true,
			        "ajax": {
			        	"url": "addressTable",
			        	"type": "GET"
			        	},
			        columns: [
			            { title: "Id", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {	
			            	if(row.addressId != null){return (row.addressId+"");}
			            } },
			            { title: "Name", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.name != null){return (row.name+"");}
			            } },
			            { title: "Status" , "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {	
			            	if(row.status != null){return (row.status+"");}
			            } },
			            { title: "Address 1", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.address1 != null){return (row.address1+"");}
			            } },
			            { title: "Address 2",  "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.address2 != null){return (row.address2+"");}
			            } },
			            { title: "City", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.city != null){return (row.city+"");}
			            } },
			            { title: "County", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.county != null){return (row.county+"");}
			            } },
			            { title: "Country", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.countryCode != null){return (row.countryCode+"");}
			            } },
			            { title: "State", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) { 	
			            	if(row.state != null){return (row.state+"");}
			            } },
			            { title: "Zip", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {	
			            	if(row.zip != null){return (row.zip+"");} 
			            } },
			            { title: "Action",  data: function ( row, type, set ) {	
			            	return "<ansi:hasPermission permissionRequired='SYSADMIN'><ansi:hasWrite><a href='#' class=\"editAction ui-icon ui-icon-pencil\" data-id='"+row.addressId+"'></a>|<a href='#' data-id='"+row.addressId+"'  class='delAction ui-icon ui-icon-trash'></a></ansi:hasWrite></ansi:hasPermission>" 
			            } }],
			            "initComplete": function(settings, json) {
			            	console.log(json);
			            	
			            	//$('#addressTable').DataTable().columns.adjust().draw();
			            	doFunctionBinding();
			            },
			            "drawCallback": function( settings ) {
			            	doFunctionBinding();
			            }
			    } );
        	}
        	
        	
        	// var resultTable = $('#addressTable').DataTable();
        	
        	$("#addButton").button().on( "click", function() {
        	      $("#addAddressForm").dialog( "open" );
            });
        	
        	init();
			
        	$( "#addAddressForm" ).dialog({
        	      autoOpen: false,
        	      height: 450,
        	      width: 550,
        	      modal: true,
        	      buttons: {
        	        "Create Address": addAddress,
        	        Cancel: function() {
        	        	$( "#addAddressForm" ).dialog( "close" );
        	        }
        	      },
        	      close: function() {
        	    	  $( "#addAddressForm" ).dialog( "close" );
        	        //allFields.removeClass( "ui-state-error" );
        	      }
        	    });
        	
        	function addAddress() {
        		$outbound = {};
        		$outbound["name"]		=	$("#name").val();
        		$outbound["status"]		=	$("#status option:selected").val();
        		$outbound["address1"]	=	$("#address1").val();
        		$outbound["address2"]	=	$("#address2").val();
        		$outbound["city"]		=	$("#city").val();
        		$outbound["county"]		=	$("#county").val();
        		$outbound["countryCode"]	=	$("#country option:selected").val();
        		$outbound["state"]		=	$("#state option:selected").val();
        		$outbound["zip"]		=	$("#zip").val();
        		
        		
				$url = "address/add";
				console.log($outbound);
				var jqxhr = $.ajax({
					type: 'POST',
					url: $url,
					data: JSON.stringify($outbound),
					success: function($data) {
						if ( $data.responseHeader.responseCode == 'SUCCESS') {
							//alert("success");
							console.log($data);
							//createData();
					
							clearAddForm();
							$( "#addAddressForm" ).dialog( "close" );
							if ( 'GLOBAL_MESSAGE' in $data.data.webMessages ) {
								$("#globalMsg").html($data.data.webMessages['GLOBAL_MESSAGE'][0]).fadeIn(10).fadeOut(6000);
							}
							
							$("#addressTable").DataTable().draw();
						} else if ( $data.responseHeader.responseCode == 'EDIT_FAILURE') {
							//alert("success fail");
							$.each($data.data.webMessages, function(key, messageList) {
								var identifier = "#" + key + "Err";
								msgHtml = "<ul>";
								$.each(messageList, function(index, message) {
									msgHtml = msgHtml + "<li>" + message + "</li>";
								});
								msgHtml = msgHtml + "</ul>";
								$(identifier).html(msgHtml);
							});		
							if ( 'GLOBAL_MESSAGE' in $data.data.webMessages ) {
								$("#addFormMsg").html($data.data.webMessages['GLOBAL_MESSAGE'][0]);
							}
							$( "#addAddressForm" ).dialog( "close" );
						} else {
							//alert("success other");
						}
					},
					error: function($data) {
						
						alert("fail");
						console.log($data);
							
					},
					statusCode: {
						403: function($data) {
							$("#globalMsg").html($data.responseJSON.responseHeader.responseMessage);
						} 
					},
					dataType: 'json'
				});
				$( "#addAddressForm" ).dialog( "close" );
        	}
        	
        	
        	
            function clearAddForm() {
	        	$("#name").val("");
	        	$("#status option[value='']").attr('selected', true);
	        	$("#address1").val("");
	        	$("#address2").val("");
	        	$("#city").val("");
	        	$("#county").val("");
	        	$("#country option[value='']").attr('selected', true);
	        	$("#state option[value='']").attr('selected', true);
	        	$("#zip").val("");
            }
            
            
            
            function init(){

					$.each($('input'), function () {
				        $(this).css("height","20px");
				        $(this).css("max-height", "20px");
				    });
					$("#address select[name='state']").selectmenu({ width : '150px', maxHeight: '400 !important', style: 'dropdown'});
					$("select[name='city']").addClass("ui-corner-all");
					$("select[name='ip']").addClass("ui-corner-all");
					$("#address select[name='country']").selectmenu({ width : '80px', maxHeight: '400 !important', style: 'dropdown'});
				
					var jqxhr1 = $.ajax({
	    				type: 'GET',
	    				url: 'options',
	    				data: 'COUNTRY',
	    				success: function($data) {
	    					setCountry($data.data.country);
	    					setStates($data.data.country);

	    				},
	    				statusCode: {
	    					403: function($data) {
	    						$("#useridMsg").html($data.responseJSON.responseHeader.responseMessage);
	    					} 
	    				},
	    				dataType: 'json'
	    			});
					
					createTable();
					
				//	setInterval( function () {
				//		doFunctionBinding();
				//	}, 3000 );
					
					
					console.debug("inits");		
            }
            
				function setCountry($optionList,$selectedValue) {
					var selectorName = "#address select[name='country']";
					selectorName = "select[name='country']";
					
					var $select = $(selectorName);
					$('option', $select).remove();

					$select.append(new Option("",""));
					$.each($optionList, function(index, val) {
						console.log(val);
					    $select.append(new Option(val.abbrev));
					});
					
					if ( $selectedValue != null ) {
						$select.val($selectedValue);
					}
					$select.selectmenu();
				} 
					
				function setStates($optionList,$selectedValue) {
					var selectorName = "#address select[name='state']";
					selectorName = "select[name='state']";
					
					var $select = $(selectorName);
					$('option', $select).remove();

					$select.append(new Option("",""));
					$.each($optionList, function(index, val) {
						var group = $('<optgroup label="' + val.abbrev + '" />');
							$.each(val.stateList, function(){
								$(group).append("<option value='"+this.abbreviation+"'>"+this.display+"</option>");
							});
							group.appendTo($select);
						});
					
					
					if ( $selectedValue != null ) {
						$select.val($selectedValue);
					}
					$select.selectmenu();
				}
				
				function doFunctionBinding() {
					$( ".editAction" ).bind( "click", function($clickevent) {
						 doEdit($clickevent);
					});
					$('.delAction').on('click', function($clickevent) {
						doDelete($clickevent);
					});
					console.log("Functions Bound");
				}
				
				function doEdit() {
					var $rowid = event.currentTarget.attributes['data-id'].value;
					
					console.log("Edit Button Clicked: " + $rowid);
					
				}
				
				function doDelete($clickevent) {
					$clickevent.preventDefault();
					
					var $rowid = $clickevent.currentTarget.attributes['data-id'].value;
					//$("#addressTable").row( $clickevent ).remove();
					$("#addressTable").DataTable().row($rowid).remove();
			        //   dataTable.draw();
		            	$outbound = JSON.stringify({});
		            	$url = 'address/' + $rowid;
		            	//$outbound = JSON.stringify({'tableName':$tableName, 'fieldName':$fieldName,'value':$value});
		            	var jqxhr = $.ajax({
		            	    type: 'delete',
		            	    url: $url,
		            	    data: $outbound,
		            	    success: function($data) {
		            	    	$("#globalMsg").html($data.responseHeader.responseMessage).fadeIn(10).fadeOut(6000);
								if ( $data.responseHeader.responseCode == 'SUCCESS') {
									$("#addressTable").DataTable().draw();
									//dataTable.draw();
								}
		            	     },
		            	     statusCode: {
		            	    	403: function($data) {
		            	    		$("#globalMsg").html($data.responseJSON.responseHeader.responseMessage);
		            	    	},
		            	    	500: function($data) {
		            	    		 $( "#deleteErrorDialog" ).dialog({
		            	    		      modal: true,
		            	    		      buttons: {
		            	    		        Ok: function() {
		            	    		          $( this ).dialog( "close" );
		            	    		        }
		            	    		      }
		            	    		    });
		            	    	} 
		            	     },
		            	     dataType: 'json'
		            	});
					
					
				}
				
        
            
        });
        </script>        
    </tiles:put>
    

    
    <tiles:put name="content" type="string">
    	<h1>Address Maintenance</h1>
    	
 	<table id="addressTable" class="display" cellspacing="0" width="100%">
        <thead>
            <tr>
                <th style="max-width:40px;">Id</th>
    			<th>Name</th>
    			<th>Status</th>
    			<th style="max-width:200px;width:200px !important;">Address 1</th>
    			<th style="max-width:200px;width:200px !important;">Address 2</th>
    			<th>City</th>
    			<th>County</th>
    			<th>Country</th>
    			<th>State</th>
    			<th>Zip</th>
    			<th>Action</th>
            </tr>
        </thead>
        <tfoot>
            <tr>
                <th>Id</th>
    			<th>Name</th>
    			<th>Status</th>
    			<th style="max-width:200px;width:200px !important;">Address 1</th>
    			<th style="max-width:200px;width:200px !important;">Address 2</th>
    			<th>City</th>
    			<th>County</th>
    			<th>Country</th>
    			<th>State</th>
    			<th>Zip</th>
    			<th>Action</th>
    			
            </tr>
        </tfoot>
    </table>
    <ansi:hasPermission permissionRequired="SYSADMIN">
			<ansi:hasWrite>
    			<div class="addButtonDiv">
    				<input type="button" id="addButton" class="prettyWideButton" value="New" />
    			</div>
			</ansi:hasWrite>
		</ansi:hasPermission>
    </tiles:put>

		<div id="deleteErrorDialog" title="Delete Failed!" class="ui-widget" style="display:none;">
		  <p>Address failed to delete. It may still be assigned to existing quotes.</p>
		</div>

		<div id="addAddressForm" title="Add New Address" class="ui-widget">
		  <p class="validateTips">All form fields are required.</p>
		 
		  <form id="addForm">
		   <table>
			<tr>
				<td><b>Name</b></td>
				<td colspan="3"><input type="text" name="name" id="name" style="width:315px" /></td>
			</tr>
			<tr>
				<td><b>Status</b></td>
				<td colspan="3">
					<select name="status" id="status" style="width:85px !important;max-width:85px !important;">
						<option value="0">Bad</option>
						<option value="1">Good</option>
					</select>
				</td>
			</tr>
			<tr>
				<td style="width:85px;">Address:</td>
				<td colspan="3"><input type="text" name="address1" id="address1" style="width:315px" /></td>
			</tr>
			<tr>
				<td>Address 2:</td>
				<td colspan="3"><input type="text" name="address2" id="address2" style="width:315px" /></td>
			</tr>
			<tr>
			<td colspan="4" style="padding:0; margin:0;">
				<table style="width:415px;border-collapse: collapse;padding:0; margin:0;">
				<tr>
					<td>City/State/Zip:</td>
					<td><input type="text" name="city" id="city" style="width:90px;" /></td>
					<td style="width:80px;"><select name="state" id="state" ></select></td>
					<td><input type="text" name="zip" id="zip" style="width:47px !important" /></td>
				</tr>
				</table>
			</td>
			</tr>
			<tr>
				<td>County:</td>
				<td><input type="text" name="county" id="county" style="width:90%" /></td>
				<td colspan="2">
					<table style="width:180px">
						<tr>
							<td>Country:</td>
							<td align="right"><select name="country" id="country" style="width:80px;max-width:80px;"></select></td>
						</tr>
					</table>
				
				
				</td>
				
			</tr>
			<tr>
				<td>Job Contact:</td>
				<td style="width:140px;"><input type="text" name="jobContactName" style="width:125px" placeholder="<name>"/></td>
				<td colspan="2"><input type="text" name="jobContactInfo" style="width:170px" placeholder="<phone,mobile,email>"/></td>
			</tr>
			<tr>
				<td>Site Contact:</td>
				<td style="width:140px;"><input type="text" name="siteContactName" style="width:125px" placeholder="<name>"/></td>
				<td colspan="2"><input type="text" name="siteContactInfo" style="width:170px" placeholder="<phone,mobile,email>"/></td>
			</tr>
		</table>
		  </form>
		</div>
</tiles:insert>

