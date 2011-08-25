package edu.umn.ncs.mail

import com.lucastex.grails.fileuploader.UFile
import grails.plugins.springsecurity.Secured

@Secured(['ROLE_USER'])
class MessageController {

	static def debug = false	
	def springSecurityService
	def fileUploaderService

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index = {
        redirect(action: "list", params: params)
    }

	// essentially the 'inbox' and 'sent items'
    def list = {
		def now = new Date()
		def userInstance = springSecurityService.currentUser
		session.username = userInstance.username
		
		// find all messages that are not deleted, but addressed to this user
		def incomingMessageInstanceList = Message.createCriteria().list{
			and {
				recipients {
					and {
						user {
							eq('username', userInstance.username)
						}
						eq('deleted', false)
					}
				}
				gt('dateExpires', now)
				eq('deleted', false)
				eq('draft', false)
			}
		}
		
		// find all messages sent by the user
		def draftMessageInstanceList = Message.createCriteria().list{
			and {
				from {
					eq('username', userInstance.username)
				}
				eq('deleted', false)
				eq('draft', true)
			}
		}

		// find all messages sent by the user
		def outgoingMessageInstanceList = Message.createCriteria().list{
			and {
				from {
					eq('username', userInstance.username)
				}
				gt('dateExpires', now)
				eq('deleted', false)
				eq('draft', false)
			}
		}
		
		// TODO: Add Trash Folder
		def deletedMessageInstanceList = Message.createCriteria().list{
			and {
				recipients {
					and {
						user {
							eq('username', userInstance.username)
						}
						eq('deleted', true)
					}
				}
				gt('dateExpires', now)
				eq('deleted', false)
				eq('draft', false)
			}
		}
		
		def deletedSentMessageInstanceList = Message.createCriteria().list{
			and {
				from {
					eq('username', userInstance.username)
				}
				gt('dateExpires', now)
				eq('deleted', true)
			}
		}
		
		deletedMessageInstanceList.addAll(deletedSentMessageInstanceList)

        [incomingMessageInstanceList: incomingMessageInstanceList, 
			draftMessageInstanceList: draftMessageInstanceList, 
			outgoingMessageInstanceList: outgoingMessageInstanceList,
			deletedMessageInstanceList: deletedMessageInstanceList]
    }

	
	def reply = {
		def userInstance = springSecurityService.currentUser
		session.username = userInstance.username
		def origMessageInstance = Message.read(params?.id)
				
		def messageInstance = new Message()
		messageInstance.from = userInstance
				
		if (origMessageInstance?.allowedToView(userInstance.username)) {
			messageInstance.addToRecipients(user: origMessageInstance.from, userCreated: userInstance )
			
			def newBody = origMessageInstance.body?.replace("\n", "\n > ")
			if (newBody) {
				newBody = "\n > " + newBody
			}
			if (debug) { println "${newBody}" }
			messageInstance.body = newBody
			messageInstance.subject = "Re: ${origMessageInstance.subject}"
		}

		if (messageInstance.save(flush: true)) {
			redirect(action: "edit", id: messageInstance.id)
		} else {
			flash.message = "${message(code: 'default.create.error' )}"
			redirect(action: "edit", id: origMessageInstance.id)
		}
	}
	
	// TODO
	def forward = {
		render "Sorry, this is unavailable"
	}
	
	// this is similar to 'compose'
	// we automatically save the message to the database
	// as a draft on create.
    def create = {
		def userInstance = springSecurityService.currentUser
		
        def messageInstance = new Message()
		messageInstance.from = userInstance
		
		if (debug) {
			messageInstance.subject = "Testing: ${new Date()}"
			messageInstance.body = "Test message sent ${new Date()}\n\nThanks!\n--${userInstance?.fullName ?: userInstance.username}"
		}

        if (messageInstance.save(flush: true)) {
            redirect(action: "edit", id: messageInstance.id)
        } else {
            flash.message = "${message(code: 'default.create.error' )}"
            render(view: "list", model: [messageInstance: messageInstance])
        }
    }

	// read a message
    def show = {
		def now = new Date()
		def isAuthor = false
		def userInstance = springSecurityService.currentUser
		session.username = userInstance.username
        def messageInstance = Message.get(params.id)
				
        if (!messageInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'message.label', default: 'Message'), params.id])}"
            redirect(action: "list")
        }
        else {
			if (userInstance.username == messageInstance.from.username) { isAuthor = true }
			
			def markedDeleted = messageInstance.deleted
			// pull the recipient record if the viewer is a recipient
			def recipientInstance = Recipient.createCriteria().get{
				and {
					user {
						eq('username', userInstance.username)
					}
					message {
						idEq(messageInstance.id)
					}
				}
			}
			
			if ( ! markedDeleted ) {
				if (recipientInstance) {
					markedDeleted = recipientInstance.deleted
				}
			}
		
			if (messageInstance.allowedToView(userInstance.username)) {
				
				if (recipientInstance) {
					// update recipient info
					recipientInstance.lastViewed = now
					recipientInstance.save(flush:true)
				}
				
				[ messageInstance: messageInstance, 
					username:userInstance.username, 
					markedDeleted: markedDeleted,
					isAuthor: isAuthor ]
			} else {
            	flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'message.label', default: 'Message'), params.id])}"
				redirect(action: "list")
			}
        }
    }
	
	// compose a message
    def edit = {
		//year in 90 days
		def maxExpireDate = new GregorianCalendar()
		def currentYear = maxExpireDate.get(Calendar.YEAR)
		maxExpireDate.add(Calendar.DATE, 90)
		def maxYear = maxExpireDate.get(Calendar.YEAR)
		
		def yearRange = (currentYear..maxYear)
		
		def userInstance = springSecurityService.currentUser
		session.username = userInstance.username

		def messageInstance = Message.read(params.id)
        if (!messageInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'message.label', default: 'Message'), params.id])}"
            redirect(action: "list")
        } else if ( ! messageInstance.allowedToEdit(userInstance.username) ) {
			flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'message.label', default: 'Message'), params.id])}"
			redirect(action: "list")
        } else {
            return [messageInstance: messageInstance, yearRange: yearRange ]
        }
    }

	// this is really only used when JavaScript isn't running.
	private def processRecipients = { params ->
		
		def userInstance = springSecurityService.currentUser
		def recipientAddUsers = []
		def recipientFailedEmails = []
		
		params?.recipient?.new?.each{ key, recipient ->
			if (recipient) {
				def recipientAddress = recipient.toLowerCase().trim()
				
				if (debug) { println "Looking for recipient: '${recipient}'" }
				def toUser = User.findByUsername(recipientAddress)
				if ( ! toUser ) {
					if (debug) { println "Trying to create new recipient: '${recipient}'" }
					toUser = User.createFromEmail(recipientAddress, true)
				}
				
				if (! toUser.hasErrors() ) {
					if (debug) { println "Found recipient: '${toUser}'" }
					recipientAddUsers.add(toUser)
				} else {
					if (debug) { println "Failed to add recipient: '${recipient}'" }
					flash.message = "Invalid email address: '${recipient}'"
				}
			}
		}
		
		return recipientAddUsers
	}
	
	// send the message
	def send = {
		
		// get logged in user
		def userInstance = springSecurityService.currentUser
		def messageInstance = Message.get(params.id)

		// only allow the person who it's from to send it.
        if (messageInstance && messageInstance.allowedToEdit(userInstance.username) ) {
			
            messageInstance.properties = params
			messageInstance.draft = false
			
			def recipientAddUsers = processRecipients(params)
			recipientAddUsers.each { r ->
				messageInstance.addToRecipients(user: r, userCreated: userInstance)
			}

            if (!messageInstance.hasErrors() && messageInstance.save(flush: true)) {

				// call the Quartz job that processes send mail notifications
				// ... it's a quartz job wrapped around the service call.
				// ... this way it runs in the background and returns to the
				// controller instantly.  This makes for a better user experience
				ProcessNotificationsJob.triggerNow([:])
				
				if (debug) { println "Triggering notifications to be mailed in the background." }

				redirect(action: "list")
            }
            else {
                render(view: "edit", model: [messageInstance: messageInstance])
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'message.label', default: 'Message'), params.id])}"
            redirect(action: "list")
        }
	}
	
	// save a message as a draft
    def saveDraft = {
		
		if (debug) {
			println "\nMessageController:update:params:: ${params} \n"
		}
		
		// get logged in user
		def userInstance = springSecurityService.currentUser
		def messageInstance = Message.get(params.id)
		
		// only allow the person who it's from to update it.
        if (messageInstance && messageInstance.allowedToEdit(userInstance.username) ) {
			
			// we don't care about record locking here...
			def previousVersion = messageInstance.version
			
            messageInstance.properties = params
			
			def recipientAddUsers = []
			if (params?.recipient) {
				recipientAddUsers = processRecipients(params)
				recipientAddUsers.each { r ->
					messageInstance.addToRecipients(user: r, userCreated: userInstance)
				}
			}
			messageInstance.version = previousVersion
			try {
	            if (!messageInstance.hasErrors() && messageInstance.save(flush: true)) {
	                redirect(action: "edit", id: messageInstance.id)
	            } else {
	                render(view: "edit", model: [messageInstance: messageInstance])
	            }
			} catch (Exception ex ){
				if (debug) {
					println "Error Saving Draft, Exception: ${ex}"
				}
				messageInstance.version = previousVersion
				render(view: "edit", model: [messageInstance: messageInstance])
			}
        } else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'message.label', default: 'Message'), params.id])}"
            redirect(action: "list")
        }
    }

	// delete a message or a draft of a messages
	// if the user is not the author, then it just marks the message as "deleted"
    def delete = {
		def userInstance = springSecurityService.currentUser
        def messageInstance = Message.get(params.id)
		def recipientInstance = null
		
		if (messageInstance) {
			recipientInstance = Recipient.createCriteria().get{
				and {
					user {
						eq("username", userInstance.username)
					}
					message {
						idEq(messageInstance?.id)
					}
				}
			}
		}
		
		def draft = messageInstance.draft
		
        if ( messageInstance.allowedToEdit(userInstance.username) && draft ) {
            try {
				messageInstance.attachments.each{ attachmentInstance ->
					def uFileId = attachmentInstance.uploadedFile.id
					// delete the file
					fileUploaderService.deleteFile(uFileId)
				}
                messageInstance.delete(flush: true)
				flash.message = "${message(code: 'message.discarded.label')}"
                redirect(action: "list")
            }
            catch (org.springframework.dao.DataIntegrityViolationException e) {
                flash.message = "${message(code: 'default.not.deleted.message', args: [message(code: 'message.label', default: 'Message'), params.id])}"
                redirect(action: "show", id: params.id)
            }
        } else if (recipientInstance) {
			if (debug) { println "Marking Recipient as 'deleted'." }
			// they are the recipient
			recipientInstance.deleted = true
			recipientInstance.save(flush:true)
			redirect(action: "list")
        } else if (messageInstance.allowedToEdit(userInstance.username) ) {
			if (debug) { println "Marking Message as 'deleted'." }
			messageInstance.deleted = true
			messageInstance.save(flush:true)
			redirect(action: "list")
		} else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'message.label', default: 'Message'), params.id])}"
            redirect(action: "list")
        }
    }
}
