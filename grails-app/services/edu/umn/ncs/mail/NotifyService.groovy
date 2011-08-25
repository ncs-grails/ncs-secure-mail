package edu.umn.ncs.mail

import java.util.Date;

// This service sends out various notifications
class NotifyService {

    static transactional = true
	
	def userAccountService
	def mailService
	static boolean debug = false
		
	def sendPasswordResetNotification(userInstance, resetKey) {
		// send notification
		mailService.sendMail {
			to userInstance.email
			from "secure-messaging@ncs.umn.edu"
			subject "SecureMessage - Password Reset Notification"
			body( view:"/notify/passwordReset",
				model:[ userInstance: userInstance,
					resetKey: resetKey])
		}
	}
	
    def checkForNotifications() {
		def now = new Date()
		
		// find everyone who has yet to be notified
		def unsentNotifications = Recipient.createCriteria().list{
			and {
				eq("sendNotification", true)
				message {
					and {
						eq("draft", false)
						gt('dateExpires', now)
					}
				}
			}
		}
		
		if (! unsentNotifications && debug ) {
			println "No notification to send."
		}
		
		unsentNotifications.each{ recipientInstance ->
			Recipient.withTransaction{
				
				def newAccount = false
				def userInstance = recipientInstance.user
				// if it's a new account and they haven't been sent a reset key
				if (userInstance?.newAccount && ! userInstance.resetKey) {
					userAccountService.resetPassword(userInstance.username, null, false)
					newAccount = true
				}
				
				def messageInstance = recipientInstance.message
				
				// send notification
				mailService.sendMail {
					to recipientInstance.user.email
					from "secure-messaging@ncs.umn.edu"
					subject "SecureMessage - Regarding: ${messageInstance.subject}"
					body( view:"/notify/newMessage",
						model:[ recipientInstance: recipientInstance,
							messageInstance: messageInstance,
							newAccount: newAccount])
				}
				
				// send the password reset email
				if (newAccount) {
					sendPasswordResetNotification(userInstance, userInstance.resetKey)
				}

				// mark as sent
				recipientInstance.sendNotification = false
				recipientInstance.notificationsSent++
				recipientInstance.lastNotified = new Date()
				
				if ( ! recipientInstance.save(flush:true) ) {
					println "ERROR! Unable to mark notification as sent"
				}
				
				if (debug) {
					println "Need to send notification to: ${recipientInstance.user}"
				}
			}
		}
		
    }
	
}
