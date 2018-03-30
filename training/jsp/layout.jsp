<html>
	<head>
		<title>ANSI Scheduling - <tiles:insert attribute="title" /></title>
		<tiles:insert attribute="headextra" />
	</head>
	<body>
		<ansi:loggedIn>
			<li>
				<a href="#">My ANSI</a>
				<ul class="sub_menu" style="z-index:1000">
			 		<li><html:link action="myAccount">My Account</html:link></li>
			 		<li><html:link action="logoff">Logoff</html:link></li>
				</ul>
			</li>
			
			
			<ansi:hasPermission permissionRequired="QUOTE">
				<ansi:hasWrite>
				<li><html:link action="quoteMaintenance">New Quote</html:link></li>
				</ansi:hasWrite>
				</ansi:hasPermission>
			</ansi:loggedIn>
			
			
			
			
			<div class="mainContent">
				<div id="globalMsg" class="err">&nbsp;</div>
	    		<tiles:insert attribute="content" />
	    	</div>
	</body>
</html>