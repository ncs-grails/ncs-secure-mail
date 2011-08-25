<g:if test="${recipientInstance}">
<div id="recipient.show.${recipientInstance.id}">
	${recipientInstance.toHTML()}
	<button onclick="removeRecipient(${recipientInstance.id});">remove</button>
	<g:if test="${errorMessage}"><span class="error">${errorMessage}</span></g:if>
	<br/>
</div>
</g:if>
<g:else><div class="recipientNotFound" style="color: red;">
	Recipient Not Found!
</div></g:else>