package edu.umn.ncs.mail

import grails.plugins.springsecurity.Secured
import grails.converters.*

@Secured(['ROLE_USER'])
class RecipientController {

	static def debug = false
		
	def springSecurityService

	def contacts = {
		def userInstance = springSecurityService.currentUser
		
		if (debug) { println "RecipientController:contacts:params:: ${params}" }
		
		def emailList = [] as Set
		def filter = '%'
		
		
		if (params?.term) {
			filter = "%${params?.term}%"
		}
		
		def recipientInstanceList = Recipient.createCriteria().list{
			and {
				message {
					from {
						eq('username', userInstance.username)
					}
				}
				user {
					ilike('username', filter)				
				}
			}
		}
		
		def userInstanceList = recipientInstanceList.collect{ it. user }
		userInstanceList.each {
			emailList.add( it.username )
		}
		
		render emailList as JSON
	}
	
	def list = {
		def userInstance = springSecurityService.currentUser
		
		session.username = userInstance.username
		def messageInstance = Message.read(params.id)

		if (!messageInstance) {
			if (debug) { println "User tried to list recipients for non-existant message." }
			render ""
		} else if ( ! messageInstance.allowedToEdit(userInstance.username) ) {
			if (debug) { println "User tried to list recipients without edit permissions" }
			render ""
		} else {
			def recipientInstanceList = messageInstance.recipients.sort{ it.user.username }
			
			return [recipientInstanceList: recipientInstanceList ]
		}
	}
	
    def create = { [id: params?.id] }
	
	def save = {
		// add a recipient (via ajax?)
		if (debug) { println "RecipientController:save:params::${params}" }
		
		// get the logged in user
		def userInstance = springSecurityService.currentUser
		
		// get the email address of the recipient to add
		def email = params?.email?.toLowerCase()?.trim()
		// the recipient to return
		def recipientInstance = null
		def returnData = [:]
		
		returnData.error = true
		
		// if there's an email address...
		if (email) {
			// get the email message
			def messageInstance = Message.get(params.id)
			
			if (messageInstance) {
				// see if the user is in the system
				def recipientUserInstance = User.findByUsername(email)
				// if not...
				if ( recipientUserInstance ) {
					if (debug) { println "Found user: ${recipientUserInstance.username}, id: ${recipientUserInstance.id}" }
				} else {
					// ... create a new user
					// This will fail if the email address is invalid.
					recipientUserInstance = User.createFromEmail(email, true)
					if (debug) { println "Created user: ${recipientUserInstance.username}, id: ${recipientUserInstance.id}" }
				}
				// if the user was found or created...
				if (recipientUserInstance?.id) {
					// add them to the recipients of the message
					recipientInstance = new Recipient(user:recipientUserInstance,
						userCreated: userInstance, message: messageInstance)
					
					if ( ! recipientInstance.validate()) {
						if (debug) { println "unable to add recipient. recipientInstance cannot be validated." }
							recipientInstance.errors.each{
								println "\t${it}"
							}
							render(view:'create', model:[email: email])
							return
					} else {
						if ( ! recipientInstance.save() ) {
							if (debug) {
								println "unable to add recipient. recipientInstance cannot be saved."
								recipientInstance.errors.each{
									println "\t${it}"
								}
							}
							render(view:'create', model:[email: email])
							return
						}
					}
				}
			} else {
				if (debug) { println "Message not found: ${params?.id}." }
			}
		}

		if (recipientInstance) {
			render(view:'show', model:[recipientInstance: recipientInstance])
		} else {
			render(view:'create', model:[email: email, cssClass: 'inputError'])
		}
	}
	
    def show = {
		// you can only show a recipient if you are the author of the message
		def recipientInstance = Recipient.read(params?.id)
		def userInstance = springSecurityService.currentUser
		
		if (recipientInstance.message.allowedToEdit(userInstance.username)) {
			[recipientInstance: recipientInstance] 
		} else {
			render ""
			return
		}
	}
	
	def delete = {
		
		def userInstance = springSecurityService.currentUser
		def recipientInstance = Recipient.get(params?.id)
		// you can only remove a recipient if you are the author of the message
		if (recipientInstance.message.allowedToEdit(userInstance.username)) {
			try {
				recipientInstance.delete(flush: true)
				// flash.message = "${message(code: 'recipient.removed.label')}"
				render ""
			}
			catch (org.springframework.dao.DataIntegrityViolationException e) {
				def errorMessage = "Unable to delete this recipient."
				render(view:'show', model:[recipientInstance: recipientInstance, errorMessage: errorMessage])
			}
		} else {
			render ""
			return
		}
	}
}
