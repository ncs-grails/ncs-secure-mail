package edu.umn.ncs.mail

class ProcessNotificationsJob {
	def timeout = 5l * 60l * 1000l // execute job once in 5 minutes
	
	def notifyService

    def execute(context) {
        // execute task
		notifyService.checkForNotifications()
    }
}
