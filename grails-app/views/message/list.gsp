<%@ page import="edu.umn.ncs.mail.Message" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="ncs" />
        <g:set var="entityName" value="${message(code: 'message.label', default: 'Message')}" />
        <title><g:message code="default.application.title" /></title>
		<g:javascript src="inbox.js" />
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><g:link class="inbox" action="list"><g:message code="message.inbox.refresh.label" /></g:link></span>
            <span class="menuButton"><g:link class="compose" action="create"><g:message code="message.compose.label" /></g:link></span>
            <span class="menuButton"><g:message code="security.loggedin.as.label" /> <sec:username/> <g:link class="logout" controller="logout" action="index"><g:message code="security.logout.label" /></g:link></span>
            <span class="menuButton"><g:link class="logout" controller="account" action="changePassword"><g:message code="security.change.password.label" /></g:link></span>
        </div>
        
        <g:if test="${flash.message}">
        <div class="message messageFloat">${flash.message}</div>
        </g:if>

        <h1><g:message code="default.application.title" /></h1>
        
    	<div id="mailboxes" class="container_12">
 	    	<div class="grid_12 pull_12">
			    <div class="grid_2 alpha">
			    	<div id="folderMenu" class="menuButton">
			    		<ul class="folderList">
				    		<li><a href="#inbox-tab" class="inbox">Inbox</a></li>
			    			<li><a href="#drafts-tab" class="save_draft">Drafts
			    				<g:if test="${draftMessageInstanceList}">
			    					(${draftMessageInstanceList.size()})
			    				</g:if>
			    			</a></li>
			    			<li><a href="#sent-tab" class="send">Sent Items</a></li>
			    			<li><a href="#trash-tab" class="trash">Trash</a></li>
			    		</ul>
			    	</div>
			    </div>
				<div class="grid_10 omega">
		            <div id="inbox-tab" class="mailbox">
						<h2>Inbox</h2>
		                <table>
		                    <thead>
		                        <tr>
		                        	<g:sortableColumn class="grid_2" property="from.username" title="${message(code: 'message.from.label', default: 'From')}" />
		                            <g:sortableColumn class="grid_6" property="subject" title="${message(code: 'message.subject.label', default: 'Subject')}" />
		                            <g:sortableColumn class="grid_1" property="dateCreated" title="${message(code: 'message.dateCreated.label', default: 'Sent')}" />
		                            <g:sortableColumn class="grid_1" property="dateExpires" title="${message(code: 'message.dateExpires.label', default: 'Expires')}" />
		                        </tr>
		                    </thead>
		                    <tbody>
		                    <g:if test="${! incomingMessageInstanceList}">
		                        <tr><td colspan="4">No Messages</td></tr>                    
		                    </g:if>
		                    <g:each in="${incomingMessageInstanceList}" status="i" var="messageInstance">
		                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
		                            <td class="grid_2"><g:link action="show" id="${messageInstance.id}">${fieldValue(bean: messageInstance, field: "from")}</g:link></td>
		                            <td class="grid_6 ${messageInstance.attachments ? 'attach' : ''}">
		                            	<g:link action="show" id="${messageInstance.id}">${fieldValue(bean: messageInstance, field: "subject")}</g:link>
		                            </td>
									<td class="grid_1 dateCreated">
										<g:if test="${messageInstance.dateCreated < thisMorning}">
											<g:formatDate format="MMM d" date="${messageInstance.dateCreated}" />
										</g:if>
										<g:else>
											<g:formatDate format="h:kk a" date="${messageInstance.dateCreated}" />
										</g:else>
									</td>
									<td class="grid_1 dateExpires">
										<g:formatDate format="MMM d" date="${messageInstance.dateExpires}" />
									</td>
		                        </tr>
		                    </g:each>
		                    </tbody>
		                </table>
		            </div>
		
		            <div id="drafts-tab" class="mailbox">
					<h2>Drafts</h2>
		                <table>
		                    <thead>
		                        <tr>
		                        	<g:sortableColumn class="grid_2"  property="recipients" title="${message(code: 'message.recipients.label', default: 'To')}" />
		                            <g:sortableColumn class="grid_6"  property="subject" title="${message(code: 'message.subject.label', default: 'Subject')}" />
		                            <g:sortableColumn class="grid_1"  property="dateCreated" title="${message(code: 'message.dateCreated.label', default: 'Sent')}" />
		                            <g:sortableColumn class="grid_1"  property="dateExpires" title="${message(code: 'message.dateExpires.label', default: 'Expires')}" />
		                        
		                        </tr>
		                    </thead>
		                    <tbody>
				            <g:if test="${! draftMessageInstanceList}">
		                        <tr><td colspan="4">No Messages</td></tr>                    
				            </g:if>
		                    <g:each in="${draftMessageInstanceList}" status="i" var="messageInstance">
		                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
		                            <td class="grid_2"><g:link action="edit" id="${messageInstance.id}">${messageInstance?.recipients.join(', ') ?: 'none yet'}</g:link></td>
		                            <td class="grid_6"><g:link action="edit" id="${messageInstance.id}">${messageInstance?.subject ?: 'no subject'}</g:link></td>
									<td class="grid_1 dateCreated">
										<g:if test="${messageInstance.dateCreated < thisMorning}">
											<g:formatDate format="MMM d" date="${messageInstance.dateCreated}" />
										</g:if>
										<g:else>
											<g:formatDate format="h:kk a" date="${messageInstance.dateCreated}" />
										</g:else>
									</td>
									<td class="grid_1 dateExpires">
										<g:formatDate format="MMM d" date="${messageInstance.dateExpires}" />
									</td>
		                        </tr>
		                    </g:each>
		                    </tbody>
		                </table>
		            </div>
		
		            <div id="sent-tab" class="mailbox">
						<h2>Sent Items</h2>
		                <table>
		                    <thead>
		                        <tr>
		                        	<g:sortableColumn class="grid_2"  property="recipients" title="${message(code: 'message.recipients.label', default: 'To')}" />
		                            <g:sortableColumn class="grid_6"  property="subject" title="${message(code: 'message.subject.label', default: 'Subject')}" />
		                            <g:sortableColumn class="grid_1"  property="dateCreated" title="${message(code: 'message.dateCreated.label', default: 'Sent')}" />
		                            <g:sortableColumn class="grid_1"  property="dateExpires" title="${message(code: 'message.dateExpires.label', default: 'Expires')}" />
		                        
		                        </tr>
		                    </thead>
		                    <tbody>
				            <g:if test="${! outgoingMessageInstanceList}">
		                        <tr><td colspan="4">No Messages</td></tr>                    
				            </g:if>
		                    <g:each in="${outgoingMessageInstanceList}" status="i" var="messageInstance">
		                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
		                            <td class="grid_2"><g:link action="show" id="${messageInstance.id}">${messageInstance?.recipients.join(', ')}</g:link></td>
		                            <td class="grid_6"><g:link action="show" id="${messageInstance.id}">${fieldValue(bean: messageInstance, field: "subject")}</g:link></td>
									<td class="grid_1 dateCreated">
										<g:if test="${messageInstance.dateCreated < thisMorning}">
											<g:formatDate format="MMM d" date="${messageInstance.dateCreated}" />
										</g:if>
										<g:else>
											<g:formatDate format="h:kk a" date="${messageInstance.dateCreated}" />
										</g:else>
									</td>
									<td class="grid_1 dateExpires">
										<g:formatDate format="MMM d" date="${messageInstance.dateExpires}" />
									</td>
		                        </tr>
		                    </g:each>
		                    </tbody>
		                </table>
		            </div>

		            <div id="trash-tab" class="mailbox">
						<h2>Trash</h2>
		                <table>
		                    <thead>
		                        <tr>
		                        	<g:sortableColumn class="grid_2" property="from.username" title="${message(code: 'message.from.label', default: 'From')}" />
		                            <g:sortableColumn class="grid_6" property="subject" title="${message(code: 'message.subject.label', default: 'Subject')}" />
		                            <g:sortableColumn class="grid_1" property="dateCreated" title="${message(code: 'message.dateCreated.label', default: 'Sent')}" />
		                            <g:sortableColumn class="grid_1" property="dateExpires" title="${message(code: 'message.dateExpires.label', default: 'Expires')}" />
		                        </tr>
		                    </thead>
		                    <tbody>
		                    <g:if test="${! deletedMessageInstanceList}">
		                        <tr><td colspan="4">No Messages</td></tr>                    
		                    </g:if>
		                    <g:each in="${deletedMessageInstanceList}" status="i" var="messageInstance">
		                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
		                            <td class="grid_2"><g:link action="show" id="${messageInstance.id}">${fieldValue(bean: messageInstance, field: "from")}</g:link></td>
		                            <td class="grid_6"><g:link action="show" id="${messageInstance.id}">${fieldValue(bean: messageInstance, field: "subject")}</g:link></td>
									<td class="grid_1 dateCreated">
										<g:if test="${messageInstance.dateCreated < thisMorning}">
											<g:formatDate format="MMM d" date="${messageInstance.dateCreated}" />
										</g:if>
										<g:else>
											<g:formatDate format="h:kk a" date="${messageInstance.dateCreated}" />
										</g:else>
									</td>
									<td class="grid_1 dateExpires">
										<g:formatDate format="MMM d" date="${messageInstance.dateExpires}" />
									</td>
		                        </tr>
		                    </g:each>
		                    </tbody>
		                </table>
		            </div>

					<div class="clear-both" style="height:1em;"></div>
				</div>
    		</div>
		</div>
		<div style="float:right">
			<label for="fullName">Your full name as displayed to others:</label>
			<g:remoteField name="fullName" value="${userInstance.fullName}" controller="user" action="update" id="${userInstance.id}" />
		</div>
    </body>
</html>
