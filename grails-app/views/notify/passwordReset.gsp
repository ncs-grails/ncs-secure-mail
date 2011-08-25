<%@ page contentType="text/html"%>
<html>
<head>
<title>SecureMessage - Password Reset Notification</title>
</head>
<body>
<h1 style="color: maroon;">NCS Secure Messaging System</h1>
<hr />
<p>Your password has been reset for accessing the Secure Mail System provided by the
       <strong>University of Minnesota - National Children's Study.</strong> </p>
<hr />
       <p>To choose your new password, please use the following link:
       <g:link absolute="true" controller="account" action="newPassword" id="${userInstance.id}" params="${[key: resetKey]}" >
       <g:createLink absolute="true" controller="account" action="newPassword" id="${userInstance.id}" params="${[key: resetKey]}" />
       </g:link></p>
</body>
</html>
