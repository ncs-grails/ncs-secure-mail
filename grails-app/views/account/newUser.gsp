<head>
<meta name='layout' content='main' />
<title>New Account - <g:message code="default.application.title" /></title>
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

#requirementList {
	text-align: left;
	font-size: 0.85em;
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
	<h1>New Account - <g:message code="default.application.title" /></h1>

	<g:form name="passwordForm" action="register" controller="account" autocomplete="off">
	<fieldset class="maroonBorder" id='login'> 
		<legend>Sign Up for an Account</legend> 

			<h2>Password Requirements</h2>
			<ul id="requirementList">
			<li>Be from 8-125 characters in length (some client software is limited to 32).</li>
		 	<li>Contain characters from three of the following four categories:
				<ul>
				<li>English uppercase letters (A through Z)</li>
			 	<li>English lowercase letters (a through z)</li>
			 	<li>Digits (0 through 9)</li>
			 	<li>Nonalphanumeric characters (e.g., !, #, %)</li>
			 	</ul>
		 	</li>
		 	<li>Not end in a space, although they may contain spaces (e.g. '3 Brown mice')</li>
			</ul>

			<g:if test='${flash.message}'>
			<div class='login_message'>${flash.message}</div>
			</g:if>
				<div class="message">To create a new account, please complete the following information.</div>
				<p>
					<label for='email'>Email: </label>
					<g:textField size='25' class='text_' name='email' value="${email}" />
				</p>
				<p>
					<label for='fullName'>Full Name (optional)</label>
					<g:textField size='25' class='text_' name='fullName' value="${fullName}" />
				</p>
				<p>
					<label for="mathAnswer">What is ${mathEquation}?</label>
					<input type="text" size="5" name="mathAnswer" id="mathAnswer" class="text_" />
				</p>
				
				<p>
					<input type='submit' value='Sign Up' />
				</p>
	</fieldset>
	</g:form>
<script type='text/javascript'>
<!--
(function(){
	document.forms['passwordForm'].elements['email'].focus();
})();
// -->
</script>
</body>
