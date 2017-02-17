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
        </style>
        
        <script type="text/javascript">
		$(function() {

			$optionData = ANSI_UTILS.getOptions('JOB_FREQUENCY,JOB_STATUS,INVOICE_TERM,INVOICE_GROUPING,INVOICE_STYLE');
			var $jobFrequencyList = $optionData.jobFrequency;
			var $jobStatusList = $optionData.jobStatus;
			var $invoiceTermList = $optionData.invoiceTerm;
			var $invoiceGroupingList = $optionData.invoiceGrouping;
			var $invoiceStyleList = $optionData.invoiceStyle;

			$divisionList = ANSI_UTILS.getDivisionList();
			$buildingTypeList = ANSI_UTILS.makeBuildingTypeList();
			
			var $jobDetail = null;			
			var $quoteDetail = null;
			var $lastRun = null;
			var $nextDue = null;
			var $lastCreated = null;
			if ( '<c:out value="${ANSI_JOB_ID}" />' != '' ) {
				$jobData = JOBUTILS.getJobDetail('<c:out value="${ANSI_JOB_ID}" />');				
				$jobDetail = $jobData.job;
				$quoteDetail = $jobData.quote;
				$lastRun = $jobData.lastRun;
				$nextDue = $jobData.nextDue;
				$lastCreated = $jobData.lastCreated;
			}
			
			JOBPANEL.init("row0_jobPanel", $divisionList, "activateModal", $jobDetail);
			JOBPROPOSAL.init("row0_jobProposal", $jobFrequencyList, $jobDetail);
			JOBACTIVATION.init("row0_jobActivation", $buildingTypeList, $jobDetail);
			JOBDATES.init("row0_jobDates", $quoteDetail, $jobDetail);
			JOBSCHEDULE.init("row0_jobSchedule", $jobDetail, $lastRun, $nextDue, $lastCreated)
			JOBINVOICE.init("row0_jobInvoice", $invoiceStyleList, $invoiceGroupingList, $invoiceTermList, $jobDetail);
			JOBAUDIT.init("row0_jobAudit", $jobDetail);
			

			$("#jobNbr").focus();
		});


		</script>
    </tiles:put>
    
    
    <tiles:put name="content" type="string">    	
    	<h1>Job Maintenance</h1>
 		<%-- quotePanel requires a table because it is also used in the quote page --%>
		<table>
			<tbody>
				<jsp:include page="quotePanel.jsp">
					 <jsp:param name="panelname" value="row0" />
					 <jsp:param name="page" value="JOB" />
				</jsp:include>
			</tbody>
		</table>
		
		<webthing:jobActivateCancel page="JOB" namespace="activateModal" />
		
    </tiles:put>

</tiles:insert>

