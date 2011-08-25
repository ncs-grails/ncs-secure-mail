<% def recipientCount = 0 %>
<div id="existingRecipients">
<g:each status="i" var="r" in="${recipientInstanceList}">
	<g:include controller="recipient" action="show" id="${r.id}" />
	<g:set var="recipientCount" value="${ i }" />
</g:each>
</div>
<div id="newRecipients">
<g:each var="n" in="${(recipientCount)..(recipientCount + 2)}">
	<g:include controller="recipient" action="create" id="${n}" />
</g:each>
</div>
<g:hiddenField name="maxRecipient" value="${recipientCount + 3}" />