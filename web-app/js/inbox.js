
// jQuery action called after HTML document is fully loaded
$(document).ready(function(){
	
	$("#folderMenu").find("a").each(function(i) {
		if (i > 0) {
			var tag = $(this).attr("href");
			$(tag).hide();
		} else {
			$(this).parent("li").addClass("folder-selected");
		}
	});

	$("#folderMenu").find("a").click(function(){
		// hide all sections
		var destTag = $(this).attr("href");
		$("#folderMenu").find("a").each(function(i) {
			$(this).parent("li").removeClass("folder-selected");
			var tag = $(this).attr("href");
			$(tag).hide();
		});

		$(this).parent("li").addClass("folder-selected");
		$(destTag).show();
		return false;
	});
	
	// Makes notifications go away slowly (unless you click them)
	$("div.message").fadeTo('fast', 0.8);
	$("div.message").click(function(){ $(this).show('fast') });
	$("div.message").delay(5000).fadeOut('slow');
	
});
