package edu.umn.ncs.mail

class Recipient {

	User user
	Boolean sendNotification = true
	Integer notificationsSent = 0
	Date lastNotified = null
	Date lastViewed = null
	Boolean deleted = false
	
	Date dateCreated = new Date()
	User userCreated
	
	static belongsTo = [ message: Message ]
	
	String toString() { 
		if (user.fullName) {
			"${user.fullName} <${user.email}>"
		} else {
			user.email
		}
	}

	String toHTML() {
		if (user.fullName) {
			"${user.fullName} &lt;${user.email}&gt;"
		} else {
			user.email
		}
	}

    static constraints = {
		user(unique:'message')
		sendNotification()
		notificationsSent()
		lastNotified(nullable:true)
		lastViewed(nullable:true)
		deleted()
		dateCreated()
		userCreated()
    }
}
