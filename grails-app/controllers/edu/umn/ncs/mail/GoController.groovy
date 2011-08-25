package edu.umn.ncs.mail

// used for sending email links
class GoController {

	def static debug = false

	def message = {
		def messageId = params?.id
		session.username = params?.user
		redirect(controller:'message', action:'show', id:messageId)
	}

	def inbox = {
		session.username = params?.user
		redirect(controller:'message', action:'list')
	}
}
