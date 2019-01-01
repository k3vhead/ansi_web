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
        Non-Direct Labor
    </tiles:put>
    
    
    <tiles:put name="headextra" type="string">
        <style type="text/css">
        	#ndl-crud-form {
        		display:none;
        		background-color:#FFFFFF;
				color:#000000;
				width:400px;
				padding:15px;
        	}
			#displayTable {
				width:90%;
			}
			
			.prettyWideButton {
				height:30px;
				min-height:30px;
			}
			
        </style>
        
        <script type="text/javascript">
        
        $(document).ready(function() {
        	;NONDIRECTLABOR = {
        		datatable : null,
        		
        		init : function() {
        			NONDIRECTLABOR.createTable();
        			NONDIRECTLABOR.makeModal();
        			NONDIRECTLABOR.makeClickers();
        			NONDIRECTLABOR.makeDivisionList();
        			NONDIRECTLABOR.makeAutoComplete();
        		},
        		
        		
        		
        		clearForm : function() {
        			console.log("Clearing form");
        			$("#ndl-crud-form select[name='divisionId']").val();
        			$.each( $("#ndl-crud-form input"), function($index, $value) {        				
        				var $selector = '#ndl-crud-form input[name="' + $value.name + '"]';
        				$($selector).val("");
        			});
        		},
        		
        		
        		
        		createTable : function() {
            		var dataTable = $('#displayTable').DataTable( {
            			"aaSorting":		[[0,'asc']],
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
            	        	'pageLength','copy', 'csv', 'excel', {extend: 'pdfHtml5', orientation: 'landscape'}, 'print',{extend: 'colvis',	label: function () {doFunctionBinding();$('#displayTable').draw();}}
            	        ],
            	        "columnDefs": [
             	            { "orderable": false, "targets": -1 },
            	            { className: "dt-left", "targets": [4,5,6,11] },
            	            { className: "dt-center", "targets": [0,1,2,3,7,8,10,12,-1] },
            	            { className: "dt-right", "targets": [9]}
            	         ],
            	        "paging": true,
    			        "ajax": {
    			        	"url": "claims/nonDirectLaborLookup",
    			        	"type": "GET"
    			        	},
    			        columns: [
    			        	
    			            { title: "Div", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {	
    			            	if(row.division != null){return (row.division+"");}
    			            } },
    			            { title: "Week", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.week != null){return (row.week);}
    			            } },
    			            { title: "Date", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.workDate != null){return (row.workDate+"");}
    			            } },
    			            { title: "Washer", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.washer_id != null){return (row.lastName+", "+row.firstName);}
    			            } },
    			            { title: "Hrs Type" , "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {	
    			            	if(row.hoursType != null){return (row.hoursType+"");}
    			            } },
    			            { title: "Hours", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.hours != null){return (row.hours+"");}
    			            } },
    			            { title: "Notes",  "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.notes != null){return (row.notes+"");}
    			            } },			            
    			            { title: "<bean:message key="field.label.action" />",  data: function ( row, type, set ) {	
    			            	//console.log(row);
    			            	{
    				            	var $edit = '<a href="jobMaintenance.html?id='+row.jobId+'" class="editAction" data-id="'+row.jobId+'"><webthing:edit>View</webthing:edit></a>';
    			            		return "<ansi:hasPermission permissionRequired='QUOTE_READ'>"+$edit+"</ansi:hasPermission>";
    			            		//return "<ansi:hasPermission permissionRequired='SYSADMIN'><ansi:hasWrite><a href='jobMaintenance.html?id="+row.jobId+"' class=\"editAction fas fa-pencil-alt\" data-id='"+row.jobId+"'></a></ansi:hasWrite></ansi:hasPermission>";
    			            	}
    			            	
    			            } }],
    			            "initComplete": function(settings, json) {
    			            	//console.log(json);
    			            	doFunctionBinding();
    			            },
    			            "drawCallback": function( settings ) {
    			            	doFunctionBinding();
    			            }
    			    } );
            		//new $.fn.dataTable.FixedColumns( dataTable );
            	},
        		
        		
            	
            	doPost : function() {
            		console.log("Doing a post");
            		$("#ndl-crud-form").dialog("close");
            	},
            	
            	
            	
            	makeAutoComplete : function() {
            		var $displaySelector = '#ndl-crud-form input[name="washerName"]';
            		var $idSelector = '#ndl-crud-form input[name="washerId"]';
            		var $washerAutoComplete = $($displaySelector).autocomplete({
						source: "washerTypeAhead?",
						select: function( event, ui ) {
							$( $idSelector ).val(ui.item.id);								
							console.log(ui.item);	
							//ADDRESSUTILS.getAddress(ui.item.id, QUOTEMAINTENANCE.populateAddressModal);
   				      	},
						response: function(event, ui) {
							if (ui.content.length === 0) {
								console.log("No match");
								$("#washerIdErr").html("No Washer Found");
					        } else {
					        	$("#washerIdErr").html("");
					        }
						}
					});	
            	},
            	
            	
            	
            	
            	makeClickers : function() {
            		$('.ScrollTop').click(function() {
        				$('html, body').animate({scrollTop: 0}, 800);
              	  		return false;
              	    });
            		
            		$("#new-NDL-button").click(function($event) {
            			NONDIRECTLABOR.clearForm();
            			$( "#ndl-crud-form" ).dialog("open");
            		});
            		
            		$('.dateField').datepicker({
                        prevText:'&lt;&lt;',
                        nextText: '&gt;&gt;',
                        showButtonPanel:true
                    });
            	},
            	
            	
            	
            	makeDivisionList : function() {
					console.log("makeDivisionList");
					var jqxhr3 = $.ajax({
						type: 'GET',
						url: 'division/list',
						data: {},
						statusCode: {
							200:function($data) {
								var $select = $("#ndl-crud-form select[name='divisionId']");
								$('option', $select).remove();

								$select.append(new Option("",""));
								$.each($data.data.divisionList, function(index, val) {
								    $select.append(new Option(val.divisionNbr + "-" + val.divisionCode, val.divisionId));
								});
							},
							403: function($data) {								
								$("#globalMsg").html("Session Expired. Log In and try again").show();
							},
							404: function($data) {
								$("#globalMsg").html("System Error Division 404. Contact Support").show();
							},
							500: function($data) {
								$("#globalMsg").html("System Error Division 500. Contact Support").show();
							}
						},
						dataType: 'json',
						async:false
					});
				},
				
            	
            	
				makeModal : function() {
					$( "#ndl-crud-form" ).dialog({
						title:'Non-Direct Labor',
						autoOpen: false,
						height: 500,
						width: 700,
						modal: true,
						closeOnEscape:true,
						//open: function(event, ui) {
						//	$(".ui-dialog-titlebar-close", ui.dialog | ui).hide();
						//},
						buttons: [
							{
								id: "ndl-cancel-button",
								click: function($event) {
									$( "#ndl-crud-form" ).dialog("close");
								}
							},{
								id: "ndl-save-button",
								click: function($event) {
									NONDIRECTLABOR.doPost();
								}
							}
						]
					});	
					$("#ndl-save-button").button('option', 'label', 'Save');
					$("#ndl-cancel-button").button('option', 'label', 'Cancel');
        		},
        	}
      	  	

        	NONDIRECTLABOR.init();
        	
        	        	
        			
            
             
				
				function doFunctionBinding() {
					$( ".editAction" ).on( "click", function($clickevent) {
						 doEdit($clickevent);
					});
				}
				
				function doEdit($clickevent) {
					var $rowid = $clickevent.currentTarget.attributes['data-id'].value;

						var $url = 'jobTable/' + $rowid;
						//console.log("YOU PASSED ROW ID:" + $rowid);
						var jqxhr = $.ajax({
							type: 'GET',
							url: $url,
							success: function($data) {
								//console.log($data);
								
				        		$("#jobId").val(($data.data.codeList[0]).jobId);
				        		$("#jobStatus").val(($data.data.codeList[0]).jobStatus);
				        		$("#divisionNbr").val(($data.data.codeList[0]).divisionNbr);
				        		$("#billToName").val(($data.data.codeList[0]).billToName);
				        		$("#jobSiteName").val(($data.data.codeList[0]).jobSiteName);
				        		$("#jobSiteAddress").val(($data.data.codeList[0]).jobSiteAddress);
				        		$("#startDate").val(($data.data.codeList[0]).startDate);
				        		$("#jobFrequency").val(($data.data.codeList[0]).startDate);
				        		$("#pricePerCleaning").val(($data.data.codeList[0]).pricePerCleaning);
				        		$("#jobNbr").val(($data.data.codeList[0]).jobNbr);
				        		$("#serviceDescription").val(($data.data.codeList[0]).serviceDescription);
				        		$("#poNumber").val(($data.data.codeList[0]).processDate);
				        		
				        		$("#jId").val(($data.data.codeList[0]).jobId);
				        		$("#updateOrAdd").val("update");
				        		$("#addJobTableForm").dialog( "open" );
							},
							statusCode: {
								403: function($data) {
									$("#useridMsg").html("Session Timeout. Log in and try again");
								} 
							},
							dataType: 'json'
						});
					//console.log("Edit Button Clicked: " + $rowid);
				}
        });
        </script>        
    </tiles:put>
    
   <tiles:put name="content" type="string">
    	<h1>Non Direct Labor</h1>
    	
 	<table id="displayTable" style="table-layout: fixed" class="display" cellspacing="0" style="font-size:9pt;max-width:1300px;width:1300px;">
       	<colgroup>
        	<col style="width:5%;" />
        	<col style="width:5%;" />
    		<col style="width:5%;" />    		
    		<col style="width:10%;" />
    		<col style="width:10%;" />
    		<col style="width:5%;" />
    		<col style="width:50%;" />
    		<col style="width:10%;" />
   		</colgroup>
        <thead>
            <tr>
                <th>Div</th>
                <th>Week</th>
    			<th>Date</th>
    			<th>Washer</th>
    			<th>Hours Type</th>
    			<th>Hours</th>
    			<th>Notes</th>
    			<th>Action</th>    			
            </tr>
        </thead>
        <tfoot>
            <tr>
                <th>Div</th>
                <th>Week</th>
    			<th>Date</th>
    			<th>Washer</th>
    			<th>Hours Type</th>
    			<th>Hours</th>
    			<th>Notes</th>   
    			<th>Action</th>  			
            </tr>
        </tfoot>
    </table>
    <input type="button" value="New" class="prettyWideButton" id="new-NDL-button" />
    
    <webthing:scrolltop />
    
    <div id="ndl-crud-form">
    	<table>
    		<tr>
    			<td><span class="formLabel">Division</span></td>
    			<td><select name="divisionId"></select></td>
    			<td><span id="divisionIdErr" class="err"></span></td>
    		</tr>
    		<tr>
    			<td><span class="formLabel">Date</span></td>
    			<td><input type="text" name="workDate" class="dateField" /></td>
    			<td><span id="dateErr" class="err"></span></td>
    		</tr>
    		<tr>
    			<td><span class="formLabel">Washer</span></td>
    			<td><input type="text" name="washerName" /><input type="hidden" name="washerId" /></td>
    			<td><span id="washerIdErr" class="err"></span></td>
    		</tr>
    		<tr>
    			<td><span class="formLabel">Hours</span></td>
    			<td><input type="text" name="hours" /></td>
    			<td><span id="hoursErr" class="err"></span></td>
    		</tr>
    		<tr>
    			<td><span class="formLabel">Type</span></td>
    			<td><input type="text"  name="hoursType" /></td>
    			<td><span id="hoursTypeErr" class="err"></span></td>
    		</tr>
    		<tr>
    			<td><span class="formLabel">Notes</span></td>
    			<td><input type="text" name="notes" /></td>
    			<td><span id="notesErr" class="err"></span></td>
    		</tr>
    	</table>
    </div>
    </tiles:put>
		
</tiles:insert>

