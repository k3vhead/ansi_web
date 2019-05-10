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
        Job Lookup
    </tiles:put>
    
    
    <tiles:put name="headextra" type="string">
        <style type="text/css">
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
			#lookupModal {
				display:none;
				text-align:center;
			}
			.prettyWideButton {
				height:30px;
				min-height:30px;
			}
			select	{
				width:80px !important;
				max-width:80px !important;
			}
        	.ansi-contact-container {
        		width:90%;
        	}
        	.ansi-contact-method-is-business-phone { display:none; } 
			.ansi-contact-method-is-mobile-phone { display:none; }
			.ansi-contact-method-is-fax { display:none; }
			.ansi-contact-method-is-email { display:none; }
			
        </style>
        
        <script type="text/javascript">
        
        $(document).ready(function() {
        	;JOBLOOKUP = {
       			dataTable : null,
       			lookupType : '<c:out value="${ANSI_JOB_LOOKUP_TYPE}" />',
       			pacColumns : [17,18,19,20],
       			contactColumns : [13,14,15,16],
       			jobColumns : [11,12],
       			
       			
       			
       			
        		init : function() {
    				$.each($('input'), function () {
						$(this).css("height","20px");
						$(this).css("max-height", "20px");
					});
    					
    				$('.ScrollTop').click(function() {
    					$('html, body').animate({scrollTop: 0}, 800);
    	      	  		return false;
    	      	    });
    				if ( JOBLOOKUP.lookupType == null || JOBLOOKUP.lookupType == '' ) {
    					JOBLOOKUP.makeLookupModal();
    					$("#lookupModal").dialog("open");
    				} else {
    					JOBLOOKUP.createTable();
    				}
                },
                
                
                
                createTable : function() {
            		var dataTable = $('#jobTable').DataTable( {
            			"aaSorting":		[[0,'desc']],
            			"processing": 		true,
            	        "serverSide": 		true,
            	        "autoWidth": 		false,
            	        "deferRender": 		true,
            	        "scrollCollapse": 	true,
            	        "scrollX": 			true,
            	        rowId: 				'dt_RowId',
            	        dom: 				'Bfrtip',
            	        "searching": 		true,
            	        "searchDelay":		800,
            	        lengthMenu: [
            	        	[ 10, 50, 100, 500, 1000 ],
            	            [ '10 rows', '50 rows', '100 rows', '500 rows', '1000 rows' ]
            	        ],
            	        buttons: [
            	        	'pageLength',
            	        	'copy', 
            	        	'csv', 
            	        	'excel', 
            	        	{extend: 'pdfHtml5', orientation: 'landscape'}, 
            	        	'print',
            	        	{extend: 'colvis',	label: function () {doFunctionBinding();$('#jobTable').draw();}},
            	        	{
	        	        		text:'Job',
	        	        		action: function(e, dt, node, config) {
	        	        			JOBLOOKUP.showJobColumns();	        	        			
	        	        		}
	        	        	},{
	        	        		text:'Contacts',
	        	        		action: function(e, dt, node, config) {
	        	        			JOBLOOKUP.showContactColumns();	        	        			
	        	        		}
	        	        	},{
	        	        		text:'PAC',
	        	        		action: function(e, dt, node, config) {
	        	        			JOBLOOKUP.showPacColumns();	        	        			
	        	        		}
	        	        	}
            	        ],
            	        "columnDefs": [
             	            { "orderable": false, "targets": -1 },
            	            { className: "dt-left", "targets": [4,5,6,11] },
            	            { className: "dt-center", "targets": [0,1,2,3,7,8,10,12,17,18,19,20,-1] },
            	            { className: "dt-right", "targets": [9]}
            	         ],
            	        "paging": true,
    			        "ajax": {
    			        	"url": "jobTable",
    			        	"type": "GET"
    			        	},
    			        columns: [
    			        	{ width: "4%", title: "<bean:message key="field.label.jobId" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {	
    			            	if(row.jobId != null){return (row.jobId+"");}
    			            } },
    			            { width: "4%", title: "<bean:message key="field.label.quoteName" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.quoteId != null){return ('<ansi:hasPermission permissionRequired="QUOTE"><a href="quoteMaintenance.html?id='+ row.quoteId+ '" style="color:#404040"></ansi:hasPermission>' + row.quoteNumber + row.revision +'<ansi:hasPermission permissionRequired="QUOTE"></ansi:hasPermission>');}
    			            } },
    			            { width: "3%", title: "<bean:message key="field.label.jobStatus" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.jobStatus != null){return (row.jobStatus+"");}
    			            } },
    			            { width: "4%", title: "<bean:message key="field.label.divisionNbr" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.divisionNbr != null){return (row.divisionNbr+"-"+row.divisionCode);}
    			            } },
    			            { width: "10%", title: "<bean:message key="field.label.billToName" />" , "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {	
    			            	if(row.billToName != null){return (row.billToName+"");}
    			            } },
    			            { width: "10%", title: "<bean:message key="field.label.jobSiteName" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.jobSiteName != null){return (row.jobSiteName+"");}
    			            } },
    			            { width: "10%", title: "<bean:message key="field.label.jobSiteAddress" />",  "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.jobSiteAddress != null){return (row.jobSiteAddress+", " + row.jobSiteCity + ", " + row.jobSiteState );}
    			            } },
    			            { width: "5%", title: "<bean:message key="field.label.startDate" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.startDate != null){return (row.startDate+"");}
    			            } },
    			            { width: "3%", title: "<bean:message key="field.label.jobFrequency" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.jobFrequency != null){return (row.jobFrequency+"");}
    			            } },
    			            { width: "5%", title: "<bean:message key="field.label.pricePerCleaning" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.pricePerCleaning != null){return (row.pricePerCleaning+"");}
    			            } },
    			            { width: "4%", title: "<bean:message key="field.label.jobNbr" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) { 	
    			            	if(row.jobNbr != null){return (row.jobNbr+"");}
    			            } },
    			            { width: "24%", title: "<bean:message key="field.label.serviceDescription" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.serviceDescription != null){return (row.serviceDescription+"");}
    			            } },
    			            { width: "4%", title: "<bean:message key="field.label.poNumber" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.poNumber != null){return (row.poNumber+"");}	    
    			            } },	
    		        		{ width: "9%", title: "<bean:message key="field.label.jobContact" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    	        			if(row.jobContact != null){
    	        				var preferredContact = row.jobContact.preferredContact;
    	        				icon = JOBLOOKUP.makeContactIcon(preferredContact);    	        				
    	        				return (row.jobContact.lastName+",&nbsp;"+row.jobContact.firstName+ "<br />" + icon + row.jobContact.contactMethod);
    	        				}
	    	        		} },
    	        			{ width: "9%", title: "<bean:message key="field.label.siteContact" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    	        			if(row.siteContact != null){
    	        				var preferredContact = row.siteContact.preferredContact;
        	        			icon = JOBLOOKUP.makeContactIcon(preferredContact);
        	        			return (row.siteContact.lastName+",&nbsp;"+row.siteContact.firstName+ "<br />" + icon + row.siteContact.contactMethod);
        	        			}
    	    	        	} },
    	        			{ width: "9%", title: "<bean:message key="field.label.contractContact" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    	        			if(row.contractContact != null){
    	        				var preferredContact = row.contractContact.preferredContact;
        	        			icon = JOBLOOKUP.makeContactIcon(preferredContact);
        	        			return (row.contractContact.lastName+",&nbsp;"+row.contractContact.firstName+ "<br />" + icon + row.contractContact.contactMethod);
        	        			}
    	    	        	} },
        	        		{ width: "9%", title: "<bean:message key="field.label.billingContact" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
        	        		if(row.jobContact != null){
    	        				var preferredContact = row.billingContact.preferredContact;
        	        			icon = JOBLOOKUP.makeContactIcon(preferredContact);
        	        			return (row.billingContact.lastName+",&nbsp;"+row.billingContact.firstName+ "<br />" + icon + row.billingContact.contactMethod);
        	        			
        	        			}
    	    	        	} },
    	    	        	{ width: "5%", title: "Proposed", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    	    	        		if(row.proposalDate != null){return (row.proposalDate+"");}	    
    			            } },
    			            { width: "5%", title: "Active", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.activationDate != null){return (row.activationDate+"");}	    
    			            } },
    			            { width: "5%", title: "Cancel", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.cancelDate != null){return (row.cancelDate+"");}	    
    			            } },
    			            { width: "5%", title: "Reason", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.cancelReason != null){return (row.cancelReason+"");}	    
    			            } },
    			            { width: "4%", title: "<bean:message key="field.label.action" />",  data: function ( row, type, set ) {	
    			            	{
    				            	var $edit = '<a href="jobMaintenance.html?id='+row.jobId+'" class="editAction" data-id="'+row.jobId+'"><webthing:edit>View</webthing:edit></a>';
    			            		return "<ansi:hasPermission permissionRequired='QUOTE_READ'>"+$edit+"</ansi:hasPermission>";
    			            	}
    			            	
    			            } }],
    			            "initComplete": function(settings, json) {
    			            	JOBLOOKUP.doFunctionBinding();
    			            },
    			            "drawCallback": function( settings ) {
    			            }
    			    } );
            	},
            	
				doFunctionBinding : function() {
					if ( JOBLOOKUP.lookupType == 'JOB' ) {
						JOBLOOKUP.showJobColumns();
					}
					if ( JOBLOOKUP.lookupType == 'PAC' ) {
						JOBLOOKUP.showPacColumns();
					}
					if ( JOBLOOKUP.lookupType == 'CONTACT' ) {
						JOBLOOKUP.showContactColumns();
					}
				},
				
				makeContactIcon : function(preferredContact) {
					
					if (preferredContact == "mobile_phone"){
						icon = '<webthing:mobilePhoneIcon>Mobile Phone</webthing:mobilePhoneIcon>';
					} else if (preferredContact == "email"){
						icon = '<webthing:emailIcon>Email</webthing:emailIcon>';
					} else if (preferredContact == "business_phone"){
						icon = '<webthing:phoneIcon>Business Phone</webthing:phoneIcon>';
					} else if (preferredContact == "fax"){
						icon = '<webthing:faxIcon>Fax</webthing:faxIcon>';
					} else {
						icon = preferredContact;
					}
					return icon;
				},
				
				makeLookupModal : function() {	
					$("#lookupModal" ).dialog({
						autoOpen: false,
						height: 300,
						width: 500,
						modal: true,
						closeOnEscape:false,
						title:"Job Lookup",
						open: function(event, ui) {
							$(".ui-dialog-titlebar-close", ui.dialog | ui).hide();
						},
						buttons: [
							{
								id: "exitButton",
								click: function($event) {
									location.href="dashboard.html";
								}
							}	      	      
						]
					});
					$("#exitButton").button('option', 'label', 'Exit');

				},
				
				showJobColumns : function() {
					var myTable = $('#jobTable').DataTable();
					$.each(JOBLOOKUP.pacColumns, function(index, columnNumber) {
						myTable.columns(columnNumber).visible(false);
					});
					$.each(JOBLOOKUP.contactColumns, function(index, columnNumber) {
						myTable.columns(columnNumber).visible(false);
					});
					$.each(JOBLOOKUP.jobColumns, function(index, columnNumber) {
						myTable.columns(columnNumber).visible(true);
					});
				},
				
				showContactColumns : function() {
					var myTable = $('#jobTable').DataTable();
					$.each(JOBLOOKUP.pacColumns, function(index, columnNumber) {
						myTable.columns(columnNumber).visible(false);
					});
					$.each(JOBLOOKUP.contactColumns, function(index, columnNumber) {
						myTable.columns(columnNumber).visible(true);
					});
					$.each(JOBLOOKUP.jobColumns, function(index, columnNumber) {
						myTable.columns(columnNumber).visible(false);
					});
				},
				
				showPacColumns : function() {
					var myTable = $('#jobTable').DataTable();
					$.each(JOBLOOKUP.pacColumns, function(index, columnNumber) {
						myTable.columns(columnNumber).visible(true);
					});
					$.each(JOBLOOKUP.contactColumns, function(index, columnNumber) {
						myTable.columns(columnNumber).visible(false);
					});
					$.each(JOBLOOKUP.jobColumns, function(index, columnNumber) {
						myTable.columns(columnNumber).visible(false);
					});
				},
        	} 
        	
        	JOBLOOKUP.init();
        			
        });
        </script>        
    </tiles:put>
    
   <tiles:put name="content" type="string">
    	<h1>Job Lookup</h1>
    	    	
	 	<table id="jobTable" style="table-layout: fixed" class="display" cellspacing="0" style="font-size:9pt;max-width:1300px;width:1300px;">
	       	
	    </table>
	    
	    <webthing:scrolltop />
    
    	<div id="lookupModal">
    		<a href="jobLookup.html?type=JOB">Standard Job Lookup</a><br />
    		<br/>
    		<a href="jobLookup.html?type=PAC">Job Lookup With PAC</a><br />
    		<br />
    		<a href="jobLookup.html?type=CONTACT">Job Lookup with Contact</a><br />
    	</div>
    </tiles:put>
		
</tiles:insert>

