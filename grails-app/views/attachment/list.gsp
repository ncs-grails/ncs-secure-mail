<g:each var="a" in="${attachmentInstanceList}">
<li>
	<g:include controller="attachment" action="show" id="${a.id}" />
</li>
</g:each>