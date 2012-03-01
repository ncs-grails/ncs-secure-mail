<head>
	<meta name='layout' content='ncs' />
	<title>Denied</title>
</head>
<body>
<div class='body'>
	<g:if test='${flash.message}'>
		<div class='errors'>${flash.message}</div>
	</g:if>
	<div class='errors'>Sorry, you're not authorized to view this page.</div>
</div>
</body>
