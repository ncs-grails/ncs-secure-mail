import edu.umn.ncs.mail.User
import edu.umn.ncs.mail.Role
import edu.umn.ncs.mail.UserRole

class BootStrap {
	def springSecurityService
	
    def init = { servletContext ->
		
		def roles = ['ROLE_ADMIN', 'ROLE_USER', 'ROLE_SWITCH_USER' ]
		roles.each{
			def role = Role.findByAuthority(it)
			if ( ! role ) {
				role = new Role(authority: it)
				if ( ! role.save(flush: true) ) {
					println "Failed to save role: ${it}"
				}
			}
		}

		environments {
			development {
								
				def resetPassword = false
				
				String password = 'change?This'
				
				// create basic user
				def ajz = User.findByUsername('ajz@umn.edu')
				if ( ! ajz ) {
					ajz = new User(username:'ajz@umn.edu'
						, fullName: 'Aaron J. Zirbes'
						, password: password
						, passwordExpired: false
						, newAccount: false)
					if ( ! ajz.save(flush: true) ) {
						println "Failed to save ajz"
						ajz.errors.each{
							println "\t${it}"
						}
					}
				} else if (resetPassword) {
					ajz.password = password
					ajz.passwordExpired = false
					if ( ajz.save(flush:true) ) {
						println "reset password successful."
					} else { 
						println "failed to reset password"
						ajz.errors.each{
							println "\t${it}"
						}
					}
				}
				
				
				def admin = Role.findByAuthority('ROLE_ADMIN')
				def user = Role.findByAuthority('ROLE_USER')
				def switchUser = Role.findByAuthority('ROLE_SWITCH_USER')
				UserRole.create(ajz, admin)
				UserRole.create(ajz, user)
				UserRole.create(ajz, switchUser)
			}
		}
		
    }
    def destroy = {
    }
}
