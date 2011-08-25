package edu.umn.ncs.mail

import com.lucastex.grails.fileuploader.UFile

class Attachment {

	String fileName
	String contentType
	UFile uploadedFile
	
	static belongsTo = [ message : Message ]
	
    static constraints = {
		fileName()
		contentType()
		// this prevents an uploaded file to be linked to the wrong message
		uploadedFile(unique:true)
    }
	static mapping = {
		sort "fileName"
	}
}
