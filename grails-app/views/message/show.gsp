
<%@ page import="edu.umn.ncs.mail.Message" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="ncs" />
        <g:set var="entityName" value="${message(code: 'message.label', default: 'Message')}" />
        <title><g:message code="default.show.label" args="[entityName]" /></title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><g:link class="inbox" action="list"><g:message code="message.inbox.label" /></g:link></span>
            <span class="menuButton"><g:link class="compose" action="create"><g:message code="message.compose.label" /></g:link></span>
            <span class="menuButton"><g:message code="security.loggedin.as.label" /> <sec:username/> <g:link class="logout" controller="logout" action="index"><g:message code="security.logout.label" /></g:link></span>
        </div>
        <div class="body">
            <h1>Viewing Message</h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <g:if test="${messageInstance?.deleted}">
            	<div class="message">This message has been deleted.  The recipients are unable to view this message.</div>
            </g:if>
            <g:if test="${markedDeleted}">
            	<div class="message">You have placed this item in your trash.  It will be purged after the message expires.</div>
            </g:if>
            <div class="dialog">
                <table>
                    <tbody>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="message.from.label" default="From" /></td>
                            
                            <td valign="top" class="value">${messageInstance?.from?.encodeAsHTML()}</td>
                        </tr>
                        
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="message.recipients.label" default="To" /></td>
                            
                            <td valign="top" style="text-align: left;" class="value">
                                <ul style="list-style: none; padding-left: 0;">
                                <g:each in="${messageInstance.recipients.sort{ it.user.username } }" var="r">
                                    <li>
                                    	${r?.encodeAsHTML()}
                                    	<g:if test="${isAuthor}">
                                    		<span class="recipientStatus">
                                    		<g:if test="${r.lastViewed}">
                                    			 - Last Viewed <g:formatDate date="${r.lastViewed}" format="M/d/yyyy h:mm a"/>.
                                    		</g:if>
                                    		<g:else>
                                    			<g:if test="${r.lastNotified}">
                                    				 - Last Notified <g:formatDate date="${r.lastNotified}" format="M/d/yyyy h:mm a"/>.
                                    				 (${r.notificationsSent} sent)
                                    				 </g:if>
                                    			<g:else>
                                    				Notification Pending...
                                    			</g:else>
                                    		</g:else>
                                    		</span>
                                    	</g:if>
                                    </li>
                                </g:each>
                                </ul>
                            </td>
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="message.Date.label" default="Date" /></td>
                            <td valign="top" class="value">
                            	<g:formatDate date="${messageInstance?.dateCreated}" />,
                            	<em>
                            	<g:message code="message.dateExpires.label" default="Date Expires" />:
                            	<g:formatDate date="${messageInstance?.dateExpires}" format="M/d/yyyy" />
                            	(${messageInstance?.dateExpires - ( new Date() )} days)
                            	</em>
                           	</td>
                        </tr>

                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="message.subject.label" default="Subject" /></td>
                            <td valign="top" class="value">
                            	<strong>${fieldValue(bean: messageInstance, field: "subject")}</strong>
                            	</td>
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="message.body.label" default="Body" /></td>
                            
                            <td valign="top" class="value">
                            	<g:if test="${! messageInstance.body}">
                            		<p style="font-style: italic;">None</p>
                            	</g:if>
                            	<g:else>
                            		<p class="messageBody">${messageInstance.body.encodeAsHTML().replace('\r\n', '<br/>').replace('\r', '<br/>').replace('\n', '<br/>')}</p>
                            	</g:else>
                            </td>
                            
                        </tr>

						<g:if test="${messageInstance.attachments}">
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="message.attachment.label" default="Attachments" /></td>
                            
                            <td valign="top" style="text-align: left;" class="value">
                            	<ul id="attachmentList">
								<g:each var="a" in="${messageInstance.attachments.sort{it.fileName}}">
								<li>
									<span id="attachment.container.${a.id}">
									<strong>${a.fileName}</strong>
									( <fileuploader:prettysize size="${a.uploadedFile.size}" /> )
									<g:if test="${a.contentType != 'application/octet-stream' }">
										<g:link controller="attachment" id="${a.id}">View</g:link>
									</g:if>
									<g:link controller="attachment" id="${a.id}" params="${[download: true]}">Download</g:link>
								</li>
								</g:each>
								</ul>
							</td>
                        </tr>
                        </g:if>
                                        
                    </tbody>
                </table>
            </div>
            <div class="buttons">
                <g:form>
                    <g:hiddenField name="id" value="${messageInstance?.id}" />
                    <span class="button"><g:actionSubmit class="reply" action="reply" value="${message(code: 'default.message.reply.label', default: 'Reply')}" /></span>
                    <span class="button"><g:actionSubmit class="reply" action="replyAll" value="${message(code: 'default.message.replyToAll.label', default: 'Reply to all')}" /></span>
                    <span class="button"><g:actionSubmit class="forward" action="forward" value="${message(code: 'default.message.forward.label', default: 'Forward')}" /></span>
                    <span class="button"><g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" /></span>
                </g:form>
            </div>
        </div>
    </body>
</html>
