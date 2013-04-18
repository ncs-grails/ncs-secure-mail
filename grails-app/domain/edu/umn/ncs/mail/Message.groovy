package edu.umn.ncs.mail

/**
This object represents the message itself.
*/
class Message {

	/** This is the user who the message is from, the composer. */
	User from
	/**
	This is subject line of the message.
	NOTE: the subject line is sent over standard email, so this part
	of the message is not "secure". */
	String subject
	/**
	This is the body of the message, the main content.
	The body is considered secure and is never exposed via email.
	*/
	String body
	/** This the date the message was created, not the date it
	 was sent.
	*/
	Date dateCreated = new Date()
	/** All messages expire in this system, so this is the date
	that the message expires.  The default expire date is
	30 days after the message was created. */
	Date dateExpires = ( new Date() ) + 30
	/** This marks whether or not the 'from' User of the message has
	marked this message as deleted. */
	Boolean deleted = false
	/** This marks whether or not the message is in draft mode.
	While in draft mode, the 'from' User can edit it, add attachments
	and add recipients.  Recipients cannot view a message while it is
	in draft mode. */
	Boolean draft = true
	
	/** This is the default toString method for the message.
	It returns <pre>"From ${from}, Subject: ${subject}"</pre>
	*/
	String toString() {
		"From ${from}, Subject: ${subject}"
	}
	
	/** The hasMany static map links this message to it's foreign keys: recipients, attachments
	<dl>
		<dt>recipients</dt>
		<dd>a collection of edu.umn.ncs.mail.Recipient objects</dd>
		<dt>attachments</dt>
		<dd>a colleciton of edu.umn.ncs.mail.Attattachments objects that hold pointers to the uploaded file attachments.</dd>
	</dl>
	*/
	static hasMany = [ recipients: Recipient
		, attachments: Attachment ]
	
	/** 
	This function will return a boolean telling you if a particular user via username, is allowed to
	view this message.
	@username the username (email address) of the user to check viewing permission for.
	*/
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
		
	/** 
	This function will return a boolean telling you if a particular user via username, is allowed to
	edit this message.
	This just checks to see if the user is the author of the message.
	@username the username (email address) of the user to check viewing permission for.
	*/
	Boolean allowedToEdit(String username) {
		// the user must either be the author...
		if (from.username == username) {
			return true
		} else {
			return false
		}
	}

	/**
	The following names queries are 
	*/
	static namedQueries = {
		// Incoming Messages
		incomingMessages { username ->
			def now = new Date()
			and {
				recipients {
					and {
						user {
							eq('username', username)
						}
						eq('deleted', false)
					}
				}
				gt('dateExpires', now)
				eq('deleted', false)
				eq('draft', false)
			}
			order('dateCreated', 'desc')
		}

		// Message Drafts
		drafts { username -> 
			and {
				from {
					eq('username', username)
				}
				eq('deleted', false)
				eq('draft', true)
			}
			order('dateCreated', 'desc')
		}

		// Sent Messages
		sentMessages { username ->
			def now = new Date()
			and {
				from {
					eq('username', username)
				}
				gt('dateExpires', now)
				eq('deleted', false)
				eq('draft', false)
			}
			order('dateCreated', 'desc')
		}

		// Deleted Messages
		deletedMessages { username ->
			def now = new Date()
			and {
				recipients {
					and {
						user {
							eq('username', username)
						}
						eq('deleted', true)
					}
				}
				gt('dateExpires', now)
				eq('deleted', false)
				eq('draft', false)
			}
			order('dateCreated', 'desc')
		}

		// Deleted Sent Messages
		deletedSentMessages { username ->
			def now = new Date()
			and {
				from {
					eq('username', username)
				}
				gt('dateExpires', now)
				eq('deleted', true)
			}
			order('dateCreated', 'desc')
		}
	}

	/**
	These are the non-default constraints placed on this class.
	Non default values are as follows:
	<dl>
		<dt>subject</dt>
		<dd>this field is optional, it can be null</dd>
		<dt>body</dt>
		<dd>this field is optional, it can be null</dd>
	</dl>
	*/
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
