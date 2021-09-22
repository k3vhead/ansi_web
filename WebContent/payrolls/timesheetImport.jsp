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
		Payroll Timesheet Import
    </tiles:put>
    
    
    <tiles:put name="headextra" type="string">
       	<link rel="stylesheet" href="css/lookup.css" />
    	<link rel="stylesheet" href="css/ticket.css" />
    	<link rel="stylesheet" href="css/document.css" />
    	<script type="text/javascript" src="js/ansi_utils.js"></script>
    	<script type="text/javascript" src="js/addressUtils.js"></script>
    	<script type="text/javascript" src="js/lookup.js"></script> 
    	<script type="text/javascript" src="js/document.js"></script> 
    
        <style type="text/css">
        	#display-div {
        		display:none;
        	}
        	#table-container {
        		width:100%;
        	}
			#filter-container {
        		width:402px;
        		float:right;
        	}			
        	#organization-display {
        		display:none;
        	}
        	#organization-display table {
        		width:100%;
        		border:solid 1px #404040;
        	}
        	#organization-display th {
        		text-align:left;
        	}
        	#organization-edit table {
        		width:100%;
        		border:solid 1px #404040;
        	}
        	#organization-edit th {
        		text-align:left;
        	}
        	.action-link {
        		text-decoration:none;
        	}
			.dataTables_wrapper {
				padding-top:10px;
			}	
			.form-label {
				font-weight:bold;
			}
			.org-status-change {
				display:none;
				cursor:pointer;
			}
			.view-link {
				color:#404040;
			}		
        </style>
        
        <script type="text/javascript">    
        
       	$(document).ready(function(){
           	;TIMESHEET_IMPORT = {
           		init : function() {
           			TIMESHEET_IMPORT.makeClickers();            			
           		},
           	
           	
           		makeClickers : function() {
           			$("#save-button").click(function($event) {
           				$("#prompt-div .err").html("");
           				var file = document.getElementById('timesheet-file').files[0];
           				var reader = new FileReader();
           				if ( file == null ) { 
							$("#prompt-div .timesheetFileErr").html("Required Value").show();
							if ( $("#prompt-div select[name='divisionId']").val().length == 0) { $("#prompt-div .divisionIdErr").html("Required Value").show(); }
							if ( $("#prompt-div input[name='payrollDate']").val().length == 0 ) {$("#prompt-div .payrollDateErr").html("Required Value").show(); }
							if ( $("#prompt-div select[name='state']").val().length == 0 ) {$("#prompt-div .stateErr").html("Required Value").show(); }
							if ( $("#prompt-div input[name='city']").val().length == 0 ) {$("#prompt-div .cityErr").html("Required Value").show(); }
           				} else {
	           				reader.readAsText(file, 'UTF-8');	           				
	           				reader.onload = TIMESHEET_IMPORT.saveFile;
	           				// reader.onprogress ...  (progress bar)
           				}
           			});
           			
           			$("#display-div input[name='cancelButton']").click(function($event) {
           				$("#display-div").hide();
           				$("#prompt-div").show();
           			});
           		},
           		

           		
           		
           		processUploadFailure : function($data) {
           			console.log("processUploadFailure");
           			$("#prompt-div .err").html("");
           			$.each($data.data.webMessages, function($index, $value) {
           				var $selector = "#prompt-div ." + $index + "Err";
           				$($selector).html($value[0]).show();
           			});
           		},
           		
           		
           		processUploadSuccess : function($data) {
           			console.log("processUploadSuccess");
           			$("#prompt-div").hide();
           			$("#display-div").show();
           			$("#display-div .divisionId").html($data.data.request.div);
           			$("#display-div .payrollDate").html($data.data.request.payrollDate);
           			$("#display-div .state").html($data.data.request.state);
           			$("#display-div .city").html($data.data.request.city);
           			$("#display-div .timesheetFile").html($data.data.request.timesheetFile);
           		},
           		
           		
           		
           		
           		saveFile : function($event) {
           			var results = $event.target.result;
           			var fileName = document.getElementById('timesheet-file').files[0].name;
           			var formData = new FormData();
           			var file = document.getElementById('timesheet-file').files[0];
           			formData.append('timesheetFile',file, fileName);
           			formData.append('divisionId', $("#prompt-div select[name='divisionId']").val());
           			formData.append('payrollDate', $("#prompt-div input[name='payrollDate']").val());
           			formData.append('state', $("#prompt-div select[name='state']").val());
           			formData.append('city', $("#prompt-div input[name='city']").val());
           			
           			var xhr = new XMLHttpRequest();
           			xhr.open('POST',"payroll/timesheetImport", true);
           			
           			xhr.onload = function() {
           				if ( xhr.status == 200 ) {
           					var $data = JSON.parse(this.response);
           					if ( $data.responseHeader.responseCode == "EDIT_FAILURE") {
           						TIMESHEET_IMPORT.processUploadFailure($data);
           					} else if ( $data.responseHeader.responseCode == "SUCCESS" ) {
           						TIMESHEET_IMPORT.processUploadSuccess($data);
           					} else {
           						$("#globalMsg").html("Invalid response code " + $data.responseHeader.responseCode + ". Contact Support");
           					}
           				} else {
           					$("#globalMsg").html("Response Code " + xhr.status + ". Contact Support");
           				}
           			};
           			
           			xhr.send(formData);
           		}
           	};
           	
           	TIMESHEET_IMPORT.init();
            	
        });
        		
        </script>        
    </tiles:put>
    
   <tiles:put name="content" type="string">
    	<h1>Payroll Timesheet Import</h1> 

    	<div id="prompt-div">
    		<table>
    			<tr>
    				<td><span class="form-label">Division:</span></td>
    				<td>
    					<select name="divisionId">
    						<option value=""></option>
    						<ansi:selectOrganization type="DIVISION" active="true" />
    					</select>
    				</td>
    				<td><span class="divisionIdErr err"></span></td>
    			</tr>
    			<tr>
    				<td><span class="form-label">Week Ending:</span></td>
    				<td>
    					<input type="date" name="payrollDate" />
    				</td>
    				<td><span class="payrollDateErr err"></span></td>
    			</tr>
    			<tr>
    				<td><span class="form-label">State:</span></td>
    				<td>
    					<select name="state">
    						<option value=""></option>
    						<webthing:states />
    					</select>
    				</td>
    				<td><span class="stateErr err"></span></td>
    			</tr>
    			<tr>
    				<td><span class="form-label">City/Jurisdiction:</span></td>
    				<td>
    					<input type="text" name="city" />
    				</td>
    				<td><span class="cityErr err"></span></td>
    			</tr>
    			<tr>
    				<td><span class="form-label">Payroll File:</span></td>
    				<td>
    					<input type="file" id="timesheet-file" name="files[]" />
    				</td>
    				<td><span class="timesheetFileErr err"></span></td>
    			</tr>
    			<tr>
    				<td colspan="2" style="text-align:center;"><input type="button" value="Save" id="save-button" /></td>
    			</tr>
    			
    		</table>
    	</div>

		<div id="display-div">
			<table style="width:100%;">
    			<tr>
    				<td><span class="form-label">Division:</span></td>
    				<td><span class="form-label">Week Ending:</span></td>
    				<td><span class="form-label">State:</span></td>
    				<td><span class="form-label">City/Jurisdiction:</span></td>
    				<td><span class="form-label">Payroll File:</span></td>
    				<td rowspan="2"><input type="button" value="Cancel" name="cancelButton" class="action-button" /></td>
    			</tr>
    			<tr>
    				<td><span class="divisionId"></span></td>
    				<td><span class="payrollDate"></span></td>
    				<td><span class="state"></span></td>
    				<td><span class="city"></span></td>
    				<td><span class="timesheetFile"></span></td>
    			</tr>
    			<tr>
    			</tr>
    			
    		</table>
			
		</div>
    </tiles:put>
		
</tiles:insert>

