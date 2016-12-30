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
        Job Maintenance
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
			#jobProposal {
				border:solid 1px #000000;
			}
			#jobActivation {
				border:solid 1px #000000;
			}
			#billTo {
				border:solid 1px #000000;
			}
			#jobSite {
				border:solid 1px #000000;
			}
        </style>
    </tiles:put>
    
    
    <tiles:put name="content" type="string">
    	<h1>Job Maintenance</h1>
		<table>
			<tr>
				<td style="width:50%;">
					<webthing:addressPanel namespace="JOBSITE" label="Job Site" cssId="jobSite" />
				</td>
				<td>
					<webthing:addressPanel namespace="BILLTO" label="Bill To" cssId="billTo" />
				</td>
			</tr>
			<tr>
				<td style="width:50%;">
					<webthing:jobDescription namespace="JOBDESCRIPTION" cssId="jobProposal" />
				</td>
				<td>
					<webthing:jobActivation namespace="JOBACTIVATION" cssId="jobActivation" />
				</td>
			</tr>
		</table>    	
		
        <script type="text/javascript">        
		$( document ).ready(function() {
			function getJobFreqs() {
        		var jqxhr1 = $.ajax({
    				type: 'GET',
    				url: 'options',
    				data: 'JOB_FREQUENCY,JOB_STATUS',
    				success: function($data) {
    					console.debug("Getting job freq");
    					JOBDESCRIPTION.setJobFrequency($data.data.jobFrequency);
    				},
    				statusCode: {
    					403: function($data) {
    						$("#useridMsg").html($data.responseJSON.responseHeader.responseMessage);
    					} 
    				},
    				dataType: 'json'
    			});
			}
    	
			function getBuildingTypes() {
        		var jqxhr2 = $.ajax({
    				type: 'GET',
    				url: 'code/list/job',
    				data: {},
    				success: function($data) {
    			        var $buildingTypeList = [];
    					console.debug("Getting building type");
    					$.each($data.data.codeList, function(index, value) {
    						if ( value.fieldName == 'building_type') {
    							$buildingTypeList.push(value);
    						}
    					});
    					JOBACTIVATION.setBuildingType($buildingTypeList);
    				},
    				statusCode: {
    					403: function($data) {
    						$("#useridMsg").html($data.responseJSON.responseHeader.responseMessage);
    					} 
    				},
    				dataType: 'json'
    			});
			}
			
			JOBDESCRIPTION.init();
			JOBACTIVATION.init();
			getBuildingTypes();
			getJobFreqs();
        });
        </script>        
    </tiles:put>

</tiles:insert>

