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
       			pacColumns : [1,2,3],
       			contactColumns : [17,18,19,20,21],
       			jobColumns : [0,1,13,14,15,16,21],
       			
       			
       			
       			
        		init : function() {
    				$.each($('input'), function () {
						$(this).css("height","20px");
						$(this).css("max-height", "20px");
					});
    					
    				$('.ScrollTop').click(function() {
    					$('html, body').animate({scrollTop: 0}, 800);
    	      	  		return false;
    	      	    });
    				
    			/*	var $filterJob = dataTable.columns( [0,1,13,14,15,16,21] ).data();
                	if ( $filterJob == 'yes' ) {
                		$filterIcon = "far fa-check-square";
                	} else {
                		$filterIcon = "fa fa-ban";
                	}*/
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
	        	        		text:'Contact Filter',
	        	        		action: function(e, dt, node, config) {
	        	        			if ( $filterJob == 'yes' ) {
	        	        				$filterJob = 'no';
	        	        			} else {
	        	        				$filterJob = 'yes';
	        	        			}
	        	        			var $url = "invoiceLookup.html?divisionId=" + $filterDivisionId + "&ppcFilter=" + $filterPPC;
	        	        			location.href=$url;
	        	        		}
	        	        	}
            	        ],
            	        "columnDefs": [
             	            { "orderable": false, "targets": -1 },
            	            { className: "dt-left", "targets": [4,5,6,11] },
            	            { className: "dt-center", "targets": [0,1,2,3,7,8,10,12,-1] },
            	            { className: "dt-right", "targets": [9]}
            	         ],
            	        "paging": true,
    			        "ajax": {
    			        	"url": "jobTable",
    			        	"type": "GET"
    			        	},
    			        columns: [
    			        	// if you change any of these, you'll need to change the jobColumns/pacColumns/contactColumns lists also
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
    			            { width: "4%", title: "<bean:message key="field.label.startDate" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.startDate != null){return (row.startDate+"");}
    			            } },
    			            { width: "3%", title: "<bean:message key="field.label.jobFrequency" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.jobFrequency != null){return (row.jobFrequency+"");}
    			            } },
    			            { width: "5%", title: "<bean:message key="field.label.pricePerCleaning" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.pricePerCleaning != null){return (row.pricePerCleaning+"");}
    			            } },
    			            { width: "3%", title: "<bean:message key="field.label.jobNbr" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) { 	
    			            	if(row.jobNbr != null){return (row.jobNbr+"");}
    			            } },
    			            { width: "24%", title: "<bean:message key="field.label.serviceDescription" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.serviceDescription != null){return (row.serviceDescription+"");}
    			            } },
    			            { width: "4%", title: "<bean:message key="field.label.poNumber" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.poNumber != null){return (row.poNumber+"");}	    
    			            } },	
    			            <%-- put those columns here --%>
    		        		{ visible:false, title: "<bean:message key="field.label.jobContact" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    	        			if(row.jobContact != null){
    	        				icon = JOBLOOKUP.makeContactIcon(row.jobContact);
    	        				return (icon + " " + row.jobContact.lastName+", "+row.jobContact.firstName);
    	        				}
	    	        		} },
    	        			{ visible:false, title: "<bean:message key="field.label.siteContact" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    	        			if(row.siteContact != null){
        	        			icon = JOBLOOKUP.makeContactIcon(row.siteContact);
        	        			return (icon + " " + row.siteContact.lastName+", "+row.siteContact.firstName);
        	        			}
    	        			} },
    	        			{ visible:false, title: "<bean:message key="field.label.contractContact" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
        	        			if(row.contractContact != null){
            	        			icon = JOBLOOKUP.makeContactIcon(row.contractContact);
            	        			return (icon + " " + row.contractContact.lastName+", "+row.contractContact.firstName);
            	        			}
        	        		} },
        	        		{ visible:false, title: "<bean:message key="field.label.billingContact" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
            	        		if(row.billingContact != null){
                	        		icon = JOBLOOKUP.makeContactIcon(row.billingContact);
                	        		return (icon + " " + row.billingContact.lastName+", "+row.billingContact.firstName);
                	        		}
            	        	} },
    	        			{ visible:false, title: "<bean:message key="field.label.contactId" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    	        				if(row.contactId != null){return (row.contactId+"");}
    	        			} },
    	        			{ visible:false, title: "<bean:message key="field.label.lastName" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    	        				if(row.lastName != null){return (row.lastName+"");}
    	        			} },
    	        			{ visible:false, title: "<bean:message key="field.label.firstName" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    	        				if(row.firstName != null){return (row.firstName+"");}
    	        			} },
    	        			{ visible:false, title: "<bean:message key="field.label.preferredContact" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.poNumber != null){return (row.poNumber+"");}	    
    			            } },	
    	        			{ visible:false, title: "<bean:message key="field.label.preferredContact" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
        	        			if(row.preferredContact != null){
            	        			icon = JOBLOOKUP.makeContactIcon(row.preferredContact);
            	        			return (icon + " " + row.preferredContact.contactMethod +"");
            	        			}
        	        		} },
    			            { width: "4%", title: "<bean:message key="field.label.action" />",  data: function ( row, type, set ) {	
    			            	//console.log(row);
    			            	{
    				            	var $edit = '<a href="jobMaintenance.html?id='+row.jobId+'" class="editAction" data-id="'+row.jobId+'"><webthing:edit>View</webthing:edit></a>';
    			            		return "<ansi:hasPermission permissionRequired='QUOTE_READ'>"+$edit+"</ansi:hasPermission>";
    			            		//return "<ansi:hasPermission permissionRequired='SYSADMIN'><ansi:hasWrite><a href='jobMaintenance.html?id="+row.jobId+"' class=\"editAction fas fa-pencil-alt\" data-id='"+row.jobId+"'></a></ansi:hasWrite></ansi:hasPermission>";
    			            	}
    			            	
    			            } }],
    			            "initComplete": function(settings, json) {
    			            	//console.log(json);
    			            	//JOBLOOKUP.doFunctionBinding();
    			            	$.each([13,14,15,16,17,18,19,20,21], function($idx, $value) {
    			            		
    			            	});
    			            },
    			            "drawCallback": function( settings ) {
    			            	//JOBLOOKUP.doFunctionBinding();
    			            }
    			    } );
            	},
            	
					<%--
//       			{ width: "4%", title: "<bean:message key="field.label.siteContact" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
//        			if(row.siteContact != null){return (row.siteContact+"");}
//        		} },
//        		{ width: "4%", title: "<bean:message key="field.label.contractContact" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
//        			if(row.contractContact != null){return (row.contractContact+"");}
//        		} },
//        		{ width: "4%", title: "<bean:message key="field.label.billingContact" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) { 	
//        			if(row.billingContact != null){return (row.billingContact+"");}
//        		} },
//        		{ width: "4%", title: "<bean:message key="field.label.contactId" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
//        			if(row.contactId != null){return (row.contactId+"");}
//        		} },
//        		{ width: "4%", title: "<bean:message key="field.label.lastName" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
//        			if(row.lastName != null){return (row.lastName+"");}
//        		} },
//        		{ width: "4%", title: "<bean:message key="field.label.firstName" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
//        			if(row.firstName != null){return (row.firstName+"");}
//        		} },
//        		{ width: "4%", title: "<bean:message key="field.label.preferredContact" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) { 	
//        			if(row.preferredContact != null){return (row.preferredContact+"");}
//        		} },  
            	--%>
            	
				doFunctionBinding : function() {
					if ( JOBTABLE.lookupType == 'JOB' ) {
						JOBLOOKUP.showJobColumns();
					}
					if ( JOBTABLE.lookupType == 'PAC' ) {
						JOBLOOKUP.showPACColumns();
					}
					if ( JOBTABLE.lookupType == 'CONTACTS' ) {
						JOBLOOKUP.showContactColumns();
					}
				},
				
				
		/*		displayModal : function () {
					$(".displayModal").click(function($event) {
						$('#lookupModal').data("permissionGroupId",null);
		        		$('#lookupModal').button('option', 'label', 'Search');
		        		$('#exitButton').button('option', 'label', 'Exit to Dashboard');
		        		
			       		$("#editPanel display[name='']").val("");
						$("#lookupModal input[name='JOB']").val("");
						$("#lookupModal input[name='PAC']").val("");
						$("#lookupModal input[name='CONTACT']").val("");			        		
		        		$("#lookupModal .err").html("");
		        		$("#lookupModal").dialog("option","title", "Select Lookup Screen to View").dialog("open");
					});
				}, */
				
				
				
				makeContactIcon : function(contact) {
					<%--
						Steal this from quote maintenance.jsp
						<webthing:phone>BUsiness Phone</webthing:phone> 
					--%>
				/*	if (contact.preferredContact=="business_phone")  {
						icon="fa fa-phone tooltip";
					}	else {
						icon="y";
					}
					return icon;*/
					

					
			    	
				  	if (contact.preferredContact=="mobile_phone")  {
			    		icon = "<i class='fa fa-mobile' aria-hidden='true'></i>&nbsp;";
			    	} else if(contact.preferredContact =="email"){
			    		icon = "<i class='fa fa-envelope-o' aria-hidden='true'></i>&nbsp";
			    	} else if(contact.preferredContact == "business_phone"){
			    		icon = "<i class='fa fa-phone' aria-hidden='true'></i>&nbsp;";
			    	} else if(contact.preferredContact == "fax"){
			    		icon = "<i class='fa fa-fax' aria-hidden='true'></i>&nbsp;";
			    	}{
						icon="y";
					}
					return icon;
					
			    	
			    	

					
				/*	populateContactPanel : function($selector, $data) {
						$($selector + " .ansi-contact-name").html($data.firstName + " " + $data.lastName);						
						$($selector + " .ansi-contact-number").html($data.method);
						$($selector + " .ansi-contact-method-is-business-phone").hide();
						$($selector + " .ansi-contact-method-is-mobile-phone").hide();
						$($selector + " .ansi-contact-method-is-fax").hide();
						$($selector + " .ansi-contact-method-is-email").hide();
						if ( $contact.preferredContact == "business_phone") { $($selector + " .ansi-contact-method-is-business-phone").show(); }
						if ( $data.preferredContact == "mobile_phone") { $($selector + " .ansi-contact-method-is-mobile-phone").show(); }
						if ( $data.preferredContact == "fax") { $($selector + " .ansi-contact-method-is-fax").show(); }
						if ( $data.preferredContact == "email") { $($selector + " .ansi-contact-method-is-email").show(); }
					},
					
					
					
					
					var $preferred = ui.item.preferredContactValue.split(":");
			    	$($contactSelector + " .ansi-contact-number").html($preferred[1]);
			    	
			    	$($contactSelector + " .ansi-contact-method-is-business-phone").hide();
					$($contactSelector + " .ansi-contact-method-is-mobile-phone").hide();
					$($contactSelector + " .ansi-contact-method-is-fax").hide();
					$($contactSelector + " .ansi-contact-method-is-email").hide();
					if ( $preferred[0] == "business_phone") { $($contactSelector + " .ansi-contact-method-is-business-phone").show(); }
					if ( $preferred[0] == "mobile_phone") { $($contactSelector + " .ansi-contact-method-is-mobile-phone").show(); }
					if ( $preferred[0] == "fax") { $($contactSelector + " .ansi-contact-method-is-fax").show(); }
					if ( $preferred[0] == "email") { $($contactSelector + " .ansi-contact-method-is-email").show(); }
					$($contactSelector).show();
					if (contact.preferredContact=="business_phone")  {
						icon="fa fa-phone tooltip";
					}	else {
						icon="y";
					}
					return icon; */
					
					
					
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
					$.each(pacColumns, function(index, columnNumber) {
						myTable.columns(columnNumber).visible(false);
					});
					$.each(contactColumns, function(index, columnNumber) {
						myTable.columns(columnNumber).visible(false);
					});
					$.each(jobColumns, function(index, columnNumber) {
						myTable.columns(columnNumber).visible(true);
					});
				}
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

