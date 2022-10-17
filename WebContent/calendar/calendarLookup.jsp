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
        <bean:message key="page.label.calendar" /> <bean:message key="page.label.lookup" />
    </tiles:put>
    
    
    <tiles:put name="headextra" type="string">
    	<link rel="stylesheet" href="css/lookup.css" />
    	<link rel="stylesheet" href="css/callNote.css" />
    	<script type="text/javascript" src="js/ansi_utils.js"></script>
    	<script type="text/javascript" src="js/lookup.js"></script> 
        <style type="text/css">
        	#delete-modal {
        		display:none;
        	}
        	#filter-container {
        		width:402px;
        		float:right;
        	}
        	#new-calendar-modal {
        		display:none;
        	}
        	.action-link {
        		cursor:pointer;
        	}
        	.form-label {
        		font-weight:bold;
        	}
        	.monthLabel {
        		font-weight:bold;
        		background-color:#000000;
        		color:#FFFFFF;
        	}
        	.pre-edit {
				background-color:#CC6600;				
			}
        </style>
        
        <script type="text/javascript">
        
        $(document).ready(function() {
        	;CALENDAR_LOOKUP = {        			
        		dateTypeList : null,
        		
        		init : function() {
        			CALENDAR_LOOKUP.getCalendar();
        			ANSI_UTILS.getOptionList('CALENDAR_TYPE',CALENDAR_LOOKUP.makeCalendarTypeList);
        			CALENDAR_LOOKUP.makeClickers();
        			CALENDAR_LOOKUP.makeNewCalendarModal();
        			CALENDAR_LOOKUP.makeDeleteModal();
        		},
        		
        		
        		clearForm : function() {
        			console.log("clearForm");
        			$.each( $("#new-calendar-modal input"), function($index, $value) {        				
        				var $selector = '#new-calendar-modal input[name="' + $value.name + '"]';
        				$($selector).val("");        				
        			});
        			$.each( $("#new-calendar-modal select"), function($index, $value) {        				
        				var $selector = '#new-calendar-modal select[name="' + $value.name + '"]';
        				$($selector).val("");        				
        			});
        			
        			$("#new-calendar-modal .err").html("");

        		},
        		
        		
        		
        		deleteDate : function() {
        			var $url = "calendar/ansiCalendar" 
           			var $outbound = {};
           			$.each( $("#delete-modal input"), function($index, $value) {
           				$outbound[$($value).attr("name")] = $($value).val();
           			});
           			$.each( $("#delete-modal select"), function($index, $value) {
           				$outbound[$($value).attr("name")] = $($value).val();
           			});

           			var jqxhr = $.ajax({
   						type: 'DELETE',
   						url: $url,
   						data: JSON.stringify($outbound),
   						statusCode: {
   							200 : function($data) {
   								if ( $data.responseHeader.responseCode == "SUCCESS" ) {
   									$("#delete-modal").dialog("close");
   									CALENDAR_LOOKUP.refreshMonth($data.data);
   									$("#globalMsg").html("Success").show().fadeOut(4000);
   								} else if ($data.responseHeader.responseCode == "EDIT_FAILURE") {
   									$("#delete-modal").dialog("close");
   									$("#globalMsg").html("Invalid system state. Reload and try again").show().fadeOut(4000);
   								} else {
   									$("#globalMsg").html("Invalid Response Code " + $data.responseHeader.responseCode + ". Contact Spport").show();
   									$("#delete-modal").dialog("close");
   									$('html, body').animate({scrollTop: 0}, 800);
   								}
   								
   							},
   							403: function($data) {
								$("#delete-modal").dialog("close");
   								$("#globalMsg").html("Session Timeout. Log in and try again").show();
   							},
   							404: function($data) {
								$("#delete-modal").dialog("close");
   								$("#globalMsg").html("System Error 404. Reload and try again").show();
   							},
   							405: function($data) {
								$("#delete-modal").dialog("close");
   								$("#globalMsg").html("System Error 405. Contact Support").show();
   							},
   							500: function($data) {
								$("#delete-modal").dialog("close");
   								$("#globalMsg").html("System Error 500. Contact Support").show();
   							},
   						},
   						dataType: 'json'
   					});
        		},
        		
        		
        		
        		getCalendar : function($year) {
        			$("#displayTable").html("");
        			var $url = "calendar/ansiCalendar"        				       
        			if ( $year != null ) {
        				$url = $url + "/" + $year;
        			}	
        			var jqxhr = $.ajax({
						type: 'GET',
						url: $url,
						statusCode: {
							200 : function($data) {
								CALENDAR_LOOKUP.makeTable($data);
							},
							403: function($data) {
								$("#globalMsg").html("Session Timeout. Log in and try again").show();
							},
							404: function($data) {
								$("#globalMsg").html("System Error 404. Reload and try again").show();
							},
							405: function($data) {
								$("#globalMsg").html("System Error 405. Contact Support").show();
							},
							500: function($data) {
								$("#globalMsg").html("System Error 500. Contact Support").show();
							},
						},
						dataType: 'json'
					});
        		},
        		
        		
        		
        		makeCalendarTypeList : function($data) {
        			CALENDAR_LOOKUP.dateTypeList = $data.calendarType;
        			var $select = $("#calendar-selector select[name='highlighter']");
					$('option', $select).remove();

					$select.append(new Option("",""));
					$.each($data.calendarType, function(index, val) {
					    $select.append(new Option(val.display, val.code));
					});
					
					$($select).change(function() {
						var $type = $("#calendar-selector select[name='highlighter'] option:selected").val();
						$("#displayTable .calendar-row").removeClass("pre-edit");
						if ( $type != null && $type != "") {
							$("#displayTable ." + $type).addClass("pre-edit");
						}
					});							
					
					var $select = $("#new-calendar-modal select[name='dateType']");
					$('option', $select).remove();

					$select.append(new Option("",""));
					$.each($data.calendarType, function(index, val) {
					    $select.append(new Option(val.display, val.code));
					});
        		},
        		
        		
        		makeClickers : function() {
            		$('.ScrollTop').click(function() {
        				$('html, body').animate({scrollTop: 0}, 800);
              	  		return false;
              	    });
            		
            		$("#calendar-selector input[name='new-calendar-button']").click(function($event) {
            			CALENDAR_LOOKUP.clearForm();
            			$( "#new-calendar-modal" ).dialog("open");
            		});
            		
            		var $select = $("#calendar-selector select[name='calendarYear']");
					$select.change(function() {
						CALENDAR_LOOKUP.getCalendar($select.val());
					});
            	},

            	
            	
            	
            	makeDeleteModal : function() {
            		$( "#delete-modal" ).dialog({
						title:'Delete ANSI Date',
						autoOpen: false,
						height: 200,
						width: 500,
						modal: true,
						closeOnEscape:true,
						//open: function(event, ui) {
						//	$(".ui-dialog-titlebar-close", ui.dialog | ui).hide();
						//},
						buttons: [
							{
								id: "delete-cancel-button",
								click: function($event) {
									$( "#delete-modal" ).dialog("close");
								}
							},{
								id: "delete-save-button",
								click: function($event) {
									CALENDAR_LOOKUP.deleteDate();
								}
							}
						]
					});	
					$("#delete-save-button").button('option', 'label', 'Yes');
					$("#delete-cancel-button").button('option', 'label', 'No');
            	},
            	
            	
            	
            	makeNewCalendarModal : function() {
            		$( "#new-calendar-modal" ).dialog({
						title:'New ANSI Date',
						autoOpen: false,
						height: 200,
						width: 500,
						modal: true,
						closeOnEscape:true,
						//open: function(event, ui) {
						//	$(".ui-dialog-titlebar-close", ui.dialog | ui).hide();
						//},
						buttons: [
							{
								id: "calendar-cancel-button",
								click: function($event) {
									$( "#new-calendar-modal" ).dialog("close");
								}
							},{
								id: "calendar-save-button",
								click: function($event) {
									CALENDAR_LOOKUP.saveDate();
								}
							}
						]
					});	
					$("#calendar-save-button").button('option', 'label', 'Save');
					$("#calendar-cancel-button").button('option', 'label', 'Cancel');
            	},
            	
            	
            	
				makeTable : function($data) {
					var $select = $("#calendar-selector select[name='calendarYear']");
					$('option', $select).remove();

					//$select.append(new Option("",""));
					$.each($data.data.years, function(index, val) {
					    $select.append(new Option(val, val));
					});
					$($select).val($data.data.selectedYear);
					
					var $monthList = ["January","February","March","April","May","June","July","August","September","October","November","December"];
					var $columnMax = 3;
					var $columnCount = 0;
					var $columnWidth = 100/eval($columnMax+1);   //+1 because we start at 0
					
					var $trLabel = $("<tr>");
					var $trData = $("<tr>");

					$.each($monthList, function($mondIndex, $monthValue) {
						if ( $columnCount > $columnMax ) {
							$("#displayTable").append($trLabel);
							$("#displayTable").append($trData);
							$trLabel = $("<tr>");
							$trData = $("<tr>");							
							$columnCount = 0;
						}
						$tdLabel = $("<td>");
						$tdLabel.attr("class","monthLabel")
						$tdLabel.attr("style","width:" + $columnWidth + "%;");
						$tdLabel.append($monthValue)
						$trLabel.append($tdLabel);
						
						$tdData = $("<td>");
						$tdData.attr("id",$monthValue)
						$tdData.attr("style","vertical-align:top; width:" + $columnWidth + "%;");
						$trData.append($tdData);
												
						$columnCount = $columnCount + 1;
					});
					$("#displayTable").append($trLabel);
					$("#displayTable").append($trData);
					
					$.each($data.data.dates, function($index, $value) {
						var $table = $('<table style="width:100%;">');
						for ( $i=0; $i < $value.length; $i++ ) {
							$dayOfMonth = $value[$i].dateString.substring(3,5);
							$tr = $("<tr>");
							$tr.attr("class","calendar-row " + $value[$i].dateType);
							$td1 = $("<td>");
							$td1.attr("style","width:10%; text-align:center;");
							$td1.append($dayOfMonth);
							$tr.append($td1);
							
							$td2 = $("<td>");
							$td2.attr("style","width:80%;");
							$td2.append($value[$i].dateTypeDescription);
							$tr.append($td2);
							
							$td3 = $("<td>");
							$td3.attr("style","width:10%;");
							$delSpan = $("<span>");
							$delSpan.attr("class","action-link del-link");
							$delSpan.attr("data-date",$value[$i].dateString);
							$delSpan.attr("data-datetype",$value[$i].dateType);
							$delSpan.append('<webthing:delete>Delete</webthing:delete>');
							$td3.append($delSpan);
							$tr.append($td3);
							
							$table.append($tr);
						}
						$("#" + $index).append($table);
					});
					
					$(".del-link").click(function() {
						var $dateString = $(this).attr("data-date");
						var $dateType = $(this).attr("data-datetype");
						CALENDAR_LOOKUP.openDeleteModal($dateString, $dateType);
					});
            	},
        		
            	
            	
            	openDeleteModal : function($dateString, $dateType) {
            		$("#delete-modal input[name='date']").val($dateString);
            		$("#delete-modal input[name='dateType']").val($dateType);
            		$("#delete-modal").dialog("open");
            	},
            	
            	
            	
            	
            	refreshMonth : function($data) {
            		console.log("Refreshing ");
            		
            		var $yearSelect = $("#calendar-selector select[name='calendarYear']");
            		var $selectedYear = $yearSelect.val();
					$('option', $yearSelect).remove();

					$.each($data.years, function(index, val) {
					    $yearSelect.append(new Option(val, val));
					});
					$($yearSelect).val($selectedYear);
            		
            		
            		
            		$selector = "#" + $data.updated;
            		$($selector).html("");
            		$value = $data.dates[$data.updated];
            		var $table = $('<table style="width:100%;">');
					for ( $i=0; $i < $value.length; $i++ ) {
						$dayOfMonth = $value[$i].dateString.substring(3,5);
						$tr = $("<tr>");
						
						var $highlightedType = $("#calendar-selector select[name='highlighter'] option:selected").val();
						if ( $highlightedType == $value[$i].dateType) {
							$tr.attr("class","pre-edit calendar-row " + $value[$i].dateType);
						} else {
							$tr.attr("class","calendar-row " + $value[$i].dateType);	
						}
						$td1 = $("<td>");
						$td1.attr("style","width:10%; text-align:center;");
						$td1.append($dayOfMonth);
						$tr.append($td1);
						
						$td2 = $("<td>");
						$td2.attr("style","width:80%;");
						$td2.append($value[$i].dateTypeDescription);
						$tr.append($td2);
						
						$td3 = $("<td>");
						$td3.attr("style","width:10%;");
						$delSpan = $("<span>");
						$delSpan.attr("class","action-link del-link");
						$delSpan.attr("data-date",$value[$i].dateString);
						$delSpan.attr("data-datetype",$value[$i].dateType);
						$delSpan.append('<webthing:delete>Delete</webthing:delete>');
						$td3.append($delSpan);
						$tr.append($td3);
						
						$table.append($tr);
					}
					$($selector).append($table);
					
					$($selector + " .del-link").click( function() {
						var $dateString = $(this).attr("data-date");
						var $dateType = $(this).attr("data-datetype");
						CALENDAR_LOOKUP.openDeleteModal($dateString, $dateType);
					});
            	},
            	
            	
        		
            	saveDate : function() {            		
        			var $url = "calendar/ansiCalendar" 
        			var $outbound = {};
        			$.each( $("#new-calendar-modal input"), function($index, $value) {
        				$outbound[$($value).attr("name")] = $($value).val();
        			});
        			$.each( $("#new-calendar-modal select"), function($index, $value) {
        				$outbound[$($value).attr("name")] = $($value).val();
        			});

        			var jqxhr = $.ajax({
						type: 'POST',
						url: $url,
						data: JSON.stringify($outbound),
						statusCode: {
							200 : function($data) {
								if ( $data.responseHeader.responseCode == "SUCCESS" ) {
									$("#new-calendar-modal").dialog("close");
									CALENDAR_LOOKUP.refreshMonth($data.data);
									$("#globalMsg").html("Success").show().fadeOut(4000);
								} else if ($data.responseHeader.responseCode == "EDIT_FAILURE") {
									$.each( $data.data.webMessages, function($index, $value) {
										var $selector = "#new-calendar-modal ." + $index + "-err";
										$($selector).html($value[0]).show();
									});
								} else {
									$("#globalMsg").html("Invalid Response Code " + $data.responseHeader.responseCode + ". Contact Spport").show();
									$("#new-calendar-modal").dialog("close");
									$('html, body').animate({scrollTop: 0}, 800);
								}
								
							},
							403: function($data) {
								$("#new-calendar-modal").dialog("close");
								$("#globalMsg").html("Session Timeout. Log in and try again").show();
							},
							404: function($data) {
								$("#new-calendar-modal").dialog("close");
								$("#globalMsg").html("System Error 404. Reload and try again").show();
							},
							405: function($data) {
								$("#new-calendar-modal").dialog("close");
								$("#globalMsg").html("System Error 405. Contact Support").show();
							},
							500: function($data) {
								$("#new-calendar-modal").dialog("close");
								$("#globalMsg").html("System Error 500. Contact Support").show();
							},
						},
						dataType: 'json'
					});
        		},

        	}
      	  	

        	CALENDAR_LOOKUP.init();
        	
        });
        </script>        
    </tiles:put>
    
   <tiles:put name="content" type="string">
    	<h1><bean:message key="page.label.calendar" /> <bean:message key="page.label.lookup" /></h1>
    	
		<div id="calendar-selector" style="text-align:center; width:100%;">
			<select name="calendarYear"></select>
			<select name="highlighter"></select>
			<input type="button" name="new-calendar-button" value="New" />
		</div>    
		
 		<table id="displayTable" style="width:100%; margin-top:10px;">
 			   	
    	</table>
    
     
    	<webthing:scrolltop />
    
    
    	<div id="new-calendar-modal">
    		<table>
    			<tr>
    				<td class="form-label">Date: </td>
    				<td><input type="date" name="date" /></td>
    				<td><span class="err date-err"></span></td>
    			</tr>
    			<tr>
    				<td class="form-label">Type: </td>
    				<td><select name="dateType"></select></td>
    				<td><span class="err dateType-err"></span></td>
    			</tr>
    		</table>
    	</div>
    	
    	
    	<div id="delete-modal">
    		<div style="width:100%; text-align:center;">
    			Delete this entry?
    			<input type="hidden" name="date" value="" />
    			<input type="hidden" name="dateType" value="" />
    		</div>
    	</div>
    </tiles:put>
		
</tiles:insert>

