<head>
<meta name='layout' content='main' />
<title>Login - Secure Messaging System</title>
<style type='text/css' media='screen'>

.maroonBorder {
    border-color: #7A0019;
    border-style: solid;
    border-width: 2px;
    margin-top: 0.5em;
    margin-bottom: 0.5em;
    padding: 0.25em;
    -moz-border-radius: 0.5em;
    -webkit-border-radius: 0.5em;
    border-radius: 0.5em;
}

#login {
	width: 20em;
	margin-left: auto;
    margin-right: auto;
	padding-left: auto;
    padding-right: auto;
    text-align: right;
}


fieldset.maroonBorder legend {
    text-align: left;
    color: #FC3;
    font-weight: bold;
    background-color: #7A0019;
    border-color: #7A0019;
    border-style: solid;
    border-width: thin;
    padding: 0.2em;
    -moz-border-radius: 0.25em;
    -webkit-border-radius: 0.25em;
    border-radius: 0.25em;
}

input.text_ {
    border-color: #7A0019;
    border-style: solid;
    border-width: thin;
    padding: 0.2em;
    -moz-border-radius: 0.2em;
    -webkit-border-radius: 0.2em;
    border-radius: 0.2em;
}

input[type="submit"] {
    background-color: #FC3;
    color: #7A0019;
    font-weight: bold;
    border-color: #7A0019;
    border-style: solid;
    border-width: thin;
    padding: 0.2em;
    -moz-border-radius: 0.25em;
    -webkit-border-radius: 0.25em;
    border-radius: 0.25em;
}

input[type="submit"]:hover {
    color: #FC3;
    background-color: #7A0019;
}

input[type="submit"]:active {
    color: #FD5;
    background-color: #A04;
}

div.login_message {
	padding-top: 0.5em;
	padding-bottom: 0.5em;
	color: red;
	font-style: italic;
}

</style>
</head>

<body>

	<h1><g:message code="default.application.title" /></h1>

	<form action='${postUrl}' method='POST' id='loginForm' class='cssform' autocomplete='off'>
	<fieldset class="maroonBorder" id='login'> 
		<legend>Please Login...</legend> 

			<g:if test='${flash.message}'>
			<div class='message'>${flash.message}</div>
			</g:if>

				<p>
					<label for='username'>Email: </label>
					<input type='text' class='text_' name='j_username' id='username' value="${session?.username}" />
				</p>
				<p>
					<label for='password'>Password: </label>
					<input type='password' class='text_' name='j_password' id='password' />
				</p>
				<p>
					<input type='submit' value='Login' />
				</p>

				<hr/>
				<p>Don't have an account? <br/>
				<g:link controller="account" action="newUser">Click here to create a new account.</g:link></p>

				<g:if test="${showReset}">
				<hr/>
				<p>Don't know your password? <br/>
				<g:link controller="account" action="forgot">Click here to reset it.</g:link></p>
				</g:if>
	</fieldset>
	</form>
<script type='text/javascript'>
<!--
(function(){
	document.forms['loginForm'].elements['j_username'].focus();
})();
// -->
</script>
</body>
