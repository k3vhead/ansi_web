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
       	<link rel="stylesheet" href="css/callNote.css" />
    	<link rel="stylesheet" href="css/accordion.css" type="text/css" />
    	<link rel="stylesheet" href="css/lookup.css" />
       	<script type="text/javascript" src="js/ansi_utils.js"></script>
    	<script type="text/javascript" src="js/lookup.js"></script> 
    	<script type="text/javascript" src="js/callNote.js"></script>  
    
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
			#filter-container {
        		width:402px;
        		float:right;
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
       			pacColumns : [17,18,19,20,21],
       			contactColumns : [13,14,15,16],
       			jobColumns : [11,12],
       			
       			
       			
       			
        		init : function() {
        			CALLNOTE.init();
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
            	            { className: "dt-left", "targets": [4,5,6,11,21] },
            	            { className: "dt-center", "targets": [0,1,2,3,7,8,10,12,17,18,19,20,-1] },
            	            { className: "dt-right", "targets": [9]}
            	         ],
            	        "paging": true,
    			        "ajax": {
    			        	"url": "jobTable",
    			        	"type": "GET"
    			        	},
    			        columns: [
    			        	{ width: "4%", title: "<bean:message key="field.label.jobId" />", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {	
    			            	if(row.job_id != null){return (row.job_id+"");}
    			            } },
    			            { width: "4%", title: "<bean:message key="field.label.quoteName" />", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            	if(row.quote_nbr != null){return ('<ansi:hasPermission permissionRequired="QUOTE"><a href="quoteMaintenance.html?id='+ row.quote_id+ '" style="color:#404040"></ansi:hasPermission>' + row.quote_nbr +'<ansi:hasPermission permissionRequired="QUOTE"></ansi:hasPermission>');}
    			            } },
    			            { width: "3%", title: "<bean:message key="field.label.jobStatus" />", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            	if(row.job_status != null){return (row.job_status+"");}
    			            } },
    			            { width: "4%", title: "<bean:message key="field.label.divisionNbr" />", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            	if(row.div != null){return (row.div);}
    			            } },
    			            { width: "10%", title: "<bean:message key="field.label.billToName" />" , "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {	
    			            	if(row.bill_to_name != null){return (row.bill_to_name+"");}
    			            } },
    			            { width: "10%", title: "<bean:message key="field.label.jobSiteName" />", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            	if(row.job_site_name != null){return (row.job_site_name+"");}
    			            } },
    			            { width: "10%", title: "<bean:message key="field.label.jobSiteAddress" />",  "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            	if(row.job_site != null){return (row.job_site);}
    			            } },
    			            { width: "5%", title: "<bean:message key="field.label.startDate" />", "defaultContent": "<i>N/A</i>", searchable:true, searchFormat: "YYYY-MM-dd", data: function ( row, type, set ) {
    			            	if(row.start_date != null){return (row.start_date+"");}
    			            } },
    			            { width: "3%", title: "<bean:message key="field.label.jobFrequency" />", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            	if(row.job_frequency != null){return (row.job_frequency+"");}
    			            } },
    			            { width: "5%", title: "<bean:message key="field.label.pricePerCleaning" />", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            	if(row.price_per_cleaning != null){return (row.price_per_cleaning+"");}
    			            } },
    			            { width: "4%", title: "<bean:message key="field.label.jobNbr" />", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) { 	
    			            	if(row.job_nbr != null){return (row.job_nbr+"");}
    			            } },
    			            { width: "24%", title: "<bean:message key="field.label.serviceDescription" />", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            	if(row.service_description != null){return (row.service_description+"");}
    			            } },
    			            { width: "4%", title: "<bean:message key="field.label.poNumber" />", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            	if(row.po_number != null){return (row.po_number+"");}	    
    			            } },	
    		        		{ width: "9%", title: "<bean:message key="field.label.jobContact" />", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    	        			if(row.job_contact != null){
    	        				var preferredContact = row.job_contact_preferred_contact;
    	        				icon = JOBLOOKUP.makeContactIcon(preferredContact);    	        				
    	        				return (row.job_contact + "<br />" + icon + row.job_contact_method);
    	        				}
	    	        		} },
    	        			{ width: "9%", title: "<bean:message key="field.label.siteContact" />", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    	        			if(row.site_contact!= null){
    	        				var preferredContact = row.site_contact_preferred_contact;
        	        			icon = JOBLOOKUP.makeContactIcon(preferredContact);
        	        			return (row.site_contact + "<br />" + icon + row.site_contact_method);
        	        			}
    	    	        	} },
    	        			{ width: "9%", title: "<bean:message key="field.label.contractContact" />", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    	        			if(row.contract_contact != null){
    	        				var preferredContact = row.contract_contact_preferred_contact;
        	        			icon = JOBLOOKUP.makeContactIcon(preferredContact);
        	        			return (row.contract_contact + "<br />" + icon + row.contract_contact_method);
        	        			}
    	    	        	} },
        	        		{ width: "9%", title: "<bean:message key="field.label.billingContact" />", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
        	        		if(row.billing_contact != null){
    	        				var preferredContact = row.billing_contact_preferred_contact;
        	        			icon = JOBLOOKUP.makeContactIcon(preferredContact);
        	        			return (row.billing_contact + "<br />" + icon + row.billing_contact_method);
        	        			
        	        			}
    	    	        	} },
    	    	        	{ width: "5%", title: "Proposed", "defaultContent": "<i>N/A</i>", searchable:true, searchFormat: "YYYY-MM-dd", data: function ( row, type, set ) {
    	    	        		if(row.proposal_date != null){return (row.proposal_date+"");}	    
    			            } },
    			            { width: "5%", title: "Active", "defaultContent": "<i>N/A</i>", searchable:true, searchFormat: "YYYY-MM-dd", data: function ( row, type, set ) {
    			            	if(row.activation_date != null){return (row.activation_date+"");}	    
    			            } },
    			            { width: "5%", title: "Cancel", "defaultContent": "<i>N/A</i>", searchable:true, searchFormat: "YYYY-MM-dd", data: function ( row, type, set ) {
    			            	if(row.cancel_date != null){return (row.cancel_date+"");}	    
    			            } },
    			            { width: "5%", title: "Reason", "defaultContent": "<i>N/A</i>", searchable:true,  data: function ( row, type, set ) {
    			            	if(row.cancel_reason != null){return (row.cancel_reason+"");}	    
    			            } },
    			            { width: "5%", title: "Tags", "defaultContent": "<i></i>", searchable:true,  data: function ( row, type, set ) {
    			            	if(row.tag_list != null){return (row.tag_list+"");}	    
    			            } },
    			            { width: "4%", title: "<bean:message key="field.label.action" />",  data: function ( row, type, set ) {	
    			            	{
    				            	var $edit = '<a href="jobMaintenance.html?id='+row.job_id+'" class="editAction" data-id="'+row.job_id+'"><webthing:edit>View</webthing:edit></a>';
    				            	var $noteLink = '<webthing:notes xrefType="QUOTE" xrefId="' + row.quote_id + '">Quote Notes</webthing:notes>'
    			            		return "<ansi:hasPermission permissionRequired='QUOTE_READ'>"+$edit+"</ansi:hasPermission>" + $noteLink;
    			            	}
    			            	
    			            } }],
    			            "initComplete": function(settings, json) {
    			            	JOBLOOKUP.doFunctionBinding();
    			            	var myTable = this;
    			            	LOOKUPUTILS.makeFilters(myTable, "#filter-container", "#jobTable", JOBLOOKUP.createTable);
    			            },
    			            "drawCallback": function( settings ) {
    			            	CALLNOTE.lookupLink();
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
    	    	
    	<webthing:lookupFilter filterContainer="filter-container" />
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
    	
    	<webthing:callNoteModals />
    </tiles:put>
		
</tiles:insert>

