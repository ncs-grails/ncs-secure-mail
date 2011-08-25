package edu.umn.ncs.mail

import org.apache.commons.lang.RandomStringUtils
import grails.plugins.springsecurity.Secured

class AccountController {

	static def debug = false
	def springSecurityService
	def notifyService
	def userAccountService

	// change password form
	@Secured(['ROLE_USER'])
	def changePassword = {
		def userInstance = springSecurityService.currentUser
		[userInstance: userInstance]
	}
	
	// change password action
	@Secured(['ROLE_USER'])
	def updatePassword = {
		def userInstance = springSecurityService.currentUser
		def passwordOld = params?.passwordOld
		def password = params?.password
		def passwordConfirm = params?.passwordConfirm
		def authError = 0
		
		if (userInstance) {
			def salt = userInstance.username
			def oldSaltedPasswordHash = springSecurityService.encodePassword(password, salt)
			def oldPasswordHash = springSecurityService.encodePassword(password)
			if (debug) {
				println "Password hash: ${userInstance.password}"
				println "Old hash: ${oldPasswordHash}"
				println "Old Salted hash: ${oldSaltedPasswordHash}"
			}
			
			if ( ! userAccountService.confirmPassword(userInstance, passwordOld) ) {
				flash.message = message(code:'security.change.password.old.invalid')
				redirect(action:'changePassword')
			} else if (! password) {
				flash.message = message(code:'security.change.password.nopass')
				authError = 1
			} else if (password != passwordConfirm) {
				flash.message = message(code:'security.change.password.doesnt.match')
				authError = 2
			} else {
				def verifyResult = userAccountService.passwordVerifier(password)
				
				if (verifyResult > 0) {
					if (verifyResult == 1) { flash.message = message(code:'security.change.password.tooshort') }
					else if (verifyResult == 2) { flash.message = message(code:'security.change.password.toolong') }
					else if (verifyResult == 3) { flash.message = message(code:'security.change.password.tooboring') }
					else if (verifyResult == 4) { flash.message = message(code:'security.change.password.nospaces') }
					
					authError = verifyResult + 2
					
				} else {
					// password verification passed.  Reset password.
					if (debug) { println "Password verification passed.  Reseting password to : ${password}" }
				
					if ( userAccountService.setNewPassword(userInstance, password) ) {
						session.username = userInstance.username
						flash.message = message(code:'security.change.password.updated')
						springSecurityService.reauthenticate(userInstance.username, password)
						redirect(controller:'message', action:'list')
						return
					} else {
						flash.message = message(code:'Unable to update password.')
					}
				}
			}
		}
		redirect(action:'changePassword')
	}

	private def resetRandomMathProblem = {
		def random = new Random()
		
		def x = random.nextInt(12)
		def y = random.nextInt(12)
		
		def mathEquation = message(code:'security.register.account.equation', args:[x, y])
		session.mathEquation = mathEquation
		session.mathSolution = x + y
		
		if (debug) {
			println "Math: ${mathEquation}${session.mathSolution}"
		}
	}
		
	def newUser = {
		resetRandomMathProblem()
		[mathEquation: session.mathEquation]
	}
	
	def register = {

		def email = params.email?.trim()?.toLowerCase()
		def fullName = params.fullName
		def mathAnswer = params.mathAnswer
		
		def model = [email: email, fullName:fullName]
		
		if ( ! session.mathTries ) {
			session.mathTries = 0
		}

		if (session.mathTries > 5) {
			flash.message = message(code:'security.register.account.badatmath')
			redirect(controller:'login', action:'denied')
			return
		} else if ( mathAnswer != session.mathSolution.toString() ) {
			flash.message = message(code:"security.register.account.solution", args:[session?.mathEquation, session?.mathSolution])
			session.mathTries++
			resetRandomMathProblem()
			model.mathEquation = session.mathEquation
			render(view:'newUser', model: model)
			return
		} else if ( ! email ) {
			flash.message = message(code:'security.register.account.email.required')
			resetRandomMathProblem()
			model.mathEquation = session.mathEquation
			render(view:'newUser', model: model)
			return
		} else {
			def result = userAccountService.resetPassword(email, fullName, true)
			if (result.success ) {
				// send notification email
				notifyService.sendPasswordResetNotification(result.userInstance, result.resetKey)
				if (debug) {
					println "Validation Link for ${email} :"
					println g.createLink(absolute: true, controller:'account', action:'newPassword', id:result.userInstance.id ,params:[key: result.resetKey] )
				}
				render(view: 'reset', model:[email: email])
				return
			} else {
				flash.message = result.errorMessage
				if (result.userInstance) {
					redirect(action:'forgot')
					return
				} else {
					redirect(action:'newUser')
					return
				}
			}
		}
		render "How did you get here?"
	}
	
	// reset new password form
	def newPassword = {
		
		def userInstance = User.read(params?.id)
		def resetKey = params?.key
		
		if ( ! userInstance || ! resetKey || userInstance.resetKey != resetKey) {
			flash.message = message(code:'security.change.password.invalid.key')
			redirect(action:'forgot')
		} else {
			// we have a user and a valid reset key
			[ userInstance: userInstance , resetKey: resetKey ]
		}
	}
	
	// reset new password action
	def setPassword = {
		def userInstance = User.read(params?.id)
		def resetKey = params?.key
		def password = params?.password
		def passwordConfirm = params?.passwordConfirm
		def authError = 0
		
		if ( ! userInstance || ! resetKey || userInstance.resetKey != resetKey) {
			flash.message = message(code:'security.change.password.invalid.key')
			redirect(action:'forgot')
		} else if (! password) {
			flash.message = message(code:'security.change.password.nopass')
			authError = 1
		} else if (password != passwordConfirm) {
			flash.message = message(code:'security.change.password.doesnt.match')
			authError = 2
		} else {
			def verifyResult = userAccountService.passwordVerifier(password)
			
			if (verifyResult > 0) {
				if (verifyResult == 1) { flash.message = message(code:'security.change.password.tooshort') }
				else if (verifyResult == 2) { flash.message = message(code:'security.change.password.toolong') }
				else if (verifyResult == 3) { flash.message = message(code:'security.change.password.tooboring') }
				else if (verifyResult == 4) { flash.message = message(code:'security.change.password.nospaces') }
				
				authError = verifyResult + 2
				
			} else {
				// password verification passed.  Reset password.
				if (debug) { println "Password verification passed.  Reseting password to : ${password}" }
			
				if ( userAccountService.setNewPassword(userInstance, password) ) {
					session.username = userInstance.username
					springSecurityService.reauthenticate(userInstance.username, password)
					flash.message = message(code:'security.change.password.updated')
					redirect(controller:'message', action:'list')
				} else {
					flash.message = message(code:'security.change.password.unable.to.update')
				}
			}
		}

		render(view:'newPassword', model:[ userInstance: userInstance , resetKey: resetKey ] )		
	}
	
	// forgot password form
	def forgot = { }
	
	def reset = {
		def email = params.email
		def emailConfirm = params.emailConfirm
		
		if (! email) {
			flash.message = message(code:'security.reset.password.email.required')
			redirect(action:'forgot')
		} else if (email != emailConfirm) {
			flash.message = message(code:'security.reset.password.email.doesnt.match')
			redirect(action:'forgot')
		} else {

			def result = userAccountService.resetPassword(email, null, false)
			if (result.success ) {
				// send notification email
				notifyService.sendPasswordResetNotification(result.userInstance, result.resetKey)
				if (debug) {
					println "Reset Link for ${email} :"
					println g.createLink(absolute: true, controller:'account', action:'newPassword', id:result.userInstance.id ,params:[key: result.resetKey] )
				}

			} else {
				flash.message = result.errorMessage
				redirect(action:'forgot')
			}
		}
		[email: email]
	}
}
