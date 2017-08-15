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
        Cash Receipts Register
    </tiles:put>
    
    
    <tiles:put name="headextra" type="string">
    	<script type="text/javascript" src="js/ansi_utils.js"></script>
        <script type="text/javascript" src="js/jobMaintenance.js"></script>
        <link rel="stylesheet" href="css/datepicker.css" type="text/css" />
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
			#jobProposal {
				border:solid 1px #000000;
			}
			#jobActivation {
				border:solid 1px #000000;
				height:100%;
			}
			#jobSchedule {
				border:solid 1px #000000;
				height:100%;
			}
			#billTo {
				border:solid 1px #000000;
			}
			#jobSite {
				border:solid 1px #000000;
			}
			#jobDates {
				border:solid 1px #000000;
			}
			#jobInvoice {
				border:solid 1px #000000;
			}
			td.jobTableCell {
				width:50%;
				vertical-align:top;
			}
			.formFieldDisplay {
				margin-left:30px;
			}
			#invoiceModal {
				display:none;
			}
			#displayTicketTable {
    			border-collapse: collapse;
				width:90%;
			}
			#displayInvoiceTable {
    			border-collapse: collapse;
				width:90%;
			}			
			#displaySummaryTable {
    			border-collapse: collapse;
				width:90%;
			}
			#selectPanel {
				width:100%;
				display:none;
			}
			#summaryTable{
				width:100%;
				display:none;
			}
			#dataTables {
				width:100%;
				display:none;
			}
			td, th {
    			border: 1px solid #dddddd;
    			text-align: left;
    			padding: 8px;
    		}
			.workPanel {
				width:95%;
				border:solid 1px #000000;
				display:none;
			}
			#resultsDiv {
				display:none;
			}
			#resultsTable {
				width:600px;
			}
			.viewAction {
				color:#000000;
				text-decoration:none;
			}
        </style>
        
        <script type="text/javascript">
        $( document ).ready(function() {
        	
        	;TICKET_GEN = {
        		init : function() {
                	$('.dateField').datepicker({
                        prevText:'&lt;&lt;',
                        nextText: '&gt;&gt;',
                        showButtonPanel:true
                    });

                	TICKET_GEN.populateDivisionSelect();
                	
        			$("#goButton").click(function($event) {		
        				TICKET_GEN.go($event);
                	});
        			
        			//$("select").change(function() {
        			//	TICKET_GEN.markValid($(this));
        			//});
        		},		
        	
                populateDivisionSelect:function() {
                	$data = ANSI_UTILS.getDivisionList();
                	$select = $("#divisionId");
        			$('option', $select).remove();
        			$select.append(new Option("",null));
        			$.each($data, function($index, $val) {
        				var $display = $val.divisionNbr + "-" + $val.divisionCode;
        				$select.append(new Option($display, $val.divisionId));
        			});	
                },

                
                addResultsRow : function($data, $divisionText) {
                	var $row = '<tr>';
                	$row = $row + '<td>' + $divisionText + '</td>';
                	$row = $row + '<td>' + $data.startDate + '</td>';
                	$row = $row + '<td>' + $data.endDate + '</td>';
                	$viewLink = "<a href=\"#\" class=\"viewAction fa fa-search-plus tooltip\" aria-hidden=\"true\" data-startdate='"+ $data.startDate +"' data-id='"+$data.divisionId+"'><span class=\"tooltiptext\">View Tickets</span></a> ";
                	$row = $row + '<td>' + $viewLink + '</td>';
                	$('#resultsTable tr:last').after($row);
                	$('.viewAction').bind("click", function($event) {
                		console.debug("clicked");
                		$event.preventDefault();
                        var $divisionId = $event.currentTarget.attributes['data-id'].value;           
                        var $startDate = $event.currentTarget.attributes['data-startdate'].value;
                        var $lookupUrl = "ticketLookup.html?status=N&divisionId=" + $divisionId + "&startDate=" + $startDate;
						location.href=$lookupUrl;
                	});
                },
                
                
                go : function($event) {
                	var $divisionId = $("#divisionId").val();
    				var $startDate = $("#startDate").val();
    				var $endDate = $("#endDate").val();
    				var $outbound = {"startDate":$startDate,"endDate":$endDate,"divisionId":$divisionId};
					console.debug($outbound);
					
    		       	var jqxhr = $.ajax({
    		       		type: 'POST',
    		       		url: "cashReceiptsRegisterReport/",
    		       		data: JSON.stringify($outbound),
    		       		statusCode: {
    		       			200: function($data) {
    		       				console.debug($data);
    		       				$("#resultsDiv").fadeIn(2000);
    		       				if ( $data.responseHeader.responseCode == 'SUCCESS') {
    		       					$(".err").html("");
    		       					$("#globalMsg").html("Success! Tickets Generated").show().fadeOut(6000);
    		       					$("#startDate").val("");
    		       					$("#endDate").val("");
    		       					TICKET_GEN.addResultsRow($outbound, $("#divisionId option:selected").text());
    							} else if ( $data.responseHeader.responseCode == 'EDIT_FAILURE') {
    								$('.err').html("");
    								$.each($data.data.webMessages, function(key, messageList) {
    									var identifier = "#" + key + "Err";
    									msgHtml = "<ul>";
    									$.each(messageList, function(index, message) {
    										msgHtml = msgHtml + "<li>" + message + "</li>";
    									});
    									msgHtml = msgHtml + "</ul>";
    									$(identifier).html(msgHtml);
    								});	
    								$("#globalMsg").html($data.responseHeader.responseMessage).fadeIn(10).fadeOut(6000);							
    							} else {
    								$("#globalMsg").html("Unexpected Result: " + $data.responseHeader.responseCode).show();
    							}
    		       			},			       		
    	       				404: function($data) {
    	        	    		$("#globalMsg").html("System Error: Contact Support").show();
    	        	    	},
    						403: function($data) {
    							$("#globalMsg").html("Session Timeout. Log in and try again");
    						},
    		       			500: function($data) {
    	            	    	$("#globalMsg").html("System Error: Contact Support").show();
    	            		},
    		       		},
    		       		dataType: 'json'
    		       	});
                },
                
                markValid : function($inputField) {
                	$fieldName = $($inputField).attr('name');
                	$fieldGetter = "input[name='" + $fieldName + "']";
                	$fieldValue = $($fieldGetter).val();
                	$valid = '#' + $($inputField).data('valid');
    	            var re = /.+/;	            	 
                	if ( re.test($fieldValue) ) {
                		$($valid).removeClass("fa-ban");
                		$($valid).removeClass("inputIsInvalid");
                		$($valid).addClass("fa-check-square-o");
                		$($valid).addClass("inputIsValid");
                	} else {
                		$($valid).removeClass("fa-check-square-o");
                		$($valid).removeClass("inputIsValid");
                		$($valid).addClass("fa-ban");
                		$($valid).addClass("inputIsInvalid");
                	}
                }

        	}
			

            
			TICKET_GEN.init();
			
			
	
			
    });

		</script>
    </tiles:put>
    
    
    <tiles:put name="content" type="string">    	
    	<h1>Cash Receipts Register</h1>
    	
    	<table>
    		<tr>
    			<td><span class="formLabel">Division: </span></td>
    			<td><select id="divisionId" data-valid="validDivisionId"></select><span id="validDivisionId"></span></td>
    			<td><span class="err" id="divisionIdErr"></span></td>
    		</tr>
    		<tr>
    			<td><span class="formLabel">Start Date: </span></td>
    			<td><input type="text" id="startDate" class="dateField" /></td>
    			<td><span class="err" id="startDateErr"></span></td>
    		</tr>
    		<tr>
    			<td><span class="formLabel">End Date: </span></td>
    			<td><input type="text" id="endDate" class="dateField" /></td>
    			<td><span class="err" id="endDateErr"></span></td>
    		</tr>
    		<tr>
    			<td colspan="2"><input type="button" value="Go" id="goButton" /></td>
    		</tr>
    	</table>

    	
    	<div id="resultsDiv">
    		<table id="resultsTable">
   		       	<colgroup>
					<col style="width:25%;" />
					<col style="width:25%;" />
					<col style="width:25%;" />
					<col style="width:25%;" />
    			</colgroup>
    			<tr>
    				<td><span class="formLabel">Division</span></td>
    				<td><span class="formLabel">Start Date</span></td>
    				<td><span class="formLabel">EndDate</span></td>
    				<td><span class="formLabel">Action</span></td>
    			</tr>
    		</table>
    	</div>
    </tiles:put>

</tiles:insert>

