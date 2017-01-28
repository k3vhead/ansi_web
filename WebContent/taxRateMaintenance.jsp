<!-- 
	
File : taxRateMaintenance.jsp
Created : 2016-12-28 11:00 AM
Borrowed heavily from : taxRateMaintenance.jsp
Contributer : Kevin J Wagner

Note : search this source for 'modthis' to quickly get to the areas that need to 
       be customized for each record type.

change log
--------------------------------------------------------------------------------------------
2016-12-17 11:00 AM - kjw : Created starter file .jsp file
2016-12-28  2:00 AM - kjw : all features now work.. view/add/edit/remove..
2016-12-29  9:45 PM - kjw : cleaned up debug code.. 
2016-12-29  9:45 PM - kjw : added // modthis comment to quickly navigate to record specific sections of the page.
2017-01-12  3:20 PM - kjw : Added 'Action' Column header and added style rules to center the column.

-->

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
		<!-- modthis -->
        Tax Rate Maintenance
    </tiles:put>
    <tiles:put name="headextra" type="string">
    	<link rel="stylesheet" href="css/datepicker.css" type="text/css" />
        <style type="text/css">
			#confirmDelete {
				display:none;
				background-color:#FFFFFF;
				color:#000000;
				width:300px;
				text-align:Left;
				padding:15px;
			}
			#displayTable {
				width:90%;
			}
			#addFormDiv {
				display:none;
				background-color:#FFFFFF;
				color:#000000;
				width:300px;
				padding:15px;
			}

			/* <!-- modthis --> */
			#col_01{							/* id column 		*/
				display : none; 
				}
			#col_02{	text-align: left;	}  /* location  		*/
			#col_03{	text-align: right;	}  /* rate 				*/
			#col_04{ 	text-align: right;	}  /* amount 			*/
			#col_05{	text-align: right;	}  /* effectiveDate 	*/
			#col_06{	text-align: center;	}  /* action 		 	*/
			#col_07{	text-align: center;	}  /* not used 		 	*/
			#col_08{	text-align: center;	}  /* not used 		 	*/
			#col_09{	text-align: center;	}  /* not used 		 	*/

			/* <!-- modthis --> */
			#col_01_hdr{							/* id column 		*/
				display : none; 
				}
			#col_02_hdr{	text-align: left;	} 	/* location  		*/
			#col_03_hdr{	text-align: right;	} 	/* rate 			*/
			#col_04_hdr{	text-align: right;	} 	/* amount 			*/
			#col_05_hdr{	text-align: right;	} 	/* effective date 	*/
			#col_06_hdr{ 	text-align: center;	} 	/* action 		*/
			#col_07_hdr{	text-align: right;	} 	/* not used 		*/
			#col_08_hdr{	text-align: center;	} 	/* not used 		*/
			#col_09_hdr{	text-align: right;	} 	/* not used 		*/
        </style>
        
        <script type="text/javascript">

		// Display a list of all of the rows in the db ( Select All )
        $(function() {        
			var jqxhr = $.ajax({
				url: 'taxRate/list', 	// modthis
				type: 'GET',
				data: {},
				success: function($data){ // if the ajax call succeeds... do all of this stuff.. 
					// display the rows..
					// calls the addRow Function for each data item (row) from url: 'taxRate/list'; 
					//    note : the name after '$data.data.'  'taxRateList'. in this case, is defined
					//           within the servlet that is doing the server side processing.
					// modthis
					$.each($data.data.taxRateList,	function(index, value) {	
						// value.<data_item>Id> = the value used to retreive the record from the server
						// value = the data item/row
						// modthis
						addRow(value.taxRateId, value);
					});
					doFunctionBinding();
				},
				error : {
					function($data) {}
				},
				statusCode: {
					403: function($data) { $("#useridMsg").html($data.responseJSON.responseHeader.responseMessage);} 
				},
				dataType: 'json'
			});

			$('.dateField').datepicker({
                prevText:'&lt;&lt;',
                nextText: '&gt;&gt;',
                showButtonPanel:true
            });
						
			function formatDate(dateValue)
			{
				var _d = new Date(dateValue);
				var _fs = ('0' + (_d.getMonth()+1)).slice(-2) + '/' 
						+ ('0' + _d.getDate()).slice(-2) + '/' 
						+ _d.getFullYear();
				return _fs;
			}

			function makeRow($data_item, $data_item_id){
				// creates the set of <td> elements for each row.. 
				// modthis - set the field names to match field names for datatypes
				var _td = '';
				_td = _td + '	<td id="col_01">' + $data_item_id  + 		'</td>';
				_td = _td + '	<td id="col_02">' + $data_item.location + 		'</td>';
				_td = _td + '	<td id="col_03">' + (($data_item.rate)*100).toFixed(2) + '%</td>';
				_td = _td + '	<td id="col_04">' + $data_item.amount + 			'</td>'; 
				_td = _td + '	<td id="col_05">' + $data_item.effectiveDate + 	'</td>';
       	    	<ansi:hasPermission permissionRequired="SYSADMIN">
					<ansi:hasWrite>
						_td = _td + '<td id="col_06">';
						_td = _td + '<a href="#" class="updAction" data-item-id="' + $data_item_id +'"><span class="green fa fa-pencil" ari-hidden="true"></span></a> | ';
						_td = _td + '<a href="#" class="delAction" data-item-id="' + $data_item_id +'"><span class="red fa fa-trash" aria-hidden="true"></span></a>';
						_td = _td + '</td>';
					</ansi:hasWrite>
       			</ansi:hasPermission>
				return _td;
			}
		
			function addRow($data_item_id, $data_item) {
				// creates a complete row to be added to the display table
				var row = '';
				row = row + '<tr class="dataRow" id="data-item-id-' + $data_item_id +'">';
				row = row + makeRow($data_item, $data_item_id);
       			row = row + '</tr>';
       			$('#displayTable').append(row);
			}
						
			function doUpdate($clickevent) {
				// This occurs when the little pencil icon is clicked.
				// it determines which row is to be edited, retreives
				// the values for the row and presents them to the user
				// for editing via the popup #addFormDiv that appears 
				// as a popup
				$clickevent.preventDefault();
				clearAddForm();

				// figure out which row was clicked.. 
				var $data_item_id = $clickevent.currentTarget.attributes['data-item-id'].value;

				// Add a title to the Add Form
				// modthis
				$("#addFormTitle").html("Update a Tax Code");
				// attach a data item to the add form containing the row number being updated
				$('#addForm').data('data_item_id',$data_item_id);
				
				// Use the data_item_id to build a locator for the row to be found.. 
            	var $rowFinder = '#data-item-id-' + $data_item_id
				var $row = $($rowFinder)

				// get all cells in the row.. 
            	var tdList = $row.children("td");

				// Store row value in local vars.. 
				// modthis
				var $taxRateId = $row.children("td")[0].textContent;
            	var $location = $row.children("td")[1].textContent;
            	var $rate = $row.children("td")[2].textContent;
            	var $amount = $row.children("td")[3].textContent;
            	var $effectiveDate = $row.children("td")[4].textContent;
				
				// populate addform with existing values.. 
				// modthis [ make sure names match database field names!]
				$('#addForm').data('data-item-id',$data_item_id);
            	$("#addForm input[name='location']").val($location);
            	$("#addForm input[name='rate']").val($rate);
            	$("#addForm input[name='amount']").val($amount);
				$("#addForm input[name='effectiveDate']").val($effectiveDate);
            	
				$.each( $('#addForm :input'), function(index, value) {
					markValid(value);
				});

				// show the update record popup
             	$('#addFormDiv').bPopup({
					modalClose: false,
					opacity: 0.6,
					positionStyle: 'fixed' //'fixed' or 'absolute'
				});				
			}
						
			function doDelete($clickevent) {
				// this happens when the little trash can on a 
				// row is clicked by the user.
				$clickevent.preventDefault();
				var data_item_id = $clickevent.currentTarget.attributes['data-item-id'].value;

				// add the id of the item to be deleted to the popup form as a data() item
				$('#confirmDelete').data('data-item-id',data_item_id);

				// show the popup delete confirmation form
             	$('#confirmDelete').bPopup({
					modalClose: false,
					opacity: 0.6,
					positionStyle: 'fixed' //'fixed' or 'absolute'
				});
			}

			function doFunctionBinding() {
				$('.updAction').bind("click", function($clickevent) {
					doUpdate($clickevent);
				});
				$('.delAction').bind("click", function($clickevent) {
					doDelete($clickevent);
				});
				$('.dataRow').bind("mouseover", function() {
					$(this).css('background-color','#CCCCCC');
				});
				$('.dataRow').bind("mouseout", function() {
					$(this).css('background-color','transparent');
				});
			}

			$("#goUpdate").click( function($clickevent) {
				// this occurs when the user clicks the 'save' 
				// button on the 'addFormDiv', after they have
				// entered the values for the new item

				// This function actually Adds the new taxRate to the database
				$clickevent.preventDefault();
				$outbound = {};

				// Copy the addFormDiv input fieldnames and values into the $outbound array.
				// note : for this to work correctly, the names assigned to the input controls
				//        in the addFormDiv must match the fields defined in the servlet!
				$.each( $('#addForm :input'), function(index, value) {
					if ( value.name ) {
						$fieldName = value.name;
						$id = "#addForm input[name='" + $fieldName + "']";
						$val = $($id).val();
						$outbound[$fieldName] = $val;
					}
				// the '$outbound' array will be passed to the server to be used to update  
				// the values for this record in the database.
					
				});

				// Check to see if this is a new or existing record. 
				// If rownum is empty.. assumed to be a new record.
				if ( $('#addForm').data('data-item-id') == null ) {
					// if this is a new record...
					//    then use this url to connect to the server to 
					//    add the record.  Note : this url is defined 
					//    by the server and is specific to each set of data items
					// modthis
					$url = "taxRate/add";
				} else {
					// if this is an existing record.. 					
					// 	( I don't think this is needed anymore -> )  $rownum = $('#addForm').data('rownum');

					// get the identifier for the record that is to be updated.
					var $data_item_id = $('#addForm').data('data-item-id');

					// 	( I don't think this is needed anymore -> )  var $tableData = [];

					// use this url to update the record
					// modthis
					$url = "taxRate/" + $data_item_id;
				}
				
				// do the ajax call to add/update the fields.. 
				var jqxhr = $.ajax({
					type: 'POST',
					url: $url,
					data: JSON.stringify($outbound),
					success: function($data) {
						if ( $data.responseHeader.responseCode == 'SUCCESS') {
							// if the row was successfully added to the database
							// modthis
							if ( $url == "taxRate/add" ) {
								// 	( I don't think this is needed anymore -> )   var count = $('#displayTable tr').length - 1;
								// ...then add it to the table being displayed on-screen as well.
								addRow($data.data.taxRate.taxRateId, $data.data.taxRate);
							} else {
								// if the row was successfully updated in the database

								// get the id of the record that was updated.
								$data_item_id = $('#addForm').data('data-item-id');

								// find the row on-scree that contains the old values for this record.
            					var $rowFinder = '#data-item-id-' + $data_item_id;					

								// create a new row containing the updated values for the record.			
				            	var $rowTd = makeRow($outbound, $data_item_id);

								// replace the row with the old values with the row with the new values.
				            	$($rowFinder).html($rowTd);
							}
							// bind the 'buttons' on the new row.. 
							doFunctionBinding();

							clearAddForm();

							// close the add/update popup form
							$('#addFormDiv').bPopup().close();

							// display a message that the update was successful
							$("#globalMsg").html($data.data.webMessages['GLOBAL_MESSAGE'][0]).fadeIn(10).fadeOut(6000);

						} else if ( $data.responseHeader.responseCode == 'EDIT_FAILURE') {
							// if the add/update was NOT successful

							// display the list of errors / messages sent back from the server.
							$.each($data.data.webMessages, function(key, messageList) {
								var identifier = "#" + key + "Err";
								msgHtml = "<ul>";
								$.each(messageList, function(index, message) {
									msgHtml = msgHtml + "<li>" + message + "</li>";
								});
								msgHtml = msgHtml + "</ul>";
								$(identifier).html(msgHtml);
							});		
							$("#addFormMsg").html($data.data.webMessages['GLOBAL_MESSAGE'][0]);
						} else {							
						}
					},
					statusCode: {
						403: function($data) {
							$("#globalMsg").html($data.responseJSON.responseHeader.responseMessage);
						} 
					},
					dataType: 'json'
				});
			});
			
			
			$("#addButton").click( function($clickevent) {
				// this happens when the 'new' (add) button is clicked.
				$clickevent.preventDefault();

				// Add a title to the addFormDiv form [the add/update form]
				// modthis
				$("#addFormTitle").html("Add a New Tax Code");

				// show the add/update form
             	$('#addFormDiv').bPopup({
					modalClose: false,
					opacity: 0.6,
					positionStyle: 'fixed' //'fixed' or 'absolute'
				});	
			});
			
			$("#cancelUpdate").click( function($clickevent) {
				// this happens when the user clicks the cancel button 
				// on the add/update form.
				$clickevent.preventDefault();
				clearAddForm();

				//close the form.
				$('#addFormDiv').bPopup().close();
			});

			/* // nn 
			$("#goUpdate").click( function($clickevent) {
				$clickevent.preventDefault();
				clearAddForm();
				$('#addFormDiv').bPopup().close();
			});
			*/

            $("#cancelDelete").click( function($event) {
            	$event.preventDefault();
            	$('#confirmDelete').bPopup().close();
            });         

            $("#doDelete").click(function($event) {
            	$event.preventDefault();

				/*  Don't think this block is necessary anymore.. 
					// Extract all values from the html table 
					//   into an array named $tableData
					var $tableData = [];
					$("#displayTable").find('tr').each(function (rowIndex, r) {
						var cols = [];
						$(this).find('th,td').each(function (colIndex, c) {
							cols.push(c.textContent);
						});
						$tableData.push(cols);
					});
					//Don't think this block is necessary anymore.. 
				*/

				// get the row number to be deleted from the confirm delete popup
				// ( rownum is a data item added to the popup when it was created )
				var $data_item_id = $('#confirmDelete').data('data-item-id');

				// nn use $rownum to extract the column values into variables
            	// nn var $taxRateId = $tableData[$rownum][0];

				// create the url to call to delete this row
				// modthis
				var $deleteUrl = 'taxRate/' + $data_item_id;

            	// create a JSON array of pairs containing the column values.
				// modthis
				$outbound = JSON.stringify({'taxRateId':$data_item_id});

				// make the ajax call to the java servlet to do the delete.
            	var jqxhr = $.ajax({
            	    type: 'delete',
            	    url: $deleteUrl,
            	    data: $outbound,
            	    success: function($data) {
            	    	$("#globalMsg").html($data.responseHeader.responseMessage).fadeIn(10).fadeOut(6000);
						if ( $data.responseHeader.responseCode == 'SUCCESS') {
							$rowfinder = "#data-item-id-" + $data_item_id
							$("#displayTable").find($rowfinder).remove();
							$('#confirmDelete').bPopup().close();
						}
            	     },
            	     statusCode: {
            	    	403: function($data) {
            	    		$("#globalMsg").html($data.responseJSON.responseHeader.responseMessage);
            	    	} 
            	     },
            	     dataType: 'json'
            	});
            });

			// attach an event handler to the focus event 
			//    for each input field on the addForm popup 
            $('#addForm').find("input").on('focus',function(e) {
				// look for a data item named 'required'
            	$required = $(this).data('required');

				// if the 'required' data item was found..
            	if ( $required == true ) {
					// pass the required input field to the markValid function
            		markValid(this);
            	}
            });
            
            $('#addForm').find("input").on('input',function(e) {
            	$required = $(this).data('required');
            	if ( $required == true ) {
            		markValid(this);
            	}
            });
            
            function clearAddForm() {
				$.each( $('#addForm').find("input"), function(index, $inputField) {
					$fieldName = $($inputField).attr('name');
					if ( $($inputField).attr("type") == "text" ) {
						$($inputField).val("");
						markValid($inputField);
					}
				});
            }
            
            function markValid($inputField) {
				// get the name of the field.
            	$fieldName = $($inputField).attr('name');
				// convert the field name into a tag to find..
            	$fieldGetter = "input[name='" + $fieldName + "']";
				// get the value of the input field
            	$fieldValue = $($fieldGetter).val();

				// get the value in the attached data item named 'valid'
            	$valid = '#' + $($inputField).data('valid');

	            var re = /.+/;	            	 
            	if ( re.test($fieldValue) ) {
					// if the field is valid ( I think as long as it doens't contain // )
					// then flag it as valid.
            		$($valid).removeClass("fa-ban");
            		$($valid).removeClass("inputIsInvalid");
            		$($valid).addClass("fa-check-square-o");
            		$($valid).addClass("inputIsValid");
            	} else {
					// else flag it as invalid
            		$($valid).removeClass("fa-check-square-o");
            		$($valid).removeClass("inputIsValid");
            		$($valid).addClass("fa-ban");
            		$($valid).addClass("inputIsInvalid");
            	}
            }
        });
        </script>        
    </tiles:put>
        
    <tiles:put name="content" type="string">
    	<h1>Tax Rate Maintenance</h1>

    	<table id="displayTable">
    		<tr>
				<!-- // modthis -->
				<th id="col_01_hdr">taxRate Id</th>
				<th id="col_02_hdr">Location</th>
				<th id="col_03_hdr">Rate</th>
				<th id="col_04_hdr">Amount</th>
				<th id="col_05_hdr">Effective Date</th>
       	    	<ansi:hasPermission permissionRequired="SYSADMIN">
					<ansi:hasWrite>
						<th id="col_06_hdr">Action</th>
					</ansi:hasWrite>
       	    	</ansi:hasPermission>
    		</tr>
    	</table>

		<ansi:hasPermission permissionRequired="SYSADMIN">
			<ansi:hasWrite>
				<div class="addButtonDiv">
					<input type="button" id="addButton" class="prettyWideButton" value="New" />
				</div>
			</ansi:hasWrite>
		</ansi:hasPermission>
    	
    	<ansi:hasPermission permissionRequired="SYSADMIN">
    		<ansi:hasWrite>
		    	<div id="confirmDelete">
					<!-- // modthis -->
		    		Are You Sure You Want to Delete this Tax Rate?<br />
		    		<input type="button" id="cancelDelete" value="No" />
		    		<input type="button" id="doDelete" value="Yes" />
		    	</div>		    	
		    	<div id="addFormDiv">
		    		<h2 id="addFormTitle"></h2>					
		    		<form action="#" method="post" id="addForm">
		    			<table>
							<!-- MAKE SURE THE 'name' FIELD MATCH THE NAMES USED IN THE DATABASE!! -->
		    				<tr>
		    					<td><span class="required">*</span><span class="formLabel">Location:</span></td>
		    					<td>
		    						<input type="text" name="location" data-required="true" data-valid="validLocation" />
		    						<i id="validTable" class="fa" aria-hidden="true"></i>
		    					</td>
		    					<td><span class="err" id="locationErr"></span></td>
		    				</tr>
							<tr>
		    					<td><span class="required">*</span><span class="formLabel">Tax Rate:</span></td>
		    					<td>
		    						<input type="text" name="rate" data-required="true" data-valid="validRate" />
		    						<i id="validField" class="fa" aria-hidden="true"></i>
		    					</td>
		    					<td><span class="err" id="rateErr"></span></td>
		    				</tr>
		    				<tr>
		    					<td><span class="required">*</span><span class="formLabel">Tax Amount:</span></td>
		    					<td>
		    						<input type="text" name="amount" data-required="true" data-valid="validtaxAmount" />
		    						<i id="validField" class="fa" aria-hidden="true"></i>
		    					</td>
		    					<td><span class="err" id="amountErr"></span></td>
		    				</tr>
		    				<tr>
		    					<td><span class="required">*</span><span class="formLabel">Effective Date:</span></td>
		    					<td>
		    						<input type="text" class="dateField" name="effectiveDate" data-required="true" data-valid="validEffectiveDate" />
		    						<i id="validValue" class="fa" aria-hidden="true"></i>
		    					</td>
		    					<td><span class="err" id="effectiveDateErr"></span></td>
		    				</tr>
		    				<tr>
		    					<td colspan="2" style="text-align:center;">
		    						<input type="button" class="prettyButton" value="Save" id="goUpdate" />
		    						<input type="button" class="prettyButton" value="Cancel" id="cancelUpdate" />
		    					</td>
		    				</tr>
		    			</table>
		    		</form>
		    	</div>		    			    	
	    	</ansi:hasWrite>
    	</ansi:hasPermission>
    </tiles:put>
</tiles:insert>
