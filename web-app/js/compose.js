// jQuery action called after HTML document is fully loaded
$(document).ready(function(){
	
	// Private Data Warning (hide by default)
	$("#private-data-warning").hide();
	
	// enable the security warning for the subject line
	$("input[name='subject']").focus(function(){
		$("#private-data-warning").show();
	});
	$("input[name='subject']").blur(function(){
		$("#private-data-warning").fadeOut('slow');
	});

	// Makes notifications go away slowly (unless you click them)
	$("div.message").fadeTo('fast', 0.8);
	$("div.message").click(function(){ $(this).show('fast') });
	$("div.message").delay(5000).fadeOut('slow');
	
	// enables the add recipient button
	$("#add-recipient-button").click(function() { createRecipient(); });
	
	// starts the auto-save process
	$(".autosave").keyup(function(){		
		setTimeout("saveNow()", 3000);
	});
	
	// enables the force saveNow() when the "Save Draft" button is pressed
	$("input[name='_action_saveDraft']").click(function(){
		forceSave();
		return false;
	});

	// auto-submit the attachment
	$("input[type='file']").parent('form').attr('id', 'fileUploadForm');
	$("input[type='file']").next("input[type='submit']").attr('id', 'fileUploadSubmit');
	$('#fileUploadSubmit').hide();
	
	$("input[type='file']").change(function(){
		// save the draft
		forceSave();
		// upload the file
		setTimeout("submitFileUploadForm()", 500);	
	});
	
	enableAutoComplete();
	
	// sets focus to the first recipient
	$("input[name='recipient\.new\.0']").focus();
});

function submitFileUploadForm(){
	$('#fileUploadSubmit').click();
}

function enableAutoComplete() {
	var url = $("form[name='contactsAction']").attr('action');
	$("input.toAddress").autocomplete({
		source: url
	});
}

// Auto Save stuff
function updateLastSaved() {
	var now = new Date();
	$("#lastSaved").html("last saved:" + now.toLocaleTimeString());
	setTimeout("setOkToSave()", 3000);
	autoSaveExecuting = false;
}

var okToSaveNow = true;
var autoSaveExecuting = false;

function setOkToSave(){
	okToSaveNow = true;
}

function forceSave() {
	setOkToSave();
	saveNow();
}

function saveNow() {
	if ( ! autoSaveExecuting && okToSaveNow ) {
		autoSaveExecuting = true;
		okToSaveNow = false;
		
		var theForm = $("form[name='messageGuts']");
		var action = $(theForm).attr('action');
		
		$.ajax({
				type: 'POST',
				url: action,
				data: $(theForm).serialize(),
				success: updateLastSaved(),
				error: setOkToSave()
			});
		// auto save every 5 seconds...
	}
}

function createRecipient() {
	var url = $("form[name='createRecipientAction']").attr('action');
	var maxRecipient = $("#maxRecipient").val();
	maxRecipient++;
	$("#maxRecipient").val(maxRecipient);
	var data = { id: maxRecipient }
	// load the new input field.
	$.ajax({
		url:url,
		data: data,
		success: function(html){
			$("#newRecipients").append(html);
			enableAutoComplete();
		}
	});	
}

function saveRecipient(control) {
	// get the email address from the control
	var email = $(control).val();
	// if the email address was > 3 characters
	if (email.length > 3) {
		// get the post URL
		var url = $("form[name='saveRecipientAction']").attr('action');
		
		// console.log("Saving Recipient: " + email + " via action: " + url);
		// get the data to pass
		var data = { email: email }
		// load the result
		$(control).parent("span").load(url, data);
	}
}

function removeRecipient(recipientId) {
	var divId = 'recipient\\.show\\.' + recipientId;
	
	var spanToDelete = $('#' + divId);
	
	var url = $("form[name='removeRecipientAction']").attr('action');
	var data = { id: recipientId };
	
	if (true) {
		$.ajax({
			url: url,
			type: 'POST',
			data: data,
			success: function() {
				$(spanToDelete).remove();
			},
			failure: function() {
				alert('Error, unable to remove this recipient!.');
			}
		});
	}
	
}

function removeAttachment(attachmentId) {
	var divId = 'attachment\\.container\\.' + attachmentId;
	
	var spanToDelete = $('#' + divId).parent('li');
	
	var url = $("form[name='removeAttachmentAction']").attr('action');
	var data = { id: attachmentId };
	
	if (true) {
		$.ajax({
			url: url,
			type: 'POST',
			data: data,
			success: function() {
				$(spanToDelete).remove();
			},
			failure: function() {
				alert('Error, unable to remove this attachment!.');
			}
		});
	}
	
}
