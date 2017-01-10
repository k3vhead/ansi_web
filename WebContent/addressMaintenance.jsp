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

        </style>
        
        <script type="text/javascript">        
        $(function() {        
        	// var resultTable = $('#addressTable').DataTable();
        	
        	$("#addButton").button().on( "click", function() {
        	      $("#addAddressForm").dialog( "open" );
            });
        	init();
        	//$('#addressTable tfoot th').each( function () {
		    //    var title = $(this).text();
		    //    $(this).html( '<input type="text" style="width:100%" placeholder="<'+title+'>" />' );
		    //} );
        	var jqxhr1 = $.ajax({
				type: 'GET',
				url: 'address/list',
				data: {},
				dataType: 'json',
				success: function($data) {
				var data = new Array();
					 
					 $.each($data, function(index, val) {
						 $.each(val.codeList, function(index, val) {
							 
							 var addressId = (val.addressId).toString();;
							 var name = val.name;
							 var status = val.status;
							 var address1 = val.address1;
							 var address2 = val.address2;
							 var city = val.city;
							 var county = val.county;
							 var country = val.country;
							 var state = val.state;
							 var zip = val.zip;
							 
							if(addressId == null) 	{	addressId = "N/A";	}	
							if(name == null) 		{	name = "N/A";		}	
							if(status == null) 		{	status = "N/A";		}	
							if(address1 == null) 	{	address1 = "N/A";	}	
							if(address2 == null) 	{	address2 = "N/A";	}	
							if(city == null) 		{	city = "N/A";		}	
							if(county == null) 		{	county = "N/A";		}	
							if(country == null) 	{	country = "N/A";	}	
							if(state == null) 		{	state = "N/A";		}	
							if(zip == null) 		{	zip = "N/A";		}	
						
							 
						 		data.push([addressId,name,status,address1,address2,city,county,country,state,zip]);
					 			//console.log(row);
					 			if(addressId != 2){
					 			//	resultTable.rows.add(row).draw(false);
					 			}
						 });
					 	//resultTable.rows.add(row).draw();
					 });
					 console.log(data);
					 //resultTable.draw();
	                 //   resultTable.rows.add($data.codeList).draw();
	                 //   dataSet = $data.codeList;
					var dataTable = $('#addressTable').DataTable( {
					        data: data,
					        "processing": true,
					        columns: [
					            { title: "Id", width: "30px" },
					            { title: "Name" },
					            { title: "Status" },
					            { title: "Address 1" },
					            { title: "Address 2" },
					            { title: "City" },
					            { title: "County" },
					            { title: "Country" },
					            { title: "State" },
					            { title: "Zip" },
					            {
					                mRender: function (data, type, row) {
					                    //return '<a class="table-edit" data-id="' + row[0] + '">EDIT</a>'
					                     //return "<button id='edit"+ row[0] + "' class='ui-button ui-widget ui-corner-all ui-button-icon-only' title='Edit'>"
					                     return "<ansi:hasPermission permissionRequired='SYSADMIN'><ansi:hasWrite><a href='#' id='edit"+ row[0] + "'class='ui-icon ui-icon-pencil'></a>|<a href='#' id='delete"+ row[0] + "'class='ui-icon ui-icon-trash'></a></ansi:hasWrite></ansi:hasPermission>"
					                }
					            
					            }]
					    } );
					dataTable.rows().every( function () {
					        var that = this;
					        console.log(this);
					 		$('#edit'+that.data()[0]).click(function(event){ 
					 			//console.log(event.target.id);
					            console.log("Button "+event.target.id+" clicked!");
					        });
					 		$('#delete'+that.data()[0]).click(function(event){ 
					 			//console.log(event.target.id);
					            console.log("Button "+event.target.id+" clicked!");

					           var $addressId = event.target.id.replace('delete','');

				            	$outbound = JSON.stringify({});
				            	$url = 'address/delete/' + $addressId;
				            	//$outbound = JSON.stringify({'tableName':$tableName, 'fieldName':$fieldName,'value':$value});
				            	var jqxhr = $.ajax({
				            	    type: 'delete',
				            	    url: $url,
				            	    data: $outbound,
				            	    success: function($data) {
				            	    	$("#globalMsg").html($data.responseHeader.responseMessage).fadeIn(10).fadeOut(6000);
										if ( $data.responseHeader.responseCode == 'SUCCESS') {
											dataTable.ajax.reload();
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
					        });
					        
					    } );
	              
					
			},
			statusCode: {
				403: function($data) {
					$("#useridMsg").html($data.responseJSON.responseHeader.responseMessage);
				} 
			},
			dataType: 'json'
		});
        	
        	
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
				$.each( $('#addForm :input'), function(index, value) {
					if ( value.name ) {
						$fieldName = value.name;
						$id = "#addForm input[name='" + $fieldName + "']";
						$val = $($id).val();
						$outbound[$fieldName] = $val;
					}
				});
			
				$outbound['seq'] = $("#addForm select[name='seq'] option:selected").val();
				$outbound['status'] = $("#addForm select[name='status'] option:selected").val();

				if ( $('#addForm').data('rownum') == null ) {
					$url = "address/add";
				} else {
					$rownum = $('#addForm').data('rownum')
					var $tableData = [];
	                $("#addressTable").find('tr').each(function (rowIndex, r) {
	                    var cols = [];
	                    $(this).find('th,td').each(function (colIndex, c) {
	                        cols.push(c.textContent);
	                    });
	                    $tableData.push(cols);
	                });

	            	var $tableName = $tableData[$rownum][0];
	            	var $fieldName = $tableData[$rownum][1];
	            	var $value = $tableData[$rownum][2];
	            	$url = "address/" + $addressId ;
				}
				$url = "address/add";
				
				var jqxhr = $.ajax({
					type: 'POST',
					url: $url,
					data: JSON.stringify($outbound),
					success: function($data) {
						if ( $data.responseHeader.responseCode == 'SUCCESS') {
							
								
							dataTable.ajax.reload();
							
							clearAddForm();
							$( "#addAddressForm" ).dialog( "close" );
							if ( 'GLOBAL_MESSAGE' in $data.data.webMessages ) {
								$("#globalMsg").html($data.data.webMessages['GLOBAL_MESSAGE'][0]).fadeIn(10).fadeOut(6000);
							}
						} else if ( $data.responseHeader.responseCode == 'EDIT_FAILURE') {
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
							$( "#addAddressForm" ).dialog( "close" );
						}
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
				$.each( $('#addForm').find("input"), function(index, $inputField) {
					$fieldName = $($inputField).attr('name');
					if ( $($inputField).attr("type") == "text" ) {
						$($inputField).val("");
						//markValid($inputField);
					}
				});
				
				$('#addForm').data('rownum',null);
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
								$('<option />').html(this.display).appendTo(group);
							});
							group.appendTo($select);
						});
					
					
					if ( $selectedValue != null ) {
						$select.val($selectedValue);
					}
					$select.selectmenu();
				}
            
        
            
        });
        </script>        
    </tiles:put>
    

    
    <tiles:put name="content" type="string">
    	<h1>Address Maintenance</h1>
    	
 	<table id="addressTable" class="display" cellspacing="0" width="100%">
        <thead>
            <tr>
                <th>Id</th>
    			<th>Name</th>
    			<th>Status</th>
    			<th>Address1</th>
    			<th>Address2</th>
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
    			<th>Address1</th>
    			<th>Address2</th>
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
				<td colspan="3"><input type="text" name="name" style="width:315px" /></td>
			</tr>
			<tr>
				<td><b>Status</b></td>
				<td colspan="3">
					<select name="status" id="state" style="width:85px !important;max-width:85px !important;">
						<option value="0">Bad</option>
						<option value="1">Good</option>
					</select>
				</td>
			</tr>
			<tr>
				<td style="width:85px;">Address:</td>
				<td colspan="3"><input type="text" name="address1" style="width:315px" /></td>
			</tr>
			<tr>
				<td>Address 2:</td>
				<td colspan="3"><input type="text" name="address2" style="width:315px" /></td>
			</tr>
			<tr>
			<td colspan="4" style="padding:0; margin:0;">
				<table style="width:415px;border-collapse: collapse;padding:0; margin:0;">
				<tr>
					<td>City/State/Zip:</td>
					<td><input type="text" name="city" style="width:90px;" /></td>
					<td><select name="state" id="state" style="width:85px !important;max-width:85px !important;"></select></td>
					<td><input type="text" name="zip" style="width:47px !important" /></td>
				</tr>
				</table>
			</td>
			</tr>
			<tr>
				<td>County:</td>
				<td><input type="text" name="county" style="width:90%" /></td>
				<td colspan="2">
					<table style="width:180px">
						<tr>
							<td>Country:</td>
							<td align="right"><select name="country" id="country"></select></td>
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

