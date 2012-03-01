<head>
<meta name='layout' content='ncs' />
<title>Password Reset - <g:message code="default.application.title" /></title>
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
	width: 30em;
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
	<h1>Password Reset - <g:message code="default.application.title" /></h1>

	<g:form action="reset" controller="account"  class='cssform' name="loginForm" autocomplete="off">
	<fieldset class="maroonBorder" id='login'> 
		<legend>Reset your password</legend> 

			<g:if test='${flash.message}'>
			<div class='login_message'>${flash.message}</div>
			</g:if>
				<div class="message">Please enter your email address and confirm it to have a new temporary password emailed to you.</div>
				<p>
					<label for='username'>Email: </label>
					<g:textField size='30' class='text_' name='email' />
				</p>
				<p>
					<label for='emailConfirm'>Confirm Email: </label>
					<g:textField size='30' class='text_' name='emailConfirm' />
				</p>
				<p>
					<input type='submit' value='Reset' />
				</p>
	</fieldset>
	</g:form>
<script type='text/javascript'>
<!--
(function(){
	document.forms['loginForm'].elements['j_username'].focus();
})();
// -->
</script>
</body>
