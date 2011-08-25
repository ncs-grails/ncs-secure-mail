package edu.umn.ncs.mail

class Message {

	User from
	String subject
	String body
	Date dateCreated = new Date()
	Date dateExpires = ( new Date() ) + 90
	Boolean deleted = false
	Boolean draft = true
	
	String toString() {
		"From ${from}, Subject: ${subject}"
	}
	
	static hasMany = [ recipients: Recipient
		, attachments: Attachment ]
	
	Boolean allowedToView(String username) {
		def allowedToView = false
		def now = new Date()
		
		if (dateExpires > now) {
			// the user must either be the author...
			if (from.username == username) allowedToView = true
			// ... or one of the recipients
			if ( ! deleted ) {
				recipients.each { recipient ->
					if (recipient.user.username == username) allowedToView = true
				}
			}
		}
		return allowedToView
	}
		
	Boolean allowedToEdit(String username) {
		// the user must either be the author...
		if (from.username == username) {
			return true
		} else {
			return false
		}
	}

	   static constraints = {
		from()
		subject(nullable:true)
		body(nullable:true, maxSize: 8094)
		dateCreated()
		dateExpires()
		deleted()
		draft()
    }
}
