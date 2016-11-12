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
<%@ taglib tagdir="/WEB-INF/tags/webthing" prefix="ansi" %>



<tiles:insert page="layout.jsp" flush="true">

    <tiles:put name="title" type="string">
        Codes Maintenance
    </tiles:put>
    
    
    <tiles:put name="headextra" type="string">
        <style type="text/css">
			#confirmDelete {
				display:none;
				background-color:#FFFFFF;
				color:#000000;
			}
        </style>
        
        <script type="text/javascript">
        $(function() {        
			var jqxhr = $.ajax({
				type: 'GET',
				url: 'codes/getList.json',
				data: {},
				success: function($data) {
					$.each($data.data.codesList, function(index, value) {
						addRow(index, value);
					});
					$('.updAction').bind("click", function($clickevent) {
						doUpdate($clickevent);
					});
					$('.delAction').bind("click", function($clickevent) {
						doDelete($clickevent);
					});
				},
				statusCode: {
					403: function($data) {
						$("#useridMsg").html($data.responseJSON.responseHeader.responseMessage);
					} 
				},
				dataType: 'json'
			});
			
			function addRow(index, $code) {	
				var $rownum = index + 1;
				var row = '<tr>';
				row = row + '<td>' + $code.tableName + '</td>';
				row = row + '<td>' + $code.fieldName + '</td>';
				row = row + '<td>' + $code.value + '</td>';
       			row = row + '<td>' + $code.displayValue + '</td>';
       			row = row + '<td>' + $code.seq + '</td>';
       			row = row + '<td>' + $code.description + '</td>';
       			row = row + '<td>' + $code.status + '</td>';
       			row = row + '<td>';
       			row = row + '<a href="#" class="updAction" data-row="' + $rownum +'"><span class="fa fa-pencil" ari-hidden="true"></span></a> | ';
       			row = row + '<a href="#" class="delAction" data-row="' + $rownum +'"><span class="fa fa-trash" aria-hidden="true"></span></a>';
       			row = row + '</td>';       			
       			row = row + '</tr>';
       			//$('#displayTable tr:last').before(row);	
       			$('#displayTable').append(row);
			}
			
			function doUpdate($clickevent) {
				$clickevent.preventDefault();
				console.debug("Doing update");
			}
			
			function doDelete($clickevent) {
				$clickevent.preventDefault();
				var rownum = $clickevent.currentTarget.attributes['data-row'].value;
				$('#confirmDelete').data('rownum',rownum);
             	$('#confirmDelete').bPopup({
					modalClose: false,
					opacity: 0.6,
					positionStyle: 'fixed' //'fixed' or 'absolute'
				});
			}
			
            $("#cancelDelete").click( function($event) {
            	$event.preventDefault();
            	$('#confirmDelete').bPopup().close();
            });         

            $("#doDelete").click(function($event) {
            	$event.preventDefault();
            	var $tableData = [];
                $("#displayTable").find('tr').each(function (rowIndex, r) {
                    var cols = [];
                    $(this).find('th,td').each(function (colIndex, c) {
                        cols.push(c.textContent);
                    });
                    $tableData.push(cols);
                });

            	var $rownum = $('#confirmDelete').data('rownum');
            	//var $rows = $("#displayTable").find('tr');
            	var $tableName = $tableData[$rownum][0];
            	var $fieldName = $tableData[$rownum][1];
            	var $value = $tableData[$rownum][2];
            	$outbound = JSON.stringify({'tableName':$tableName, 'fieldName':$fieldName,'value':$value});
            	console.debug($outbound);
            	var jqxhr = $.ajax({
            	     type: 'delete',
            	     url: 'codes/delete.json',
            	     data: $outbound,
            	     success: function($data) {
						//location.href="dashboard.html";
						console.debug($data);
						//$('#confirmDelete').bPopup().close();
            	     },
            	     statusCode: {
            	    	403: function($data) {
            	    		$("#useridMsg").html($data.responseJSON.responseHeader.responseMessage);
            	    	} 
            	     },
            	     dataType: 'json'
            	});
            });

        });
        </script>        
    </tiles:put>
    
    
    <tiles:put name="content" type="string">
    	<h1>Codes Maintenance</h1>
    	
    	<table id="displayTable">
    		<tr>
    			<th>Table</th>
    			<th>Field</th>
    			<th>Value</th>
    			<th>Display</th>
    			<th>Seq</th>
    			<th>Description</th>
    			<th>Status</th>
    			<th>Action</th>
    		</tr>
    	</table>
    	
    	<div id="confirmDelete">
    		Are You Sure You Want to Delete this Code?<br />
    		<input type="button" id="cancelDelete" value="No" />
    		<input type="button" id="doDelete" value="Yes" />
    	</div>
    </tiles:put>

</tiles:insert>

