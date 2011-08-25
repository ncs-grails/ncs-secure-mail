package edu.umn.ncs.mail

class CleanupStuffJob {
	static triggers = {
		// run twice daily
		cron name: 'biDaily', cronExpression: "0 0 */12 * * ?"
		// start after 5 seconds, repeat every 5 minutes
		//cron name: 'everyThreeMinutes', cronExpression: "0 */3 * * * ?"
	}
	
	def cleanupService

    def execute(context) {
        // execute task
		cleanupService.removeExpiredMessages()
		cleanupService.removeUnusedUsers()
		cleanupService.removeOrphanedFiles()
    }
}
