package edu.umn.ncs.mail

import com.lucastex.grails.fileuploader.UFile

class CleanupService {
	
	static def debug = false
    static transactional = true
	
	def fileUploaderService
	
	// Removes messages and attachments that have expires more than 7 days ago
    def removeExpiredMessages() {
		
		def now = new Date()
		// give a 7 day buffer...
		def sevenDaysAgo = now - 7
		// Query all Messages that the expired seven or more days ago 
		def expiredMessages = Message.createCriteria().list{
			lt('dateExpires', sevenDaysAgo)
		} 
		
		expiredMessages.each{ messageInstance ->

			try {
				def messageId = messageInstance.id 
				// we need to delete all the files from the attachments first.
				messageInstance.attachments.each{ attachmentInstance ->
					def uFileId = attachmentInstance.uploadedFile.id
					// delete the file
					fileUploaderService.deleteFile(uFileId)
				}
				// then we can delete the message
				messageInstance.delete(flush:true)
				
				println "${now}\tncs-secure-mail\tRemoved Message ID: ${messageId}."				
			}
			catch (org.springframework.dao.DataIntegrityViolationException e) {
				println "${now}\tncs-secure-mail\tUnable to delete expired message: ${messageInstance.id}."				
			}
		}
		// done deleting expired messages
    }

	// TODO:
	// Removes User accounts that are not a recipient of an active message, and have not logged in to the system
    def removeUnusedUsers() {
		if (debug) {
			println "Removing unused accounts."
		}
		
		def recipientUserList = [] as Set
		recipientUserList.addAll( Recipient.list().collect{it.user} )
		
		def freshUserList = User.findAllByNewAccount(true)
		recipientUserList.each{
			if (debug) {
				println "found recipient: ${it.user}"
			}
			freshUserList.remove(it)
		}
		
		freshUserList.each{
			if (debug) {
				println "User: ${it.user}, will be removed."
			}
		}
		
		return false
    }
	
	// TODO:
	// removes records and files from a UFileID that do not belong to an attachment
	// removes files in the file system that do not belong to a UFileID Record
	def removeOrphanedFiles() {
		return false
	}
}
