package edu.umn.ncs.mail

import com.lucastex.grails.fileuploader.UFile
import grails.plugins.springsecurity.Secured

@Secured(['ROLE_USER'])
class AttachmentController {

	def static debug = false
	
	def messageSource
	def springSecurityService
	def fileUploaderService
	def contentService
	
	// we have to implement our own download action because the 
	// download controller security is insufficient for our needs.
	// as it lets anyone arbitrarily download any file by guessing
	// the id number.
	def index = {
		def userInstance = springSecurityService.currentUser
		session.username = userInstance.username
		def attachmentInstance = Attachment.read(params?.id)
		
		// let's see if the person is allowed to view it
		if (! attachmentInstance) {
			// database record not found
			response.sendError(404)
			return
		} else if (! attachmentInstance?.message?.allowedToView(userInstance.username)) {
			// access denied
			response.sendError(403)
			return
			// found attachment, let's check read permissions on it
		} else {
			// render it
			def file = new File(attachmentInstance?.uploadedFile?.path)
			if (file.exists()) {
				// mark the file as downloaded
				def ufile = UFile.get(attachmentInstance.uploadedFile.id)
				log.debug "Serving file id=[${ufile.id}] for the ${ufile.downloads} to ${request.remoteAddr}"
				ufile.downloads++
				ufile.save()
				
				def contentType = attachmentInstance.contentType
				if (params?.download) {
					contentType = "application/octet-stream"
				}
				response.setContentType(contentType)
				response.setHeader("Content-disposition", "filename=${attachmentInstance.fileName}")
				response.outputStream << file.readBytes()
				return
			} else {
				def msg = messageSource.getMessage("fileupload.download.filenotfound", [attachmentInstance?.uploadedFile?.name] as Object[], request.locale)
				log.error msg
				// actual file not found
				response.sendError(404)
				return
			}
		}
	}
	
	// lists attachments for a specific message
	// only allow the author of the message to do this
	def list = {
		def userInstance = springSecurityService.currentUser
		session.username = userInstance.username
		def messageInstance = Message.get(params.id)
		
		if (!messageInstance) {
			render ""
		} else if ( ! messageInstance.allowedToEdit(userInstance.username) ) {
			render ""
		} else {
			return [attachmentInstanceList: messageInstance.attachments.sort{ it.fileName } ]
		}
	}
	
	def uploadSuccess = {
		if (debug) {
			println "MessageController:uploadSuccess():flash.message::${flash?.message}"
			println "MessageController:uploadSuccess():params::${params}"
		}
		def ufileId = params?.ufileId
		def messageId = params?.id
		
		def ufileInstance = UFile.read(ufileId)
		def contentType = contentService.detectMimeType(ufileInstance.extension)
		
		def messageInstance = Message.get(messageId)
		if (ufileInstance && messageInstance) {
			
			// attaching file to message
			if ( messageInstance.addToAttachments(fileName: ufileInstance.name,
				contentType: contentType,
				uploadedFile: ufileInstance).save(flush:true) ) {
					flash.message = message(code:'message.attach.file.success')
				} else {
					flash.message = message(code:'message.attach.file.failed')
					if (debug) {
						messageInstance.errors.each {
							println "\t${it}"
						}
					}
				}
		} else {
			flash.message = message(code:'message.attach.file.failed')
		}
		redirect(controller:'message', action:'edit', id:messageId)
	}
	
	def uploadError = {
		if (debug) {
			println "MessageController:uploadError():flash.message::${flash?.message}"
			println "MessageController:uploadError():params::${params}"
		}
		def messageId = params?.id
		flash.message = message(code:'message.attach.file.failed')
		redirect(controller:'message', action:'edit', id:messageId)
	}
	
	def show = {
		
		def userInstance = springSecurityService.currentUser
		
		def attachmentInstance = Attachment.read(params?.id)
		if ( ! attachmentInstance ) {
			render ""
		} else if (! attachmentInstance.message.allowedToEdit(userInstance.username)) {
			render ""
		} else {
			[ attachmentInstance: attachmentInstance ]
		}
	}
	
	def delete = {
		
		def userInstance = springSecurityService.currentUser
		def attachmentInstance = Attachment.get(params?.id)
		// you can only remove a recipient if you are the author of the message
		if (attachmentInstance) {
			if (attachmentInstance.message.allowedToEdit(userInstance.username)) {
				try {
					// get the file ID
					def uFileId = attachmentInstance.uploadedFile.id
					// delete the record
					attachmentInstance.delete(flush: true)
					// delete the file
					fileUploaderService.deleteFile(uFileId)
					// flash.message = "${message(code: 'recipient.removed.label')}"
					render ""
				}
				catch (org.springframework.dao.DataIntegrityViolationException e) {
					def errorMessage = message(code:'message.remove.file.failed')
					render(view:'show', model:[attachmentInstance: attachmentInstance, errorMessage: errorMessage])
				}
			} else {
				if (debug) { println "user attempted to delete a file that doesn't belong to them!" }
				render ""
				return
			}
		} else {
			if (debug) { println "user attempted to delete a file that doesn't exist!" }
			render ""
			return
		}
	}	
}
