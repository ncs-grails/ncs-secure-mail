package edu.umn.ncs.mail

import org.apache.commons.lang.RandomStringUtils

class User {

	def springSecurityService
	
	String username
	String fullName
	String password
	boolean enabled = true
	boolean accountExpired = false
	boolean accountLocked = false
	boolean passwordExpired = true
	boolean newAccount = true
	String resetKey
	
	String toString() {
		username
	}
	
	String getEmail() { username }

	String getFullEmail() {
		if (fullName) {
			"${fullName} <${email}>"
		} else {
			email
		}
	}

	static constraints = {
		username blank: false, unique: true, email:true
		password blank: false
		fullName nullable: true
		resetKey nullable:true
	}

	static transients = [ 'email', 'fullEmail' ]
	
	static mapping = {
		password column: '`password`'
	}
		
	Set<Role> getAuthorities() {
		UserRole.findAllByUser(this).collect { it.role } as Set
	}

	static User createFromEmail(String emailAddress, boolean flush = false) {
		return createFromEmail(emailAddress, null, flush)
	}
	
	static User createFromEmail(String emailAddress, String fullName, boolean flush = false) {
		
		// generate a useless random password
		def password = RandomStringUtils.random(12, true, true)
		
		def userInstance = new User(username: emailAddress, fullName: fullName, password:password)
		if (userInstance.validate()) {
			if ( userInstance.save(flush:flush) ) {
				def userRole = Role.findByAuthority('ROLE_USER')
				UserRole.create(userInstance, userRole)
			}
		}
		return userInstance
	}	
}
