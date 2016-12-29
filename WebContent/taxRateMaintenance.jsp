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

			#col_01{							/* id column 		*/
				display : none; 
				}
			#col_02{	text-align: left;	}  /* location  		*/
			#col_03{	text-align: right;	}  /* rate 				*/
			#col_04{ 	text-align: right;	}  /* amount 			*/
			#col_05{	text-align: right;	}  /* effectiveDate 	*/
			#col_06{	text-align: center;	}  /* not used 		 	*/
			#col_07{	text-align: center;	}  /* not used 		 	*/
			#col_08{	text-align: center;	}  /* not used 		 	*/
			#col_09{	text-align: center;	}  /* not used 		 	*/

			/* #col_location {} */
			#col_01_hdr{							/* id column 		*/
				display : none; 
				}
			#col_02_hdr{	text-align: left;	} 	/* location  		*/
			#col_03_hdr{	text-align: right;	} 	/* rate 			*/
			#col_04_hdr{	text-align: right;	} 	/* amount 			*/
			#col_05_hdr{	text-align: right;	} 	/* effective date 	*/
			#col_06_hdr{ 	text-align: center;	} 	/* not used 		*/
			#col_07_hdr{	text-align: right;	} 	/* not used 		*/
			#col_08_hdr{	text-align: center;	} 	/* not used 		*/
			#col_09_hdr{	text-align: right;	} 	/* not used 		*/

        </style>
        
        <script type="text/javascript">
        $(function() {        
			var jqxhr = $.ajax({
				url: 'taxRate/list',
				type: 'GET',
				data: {},
				success: function($data) 
					{
						// if the ajax call succeeds... do all of this stuff.. 

						// Add the rows..
						// calls the addRow Function for each value pair recieved from url: 'taxRate/list'; 
						console.log("about to call .each function...");
						console.log($data); 
						$.each($data.data.taxRateList,	function(index, value) 		{	addRow(index, value);});

					    doFunctionBinding();
					},
				error : {
					function($data) { console.log("about to call .each function...");}
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

			function makeRow($code, $data_item_id){
				var _td = '';
				_td = _td + '	<td id="col_01">' + $data_item_id  + 		'</td>';
				_td = _td + '	<td id="col_02">' + $code.location + 		'</td>';
				_td = _td + '	<td id="col_03">' + $code.rate + 			'</td>';
				_td = _td + '	<td id="col_04">' + $code.amount + 			'</td>'; 
				_td = _td + '	<td id="col_05">' + $code.effectiveDate + 	'</td>';
       	    	<ansi:hasPermission permissionRequired="SYSADMIN">
					<ansi:hasWrite>
						_td = _td + '<td>';
						_td = _td + '<a href="#" class="updAction" data-item-id="' + $data_item_id +'"><span class="green fa fa-pencil" ari-hidden="true"></span></a> | ';
						_td = _td + '<a href="#" class="delAction" data-item-id="' + $data_item_id +'"><span class="red fa fa-trash" aria-hidden="true"></span></a>';
						_td = _td + '</td>';
					</ansi:hasWrite>
       			</ansi:hasPermission>
				return _td;
			}
		
			function addRow(index, $code) {					
				var $data_item_id = $code.taxRateId;
				var $rownum = index + 1;
				var row = '';
				row = row + '<tr class="dataRow" id="data-item-id-' + $data_item_id +'">';
				row = row + makeRow($code, $data_item_id);
       			row = row + '</tr>';
       			//$('#displayTable tr:last').before(row);	
       			$('#displayTable').append(row);
				console.log("addRow() : Row being added = " + row)				
			}
						
			function doUpdate($clickevent) {
				console.log("doUpdate(): We're in");
				$clickevent.preventDefault();
				clearAddForm();

				// figure out which row was clicked.. 
				var $data_item_id = $clickevent.currentTarget.attributes['data-item-id'].value;
				console.log("doUpdate(): Data Item Id = " +  $data_item_id);

				// Add a title to the Add Form
				$("#addFormTitle").html("Update a Tax Code");
				// attach a data item to the add form containing the row number being updated
				$('#addForm').data('data_item_id',$data_item_id);
				
				// Use the data_item_id to build a locator for the row to be found.. 
                //var $data_item_id = eval($rownum) + 1;
            	//var $rowFinder = "#displayTable tr:nth-child(" + $rowId + ")"
            	var $rowFinder = '#data-item-id-' + $data_item_id
				console.log("doUpdate(): rowfinder value = " + $rowFinder);
            	//var $row = $($rowFinder)  
				var $row = $($rowFinder)
				console.log("doUpdate(): html for row to update = " + $($rowFinder).text());

				// get all cells in the row.. 
            	var tdList = $row.children("td");
				console.log("doUpdate(): td list = " + tdList.text());

				// Store row value in local vars.. 
				var $taxRateId = $row.children("td")[0].textContent;
            	var $location = $row.children("td")[1].textContent;
            	var $rate = $row.children("td")[2].textContent;
            	var $amount = $row.children("td")[3].textContent;
            	var $effectiveDate = $row.children("td")[4].textContent;
				
				console.log("doUpdate(): taxRateId = " + $taxRateId);
				console.log("doUpdate(): location = " + $location);
				console.log("doUpdate(): rate = " + $rate);
				console.log("doUpdate(): amount = " + $amount);
				console.log("doUpdate(): effectiveDate = " + $effectiveDate);

				// populate addform with existing values.. 
				console.log("doUpdate(): Setting addForm.data-item-id to " + $data_item_id);
				$('#addForm').data('data-item-id',$data_item_id);
            	$("#addForm input[name='location']").val($location);
            	$("#addForm input[name='rate']").val($rate);
            	$("#addForm input[name='amount']").val($amount);
				$("#addForm input[name='effectiveDate']").val($effectiveDate);
            	
				$.each( $('#addForm :input'), function(index, value) {
					markValid(value);
				});

             	$('#addFormDiv').bPopup({
					modalClose: false,
					opacity: 0.6,
					positionStyle: 'fixed' //'fixed' or 'absolute'
				});				
			}
						
			function doDelete($clickevent) {
				$clickevent.preventDefault();
				var data_item_id = $clickevent.currentTarget.attributes['data-item-id'].value;
				$('#confirmDelete').data('data-item-id',data_item_id);
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
				// Add the new taxRate to the database
				console.log("#goUpdate() : ok, we're in the goUpdate routine");
				$clickevent.preventDefault();
				$outbound = {};

				// Copy the addForm input fieldnames and values into the $outbound array.
				$.each( $('#addForm :input'), function(index, value) {
					if ( value.name ) {
						$fieldName = value.name;
						$id = "#addForm input[name='" + $fieldName + "']";
						$val = $($id).val();
						console.log("#goUpdate() : outbound " + $fieldName + " = " + $val);
						$outbound[$fieldName] = $val;
					}
				});
				console.log("#goUpdate() : copied values from for to data array ");

				// Check to see if this is a new or existing record. 
				// If rownum is empty.. assumed to be a new record.
				if ( $('#addForm').data('data-item-id') == null ) {
					// if this is a new record...
					$url = "taxRate/add";
					console.log("#goUpdate() : this is a new record.. using url " + $url);
				} else {
					// if this is an existing record.. 
					
					// copy the rowdata from the display table 
					//   ..into $tableData 
					$rownum = $('#addForm').data('rownum');
					var $data_item_id = $('#addForm').data('data-item-id');
					var $tableData = [];

					$url = "taxRate/" + $data_item_id;
					console.log("#goUpdate() : this is an existing record.. using url " + $url);					
					console.log("#goUpdate() : Sending Outbound info " + $outbound)
				}
				
				// do the ajax call to add/update the fields.. 
				console.log("#goUpdate() : and we're making the call to ajax..." + $url);
				console.log("#goUpdate() : outbound var: " + JSON.stringify($outbound));
				var jqxhr = $.ajax({
					type: 'POST',
					url: $url,
					data: JSON.stringify($outbound),
					//data: $outbound,
					success: function($data) {
						if ( $data.responseHeader.responseCode == 'SUCCESS') {
							if ( $url == "taxRate/add" ) {
								var count = $('#displayTable tr').length - 1;
								console.log("goUpdate() : url = " + $url);
								console.log("goUpdate() : Successfully Added the Row!");
								console.log("goUpdate() : Attempting to add the new row to the display table!");
								console.log("goUpdate() : We're very excited!");
								console.log("goUpdate() : now calling addRow(" + count + ',' + $data.data.code + ")");
								addRow(count, $data.data.code);
							} else {
								console.log("goUpdate() : url = " + $url);
								$data_item_id = $('#addForm').data('data-item-id');
								console.log("goUpdate() : data_item_id = " + $data_item_id);
				            	//var $rownum = $('#addForm').data('rownum');
				                //var $rowId = eval($rownum) + 1;
            					var $rowFinder = '#data-item-id-' + $data_item_id;
								console.log("goUpdate() : rowFinder = " + $rowFinder);
								
				            	//var $rowFinder = "#displayTable tr:nth-child(" + $rowId + ")"
								console.log("goUpdate() : Successfully updated the Row!");
								console.log("goUpdate() : Attempting to update the data in the correct row to the display table!");
								console.log("goUpdate() : We're very excited!");

								console.log("goUpdate() : code.taxRateId = " + $data.taxRateId);
								console.log("goUpdate() : code.location = " + $("#addForm input[name='location']").val);
								console.log("goUpdate() : code.amount = " + $("#addForm input[name='amount']").val);
								console.log("goUpdate() : code.rate = " + $("#addForm input[name='rate']").val);
								console.log("goUpdate() : code.effectiveDate = " + $("#addForm input[name='effectiveDate']").val);
								console.log("goUpdate() : calling makeRow using data-item-id " + $data_item_id);
				            	var $rowTd = makeRow($outbound, $data_item_id);
				            	$($rowFinder).html($rowTd);
							}
							doFunctionBinding();
							clearAddForm();
							$('#addFormDiv').bPopup().close();
							$("#globalMsg").html($data.data.webMessages['GLOBAL_MESSAGE'][0]).fadeIn(10).fadeOut(6000);
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
				$clickevent.preventDefault();
				// Add a title to the Add Form
				$("#addFormTitle").html("Add a New Tax Code");

             	$('#addFormDiv').bPopup({
					modalClose: false,
					opacity: 0.6,
					positionStyle: 'fixed' //'fixed' or 'absolute'

				});	
			});
			
			$("#cancelUpdate").click( function($clickevent) {
				console.debug("Canceling update");
				$clickevent.preventDefault();
				clearAddForm();
				$('#addFormDiv').bPopup().close();
			});

			/*
			$("#goUpdate").click( function($clickevent) {
				$clickevent.preventDefault();
				console.debug("Doing update");
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
				
				// get the row number to be deleted from the confirm delete popup
				// ( rownum is a data item added to the popup when it was created )
				var $data_item_id = $('#confirmDelete').data('data-item-id');

				// use $rownum to extract the column values into variables
            	// var $taxRateId = $tableData[$rownum][0];

				// create the url to call to delete this row
				var $deleteUrl = 'taxRate/' + $data_item_id;
				console.debug($deleteUrl);

            	// create a JSON array of pairs containing the column values.
				$outbound = JSON.stringify({'taxRateId':$data_item_id});
            	console.debug($outbound);


				// make the ajax call to the java servlet to do the delete.
            	var jqxhr = $.ajax({
            	    type: 'delete',
            	    url: $deleteUrl,
					//data: JSON.stringify($outbound),
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
				<th id="col_01_hdr">taxRate Id</th>
				<th id="col_02_hdr">Location</th>
				<!-- For 2.0 Maybe ~kjw
					<th>State</th>
					<th>County</th>
					<th>City</th>
					<th>Type</th>
				-->
				<th id="col_03_hdr">Rate</th>
				<th id="col_04_hdr">Amount</th>
				<th id="col_05_hdr">Effective Date</th>
				<!--
				<th id="col_aBy_hdr">Added By</th>
				<th id="col_aDt_hdr">Added Date</th>
				<th id="col_uBy_hdr">Updated By</th>
				<th id="col_uDt_hdr">Update Date</th>
				-->
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
		    		Are You Sure You Want to Delete this Tax Rate?<br />
		    		<input type="button" id="cancelDelete" value="No" />
		    		<input type="button" id="doDelete" value="Yes" />
		    	</div>		    	
		    	<div id="addFormDiv">
		    		<h2 id="addFormTitle"></h2>					
		    		<form action="#" method="post" id="addForm">
		    			<table>
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
