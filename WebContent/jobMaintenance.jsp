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
    	<script type="text/javascript" src="js/ansi_utils.js"></script>
        <script type="text/javascript" src="js/jobMaintenance.js"></script>
                
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
			#billTo {
				border:solid 1px #000000;
			}
			#jobSite {
				border:solid 1px #000000;
			}
			#jobDates {
				border:solid 1px #000000;
			}
			#jobActivation2 {
				border:solid 1px #FF0000;
			}
			td.jobTableCell {
				width:50%;
				vertical-align:top;
			}
        </style>
        
        <script type="text/javascript">
		$(function() {

			$optionData = ansi_utils.getOptions('JOB_FREQUENCY,JOB_STATUS')
			var $jobFrequencyList = $optionData.jobFrequency;
			var $jobStatusList = $optionData.jobStatus;

			$divisionList = ansi_utils.getDivisionList();
			
			JOBDESCRIPTION.init("jobDescription", $jobFrequencyList);
			JOBACTIVATION.init("jobActivation");
			JOBPANEL.init("jabPanel", $divisionList);

			$("#jobNbr").focus();
		});


		</script>
    </tiles:put>
    
    
    <tiles:put name="content" type="string">
    	<h1>Job Maintenance</h1>
    	JOB ID: <c:out value="${ANSI_JOB_ID}" />
		<table>
			<tr>
				<td class="jobTableCell">
					<webthing:addressPanel namespace="JOBSITE" label="Job Site" cssId="jobSite" />
				</td>
				<td class="jobTableCell">
					<webthing:addressPanel namespace="BILLTO" label="Bill To" cssId="billTo" />
				</td>
			</tr>
		</table>
		<table style="border:solid 1px #000000; margin-top:8px;">
			<tr>
				<td class="jobTableCell" colspan="2">
					<%--
					<form name="jobForm">
						JOB: <input type="text" name="jobNbr" id="jobNbr" />
						Status: <input type="text" name="jobStatus" />
						Div: <select name="division"></select>
						<div style="float:right;">
							<input type="button" value="Activate Job" />
							<input type="button" value="Cancel Job" />
						</div>
					</form>
					 --%>
					 <webthing:jobPanel namespace="jobPanel" cssId="jobPanel" />
				</td>
			</tr>
			<tr>
				<td class="jobTableCell">
					<webthing:jobDescription namespace="jobDescription" cssId="jobProposal" />
				</td>
				<td class="jobTableCell">
					<webthing:jobActivation namespace="jobActivation" cssId="jobActivation" />
				</td>
			</tr>
			<tr>
				<td class="jobTableCell">
					<webthing:jobDates namespace="JOBDATES" cssId="jobDates" />
				</td>
				<td class="jobTableCell">
					<webthing:jobActivation namespace="JOBACTIVATION2" cssId="jobActivation2" />
				</td>
			</tr>
		</table>    	
		
    </tiles:put>

</tiles:insert>

