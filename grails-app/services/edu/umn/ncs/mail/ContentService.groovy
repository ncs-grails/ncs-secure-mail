package edu.umn.ncs.mail

class ContentService {

    static transactional = true
	static def debug = false
	
	// Future TODO: detect MIME type with Tika
	// http://www.jworks.nl/blog/show/76/Indexing%20documents%20with%20Tika%20and%20Grails
	
	def defaultMimeType() {
		"application/octet-stream"
	}
	
    def detectMimeType(fileExtension) {
		String mimeType = defaultMimeType()
		
		switch (fileExtension?.toLowerCase()) {
			// MS Office Docs
			case 'xls':		mimeType = "application/vnd.ms-excel"; break;
			case 'xlsx':	mimeType = "application/vnd.ms-excel"; break;
			case 'rtf':		mimeType = "application/msword"; break;
			case 'doc':		mimeType = "application/msword"; break;
			case 'docx':	mimeType = "application/msword"; break;
			case 'ppt':		mimeType = "application/vnd.ms-powerpoint"; break;
			case 'pptx':	mimeType = "application/vnd.ms-powerpoint"; break;
			// Images
			case 'gif':		mimeType = "image/gif"; break;
			case 'jpg':		mimeType = "image/jpeg"; break;
			case 'jpeg':	mimeType = "image/jpeg"; break;
			case 'png':		mimeType = "image/png"; break;
			// Text
			case 'txt':		mimeType = "text/plain"; break;
			case 'text':	mimeType = "text/plain"; break;
			// HTML
			case 'htm':		mimeType = "text/html"; break;
			case 'html':	mimeType = "text/html"; break;
			// PDF
			case 'pdf':		mimeType = "application/pdf"; break;
			// Default
			default:		mimeType = defaultMimeType()
		}
		
		return mimeType
    }
}
