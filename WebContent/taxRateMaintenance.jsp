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
			/* #col_location {} */
			#col_id{	
				display : none; 
				}
			#col_rat{	text-align: right;	}
			#col_amt{	text-align: right;	}
			#col_eDt{	text-align: right;	}
			#col_aBy{ 	text-align: center;	}
			#col_aDt{	text-align: right;	}
			#col_uBy{	text-align: center;	}
			#col_uDt{	text-align: right;	}

			/* #col_location {} */
			#col_id_hdr{	
				display : none; 
				}
			#col_loc_hdr{	text-align: left;	}
			#col_rat_hdr{	text-align: right;	}
			#col_amt_hdr{	text-align: right;	}
			#col_eDt_hdr{	text-align: right;	}
			#col_aBy_hdr{ 	text-align: center;	}
			#col_aDt_hdr{	text-align: right;	}
			#col_uBy_hdr{	text-align: center;	}
			#col_uDt_hdr{	text-align: right;	}

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
						/*
						// bind the click events for update and delete respectively
						$('.updAction').bind("click", 	function($clickevent) 	{	doUpdate($clickevent);});
						$('.delAction').bind("click", 	function($clickevent) 	{	doDelete($clickevent);});
						// setup the rollover effect on each row
						$('.dataRow').bind("mouseover", function() { $(this).css('background-color','#CCCCCC');});
						$('.dataRow').bind("mouseout", 	function() { $(this).css('background-color','transparent');});
						*/
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

			function addRow(index, $code) {	
				var $rownum = index + 1;
				var row = '<tr class="dataRow">';
				row = row + '<td id="col_id">' + $code.taxRateId  + '</td>';
				row = row + '<td id="col_loc">' + $code.location  + '</td>';
				row = row + '<td id="col_rat">' + ($code.rate*100) + '%</td>';
				row = row + '<td id="col_amt">' + $code.amount + '</td>'; 
				row = row + '<td id="col_eDt">' + formatDate($code.effectiveDate) + '</td>';

       	    	<ansi:hasPermission permissionRequired="SYSADMIN">
					<ansi:hasWrite>
						row = row + '<td>';
						row = row + '<a href="#" class="updAction" data-row="' + $rownum +'"><span class="green fa fa-pencil" ari-hidden="true"></span></a> | ';
						row = row + '<a href="#" class="delAction" data-row="' + $rownum +'"><span class="red fa fa-trash" aria-hidden="true"></span></a>';
						row = row + '</td>';
					</ansi:hasWrite>
       			</ansi:hasPermission>
       			row = row + '</tr>';
       			//$('#displayTable tr:last').before(row);	
       			$('#displayTable').append(row);
			}
						
			function doUpdate($clickevent) {
				$clickevent.preventDefault();
				clearAddForm();

				// figure out which row was clicked.. 
				var $rownum = $clickevent.currentTarget.attributes['data-row'].value;

				// Add a title to the Add Form
				$("#addFormTitle").html("Update a Tax Code");
				// attach a data item to the add form containing the row number being updated
				$('#addForm').data('rownum',$rownum);
				
				// Use the rownum to build a locator for the row to be found.. 
                var $rowId = eval($rownum) + 1;
            	var $rowFinder = "#displayTable tr:nth-child(" + $rowId + ")"
            	var $row = $($rowFinder)  

				// get all cells in the row.. 
            	var tdList = $row.children("td");

				// Store row value in local vars.. 
				var $taxRateId = $row.children("td")[0].textContent;
            	var $location = $row.children("td")[1].textContent;
            	var $taxRate = $row.children("td")[2].textContent;
            	var $amount = $row.children("td")[3].textContent;
            	var $effectiveDate = $row.children("td")[4].textContent;
				
				console.log("Setting addForm Id to : " + $taxRateId);

				// populate addform with existing values.. 
				$('#addForm').data('taxRateId',$taxRateId);
            	$("#addForm input[name='location']").val($location);
            	$("#addForm input[name='taxRate']").val($taxRate);
            	$("#addForm input[name='taxAmount']").val($amount);
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
				var rownum = $clickevent.currentTarget.attributes['data-row'].value;
				$('#confirmDelete').data('rownum',rownum);
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
				console.log("ok, we're in the goUpdate routine");
				$clickevent.preventDefault();
				$outbound = {};

				// Copy the addForm input fieldnames and values into the $outbound array.
				$.each( $('#addForm :input'), function(index, value) {
					if ( value.name ) {
						$fieldName = value.name;
						$id = "#addForm input[name='" + $fieldName + "']";
						$val = $($id).val();
						$outbound[$fieldName] = $val;
					}
				});

				// Check to see if this is a new or existing record. 
				// If rownum is empty.. assumed to be a new record.
				if ( $('#addForm').data('rownum') == null ) {
					// if this is a new record...
					$url = "taxRate/add";
					console.log("ok, we're an add.. taxRate/add");
				} else {
					// if this is an existing record.. 
					
					// copy the rowdata from the display table 
					//   ..into $tableData 
					$rownum = $('#addForm').data('rownum');
					$taxRateId = $('#addForm').data('taxRateId');
					var $tableData = [];

					$url = "taxRate/" + $taxRateId;
					console.log("ok, we're an update.." + $url);
					console.log("Sending Outbound info " + $outbound)
				}
				
				// do the ajax call to add/update the fields.. 
				console.log("and we're making the call to ajax..." + $url);
				console.log("outbound var: " + JSON.stringify($outbound));
				var jqxhr = $.ajax({
					stype: 'POST',
					url: $url,
					data: JSON.stringify($outbound),
					success: function($data) {
						if ( $data.responseHeader.responseCode == 'SUCCESS') {
							if ( $url == "taxRate/add" ) {
								var count = $('#displayTable tr').length - 1;
								addRow(count, $data.data.code);
							} else {
				            	var $rownum = $('#addForm').data('rownum');
				                var $rowId = eval($rownum) + 1;
				            	var $rowFinder = "#displayTable tr:nth-child(" + $rowId + ")"
				            	var $rowTd = makeRow($data.data.code, $rownum);
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
            	var $rownum = $('#confirmDelete').data('rownum');

				// use $rownum to extract the column values into variables
            	var $taxRateId = $tableData[$rownum][0];

            	// create a JSON array of pairs containing the column values.
				$outbound = JSON.stringify({'taxRateId':$taxRateId});
            	console.debug($outbound);

				// make the ajax call to the java servlet to do the delete.
            	var jqxhr = $.ajax({
            	    type: 'delete',
            	    url: 'taxRate/delete',
            	    data: $outbound,
            	    success: function($data) {
            	    	$("#globalMsg").html($data.responseHeader.responseMessage).fadeIn(10).fadeOut(6000);
						if ( $data.responseHeader.responseCode == 'SUCCESS') {
							$rowfinder = "tr:eq(" + $rownum + ")"
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
				<th id="col_id_hdr">taxRate Id</th>
				<th id="col_loc_hdr">Location</th>
				<!-- For 2.0 Maybe ~kjw
					<th>State</th>
					<th>County</th>
					<th>City</th>
					<th>Type</th>
				-->
				<th id="col_rat_hdr">Rate</th>
				<th id="col_amt_hdr">Amount</th>
				<th id="col_eDt_hdr">Effective Date</th>
				<!--
				<th id="col_aBy_hdr">Added By</th>
				<th id="col_aDt_hdr">Added Date</th>
				<th id="col_uBy_hdr">Updated By</th>
				<th id="col_uDt_hdr">Update Date</th>
				-->
    		</tr>
    	</table>

    	<div class="addButtonDiv">
    		<input type="button" id="addButton" class="prettyWideButton" value="New" />
    	</div>
    	
    	<ansi:hasPermission permissionRequired="SYSADMIN">
    		<ansi:hasWrite>
		    	<div id="confirmDelete">
		    		Are You Sure You Want to Delete this Code?<br />
		    		<input type="button" id="cancelDelete" value="No" />
		    		<input type="button" id="doDelete" value="Yes" />
		    	</div>		    	
		    	<div id="addFormDiv">
		    		<form action="#" method="post" id="addForm">
		    			<table>
		    				<tr>
		    					<td><span class="required">*</span><span class="formLabel">Location:</span></td>
		    					<td>
		    						<input type="text" name="location" data-required="true" data-valid="validLocation" />
		    						<i id="validTable" class="fa" aria-hidden="true"></i>
		    					</td>
		    					<td><span class="err" id="tableNameErr"></span></td>
		    				</tr>
							<tr>
		    					<td><span class="required">*</span><span class="formLabel">Tax Rate:</span></td>
		    					<td>
		    						<input type="text" name="taxRate" data-required="true" data-valid="validTaxRate" />
		    						<i id="validField" class="fa" aria-hidden="true"></i>
		    					</td>
		    					<td><span class="err" id="fieldNameErr"></span></td>
		    				</tr>
		    				<tr>
		    					<td><span class="required">*</span><span class="formLabel">Tax Amount:</span></td>
		    					<td>
		    						<input type="text" name="taxAmount" data-required="true" data-valid="validTaxAmount" />
		    						<i id="validField" class="fa" aria-hidden="true"></i>
		    					</td>
		    					<td><span class="err" id="fieldNameErr"></span></td>
		    				</tr>
		    				<tr>
		    					<td><span class="required">*</span><span class="formLabel">effective Date:</span></td>
		    					<td>
		    						<input type="text" class="dateField" name="effectiveDate" data-required="true" data-valid="validEffectiveDate" />
		    						<i id="validValue" class="fa" aria-hidden="true"></i>
		    					</td>
		    					<td><span class="err" id="valueErr"></span></td>
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
