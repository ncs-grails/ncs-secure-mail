<%@ page contentType="text/html"%>
<html>
<head>
<title>SecureMessage - Regarding: ${messageInstance.subject}</title>
</head>
<body>
<h1 style="color: maroon;">NCS Secure Messaging System</h1>
<hr />
<p>You have a new message waiting in the Secure Messaging system provided by the
       <strong>University of Minnesota - National Children's Study.</strong> </p>

<p>The message is from: <br/>
       <strong>${messageInstance.from}</strong></p>

<p>The message is regarding: <br/>
       <strong>${messageInstance.subject}</strong></p>

<hr />

       <g:if test="${newAccount}">
       	<p><strong>It appears this is the first time you have used this service.  Before 
       	you use this service, you must set up a password.</strong>  A separate email has 
       	been sent to you that has a link in it that you can follow to set up your 
       	password for the first time.
       </g:if>

       <p>To view the message, please enter the following URL in your browser:
       <g:link absolute="true" controller="go" action="message" id="${messageInstance.id}" params="${[user:recipientInstance.user.username]}" >
       <g:createLink absolute="true" controller="go" action="message" id="${messageInstance.id}" params="${[user:recipientInstance.user.username]}" />
       </g:link></p>
       
     
       <p>The message will expire on: <g:formatDate date="${messageInstance.dateExpires}" format="M/d/yyyy" /></p>

</body>
</html>
