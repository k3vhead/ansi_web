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
        <bean:message key="page.label.ticket.assignment" />
    </tiles:put>
    
    
    <tiles:put name="headextra" type="string">
       	<link rel="stylesheet" href="css/lookup.css" />
    	<link rel="stylesheet" href="css/ticket.css" />
    	<script type="text/javascript" src="js/ansi_utils.js"></script>
    	<script type="text/javascript" src="js/addressUtils.js"></script>
    	<script type="text/javascript" src="js/lookup.js"></script> 
    	<script type="text/javascript" src="js/claims.js"></script> 
    	<script type="text/javascript" src="js/ticket.js"></script> 
    
        <style type="text/css">
        	#action-container {
           		width:49%;
        		float:right; 
        	}        
        	#action-button-container {
        		width:100%;
        		text-align:center;
        		display:none;
        	}
        	<%--
        	#column-container-a {	
        		display:none;
        	}
        	--%>
        	#column-container-b { 
        		 width:66%;
        		 float:left; 
        	}
        	#action-list-container {
				width:100%;
        	}
        	#division-description {
        		text-align:center;
        		font-weight:bold;
        	}
        	#div-selector-container {
        		margin-bottom:8px;
        	}
        	#loading-container {
        		width:100%;
        		border:solid 1px #000000;
        		display:none;
        	}
        	#message-button-container {
        		width:100%;
        		text-align:center;
        		display:none;
        	}
        	#ticket-list-container {
        		width:49%; 
        		float:left; 
        	}
        	#ticket-table {
				width:100%;
			}
			
        	#ticket-modal {
				display:none;	
			}
        	#user-list-container {
				width:33%;
				float:right;
			}
			#user-list-table {
				width:100%;
			}
			.action-button {
				cursor:pointer;
			}
			.search-button {
				color:#000000;
				text-decoration:none;
			}
        	.ticket {
        		border:solid 1px #404040;
        		width:90%;
        	}
			.ticket-clicker {
				color:#000000;
			}
			.ui-droppable-hover {
				background-color:#CC6600;
			}
        	.user {

        	}
        </style>
        
        <script type="text/javascript">
        
        $(document).ready(function() {
        	;TICKETASSIGNMENT = {
        		divisionMap : {},
        		division : null,
        		usersLoaded : true,
        		ticketsLoaded : true,
        		userTable : null,
        		ticketTable : null,
        		selectedTicketList : [],
        		selectedUserList : {},
        		
        		divisionFilter : '<c:out value="${ANSI_DIVISION_ID}" />',	// col 1
				jobFilter : '<c:out value="${ANSI_JOB_ID}" />', 			// col 7
				ticketFilter : '<c:out value="${ANSI_TICKET_ID}" />',   	//col 0
				washerFilter : '<c:out value="${ANSI_WASHER_ID}" />',		//col 9

        		
        		
        		init : function() {
        			ANSI_UTILS.makeDivisionList(TICKETASSIGNMENT.makeDivListSuccess, TICKETASSIGNMENT.makeDivListFailure);
        			TICKETUTILS.makeTicketViewModal("#ticket-modal");
        			TICKETASSIGNMENT.makeModals();
        			TICKETASSIGNMENT.makeClickers();
        			TICKETASSIGNMENT.loadTickets();
        			TICKETASSIGNMENT.loadUsers();
        		},

        		
        		
        		cancelAssignments : function() {
        			console.log("cancelAssignments");
        			TICKETASSIGNMENT.selectedTicketList = [];
        			TICKETASSIGNMENT.selectedUserList = {};
        			$("#action-button-container").hide();
        			//$("#ticket-table").DataTable().ajax.reload();
        			//$("#user-list-table").DataTable().ajax.reload();
        			$(".user-selector").prop("checked", false);
        			$(".ticket-selector").prop("checked", false);
        		},
        		
        		
        		
        		displayActionButtons : function() {
        			console.log(TICKETASSIGNMENT.selectedTicketList);
        			console.log(TICKETASSIGNMENT.selectedUserList);
        			if ( TICKETASSIGNMENT.selectedTicketList.length > 0 && Object.keys(TICKETASSIGNMENT.selectedUserList).length > 0 ) {
        				console.log("Showing buttons");
       					$("#action-button-container").show();
       				} else {
       					$("#action-button-container").hide();
        			}
        		},
        		
        		
        		
        		<%--
        		displayPanels : function() {
        			console.log("Display panels " + TICKETASSIGNMENT.usersLoaded + " " + TICKETASSIGNMENT.ticketsLoaded);
        			if ( TICKETASSIGNMENT.usersLoaded == true && TICKETASSIGNMENT.ticketsLoaded == true) {
    					$("#loading-container").hide();
        				$("#column-container-a").show();
        			} else {
        				setTimeout(function() {
	            			TICKETASSIGNMENT.displayPanels();
	            		},250);
        			}
        		},
        		--%>
        		
        		
        		loadTickets : function() {
        			displayDivisionId = null;
        			if ( TICKETASSIGNMENT.division != null ) {
        				displayDivisionId = TICKETASSIGNMENT.division.divisionId;
        			}
        			console.log("Loading tickets for " + displayDivisionId);
        			TICKETASSIGNMENT.ticketTable = $('#ticket-table').DataTable( {
            			"aaSorting":		[[0,'desc']],
            			"processing": 		true,
            	        "serverSide": 		true,
            	        "autoWidth": 		false,
            	        "deferRender": 		true,
            	        "scrollCollapse": 	true,
            	        "scrollX": 			true,
            	        rowId: 				'dt_RowId',
            	        dom: 				'Bfrtip',
            	        "searching": 		false,
            	        "searchDelay":		800,
            	        lengthMenu: [],
            	        buttons: [],
            	        "columnDefs": [
             	            { "orderable": false, "targets": -1 },
							{ className: "dt-left", "targets": [4,5,6,12] },
	           	            { className: "dt-center", "targets": [0,1,2,3,7,8,10,11,13,14] },
           	            	{ className: "dt-right", "targets": [9,15]}
            	         ],
             	        "pageLength":		50,
            	        "paging": true,
    			        "ajax": {
    			        	"url": "ticketTable",
    			        	"type": "GET",
    			        	"data": {"jobId":null,"divisionId":displayDivisionId,"startDate":null,"status":"D"}
    			        	},
    			        columns: [
    			        { 	searchable:true,
    			        	width:"5%", 
    			        	title: "<bean:message key="field.label.ticketId" />", 
    			        	"defaultContent": "<i>N/A</i>", 
    			        	searchable:true, 
    			        	data: function ( row, type, set ) {	
			            		if(row.ticket_id != null){return ('<a href="#" data-id="'+row.ticket_id+'" class="ticket-clicker">'+row.ticket_id+'</a>');}
			            	} 
    			        },
			            { searchable:false, visible:false, width:"5%", title: "<bean:message key="field.label.ticketStatus" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.ticket_status != null){return ('<span class="tooltip">' + row.ticket_status+'<span class="tooltiptext">'+row.ticket_status_desc+'</span></span>');}
			            } },
			            { searchable:false, visible:false, width:"5%", title: "<bean:message key="field.label.ticketType" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.ticket_type_desc != null){return (row.ticket_type_desc+"");}
			            } },
			            { searchable:false, visible:false, width:"4%", title: "<bean:message key="field.label.divisionNbr" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.div != null){return (row.div);}
			            } },
			            { searchable:true, visible:false, width:"8%", title: "<bean:message key="field.label.billToName" />" , "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {	
			            	if(row.bill_to_name != null){return (row.bill_to_name+"");}
			            } },
			            { searchable:true,width:"8%", title: "<bean:message key="field.label.jobSiteName" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.job_site_name != null){return (row.job_site_name+"");}
			            } },
			            { searchable:true,visible:false, width:"7%", title: "<bean:message key="field.label.jobSiteAddress" />",  "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.job_site_address != null){return (row.job_site_address+"");}
			            } },
			            { searchable:true,width:"6%", title: "<bean:message key="field.label.startDate" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.display_start_date != null){return (row.display_start_date+"");}
			            } },
			            { searchable:true,width:"5%", title: "<bean:message key="field.label.jobFrequency" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.job_frequency != null){return (row.job_frequency+"");}
			            } },
			            { searchable:false, visible:false, width:"5%", title: "<bean:message key="field.label.pricePerCleaning" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.price_per_cleaning != null){return (row.price_per_cleaning+"");}
			            } },
			            { searchable:false, visible:false, width:"5%", title: "<bean:message key="field.label.jobNbr" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) { 	
			            	if(row.job_nbr != null){return (row.job_nbr+"");}
			            } },
			            { searchable:true,visible:false, width:"4%", title: "<bean:message key="field.label.jobId" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {	
			            	if(row.job_id != null){return ('<ansi:hasPermission permissionRequired="QUOTE_READ"><a href="jobMaintenance.html?id='+ row.job_id +'" class="jobLink"></ansi:hasPermission>'+row.job_id+'<ansi:hasPermission permissionRequired="QUOTE_READ"></a></ansi:hasPermission>');}
			            } },
			            { searchable:true,visible:false, width:"16%", title: "<bean:message key="field.label.serviceDescription" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.service_description != null){return (row.service_description+"");}
			            } },
			            { searchable:false, visible:false, width:"6%", title: "<bean:message key="field.label.processDate" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) { 	
			            	if(row.process_date != null){return (row.process_date+"");}
			            } },
			            { searchable:false, visible:false, width:"6%", title: "<bean:message key="field.label.invoiceId" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {	
			            	if(row.invoice_id != null){return (row.invoice_id+"");} 
			            } },
			            { searchable:false, visible:false, width:"6%", title: "<bean:message key="field.label.amountDue" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {	
			            	if(row.amount_due != null){return (row.amount_due+"");} 
			            } },
			            { width:"5%", title: "Select",  data: function ( row, type, set ) {	
			            	var $search = '<a href="ticketAssignmentLookup.html?ticketId='+row.ticket_id+'" class="search-button"><webthing:view styleClass="action-button">Search</webthing:view></a>';
			            	var $checkbox = '<input type="checkbox" class="ticket-selector" data-id="'+row.ticket_id+'" id="ticket-selector-'+row.ticket_id+'">';
			            	return $search + " " + $checkbox;
			            } }
			            ],
    			            "initComplete": function(settings, json) {
    			            	var myTable = this;
       			            	LOOKUPUTILS.makeFilters(myTable, "#filter-container", "#ticket-table", TICKETASSIGNMENT.loadTickets, TICKETASSIGNMENT.loadTicketComplete);    			            		
    			            },
    			            "fnRowCallback": function(nRow, aData, iDisplayIndex) {
    							$(nRow).addClass("ticket");
    						},
    			            "drawCallback": function( settings ) {
    			            	TICKETASSIGNMENT.ticketDrawCallback();
    			            }
    			    } );
        		},
        		
        		
        		loadTicketComplete : function() {
        			console.log("loadTicketComplete");
        			//setTimeout(function() {
            			if ( TICKETASSIGNMENT.isFiltered()) {
            				$("#searching-modal").dialog("open");
		            		TICKETASSIGNMENT.doTableFilter(0, TICKETASSIGNMENT.ticketFilter);
		            		TICKETASSIGNMENT.doTableFilter(7, TICKETASSIGNMENT.jobFilter);
		            		TICKETASSIGNMENT.doTableFilter(8, TICKETASSIGNMENT.washerFilter);
		            		//clear filters so we don't reuse them 
		            		TICKETASSIGNMENT.ticketFilter = null;
		            		TICKETASSIGNMENT.jobFilter = null;
		            		TICKETASSIGNMENT.washerFilter = null;
            			}
            		//},100)
	            	//$("#filter-container .filter-banner .filter-hider .filter-data-closed").click(); //open the filter container inside the modal
	            	TICKETASSIGNMENT.ticketsLoaded = true; 
	            	//TICKETASSIGNMENT.displayPanels();
        		},
        		
        		
        		
        		
        		loadUsers : function() {
        			displayDivisionId = "";
        			if ( TICKETASSIGNMENT.division != null ) {
        				displayDivisionId = TICKETASSIGNMENT.division.divisionId;
        			}
        			console.log("Loading users for " + displayDivisionId);
        			var $url = "userDivision/" + displayDivisionId;
        				
            		TICKETASSIGNMENT.userTable = $('#user-list-table').DataTable( {
            			"aaSorting":		[[0,'asc']],
            			"processing": 		true,
            	        "serverSide": 		true,
            	        "autoWidth": 		false,
            	        "deferRender": 		true,
            	        "scrollCollapse": 	true,
            	        "scrollX": 			true,
            	        rowId: 				'dt_RowId',
            	        dom: 				'Bfrtip',
            	        "searching": 		false,
            	        "searchDelay":		800,
            	        "pageLength":		100,
            	        lengthMenu: [],
            	        buttons: [],
            	        "columnDefs": [
             	            { "orderable": false, "targets": -1 },
            	            { className: "dt-left", "targets": [0] },
            	            { className: "dt-center", "targets": [1] },
            	            { className: "dt-right", "targets": []},
            	         ],
            	        "paging": true,
    			        "ajax": {
    			        	"url": $url,
    			        	"type": "GET"
    			        	},
    			        	columns: [
        			        	{ title: "Washer", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {	
        			            	if(row.user_id != null){return row.last_name+', '+row.first_name;}
        			            } },
        			            { title: "Select", "defaultContent":"<i>N/A</i>", searchable:false, data: function(row, type, set) {
        			            	var $search = '<a href="ticketAssignmentLookup.html?washerId='+row.last_name+', '+row.first_name+'" class="search-button"><webthing:view styleClass="action-button">Search</webthing:view></a>';
        			            	var $checkbox = '<input type="checkbox" class="user-selector" data-id="'+row.user_id+'" data-first="'+row.first_name+'" data-last="'+row.last_name+'" />';
        			            	return $search + " " + $checkbox;
        			            }}
    							],
    						"fnRowCallback": function(nRow, aData, iDisplayIndex) {
    							$(nRow).addClass("user");
    						},
    			            "initComplete": function() { 
    			            	TICKETASSIGNMENT.usersLoaded = true; 
    			            	//TICKETASSIGNMENT.displayPanels();
    			            },    			            
    			            "drawCallback": function() {
    			            	TICKETASSIGNMENT.userDrawCallback();
    			            },
    			    } );
        		},
        		
        		
        		
        		makeAssignment : function($ticketId, $userId, $firstName, $lastName) {
        			var $msg = $("<div>");
        			var $className = "assignment-" + $ticketId + "-" + $userId;
        			$msg.addClass($className);
        			$msg.append("Assigning ticket " + $ticketId + " to " + $firstName + " " + $lastName);
        			$("#action-list-container").append($msg);
        			$("." + $className).fadeOut(6000);
        			
        			
        			
        			$outbound = {"washerId":$userId, "ticketId":$ticketId};
        			var jqxhr = $.ajax({
						type: 'POST',
						url: "ticket/ticketAssignment",
						data: JSON.stringify($outbound),
						statusCode: {
							200: function($data) {
								if ( $data.responseHeader.responseCode == 'SUCCESS') {
									var $responseText = $data.data.webMessages['GLOBAL_MESSAGE'][0];
									$msg.append(' - <span class="err">' + $responseText + "</span>");
									$("." + $className).fadeOut(6000);
								} else if ( $data.responseHeader.responseCode == 'EDIT_FAILURE') {
									$msg.append(" - Failed");
								} else {
									$("#globalMsg").html("System Error, Invalid response code "+$data.responseHeader.responseCode+": Contact Support").show();
								}
							},
							403: function($data) {
								$("#globalMsg").html("Session Timeout. Log in and try again").show();
							}, 
							404: function($data) {
								$("#globalMsg").html("System Error 404: Contact Support").show();
							}, 
							405: function($data) {
								$("#globalMsg").html("System Error 405: Contact Support").show();
							}, 
							500: function($data) {
								$("#globalMsg").html("System Error 500: Contact Support").show();
							} 
						},
						dataType: 'json'
					});
        		},
        		
        		
        		
        		makeAssignments : function() {
        			console.log("makeAssignments");
        			$.each(TICKETASSIGNMENT.selectedTicketList, function($tktIdx, $ticket){
        				$.each(TICKETASSIGNMENT.selectedUserList, function($userIdx, $user){
        					console.log("Assigning " + $ticket + " to " + $user["userId"]);
        					TICKETASSIGNMENT.makeAssignment($ticket, $user.userId, $user["first"], $user["last"]);
        				});
        			});
        			$("#cancel-button").click();
        		},
        		
        		
        		
        		makeAssignmentTable : function($ticketId, $userId) {
        			console.log("Search for ticket: " + $ticketId + " user: " + $userId)
        		},
        		
        		
        		
        		makeClickers : function() {
        			$("#save-button").click(TICKETASSIGNMENT.makeAssignments);
        			$("#cancel-button").click(TICKETASSIGNMENT.cancelAssignments);
        		},
        		
        		

        		makeDivListFailure : function($data) {
					$("#globalMsg").html("Failed to load Division List. Contact Support");
					$("#column-container-a").hide();
					$("#loading-container").hide();
	       		},

	       		
	       		
        		makeDivListSuccess : function($data) {
   			 		console.log("Div List Success");
   			 		var $select = $("select[name='divisionId']");
					$('option', $select).remove();

					$select.append(new Option("",""));
					$.each($data.data.divisionList, function(index, val) {
    					$select.append(new Option(val.divisionNbr + "-" + val.divisionCode, val.divisionId));
    					TICKETASSIGNMENT.divisionMap[val.divisionId] = val;
					});
					
					$select.change(function() {
						TICKETASSIGNMENT.processDivChange();						
					});
					
					if ( TICKETASSIGNMENT.divisionFilter != null && TICKETASSIGNMENT.divisionFilter != '') {
						$select.val(TICKETASSIGNMENT.divisionFilter);
						TICKETASSIGNMENT.divisionFilter = null;
						TICKETASSIGNMENT.processDivChange();
					}
        		},
        		
        		
        		
        		
        		makeModals : function() {
        			console.log("no modals");
        		},
        		
        		
        		
        		processDivChange : function() {
        			var $selectedDiv = TICKETASSIGNMENT.divisionMap[$("select[name='divisionId']").val()];
					$("#division-description").html( $selectedDiv.description + " (" +  $selectedDiv.divisionNbr + "-" + $selectedDiv.divisionCode + ")");
					//$("#column-container-a").hide();
					$("#action-button-container").hide();
					//$("#loading-container").show();
					TICKETASSIGNMENT.usersLoaded = false;
					TICKETASSIGNMENT.ticketsLoaded = false;
					if ( TICKETASSIGNMENT.userTable != null ) {
						TICKETASSIGNMENT.userTable.destroy();
					}
					if ( TICKETASSIGNMENT.ticketTable != null ) {
						TICKETASSIGNMENT.ticketTable.destroy();
					}
					// we do it this way so the load methods can be called later without knowing the division
					TICKETASSIGNMENT.division = $selectedDiv;
					TICKETASSIGNMENT.loadUsers();
					TICKETASSIGNMENT.loadTickets();
        		},
        		
        		
        		
        		ticketDrawCallback : function() {
        			$(".ticket-selector").on("click", function($clickevent){
        				var $ticketId = $(this).attr("data-id");
        				if ( $(this).prop('checked') == true ) {
	        				console.log("Add ticket: " + $ticketId);
        					TICKETASSIGNMENT.selectedTicketList.push($ticketId);
        				} else {
	        				console.log("Drop ticket: " + $ticketId);
	        				ANSI_UTILS.removeFromArray(TICKETASSIGNMENT.selectedTicketList, $ticketId);
        				}
        				TICKETASSIGNMENT.displayActionButtons();
        			});
        			
        			
        			$(".ticket-clicker").on("click", function($clickevent) {
    					$clickevent.preventDefault();
    					var $ticketId = $(this).attr("data-id");
    					TICKETUTILS.doTicketViewModal("#ticket-modal",$ticketId);
    					$("#ticket-modal").dialog("open");
    				});
        			
        			$(".search-by-ticket").click(function($clickevent) {
        				var $ticketId = $(this).attr("data-id");
        				TICKETASSIGNMENT.makeAssignmentTable($ticketId, null);
        			});
        		},
        		
        		
        		
        		
        		userDrawCallback : function() {
        			$(".user-selector").on("click", function($clickevent){
        				var $userId = $(this).attr("data-id");
        				if ( $(this).prop('checked') == true ) {
        					console.log("Add user: " + $userId);
        					var $user = {
        						"userId":$(this).attr("data-id"),
        						"first":$(this).attr("data-first"),
        						"last":$(this).attr("data-last"),
        					};
        					TICKETASSIGNMENT.selectedUserList[$userId]=$user;
        				} else {
	        				console.log("Drop ticket: " + $userId);
	        				delete TICKETASSIGNMENT.selectedUserList[$userId];
        				}
        				TICKETASSIGNMENT.displayActionButtons();
        			});
        			
        			
        			$(".search-by-user").click(function($clicevent) {
        				var $userId = $(this).attr("data-id");
        				TICKETASSIGNMENT.makeAssignmentTable(null, $userId);
        			});
        		},
        		
        		
        		
        		
        		userDrop : function($event, $ui) {
        			console.log("I dropped on a ticket");
        			var $ticketId = $ui.helper.attr("data-id");
        			var $userId = $(this).attr("data-id");
        			var $firstName = $(this).attr("data-first");
        			var $lastName = $(this).attr("data-last");
        			TICKETASSIGNMENT.makeAssignment($ticketId, $userId, $firstName, $lastName);
        		},
        		
        		
        		
        		ticketDrop: function($event, $ui) {
        			console.log("I dropped on a user");
        			var $userId = $ui.helper.attr("data-id");
        			var $ticketId = $(this).attr("data-id");
        			var $firstName = $ui.helper.attr("data-first");
        			var $lastName = $ui.helper.attr("data-last");
        			TICKETASSIGNMENT.makeAssignment($ticketId, $userId, $firstName, $lastName);
        		},
        		
        		
        		
        		
        		
        		doTableFilter : function($colNbr, $filterValue) {
        			console.log("doTableFilter");
	            	if ( $filterValue != null &&  $filterValue !='' ) {
	            		console.log("Setting  filter");
	        			console.log("filtering for : " + $filterValue);
	            		LOOKUPUTILS.setFilterValue("#filter-container", $colNbr, $filterValue); //set value in filters
	        			var dataTable = $('#ticket-assignment-table').DataTable();
	    				myColumn = dataTable.columns($colNbr);
	   					myColumn.search($filterValue).draw();
	            	}
    			},
    			
    			
    			
    			isFiltered : function() {
    				console.log("isFiltered");
    				var $isFiltered = false;
    				
    				if (TICKETASSIGNMENT.jobFilter != null && TICKETASSIGNMENT.jobFilter != '') {
    					console.log("by ticket");
    					$isFiltered = true;
    				} else if (TICKETASSIGNMENT.ticketFilter != null && TICKETASSIGNMENT.ticketFilter != '') {
    					$isFiltered = true;
    				} else if (TICKETASSIGNMENT.washerFilter != null && TICKETASSIGNMENT.washerFilter != '') {
    					$isFiltered = true;
    				}
    				console.log("isFiltered: " + $isFiltered);
    				return $isFiltered;
    			},
        	},
        	
        	TICKETASSIGNMENT.init();

        });
        </script>        
    </tiles:put>
    
   <tiles:put name="content" type="string">
    	<h1><bean:message key="page.label.ticket.assignment" /></h1>
    	
    	
    	
    	<%--
    	 -----------------------------------------------------------------------------------------------------
    	 | column-container-a                                                                                |
		 | ---------------------------------------------------------------  -------------------------------- |
		 | | column-container-b                                          |  | user-list-container          | |
		 | | ------------------------- --------------------------------  |  | --------------------------   | |
		 | | | ticket-list-container | | action-container             |  |  | | user-list-table        |   | |	
    	 | | | ------------------    | | ---------------------------  |  |  | |                        |   | |
    	 | | | | ticket-table   |    | | | action-button-container |  |  |  | |                        |   | |
    	 | | | |                |    | | ---------------------------  |  |  | |                        |   | |
    	 | | | |                |    | | ---------------------------  |  |  | |                        |   | |
    	 | | | |                |    | | | action-list-container   |  |  |  | |                        |   | |
    	 | | | ------------------    | | ---------------------------  |  |  | --------------------------   | |
    	 | | ------------------------- --------------------------------  |  |                              | |
    	 | ---------------------------------------------------------------  -------------------------------- |
    	 -----------------------------------------------------------------------------------------------------
    	 --%>
    	<div id="div-selector-container">
    		<span class="formLabel">Division:</span>&nbsp;<select name="divisionId"></select>
    	</div>
    	<div id="division-description"></div>
    	<div id="loading-container"><webthing:thinking style="width:100%" /></div>
      	<webthing:lookupFilter filterContainer="filter-container" />
    	<div id="column-container-a">
    		<div id="user-list-container" >
    			<table id="user-list-table" style="width:100%;">
    				<thead style="width:100%;"></thead>
    				<tbody style="width:100%;"></tbody>
    				<tfoot style="width:100%;"></tfoot>
    			</table>
			</div>
			<div id="column-container-b">
				<div id="action-container">
					<div id="action-button-container">
						<webthing:checkmark styleId="save-button" styleClass="action-button green fa-2x">Save</webthing:checkmark>
						<webthing:ban styleId="cancel-button" styleClass="action-button red fa-2x">Cancel</webthing:ban>
					</div>
					<div id="action-list-container">
					</div>
				</div>
				<div id="ticket-list-container">
					<table id="ticket-table" style="width:100%;">
						<thead style="width:100%;"></thead>
						<tbody style="width:100%;"></tbody>
						<tfoot style="width:100%;"></tfoot>
					</table>
				</div>
				<div class="spacer">&nbsp;</div>
			</div>
			<div class="spacer">&nbsp;</div>
    	</div>
    	
    	
	
    	<webthing:scrolltop />

	    <webthing:ticketModal ticketContainer="ticket-modal" />
		
   
   
    </tiles:put>
		
</tiles:insert>

