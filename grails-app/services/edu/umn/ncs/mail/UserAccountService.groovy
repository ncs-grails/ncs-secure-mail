package edu.umn.ncs.mail

import org.apache.commons.lang.RandomStringUtils

// This service provides account related utilities
class UserAccountService {

	def springSecurityService
	def daoAuthenticationProvider
	
    static transactional = true
	static def debug = false

	def passwordVerifier = { password ->
		/*
		 * Be from 8-125 characters in length (some client software is limited to 32).
		 * Contain characters from three of the following four categories:
			* English uppercase letters (A through Z)
			 * English lowercase letters (a through z)
			 * Digits (0 through 9)
			 * Nonalphanumeric characters (e.g., !, #, %)
		 * Not end in a space, although they may contain spaces (e.g. '3 Brown mice')
		 */
		
		def error = 0
		// 1 = too short
		// 2 = too long
		// 3 = complexity fails
		// 4 = begins or ends with a space
		
		def numericPattern = /[0-9]/
		def lowerAlphaPattern = /[a-z]/
		def upperAlphaPattern = /[A-Z]/
		def nonAlphaNumeric = /[0-9a-zA-Z]*/
		
		if (password.length() < 8) {
			error = 1
		} else if (password.length() > 125) {
			error = 2
		} else if (password[0] == ' ' | password[-1] == ' ') {
			error = 4
		} else {
			def score = 0
			if ( password =~ numericPattern ) { score++ }
			if ( password =~ lowerAlphaPattern ) { score++ }
			if ( password =~ upperAlphaPattern ) { score++ }
			if ( ! (password ==~ nonAlphaNumeric) ) { score++ }
			if (score < 3) {
				error = 3
			}
		}
		
		return error
	}
	
	def confirmPassword(userInstance, password) {
		// I don't know how to do this yet using spring security
		true
	}
	
	def setNewPassword(userInstance, newPassword) {
		def success = false
		
		if (debug) {
			println "Setting new password for ${userInstance.username} to ${newPassword}"
		}

		String passwordHash = springSecurityService.encodePassword(newPassword)
		userInstance.password = passwordHash
		userInstance.passwordExpired = false
		userInstance.resetKey = null
		userInstance.newAccount = false
		
		if (userInstance.save(flush:true)) {
			success = true
		} else {
			if (debug) {
				println "Unable to set new password for user."
			}
		}
		return success
	}
	
	def resetPassword(emailAddress, fullName, createOnly) {
		
		String errorMessage = ""
		String resetKey = ""
		def success = false
		def userInstance = null

		if (emailAddress) {
			def email = emailAddress.trim().toLowerCase()
			// send a reset password email link out
			userInstance = User.findByUsername(email)
			if (! userInstance) {
				userInstance = User.createFromEmail(email, fullName, true)
				if ( ! userInstance.validate() ) {
					// that's an invalid email address
					errorMessage = "Sorry, that's not a valid email address. (${email})"
					if (debug) {
						userInstance.errors.each{
							println "\t${it}"
						}
					}
					userInstance = null
				}
			} else if (createOnly) {
				errorMessage = "You already have an account.  Try resetting your password if you forgot it."
				return [errorMessage: errorMessage, success: success, 
							resetKey: null, userInstance: userInstance ]
			}
			
			if (userInstance) {
				// the user has been pulled, or created.  Now we reset the password
				// generate a useless random password
				resetKey = RandomStringUtils.random(32, true, true)
			
				if (debug) {
					println "Creating reset key for ${email} : ${resetKey}."
				}
				userInstance.resetKey = resetKey
				userInstance.newAccount = false
				if ( ! userInstance.save(flush:true)) {
					if (debug) {
						println "ERROR!  Unable to reset the password for user ${email}"
						userInstance.errors.each{
							println "\t${it}"
						}
					}
					errorMessage = "Sorry, we were unable to reset your password."
				} else {
					success = true
				}
			} else if ( ! errorMessage) {
				if (debug) {
					println "ERROR!  Unable to find or create an account for user ${email}"
				}
				errorMessage = "Sorry, we were unable to reset your password."
			}
		}
		[errorMessage: errorMessage, success: success
			, resetKey: resetKey, userInstance: userInstance ]
	}
}
