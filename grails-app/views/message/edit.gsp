<%@ page import="edu.umn.ncs.mail.Message" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="ncs" />
        <g:set var="entityName" value="${message(code: 'message.label', default: 'Message')}" />
        <title><g:message code="message.compose.label" /> - <g:message code="default.application.title" /></title>
		<g:javascript src="compose.js" />
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><g:link class="inbox" action="list"><g:message code="message.inbox.label" /></g:link></span>
            <span class="menuButton"><g:message code="security.loggedin.as.label" /> <sec:username/> <g:link class="logout" controller="logout" action="index"><g:message code="security.logout.label" /></g:link></span>
        </div>

        <g:if test="${flash.message}">
        <div class="message messageFloat">${flash.message}</div>
        </g:if>

        <h1><g:message code="message.compose.label" /></h1>
        <g:hasErrors bean="${messageInstance}">
        <div class="errors">
            <g:renderErrors bean="${messageInstance}" as="list" />
        </div>
        </g:hasErrors>
        
        <g:form name="contactsAction" controller="recipient" action="contacts" ></g:form>
        <g:form name="saveDraftAction" action="saveDraft" id="${messageInstance?.id}" ></g:form>
        <g:form name="attachmentListAction" action="attachmentList" id="${messageInstance?.id}" ></g:form>
        <g:form name="createRecipientAction" controller="recipient" action="create" id="${messageInstance?.id}" ></g:form>
        <g:form name="saveRecipientAction" controller="recipient" action="save" id="${messageInstance?.id}" ></g:form>
        <g:form name="removeRecipientAction" controller="recipient" action="delete"></g:form>
        <g:form name="removeAttachmentAction" controller="attachment" action="delete"></g:form>
            <div class="dialog">
                <table>
                    <tbody>
                        <tr class="prop">
                            <td valign="top" class="name">
                                <label for="from"><g:message code="message.from.label" default="From" /></label>
                            </td>
                            <td valign="top" class="value ${hasErrors(bean: messageInstance, field: 'from', 'errors')}">
                                <sec:username/>
                            </td>
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name">
                                <label for="recipient"><g:message code="message.recipient.label" default="To" /></label>
                            </td>
                            <td valign="top" class="value">
                            	<div id="recipientsContainer">
                            		<g:include controller="recipient" action="list" id="${messageInstance?.id}" />
                                </div>
                                <button id="add-recipient-button">+</button>
                            </td>
                        </tr>

						<tr class="prop">
							<td valign="top" class="name">
								<label for="attachment"><g:message code="message.attachment.label" default="Attachments" /></label>
							</td>
							<td valign="top" class="value">
								<g:if test="${! messageInstance.attachments}">
									<em></em>
								</g:if>
								<ul id="attachmentList">
									<g:include controller="attachment" action="list" id="${messageInstance?.id}" />
								</ul>
						        <div id="attachFile">
							        <fileuploader:form name="fileUpload"
							        	upload="files" 
										successAction="uploadSuccess"
										successController="attachment"
										errorAction="uploadError"
										errorController="attachment" 
										id="${messageInstance?.id}"/>
										(Max File Size: 25MB)
						        </div>
								<button id="add-attachment-button" style="display:none;">+</button>
							</td>
						</tr>
                    </tbody>
                </table>
        	<g:form action="saveDraft" method="post" name="messageGuts">
                <table>
                    <tbody>
                        <tr class="prop">
                            <td valign="top" class="name">
                              <label for="dateExpires"><g:message code="message.dateExpires.label" default="Date Expires" /></label>
                            </td>
                            <td valign="top" class="value ${hasErrors(bean: messageInstance, field: 'dateExpires', 'errors')}">
                                <g:datePicker class="autosave" name="dateExpires" precision="day" value="${messageInstance?.dateExpires}" years="${yearRange}"  />
                            </td>
                        </tr>

                        <tr class="prop">
                            <td valign="top" class="name">
                              <label for="subject"><g:message code="message.subject.label" default="Subject" /></label>
                            </td>
                            <td valign="top" class="value ${hasErrors(bean: messageInstance, field: 'subject', 'errors')}">
                                <g:textField class="autosave" size="73" name="subject" value="${messageInstance?.subject}" /><br/>
								<div id="private-data-warning" class="warning">Do not use private data in the subject line, since it is exposed and not encrypted when the notification is sent via email.</div>
                            </td>
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name">
                              <label for="body"><g:message code="message.body.label" default="Body" /></label>
                            </td>
                            <td valign="top" class="value ${hasErrors(bean: messageInstance, field: 'body', 'errors')}">
                                <g:textArea class="autosave" name="body" cols="76" rows="10" value="${messageInstance?.body}" />
                            </td>
                        </tr>
			
                    </tbody>
                </table>
            </div>
            <div class="buttons">
                <span class="button"><g:actionSubmit class="send" action="send" value="${message(code: 'message.send.label', default: 'Send')}" /></span>
                <span class="button"><g:actionSubmit class="save_draft" action="saveDraft" value="${message(code: 'message.save.as.draft.label', default: 'Save as Draft')}" /><span id="lastSaved"></span></span>
                <span class="button"><g:actionSubmit class="trash" action="delete" value="${message(code: 'default.button.discard.label', default: 'Discard')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" /></span>
            </div>
            <g:hiddenField name="id" value="${messageInstance?.id}" />
        </g:form>
    </body>
</html>
