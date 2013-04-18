package edu.umn.ncs.mail

import com.lucastex.grails.fileuploader.UFile
import grails.plugins.springsecurity.Secured

@Secured(['ROLE_USER'])
class MessageController {

	static def debug = false
	def springSecurityService
	def fileUploaderService

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

	/**
	The default action, index, redirects to the 'list' action.
	*/
    def index = {
        redirect(action: "list", params: params)
    }

	/**
	This is the "mailbox" view that gets a colleciton of incoming messages, outgoing, drafts and deleted messages.
	*/
    def list = {
		def now = new Date()
		def thisMorning = Calendar.instance
		thisMorning.set(Calendar.HOUR, 0)
		thisMorning.set(Calendar.MINUTE, 0)
		thisMorning.set(Calendar.SECOND, 0)

		def userInstance = springSecurityService.currentUser
		session.username = userInstance.username
		
		// find all messages that are not deleted, but addressed to this user
		def incomingMessageInstanceList = Message.incomingMessages(userInstance.username).list()
		
		// find all messages sent by the user
		def draftMessageInstanceList = Message.drafts(userInstance.username).list()

		// find all messages sent by the user
		def outgoingMessageInstanceList = Message.sentMessages(userInstance.username).list()
		
		// Add Trash Folder
		def deletedMessageInstanceList = Message.deletedMessages(userInstance.username).list()
		def deletedSentMessageInstanceList = Message.deletedSentMessages(userInstance.username).list()
		
		// Re-order the deleted messages by date created
		deletedMessageInstanceList.addAll(deletedSentMessageInstanceList)

        [incomingMessageInstanceList: incomingMessageInstanceList, 
			draftMessageInstanceList: draftMessageInstanceList, 
			outgoingMessageInstanceList: outgoingMessageInstanceList,
			deletedMessageInstanceList: deletedMessageInstanceList,
			thisMorning: thisMorning.time,
		    userInstance: userInstance ]
    }

	/**
	The reply to message action. This takes a message, sets the composer to the person
	who is viewing the message, sets the original composer as a recipient,
	modify's the subject to "RE: ${subject}", marks the message as a draft,
	adds "&gt; " to each line in the body, and opens the message composer view.
	*/
	def replyAll = {
		def userInstance = springSecurityService.currentUser
		session.username = userInstance.username
		def origMessageInstance = Message.read(params?.id)

		return this.replyToMessage(userInstance, origMessageInstance, true)
	}

	/**
	The reply to message action. This takes a message, sets the composer to the person
	who is viewing the message, sets the original composer as a recipient,
	modify's the subject to "RE: ${subject}", marks the message as a draft,
	adds "&gt; " to each line in the body, and opens the message composer view.
	*/
	def reply = {
		def userInstance = springSecurityService.currentUser
		session.username = userInstance.username
		def origMessageInstance = Message.read(params?.id)

		return this.replyToMessage(userInstance, origMessageInstance, false)
	}

	/**
	This is the actual method that sets up the message reply.
	It determines if all the recipients are added in from the 
	original message or not.
	*/
	private def replyToMessage(User userInstance, Message origMessageInstance, Boolean toAll) {

		if (userInstance && origMessageInstance ) {
			session.username = userInstance.username
					
			def messageInstance = new Message()
			messageInstance.from = userInstance
					
			if (origMessageInstance?.allowedToView(userInstance.username)) {
				messageInstance.addToRecipients(user: origMessageInstance.from, userCreated: userInstance )

				// If it's reply to all...
				if (toAll) {
					origMessageInstance.recipients.each{ recip ->
						if (recip.user.id != userInstance.id) {
							if (debug) {
								println "adding recipient ${recip.user}"
							}
							messageInstance.addToRecipients(user: recip.user, userCreated: userInstance)
						}
					}
				}
				
				def newBody = origMessageInstance.body?.replace("\n", "\n > ")
				if (newBody) {
					newBody = "\n > " + newBody
				}
				if (debug) { println "${newBody}" }
				messageInstance.body = newBody
				messageInstance.subject = "Re: ${origMessageInstance.subject}"
			}

			if (! messageInstance.hasErrors() && messageInstance.save(flush: true)) {
				redirect(action: "edit", id: messageInstance.id)
			} else {
				flash.message = "${message(code: 'default.create.error' )}"
				redirect(action: "edit", id: origMessageInstance.id)
			}
		} else {
			flash.message = "${message(code: 'default.create.error' )}"
			redirect(action: "edit", id: origMessageInstance.id)
		}
	}
	
	/**
	The forward message action. This takes a message, sets the composer to the person
	who is viewing the message, sets the original composer as a recipient,
	modify's the subject to "RE: ${subject}", marks the message as a draft,
	adds "&gt; " to each line in the body, and opens the message composer view.
	*/
	def forward = {
		def userInstance = springSecurityService.currentUser
		session.username = userInstance.username
		def origMessageInstance = Message.read(params?.id)
		def uploadFolder = grailsApplication.config.fileuploader.files.path
		def now = new Date()
				
		def messageInstance = new Message()
		messageInstance.from = userInstance
				
		if (origMessageInstance?.allowedToView(userInstance.username)) {
			// Set the expire date to the same as the original
			messageInstance.dateExpires = origMessageInstance.dateExpires
			// add the attachments from the original
			origMessageInstance.attachments.each{ attach ->

				// Make a copy of the file
				def origFile = new File(attach.uploadedFile.path)
				// get the new folder to hold the file copy
				def newUploadFolder = new File(uploadFolder + now.time)
				if ( ! newUploadFolder.exists() ) { newUploadFolder.mkdir() }
				def newFile = newUploadFolder.absolutePath + '/' + origFile.name
				// copy the file
				def ant = new AntBuilder()
				ant.copy(file: origFile, toFile: newFile)

				if (debug) {
					println "from file:"
					println "\t${origFile.name}"
					println "\t${origFile.absolutePath}"
					println "\t${origFile.parent}"
					println "\t${newUploadFolder.absolutePath}"
					println "\t${newFile}"
				}

				// Make a copy of the UFile from the original attachment.
				def ufileInstance = new UFile(size: attach.uploadedFile.size,
					path: newFile,
					name: attach.uploadedFile.name,
					extension: attach.uploadedFile.extension,
					dateUploaded: attach.uploadedFile.dateUploaded,
					downloads: 0)
					
				if ( ufileInstance.save(flush:true) ) {
					// add the new attachment
					messageInstance.addToAttachments(fileName: attach.fileName,
						contentType: attach.contentType,
						uploadedFile: ufileInstance).save(flush:true)
				} else if (debug) {
					println "Couldn't save UFile!"
					ufileInstance.errors.each {
						println "	E> ${it}"
					}
				}
			}
			
			def newBody = origMessageInstance.body?.replace("\n", "\n > ")
			if (newBody) {
				newBody = "\n > " + newBody
			}
			messageInstance.body = newBody
			messageInstance.subject = "Fwd: ${origMessageInstance.subject}"
		}

		if (messageInstance.save(flush: true)) {
			redirect(action: "edit", id: messageInstance.id)
		} else {
			if (debug) {
				println "messageInstance Errors:"
				messageInstance.errors.each{
					println "	E> ${it}"
				}
			}
			flash.message = "${message(code: 'default.forward.error', default: 'Error forwarding message!')}"
			redirect(action: "show", id: origMessageInstance.id)
		}
	}
	
	/** this is similar to 'compose', 
	we automatically save the message to the database
	as a draft on create. The message is automatically
	saved on create to allow for the support of multiple
	file attachment uploads.  Without doing this, it gets
	rather tricky.
	*/
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

	/** This is the action to read a message. */
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
	
	/** This is the action for composing a message. */
    def edit = {
		//year in 30 days
		def maxExpireDate = new GregorianCalendar()
		def currentYear = maxExpireDate.get(Calendar.YEAR)
		maxExpireDate.add(Calendar.DATE, 30)
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

	/** this is really only used when JavaScript isn't running.
	It is called by the saveDraft() and send() actions.
	This is usually taken care of by the jQuery AJAX call to
	add a single recipient at a time.
	*/
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
	
	/** This sends the message message out.
	It sets the draft attribute of the message to false, 
	and calls the ProcessNotificationsJob to sent out
	notification emails to all the recipients.
	*/
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
	
	/** This saves the message as a draft.
	It is called rather frequently by the jQuery AJAX triggers.
	*/
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

	/** This deletes a message or a draft of a messages.
	Thie behavior of this changes if the user is not the 
	author, then it just marks the message as "deleted".
	*/
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
